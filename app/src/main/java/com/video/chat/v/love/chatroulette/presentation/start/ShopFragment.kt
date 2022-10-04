package com.video.chat.v.love.chatroulette.presentation.start

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.databinding.ShopFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.match.MatchFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AFInAppEventType // Predefined event names
import com.appsflyer.AFInAppEventParameterName // Predefined parameter na
import com.video.chat.v.love.chatroulette.App

@AndroidEntryPoint
class ShopFragment : BaseFragment<ShopFragmentBinding>() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    private lateinit var billingClient: BillingClient
    private lateinit var productDetails: ProductDetails
    private lateinit var productSubscribeDetails: ProductDetails
    private lateinit var purchase: Purchase
    private val demo_product = "swipes_pack"
    private val subscribe_product = "swipes_vip_subscription"
    private lateinit var sharedPreferences: SharedPreferences

    val TAG = "InAppPurchaseTag"
    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ShopFragmentBinding.inflate(inflater)
        return binding.root
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
    }

    override fun onStart() {
        super.onStart()

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )

        if ( sharedPreferences.getBoolean(Constants.IS_SUBSCRIBE, false)){
            binding.cardNew.setImageResource(R.drawable.ic_subscribed_is_clicked)
        }else{
            binding.cardNew.setImageResource(R.drawable.ic_subscribe)
        }

        hideSystemBars()
        hideSystemUI()
        billingSetup()

        binding.allCl.visibility = View.VISIBLE

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_shop_screen", "on shop screen")
        firebaseAnalytics.logEvent("on_shop_screen", params)


        binding.back.setOnClickListener {
            val params = Bundle()
            params.putString("on_shop_screen_click_back", "on shop screen click back")
            firebaseAnalytics.logEvent("on_shop_screen_click_back", params)
            findNavController().popBackStack(R.id.tapToStartFragment, false)

        }

        binding.imageView4.setOnClickListener {
            try {
                makePurchase(binding.imageView4)
            } catch (ex: UninitializedPropertyAccessException) {
                ex.printStackTrace()
            }
        }

        binding.cardNew.setOnClickListener {
            try {
                makeSubscribe()
            } catch (ex: UninitializedPropertyAccessException) {
                ex.printStackTrace()
            }
        }


        binding.allCl.visibility = View.VISIBLE


    }

    override fun onStop() {
        super.onStop()
        binding.allCl.visibility = View.GONE

    }

    override fun onPause() {
        super.onPause()

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
                    Log.i(TAG, "OnBillingSetupFinish connected")

                    queryProductSubscribe(subscribe_product)
                    reloadPurchaseSubs()
                    queryProduct(demo_product)
                    reloadPurchase()


                } else {
                    Log.i(TAG, "OnBillingSetupFinish failed")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i(TAG, "OnBillingSetupFinish connection lost")
            }
        })


    }

    private fun queryProduct(productId: String) {
        Log.i(TAG, "queryProduct:")

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(
                            BillingClient.ProductType.INAPP
                        )
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { billingResult, productDetailsList ->
            if (!productDetailsList.isEmpty()) {
                productDetails = productDetailsList[0]
                run {
                    Log.i(TAG,
                        "onProductDetailsResponse: products " + productDetails.name) //onProductDetailsResponse: products Swipes pack
                }
            } else {
                Log.i(TAG, "onProductDetailsResponse: No products")
            }
        }
    }


    private fun queryProductSubscribe(productId: String) {
        Log.i(TAG, "queryProduct:")

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

                run {
                    Log.i(TAG,
                        "productSubscribeDetails: productssubs ")
                }
            } else {
                Log.i(TAG, "productSubscribeDetails: No productssubs")
            }
        }
    }

    fun makePurchase(view: View?) {
        Log.i(TAG, "makePurchase:")

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                ImmutableList.of(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
    }

    fun makeSubscribe() {
        Log.i(TAG, "makeSubscribe:")
        val offerToken = productSubscribeDetails.subscriptionOfferDetails?.get(0)?.offerToken

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                ImmutableList.of(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productSubscribeDetails)
                        .setOfferToken(offerToken.toString())
                        .build()
                )
            )
            .build()
        billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
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
                Log.i(TAG, "onPurchasesUpdated: Purchase Canceled")
            } else {
                Log.i(TAG, "onPurchasesUpdated: Error")
            }
        }

    private fun completePurchase(item: Purchase) {
        purchase = item
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            run {
                try {
                    Log.d(TAG, "Purchase Complete " + item.products[0])
                    Log.i(TAG, "Purchase Complete " + item.products[0])
                    if (item.products[0].equals("swipes_pack")) {

                        sharedPreferences.edit()
                            .putInt(Constants.CURRENT_SWIPES, getLocalSwipes() + 20)
                            .apply()
                        consumePurchase()
                        val eventValues = HashMap<String, String>()
                        eventValues.put("some_parameter", "swipes_pack")
                        AppsFlyerLib.getInstance().validateAndLogInAppPurchase(requireContext(),
                            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGECAqTnnmbf22vjAXg7J/bZ+Fh9zio6sQp65+u3He6cyyXOtDBRCwfrpR8J9uwCyjPvnLWSAyWWY76+pViwqWKWFnmspXK0mFCiFMrYqRS0/H1fl42C4kesTGEDnQY+mEJi0EsGJETQ9s4YijMJQLr+wTK5BjVMm5S8jhmClZkvXzDPY+PkpGXdHNQsL/EvoVgaQNq1QUj8Cj0HOWkYvh/h+dei+WGj1cMFZqxs9pYtGhFJDt00s0y8bCgw2K4k45l5sEjjscSvIGyAe3pTOu0hFV0Y6fef/pikyRvtImiajF/XuVIvO99IzQwiPowCuXgh9lLgDlgDWUawcPyVZQIDAQAB",
                            purchase.getSignature(),
                            purchase.getOriginalJson(),
                            "1",
                            "USD",
                            eventValues)


                    } else if (item.products[0].equals("swipes_vip_subscription")) {
                        sharedPreferences.edit()
                            .putInt(Constants.CURRENT_SWIPES, getLocalSwipes() + 50)
                            .apply()
                        sharedPreferences.edit().putBoolean(Constants.IS_SUBSCRIBE, true)
                            .apply()
                        consumePurchase()

                        binding.cardNew.setImageResource(R.drawable.ic_subscribed_is_clicked)
                        val eventValues = HashMap<String, String>()
                        eventValues.put("some_parameter", "swipes_vip_subscription")
                        AppsFlyerLib.getInstance().validateAndLogInAppPurchase(requireContext(),
                            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiGECAqTnnmbf22vjAXg7J/bZ+Fh9zio6sQp65+u3He6cyyXOtDBRCwfrpR8J9uwCyjPvnLWSAyWWY76+pViwqWKWFnmspXK0mFCiFMrYqRS0/H1fl42C4kesTGEDnQY+mEJi0EsGJETQ9s4YijMJQLr+wTK5BjVMm5S8jhmClZkvXzDPY+PkpGXdHNQsL/EvoVgaQNq1QUj8Cj0HOWkYvh/h+dei+WGj1cMFZqxs9pYtGhFJDt00s0y8bCgw2K4k45l5sEjjscSvIGyAe3pTOu0hFV0Y6fef/pikyRvtImiajF/XuVIvO99IzQwiPowCuXgh9lLgDlgDWUawcPyVZQIDAQAB",
                            purchase.getSignature(),
                            purchase.getOriginalJson(),
                            "5",
                            "USD",
                            eventValues)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            try {
                Log.d(TAG, "Purchase PENDING " + item.products[0])
                Log.i(TAG, "Purchase PENDING " + item.products[0])
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            try {
                Log.d(TAG, "Purchase PURCHASED " + item.products[0])
                Log.i(TAG, "Purchase PURCHASED " + item.products[0])
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun getLocalSwipes() = sharedPreferences.getInt(Constants.CURRENT_SWIPES,
        0)


    fun consumePurchase() {

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        coroutineScope.launch {
            val result = billingClient.consumePurchase(consumeParams)

            if (result.billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {
                run() {

                    Log.d(TAG, "Purchase consumed ")

                }
            }
        }
    }

    private fun reloadPurchase() {

        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(
            queryPurchasesParams,
            purchasesListener
        )
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

    private val purchasesSubsListener =
        PurchasesResponseListener { billingResult, purchases ->
            if (!purchases.isEmpty()) {
                purchase = purchases.first()

                sharedPreferences.edit().putBoolean(Constants.IS_SUBSCRIBE, true)
                    .apply()
                binding.cardNew.setImageResource(R.drawable.ic_subscribed_is_clicked)
            } else {
                sharedPreferences.edit().putBoolean(Constants.IS_SUBSCRIBE, false)
                    .apply()
                binding.cardNew.setImageResource(R.drawable.ic_subscribe)

            }
        }

    private val purchasesListener =
        PurchasesResponseListener { billingResult, purchases ->
            if (!purchases.isEmpty()) {
                purchase = purchases.first()
            }
        }

    companion object {

        fun newInstance(): ShopFragment {
            return ShopFragment().apply {
                arguments = bundleOf(
                    "ENTITY_ID" to "videos",

                    )
            }
        }
    }


}