package com.video.chat.v.love.chatroulette.presentation.start

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.*
import com.bumptech.glide.Glide
import com.google.common.collect.ImmutableList
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.databinding.TapToStartFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.MainActivity
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.interfaces.BackFromSwipeWarning
import com.video.chat.v.love.chatroulette.presentation.search.SwipeWarningDialog
import dagger.hilt.android.AndroidEntryPoint
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*


@AndroidEntryPoint
class TapToStartFragment : BaseFragment<TapToStartFragmentBinding>(), BackFromSwipeWarning {
    private lateinit var sharedPreferences: SharedPreferences
    var isUp: Boolean = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity
    private val viewModel: TapToStartViewModel by viewModels()
    private lateinit var purchase: Purchase
    private val subscribe_product = "swipes_vip_subscription"
    private lateinit var billingClient: BillingClient
    private lateinit var productSubscribeDetails: ProductDetails


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = TapToStartFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

//        hideSystemBars()
//        hideSystemUI()
//        try {
//            if (getSubscribeState()){
//                if (getCurrentFilter() == Constants.MALE){
////                    selectMale()
//                    binding.singleText.setText(R.string.male)
//                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_male_single, null))
//                }else if (getCurrentFilter() == Constants.FEMALE){
//                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_female_single, null))
//                    binding.singleText.setText(R.string.female)
//
////                    selectFeMale()
//                }else{
//                    binding.singleText.setText(R.string.all)
//                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders, null))
////                    selectAll()
//                }
//            }else{
//                binding.singleText.setText(R.string.all)
//                binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders, null))
//            }
//        }catch (e:Exception){
//            e.printStackTrace()
//        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        firebaseAnalytics =
            FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_tap_to_start_screen", "on tap to start screen")
        firebaseAnalytics.logEvent("on_tap_to_start_screen", params)
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        if (getSubscribeState()) {
            when {
                getCurrentFilter() == Constants.MALE -> {
                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_male_single,
                        null))
                    selectMale()


                }
                getCurrentFilter() == Constants.FEMALE -> {
                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_female_single,
                        null))
                    selectFeMale()

                }
                else -> {
                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders,
                        null))
                    selectAll()

                }
            }
        } else {
            binding.singleText.setText(R.string.all)
            binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders,
                null))
        }

    }

    private fun reloadPurchaseSubs() {

        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(
            queryPurchasesParams,
            purchasesSubsListener
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private val purchasesSubsListener =
        PurchasesResponseListener { billingResult, purchases ->
            if (!purchases.isEmpty()) {
                purchase = purchases.first()
                changeImaheBtnWithSubs()
                sharedPreferences.edit().putBoolean(Constants.IS_SUBSCRIBE, true)
                    .apply()


                when {
                    getCurrentFilter() == Constants.MALE -> {
                        binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_male_single,
                            null))
                        selectMale()
                    }
                    getCurrentFilter() == Constants.FEMALE -> {
                        binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_female_single,
                            null))
                        selectFeMale()

                    }
                    else -> {
                        binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders,
                            null))
                        selectAll()

                    }
                }

            } else {
                sharedPreferences.edit().putBoolean(Constants.IS_SUBSCRIBE, false)
                    .apply()
                changeImaheBtnWithoutSubs()

                setCurrentFilter(Constants.ALL)
                binding.singleText.setText(R.string.all)
                binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders,
                    null))

            }
        }

    private fun billingSetup() {
        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(
                billingResult: BillingResult,
            ) {
                if (billingResult.responseCode ==
                    BillingClient.BillingResponseCode.OK
                ) {
                    queryProductSubscribe(subscribe_product)
                    reloadPurchaseSubs()
                } else {
                    Log.i("TAG", "OnBillingSetupFinish failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i("TAG", "OnBillingSetupFinish connection lost")
            }
        })
    }

    private fun queryProductSubscribe(productId: String) {
        Log.i("TAG", "queryProduct:")

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(
                            BillingClient.ProductType.SUBS
                        )
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { billingResult, productDetailsList ->
            if (!productDetailsList.isEmpty()) {
                productSubscribeDetails = productDetailsList[0]

                requireActivity().run {
                    Log.i("TAG",
                        "productSubscribeDetails: productssubs " + productSubscribeDetails.name)
                }
            } else {
                Log.i("TAG", "productSubscribeDetails: No productssubs")
            }
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                for (purchase in purchases) {
                    completePurchase(purchase)
                }
            } else if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.USER_CANCELED
            ) {
                Log.i("TAG", "onPurchasesUpdated: Purchase Canceled")
            } else {
                Log.i("TAG", "onPurchasesUpdated: Error")
            }
        }


    private fun completePurchase(item: Purchase) {
        purchase = item
    }


    private fun getSubscribeState() = sharedPreferences.getBoolean(Constants.IS_SUBSCRIBE,
        false)


    private fun setCurrentFilter(gender: String) =
        sharedPreferences.edit().putString(Constants.GENDER_FILTER,
            gender).apply()

    private fun getCurrentFilter() = sharedPreferences.getString(Constants.GENDER_FILTER,
        Constants.ALL)

    private fun navShop() {
        binding.tap.visibility = View.GONE
        findNavController().navigate(TapToStartFragmentDirections.actionTapToStartFragmentToShopFragment())
    }

    fun ImageView.slideUp(duration: Int = 400) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat() + 110f, 0f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }

    fun CardView.slideUps(duration: Int = 400) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat() + 110f, 0f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }

    fun ImageView.slideDown(duration: Int = 400) {
        visibility = View.GONE
        val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat() + 110f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)

    }


    private fun navTo() {
        fotoapparat.stop()
        findNavController().navigate(TapToStartFragmentDirections.actionTapToStartFragmentToSearchFragment())
    }

    private lateinit var fotoapparat: Fotoapparat
    private var permissionsGranted: Boolean = true
    private var activeCamera: Camera = Camera.Front
    private fun getCurrentLang() = sharedPreferences.getString(Constants.CURRENT_LANGUAGE,
        "")

    private fun getCurrentSwipes() = sharedPreferences.getInt(Constants.CURRENT_SWIPES,
        0)


    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
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
        binding.tap.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(R.drawable.tap_two)
            .into(binding.tap)


        billingSetup()

        isUp = false

        binding.cameraView.visibility = View.VISIBLE
        try {
            binding.cameraView.setScaleType(ScaleType.CenterCrop)

        } catch (e: Exception) {
            e.printStackTrace()
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
            val params = Bundle()
            params.putString("on_tap_to_start_screen_click_to_tap",
                "on tap to start screen click to tap")
            firebaseAnalytics.logEvent("on_tap_to_start_screen_click_to_tap", params)

            if (getCurrentSwipes() > 0) {
                try {
                    navTo()
                } catch (ex: IllegalArgumentException) {
                    ex.printStackTrace()
                    sharedPreferences.edit().putBoolean("is_from_crash", true).apply()
                    MainActivity.start(requireContext())
                }
            } else {
                try {
                    SwipeWarningDialog.show(childFragmentManager, this)
                } catch (ex: IllegalArgumentException) {
                    ex.printStackTrace()
                }
            }
        }


        binding.two.setOnClickListener {

            binding.female.slideUp()
            binding.male.slideUp()

            binding.allGender.slideUp()
            binding.frameSingleAll.visibility = View.GONE
            binding.containers.visibility = View.VISIBLE

            if (getSubscribeState()) {
                if (getCurrentFilter() == Constants.MALE) {
                    selectMale()
                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_male_single,
                        null))
                } else if (getCurrentFilter() == Constants.FEMALE) {
                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_female_single,
                        null))
                    selectFeMale()
                } else {
                    binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders,
                        null))
                    selectAll()
                }
            }
        }



        binding.allGender.setOnClickListener {
            with(binding) {
                setCurrentFilter(Constants.ALL)
                binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_all_genders,
                    null))
                binding.frameSingleAll.visibility = View.VISIBLE
                binding.containers.visibility = View.GONE
                binding.singleText.setText(R.string.all)
                selectAll()

                binding.two.slideUps()
            }

        }


        binding.male.setOnClickListener {
            if (getSubscribeState()) {
                setCurrentFilter(Constants.MALE)
                binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_male_single,
                    null))
                binding.frameSingleAll.visibility = View.VISIBLE
                binding.containers.visibility = View.GONE
                binding.singleText.setText(R.string.male)

                selectMale()
                binding.two.slideUps()

            } else {
                navShop()
            }
        }

        binding.female.setOnClickListener {
            if (getSubscribeState()) {

                setCurrentFilter(Constants.FEMALE)
                binding.icSingleGenderImage.setImageDrawable(resources.getDrawable(R.drawable.ic_female_single,
                    null))
                binding.frameSingleAll.visibility = View.VISIBLE
                binding.containers.visibility = View.GONE
                binding.singleText.setText(R.string.female)

                selectFeMale()

                binding.two.slideUps()

            } else {
                navShop()
            }
        }


        val timer = object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                try {
                    binding.tvTapToStart.visibility = View.VISIBLE
                    binding.ivHand.visibility = View.VISIBLE
                    binding.tap.visibility = View.VISIBLE

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        timer.start()



        binding.frameSingleAll.visibility = View.VISIBLE
        binding.tap.visibility = View.VISIBLE
        if (permissionsGranted) {
            fotoapparat.start()
        }
        val timers = object : CountDownTimer(700, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                try {
                    binding.tap.visibility = View.VISIBLE
                    binding.tvTapToStart.visibility = View.VISIBLE
                    binding.two.visibility = View.VISIBLE
                    binding.ivHand.visibility = View.VISIBLE
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        timers.start()
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
        binding.two.visibility = View.VISIBLE

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


    private fun changeImaheBtnWithSubs() {
        with(binding) {
            male.setImageResource(R.drawable.ic_male_subs)
            allGender.setImageResource(R.drawable.ic_all)
            female.setImageResource(R.drawable.ic_female_subs)
        }
    }

    private fun changeImaheBtnWithoutSubs() {
        with(binding) {
            male.setImageResource(R.drawable.ic_male)
            allGender.setImageResource(R.drawable.ic_all)
            female.setImageResource(R.drawable.ic_female)
        }
    }

    private fun selectMale() {
        with(binding) {
            singleText.setText(R.string.male)

            male.setImageResource(R.drawable.ic_male_subs_checked)
            allGender.setImageResource(R.drawable.ic_all_unckecked)
            female.setImageResource(R.drawable.ic_female_subs)
        }
    }

    private fun selectFeMale() {
        with(binding) {
            singleText.text = getString(R.string.female)

            male.setImageResource(R.drawable.ic_male_subs)
            allGender.setImageResource(R.drawable.ic_all_unckecked)
            female.setImageResource(R.drawable.ic_female_subs_checked)
        }
    }

    private fun selectAll() {
        with(binding) {
            singleText.text = getString(R.string.all)
            male.setImageResource(R.drawable.ic_male_subs)
            allGender.setImageResource(R.drawable.ic_all)
            female.setImageResource(R.drawable.ic_female_subs)
        }
    }

    override fun onBackToMain() {
    }

    override fun onOpenShop() {
        navShop()
    }

    companion object {

        fun newInstance(videos: String): TapToStartFragment {
            return TapToStartFragment().apply {
                arguments = bundleOf(
                    "ENTITY_ID" to videos,

                    )
            }
        }
    }
}