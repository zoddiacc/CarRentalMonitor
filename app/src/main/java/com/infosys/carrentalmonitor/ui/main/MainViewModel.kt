package com.infosys.carrentalmonitor.ui.main

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infosys.carrentalmonitor.data.repository.SpeedRepository
import com.infosys.carrentalmonitor.service.SpeedTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val speedRepository: SpeedRepository
) : ViewModel() {

    private val _isTracking = mutableStateOf(false)
    val isTracking: State<Boolean> = _isTracking

    private val _speedLimit = mutableStateOf<Double?>(null)
    val speedLimit: State<Double?> = _speedLimit

    init {
        fetchSpeedLimit()
    }

    fun toggleTracking(context: Context) {
        val intent = Intent(context, SpeedTrackingService::class.java)
        if (_isTracking.value) context.stopService(intent)
        else context.startService(intent)
        _isTracking.value = !_isTracking.value
    }

    private fun fetchSpeedLimit() {
        viewModelScope.launch {
            speedRepository.getUserSpeedLimit { speed ->
                _speedLimit.value = speed
            }
        }
    }
}