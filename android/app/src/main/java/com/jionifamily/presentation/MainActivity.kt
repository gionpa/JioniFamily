package com.jionifamily.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jionifamily.presentation.navigation.JioniNavGraph
import com.jionifamily.presentation.theme.CreamWhite
import com.jionifamily.presentation.theme.JioniFamilyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JioniFamilyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CreamWhite,
                ) {
                    val navController = rememberNavController()
                    JioniNavGraph(navController = navController)
                }
            }
        }
    }
}
