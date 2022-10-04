package com.video.chat.v.love.chatroulette.presentation.makeselfie

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.constants.Constants.IMAGE
import com.video.chat.v.love.chatroulette.databinding.SetUpPermissionsDialogBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseDialog
import com.video.chat.v.love.chatroulette.presentation.interfaces.OpenCamera
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetUpPermissionsDialog(var openCameras: OpenCamera) :
    BaseDialog<SetUpPermissionsDialogBinding>() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    private lateinit var sharedPreferences: SharedPreferences
    private val requestIdMultiplePermissions = 1
    private val permissionsRequest: ArrayList<String> =
        arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomAlertDialog)

        firebaseAnalytics =
            FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_setup_permission_screen", "on setup permission screen")
        firebaseAnalytics.logEvent("on_setup_permission_screen", params)
    }

    private var storageIsChecked = false
    private var cameraIsChecked = false
    private var recordAudioIsChecked = false
    private var isFirstInit = true

    val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
            if (it.key == "android.permission.READ_EXTERNAL_STORAGE") {
                if (it.value == true) {
                    sharedPreferences.edit()
                        .putBoolean(Constants.STORAGE_PERMISSION_ISCHECKED, true)
                        .apply()
                    storageIsChecked = true
                    val params = Bundle()
                    params.putString("storage_permission", "storage permission is " + true)
                    firebaseAnalytics.logEvent("storage_permission", params)
                    binding.storage.setImageResource(R.drawable.ic_granted_perm_checkbox)
                } else {
                    val params = Bundle()
                    params.putString("storage_permission", "storage permission is " + false)
                    firebaseAnalytics.logEvent("storage_permission", params)
                    storageIsChecked = false
                    binding.storage.setImageResource(R.drawable.ic_red_checkbox)
                }
            }
            if (it.key == "android.permission.CAMERA") {
                if (it.value == true) {
                    sharedPreferences.edit().putBoolean(Constants.CAMERA_PERMISSION_ISCHECKED, true)
                        .apply()
                    cameraIsChecked = true
                    val params = Bundle()
                    params.putString("camera_permission", "storage permission is " + true)
                    firebaseAnalytics.logEvent("camera_permission", params)
                    binding.camera.setImageResource(R.drawable.ic_granted_perm_checkbox)
                } else {
                    val params = Bundle()
                    params.putString("camera_permission", "storage permission is " + false)
                    firebaseAnalytics.logEvent("camera_permission", params)
                    cameraIsChecked = false
                    binding.camera.setImageResource(R.drawable.ic_red_checkbox)
                }
            }
            if (it.key == "android.permission.RECORD_AUDIO") {
                if (it.value == true) {

                    sharedPreferences.edit().putBoolean(Constants.AUDIO_PERMISSION_ISCHECKED, true)
                        .apply()

                    recordAudioIsChecked = true
                    val params = Bundle()
                    params.putString("micro_permission", "storage permission is " + true)
                    firebaseAnalytics.logEvent("micro_permission", params)
                    binding.micro.setImageResource(R.drawable.ic_granted_perm_checkbox)
                } else {
                    val params = Bundle()
                    params.putString("micro_permission", "storage permission is " + false)
                    firebaseAnalytics.logEvent("micro_permission", params)
                    recordAudioIsChecked = false
                    binding.micro.setImageResource(R.drawable.ic_red_checkbox)
                }
            }

            if (storageIsChecked && cameraIsChecked && recordAudioIsChecked) {
                openCamera.onOpenCameraFragment()
                dismiss()
            }
        }


    }


    private lateinit var openCamera: OpenCamera
    private val readStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            storageIsChecked = true
            binding.storage.setImageResource(R.drawable.ic_granted_perm_checkbox)

