package tv.mediastage.frontstagesdk.channel.management;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.badlogic.gdx.backends.android.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.FadeTo;
import com.badlogic.gdx.scenes.scene2d.layouts.FrameGroup;

import java.util.Arrays;
import java.util.List;

import tv.mediastage.frontstagesdk.BuildConfig;
import tv.mediastage.frontstagesdk.GLListener;
import tv.mediastage.frontstagesdk.R;
import tv.mediastage.frontstagesdk.TheApplication;
import tv.mediastage.frontstagesdk.cache.ChannelCategoriesCache;
import tv.mediastage.frontstagesdk.cache.ChannelServicesCache;
import tv.mediastage.frontstagesdk.cache.ChannelsCache;
import tv.mediastage.frontstagesdk.cache.ProgramDescriptionCache;
import tv.mediastage.frontstagesdk.cache.epg.EpgCache;
import tv.mediastage.frontstagesdk.factories.GLBaseScreenFactory;
import tv.mediastage.frontstagesdk.model.CategoryModel;
import tv.mediastage.frontstagesdk.model.ChannelModel;
import tv.mediastage.frontstagesdk.model.ProgramModel;
import tv.mediastage.frontstagesdk.util.GdxHelper;
import tv.mediastage.frontstagesdk.util.MiscHelper;
import tv.mediastage.frontstagesdk.util.PvrChecker;
import tv.mediastage.frontstagesdk.util.RecordHelper;
import tv.mediastage.frontstagesdk.util.SizeHelper;
import tv.mediastage.frontstagesdk.util.TimeHelper;
import tv.mediastage.frontstagesdk.widget.DescriptionView;
import tv.mediastage.frontstagesdk.widget.ImageActor;
import tv.mediastage.frontstagesdk.widget.KeyboardInput;
import tv.mediastage.frontstagesdk.widget.PvrButton;
import tv.mediastage.frontstagesdk.widget.Recyclable;
import tv.mediastage.frontstagesdk.widget.Spinner;
import tv.mediastage.frontstagesdk.widget.TextActor;
import tv.mediastage.frontstagesdk.widget.TextButton;
import tv.mediastage.frontstagesdk.widget.TimeLine;
import tv.mediastage.frontstagesdk.widget.WebImage;
import tv.mediastage.frontstagesdk.widget.list.VerticalExpandableList;
import tv.mediastage.frontstagesdk.widget.list.adapter.ExpandableLoupeListAdapter;
import tv.mediastage.frontstagesdk.widget.list.adapter.LoupeListAdapter;

import static com.badlogic.gdx.scenes.scene2d.Actor.Gravity.BOTTOM;
import static com.badlogic.gdx.scenes.scene2d.Actor.Gravity.CENTER;
import static com.badlogic.gdx.scenes.scene2d.Actor.Gravity.CENTER_HORIZONTAL;
import static com.badlogic.gdx.scenes.scene2d.Actor.Gravity.CENTER_VERTICAL;
import static com.badlogic.gdx.scenes.scene2d.Actor.Gravity.LEFT;
import static com.badlogic.gdx.scenes.scene2d.Actor.Gravity.TOP;
import static tv.mediastage.frontstagesdk.subscription.ChannelSubscriptionScreen.SubscriptionListener;

/**
 * Created by yuly on 08.08.17.
 */
public final class CategoriesChannelsProgramsList extends VerticalExpandableList {
  static final int CATEGORY_ID_ALL = 0;
  static final int CATEGORY_ID_FAVORITE = -1;

