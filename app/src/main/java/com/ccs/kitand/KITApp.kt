package com.ccs.kitand

import android.app.Application
import android.content.Context

class KITApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var greeting = "Hello from KITApp"
        var context: Context? = null
            private set
        var instance: KITApp? = null
            private set
    }
}