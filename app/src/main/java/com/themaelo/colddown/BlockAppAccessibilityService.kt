package com.themaelo.colddown

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp

class BlockAppAccessibilityService : AccessibilityService() {

    // TAG para logs
    private val TAG = this::class.java.simpleName


    private val tiktokPackageName = "com.zhiliaoapp.musically"

    private var isOverlayVisible : Boolean = false

    private var windowsManager : WindowManager? = null
    private var overLayView : View? = null
    private var params : WindowManager.LayoutParams? = null





    /**
     * Se ejecuta cuando el sistema conecta el AccessibilityService.
     * Aquí se pueden inicializar configuraciones o estado del servicio.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Servicio conectado correctamente")
        windowsManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overLayView = ComposeView(this).apply {
            setContent {
                OverlayScreen()
            }
        }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            0,
            PixelFormat.TRANSLUCENT
        )
    }


    /**
     * Callback que recibe eventos del sistema según la configuración del servicio.
     * Aquí detectamos qué aplicación se abre.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, "se detecto un evento: ${event?.eventType} paquete: ${event?.packageName}")

        if(event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED )return
        Log.d(TAG, "el evento es de tipo cambio de pantalla")

        val packageName = event.packageName?.toString() ?: return

        if (packageName == tiktokPackageName) {
            if (!isOverlayVisible) {
                showOverlay()
                isOverlayVisible = true
            }
        } else {
            if (isOverlayVisible) {
                hideOverlay()
                isOverlayVisible = false
            }
        }
    }


    fun showOverlay(){
        if (overLayView?.isAttachedToWindow == true) return
        windowsManager?.addView(overLayView, params)
    }

    fun hideOverlay() {
        if (overLayView?.isAttachedToWindow == true) {
            windowsManager?.removeView(overLayView)
        }
    }


    @Composable
    fun OverlayScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA000000)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🚫 MODO ESTUDIO ACTIVADO",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }

    /**
     * Llamado cuando el sistema interrumpe el servicio.
     */
    override fun onInterrupt() {
        Log.d(TAG, "Servicio interrumpido")
    }
}