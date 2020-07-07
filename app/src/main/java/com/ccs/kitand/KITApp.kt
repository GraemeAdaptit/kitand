package com.ccs.kitand

import android.app.Application

class KITApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var greeting = "Hello from KITApp"

        var instance: KITApp? = null
            private set
    }
}