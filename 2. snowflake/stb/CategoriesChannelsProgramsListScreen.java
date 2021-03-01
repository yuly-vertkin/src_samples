package tv.mediastage.frontstagesdk.channel.management;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.backends.android.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;

import tv.mediastage.frontstagesdk.AbstractScreen;
import tv.mediastage.frontstagesdk.BuildConfig;
import tv.mediastage.frontstagesdk.GLIntent;
import tv.mediastage.frontstagesdk.GLListener;
import tv.mediastage.frontstagesdk.cache.ChannelCategoriesCache;
import tv.mediastage.frontstagesdk.cache.ChannelsCache;
import tv.mediastage.frontstagesdk.cache.ResponseCache;
import tv.mediastage.frontstagesdk.cache.epg.Epg;
import tv.mediastage.frontstagesdk.cache.epg.EpgCache;
import tv.mediastage.frontstagesdk.model.CategoryModel;
import tv.mediastage.frontstagesdk.model.ChannelModel;
import tv.mediastage.frontstagesdk.model.ProgramModel;
import tv.mediastage.frontstagesdk.requests.RequestManager;
import tv.mediastage.frontstagesdk.util.ExceptionWithErrorCode;
import tv.mediastage.frontstagesdk.util.GdxHelper;
import tv.mediastage.frontstagesdk.util.Log;
import tv.mediastage.frontstagesdk.util.MiscHelper;
import tv.mediastage.frontstagesdk.watching.WatchingController;
import tv.mediastage.frontstagesdk.widget.LinearGroup;
import tv.mediastage.frontstagesdk.widget.RectangleShape;
import tv.mediastage.frontstagesdk.widget.ScrollGroup;
import tv.mediastage.frontstagesdk.widget.Spinner;
import tv.mediastage.frontstagesdk.widget.list.AbsList;
import tv.mediastage.frontstagesdk.widget.list.adapter.ExpandableLoupeListAdapter;
import tv.mediastage.frontstagesdk.widget.list.adapter.ListAdapter;
import tv.mediastage.frontstagesdk.controller.AnalyticsManager;

import static tv.mediastage.frontstagesdk.cache.ChannelCategoriesCache.ALL_CHANNELS_ID;
import static tv.mediastage.frontstagesdk.cache.ChannelCategoriesCache.FAVORITES_CHANNELS_ID;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.ACTIVE_COLOR;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.CategoriesAdapter;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.ChannelsAdapter;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.DescriptionAdapter;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.EmptyListAdapter;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.INACTIVE_COLOR;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.INACTIVE_LINE_HEIGHT;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.LINE_HEIGHT;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.LINE_MARGIN;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.LOUPE_ITEM_HEIGHT;
import static tv.mediastage.frontstagesdk.channel.management.CategoriesChannelsProgramsList.ProgramsAdapter;

/**
 * Created by yuly on 04.08.17.
 */
