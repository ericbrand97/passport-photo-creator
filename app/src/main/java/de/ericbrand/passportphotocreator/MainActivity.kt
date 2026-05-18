package de.ericbrand.passportphotocreator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.ericbrand.passportphotocreator.app.PassportPhotoApp
import de.ericbrand.passportphotocreator.ui.theme.PassportPhotoCreatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PassportPhotoCreatorTheme() {
                PassportPhotoApp()
            }
        }
    }
}