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
                    .setApplicationId(context.packageName)
                    .setProjectId("portal-breaker-novel")
                    .setApiKey("AIzaSyFakeKeySafeInitPortalBreaker9912")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization warning: ${e.message}")
            null
        }
    }
}
