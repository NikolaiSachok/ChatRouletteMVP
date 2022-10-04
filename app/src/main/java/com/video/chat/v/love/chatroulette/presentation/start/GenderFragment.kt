package com.video.chat.v.love.chatroulette.presentation.start

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.databinding.GenderFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.selector.*

@AndroidEntryPoint
class GenderFragment : BaseFragment<GenderFragmentBinding>() {
    private lateinit var fotoapparat: Fotoapparat
    private var permissionsGranted: Boolean = true
    private var genderIsClicked: Boolean = false
    private var activeCamera: Camera = Camera.Front

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = GenderFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraView.visibility = View.VISIBLE
        binding.rootClick.setOnClickListener {
        }
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

        binding.focusView.setOnClickListener {
            navTo()
        }

        binding.male.setOnClickListener {
            navShop()
        }

        binding.female.setOnClickListener {
            navShop()
        }
        binding.allGender.setOnClickListener {
            if (genderIsClicked){
                genderIsClicked = false
                binding.female.visibility = View.VISIBLE
                binding.male.visibility = View.VISIBLE
                binding.allGender.setImageResource(R.drawable.ic_all)
            }else{
                genderIsClicked = true
                binding.female.visibility = View.GONE
                binding.male.visibility = View.GONE
                binding.allGender.setImageResource(R.drawable.ic_all_gender)
            }

        }

    }

    private fun navShop() {
        navigateToDirections(
            GenderFragmentDirections.actionTapToStartFragmentToShopFragment(),
        )
    }


    private fun navTo(){
        navigateToDirections(
            GenderFragmentDirections.actionTapToStartFragmentToSearchFragment(),
        )
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
        val timer = object : CountDownTimer(600, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                binding.tap.visibility = View.VISIBLE
                binding.allGender.visibility = View.VISIBLE
            }
        }
        timer.start()
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
        binding.tap.visibility = View.VISIBLE
        binding.allGender.visibility = View.VISIBLE

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