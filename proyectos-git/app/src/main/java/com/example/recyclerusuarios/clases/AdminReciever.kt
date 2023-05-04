package com.example.recyclerusuarios.clases

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.recyclerusuarios.MainActivity


class AdminReciever : DeviceAdminReceiver()

class MyDeviceAdmin(context: Context) {
    private val mDpm: DevicePolicyManager
    private val mAdminName: ComponentName

    init {
        mDpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mAdminName = ComponentName(context, AdminReciever::class.java)
    }

    fun enableKioskMode(context: Context, activityClass: Class<*>) {
        if (mDpm.isDeviceOwnerApp(context.getPackageName())) {
            // Set the app as a kiosk app
            mDpm.setLockTaskPackages(mAdminName, arrayOf(context.getPackageName()))
            // Start the kiosk activity
            startKioskActivity(context, activityClass)
        } else {
            Log.e(TAG, "Not a device owner app")
        }
    }

    private fun startKioskActivity(context: Context, activityClass: Class<*>) {
        val intent = Intent(context, activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun disableKioskMode(context: Context) {
        if (mDpm.isLockTaskPermitted(context.getPackageName())) {
            // Stop the kiosk activity
            stopKioskActivity(context)
            // Clear the kiosk app
            mDpm.setLockTaskPackages(mAdminName, arrayOfNulls(0))
        }
    }

    private fun stopKioskActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }
}
