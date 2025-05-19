package com.infosys.carrentalmonitor.presentation.auth

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PhoneAuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneAuthUiState())
    val uiState: StateFlow<PhoneAuthUiState> = _uiState.asStateFlow()

    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _uiState.update { it.copy(isLoading = true) }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    storedVerificationId = verificationId
                    resendToken = token
                    _uiState.update { it.copy(codeSent = true, isLoading = false) }
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        _uiState.update { it.copy(isLoading = true) }
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = task.isSuccessful,
                        errorMessage = task.exception?.message
                    )
                }
            }
    }
}

data class PhoneAuthUiState(
    val isLoading: Boolean = false,
    val codeSent: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
