package com.video.chat.v.love.chatroulette.presentation.search

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.video.chat.v.love.chatroulette.R
import com.video.chat.v.love.chatroulette.constants.Constants
import com.video.chat.v.love.chatroulette.databinding.SearchFragmentBinding
import com.video.chat.v.love.chatroulette.network.data.videos.UserVideosData
import com.video.chat.v.love.chatroulette.network.data.videos.Videos
import com.video.chat.v.love.chatroulette.presentation.base.BaseFragment
import com.video.chat.v.love.chatroulette.presentation.interfaces.BackFromSwipeWarning
import com.video.chat.v.love.chatroulette.presentation.match.MatchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchFragmentBinding>(), BackFromSwipeWarning {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var matchFragment: MatchFragment
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics //Declare the FirebaseAnalytics object at the top of the activity

    private val mInterval = 0f // 1 second in this case
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private var timeInSecondsDetect = 0L
    private var lisfAllData: MutableList<Videos> = mutableListOf()

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()


    }

    private fun stopTimer() {
        try {
            mHandler?.removeCallbacks(mStatusChecker)
            timeInSeconds = 0L
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                updateStopWatchView(timeInSeconds)
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        timeInSecondsDetect = timeInSeconds
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        Glide.with(requireContext())
            .load(R.drawable.loader)
            .into(binding.progressBar)
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        decode(getMyPhoto())
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        firebaseAnalytics =
            FirebaseAnalytics.getInstance(requireContext()) //Initialize Firebase Analytics in the OnCreate method
        val params = Bundle()
        params.putString("on_search_screen", "on search screen")
        firebaseAnalytics.logEvent("on_search_screen", params)

        binding.imageView2.visibility = View.VISIBLE

        binding.stop.setOnClickListener {
            val params = Bundle()
            params.putString("on_search_screen_click_stop", "on search screen click stop")
            firebaseAnalytics.logEvent("on_search_screen_click_stop", params)
            findNavController().popBackStack()
            stopTimer()
        }

        requireActivity().actionBar?.show()
        startTimer()
        initObservers()
    }


    private fun getCurrentVideo() = sharedPreferences.getInt(Constants.UNICAL_USER_CODE,
        Constants.UNICAL_USER)

    private fun getSwipes(): Int {
        return sharedPreferences.getInt(Constants.CURRENT_SWIPES,
            0)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun getMyPhoto() = sharedPreferences.getString(Constants.MY_IMAGE,
        "")

    fun decode(imageString: String?) {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.imageView2.setImageBitmap(decodedImage)
    }

    private fun initObservers() {
        // call your function here
        viewModel.registrationData.observe(viewLifecycleOwner) {
            if (getSwipes() == 0 || getSwipes() == -1 || getSwipes() == -2) {

            } else {
                lisfAllData.clear()
                Log.d("yarek", "getSwipes(00) " + getSwipes())
                it?.let { videosData ->


                    if (getCurrentFilter() == Constants.FEMALE) {
                        if (getLocalCounter() >= videosData.videosFemale.size) {
                            setLocalCounter(0)
                        }
                        prepareData(videosData, videosData.videosFemale)

                    } else if (getCurrentFilter() == Constants.MALE) {
                        if (getLocalCounter() >= videosData.videosMale.size) {
                            setLocalCounter(0)
                        }
                        prepareData(videosData, videosData.videosMale)

                    } else {


                        videosData.videosFemale.forEach {
                            lisfAllData.add(it)
                        }
                        videosData.videosMale.forEach {
                            lisfAllData.add(it)
                        }

                        if (getLocalCounter() >= lisfAllData.size) {
                            setLocalCounter(0)
                        }
                        prepareData(videosData, lisfAllData)

                    }
                }
            }
        }

    }

    private fun hideByTimer() {
        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                try {
                    binding.childMainFragments?.visibility = View.VISIBLE

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        timer.start()
    }


    private fun prepareData(videosData: UserVideosData, videos: MutableList<Videos>) {
        if (getSwipes() == -1) {
            navTo(videosData)
        } else if (getSwipes() == -2 || getSwipes() == -3 || getSwipes() == -1 || getSwipes() == 0) {
            SwipeWarningDialog.show(childFragmentManager, this)
        } else {

            if (getLocalCounter() >= videos.size - 1) {
                setLocalCounter(0)
            }
            matchFragment = MatchFragment.newInstance(videos[getLocalCounter()].image)
            val timer = object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    try {
                        sharedPreferences.edit().putString("LINK_IMAGE",
                            videos[getLocalCounter()].image)
                            .apply()

                        sharedPreferences.edit().putLong("TIME_TO_SKIP",
                            videos[getLocalCounter()].timeToSkip)
                            .apply()

                        sharedPreferences.edit().putString("LINK_VIDEO",
                            videos[getLocalCounter()].videoUrl)
                            .apply()

                        stopTimer()

                        childFragmentManager.commit(allowStateLoss = true) {
                            replace(R.id.child_main_fragments, matchFragment)
                        }
                        val params = Bundle()
                        params.putString("seach_user_time",
                            "seach user millseconds spend " + timeInSecondsDetect)
                        firebaseAnalytics.logEvent("seach_user_time", params)
                        hideByTimer()

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                }
            }
            if (videosData.videosFemale.isNotEmpty() || videosData.videosMale.isNotEmpty()) {
                timer.start()
            }
        }


    }


    private fun showelements() {
        binding.rootContainers?.visibility = View.VISIBLE
    }

    private fun getLocalCounter() = sharedPreferences.getInt(Constants.COUNTER,
        0)

    private fun setLocalCounter(count: Int) = sharedPreferences.edit().putInt(Constants.COUNTER,
        count).apply()


    private fun getCurrentFilter() = sharedPreferences.getString(Constants.GENDER_FILTER,
        Constants.ALL)

    private fun navTo(videosData: UserVideosData) {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToMatchFragment())
    }

    override fun onBackToMain() {
        findNavController().popBackStack(R.id.tapToStartFragment, false)
    }

    override fun onOpenShop() {
        navigateToDirections(
            SearchFragmentDirections.actionSearchFragmentToShopFragment(),
        )
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()

        viewModel.getUserVideos()
        showelements()
    }

    override fun onStop() {
        super.onStop()
        stopTimer()
    }

}