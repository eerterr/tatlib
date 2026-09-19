package com.tatlib.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tatlib.app.ui.TatlibApp
import com.tatlib.app.ui.theme.TatlibTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TatlibTheme {
                TatlibApp()
            }
        }
    }
}
