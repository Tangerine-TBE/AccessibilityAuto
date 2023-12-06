package com.example.accessibilityauto

import android.util.Log
import com.example.accessibilityauto.custom.App
import com.example.accessibilityauto.custom.event.base.SuspendEventManager
import com.example.accessibilityauto.custom.task.TaskProperty
import com.example.accessibilityauto.custom.task.TaskType
import com.example.accessibilityauto.service.AccessibilityService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyService : AccessibilityService() {
    private var currentJob: Job? = null
    private var currentType: TaskType? = null

    override fun initEvent() {
        Log.e("tag", "初始化成功")
        App.service = this
        initLooperTask()
    }

    private fun initTimerTask() {

    }

    private fun initLooperTask() {
        currentJob = App.app.launch {
            SuspendEventManager.suspendAutoCheckBaseEvent(
                TaskProperty(
                    TaskType.AUTO_LOOPER_BASE, "", "", "", false, currentJob, ""
                )
            )
        }

    }


}