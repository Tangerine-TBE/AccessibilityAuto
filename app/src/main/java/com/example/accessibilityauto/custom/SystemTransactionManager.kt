package com.example.accessibilityauto.custom

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import com.example.accessibilityauto.service.AccessibilityDelegate

/*拦截所有事件，视情况下发事件*/
class SystemTransactionManager() : AccessibilityDelegate {


    override val eventTypes: Set<Int>?
        get() {
            return if (EventController.INSTANCE.getCurrentEvent() == null){
                null
            }else{
                EventController.INSTANCE.getCurrentEvent()!!.type
            }
        }


    override fun onAccessibilityEvent(
        service: AccessibilityService,
        event: AccessibilityEvent
    ): Boolean {
        if (eventTypes != null){
            if (eventTypes!!.contains(event.eventType)) {
                val window = service.getWindow(event.windowId)
                if (window?.isFocused != false) {
                    val handlerEvent =  EventController.INSTANCE.getCurrentEvent()
                    handlerEvent?.execute(event)
                    return false
                }
            }
        }

        return false
    }

    private fun AccessibilityService.getWindow(windowId: Int): AccessibilityWindowInfo? {
        windows.forEach {
            if (it.id == windowId) {
                return it
            }
        }
        return null
    }
}