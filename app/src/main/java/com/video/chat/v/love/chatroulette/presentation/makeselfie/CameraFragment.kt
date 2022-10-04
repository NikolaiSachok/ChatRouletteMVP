package com.video.chat.v.love.chatroulette.presentation.makeselfie

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.databinding.CameraFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.interfaces.Next
import com.video.chat.v.love.chatroulette.utils.onClick
import dagger.hilt.android.AndroidEntryPoint
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import java.io.ByteArrayOutputStream
import java.io.File


@AndroidEntryPoint
class CameraFragment : BaseFragment<CameraFragmentBinding>(), Next {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CameraFragmentBinding.inflate(inflater)

        firebaseAnalytics =
            FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_selfie_screen", "on selfie screen")
        firebaseAnalytics.logEvent("on_selfie_screen", params)

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.capture.visibility = View.VISIBLE

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
        WindowInsetsCompat.Type.statusBars()

        val windowInsetsController =
            ViewCompat.getWindowInsetsController(requireActivity().window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemBars()
        hideSystemUI()

        if (permissionsGranted) {
            binding.cameraView.visibility = View.VISIBLE
        } else {
//            permissionsDelegate.requestCameraPermission()
        }
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = binding.cameraView,
            focusView = binding.focusView,
            logger = logcat(),
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration,
            cameraErrorCallback = { Log.e("LOGGING_TAG", "Camera error: ", it) }
        )

        binding.capture onClick takePicture()
        changeCamera()

        binding.update.setOnClickListener {
            binding.capture.visibility = View.VISIBLE
            binding.update.visibility = View.GONE
            binding.next.visibility = View.GONE
            binding.cardView2.visibility = View.GONE
            binding.cameraView.foreground = resources.getDrawable(android.R.color.transparent)
        }

        binding.next.setOnClickListener {
            navigateToDirections(
                CameraFragmentDirections.actionCameraFragmentToTapToStartFragment(),
            )
        }

    }

    private lateinit var fotoapparat: Fotoapparat


    private var permissionsGranted: Boolean = true
    private var activeCamera: Camera = Camera.Front


    private fun takePicture(): () -> Unit = {
        val params = Bundle()
        params.putString("selfie_make_photo_btn_click", "selfie make photo btn click")
        firebaseAnalytics.logEvent("selfie_make_photo_btn_click", params)

        val photoResult = fotoapparat
            .autoFocus()
            .takePicture()

        photoResult
            .saveToFile(File(
                requireActivity().getExternalFilesDir("photos"),
                "photo.jpg"
            ))

        photoResult
            .toBitmap(scaled(scaleFactor = 0.25f))
            .whenAvailable { photo ->
                photo
                    ?.let {
                        Log.i("LOGGING_TAG",
                            "New photo captured. Bitmap length: ${it.bitmap.byteCount}")
                        sharedPreferences.edit()
                            .putString(Constants.CURRENT_LANGUAGE, encode(it.bitmap)).apply()
                        val rotatedBitmap = it.bitmap.rotate(-90f)
                        val cx = rotatedBitmap.width / 2f
                        val cy = rotatedBitmap.height / 2f
                        val flippedBitmap = rotatedBitmap.flip(-1f, 1f, cx, cy)
                        binding.result.setImageBitmap(flippedBitmap)
                        binding.cardView2.visibility = View.VISIBLE
                        binding.cameraView.foreground =
                            resources.getDrawable(R.color.transparent_black)
                        binding.update.visibility = View.VISIBLE
                        binding.next.visibility = View.VISIBLE
                        sharedPreferences.edit()
                            .putString(Constants.MY_IMAGE, encode(flippedBitmap)).apply()
                        binding.capture.visibility = View.GONE
                    }
                    ?: Log.e("LOGGING_TAG", "Couldn't capture photo.")
            }
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun Bitmap.flip(x: Float, y: Float, cx: Float, cy: Float): Bitmap {
        val matrix = Matrix().apply { postScale(x, y, cx, cy) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }


    fun encode(imageUri: Bitmap): String {
        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        imageUri.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return imageString
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

    private fun toggleFlash(): (CompoundButton, Boolean) -> Unit = { _, isChecked ->
        fotoapparat.updateConfiguration(
            UpdateConfiguration(
                flashMode = if (isChecked) {
                    firstAvailable(
                        torch(),
                        off()
                    )
                } else {
                    off()
                }
            )
        )
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted) {
            fotoapparat.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (permissionsGranted) {
            fotoapparat.stop()
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
    }


    sealed class Camera(
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

    override fun onNextClicked() {
        navigateToDirections(
            CameraFragmentDirections.actionCameraFragmentToTapToStartFragment(),
        )
    }

    override fun onBackSkipClicked() {
        binding.capture.visibility = View.VISIBLE

    }
}