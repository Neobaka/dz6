package com.example.dz6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.dz6.presentation.PROFILE_GRAPH
import com.example.dz6.presentation.profileGraph
import com.example.dz6.ui.theme.Dz6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dz6Theme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = PROFILE_GRAPH
                ) {
                    profileGraph(navController)
                }
            }
        }
    }
}