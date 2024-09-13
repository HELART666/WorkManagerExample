package com.example.parcelable

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


const val PREFS_NAME = "PREFS_NAME"
const val DATE_APP_START_KEY = "DATE_APP_START_KEY"
const val DATE_APP_START_KEY_PREFS = "DATE_APP_START_KEY_PREFS"
const val DATE_APP_END_KEY = "DATE_APP_END_KEY"
const val DATE_APP_END_KEY_PREFS = "DATE_APP_END_KEY_PREFS"
const val SUCCESS_DATE_APP_START_KEY = "SUCCESS_DATE_APP_START_KEY"
const val SUCCESS_DATE_APP_END_KEY = "SUCCESS_DATE_APP_END_KEY"

class LogWorkManager(
    private val appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(
    appContext = appContext,
    params = params
) {
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        val startTime = params.inputData.getString(DATE_APP_START_KEY)
        val endTime = params.inputData.getString(DATE_APP_END_KEY)

        startTime?.let {
            saveStartDataToPrefs(context = appContext, data = it)
            Log.d("WORKMANAGER_DATA", "START_DATA_SAVE_TO_PREFS $it")
        }

        endTime?.let {
            saveEndDataToPrefs(context = appContext, data = it)
            Log.d("WORKMANAGER_DATA", "END_DATA_SAVE_TO_PREFS $it")
        }

        if (isAppInForeground(appContext)) {
            getStartDataFromPrefsAndLoggIt(appContext)?.let {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        appContext,
                        "Последний запуск был $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            getEndDataFromPrefsAndLoggIt(appContext)?.let {
                createAndShowNotification(appContext, it)
            }
        }

        return Result.Success(
            workDataOf(
                SUCCESS_DATE_APP_START_KEY to startTime,
                SUCCESS_DATE_APP_END_KEY to endTime
            )
        )
    }

    companion object {
        fun getStartDataFromPrefsAndLoggIt(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val data = prefs.getString(DATE_APP_START_KEY_PREFS, "dataDefault")
            Log.d("WORKMANAGER_DATA", "ПОСЛЕДНИЙ ЗАПУСК ${data.toString()}")
            return data
        }

        fun getEndDataFromPrefsAndLoggIt(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val data = prefs.getString(DATE_APP_END_KEY_PREFS, "dataDefault")
            Log.d("WORKMANAGER_DATA", "ПОСЛЕДНИЙ ВЫХОД ${data.toString()}")
            return data
        }
    }
}

/**
 * Проверка на то, запущено ли приложение или находится в фоне
 */
private fun isAppInForeground(context: Context): Boolean {
    return (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.runningAppProcesses
        ?.any { it.processName == context.packageName && it.importance == IMPORTANCE_FOREGROUND }
        ?: false
}

private fun saveStartDataToPrefs(context: Context, data: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(DATE_APP_START_KEY_PREFS, data).apply()
}

private fun saveEndDataToPrefs(context: Context, data: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(DATE_APP_END_KEY_PREFS, data).apply()
}

private fun createAndShowNotification(context: Context, endDate: String) {
    val channelId = "NOTIFICATION_CHANNEL_ID"
    val channelName = "Periodic push"

    val channel =
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Уведомление о выходе из приложения")
        .setContentText("Дата последнего выхода из приложения: $endDate")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setAutoCancel(true)

    val notification = notificationBuilder.build()
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(1, notification)
}