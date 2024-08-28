package com.example.parcelable

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf


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

fun saveStartDataToPrefs(context: Context, data: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(DATE_APP_START_KEY_PREFS, data).apply()
}

fun saveEndDataToPrefs(context: Context, data: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(DATE_APP_END_KEY_PREFS, data).apply()
}