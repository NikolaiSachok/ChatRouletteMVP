package com.video.chat.v.love.chatroulette.presentation.auth.login

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.video.chat.v.love.chatroulette.App
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants.CURRENT_LANGUAGE
import com.video.chat.v.love.chatroulette.databinding.LoginFragmentBinding
import com.video.chat.v.love.chatroulette.presentation.DashboardActivity
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginFragmentBinding>() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        initLocale()
        enterInsideApp()


        initUI()

    }

    private fun enterInsideApp() {
        if (sharedPreferences.getBoolean("is_from_crash", false)){
            try {
                goHomeScreen()
                sharedPreferences.edit().putBoolean("is_from_crash", false).apply()
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

    private fun initUI() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method

        auth.signInAnonymously().addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "isSuccessful signup")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("yarek", "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("yarek", "signInAnonymously:failure", task.exception)
                    Toast.makeText(App.applicationContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }



        val params = Bundle()
        params.putString("on_splash_screen", "open splash screen")
        firebaseAnalytics.logEvent("on_splash_screen", params)

        binding.buildNum.text = "Version: " + getBuildNum()
         val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                try {
                    goHomeScreen()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        timer.start()

    }

     override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)



    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d("yarek", "current uid "+user?.uid)
        Log.d("yarek", "current tenantId "+user?.tenantId)
        Log.d("yarek", "current displayName "+user?.displayName)
        Log.d("yarek", "current user "+user?.providerId)

    }

    private fun getBuildNum(): String {
        val manager = requireActivity().packageManager
        val info =
            manager.getPackageInfo(requireActivity().packageName, PackageManager.GET_ACTIVITIES)

        return info.versionName
    }

    private fun goHomeScreen() {

        if (getCurrentLang() == "") {
            DashboardActivity.start(requireContext())


        } else {
            navigateToDirections(
                LoginFragmentDirections.actionGetLoginFragmentToTapToStartFragment(),
            )
        }
    }


    private fun initLocale() {
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }

    private fun updateAppLanguage(language: String) {
        LocaleHelper().setLocale(requireContext(), language)
        ActivityCompat.recreate(requireActivity())
    }

    private fun getCurrentLang() = sharedPreferences.getString(CURRENT_LANGUAGE, "")
}