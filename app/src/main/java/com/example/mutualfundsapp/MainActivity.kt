package com.example.mutualfundsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mutualfundsapp.navigation.MainScreen
import com.example.mutualfundsapp.ui.theme.MutualFundsTheme
import com.example.mutualfundsapp.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            MutualFundsTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}

