package com.video.chat.v.love.chatroulette.presentation.match

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.databinding.CommunicationFragmentBinding
import com.video.chat.v.love.chatroulette.network.data.videos.Videos
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.presentation.search.SearchFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.selector.*

@AndroidEntryPoint
class CommunicationFragment  : BaseFragment<CommunicationFragmentBinding>(){
    private var player: ExoPlayer? = null
    private var playerIsStarnted: Boolean = false
    private var onStopIsClicked: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CommunicationFragmentBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        hideSystemBars()
        hideSystemUI()
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_communication_screen", "on communication screen")
        firebaseAnalytics.logEvent("on_communication_screen", params)
        initUI()
        initClickListeners()
    }
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(requireActivity().window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun hideSystemBars() {
        WindowInsetsCompat.Type.statusBars()

        val windowInsetsController =
            ViewCompat.getWindowInsetsController(requireActivity().window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }


    private fun initClickListeners() {
        binding.endCall.setOnClickListener {
            val params = Bundle()
            params.putString("on_end_call_click", "on end call click")
            firebaseAnalytics.logEvent("on_end_call_click", params)

            player?.run {
                stop()
            }
//            findNavController().popBackStack(R.id.searchFragment, true)
            findNavController().navigate(CommunicationFragmentDirections.actionCommunicationFragmentToSearch())

        }

        binding.icClose.setOnClickListener {
            player?.run {
                stop()
            }
//            findNavController().popBackStack(R.id.searchFragment, true)
            findNavController().navigate(CommunicationFragmentDirections.actionCommunicationFragmentToMain())
        }
    }

    private fun initUI() {
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        player = getPlayer(binding.videoPlayer.context)
        binding.videoPlayer.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.videoPlayer.player = player
//        getCurrentVideo())?.videoUrl?.let { playVideo(it) }
        getVideoUrl()?.let { playVideo(it) }
        binding.cameraView.visibility = View.VISIBLE

        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = binding.cameraView,
            focusView = binding.focusView,
            logger = logcat(),
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration,
            cameraErrorCallback = { Log.e("LOGGING_TAG", "Camera error: ", it) }
        )
        changeCamera()


    }

    private fun getVideoUrl() = sharedPreferences.getString("LINK_VIDEO",
        "")

    private lateinit var fotoapparat: Fotoapparat
    private var permissionsGranted: Boolean = true
    private var activeCamera: Camera = Camera.Front

    private fun getCurrentVideo() = sharedPreferences.getInt(Constants.UNICAL_USER_CODE,
        Constants.UNICAL_USER)



    private fun playVideo(url: String) {
        playerIsStarnted = true
        player?.run {
            addListener(videoPlayerListener)
            addMediaItem(getMediaItem(url))
            prepare()
            play()
        }
    }

    private val videoPlayerListener = object : Player.Listener { // player listener

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) { // check player play back state
                Player.STATE_READY -> {
//                    aspectRatioFrameLayout.setAspectRatio(16f / 9f)
                }
                Player.STATE_ENDED -> {
                    player?.run {
                        stop()
                    }
                    try{
                        findNavController().popBackStack(R.id.searchFragment, false)
                    }catch (ex: Exception){
                        ex.printStackTrace()
                    }
                }
                Player.STATE_BUFFERING ->{

                    //your logic
                }
                Player.STATE_IDLE -> {
                    //your logic
                }
                else -> {
//                    playerView.hideController()
                }
            }
        }
    }

    fun getPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(getMediaSource(context))
            .build()
    }

    private fun getMediaItem(url: String): MediaItem {
        return MediaItem.fromUri(url)
    }

    private fun getSourceFactory(context: Context): DataSource.Factory {
        return DefaultDataSource.Factory(context)
    }

    private fun getMediaSource(context: Context): MediaSource.Factory {
        return ProgressiveMediaSource.Factory(getSourceFactory(context))
    }

    private fun changeCamera(): () -> Unit = {
        activeCamera = when (activeCamera) {
            Camera.Front -> Camera.Front
            Camera.Back -> Camera.Front
        }

        fotoapparat.switchTo(
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration
        )
        Log.i("LOGGING_TAG",
            "New camera position: ${if (activeCamera is Camera.Back) "back" else "front"}")
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
        }
        if (player != null){
            player?.run {
                volume = 1F
            }
        }

        if (player != null){
            if (player?.isPlaying == false && playerIsStarnted && onStopIsClicked){
                findNavController().popBackStack(R.id.searchFragment, false)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (permissionsGranted) {
            fotoapparat.stop()
        }
        if (player != null) {
            onStopIsClicked = true
            player?.run {
                volume = 0F
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsGranted = true
        fotoapparat.start()
        binding.cameraView.visibility = View.VISIBLE
//        binding.tap.visibility = View.VISIBLE
//        binding.allGender.visibility = View.VISIBLE

    }


    private sealed class Camera(
        val lensPosition: LensPositionSelector,
        val configuration: CameraConfiguration,
    ) {

        object Back : Camera(
            lensPosition = back(),
            configuration = CameraConfiguration(
                previewResolution = firstAvailable(
                    wideRatio(highestResolution()),
                    standardRatio(highestResolution())
                ),
                previewFpsRange = highestFps(),
                flashMode = off(),
                focusMode = firstAvailable(
                    continuousFocusPicture(),
                    fixed()
                ),
                frameProcessor = {
                    // Do something with the preview frame
                }
            )
        )

        object Front : Camera(
            lensPosition = front(),
            configuration = CameraConfiguration(
                previewResolution = firstAvailable(
                    wideRatio(highestResolution()),
                    standardRatio(highestResolution())
                ),
                previewFpsRange = highestFps(),
                flashMode = off(),
                focusMode = firstAvailable(
                    fixed(),
                    autoFocus()
                )
            )
        )
    }
}