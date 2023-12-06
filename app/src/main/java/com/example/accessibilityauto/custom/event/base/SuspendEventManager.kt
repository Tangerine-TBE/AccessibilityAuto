package com.example.accessibilityauto.custom.event.base

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.accessibilityauto.custom.App
import com.example.accessibilityauto.custom.task.TaskProperty
import com.example.accessibilityauto.custom.task.TaskType
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val CLAZZ_PARENT_PACK = "cn.com.auto.thkl.custom.event."

@RequiresApi(Build.VERSION_CODES.P)
object SuspendEventManager {
    public fun getEvent(clazzName: String, scope: TaskProperty): Event {
        val clazz = Class.forName("$CLAZZ_PARENT_PACK${App.EVENT_PACK_NAME}.${clazzName}")
        val constructor = clazz.getConstructor(scope::class.java)
        return constructor.newInstance(scope) as Event

    }
//    suspend fun suspendAutoCheckBaseEvent(scope: TaskProperty){
//        suspendCoroutine { continuation ->
//            EventController.INSTANCE.execute(getEvent("AutoLoopBaseEvent",scope,object :EventAction.OnEventCompleted{
//                override fun eventCompleted(name: String) {
//                    continuation.resume(name)
//                }
//            }))
//        }
//    }

//    suspend fun suspendAutoShutDownEvent(scope: TaskProperty) {
//        suspendCoroutine<String> { continuation ->
//            EventController.INSTANCE.addEvent(getEvent("AutoShutDownEvent", scope))
//                .execute(object : EventAction.OnEventCompleted {
//                    override fun eventCompleted(name: String) {
//                        continuation.resume(name)
//                    }
//                })
//        }
//    }
//
//    suspend fun suspendAutoStartTaskEvent(scope: TaskProperty) {
//        suspendCoroutine<String> { continuation ->
//            EventController.INSTANCE.addEvent(getEvent("AutoStartSetEvent", scope))
//                .execute(object : EventAction.OnEventCompleted {
//                    override fun eventCompleted(name: String) {
//                        continuation.resume(name)
//                    }
//                })
//        }
//    }
//
//    suspend fun suspendAutoClearCacheEvent(scope: TaskProperty) {
//        suspendCoroutine<String> { continuation ->
//            EventController.INSTANCE.addEvent(getEvent("AutoClearCacheEvent", scope))
//                .execute(object : EventAction.OnEventCompleted {
//                    override fun eventCompleted(name: String) {
//                        continuation.resume(name)
//                    }
//                })
//        }
//    }

//    suspend fun suspendAutoOverLayerEvent(job: Job?, context: Context) {
//        suspendCoroutine<String> { continuation ->
//            EventController.INSTANCE.execute(      getEvent(
//                "AutoOverLayerEvent", TaskProperty(
//                    TaskType.AUTO_OVER_LAYER_TASK,
//                    context.packageName,
//                    "",
//                    "",
//                    false,
//                    job,
//                    AppUtils.getAppName()
//                )
//            ),object : EventAction.OnEventCompleted {
//                override fun eventCompleted(name: String) {
//                    continuation.resume(name)
//                }
//            })
//        }
//    }




    suspend fun suspendAutoCheckBaseEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoRequestPermissionEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoLieBaoEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoLieBaoEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoClearEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoClearEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoCheckWXEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoCheckWXLoginEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoCheckAliPayEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoCheckAlpayEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoRestartEvent(scope: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoRestartEvent", scope),object : EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })

        }
    }

    suspend fun suspendAutoUninstallPackEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoUninstallEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }
    suspend fun suspendAutoRetryFixEvent(taskProperty: TaskProperty){
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoRetryHandlerEvent",taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoStopEvent(
        taskProperty: TaskProperty
    ) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoStopEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoInstallPackEvent(taskProperty: TaskProperty): String {
        return suspendCoroutine { continuation ->
            EventController.INSTANCE
                .execute(getEvent("AutoInstallPackEvent", taskProperty),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun suspendAutoPermissionAppsEvent(taskProperty: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE.execute(  getEvent(
                "AutoRequestAppsPermissionEvent", taskProperty
            ),object : EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }
    }

    suspend fun firstStartEvent(scope: TaskProperty) {
        suspendCoroutine<String> { continuation ->
            EventController.INSTANCE
                .execute(getEvent("FirstStartAccessibilityEvent", scope),object :
                    EventAction.OnEventCompleted {
                    override fun eventCompleted(name: String) {
                        continuation.resume(name)
                    }
                })
        }

    }
}

