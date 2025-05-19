package com.infosys.carrentalmonitor.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class SpeedRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun getUserSpeedLimit(onResult: (Double?) -> Unit) {
        val user = auth.currentUser

        Log.d("SpeedRepo", "Fetching speed limit for user: ${user?.uid}")

        if (user != null) {
            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener {
                    Log.d("SpeedRepo", "Document fetched: ${it.data}")
                    onResult(it.getDouble("maxSpeed"))
                }
                .addOnFailureListener {
                    Log.e("SpeedRepo", "Failed to fetch document: ${it.message}")
                    onResult(null)
                }
        } else {
            Log.w("SpeedRepo", "User is null. Signing in anonymously...")
            auth.signInAnonymously()
                .addOnSuccessListener {
                    getUserSpeedLimit(onResult)
                }
                .addOnFailureListener {
                    Log.e("SpeedRepo", "Anonymous sign-in failed: ${it.message}")
                    onResult(null)
                }
        }
    }
}
