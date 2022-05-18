package ltd.abtech.androidtv.tv

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.leanback.widget.VerticalGridView.WINDOW_ALIGN_NO_EDGE
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ltd.abtech.android_common_ui.setGone
import ltd.abtech.android_common_ui.setVisible
import ltd.abtech.android_common_ui.startTimeFormat
import ltd.abtech.androidtv.R
import ltd.abtech.androidtv.databinding.FragmentTvProgramGuideBinding
import ltd.abtech.androidtv.databinding.ItemTvProgramGuideBinding
import ltd.abtech.androidtv.framework.BaseFragment
import ltd.abtech.androidtv.framework.BaseViewModel
import ltd.abtech.androidtv.isAuthorized
import ltd.abtech.androidtv.util.*
import ltd.abtech.androidtv.widget.Action
import ltd.abtech.shared.logic.channels.GetChannelsUseCase
import ltd.abtech.shared.logic.entities.Channel
import ltd.abtech.shared.logic.entities.ChannelCategory
import ltd.abtech.shared.logic.entities.programs.Epg
import ltd.abtech.shared.logic.entities.programs.Program
import ltd.abtech.shared.logic.programs.ObserveEpgUseCase
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

data class TvProgramGuideFragmentState(
    val epg: Async<Epg> = Uninitialized,
    val favoriteChannel: Async<Channel> = Uninitialized
) : MvRxState

class TvProgramGuideFragmentViewModel(initialStateGuide: TvProgramGuideFragmentState,
                                      private val channelsUseCase: GetChannelsUseCase,
                                      private val observePrograms: ObserveEpgUseCase) :
    BaseViewModel<TvProgramGuideFragmentState>(initialStateGuide) {
    var observeProgramsJob : Job? = null

    fun loadPrograms(channel: Channel) = viewModelScope.launch {
        observeProgramsJob?.cancel()
        observeProgramsJob = observePrograms.observeEpgForChannel(channel.id, ObserveEpgUseCase.EpgRequestType.Long)
            .executeFlowResponse(viewModelScope){
            copy(epg = it)
        }
    }

    fun cancelObservePrograms() {
        observeProgramsJob?.cancel()
    }

    fun setChannelFavorite(channel: Channel) = viewModelScope.launch {
        try {
            if (!channel.isFavorite) {
                channelsUseCase.setChannelFavorite(channel.id, true)
                setState { copy(favoriteChannel = Success(channel.copy(isFavorite = true))) }
            }
        } catch (ignore: CancellationException) {
            // setState() throws java.lang.ArrayIndexOutOfBoundsException when cancel
        } catch (e: Throwable) {
            setState { copy(favoriteChannel = Fail(e)) }
        }
    }

    companion object : MvRxViewModelFactory<TvProgramGuideFragmentViewModel, TvProgramGuideFragmentState> {
        override fun create(viewModelContext: ViewModelContext, stateGuide: TvProgramGuideFragmentState): TvProgramGuideFragmentViewModel {
            val channelsUseCase: GetChannelsUseCase by viewModelContext.activity.inject()
            val observePrograms: ObserveEpgUseCase by viewModelContext.activity.inject()
            return TvProgramGuideFragmentViewModel(
                stateGuide,
                channelsUseCase,
                observePrograms
            )
        }
    }
}

class TvProgramGuideFragment : BaseFragment(R.layout.fragment_tv_program_guide) {
    private companion object {
        const val TODAY_TIME_INDEX = 3
        const val MAX_PROGRESS = 1000
        const val SCALE_INDEX = 1.1f
        const val NO_SCALE = 1.0f
        const val DURATION = 200L
    }

