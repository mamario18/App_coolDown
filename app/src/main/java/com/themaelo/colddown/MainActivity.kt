package com.themaelo.colddown

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
        } else{
            checkAccessibilitySettings()
        }
    }

    /**Inicia el servicio (solo el usuario puede encender el servicio)*/
    fun checkAccessibilitySettings() {
        Log.d(TAG, "is accessibility service enabled : ${isAccessibilityServiceEnabled()}")
        if (!isAccessibilityServiceEnabled()) {
            Toast.makeText(this, "Necesitamos encender el servicio", Toast.LENGTH_LONG).show()
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

    fun openBatteryOptimizationScreen(){
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        startActivity(intent)
    }

    fun openAutoStartScreen(){
        try {
            val intent = Intent().apply {
                component = android.content.ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No disponible en este dispositivo", Toast.LENGTH_SHORT).show()
        }
    }



    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {

        Column(
            modifier = modifier.fillMaxSize()
        ) {

            // 🔷 Caja superior (1/3)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Hello $name!")
            }

            // 🔶 Caja inferior (2/3)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {

                // 🧠 Grid de botones (2 filas x 2 columnas)
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { openOverlayPermissionScreen() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Overlay")
                        }

                        Button(
                            onClick = { openAccessibilityServiceScreen() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Accesibilidad")
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { openBatteryOptimizationScreen() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Optimización batería")
                        }

                        Button(
                            onClick = { openAutoStartScreen() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Inicio automático")
                        }
                    }
                }
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