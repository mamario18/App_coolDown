package com.themaelo.colddown

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.Toast

class BlockAppAccessibilityService : AccessibilityService() {

    private val tag = this::class.java.simpleName

    private val tiktokPackageName = "com.zhiliaoapp.musically"
    private var lastPackage: String? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    val SECOND = 1000L

    private var allowedTime = 15000L // default
    private var coolDown = 15000L    // default
    private var timeUsingTiktok = 0L

    private var isOverlayShow = false
    private var shouldBlockTikTok = false
    private var isTiktokTimerRunning = false
    private var isCooldownRunning = false

    private lateinit var tiktokTimer: CountDownTimer

    private lateinit var windowManager: WindowManager
    private lateinit var overLayView: View
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var eventPackageName: String

    // ----------------------------------
    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        loadSettings()                 // 🔹 cargar configuración editable
        initializeCountDownTimer()
        initializeOverlay()
        logToastnotifyServiceConnected()
        startFailsafe()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        eventPackageName = event.packageName?.toString() ?: return
        if (eventPackageName == lastPackage) return
        lastPackage = eventPackageName

        logEventInfo(event)
        handleAppBlocking(eventPackageName)
    }

    override fun onInterrupt() {
        Log.d(tag, "Servicio interrumpido")
    }

    // ----------------------------------
    private fun handleAppBlocking(packageName: String) {
        if (packageName != tiktokPackageName) return
        if (shouldBlockTikTok) blockTiktok()
        else if (!isTiktokTimerRunning) {
            tiktokTimer.start()
            isTiktokTimerRunning = true
        }
    }

    private fun blockTiktok() {
        performGlobalAction(GLOBAL_ACTION_HOME)
        showOverlay()
        if (!isCooldownRunning) startCoolDown()
    }

    private fun startCoolDown() {
        isCooldownRunning = true
        handler.postDelayed({
            shouldBlockTikTok = false
            isCooldownRunning = false
        }, coolDown)
    }

    private fun initializeCountDownTimer() {
        tiktokTimer = object : CountDownTimer(allowedTime, SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                timeUsingTiktok = allowedTime - millisUntilFinished
            }

            override fun onFinish() {
                shouldBlockTikTok = true
                isTiktokTimerRunning = false
                timeUsingTiktok = 0L
                blockTiktok()
            }
        }
    }

    private fun stopTikTokTimer() {
        if (isTiktokTimerRunning) {
            tiktokTimer.cancel()
            isTiktokTimerRunning = false
            timeUsingTiktok = 0L
        }
    }

    // ----------------------------------
    private fun initializeOverlay() {
        createOverlayView()
        setupOverlayView()
    }

    private fun createOverlayView() {
        val parent = FrameLayout(this)
        overLayView = LayoutInflater.from(this)
            .inflate(R.layout.overlay_block, parent, false)
        removeOverlayWithOnTouch()
    }

    private fun setupOverlayView() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
    }

    private fun showOverlay() {
        if (isOverlayShow) return
        try {
            windowManager.addView(overLayView, params)
            isOverlayShow = true
            logStateOverlay()
            removeOverlayWithDelay()
        } catch (e: Exception) {
            Log.e(tag, "Error al agregar overlay", e)
        }
    }

    private fun removeOverlayWithDelay() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ removeOverlay() }, 5000)
    }

    private fun removeOverlayWithOnTouch() {
        overLayView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
                removeOverlay()
            }
            true
        }
    }

    private fun removeOverlay() {
        if (!isOverlayShow) return
        try {
            windowManager.removeView(overLayView)
            isOverlayShow = false
            logStateOverlay()
        } catch (e: Exception) {
            Log.e(tag, "Error al remover overlay (failsafe)", e)
            isOverlayShow = false
        }
    }

    // ----------------------------------
    private fun startFailsafe() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isOverlayShow) {
                    removeOverlay()
                    Log.w(tag, "Failsafe activado: overlay removido automáticamente")
                }
                stopTikTokTimer()
                handler.postDelayed(this, 10000L)
            }
        }, 10000L)
    }

    // ----------------------------------
    private fun loadSettings() {
        val prefs = getSharedPreferences("TikTokBlockPrefs", MODE_PRIVATE)
        allowedTime = prefs.getLong("allowedTime", 15000L)
        coolDown = prefs.getLong("coolDown", 15000L)
    }

    // ----------------------------------
    private fun logToastnotifyServiceConnected() {
        Toast.makeText(this, "servicio encendido", Toast.LENGTH_LONG).show()
        Log.d(tag, "Servicio conectado correctamente")
    }

    private fun logEventInfo(event: AccessibilityEvent) {
        Log.d(tag, "Cambio de pantalla por ${event.packageName}")
    }

    private fun logStateOverlay() {
        if (isOverlayShow) Log.d(tag, "overlay mostrado")
        else Log.d(tag, "overlay quitado")
    }
}