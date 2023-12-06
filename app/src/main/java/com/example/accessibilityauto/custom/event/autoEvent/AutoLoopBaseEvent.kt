package com.example.accessibilityauto.custom.event.autoEvent

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.accessibilityauto.custom.event.base.Event
import com.example.accessibilityauto.custom.event.base.EventAction
import com.example.accessibilityauto.custom.event.base.EventController
import com.example.accessibilityauto.custom.task.TaskProperty

class AutoLoopBaseEvent(
    override val task: TaskProperty,
) : EventAction("循环检测", EventController.SYSTEM_EVENT), Event {
    override fun start(service: AccessibilityService, event: AccessibilityEvent?) {
        if (currentStep == 1){
            currentStep++
            while (stop() != true){
                Thread.sleep(1000)
                runTime++
            }
        }


    }

    override var currentStep: Int = 1

    override var runTime: Int = 1

}