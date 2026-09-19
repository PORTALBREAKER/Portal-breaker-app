package com.example.data.remote

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseHelper {
    private const val TAG = "FirebaseHelper"

    fun ensureInitialized(context: Context): FirebaseApp? {
        return try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                apps[0]
            } else {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:289538537180:android:3c0fc14d5a00a1f5766619")
                    .setProjectId("portal-breaker")
                    .setApiKey("AIzaSyDj5hgOZgwILwttKNNAIDrYpJK3h10CKVs")
                    .setStorageBucket("portal-breaker.firebasestorage.app")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization warning: ${e.message}")
            null
        }
    }
}
