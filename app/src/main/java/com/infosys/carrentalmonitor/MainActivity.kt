package com.infosys.carrentalmonitor.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.infosys.carrentalmonitor.presentation.auth.PhoneAuthScreen
import com.infosys.carrentalmonitor.ui.theme.CarRentalMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarRentalMonitorTheme {
                Surface {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "phone_auth") {
                        composable("phone_auth") {
                            PhoneAuthScreen(navController)
                        }
                        composable("main") {
                            SpeedScreen()
                        }
                    }
                }
            }
        }
    }
}
