package com.example.ping_proof

import android.app.Application

class PingProof : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceManger.init(this)
    }
}