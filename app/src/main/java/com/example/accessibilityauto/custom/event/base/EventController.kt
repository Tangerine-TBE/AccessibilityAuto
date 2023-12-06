package com.example.accessibilityauto.custom.event.base

import android.view.accessibility.AccessibilityEvent
import com.example.accessibilityauto.custom.App
import com.example.accessibilityauto.custom.task.TaskType
import kotlin.concurrent.thread


class EventController private constructor() {
    private var currentEvent: Event? = null
        @Synchronized
        set
    private var eventTimeTask: EventTimeTask? = null

    init {
        if (!flag) {
            flag = true
        } else {
            throw Throwable("SingleTon is being attacked.")
        }
    }


    companion object {
        private var flag = false
        val INSTANCE = CommonSingletonHolder.holder
        val SYSTEM_EVENT = setOf(
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, AccessibilityEvent.WINDOWS_CHANGE_ACTIVE
        )
        val TOUCH_EVENT = setOf(
            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
        )
        val ALL_EVENT = setOf(
            AccessibilityEvent.TYPE_VIEW_CLICKED,
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.WINDOWS_CHANGE_ACTIVE
        )
    }

    /**
     * 静态内部类单例
     */
    private object CommonSingletonHolder {
        val holder = EventController()
    }

    /**每次添加事件，记录添加事件的时间*/

    fun execute(event: Event, eventCompleted: EventAction.OnEventCompleted) {
        if (eventTimeTask?.stop == false) {
            eventTimeTask?.stop = true
        }
        eventTimeTask = EventTimeTask(event)
        thread {
            /*这里会出现多线程问题!*/
            this.currentEvent = event
            /*这里可能出现延迟状态*/
            (event as EventAction).setOnEventCompleted(eventCompleted)
            event.execute(null)
            eventTimeTask?.run()
        }


    }

    @Synchronized
    fun removeEvent(event: Event?, msgType: MsgType) {
        if (eventTimeTask != null) {
            eventTimeTask?.stop = true
        }
        if (event != null) {
            if (event.task.job != null) {/*如果协程进行了取消就不需要回调这个锁*/
                if (!event.task.job.isCancelled) {
                    event.runTime++
                    (event as EventAction).eventCompleted?.eventCompleted(msgType.name)
                    event.eventCompleted = null
                    this.currentEvent = null
                } else {
                }
            } else {
                event.runTime++
                (event as? EventAction)?.eventCompleted?.eventCompleted(msgType.name)
                (event as? EventAction)?.eventCompleted = null
                this.currentEvent = null
            }
        } else {
        }
    }


    fun getCurrentEvent(): Event? {
        return currentEvent
    }

    fun getEventTask(): EventTimeTask? {
        return eventTimeTask
    }


    class EventTimeTask(
        private val event: Event?,
    ) : Runnable {
        private var startTime: Long = 0
        var stop = false
            set(value) {
                if (value) {
                    val offsetTime = System.currentTimeMillis() / 1000 - startTime
                }
                field = value
            }

        override fun run() {
            startTime = System.currentTimeMillis() / 1000
            while (!stop) {
                /**运行超时策略*/
                /**runTime不作为一个事件的总体超时，而是作为一个观察对象存在，每一个步伐递进与回退都需要及时的更改其属性
                 * */
                val last = event?.runTime
                if (App.EVENT_PACK_NAME == "huaweiAndroid9") {
                    Thread.sleep(30 * 1000)
                } else {
                    Thread.sleep(20 * 1000)
                }
                val now = event?.runTime
                if (last == now) {
                    /**超时*/
                    /**汇报事件，并重启当前任务*/
                    if (!stop) {
                        INSTANCE.removeEvent(event, MsgType.TIME_OUT)
//                        if (event?.task?.taskType?.name != TaskType.AUTO_NULL.name) {
////                            AccessibilityViewModel.retry.postValue(event?.task)/*重试任务，非事件！*/
//                        } else if (event.task.taskType?.name == TaskType.AUTO_NULL.name) {
////                            L.e("无法重试任务")
//                        } else {
////                            L.e("未知的类型")
//                        }
//                        AccessibilityViewModel.showBottomToast.postValue("${event?.name}-运行超时")
                        return
                    }
                }
            }
        }

    }


}