public class CategoriesChannelsProgramsListScreen extends AbstractScreen
  implements CategoriesChannelsProgramsList.DescriptionAdapter.Listener,
             ResponseCache.Observer<List<CategoryModel>>,
             ScrollGroup.ScrollStateChangeListener,
             AbsList.ActiveItemChangeListener {
  @IntDef({ListIndex.FIRST_EMPTY, ListIndex.CATEGORIES, ListIndex.CHANNELS,
    ListIndex.PROGRAMS, ListIndex.DESCRIPTION, ListIndex.LAST_EMPTY})
  @Retention(RetentionPolicy.SOURCE)
  private @interface ListIndex {
    int FIRST_EMPTY = 0;
    int CATEGORIES  = 1;
    int CHANNELS    = 2;
    int PROGRAMS    = 3;
    int DESCRIPTION = 4;
    int LAST_EMPTY  = 5;
  }

  private final static int LIST_WIDTH = MiscHelper.getScreenWidth() / 3;
  private final static int UPDATE_PROGRAMS_DISTANCE = 3;

  private static final long SMALL_HANDLE_ENTITIES_CHANGED_TASK_DELAY = 50; //ms
  private static final long BIG_HANDLE_ENTITIES_CHANGED_TASK_DELAY = 500; //ms

  private static final float DISABLED_LIST_FADE_DURATION = 0.3f; //sec
  private static final float ENABLED_LIST_FADE_DURATION = 0.2f; //sec
  private static final float DISABLED_LIST_ALPHA = 0.2f;

  private final DelayedTask handleCategoriesChangedTask = new DelayedTask() {
    @Override
    public void start() {
      super.start();
      getList(ListIndex.CHANNELS).enable(false);
      getList(ListIndex.PROGRAMS).enable(false);
      getList(ListIndex.DESCRIPTION).enable(false);
    }

    @Override
    public void run() {
      getList(ListIndex.CATEGORIES).enable(true);
      int activeIndex = getList(ListIndex.CATEGORIES).getActiveIndex();
      CurrentCategoryHelper.getInstance().setTemp(categories.get(activeIndex));
      if (setChannels()) {
        getList(ListIndex.CHANNELS).enable(true);
      }
    }
  };
  private final DelayedTask handleChannelsChangedTask = new DelayedTask() {
    @Override
    public void start() {
      super.start();
      getList(ListIndex.PROGRAMS).enable(false);
      getList(ListIndex.DESCRIPTION).enable(false);
    }

    @Override
    public void run() {
      getList(ListIndex.CHANNELS).enable(true);
      if (setPrograms()) {
        getList(ListIndex.PROGRAMS).enable(true);
      }
    }
  };
  private final DelayedTask handleProgramsChangedTask = new DelayedTask() {
    @Override
    public void start() {
      super.start();
      getList(ListIndex.DESCRIPTION).enable(false);
      getList(ListIndex.PROGRAMS).setBlocked(isProgramsLoading = true);
    }

    @Override
    public void stop() {
      super.stop();
      getList(ListIndex.PROGRAMS).setBlocked(isProgramsLoading = false);
    }

    @Override
    public void run() {
      boolean isLoad = loadProgramsIfNeeded();
      if (!isLoad) {
        getList(ListIndex.PROGRAMS).setBlocked(isProgramsLoading = false);
        getList(ListIndex.PROGRAMS).enable(true);
        if (setProgramDescription()) {
          getList(ListIndex.DESCRIPTION).enable(true);
        }
      }
    }
  };

  private final CategoriesChannelsProgramsList[] lists;
  private final RectangleShape inactiveListItemLine;
  private final RectangleShape activeListItemLine;
  private final ChannelCategoriesCache mCache =  ChannelCategoriesCache.getInstance();

  private ScrollGroup scrollGroup;
  private Spinner spinner;

  private @NonNull List<CategoryModel> categories = Collections.emptyList();
  private @NonNull List<ChannelModel> channels = Collections.emptyList();
  private @NonNull List<ProgramModel> programs = Collections.emptyList();

  private @Nullable Epg currentEpg;
  private @Nullable String programsRequestId;
  private long programsChannelId = ChannelModel.INVALID_CHANNEL_ID;
  private @ListIndex int activeListIndex = ListIndex.CHANNELS;
  private boolean isDataLoaded;
  private boolean isProgramsLoading;
  private long handleEntitiesChangedTaskDelay = SMALL_HANDLE_ENTITIES_CHANGED_TASK_DELAY;

  //temporary var(s) to prevent boxing/unboxing during of logging
//  private final MutIntegral.Long t1 = new MutIntegral.Long();
//  private final MutIntegral.Long t2 = new MutIntegral.Long();
//  private final MutIntegral.Long t3 = new MutIntegral.Long();
//  private final MutIntegral.Long t4 = new MutIntegral.Long();

  private static int calculateScrollGroupContentX(@ListIndex int activeListIndex) {
    return -LIST_WIDTH * (activeListIndex - 1);
  }

  private static boolean isActiveListIndexValid(@ListIndex int value) {
    return ListIndex.CATEGORIES <= value && ListIndex.DESCRIPTION >= value;
  }

  private static @ListIndex int calculateActiveListIndex(int contentX) {
    int i = 0;
    for (int x = 0; x > contentX; x -= LIST_WIDTH) {
      ++i;
    }
    return i + 1;

    //if (BuildConfig.DEBUG) {
      //if ((contentX % LIST_WIDTH) != 0) {
      //  throw new AssertionError("Bad content position: " + Integer.toString(contentX));
      //}
    //}
    //return Math.round(Math.abs((float)contentX / LIST_WIDTH)) + 1;
  }

  public CategoriesChannelsProgramsListScreen(GLListener listener) {
    super(listener);
    lists = new CategoriesChannelsProgramsList[ListIndex.LAST_EMPTY + 1];
    activeListItemLine = new RectangleShape(null);
    activeListItemLine.setColor(ACTIVE_COLOR);
    activeListItemLine.setSize(LIST_WIDTH - 2 * LINE_MARGIN, LINE_HEIGHT);
    inactiveListItemLine = new RectangleShape(null);
    inactiveListItemLine.setColor(INACTIVE_COLOR);
    inactiveListItemLine.setSize(listener.getWidth(), INACTIVE_LINE_HEIGHT);
  }

  @Override
  public void onCreate(GLIntent intent) {
    super.onCreate(intent);
    setHubButton(true);
    setQuickChannelButton(true);
	AnalyticsManager.INSTANCE.screenEvent(AnalyticsManager.TV_PROGRAM_SCREEN);

    LinearGroup content = new LinearGroup(null);
    content.setGravity(Gravity.TOP);
    content.setOrientation(LinearGroup.HORIZONTAL);
    content.setDesiredSize(WRAP_CONTENT, MATCH_PARENT);

    for (int i = 0; i <= ListIndex.LAST_EMPTY; i++) {
      CategoriesChannelsProgramsList list = new CategoriesChannelsProgramsList(null,
        mListener.getKeyboardController());
      list.setDesiredSize(LIST_WIDTH, MATCH_PARENT);
      list.setGravity(Gravity.CENTER);
      list.setActiveItemTouchable(false);
      list.setActiveItemChangeListener(this);
      list.setShouldRecycle(true);
      list.setBigRangeScrollingEnabled(false);
      list.setDrawDefaultLoupeDecorator(false);
      list.fadeWhenEnablityChanging(true, DISABLED_LIST_ALPHA,
        DISABLED_LIST_FADE_DURATION, ENABLED_LIST_FADE_DURATION);
      content.addActor(list);
      lists[i] = list;
    }
    EmptyListAdapter emptyAdapter = new EmptyListAdapter();
    getList(ListIndex.LAST_EMPTY).setAdapter(emptyAdapter);
    getList(ListIndex.FIRST_EMPTY).setAdapter(emptyAdapter);

    scrollGroup = new ScrollGroup(null) {
      @Override
      public void onLayout(int measuredWidth, int measuredHeight) {
        //because super layout resets current content position
        //we should undertake some steps to guarantee that
        //content keeps previous position inside of scroll group
        pushContentPosition();
        super.onLayout(measuredWidth, measuredHeight);
        popContentPosition();
    }};
    scrollGroup.setScrollStateChangeListener(this);
    scrollGroup.setDesiredSize(MATCH_PARENT, MATCH_PARENT);
    scrollGroup.setAlignScrollByStep(true);
    scrollGroup.setScrollStep(LIST_WIDTH);
    scrollGroup.setVisibility(INVISIBLE);
    scrollGroup.setContent(content);
    addActor(scrollGroup);

    spinner = new Spinner(null);
    spinner.setDesiredSize(WRAP_CONTENT, WRAP_CONTENT);
    spinner.setGravity(Gravity.CENTER);
    spinner.setSpinnerStyle(Spinner.Style.LARGE);
    spinner.setVisibility(VISIBLE);
    addActor(spinner);

    mCache.observeOnGdxThread(this);
    mCache.update();
  }

  @Override
  public void onDestroy() {
    handleCategoriesChangedTask.stop();
    handleChannelsChangedTask.stop();
    handleProgramsChangedTask.stop();
    mCache.removeObserver(this);
    super.onDestroy();
  }

  private @Nullable ChannelModel getCurrentOrLastPlayedChannel() {
    WatchingController wc = mListener.getWatchingController();
    ChannelModel channel = wc.getCurrentContent().getChannel();
    if (channel != null) {
      return channel;
    }
    //get last successfully played channel
    return ChannelsCache.getInstance().getChannel(wc.getLastPlayedChannelId());
  }

  private @NonNull CategoriesChannelsProgramsList getList(@ListIndex int index) {
    return lists[index];
  }

  @Override
  public void onNext(@NonNull List<CategoryModel> categories) {
    setCategories(mCache.getNotEmptyCategories());
  }

  @Override
  public void onError(ExceptionWithErrorCode e) {
    Log.e(Log.GL, e);
    mCache.update();
  }

  @Override
  public void onShowView() {
    super.onShowView();
    CurrentCategoryHelper.getInstance().forgetIntent();
  }

  private void setCategories(@NonNull List<CategoryModel> categories) {
    CategoriesChannelsProgramsList list = getList(ListIndex.CATEGORIES);
    CategoriesAdapter adapter = new CategoriesAdapter(this.categories = categories, mListener);
    adapter.setActive(ListIndex.CATEGORIES == activeListIndex);
    list.setAdapter(adapter);
    list.enable(true);
    ChannelModel channel = getCurrentOrLastPlayedChannel();
    CategoryModel savedCategory = CurrentCategoryHelper.getInstance().getCurrent();
    if (channel != null) {
      int categoryId = savedCategory != null && (savedCategory.id == FAVORITES_CHANNELS_ID || channel.categoryIds != null && channel.categoryIds.contains(savedCategory.id))? savedCategory.id : ALL_CHANNELS_ID;
      for (int i = 0; i < categories.size(); ++i) {
        CategoryModel category = categories.get(i);
        if (category.id == categoryId) {
          list.setActiveIndex(i);
          break;
        }
      }
//      for (int i = 0; i < categories.size(); ++i) {
//        CategoryModel category = categories.get(i);
//        if (channel.categoryIds.contains(category.id)) {
//          list.setActiveIndex(i);
//          break;
//        }
//      }
    }
    handleActiveListChanged(list);
  }

  private boolean setChannels() {
    if (categories == null || categories.isEmpty()) {
      return false;
    }
    int activeIndex = getList(ListIndex.CATEGORIES).getActiveIndex();
    channels = MiscHelper.safeRead(mCache.getChannelsByCategory(categories.get(activeIndex).id));
    CategoriesChannelsProgramsList list = getList(ListIndex.CHANNELS);
    ChannelsAdapter adapter = new ChannelsAdapter(channels, mListener);
    adapter.setActive(ListIndex.CHANNELS == activeListIndex);
    list.setAdapter(adapter);
    ChannelModel channel = getCurrentOrLastPlayedChannel();
    if (channel != null) {
      for (int i = 0; i < channels.size(); ++i) {
        ChannelModel ch = channels.get(i);
        if (ch.id == channel.id) {
          list.setActiveIndex(i);
          break;
        }
      }
    }
    handleActiveListChanged(list);
    return true;
  }

  private boolean loadProgramsIfNeeded() {
    int activeIndex = getList(ListIndex.PROGRAMS).getActiveIndex();
    ExpandableLoupeListAdapter adapter = getList(ListIndex.PROGRAMS).getExpandableLoupeListAdapter();

    if (adapter != null && currentEpg != null) {
      final int left = adapter.getMinimumAdapterIndex() + UPDATE_PROGRAMS_DISTANCE;
      final int right = adapter.getMaximumAdapterIndex() - UPDATE_PROGRAMS_DISTANCE;

      Log.trace(Log.GL, "!!! activeIndex: " + activeIndex + ", left: " + left + ", right: " + right);

      if (activeIndex <= left && !currentEpg.isStartBlocked()) {
        Log.trace(Log.GL, "!!! UP && start not blocked");
        setPrograms(EpgCache.EpgPart.PAST, false);
        return true;
      } else if (activeIndex <= left && currentEpg.isStartBlocked() && !currentEpg.isEndBlocked()) {
        Log.trace(Log.GL, "!!! UP && start blocked && end not blocked");
        setPrograms(EpgCache.EpgPart.FUTURE, true);
        return true;
      } else if (activeIndex >= right && !currentEpg.isEndBlocked()) {
        Log.trace(Log.GL, "!!! DOWN && end not blocked");
        setPrograms(EpgCache.EpgPart.FUTURE, false);
        return true;
      } else if (activeIndex >= right && currentEpg.isEndBlocked() && !currentEpg.isStartBlocked()) {
        Log.trace(Log.GL, "!!! DOWN && end blocked && start not blocked");
        setPrograms(EpgCache.EpgPart.PAST, true);
        return true;
      }
    }
    return false;
  }

  private boolean setPrograms() {
    return setPrograms(EpgCache.EpgPart.FULL, false);
  }

  private boolean setPrograms(EpgCache.EpgPart epgPart, final boolean loadAll) {
    if (channels == null || channels.isEmpty()) {
      return false;
    }
    final ChannelModel channel = channels.get(getList(ListIndex.CHANNELS).getActiveIndex());

    RequestManager.cancelRequestsByIds(programsRequestId);
    currentEpg = null;

    programsRequestId = EpgCache.getInstance().getEpgForChannelId(programsChannelId = channel.id, epgPart,
      new EpgCache.EpgCallback<Epg>() {
        @Override
        public void onReceive(long channelId, EpgCache.EpgPart epgPart, Epg epg) {
          currentEpg = epg;
          ProgramModel curProgram = null;
          if (epgPart != EpgCache.EpgPart.FULL) {
            int activeIndex = getList(ListIndex.PROGRAMS).getActiveIndex();
            curProgram = programs != null ? programs.get(activeIndex) : null;
          }

          programs = epg != null && epg.hasEpg() ?
                  MiscHelper.safeRead(epg.getPrograms()) : Collections.<ProgramModel>emptyList();
          ProgramsAdapter adapter = new ProgramsAdapter(channel, programs, mListener);
          adapter.setActive(ListIndex.PROGRAMS == activeListIndex);
          CategoriesChannelsProgramsList list = getList(ListIndex.PROGRAMS);
          list.setAdapter(adapter);
          if (epg != null && !programs.isEmpty()) {
            getList(ListIndex.PROGRAMS).setActiveIndex(programs.indexOf(curProgram != null ? curProgram : epg.getNowProgram()));
          }

          if (!loadProgramsIfNeeded()) {
            programsRequestId = null;
            programsChannelId = ChannelModel.INVALID_CHANNEL_ID;
            getList(ListIndex.PROGRAMS).setBlocked(isProgramsLoading = false);
            handleActiveListChanged(list);
          }
        }

        @Override
        public void onError(long channelId, ExceptionWithErrorCode e) {
          Log.e(Log.GL, e);
          programsRequestId = null;
          programsChannelId = ChannelModel.INVALID_CHANNEL_ID;
          programs = Collections.emptyList();
          currentEpg = null;
          ProgramsAdapter adapter = new ProgramsAdapter(channel, programs, mListener);
          adapter.setActive(ListIndex.PROGRAMS == activeListIndex);
          CategoriesChannelsProgramsList list = getList(ListIndex.PROGRAMS);
          list.setAdapter(adapter);
          getList(ListIndex.PROGRAMS).setBlocked(isProgramsLoading = false);
          handleActiveListChanged(list);
        }
      });
    return false;
  }

  private boolean setProgramDescription() {
    CategoriesChannelsProgramsList list = getList(ListIndex.DESCRIPTION);
    DescriptionAdapter adapter = (DescriptionAdapter) list.getExpandableLoupeListAdapter();
    if (adapter != null) {
      adapter.setListener(null);
    }
    ProgramModel program = programs.isEmpty() ? null : programs.get(getList(ListIndex.PROGRAMS).getActiveIndex());
    adapter = new DescriptionAdapter(program, mListener);
    adapter.setActive(ListIndex.DESCRIPTION == activeListIndex);
    adapter.setListener(this);
    list.setAdapter(adapter);
    list.enable(false);
    adapter.updateText();
    if (!isDataLoaded) {
      isDataLoaded = true;
      spinner.setVisibility(INVISIBLE);
      scrollGroup.setVisibility(VISIBLE);
      scrollGroup.scrollContentTo(calculateScrollGroupContentX(activeListIndex), 0);
    }
    return false;
  }

  private void handleActiveListChanged(AbsList list) {
    if (list == getList(ListIndex.CATEGORIES)) {
      handleProgramsChangedTask.stop();
      handleChannelsChangedTask.stop();
      handleCategoriesChangedTask.start();
    } else if (list == getList(ListIndex.CHANNELS)) {
      handleProgramsChangedTask.stop();
      handleChannelsChangedTask.start();
    } else if (list == getList(ListIndex.PROGRAMS)) {
      if (!isProgramsLoading) {
        handleProgramsChangedTask.start();
      }
    } else if (BuildConfig.DEBUG) {
      throw new AssertionError("Invalid active list specified");
    }
  }

  @Override
  public void onActiveItemChanged(AbsList list, ListAdapter<?> adapter,
    int oldIndex, Actor oldActor, int newIndex, Actor newActor) {
    if (oldIndex != newIndex && newActor != null) {
      handleActiveListChanged(list);
    }
  }

  @Override
  public void onDescriptionChanged() {
    getList(ListIndex.DESCRIPTION).enable(true);
  }

  @Override
  public void onContentStartScroll(int startX, int startY, int endX, int endY) {
    //Log.trace(Log.GL, "start x:", t1.set(startX), "y:", t2.set(startY),
    //  "end x:", t3.set(endX), "y:", t4.set(endY));
    @ListIndex int index = calculateActiveListIndex(endX);
    //Log.trace(Log.GL, "cur active index:", t1.set(activeListIndex),
    //  "next active index:", t2.set(index));
    if (index != this.activeListIndex && isActiveListIndexValid(index) ) {
      if (isActiveListIndexValid(this.activeListIndex)) {
        getList(this.activeListIndex).setActive(false);
      }
      getList(this.activeListIndex = index).setActive(true);
    }
  }

  @Override
  public void onContentScrollStateChanged(@ScrollGroup.ScrollState int state) {}

  @Override
  public boolean keyDown(int code, int repeatCount) {
    if (activeListIndex == ListIndex.CATEGORIES && Keys.likeEnter(code)) {
      code = Keys.RIGHT;
    }
    handleEntitiesChangedTaskDelay = repeatCount > 0 ? BIG_HANDLE_ENTITIES_CHANGED_TASK_DELAY : SMALL_HANDLE_ENTITIES_CHANGED_TASK_DELAY;
    return GdxHelper.keyDown(getList(activeListIndex), code, repeatCount)
      || GdxHelper.keyDown(scrollGroup, code, repeatCount)
      || super.keyDown(code, repeatCount);
  }

  @Override
  public void draw(SpriteBatch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);
    if (scrollGroup.isVisible()) {
      drawDecorators(batch, parentAlpha);
    }
  }

  private void drawDecorators(SpriteBatch batch, float parentAlpha) {
    //draw default loupe list decorator
    float left = x;
    float bottom = y + (getMeasuredHeight() - LOUPE_ITEM_HEIGHT) / 2f;
    float top = y + (getMeasuredHeight() + LOUPE_ITEM_HEIGHT) / 2f - INACTIVE_LINE_HEIGHT;
    inactiveListItemLine.setPosition((int) left, (int) top);
    inactiveListItemLine.draw(batch, parentAlpha);
    inactiveListItemLine.setPosition((int) left, (int) bottom);
    inactiveListItemLine.draw(batch, parentAlpha);
    //draw decorator for an active list item
    top = y + (getMeasuredHeight() + LOUPE_ITEM_HEIGHT) / 2f - LINE_HEIGHT;
    left = x + (getMeasuredWidth() - LIST_WIDTH) / 2f + LINE_MARGIN;
    bottom = y + (getMeasuredHeight() - LOUPE_ITEM_HEIGHT) / 2f;
    activeListItemLine.setPosition((int) left, (int) top);
    activeListItemLine.draw(batch, parentAlpha);
    activeListItemLine.setPosition((int) left, (int) bottom);
    activeListItemLine.draw(batch, parentAlpha);
  }

  private abstract class DelayedTask implements Runnable {
    public void start() {
      GdxHelper.removeRunnable(this);
      GdxHelper.runOnGdxThread(this, handleEntitiesChangedTaskDelay);
    }

    public void stop() {
      GdxHelper.removeRunnable(this);
    }
  }
}
