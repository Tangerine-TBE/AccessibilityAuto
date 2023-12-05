package com.example.accessibilityauto.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.accessibilityauto.R
import com.example.accessibilityauto.custom.SystemTransactionManager
import com.example.accessibilityauto.service.record.accessibility.AccessibilityActionRecorder
import com.example.accessibilityauto.service.record.accessibility.AccessibilityNotificationObserver
import com.example.accessibilityauto.service.record.accessibility.ActivityInfoProvider
import java.util.TreeMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

private const val NOTIFICATION_ID = 1
private const val CHANEL_ID = "com.example.accessibilityAuto"

abstract class AccessibilityService : android.accessibilityservice.AccessibilityService() {
    private lateinit var mActivityInfoProvider: ActivityInfoProvider
    private lateinit var mNotificationObserver: AccessibilityNotificationObserver
    private val mAccessibilityActionRecorder: AccessibilityActionRecorder =
        AccessibilityActionRecorder()

    private var mFastRootInActiveWindow: AccessibilityNodeInfo? = null
    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        instance = this
        // Log.v(TAG, "onAccessibilityEvent: $event");
        if (!containsAllEventTypes && !eventTypes.contains(event.eventType)) return
        val type = event.eventType
        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || type == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            val root = rootInActiveWindow
            if (root != null) {
                mFastRootInActiveWindow = root
            }
        }

        for ((_, delegate) in mDelegates) {
            val types = delegate.eventTypes
            if (types != null && !delegate.eventTypes!!.contains(event.eventType)) {/*当无障碍服务监听到的时间不能被当前事务消费则交给下一个事务处理*/
                continue
            }
            if (types == null) {
                continue
            }
            //long start = System.currentTimeMillis();
            if (delegate.onAccessibilityEvent(
                    this@AccessibilityService,
                    event
                )
            ) {/*这里的意图是，当事件被当前优先级比较高的事务消费掉时不再往下分发了*/
                break
            }

            //Log.v(TAG, "millis: " + (System.currentTimeMillis() - start) + " delegate: " + entry.getValue().getClass().getName());
        }

    }

    override fun onInterrupt() {
    }

    override fun getRootInActiveWindow(): AccessibilityNodeInfo? {
        return try {
            super.getRootInActiveWindow()
        } catch (e: Exception) {
            null
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        mNotificationObserver = AccessibilityNotificationObserver(this)
        mActivityInfoProvider = ActivityInfoProvider(this)
        startForeground(NOTIFICATION_ID, buildNotification())
        addDelegate(100, mActivityInfoProvider)
        addDelegate(200, mNotificationObserver)
        addDelegate(300, mAccessibilityActionRecorder)
        addDelegate(400, SystemTransactionManager())
        initEvent()
    }

    abstract fun initEvent()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(this, CHANEL_ID).setContentTitle("需要一直保持运行中")
            .setContentText("請勿打擾").setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis()).setContentIntent(null).setChannelId(CHANEL_ID)
            .setVibrate(LongArray(0)).build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        val name: CharSequence = "守護服務"
        val description = "无障碍守护進程正在运行"
        val channel = NotificationChannel(
            CHANEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = description
        channel.enableLights(false)
        manager.createNotificationChannel(channel)
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.v(TAG, "onServiceConnected: " + serviceInfo.toString())
        instance = this
        super.onServiceConnected()
        LOCK.lock()
        ENABLED.signalAll()
        LOCK.unlock()
        super.onDestroy()

    }

    fun fastRootInActiveWindow(): AccessibilityNodeInfo? {
        return mFastRootInActiveWindow
    }

    companion object {

        private val TAG = "AccessibilityService"

        private val mDelegates = TreeMap<Int, AccessibilityDelegate>()
        private val LOCK = ReentrantLock()
        private val ENABLED = LOCK.newCondition()
        var instance: AccessibilityService? = null
            private set
        private var containsAllEventTypes = false
        private val eventTypes = HashSet<Int>()

        fun addDelegate(uniquePriority: Int, delegate: AccessibilityDelegate) {
            mDelegates[uniquePriority] = delegate
            val set = delegate.eventTypes
            if (set == null) containsAllEventTypes = true
            else eventTypes.addAll(set)
        }

        fun disable(): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                instance?.disableSelf()
                return true
            }
            return false
        }

        fun waitForEnabled(timeOut: Long): Boolean {
            if (instance != null) return true
            LOCK.lock()
            try {
                if (instance != null) return true
                if (timeOut == -1L) {
                    ENABLED.await()
                    return true
                }
                return ENABLED.await(timeOut, TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return false
            } finally {
                LOCK.unlock()
            }
        }
    }

}