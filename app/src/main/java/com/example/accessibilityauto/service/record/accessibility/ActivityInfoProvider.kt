package com.example.accessibilityauto.service.record.accessibility

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import com.example.accessibilityauto.service.AccessibilityDelegate

/**
 * Created by Stardust on 2017/3/9.
 *
 * tangerine 这个事件处理是响应所有类容的
 */

class ActivityInfoProvider(private val context: Context) : AccessibilityDelegate {

    private val mPackageManager: PackageManager = context.packageManager

    @Volatile
    private var mLatestPackage: String = ""
    @Volatile
    private var mLatestActivity: String = ""

    val latestPackage: String
        get() {
            if (useUsageStats && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mLatestPackage = getLatestPackageByUsageStats()
            }
            return mLatestPackage
        }

    val latestActivity: String
        get() {
            return mLatestActivity
        }

    var useUsageStats: Boolean = false


    override val eventTypes: Set<Int>
        get() = setOf(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,AccessibilityEvent.WINDOWS_CHANGE_ACTIVE)

    override fun onAccessibilityEvent(
        service: android.accessibilityservice.AccessibilityService,
        event: AccessibilityEvent
    ): Boolean {
        if (eventTypes.contains(event.eventType)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = service.getWindow(event.windowId)
                if (window?.isFocused != false) {
                    setLatestComponent(event.packageName, event.className)
                    /*事务处理*/
                    return  false
                }
            }
        }
        return false

    }

    fun getLatestPackageByUsageStats(): String {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val current = System.currentTimeMillis()
        val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, current - 60 * 60 * 1000, current)
        return if (usageStats.isEmpty()) {
            mLatestPackage
        } else {
            usageStats.sortBy {
                it.lastTimeStamp
            }
            usageStats.last().packageName
        }

    }

    private fun setLatestComponent(latestPackage: CharSequence?, latestClass: CharSequence?) {
        if (latestPackage == null)
            return
        val latestPackageStr = latestPackage.toString()
        val latestClassStr = (latestClass ?: "").toString()
        if (isPackageExists(latestPackageStr)) {
            mLatestPackage = latestPackage.toString()
            mLatestActivity = latestClassStr
        }
    }

    private fun isPackageExists(packageName: String): Boolean {
        return try {
            mPackageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    companion object {
        private val DUMP_WINDOW_COMMAND = """
            oldActivity=""
            currentActivity=`dumpsys window windows | grep -E 'mCurrentFocus'`
            while true
            do
                if [[ ${'$'}oldActivity != ${'$'}currentActivity && ${'$'}currentActivity != *"=null"* ]]; then
                    echo ${'$'}currentActivity
                    oldActivity=${'$'}currentActivity
                fi
                currentActivity=`dumpsys window windows | grep -E 'mCurrentFocus'`
            done
        """.trimIndent()

        private const val LOG_TAG = "ActivityInfoProvider"
    }
}
private fun android.accessibilityservice.AccessibilityService.getWindow(windowId: Int): AccessibilityWindowInfo? {
    windows.forEach {
        if (it.id == windowId) {
            return it
        }
    }
    return null
}