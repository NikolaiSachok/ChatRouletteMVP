package com.video.chat.v.love.chatroulette.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.databinding.SwipeWarningDialogFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.base.BaseDialog
import com.video.chat.v.love.chatroulette.presentation.interfaces.BackFromSwipeWarning
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwipeWarningDialog(var backFromSwipeWarning: BackFromSwipeWarning) :
    BaseDialog<SwipeWarningDialogFragmentBinding>() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomAlertDialog)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_swipe_warning_dialog_screen", "on swipe warning dialog screen")
        firebaseAnalytics.logEvent("on_swipe_warning_dialog_screen", params)
    }

    private lateinit var backFromSwipe: BackFromSwipeWarning

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SwipeWarningDialogFragmentBinding.inflate(inflater)
        backFromSwipe = backFromSwipeWarning;
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        with(binding){
            openShop.setOnClickListener {
                val params = Bundle()
                params.putString("on_swipe_warning_click_open_shop", "on swipe warning dialog click open shop")
                firebaseAnalytics.logEvent("on_swipe_warning_click_open_shop", params)
                backFromSwipe.onOpenShop()
                dismiss()
            }
            back.setOnClickListener {
                val params = Bundle()
                params.putString("on_swipe_warning_click_back", "on swipe warning dialog click back")
                firebaseAnalytics.logEvent("on_swipe_warning_click_back", params)
                backFromSwipe.onBackToMain()
                dismiss()
            }
        }
    }

    companion object {

        fun newInstance(backFromSwipeWarning: BackFromSwipeWarning): SwipeWarningDialog {
            return SwipeWarningDialog(backFromSwipeWarning)
        }

        fun show(fragmentManager: FragmentManager, backFromSwipeWarning: BackFromSwipeWarning) {
            newInstance(backFromSwipeWarning).apply {
                show(fragmentManager, tag)
            }
        }
    }
}