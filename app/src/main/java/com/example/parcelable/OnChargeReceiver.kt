package com.example.parcelable

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver

@SuppressLint("RestrictedApi")
class OnChargeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_POWER_CONNECTED) {
            Toast.makeText(context, "Устройство подключено к зарядке", Toast.LENGTH_SHORT).show()
        }
        if (intent?.action == Intent.ACTION_POWER_DISCONNECTED) {
            Toast.makeText(context, "Устройство отключено от зарядки", Toast.LENGTH_SHORT).show()
        }
    }
}