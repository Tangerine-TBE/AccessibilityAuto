package com.example.accessibilityauto.custom.event

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.example.accessibilityauto.custom.App
import com.example.accessibilityauto.custom.event.base.EventAction
import com.example.accessibilityauto.custom.event.base.EventController
import com.example.accessibilityauto.custom.event.base.MsgType
import com.example.accessibilityauto.custom.task.TaskProperty
import kotlin.concurrent.thread

class AutoShutDownEvent(override val task: TaskProperty) :
    EventAction("自动关机", EventController.SYSTEM_EVENT) {
    override var currentStep = 1

    override fun start(service: AccessibilityService, event: AccessibilityEvent?) {
        when (currentStep) {
            1 -> {
                thread {
                    App.service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
                    currentStep++
                    Thread.sleep(2000)
                    while (stop() != true) {
                        if (findViewByTextExactly("重启") != null) {
                            val target = findViewByTextExactly("重启")?.parent?.getChild(3)
                            if (target != null) {
                                target.getBoundsInScreen(rect)
                                val x1 = rect.centerX()
                                val y1 = rect.centerY()
                                App.service.rootInActiveWindow?.getBoundsInScreen(rect)
                                scroll(x1, y1, x1, rect.top)
                                EventController.INSTANCE.removeEvent(this, MsgType.SUCCESS)
                                return@thread
                            }

                        }
                    }
                }

            }
        }

    }


    override var runTime: Int = 60

}