package com.video.chat.v.love.chatroulette.presentation.makeselfie

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.video.chat.v.love.chatroulette.databinding.MakeselfieFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.interfaces.OpenCamera
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics


@AndroidEntryPoint
class MakeSelfieFragment : BaseFragment<MakeselfieFragmentBinding>() , OpenCamera {
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MakeselfieFragmentBinding.inflate(inflater)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideSystemBars()
        hideSystemUI()

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_selfie_requirement_screen", "on selfie requirement screen")
        firebaseAnalytics.logEvent("on_selfie_requirement_screen", params)

        val onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed(){
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            onBackPressedCallback
        )

        binding.makeASelfie.setOnClickListener {
            if (!checkPermissions()){
                val params = Bundle()
                params.putString("on_selfie_requirement_click_make_selfie", "on selfie requirement click make selfie")
                firebaseAnalytics.logEvent("on_selfie_requirement_click_make_selfie", params)
                SetUpPermissionsDialog.show(childFragmentManager, this)
            }else{
                navTo()
            }
        }
    }

    private fun  checkPermissions() : Boolean = (isReadStoragePermissionGranted() && isCameraPermissionGranted() && isMicroPermissionGranted())


    private fun isReadStoragePermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.CAMERA)
    }

    private fun isMicroPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.RECORD_AUDIO)
    }


    override fun onOpenCameraFragment() {
        navTo()
    }

    private fun navTo(){
        findNavController().navigate(MakeSelfieFragmentDirections.actionSelfieFragmentToCameraFragment())
    }
}