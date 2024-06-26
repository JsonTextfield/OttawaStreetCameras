package com.textfield.json.ottawastreetcameras.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.textfield.json.ottawastreetcameras.ui.screens.MainScreen
import com.textfield.json.ottawastreetcameras.ui.theme.AppTheme
import com.textfield.json.ottawastreetcameras.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels { MainViewModel.MainViewModelFactory }
        setContent { AppTheme { MainScreen(mainViewModel) } }
    }
}