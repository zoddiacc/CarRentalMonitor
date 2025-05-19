package com.infosys.carrentalmonitor.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SpeedScreen(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val isTracking by viewModel.isTracking
    val speedLimit by viewModel.speedLimit

    Surface(modifier = Modifier.fillMaxSize()) {
        when (speedLimit) {
            null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Speed Limit: $speedLimit km/h")
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { viewModel.toggleTracking(context) }) {
                        Text(if (isTracking) "Stop Tracking" else "Start Tracking")
                    }
                }
            }
        }
    }
}
