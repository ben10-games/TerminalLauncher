package com.Ben10.terminallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.Ben10.terminallauncher.ui.theme.TerminalLauncherTheme

/**
 * App entry point. All terminal UI is implemented in TerminalScreen.kt
 * (TerminalHomeScreen and its sub-composables) — this file stays a
 * thin shell around the Activity lifecycle and Scaffold setup.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TerminalLauncherTheme {
                // containerColor is forced to black here rather than relying on
                // the theme's background, since Theme.kt uses dynamic color on
                // Android 12+, which would make the background follow the
                // system wallpaper instead of staying pure black (#000000).
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Black
                ) { innerPadding ->
                    TerminalHomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
