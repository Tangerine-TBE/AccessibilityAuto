package com.example.accessibilityauto.custom

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import com.example.accessibilityauto.utils.SP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference

class App : Application(), CoroutineScope by CoroutineScope(Dispatchers.IO) {
    override fun onCreate() {
        super.onCreate()
        instance = WeakReference(this)
        SP.init(this)
    }
    companion object {
        private lateinit var instance: WeakReference<App>
        @SuppressLint("StaticFieldLeak")
        lateinit var service: AccessibilityService
        public lateinit var handler: Handler
        val app: App
            get() = instance.get()!!
        val EVENT_PACK_NAME: String
            get() {
                return "autoEvent"
            }
//        val OFFSET_VALUE: Float
//            /*2160   2560*//*2560 -2160 = 400*//*2160 == 90*//*2560 == 90+n*//*90/2160 == n/2560*//*n == 107*/
//            get() = (ScreenUtils.getScreenHeight() * 80 / 2160).toFloat()
    }
}