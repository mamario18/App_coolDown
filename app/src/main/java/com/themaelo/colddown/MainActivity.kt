package com.themaelo.colddown

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.themaelo.colddown.ui.theme.ColddownTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColddownTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Mi App") }
                        )
                    }) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkOverlayPermission()
    }

    /**revisa si la app tiene permisos de overLay e
     * Inicia el servicio (solo el usuario puede encender el servicio)*/
    fun checkOverlayPermission() {
        val hasOverLayPermission = Settings.canDrawOverlays(this)

        Log.d(TAG,"has overLay permission : $hasOverLayPermission")
        if (!hasOverLayPermission) {
            Toast.makeText(this, "Necesitamos permiso de overlay", Toast.LENGTH_LONG).show()
            openOverlayPermissionScreen()
        } else{
            checkAccessibilitySettings()
        }
    }

    /**Inicia el servicio (solo el usuario puede encender el servicio)*/
    fun checkAccessibilitySettings() {
        Log.d(TAG, "is accessibility service enabled : ${isAccessibilityServiceEnabled()}")
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Necesitamos encender el servicio", Toast.LENGTH_LONG).show()
            openAccessibilityServiceScreen()

        } else {
            Toast.makeText(this, "servicio encendido", Toast.LENGTH_LONG).show()
        }
    }

    /**Revisa si la app tiene permiso de accesibilidad*/
    fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName = packageName + "/" + BlockAppAccessibilityService::class.java.canonicalName

        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(serviceName) == true
    }

    fun openOverlayPermissionScreen(){
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivity(intent)
    }

    fun openAccessibilityServiceScreen(){
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }



    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {

            // Texto centrado (opcional, puedes moverlo si quieres)
            Text(
                text = "Hello $name!",
                modifier = Modifier.align(Alignment.Center)
            )

            // Botón en esquina inferior derecha
            Button(
                onClick = { openOverlayPermissionScreen() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("dar permiso de overLay")
            }
            Button(
                onClick = { openAccessibilityServiceScreen() },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
            ) {
                Text("activar el servicio")
            }
        }
    }





    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(
        showBackground = true,
        device = "spec:width=411dp,height=891dp"
    )
    @Composable
    fun AppPreview() {
        ColddownTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text("Mi App") }
                    )
                }
            ) { innerPadding ->
                Greeting(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}