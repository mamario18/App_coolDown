package com.themaelo.colddown

import android.accessibilityservice.AccessibilityService
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class AppBlockService : AccessibilityService() {

    private val packageNameTiktok: String = "com.zhiliaoapp.musically"

    private var overlayView: android.view.View? = null

    override fun onAccessibilityEvent( events: AccessibilityEvent): Unit{
        when(events.eventType){
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = events.packageName.toString()// lo hago con val por que creo que la variable vive un ciclo y se vuelve a crear
                if(packageName == packageNameTiktok){//hasta aqui se que ya detecte a tiktok
                    showOverLay()//

                }
            }
        }
    }


    override fun onInterrupt(): Unit{}

    fun showOverLay(){
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        if(overlayView != null)return
        //ok arriba le estoy pidiendo que me deje controlar los servicios de las ventanas pero
        //ahora aqui deberia hacer un intent para lanzar la ventana que quiero mostrar encima de tiktok
    }
}