    private val viewModel: TvProgramGuideFragmentViewModel by fragmentViewModel()
    private val binding by viewLifecycleLazy {
        FragmentTvProgramGuideBinding.bind(requireView())
    }
    private var viewWasCreated = false
    private var favoriteSet = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemTvProgramGuideBinding.bind(itemView)
    }

    private var programAdapter = object : ListAdapter<Program, ViewHolder>
        (object : DiffUtil.ItemCallback<Program>() {
        override fun areItemsTheSame(oldItem: Program, newItem: Program) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Program, newItem: Program) = areItemsTheSame(oldItem, newItem)
    }) {
        val channel by lazy{ getTvMenuState().channel }
        val category by lazy{ getTvMenuState().category }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.item_tv_program_guide, parent, false).apply {
                        //Android bug - xml attribute not recognizable
                        clipToOutline = true
                        nextFocusUpId = R.id.program_time_list
                        nextFocusDownId = R.id.program_info_btn
                    }
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder.binding){
                val program =  getItem(position)
                program.let {
                    name.text = it.name
                    startTime.text = it.startTimeFormat()
                    Glide.with(image).load(it.image.getUrl()).into(image)
                    with(nowProgress) {
                        max = MAX_PROGRESS
                        progress = (it.getCurrentPositionRelative() * MAX_PROGRESS).toInt()
                        visibility = if(it.getType() == Program.Type.Now) View.VISIBLE else View.GONE
                    }
                }
                holder.itemView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    play.visibility = if(hasFocus && program.isPlayable()) View.VISIBLE else View.GONE
                    val scale = if(hasFocus) SCALE_INDEX else NO_SCALE
                    v.post{ v.animate().scaleX(scale).scaleY(scale).setDuration(DURATION).start() }
                }
                holder.itemView.setOnClickListener {
                    channel?.let{
                        if(program.isPlayable()){
                            playTv(program , it, category , true)
                        }
                    }
                }
            }
        }

        public override fun getItem(position: Int): Program = super.getItem(position)
    }

    private var timelineProgramAdapter = object : ListAdapter<String, RecyclerView.ViewHolder>
        (object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = areItemsTheSame(oldItem, newItem)
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : RecyclerView.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_tv_program_guide_timeline, parent, false).apply {
                    nextFocusDownId = R.id.program_list
                }) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with(getItem(position)) {
                (holder.itemView as TextView).text = this
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val (channel, program, category) = getTvMenuState()
        with(binding){
            mainGroup.setVisible(false)

            viewWasCreated = true
            channel?.let { viewModel.loadPrograms(channel) }

            channelName.text = channel?.name ?: "undefined"

            with(programList) {
                adapter = programAdapter
                addOnChildViewHolderSelectedListener(object : OnChildViewHolderSelectedListener() {
                    override fun onChildViewHolderSelected(parent: RecyclerView?, child: RecyclerView.ViewHolder?,
                                                           position: Int, subposition: Int) {
                        with(programInfoBtn) {
                            animate().alpha(0f).setDuration(200)
                                .setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        animate().alpha(1f).setDuration(200).start()
                                    }
                                }).start()
                        }
                    }
                })
                windowAlignment = WINDOW_ALIGN_NO_EDGE
            }

            with(programTimeList) {
                adapter = timelineProgramAdapter
                addOnChildViewHolderSelectedListener(object : OnChildViewHolderSelectedListener() {
                    var activated: View? = null
                    override fun onChildViewHolderSelected(parent: RecyclerView?, child: RecyclerView.ViewHolder?,
                                                           position: Int, subposition: Int) {
                        activated?.isActivated = false
                        activated = child?.itemView
                        activated?.isActivated = true
                        programList.scrollToPosition(getDayProgramIndex(position - TODAY_TIME_INDEX))
                    }
                })
                windowAlignment = WINDOW_ALIGN_NO_EDGE
            }

            val dateFormater = SimpleDateFormat("d.MM EEE")
            timelineProgramAdapter.submitList(listOf(
                getDayRelativeToday(dateFormater, -3),
                getDayRelativeToday(dateFormater, -2),
                getString(R.string.yesterday), getString(R.string.today), getString(
                    R.string.tomorrow
                ),
                getDayRelativeToday(dateFormater, 2),
                getDayRelativeToday(dateFormater, 3)
            ))

            programInfoBtn.setOnClickListener {
                navigateTvMenu(TvProgramInfoFragment(), channel, programAdapter.getItem(programList.selectedPosition), category)
                viewModel.cancelObservePrograms()
            }

            loadError.refresh.setOnClickListener {
                channel?.let { viewModel.loadPrograms(channel) }
            }
        }
        setActions(channel, category)
    }

    private fun setActions(channel: Channel?, category: ChannelCategory?) {
        binding.actionBar.setActions(
            listOf(
                Action(R.string.channel_info, R.drawable.ic_channel_info, {
                    navigateTvMenu(
                        TvChannelInfoFragment(),
                        channel,
                        programAdapter.getItem(binding.programList.selectedPosition),
                        category
                    )
                    viewModel.cancelObservePrograms()
                }),
                Action(R.string.add_favorite, if(channel?.isFavorite ?: false) R.drawable.ic_star else R.drawable.ic_star_stroke, {
                    if (isAuthorized()) {
                        channel?.let { viewModel.setChannelFavorite(channel) }
                    } else {
                        setFragmentResultListener(TvLoginFragment.LOGIN_REQUEST_KEY) { key, bundle ->
                            val result = bundle.getBoolean(TvLoginFragment.LOGIN_RESULT_KEY)
                            if (result) {
                                channel?.let { viewModel.setChannelFavorite(channel) }
                            }
                        }
                        mainActivity().tvPlaybackModel.play(null, channel, category, false)
                        mainActivity().tvPlaybackModel.hideMenu(false)
                        navigate(TvLoginFragment())
                    }
                })
            )
        )
    }

    override fun invalidate() {
        withState(viewModel) {
            with(binding) {

                if (it.epg is Success) {
                    val programs = it.epg()!!.programs

                    if (!programs.isEmpty()) {
                        programAdapter.submitList(programs)

                        if (viewWasCreated) {
                            mainGroup.setVisible(true)
                            programTimeList.smoothScrollToPosition(TODAY_TIME_INDEX)
                            programList.scrollToPosition(getDayProgramIndex(0))
                            programList.post{ programList.requestFocus() }
                        } else {
                        }
                    } else {    //error
                        mainGroup.setVisible(false)
                        loadError.setVisible(true)
                        loadError.refresh.post{loadError.refresh.requestFocus()}
                    }
                    skeletonGroup.setGone(true)
                    viewWasCreated = false
//                    return@withState
                }

                if (!favoriteSet && it.favoriteChannel is Success) {
                    val (channel, program, category) = getTvMenuState()
                    val favoriteChannel = it.favoriteChannel()
                    val tvMenuModel = mainActivity().tvMenuModel
                    tvMenuModel.update(TvMenuState(favoriteChannel, program, category))
                    setActions(favoriteChannel, category)
                    favoriteSet = true
                }
            }
        }
    }

    private fun getDayProgramIndex(dayOffset: Int) : Int {
        val c = Calendar.getInstance()
        c[Calendar.DATE] += dayOffset
        val time = c.timeInMillis
        val count = programAdapter.itemCount

        for(i in 0 until count) {
            if (programAdapter.getItem(i).startTime <= time && programAdapter.getItem(i).endTime > time)
                return i
        }
        return if (dayOffset < 0 ) 0 else count - 1
    }
}
