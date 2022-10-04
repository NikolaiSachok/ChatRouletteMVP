package com.video.chat.v.love.chatroulette.presentation.match

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.databinding.MatchFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.graphics.BitmapFactory
import com.bumptech.glide.request.target.Target

import android.graphics.Bitmap
import java.net.URL
import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable
import android.os.Handler
import com.squareup.picasso.Picasso
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import android.os.Looper
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.commit
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.video.chat.v.love.chatroulette.App
import com.video.chat.v.love.chatroulette.presentation.search.SearchFragment
import com.video.chat.v.love.chatroulette.presentation.start.TapToStartFragment


@AndroidEntryPoint
class MatchFragment : BaseFragment<MatchFragmentBinding>() {


    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity
    private val viewModel: SearchViewModel by activityViewModels()

    //    private var player: ExoPlayer? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var timer: CountDownTimer
    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MatchFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        try {
//                    val url = URL(it.toString())
//                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())

//    requireActivity().run {
//        val imageBitmap = Picasso.get().load(getUrl().toString()).get()
//        val d: Drawable = BitmapDrawable(resources, imageBitmap)
//        binding.root.background = d
//    }


        } catch (e: IOException) {
            println(e)
        }

        initUI()
        initClickListeners()
    }

    private fun navShop() {
        findNavController().navigate(MatchFragmentDirections.actionTapToStartFragmentToShopFragment())
    }

    private fun setLocalCounter(count: Int) = sharedPreferences.edit().putInt(Constants.COUNTER,
        count).apply()

    private fun getLocalCounter() = sharedPreferences.getInt(Constants.COUNTER,
        0)

    private fun initClickListeners() {

        binding.swipeId.setOnClickListener {
            val params = Bundle()
            params.putString("match_screen_click_on_swipe", "match screen click on swipe")
            firebaseAnalytics.logEvent("match_screen_click_on_swipe", params)
            navShop()
        }

        binding.icClose.setOnClickListener {
            val params = Bundle()
            params.putString("match_screen_click_on_close", "match screen click on close")
            firebaseAnalytics.logEvent("match_screen_click_on_close", params)

            if (getLocalSwipes() == 0) {
                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, -2).apply()

            } else {
                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, getLocalSwipes() - 1)
                    .apply()

                setLocalCounter(getLocalCounter() + 1)

            }
//            findNavController().popBackStack(R.id.tapToStartFragment, false)
//            childFragmentManager.commit(allowStateLoss = true) {
//                replace(R.id.child_main_fragment, TapToStartFragment())
            try {
//                childFragmentManager.commit(allowStateLoss = true) {
//                    replace(R.id.child_main_fragment, TapToStartFragment.newInstance("sa"))
//                }
                viewModel.setImageUrl(false)

                findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToTapToStartFragment())
                timer.cancel()
                _binding = null
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

//            }
        }
        binding.skip.setOnClickListener {
            val params = Bundle()
            params.putString("match_screen_click_on_skip", "match screen click on skip")
            firebaseAnalytics.logEvent("match_screen_click_on_skip", params)
            if (getLocalSwipes() == 0) {
                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, -2).apply()

            } else {
                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, (getLocalSwipes() - 1))
                    .apply()

                setLocalCounter(getLocalCounter() + 1)

            }
