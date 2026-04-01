package com.themaelo.colddown

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import android.widget.Toast



class BlockAppAccessibilityService : AccessibilityService() {

    // tag para logs
    private val tag = this::class.java.simpleName

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private var stateOverlay = false
    //private val myPackageName by lazy { applicationContext.packageName }
    private val tiktokPackageName = "musically"
    private var lastPackage : String? = null

    private lateinit var windowManager : WindowManager
    private lateinit var overLayView : View
    private lateinit var params : WindowManager.LayoutParams

    /**
     * Se ejecuta cuando el sistema conecta correctamente este servicio.
     * Aquí se pueden inicializar configuraciones o estado del servicio.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        initializeOverlay()
        logToastnotifyServiceConnected()
    }


    /**
     * Callback que recibe eventos del sistema según la configuración del servicio.
     * Aquí detectamos qué aplicación se abre.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        if(event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED )return
        val packageName = event.packageName?.toString() ?: return

        if (packageName == lastPackage) return
        lastPackage = packageName

        logEventInfo(event)

        handleAppBlocking(packageName)
    }


    private fun initializeOverlay(){
        createOverlayView()
        setupoOverlayView()
    }

    private fun createOverlayView(){
        val parent = FrameLayout(this)
        overLayView = LayoutInflater.from(this)
            .inflate(R.layout.overlay_block, parent, false)

        overlaySetOnTouchListener()
    }

    private fun setupoOverlayView(){
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
    }

    private fun handleAppBlocking(packageName: String){
        if (!packageName.contains(tiktokPackageName)) return
        performGlobalAction(GLOBAL_ACTION_HOME)

        showOverlay()
    }

    private fun showOverlay() {
        if (stateOverlay) return

        try {
            windowManager.addView(overLayView, params)
            stateOverlay = true
            logStateOverlay()

            removeOverlayWithDelay()
        } catch (e: Exception) {
            Log.e(tag, "Error al agregar overlay", e)
        }
    }

    private fun removeOverlayWithDelay() {
        handler.removeCallbacksAndMessages(null)

        handler.postDelayed({
            removeOverlay()
        }, 5000)
    }

    private fun overlaySetOnTouchListener(){
        overLayView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()

                removeOverlay()
            }
            true
        }
    }

    private fun removeOverlay(){
        if (stateOverlay) {
            try {
                windowManager.removeView(overLayView)
                stateOverlay = false
                logStateOverlay()
            } catch (e: Exception) {
                Log.e(tag, "Error al remover overlay", e)
            }
        }
    }

    private fun logToastnotifyServiceConnected(){
        Toast.makeText(this, "servicio encendido", Toast.LENGTH_LONG).show()

        Log.d(tag, "                       ")
        Log.d(tag, "+++++++++++++++++++++++")
        Log.d(tag, "Servicio conectado correctamente")
        Log.d(tag, "+++++++++++++++++++++++")
        Log.d(tag, "                       ")
    }

    private fun logEventInfo(event: AccessibilityEvent){
        Log.d(tag, "Evento de cambio de pantalla")
        Log.d(tag, "Cambio de pantalla por${event.packageName} ")
        Log.d(tag, "+++++++++++++++++++++++")
        Log.d(tag, "                       ")
    }
    private fun logStateOverlay(){
        if (stateOverlay) {
            Log.d(tag, "overlay mostrado")
            Log.d(tag, "+++++++++++++++++++++++")
            Log.d(tag, "                       ")
        }else{
            Log.d(tag, "overlay quitado")
            Log.d(tag, "+++++++++++++++++++++++")
            Log.d(tag, "                       ")
        }
    }



    /**
     * Llamado cuando el sistema interrumpe el servicio.
     */
    override fun onInterrupt() {
        Log.d(tag, "Servicio interrumpido")
    }
}
