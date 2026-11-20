package com.kodex.guide.ui

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import dagger.hilt.android.HiltAndroidApp
import android.util.Log
import com.kodex.guide.BuildConfig

import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        val factory = if (BuildConfig.DEBUG) {
            Log.d("AppCheck", "Using DEBUG provider")
            DebugAppCheckProviderFactory.getInstance()
        } else {
            Log.d("AppCheck", "Using PLAY INTEGRITY provider")
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }

        Firebase.appCheck.installAppCheckProviderFactory(factory)

        if (BuildConfig.DEBUG) {
            Firebase.appCheck.getAppCheckToken(false)
                .addOnSuccessListener { Log.d("AppCheck", "Token OK: ${it.token}") }
                .addOnFailureListener { Log.e("AppCheck", "Token FAIL", it) }
        }
    }
    }

