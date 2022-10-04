package com.video.chat.v.love.chatroulette

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp
import com.video.chat.v.love.chatroulette.utils.LocaleHelper
import com.appsflyer.AppsFlyerLib

@HiltAndroidApp
class App : Application() {
     val ONESIGNAL_APP_ID = "2c783605-7b98-4b19-bd5c-2cfad09a8aa8"

    init {
        instance = this
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
        AppsFlyerLib.getInstance().init("GzuKdki7JYYTFqT8KCoFtD", null, this)
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setDebugLog(true)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

    }

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun attachBaseContext(base: Context) {
        LocaleHelper().setLocale(base, LocaleHelper().getLanguage(base))
        super.attachBaseContext(LocaleHelper().onAttach(base))
    }
}