  static final int INACTIVE_LINE_HEIGHT = MiscHelper.getPixelDimen(R.dimen.focus_line_height);
  static final int LINE_HEIGHT = MiscHelper.getPixelDimen(R.dimen.pcl_active_line_height);
  static final int LINE_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_active_line_margin);
  static final int ITEM_HEIGHT = MiscHelper.getPixelDimen(R.dimen.pcl_item_height);
  static final int LOUPE_ITEM_HEIGHT = MiscHelper.getPixelDimen(R.dimen.pcl_loupe_item_height);
  static final int TITLE_FONT_SIZE = SizeHelper.getDPScaledDimen(R.dimen.pcl_title_font_size);
  static final int TEXT_FONT_SIZE = SizeHelper.getDPScaledDimen(R.dimen.pcl_text_font_size);
  static final int TEXT_SEL_FONT_SIZE = SizeHelper.getDPScaledDimen(R.dimen.pcl_text_selected_font_size);
  static final int TEXT_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_text_margin);
  static final int TITLE_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_title_margin);
  static final int GROUP_MARGIN1 = MiscHelper.getPixelDimen(R.dimen.pcl_group_margin1);
  static final int GROUP_MARGIN2 = MiscHelper.getPixelDimen(R.dimen.pcl_group_margin2);
  static final int GROUP_MARGIN_SMALL = MiscHelper.getPixelDimen(R.dimen.pcl_group_margin_small);
  static final int ICON_SIZE = MiscHelper.getPixelDimen(R.dimen.pcl_category_icon_size);
  static final int LOGO_WIDTH = MiscHelper.getPixelDimen(R.dimen.pcl_channel_logo_width);
  static final int LOGO_HEIGHT = MiscHelper.getPixelDimen(R.dimen.pcl_channel_logo_height);
  static final int POSTER_WIDTH = MiscHelper.getPixelDimen(R.dimen.pcl_poster_width);
  static final int POSTER_HEIGHT = MiscHelper.getPixelDimen(R.dimen.pcl_poster_height);
  static final int POSTER_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_poster_margin);
  static final int PVR_BUTTON_SIZE = MiscHelper.getPixelDimen(R.dimen.pcl_pvr_icon_size);
  static final int HOR_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_description_hor_margin);
  static final int VER_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_description_ver_margin);
  static final int LIVE_HOR_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_live_hor_margin);
  static final int LIVE_VER_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_live_ver_margin);
  static final int RUBLE_VER_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_ruble_ver_margin);
  static final int TIMELINE_BOTTOM_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_timeline_bottom_margin);
  static final int PR_BUTTON_SIZE = MiscHelper.getPixelDimen(R.dimen.pcl_pr_icon_size);
  static final int PR_BUTTON_LEFT_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_pr_icon_left_margin);
  static final int PR_BUTTON_BOTTOM_MARGIN = MiscHelper.getPixelDimen(R.dimen.pcl_pr_icon_bottom_margin);

  static final Color ACTIVE_COLOR = MiscHelper.colorFrom(R.color.active_color);
  static final Color INACTIVE_COLOR = MiscHelper.colorFrom(R.color.non_active_color);
  static final Color NOT_AVAILABLE_COLOR = MiscHelper.colorFrom(R.color.not_avialable_color);

  private float disabledActionAlpha, enabledActionAlpha;
  private @Nullable FadeTo disableAction;
  private @Nullable FadeTo enableAction;
  private boolean isEnabled = true;
  private boolean isBlocked;

  public CategoriesChannelsProgramsList(String name, @NonNull KeyboardInput keyboardInput) {
    super(name, keyboardInput);
  }

  public void fadeWhenEnablityChanging(boolean fade,
    float disabledAlpha, float disableDuration, float enableDuration) {
    Action enableAction = this.enableAction, disableAction = this.disableAction;
    if (enableAction != null) {
      enableAction.setCompletionListener(null);
      enableAction.reset();
      enableAction.finish();
      this.enableAction = null;
    } else {
      enabledActionAlpha = color.a;
    }
    if (disableAction != null) {
      disableAction.setCompletionListener(null);
      disableAction.reset();
      disableAction.finish();
      this.disableAction = null;
    }
    if (fade) {
      this.enableAction = FadeTo.$(enabledActionAlpha, enableDuration);
      this.disableAction = FadeTo.$(disabledActionAlpha = disabledAlpha, disableDuration);
    }
    changeEnablity(isEnabled);
  }

  public void enable(boolean value) {
    if (isEnabled != value) {
      changeEnablity(value);
    }
  }

  private void changeEnablity(boolean enable) {
    if (enable) {
      Action action = disableAction;
      if (action != null && action.getTarget() != null) {
        action.setCompletionListener(null);
        action.reset();
      }
      if ((action = enableAction) != null) {
        if (color.a == enabledActionAlpha) {
          isEnabled = true;
        } else if (action.getTarget() == null) {
          action.setTarget(this);
        }
      } else {
        isEnabled = true;
      }
    } else {
      isEnabled = false;
      Action action = enableAction;
      if (action != null && action.getTarget() != null) {
        action.setCompletionListener(null);
        action.reset();
      }
      if ((action = disableAction) != null) {
        if (color.a != disabledActionAlpha && action.getTarget() == null) {
          action.setTarget(this);
        }
      }
    }
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    FadeTo action = enableAction;
    if (action != null && action.getTarget() != null) {
      action.act(delta);
      if (action.isDone()) {
        action.setCompletionListener(null);
        action.reset();
        isEnabled = true;
      }
    }
    if ((action = disableAction) != null && action.getTarget() != null) {
      action.act(delta);
      if (action.isDone()) {
        action.setCompletionListener(null);
        action.reset();
      }
    }
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public boolean isBlocked() {
    return isBlocked;
  }

  public void setBlocked(boolean blocked) {
    isBlocked = blocked;
  }

  public void setActive(boolean active) {
    ProgramChannelListAdapter a = (ProgramChannelListAdapter) getExpandableLoupeListAdapter();
    if (BuildConfig.DEBUG && a == null) {
      throw new AssertionError("No adapter");
    }
    if (a != null) {
      a.setActive(active);
    }
  }

  @Override
  public boolean smoothScrollToIndex(int index) {
    ExpandableLoupeListAdapter a = getExpandableLoupeListAdapter();
    if (!isBlocked && a != null && (index < a.getMinimumAdapterIndex() || index > a.getMaximumAdapterIndex())) {
      index = index < a.getMinimumAdapterIndex() ? a.getMaximumAdapterIndex() :
              index > a.getMaximumAdapterIndex() ? a.getMinimumAdapterIndex() : index;
      int oldIndex = getActiveIndex();
      Actor oldActor = getActiveActor();
      setActiveIndex(index);
      notifyActiveChanged(oldIndex, oldActor, index, obtainActor(index));
      return true;
    }
    return super.smoothScrollToIndex(index);
  }

  @Override
  public boolean keyDown(int code, int repeatCount) {
    if (!isEnabled) {
      return false;
    }
    ProgramChannelListAdapter a = (ProgramChannelListAdapter) getExpandableLoupeListAdapter();
    return (a != null && a.keyDown(this, code, repeatCount)) || super.keyDown(code, repeatCount);
  }

  public static abstract class ProgramChannelListAdapter<T> extends LoupeListAdapter {
	  protected static final String TITLE_NAME = "title";
	  protected static final String TEXT_NAME = "text";
	  protected static final String IMAGE_NAME = "image";
	  protected static final String BUTTON_NAME = "button";
	  protected static final String LIVE_ICON = "live_icon";
	  protected static final String TIMELINE_NAME = "timeline";
	  protected static final String PR_IMAGE_NAME = "pr_image";
      protected static final String CHECK_NAME = "check";

	  protected final GLListener glListener;

    protected List<T> items;
    protected boolean isActive;

    public static TextActor createSingleLineTextView(String text,  boolean layoutCenterX,
      boolean isActiveFontSize, boolean isActiveColor, boolean isActiveFontStyle,
      boolean isNeedScroll, boolean isNotAvailable, int leftMargin, int rightMargin,
      int bottomMargin, int topMargin, int fontSize,
      int activeFontSize) {
      TextActor result = new TextActor(text);
      result.setDesiredSize(WRAP_CONTENT, WRAP_CONTENT);
      result.setMargin(leftMargin, rightMargin, bottomMargin, topMargin);
      result.setGravity((layoutCenterX ? CENTER_HORIZONTAL : LEFT) | BOTTOM);
      result.setAlignment(layoutCenterX ? BitmapFont.HAlignment.CENTER : BitmapFont.HAlignment.LEFT);
      result.setFontSize(isActiveFontSize ? activeFontSize : fontSize);
      result.setFontStyle(isActiveFontStyle ? TextActor.FontStyle.BOLD : TextActor.FontStyle.BOOK);
      result.setColor(isActiveColor ? ACTIVE_COLOR : isNotAvailable? NOT_AVAILABLE_COLOR : INACTIVE_COLOR);
      result.setText(text);
      result.setScrollHorizontal(isNeedScroll);
      result.setSingleLine(true);
      result.touchable = false;
      return result;
    }

    public ProgramChannelListAdapter(List<T> items, GLListener glListener) {
      this.glListener = glListener;
      this.items = items;
    }

    public void setActive(boolean isActive) {
      if (this.isActive != isActive) {
        this.isActive = isActive;
        notifyDataChanged();
      }
    }

    public List<T> getItems() {
      return items;
    }

    public void setItems(List<T> items) {
      this.items = items;
      notifyDataChanged();
    }

    @Override
    public T getItem(int index) {
      return items != null ? items.get(index) : null;
    }

    @Override
    public void onActorBind(int index, Actor convertActor, boolean loupe) {}

    @Override
    public int getItemCount() {
      return items != null ? items.size() : 0;
    }
      
    public boolean keyDown(CategoriesChannelsProgramsList list, int code, int repeatCount) {
      return false;
    }
  }

  private static abstract class RecycableFrameGroup extends FrameGroup implements Recyclable {
    public RecycableFrameGroup() {
      super(null);
    }
  }

  public static final class CategoriesAdapter extends ProgramChannelListAdapter<CategoryModel> {
    public CategoriesAdapter(@NonNull List<CategoryModel> categories, GLListener glListener) {
      super(categories, glListener);
    }

    @Override
    public Actor onActorCreate(int index, final boolean loupe) {
      final TextActor text = createSingleLineTextView(TEXT_NAME,
        isActive && loupe, loupe, isActive && loupe, isActive && loupe, isActive && loupe, false,
        0, 0, TEXT_MARGIN, 0, TEXT_FONT_SIZE, TEXT_SEL_FONT_SIZE);

      final TextActor title = createSingleLineTextView(TITLE_NAME,
        isActive && loupe, loupe, isActive && loupe, isActive && loupe, false, false,
        0, 0, TITLE_MARGIN, 0, TITLE_FONT_SIZE, TITLE_FONT_SIZE);
        title.setVisibility(loupe ? VISIBLE : GONE);

      final WebImage icon = new WebImage(IMAGE_NAME);
      icon.setDesiredSize(ICON_SIZE, ICON_SIZE);
      icon.setGravity(CENTER_HORIZONTAL | TOP);
      icon.setVisibility(isActive && loupe ? VISIBLE : GONE);

      final ImageActor checkMark = new ImageActor(CHECK_NAME);
      checkMark.setDesiredSize(WRAP_CONTENT, WRAP_CONTENT);
      checkMark.setImageResource(loupe && isActive? R.drawable.check_mark_icon : R.drawable.check_mark_thin_icon);
      checkMark.setMargin(MiscHelper.getDefaultMargin()/2,0,TEXT_MARGIN + MiscHelper.getDefaultMargin()/2,0);
      MiscHelper.setColorFrom(checkMark.color, loupe && isActive? R.color.active_color : R.color.non_active_color);

      FrameGroup group = new RecycableFrameGroup() {
        @Override
        public void onMeasure(int actorMeasureSpecWidth, int actorMeasureSpecHeight) {
          super.onMeasure(actorMeasureSpecWidth, actorMeasureSpecHeight);
          if (isActive && loupe) {
            int margin = (getMeasuredHeight() - icon.getMeasuredHeight() -
              title.getMeasuredHeight() - title.getBottomMargin() - title.getTopMargin()
              - text.getMeasuredHeight() - text.getBottomMargin() - text.getTopMargin()) / 2
              + LINE_HEIGHT;
            icon.setMargin(0, 0, 0, margin);
          }
        }

        @Override
        public void onLayout(int measuredWidth, int measuredHeight) {
          super.onLayout(measuredWidth, measuredHeight);
          getLayouter(title).aboveWithMargins(text);
          getLayouter(checkMark).toRightOf(text);
        }

        @Override
        public void recycle() {
          icon.recycle();
        }
      };
      group.setDesiredSize(WRAP_CONTENT, loupe ? LOUPE_ITEM_HEIGHT : ITEM_HEIGHT);
      int margin = !isActive ? GROUP_MARGIN1 : GROUP_MARGIN_SMALL;
      group.setMargin(margin, margin, 0, 0);
      group.setLayoutWithGravity(true);
      group.addActor(text);
      group.addActor(title);
      group.addActor(icon);
      group.addActor(checkMark);
      return group;
    }

	@Override
	public void onActorBind(int index, Actor convertActor, boolean loupe) {
		final CategoryModel category = items.get(index);

		final TextActor text = (TextActor) ((Group) convertActor).findActor(TEXT_NAME);
		text.setText(String.format("%s (%d)", category.name.toUpperCase(), ChannelCategoriesCache.getInstance().getChannelsByCategory(category.id).size()));

		final TextActor title = (TextActor) ((Group) convertActor).findActor(TITLE_NAME);
		title.setText(R.string.category);

		final WebImage icon = (WebImage) ((Group) convertActor).findActor(IMAGE_NAME);
		if (!TextUtils.isEmpty(category.srcImgFile)) {
			icon.refresh(TheApplication.getPicasso()
					.loadWithServer(category.srcImgFile)
					.resize(ICON_SIZE, ICON_SIZE).centerInside()
					.error(R.drawable.icon_ch_category_all)
					.inBitmap(true));
		} else {
			icon.recycle();
			icon.setImageResource(category.id == CATEGORY_ID_FAVORITE ? R.drawable.icon_ch_category_favorite : R.drawable.icon_ch_category_all);
		}

		final Actor check = ((Group) convertActor).findActor(CHECK_NAME);
		check.setVisibility(CurrentCategoryHelper.getInstance().isCurrent(category)? VISIBLE : INVISIBLE);
	}

    @Override
    public boolean keyDown(CategoriesChannelsProgramsList list, int code, int repeatCount) {
      return false;
    }
  }

  public static final class ChannelsAdapter extends ProgramChannelListAdapter<ChannelModel> {
	  public ChannelsAdapter(@NonNull List<ChannelModel> channels, GLListener glListener) {
      super(channels, glListener);
    }

    @Override
    public Actor onActorCreate(int index, final boolean loupe) {
      ChannelModel channel = items.get(index);

      final TextActor text = createSingleLineTextView(TEXT_NAME,
        isActive && loupe, loupe, isActive && loupe, isActive && loupe, isActive && loupe, false,
        isActive && loupe ? 0 : TEXT_MARGIN, 0, TEXT_MARGIN, 0, TEXT_FONT_SIZE, TEXT_SEL_FONT_SIZE);

      final TextActor title = createSingleLineTextView(TITLE_NAME,
        isActive && loupe, loupe, isActive && loupe, isActive && loupe, false, false,
        0, 0, TITLE_MARGIN, 0, TITLE_FONT_SIZE, TITLE_FONT_SIZE);

      final WebImage logo = new WebImage(IMAGE_NAME);
      logo.setGravity(CENTER_HORIZONTAL | TOP);
      logo.setScaleType(ImageActor.ScaleType.FIT_CENTER);
      logo.setUseFadeAnimation(false);
      logo.setVisibility(loupe ? VISIBLE : GONE);

      final ImageActor rubleButton = new ImageActor(BUTTON_NAME);
      rubleButton.setDesiredSize(PVR_BUTTON_SIZE, PVR_BUTTON_SIZE);
      rubleButton.setGravity(LEFT | (loupe ? TOP : CENTER_VERTICAL));
      rubleButton.setMargin(0, TEXT_MARGIN, 0, loupe ? RUBLE_VER_MARGIN : 0);

      FrameGroup group = new RecycableFrameGroup() {
        @Override
        public void onMeasure(int actorMeasureSpecWidth, int actorMeasureSpecHeight) {
          super.onMeasure(actorMeasureSpecWidth, actorMeasureSpecHeight);
          int margin = (getMeasuredHeight() - logo.getMeasuredHeight()
            - title.getMeasuredHeight() - title.getBottomMargin() - title.getTopMargin()
            - text.getMeasuredHeight() - text.getBottomMargin() - text.getTopMargin()) / 2
            + LINE_HEIGHT;
          logo.setMargin(0, 0, 0, margin);
        }

        @Override
        public void onLayout(int measuredWidth, int measuredHeight) {
          super.onLayout(measuredWidth, measuredHeight);
          if (!(isActive && loupe)) {
            getLayouter(text).toRightOf(rubleButton);
            getLayouter(title).alignLeft(text);
          }
          getLayouter(title).aboveWithMargins(text);
        }

        @Override
        public void recycle() {
          logo.recycle();
          rubleButton.recycle();
        }
      };
      group.setDesiredSize(WRAP_CONTENT, loupe ? LOUPE_ITEM_HEIGHT : ITEM_HEIGHT);
      int margin = !isActive ? GROUP_MARGIN1 : GROUP_MARGIN_SMALL;
      group.setMargin(margin, margin, 0, 0);
      group.setLayoutWithGravity(true);
      group.addActor(text);
      group.addActor(title);
      group.addActor(logo);
      group.addActor(rubleButton);
      return group;
    }

	  @Override
	  public void onActorBind(int index, Actor convertActor, boolean loupe) {
		  ChannelModel channel = items.get(index);

		  final TextActor text = (TextActor) ((Group) convertActor).findActor(TEXT_NAME);
		  text.setText(channel.getName().toUpperCase());

		  final TextActor title = (TextActor) ((Group) convertActor).findActor(TITLE_NAME);
		  title.setText("" + channel.number);

		  final WebImage logo = (WebImage) ((Group) convertActor).findActor(IMAGE_NAME);
		  if (!TextUtils.isEmpty(channel.srcImgFile)) {
			  logo.refresh(TheApplication.getPicasso()
					  .loadWithServer(channel.srcImgFile)
					  .resize(LOGO_WIDTH, LOGO_HEIGHT).centerInside()
					  .error(R.drawable.epg_poster_stub)
					  .inBitmap(true));
		  } else {
			  logo.recycle();
			  logo.setImageResource(R.drawable.epg_poster_stub);
		  }

		  final ImageActor rubleButton = (ImageActor) ((Group) convertActor).findActor(BUTTON_NAME);
		  rubleButton.setImageResource(R.drawable.currency_rub_white);
		  MiscHelper.setColorFrom(rubleButton.color, R.color.pvr_buttons_tint_color);
		  rubleButton.setVisibility(!channel.isSubscribed() ? VISIBLE : GONE);
	  }

	  @Override
    public boolean keyDown(CategoriesChannelsProgramsList list, int code, int repeatCount) {
      if (Keys.likeEnter(code)) {
        ChannelModel channel = getItem(list.getActiveIndex());
        CurrentCategoryHelper.getInstance().onGoingToChangeCategory(channel.id);
        ProgramModel program = EpgCache.getInstance().getNowProgram(channel.id);
        if (program != null) {
          ProgramModel.handleProgramClick(glListener, program);
        } else if (!channel.isSubscribed()) {
          glListener.startScreen(
            GLListener.getScreenFactory().createChannelSubscriptionScreenIntent(channel, program));
        } else {
          glListener.getWatchingController().playChannel(channel, true);
        }
        return true;
      }
      return false;
    }
  }

  public static final class ProgramsAdapter extends ProgramChannelListAdapter<ProgramModel> {
	  private ChannelModel channel;

    public ProgramsAdapter(@NonNull ChannelModel channel, @NonNull List<ProgramModel> programs,
      @NonNull GLListener glListener) {
      super(!programs.isEmpty() ? programs : Arrays.asList(new ProgramModel[] {null}), glListener);
      this.channel = channel;
    }

    @Override
    public Actor onActorCreate(int index, final boolean loupe) {
      ProgramModel program = items.get(index);
      FrameGroup group;

      if (program != null) {
        final boolean isNowProgram = program.getType() == ProgramModel.TYPE_NOW;
        final boolean isFutureProgram = program.getType() == ProgramModel.TYPE_NEXT;
        final boolean isNoPvrProgram = !PvrChecker.isPvrEnabled(channel, program, isNowProgram ?
          PvrChecker.PvrType.SO :
            isFutureProgram ? PvrChecker.PvrType.PVR : PvrChecker.PvrType.LXDTV);

        final TextActor text = createSingleLineTextView(TEXT_NAME,
          isActive && loupe, loupe, isActive && loupe, isActive && loupe,
          isActive && loupe, isFutureProgram || isNoPvrProgram,
          !loupe && !isNoPvrProgram ? TEXT_MARGIN : 0, 0,
          TEXT_MARGIN, 0, TEXT_FONT_SIZE, TEXT_SEL_FONT_SIZE);

        final TextActor title = createSingleLineTextView(TITLE_NAME,
          isActive && loupe, loupe, isActive && loupe, isActive && loupe,
          false, isFutureProgram || isNoPvrProgram,
          0, 0, TITLE_MARGIN, 0, TITLE_FONT_SIZE, TITLE_FONT_SIZE);

        final TextButton liveIcon = new TextButton(LIVE_ICON);
        liveIcon.touchable = false;
        liveIcon.setDesiredSize(WRAP_CONTENT,
        MiscHelper.getPixelDimen(R.dimen.default_font_size_large));
        liveIcon.setMargin(LIVE_HOR_MARGIN, 0, LIVE_VER_MARGIN, 0);
        liveIcon.setText("LIVE");
        liveIcon.setGravity(LEFT | TOP);
        liveIcon.setResourceFontSize(R.dimen.program_live_icon_font_size);

        final PvrButton pvrButton = PvrButton.makeButton(glListener, channel, program, true, !loupe, false);
        if (pvrButton != null) {
          int size = loupe ? PvrButton.PVR_ICON_SIZE : PVR_BUTTON_SIZE;
          pvrButton.setDesiredSize(size, size);
          pvrButton.setGravity(loupe ? CENTER_HORIZONTAL | TOP : LEFT | CENTER_VERTICAL);
        }

        final WebImage poster = new WebImage(IMAGE_NAME);
        poster.setDesiredSize(POSTER_WIDTH, POSTER_HEIGHT);
        poster.setGravity((isActive ? Gravity.CENTER_HORIZONTAL : Gravity.LEFT) | Gravity.TOP);
        poster.setMargin(0, 0, 0, POSTER_MARGIN);
        poster.setChangeAlphaOnTouch(true);
        poster.touchable = true;

        final TimeLine timeLine = new TimeLine(TIMELINE_NAME);
        timeLine.setDesiredSize(POSTER_WIDTH, WRAP_CONTENT);
        timeLine.setMargin(0, 0, TIMELINE_BOTTOM_MARGIN, 0);
        timeLine.setMinimumSize(0, 0);
        timeLine.setVisibility(isNowProgram && loupe ? VISIBLE : GONE);
        timeLine.setSeekable(false);

        final ImageActor prImage = new ImageActor(PR_IMAGE_NAME);
        prImage.setDesiredSize(PR_BUTTON_SIZE, PR_BUTTON_SIZE);
        prImage.setMargin(PR_BUTTON_LEFT_MARGIN, 0, PR_BUTTON_BOTTOM_MARGIN, 0);

        group = new RecycableFrameGroup() {
          @Override
          public void onLayout(int measuredWidth, int measuredHeight) {
            super.onLayout(measuredWidth, measuredHeight);
            if (!loupe && pvrButton != null) {
              getLayouter(text).toRightOf(pvrButton);
              getLayouter(title).alignLeft(text);
            }
            if (loupe && pvrButton != null) {
              getLayouter(pvrButton).alignExternalCenter(poster);
            }
            getLayouter(title).aboveWithMargins(text);
            getLayouter(liveIcon).toRightOf(title).alignExternalBottom(title);
            getLayouter(prImage).toRightOf(title).alignExternalBottom(title);
            getLayouter(timeLine).alignLeft(poster).alignExternalBottom(poster);
          }

          @Override
          public void recycle() {
            poster.recycle();
            if (pvrButton != null) {
              pvrButton.recycle();
            }
            prImage.recycle();
          }
        };
        group.setDesiredSize(WRAP_CONTENT, loupe ? LOUPE_ITEM_HEIGHT : ITEM_HEIGHT);
        int margin = !isActive ? GROUP_MARGIN1 : GROUP_MARGIN_SMALL;
        group.setMargin(margin + (loupe && !isActive && !isNoPvrProgram ? GROUP_MARGIN2 : 0),
          margin, 0, 0);
        group.setLayoutWithGravity(true);
        group.addActor(text);
        group.addActor(title);
        group.addActor(liveIcon);
        group.addActor(poster);
        group.addActor(timeLine);
        group.addActor(prImage);
        if (pvrButton != null) {
          group.addActor(pvrButton);
        }
      } else {
        TextActor text = new TextActor(TEXT_NAME);
        text.setDesiredSize(MATCH_PARENT, WRAP_CONTENT);
        text.setGravity(CENTER);
        text.setAlignment(BitmapFont.HAlignment.CENTER);
        text.setFontSize(TEXT_FONT_SIZE);
        text.setFontStyle(TextActor.FontStyle.BOOK);
        text.setColor(INACTIVE_COLOR);
        text.setScrollHorizontal(true);
        text.setSingleLine(true);
        text.touchable = false;

        group = new FrameGroup(null);
        group.setDesiredSize(MATCH_PARENT, LOUPE_ITEM_HEIGHT);
        group.setLayoutWithGravity(true);
        group.addActor(text);
      }
      return group;
    }

	  @Override
	  public void onActorBind(int index, Actor convertActor, boolean loupe) {
		  ProgramModel program = items.get(index);

		  if (program != null) {
			  final boolean isNowProgram = program.getType() == ProgramModel.TYPE_NOW;

			  final TextActor text = (TextActor) ((Group) convertActor).findActor(TEXT_NAME);
			  text.setText(program.getName());

			  final TextActor title = (TextActor) ((Group) convertActor).findActor(TITLE_NAME);
			  title.setText(TimeHelper.getDateFormatter_HHmm().format(program.getStartTimeDate()) +
							(loupe ? " - " + TimeHelper.getDateFormatter_HHmm().format(program.getStopTimeDate()) : ""));

			  final TextButton liveIcon = (TextButton) ((Group) convertActor).findActor(LIVE_ICON);
			  liveIcon.setVisibility((!loupe && isNowProgram) ? VISIBLE : INVISIBLE);

			  final PvrButton pvrButton = (PvrButton) ((Group) convertActor).findActor(PvrButton.PVR_BUTTON_NAME);
			  PvrButton.updateButton(pvrButton, glListener, channel, program, true, !loupe, false);

			  final WebImage poster = (WebImage) ((Group) convertActor).findActor(IMAGE_NAME);
			  poster.setVisibility(loupe ? VISIBLE : GONE);
			  if (!TextUtils.isEmpty(program.srcImgFile)) {
				  poster.refresh(TheApplication.getPicasso()
						  .loadWithServer(program.srcImgFile)
						  .resize(POSTER_WIDTH, POSTER_HEIGHT).centerCrop()
						  .error(R.drawable.epg_poster_stub));
			  } else {
				  poster.recycle();
				  poster.setImageResource(R.drawable.epg_poster_stub);
			  }

			  final TimeLine timeLine = (TimeLine) ((Group) convertActor).findActor(TIMELINE_NAME);
			  timeLine.setDuration(program.getStopTimeDate().getTime() - program.getStartTimeDate().getTime());
			  timeLine.setProgress(System.currentTimeMillis() - program.getStartTimeDate().getTime());

			  final ImageActor prImage = (ImageActor) ((Group) convertActor).findActor(PR_IMAGE_NAME);
			  MiscHelper.setColorFrom(prImage.color, R.color.pr_icon_tint_color);
			  prImage.setImageResource(MiscHelper.getPrLevelDrawableResId(channel.prLevel));
			  prImage.setVisibility(channel.isAgeRestricted() && loupe ? Actor.VISIBLE : Actor.GONE);

		  } else {
			  TextActor text = (TextActor) ((Group) convertActor).findActor(TEXT_NAME);
			  text.setText(R.string.no_program_text);
		  }
	  }

	@Override
	public boolean keyDown(CategoriesChannelsProgramsList list, int code, int repeatCount) {
		final ProgramModel program = getItemCount() != 0 ? getItem(list.getActiveIndex()) : null;

		if (Keys.likeEnter(code)) {
			if(program != null) {
				CurrentCategoryHelper.getInstance().onGoingToChangeCategory(program.channelId);
			}
			handleProgramClick(program);
			return true;
      	}

	  	if (Keys.isRec(code) && program != null && program.getType() == ProgramModel.TYPE_NEXT &&
			PvrChecker.isPvrEnabled(channel, program, PvrChecker.PvrType.PVR)) {
			handleProgramClick(program);
			return true;
	  	}
      	return false;
    }

	private void handleProgramClick(ProgramModel program) {
		ProgramModel.handleProgramClick(glListener, program, true, true, new RecordHelper.RecordingListener() {
			@Override
			public void onResult(RecordHelper.RecordResult recordResult) {
				notifyDataChanged();
			}
		});
	}
  }

  public static final class DescriptionAdapter extends ProgramChannelListAdapter<ProgramModel>
    implements ProgramDescriptionCache.DescriptionCallback {

    public interface Listener {
      void onDescriptionChanged();
    }

    private final DescriptionView descriptionView;
    private final Spinner spinner;

    private final Runnable notifyDescriptionChangedTask = new Runnable() {
      @Override
      public void run() {
        if (listener != null) {
          listener.onDescriptionChanged();
        }
      }
    };

    private Listener listener;

    public DescriptionAdapter(@Nullable ProgramModel program, GLListener glListener) {
      super(Arrays.asList(program), glListener);
      descriptionView = new DescriptionView(null);
      descriptionView.setMargin(HOR_MARGIN, HOR_MARGIN, VER_MARGIN, VER_MARGIN);
      descriptionView.setTextViewGravity(Gravity.TOP);
      descriptionView.setDrawBottomShader(true);
      spinner = Spinner.createDefault(Spinner.Style.SMALL, true);
    }

    public void setListener(Listener value) {
      listener = value;
    }

    @Override
    public Actor onActorCreate(int index, boolean loupe) {
      final FrameGroup result;
      ProgramModel program = items.get(0);
      if (program != null) {  
        ChannelModel channel = ChannelsCache.getInstance().getChannel(program);
        final boolean isNowProgram = program.getType() == ProgramModel.TYPE_NOW;
        final TextActor text = new TextActor(null);
        text.setDesiredSize(WRAP_CONTENT, WRAP_CONTENT);
        text.setMargin(0, 0, TEXT_MARGIN, 0);
        text.setGravity(CENTER_HORIZONTAL | BOTTOM);
        text.setFontSize(loupe ? TEXT_SEL_FONT_SIZE : TEXT_FONT_SIZE);
        text.setColor(isActive && loupe ? ACTIVE_COLOR : INACTIVE_COLOR);
        text.setFontStyle(isActive && loupe ? TextActor.FontStyle.BOLD : TextActor.FontStyle.BOOK);
        text.setText(
          isNowProgram && PvrChecker.isPvrEnabled(channel, program, PvrChecker.PvrType.SO) ?
            R.string.description_now_text : R.string.description_text);
        text.setSingleLine(true);
        text.touchable = false;
        text.setVisibility(isNowProgram || !isActive ? VISIBLE : GONE);
        result = new FrameGroup(null) {
          @Override
          public void onMeasure(int actorMeasureSpecWidth, int actorMeasureSpecHeight) {
            super.onMeasure(actorMeasureSpecWidth, actorMeasureSpecHeight);
            int descHeight = getMeasuredHeight() - descriptionView.getTopMargin()
              - descriptionView.getBottomMargin()
              - ((isNowProgram || !isActive) ?
                  text.getMeasuredHeight() + text.getTopMargin() + text.getBottomMargin() : 0);
            descriptionView.setDesiredSize(descriptionView.getMeasuredWidth(), descHeight);
          }

          @Override
          public void onLayout(int measuredWidth, int measuredHeight) {
            super.onLayout(measuredWidth, measuredHeight);
            if (isNowProgram || !isActive) {
              getLayouter(descriptionView).aboveWithMargins(text);
            }
          }

          @Override
          public boolean keyDown(int keycode, int repeatCount) {
            return GdxHelper.keyDown(descriptionView, keycode, repeatCount);
          }
        };
        result.setDesiredSize(MATCH_PARENT, LOUPE_ITEM_HEIGHT);
        result.setLayoutWithGravity(true);
        result.addActor(descriptionView);
        result.addActor(spinner);
        result.addActor(text);
      } else {
        descriptionView.setTextViewGravity(Gravity.CENTER);
        descriptionView.setTextViewTextAlignment(BitmapFont.HAlignment.CENTER);
        result = new FrameGroup(null);
        result.setDesiredSize(MATCH_PARENT, LOUPE_ITEM_HEIGHT);
        result.setLayoutWithGravity(true);
        result.addActor(descriptionView);
      }
      return result;
    }

    @Override
    public void onReceived(long programId, String description) {
      descriptionView.setText(!TextUtils.isEmpty(description) ? description + "\n " : "");
      spinner.setVisibility(Actor.INVISIBLE);
      notifyDescriptionChanged();
    }

    public void updateText() {
      ProgramModel program = items.get(0);
      if (program != null) {  
        if (TextUtils.isEmpty(descriptionView.getTextView().getText())) {
          spinner.setVisibility(Actor.VISIBLE);
        }
        program.getAsyncDescription(this);
      } else {
        descriptionView.getTextView().setText(R.string.no_description_text);
        spinner.setVisibility(Actor.INVISIBLE);
        notifyDescriptionChanged();        
      }
    }

    private void notifyDescriptionChanged() {
      GdxHelper.removeRunnable(notifyDescriptionChangedTask);
      GdxHelper.runOnGdxThread(notifyDescriptionChangedTask);
    }

    @Override
    public boolean keyDown(CategoriesChannelsProgramsList list, int code, int repeatCount) {
      if (Keys.likeEnter(code)) {
        if (getItemCount() != 0) {
          final ProgramModel program = getItem(0);
          if (program != null) {
            ChannelModel channel = ChannelsCache.getInstance().getChannel(program.channelId);
            if (channel != null) {
              CurrentCategoryHelper.getInstance().onGoingToChangeCategory(channel.id);
              if (!channel.isSubscribed()) {
                GLBaseScreenFactory f = GLListener.getScreenFactory();
                glListener.startScreen(f.createChannelSubscriptionScreenIntent(channel, program));
              } else if (program.getType() == ProgramModel.TYPE_NOW && PvrChecker.isPvrEnabled(channel, program, PvrChecker.PvrType.SO)) {
				  if (!ChannelServicesCache.getInstance().isAllPvrSubscribed()) {
					  glListener.startScreen(GLListener.getScreenFactory().createChannelSubscriptionScreenIntent(channel, program, true, false, new SubscriptionListener() {
						  @Override
						  public void onSubscribed() {
							  glListener.getWatchingController().playProgram(program, true);
						  }
					  }));
				  } else {
					  glListener.getWatchingController().playProgram(program, true);
				  }
              } else {
                GLBaseScreenFactory f = GLListener.getScreenFactory();
                glListener.startScreen(f.createProgramScreenIntent(program));
              }
            }
          }
          return true;          
        }
      }
      return false;
    }
  }

  public static final class EmptyListAdapter extends ProgramChannelListAdapter<String> {
    public EmptyListAdapter() {
      super(Arrays.asList(""), null);
    }

    @Override
    public Actor onActorCreate(int index, boolean loupe) {
      TextActor text = new TextActor(null);
      text.touchable = false;
      text.setDesiredSize(MATCH_PARENT, WRAP_CONTENT);
      text.setText(items.get(index));
      FrameGroup group = new FrameGroup(null);
      group.setDesiredSize(WRAP_CONTENT, loupe ? LOUPE_ITEM_HEIGHT : ITEM_HEIGHT);
      group.setLayoutWithGravity(true);
      group.addActor(text);
      return group;
    }
  }
}