//            initPermisionsGranted()
        } else {

            binding.storage.setImageResource(R.drawable.ic_red_checkbox)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            binding.camera.setImageResource(R.drawable.ic_granted_perm_checkbox)

        } else {
            binding.camera.setImageResource(R.drawable.ic_red_checkbox)

        }
    }

    private var microPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            binding.micro.setImageResource(R.drawable.ic_granted_perm_checkbox)

        } else {
            binding.micro.setImageResource(R.drawable.ic_red_checkbox)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SetUpPermissionsDialogBinding.inflate(inflater)
        openCamera = openCameras;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        if (!isFirstInit()) {
            sharedPreferences.edit().putBoolean(Constants.IS_FIRST_INIT, true)
                .apply()
        } else {
//            initPermisionsGranted()
        }
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences.edit().putBoolean(Constants.IS_FIRST_INIT, true)
            .apply()
        with(binding) {

            if (getStoragePerm()) {
                storage.setImageResource(R.drawable.ic_granted_perm_checkbox)
            } else {
                storage.setImageResource(R.drawable.ic_unchecked_permissions)
            }
            if (getCameraPerm()) {
                camera.setImageResource(R.drawable.ic_granted_perm_checkbox)
            } else {
                camera.setImageResource(R.drawable.ic_unchecked_permissions)
            }

            if (getAudioPerm()) {
                micro.setImageResource(R.drawable.ic_granted_perm_checkbox)
            } else {
                micro.setImageResource(R.drawable.ic_unchecked_permissions)
            }
        }


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.edit().putBoolean(Constants.IS_FIRST_INIT, false)
            .apply()
    }

    private fun isFirstInit() = sharedPreferences.getBoolean(Constants.IS_FIRST_INIT,
        false)

    private fun getStoragePerm() =
        sharedPreferences.getBoolean(Constants.STORAGE_PERMISSION_ISCHECKED,
            false)

    private fun getAudioPerm() =
        sharedPreferences.getBoolean(Constants.AUDIO_PERMISSION_ISCHECKED,
            false)

    private fun getCameraPerm() =
        sharedPreferences.getBoolean(Constants.CAMERA_PERMISSION_ISCHECKED,
            false)

    private fun setupUi() {
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        with(binding) {
            sendPermission.setOnClickListener {
                sharedPreferences.edit().putBoolean(Constants.IS_FIRST_INIT, false)
                    .apply()

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            }
        }
    }

    private fun checkMultipleRequestPermissions(): Boolean {
        isFirstInit = false;
        val listPermissionsNeeded: MutableList<String> = ArrayList()

        for (p in permissionsRequest) {
            val result = ContextCompat.checkSelfPermission(requireContext(), p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listPermissionsNeeded.toTypedArray(),
                requestIdMultiplePermissions
            )
            return false

        }


        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestIdMultiplePermissions) {
            if (grantResults.isNotEmpty()) {
                var isGrant = true
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        isGrant = false

                    }
                }
                if (isGrant) {

                } else {

                    var someDenied = false
                    for (permission in permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                permission
                            )
                        ) {
                            if (ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    permission
                                ) == PackageManager.PERMISSION_DENIED
                            ) {
                                someDenied = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestStoragePermission() {
        readStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun requestMicroPermission() {
        microPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun isReadStoragePermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.CAMERA)
    }

    private fun isMicroPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.RECORD_AUDIO)
    }


    private fun shouldShowPermissionDialog(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).not()
    }

    private fun shouldShowCameraPermissionDialog(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.CAMERA
        ).not()
    }

    private val pickImages = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) {
//            contentUriList -> handleResultAddImage(contentUriList)
    }


    private fun initPermisionsGranted() {
        with(binding) {
            if (isCameraPermissionGranted()) {
                camera.setImageResource(R.drawable.ic_granted_perm_checkbox)
            } else {
                camera.setImageResource(R.drawable.ic_red_checkbox)
            }
            if (isMicroPermissionGranted()) {
                micro.setImageResource(R.drawable.ic_granted_perm_checkbox)
            } else {
                micro.setImageResource(R.drawable.ic_red_checkbox)
            }
            if (isReadStoragePermissionGranted()) {
                storage.setImageResource(R.drawable.ic_granted_perm_checkbox)
            } else {
                storage.setImageResource(R.drawable.ic_red_checkbox)
            }
        }
    }

    private fun checkCameraPermissionAndLaunchGallery(
        action: () -> Unit,
    ) {
        cameraIsChecked = isCameraPermissionGranted()
        if (cameraIsChecked) {
            action()
        } else {
            requestCameraPermission()

        }
    }

    private fun checkMicroPermissionAndLaunchGallery(
        action: () -> Unit,
    ) {
        recordAudioIsChecked = isMicroPermissionGranted()
        if (recordAudioIsChecked) {
            action()
        } else {
            requestMicroPermission()

        }
    }

    private fun checkStoragePermissionAndLaunchGallery(
        action: () -> Unit,
    ) {
        storageIsChecked = isReadStoragePermissionGranted()
        if (storageIsChecked) {
            action()

        } else {
            requestStoragePermission()
        }
    }


    private fun pickImage() {
        checkStoragePermissionAndLaunchGallery {
            pickImages.launch(arrayOf(IMAGE))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDestroy()
    }

    companion object {

        fun newInstance(openCameras: OpenCamera): SetUpPermissionsDialog {
            return SetUpPermissionsDialog(openCameras)
        }

        fun show(fragmentManager: FragmentManager, openCameras: OpenCamera) {
            newInstance(openCameras).apply {
                show(fragmentManager, tag)
            }
        }
    }
}