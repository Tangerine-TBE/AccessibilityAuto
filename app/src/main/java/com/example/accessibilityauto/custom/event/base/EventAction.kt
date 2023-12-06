package com.example.accessibilityauto.custom.event.base

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.Rect
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import com.example.accessibilityauto.custom.App
import kotlin.concurrent.thread

abstract class EventAction(eventName: String, eventType: Set<Int>) : Event {
    override val name: String = eventName //初始化必须要的
    override var type: Set<Int> = eventType //初始化必须要的
    override var isWorking: Boolean = false
    var runnable: Runnable? = null
    val rect = Rect()
    var eventCompleted: OnEventCompleted? = null

    interface OnEventCompleted {
        fun eventCompleted(name: String)
    }

    fun setOnEventCompleted(eventCompleted: OnEventCompleted?) {
        if (this.eventCompleted == null) {
            this.eventCompleted = eventCompleted
        } else {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    fun globalRun(looperBody: () -> Unit, targetBody: () -> Unit) = thread {
        targetBody
        Thread.sleep(2000)
        while (stop() != true) {
            looperBody
        }
    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    fun divDeal() {
//        /*这里的判断需要更多准确*/
//
//        val wait = findViewByText("稍后")
//        val closeApp = findViewByText("关闭应用")
//        val know = findViewByText("知道了")
//        val allow = findViewById("允许")
//        val button = findViewByTextExactly("暂不开启")
//        val cancel = findViewByTextExactly("取消")
//        if (button != null) {
//            clickTargetWithRect(button)
//        }
//        /*这样做才完美嘛!*/
//        if (cancel != null) {
//            if (findViewByText(AppUtils.getAppName()) == null && cancel.viewIdResourceName != "android:id/button2") {
//                clickTargetWithRect(cancel)
//            }
//        }
//        if (wait != null) {
//            clickTargetWithRect(wait)
//        }
//        if (closeApp != null) {
//            clickTargetWithRect(closeApp)
//
//        }
//        if (know != null) {
//            clickTargetWithRect(know)
//
//        }
//        if (allow != null) {
//            clickTargetWithRect(allow)
//        }
//    }

    override fun cancel() {
        App.handler.removeCallbacksAndMessages(null)
    }



    fun stop() = EventController.INSTANCE.getEventTask()?.stop

    fun getPackAgeName() = App.service.rootInActiveWindow?.packageName

    @RequiresApi(Build.VERSION_CODES.P)
    fun runEvent(runnable: Runnable) {
        if (task.job != null) {
            if (!task.job.isCancelled) {
                runTime++
                App.handler.removeCallbacksAndMessages(1)
                App.handler.postDelayed(runnable, 1, 500)
            }
        } else {
            runTime++
            App.handler.removeCallbacksAndMessages(1)
            App.handler.postDelayed(runnable, 1, 500)
        }

    }

    public fun windowInfo(): AccessibilityNodeInfo {
        return App.service.rootInActiveWindow!!
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun runEvent(runnable: Runnable, delay: Float) {
        if (task.job != null) {
            if (!task.job.isCancelled) {
                runTime++
                App.handler.removeCallbacksAndMessages(1)
                App.handler.postDelayed(runnable, 1, (delay * 1000).toLong())
            }
        } else {
            runTime++
            App.handler.removeCallbacksAndMessages(null)
            App.handler.postDelayed(runnable, 1, (delay * 1000).toLong())
        }

    }

    override fun execute(event: AccessibilityEvent?) {
        start(App.service, event)
    }

    /**判断当前控件是否在被用户可见?*/
    fun isVisibleToUser(nodeInfo: AccessibilityNodeInfo?): Boolean {
        if (nodeInfo == null) {
            return false
        }
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        val displayMetrics = App.service.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels //这里需要用到父布局的宽度
        val screenHeight = displayMetrics.heightPixels //这里需要用到父布局的高度。。。需要具体分析/**/
        return rect.left >= 0 && rect.top >= 0 && rect.right <= screenWidth && rect.bottom <= screenHeight
    }


    abstract fun start(service: AccessibilityService, event: AccessibilityEvent?)
    fun back(service: AccessibilityService) {
        runTime++
        Thread.sleep(2000)
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }

    fun back(service: AccessibilityService, time: Long) {
        runTime++
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
        Thread.sleep(time)
    }


//    var x = (ScreenUtils.getAppScreenWidth() / 2).toFloat()//起点x
//    var y = 0f//起点y
//    var perMoveY = 0f //上下每次滑动的距离
//    var perMoveX = 0f //左右每次滑动的距离

//    @SuppressLint("NewApi")
//    fun scrollDownPoint(
//        accessibilityNodeInfo: AccessibilityNodeInfo?, service: AccessibilityService
//    ) {
//        runTime++
//        if (isWorking) {
//            return
//        }
//        val builder = GestureDescription.Builder()
//        val path = Path()
//        if (accessibilityNodeInfo == null) {
//            return
//        }
//        if (accessibilityNodeInfo.parent == null) {
//            return
//        }
//        accessibilityNodeInfo.parent.getBoundsInScreen(rect)
//        y = rect.bottom.toFloat() - App.OFFSET_VALUE
//        if (perMoveY == 0f) {/*设置滑动距离，每一次滑动根据父布局长度的一半来定*/
//            perMoveY = rect.top + App.OFFSET_VALUE
//        }
//        path.moveTo(x, y)
//        path.lineTo(x, perMoveY)
//        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 500))
//        val gesture = builder.build()
//        isWorking = true
//        service.dispatchGesture(
//            gesture,
//            @RequiresApi(Build.VERSION_CODES.N) object :
//                AccessibilityService.GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    L.e("滑动完成")
//                    isWorking = false
//                    super.onCompleted(gestureDescription)
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    L.e("滑动失败")
//                    isWorking = false
//                }
//            },
//            null
//        )
//
//    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    fun scrollViewToTop(
//        accessibilityNodeInfo: AccessibilityNodeInfo?, service: AccessibilityService
//    ) {
//        runTime++
//        if (isWorking) {
//            return
//        }
//        val builder = GestureDescription.Builder()
//        val path = Path()
//        if (accessibilityNodeInfo == null) {
//            return
//        }
//        accessibilityNodeInfo.getBoundsInScreen(rect)
//        y = rect.bottom.toFloat()
//        if (perMoveY == 0f) {/*设置滑动距离，每一次滑动根据父布局长度的一半来定*/
//            perMoveY = rect.top.toFloat()
//        }
//        path.moveTo(x, y - OFFSET_VALUE)
//        path.lineTo(x, perMoveY)
//        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 500))
//        val gesture = builder.build()
//        isWorking = true
//        service.dispatchGesture(
//            gesture,
//            @RequiresApi(Build.VERSION_CODES.N) object :
//                AccessibilityService.GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    L.e("滑动完成")
//                    isWorking = false
//                    super.onCompleted(gestureDescription)
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    L.e("滑动失败")
//                    isWorking = false
//                }
//            },
//            null
//        )
//    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    fun scrollRightPoint(accessibilityNodeInfo: AccessibilityNodeInfo?) {
//        runTime++
//        if (isWorking) {
//            return
//        }
//        val builder = GestureDescription.Builder()
//        val path = Path()
//        if (accessibilityNodeInfo == null) {
//            return
//        }
//        accessibilityNodeInfo.getBoundsInScreen(rect)
//        if (perMoveX == 0f) {/*设置滑动距离，每一次滑动根据父布局长度的一半来定*/
//            perMoveX = rect.left + OFFSET_VALUE
//        }
//        path.moveTo(x, y)
//        path.lineTo(perMoveX, y)
//        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 500))
//        val gesture = builder.build()
//        isWorking = true
//        service.dispatchGesture(
//            gesture,
//            @RequiresApi(Build.VERSION_CODES.N) object :
//                AccessibilityService.GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    L.e("滑动完成")
//                    isWorking = false
//                    super.onCompleted(gestureDescription)
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    L.e("滑动失败")
//                    isWorking = false
//                }
//            },
//            null
//        )
//    }

//    @RequiresApi(Build.VERSION_CODES.N)
//    fun scrollUpPoint(
//        accessibilityNodeInfo: AccessibilityNodeInfo?,
//        service: AccessibilityService,
//    ) {
//        if (accessibilityNodeInfo == null) {
//            return
//        }
//        runTime++
//        if (isWorking) {
//            return
//        }
//        val builder = GestureDescription.Builder()
//        val path = Path()
//        accessibilityNodeInfo.parent.getBoundsInScreen(rect)
//        y = rect.bottom.toFloat() - OFFSET_VALUE
//        if (perMoveY == 0f) {/*设置滑动距离，每一次滑动根据父布局长度的一半来定*/
//            perMoveY = rect.top + OFFSET_VALUE
//        }
//        path.moveTo(x, perMoveY)
//        path.lineTo(x, y)
//        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 500))
//        val gesture = builder.build()
//        isWorking = true
//        service.dispatchGesture(
//            gesture,
//            @RequiresApi(Build.VERSION_CODES.N) object :
//                AccessibilityService.GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    L.e("滑动完成")
//                    isWorking = false
//                    super.onCompleted(gestureDescription)
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    L.e("滑动失败")
//                    isWorking = false
//                }
//            },
//            null
//        )
//
//    }


//    @RequiresApi(Build.VERSION_CODES.N)
//    fun scrollUpPointOfHalf(
//        accessibilityNodeInfo: AccessibilityNodeInfo?,
//        service: AccessibilityService,
//    ) {
//        if (accessibilityNodeInfo == null) {
//            return
//        }
//        runTime++
//        if (isWorking) {
//            return
//        }
//        val builder = GestureDescription.Builder()
//        val path = Path()
//        accessibilityNodeInfo.getBoundsInScreen(rect)
//        y = (rect.bottom.toFloat() + rect.top.toFloat()) / 2
//        perMoveY = y + OFFSET_VALUE * 5
//        path.moveTo(x, y)
//        path.lineTo(x, perMoveY)
//        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 500))
//        val gesture = builder.build()
//        isWorking = true
//        service.dispatchGesture(
//            gesture,
//            @RequiresApi(Build.VERSION_CODES.N) object :
//                AccessibilityService.GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    L.e("滑动完成")
//                    isWorking = false
//                    super.onCompleted(gestureDescription)
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    L.e("滑动失败")
//                    isWorking = false
//                }
//            },
//            null
//        )
//        Thread.sleep(2000)
//
//
//    }

//    @SuppressLint("NewApi")
//    fun scrollDownPointOfHalf(
//        accessibilityNodeInfo: AccessibilityNodeInfo?,
//        service: AccessibilityService,
//    ) {
//        runTime++
//        if (isWorking) {
//            return
//        }
//        val builder = GestureDescription.Builder()
//        val path = Path()
//        if (accessibilityNodeInfo == null) {
//            return
//        }
//
//        accessibilityNodeInfo.getBoundsInScreen(rect)
//        y = (rect.bottom.toFloat() + rect.top.toFloat()) / 2
//        perMoveY = y - OFFSET_VALUE * 5
//        path.moveTo(x, y)
//        path.lineTo(x, perMoveY)
//        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 500))
//        val gesture = builder.build()
//        isWorking = true
//        service.dispatchGesture(
//            gesture,
//            @RequiresApi(Build.VERSION_CODES.N) object :
//                AccessibilityService.GestureResultCallback() {
//                override fun onCompleted(gestureDescription: GestureDescription?) {
//                    L.e("滑动完成")
//                    isWorking = false
//                    super.onCompleted(gestureDescription)
//                }
//
//                override fun onCancelled(gestureDescription: GestureDescription?) {
//                    super.onCancelled(gestureDescription)
//                    L.e("滑动失败")
//                    isWorking = false
//                }
//            },
//            null
//        )
//        while (isWorking) {
//            L.e("正在滑动")
//        }
//        Thread.sleep(2000)
//
//    }

    @SuppressLint("NewApi")
    fun clickPoint(x: Float, y: Float, service: AccessibilityService) {
        runTime++
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x, y)
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 100))
        val gesture = builder.build()
        isWorking = true
        service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)/*出现失败，可能是第三方正在占用，重新进行一次*/
                    isWorking = false
                }
            },
            null
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun clickPoint(service: AccessibilityService) {
        runTime++
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        val x = ((rect.right + rect.left) / 2).toFloat()
        val y = ((rect.bottom + rect.top) / 2).toFloat()
        path.moveTo(x, y)
        path.lineTo(x, y)
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 100))
        val gesture = builder.build()
        isWorking = true
        service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    isWorking = false
                }
            },
            null
        )
    }

    fun findViewById(id: String): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByViewId(id)
        if (target?.isNotEmpty() == true) {
            return target[0]
        }
        return null
    }

    fun findViewById(id: String, index: Int): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByViewId(id)
        if (target?.isNotEmpty() == true) {
            if (index == -1) {
                return target[target.size + index]
            }
            return target[index]
        }
        return null
    }

    fun findViewById(id: String, parent: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        val target = parent?.findAccessibilityNodeInfosByViewId(id)
        return if (target?.isNotEmpty() == true) {
            target[0]
        } else {
            null
        }
    }

    fun findViewByIdOrText(strings: List<String>): AccessibilityNodeInfo? {
        for (item in strings) {
            val target = findViewById(item)
            if (target != null) {
                return target
            }
        }
        return null
    }

    fun findViewByText(text: String, parent: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        val target = parent?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            return target[0]
        }
        return null
    }

    fun findViewByText(text: String): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            return target[0]
        }
        return null
    }

    fun findViewByTextExactly(text: String): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                if (text == item?.text?.trim()) {
                    return item
                }
            }
        }
        return null
    }

    fun findViewByIndex(
        accessibilityNodeInfo: AccessibilityNodeInfo?,
        index: Int
    ): AccessibilityNodeInfo? {
        return accessibilityNodeInfo?.getChild(index)
    }

    fun findViewByTextExactly(array: Array<String>): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        for (item in array) {
            val target = rootNodeInfo?.findAccessibilityNodeInfosByText(item)
            if (target?.isNotEmpty() == true) {
                for (child in target) {
                    if (item == child.text.trim()) {
                        return child
                    }
                }
            }
        }
        return null
    }

    fun findViewByTextExactly(text: String, id: String): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                if (!item.text.isNullOrEmpty() && !item.viewIdResourceName.isNullOrEmpty()) {
                    if (text == item?.text && id == item.viewIdResourceName) {
                        return item
                    }
                }

            }
        }
        return null
    }
    fun findViewVisibilityUser(text: String,id:String):AccessibilityNodeInfo?{
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                if (!item.text.isNullOrEmpty() && !item.viewIdResourceName.isNullOrEmpty()) {
                    if (text == item?.text && id == item.viewIdResourceName) {
                        if (item.isVisibleToUser){
                            return item
                        }
                    }
                }

            }
        }
        return null
    }

    fun findViewVisibilityUser(text: String):AccessibilityNodeInfo?{
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                if (text == item?.text?.trim()) {
                    if (item.isVisibleToUser){
                        return item
                    }
                }
            }
        }
        return null
    }



    fun findViewByText(text: String, type: String, index: Int): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                if (item.className == type) {
                    return item
                }
            }
        }
        return null
    }

    fun findViewByText(list: List<String>): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        for (item in list) {
            val target = rootNodeInfo?.findAccessibilityNodeInfosByText(item)
            if (target?.isNotEmpty() == true) {
                return target[0]
            }
        }
        return null
    }

    fun findViewByText(list: List<String>, id: String): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        for (item in list) {
            val targetList = rootNodeInfo?.findAccessibilityNodeInfosByText(item)
            if (targetList?.isNotEmpty() == true) {
                val target = targetList[0]
                if (target.viewIdResourceName == id) {
                    return target
                }
            }
        }
        return null
    }

    fun findViewByText(text: String, id: Array<String>): AccessibilityNodeInfo? {
        val rootNodeInfo = App.service.rootInActiveWindow
        val target = rootNodeInfo?.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                for (idItem in id) {
                    if (item.viewIdResourceName == idItem) {
                        return item
                    }
                }
            }

        }
        return null
    }

    fun findViewByText(
        text: String, id: Array<String>, accessibilityNodeInfo: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {
        val target = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text)
        if (target?.isNotEmpty() == true) {
            for (item in target) {
                for (idItem in id) {
                    if (item.viewIdResourceName == idItem) {
                        return item
                    }
                }
            }

        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun clickTargetWithRect(accessibilityNodeInfo: AccessibilityNodeInfo?) {
        if (accessibilityNodeInfo == null) {
            return
        }
        runTime++
        accessibilityNodeInfo.getBoundsInScreen(rect)
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        val x = ((rect.right + rect.left) / 2).toFloat()
        val y = ((rect.bottom + rect.top) / 2).toFloat()
        path.moveTo(x, y)
        path.lineTo(x, y)/*无效的path需要废弃*/
        if (x < 0 || y < 0) {
            return
        }
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 100))
        val gesture = builder.build()
        isWorking = true
        App.service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    isWorking = false
                }
            },
            null
        )

        Thread.sleep(2000)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun clickTargetWithRect(accessibilityNodeInfo: AccessibilityNodeInfo?, time: Long) {
        if (accessibilityNodeInfo == null) {
            return
        }
        runTime++
        accessibilityNodeInfo.getBoundsInScreen(rect)
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        val x = ((rect.right + rect.left) / 2).toFloat()
        val y = ((rect.bottom + rect.top) / 2).toFloat()
        path.moveTo(x, y)
        path.lineTo(x, y)/*无效的path需要废弃*/
        if (x < 0 || y < 0) {
            return
        }
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 100))
        val gesture = builder.build()
        isWorking = true
        App.service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    isWorking = false
                }
            },
            null
        )

        Thread.sleep(time)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun clickTargetEndWithRect(accessibilityNodeInfo: AccessibilityNodeInfo?) {
        if (accessibilityNodeInfo == null) {
            return
        }
        runTime++
        accessibilityNodeInfo.getBoundsInScreen(rect)
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        val x = (rect.right - 10).toFloat()
        val y = ((rect.bottom + rect.top) / 2).toFloat()
        path.moveTo(x, y)
        path.lineTo(x, y)/*无效的path需要废弃*/
        if (x < 0 || y < 0) {
            return
        }
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 100))
        val gesture = builder.build()
        isWorking = true
        App.service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    isWorking = false
                }
            },
            null
        )
        Thread.sleep(2000)

    }

    fun clickTargetWithPerform(accessibilityNodeInfo: AccessibilityNodeInfo?) {
        if (accessibilityNodeInfo == null) {
            return
        }
        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        Thread.sleep(2000)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun scroll(x1: Int, y1: Int, x2: Int, y2: Int) {
        runTime++
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(x1.toFloat(), y1.toFloat())
        path.lineTo(x2.toFloat(), y2.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, 100))
        val gesture = builder.build()
        isWorking = true
        App.service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    isWorking = false
                }
            },
            null
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun scroll(x1: Int, y1: Int, x2: Int, y2: Int,duration:Long) {
        runTime++
        if (isWorking) {
            return
        }
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(x1.toFloat(), y1.toFloat())
        path.lineTo(x2.toFloat(), y2.toFloat())
        builder.addStroke(GestureDescription.StrokeDescription(path, 500, duration))
        val gesture = builder.build()
        isWorking = true
        App.service.dispatchGesture(
            gesture,
            @RequiresApi(Build.VERSION_CODES.N) object :
                AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    isWorking = false/*当点击完成之后，根据上一个业务进行*/
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    isWorking = false
                }
            },
            null
        )
    }
}


