package com.example.embeddedsygicid.sygic

import android.app.Activity
import android.util.Log
import com.sygic.aura.embedded.IApiCallback
import com.sygic.sdk.api.events.ApiEvents

private const val TAG = "SygicNaviCallback"

class SygicNaviCallback(private val mActivity: Activity) : IApiCallback {

    override fun onEvent(event: Int, data: String?) {
        if (event == ApiEvents.EVENT_APP_EXIT) {
            Log.d(TAG, "Sygic app exit")
            mActivity.finish()
        }
    }

    override fun onServiceConnected() {
        Log.d(TAG, "Service connected")
    }

    override fun onServiceDisconnected() {
        Log.d(TAG, "Service disconnected")
    }
}