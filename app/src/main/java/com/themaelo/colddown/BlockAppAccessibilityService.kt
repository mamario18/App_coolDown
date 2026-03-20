package com.themaelo.colddown

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class OpenApplicationDetectionService : AccessibilityService() {

    // TAG para logs
    private val TAG = this::class.java.simpleName

    val tiktokPackageName = "com.zhiliaoapp.musically"

    /**
     * Se ejecuta cuando el sistema conecta el AccessibilityService.
     * Aquí se pueden inicializar configuraciones o estado del servicio.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Servicio conectado correctamente XD")
    }

    /**
     * Callback que recibe eventos del sistema según la configuración del servicio.
     * Aquí detectamos qué aplicación se abre.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, "se detecto un evento")

        if(event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ){
            Log.d(TAG, "el evento es de tipo cambio de pantalla")

            val packageName = event?.packageName

            if (packageName == tiktokPackageName) {
                Log.d(TAG, "TikTok fue abierto")
                Toast.makeText(this, "TikTok fue abierto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Llamado cuando el sistema interrumpe el servicio.
     */
    override fun onInterrupt() {
        Log.d(TAG, "Servicio interrumpido")
    }
}