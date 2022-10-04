package com.video.chat.v.love.chatroulette.presentation.bottomTabs.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.Purchase
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import dagger.hilt.android.AndroidEntryPoint
import com.video.chat.v.love.chatroulette.databinding.MainFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.bottomTabs.main.MainFragmentDirections
import com.video.chat.v.love.chatroulette.presentation.main.adapter.EventsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MainFragment : BaseFragment<MainFragmentBinding>() {
    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: MainViewModel by viewModels()
    private lateinit var addressesAdapter: EventsAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = MainFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideSystemBars()
        hideSystemUI()
        firebaseAnalytics =
            FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method

        val params = Bundle()


        params.putString("on_start_screen", "open start screen")
        firebaseAnalytics.logEvent("on_start_screen", params)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }
        }
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        viewModel.getUserSwipes()
        initObservable()
        requireActivity().getOnBackPressedDispatcher()
            .addCallback(viewLifecycleOwner, onBackPressedCallback)

        initAdapter()
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

    private fun initAdapter() {
        binding.clickStart.setOnClickListener {
            val params = Bundle()
            params.putString("click_on_start_btn", "start_btn_click")
            firebaseAnalytics.logEvent("click_on_start_btn", params)

            navigateToDirections(
                MainFragmentDirections.actionMainPageToSelfieFragment()
            )
        }
    }

    private fun initObservable() {
        viewModel.registrationData.observe(viewLifecycleOwner) {
            it?.let { data ->
                if (!sharedPreferences.getBoolean(Constants.IS_SUBSCRIBE, false)) {
                    sharedPreferences.edit()
                        .putInt(Constants.CURRENT_SWIPES, data.swipes)
                        .apply()
                }
            }

        }
    }
}