//            findNavController().popBackStack()
//            findNavController().popBackStack(R.id.searchFragment, false)

            childFragmentManager.commit(allowStateLoss = true) {
                replace(R.id.child_main_fragment, SearchFragment())
            }
            viewModel.setImageUrl(false)

            timer.cancel()
            _binding = null
        }
        binding.next.setOnClickListener {
            val params = Bundle()
            params.putString("match_screen_click_on_next", "match screen click on next")
            firebaseAnalytics.logEvent("match_screen_click_on_next", params)
            if (getLocalSwipes() == 0) {
                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, -2).apply()

            } else {
                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, getLocalSwipes() - 1)
                    .apply()
                setLocalCounter(getLocalCounter() + 1)

            }
            try {
                timer.cancel()
            }catch (e: Exception){
                e.printStackTrace()
            }
            navTo()
        }
    }

    private fun navTo() {
        findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToCommunicationFragment())
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(requireActivity().window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        WindowInsetsCompat.Type.statusBars()
    }

    fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    private fun initUI() {
        binding.childMainFragment.visibility = View.VISIBLE

//            val uiHandler = Handler(Looper.getMainLooper())
//            uiHandler.post(Runnable {
////                Glide.with(requireContext())
////                    .load(it)
////                    .into(binding.videoPlayer)
//                    val url = URL(it.toString())
//                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//
//                val d: Drawable = BitmapDrawable(resources, Picasso.get().load(getUrl().toString()).get())
//                binding.root.background = d
//            })
////            url = it.toString()
////                showUi(it.toString())
////            val bitmap = BitmapFactory.decodeStream(URL(it).getContent() as InputStream)
//
//        }

        hideSystemBars()
        hideSystemUI()





        try {
            showUi(getUrl().toString())
            sharedPreferences.edit()
                .putInt(Constants.UNICAL_USER_CODE, getLocalSwipes()).apply()

            binding.progressAction.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }


        firebaseAnalytics =
            FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_match_screen", "on match screen")
        firebaseAnalytics.logEvent("on_match_screen", params)

//        val timer = object : CountDownTimer(600, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//            }
//
//            override fun onFinish() {
                try {
//                    binding.constraintLayout.visibility = View.VISIBLE
//                    binding.skip.visibility = View.VISIBLE
//                    binding.next.visibility = View.VISIBLE
//                    binding.icClose.visibility = View.VISIBLE

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            //}
//        }

//        timer.start()

    }


    private fun startSecondsTimer() {
        try {
            timer = object : CountDownTimer(getTimeToSkip() + 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    try {

                        if ((millisUntilFinished / 1000).toInt() == 0) {
                            timer.onFinish()
                        } else {
                            if (binding != null){
                                binding.skip.text = "Skip (" + millisUntilFinished / 1000 + ")"
                            }
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFinish() {
                    try {
                        if (getLocalSwipes() == 0) {
                            sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, -2).apply()

                        } else {
                            if (getLocalSwipes() == 0) {
                                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, -2).apply()

                            } else {
                                sharedPreferences.edit().putInt(Constants.CURRENT_SWIPES, (getLocalSwipes() - 1))
                                    .apply()

                                setLocalCounter(getLocalCounter() + 1)

                            }

                        }
//                        binding.childMainFragment.visibility = View.GONE
//            findNavController().popBackStack(R.id.searchFragment, false)
                        childFragmentManager.commit(allowStateLoss = true) {
                            replace(R.id.child_main_fragment, SearchFragment())
                        }

                        viewModel.setImageUrl(false)
                        timer.cancel()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            timer.start()

        } catch (ex: Exception) {
            timer.cancel()
            findNavController().popBackStack()
            ex.toString()

        }
    }


    private fun showUi(s: String) {
        Glide.with(App.applicationContext())
            .load(s)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    try {
                        binding.conteinerWhenReady?.visibility = View.VISIBLE
                        binding.constraintLayout.visibility = View.VISIBLE
                        binding.skip.visibility = View.VISIBLE
                        binding.next.visibility = View.VISIBLE
                        binding.icClose.visibility = View.VISIBLE
                        binding.swipeId.text = getFormattedSwipes()
                        startSecondsTimer()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }


                    return false
                }
            }).into(binding.videoPlayer)


        if (s.isNotEmpty()){
            viewModel.setImageUrl(true)
        }

//        if (binding.videoPlayer)

    }

    private fun getFormattedSwipes(): CharSequence? {
        return if (getLocalSwipes() <= 1) {
            " Swipe " + getLocalSwipes()
        } else {
            " Swipes " + getLocalSwipes()
        }
    }

    private fun getCurrentVideo() = sharedPreferences.getInt(Constants.UNICAL_USER_CODE,
        Constants.UNICAL_USER)

//    private fun getVideosFemale(): ArrayList<Videos>? {
//        return args.videosList?.videosFemale
//    }
//
//    private fun getVideosMle(): ArrayList<Videos>? {
//        return args.videosList?.videosFemale
//    }

    private fun getLocalSwipes() = sharedPreferences.getInt(Constants.CURRENT_SWIPES,
        0)

    private fun getTimeToSkip() = sharedPreferences.getLong("TIME_TO_SKIP",
        10000)

    private fun getUrl() = sharedPreferences.getString("LINK_IMAGE",
        "")


//    private fun getVideosData(): UserVideosData? {
//        return args.videosList
//    }

//    private fun playVideo(url: String) {
//        player?.run {
//            addMediaItem(getMediaItem(url))
//            prepare()
////            play()
//        }
//    }

    //    fun getPlayer(context: Context): ExoPlayer {
//        return ExoPlayer.Builder(context)
//            .setMediaSourceFactory(getMediaSource(context))
//            .build()
//    }
//
//    private fun getMediaItem(url: String): MediaItem {
//        return MediaItem.fromUri(url)
//    }
//
//    private fun getSourceFactory(context: Context): DataSource.Factory {
//        return DefaultDataSource.Factory(context)
//    }
//
//    private fun getMediaSource(context: Context): MediaSource.Factory {
//        return ProgressiveMediaSource.Factory(getSourceFactory(context))
//    }
    private fun getEntityType() = requireArguments().getString("ENTITY_ID")

    companion object {

        fun newInstance(videos: String): MatchFragment {
            return MatchFragment().apply {
                arguments = bundleOf(
                    "ENTITY_ID" to videos,

                    )
            }
        }
    }
}