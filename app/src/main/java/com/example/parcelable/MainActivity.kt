package com.example.parcelable

import android.content.Intent
import android.content.IntentFilter
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.parcelable.ui.theme.ParcelableTheme

class MainActivity : ComponentActivity() {

    val chargeReceiver = OnChargeReceiver()
    val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(
            chargeReceiver,
            filter
        )

        enableEdgeToEdge()
        setContent {
            ParcelableTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    sendStartData()
                    Text(
                        text = "Последний старт приложения: ${
                            LogWorkManager.getStartDataFromPrefsAndLoggIt(
                                context = applicationContext
                            )
                        }"
                    )
                    Text(
                        text = "Последний выход из приложения: ${
                            LogWorkManager.getEndDataFromPrefsAndLoggIt(
                                context = applicationContext
                            )
                        }"
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sendEndData()
    }

    private fun sendEndData() {
        val request = OneTimeWorkRequestBuilder<LogWorkManager>()
            .setInputData(
                workDataOf(
                    DATE_APP_END_KEY to Calendar.getInstance().time.toString(),
                )
            ).build()
        WorkManager.getInstance(applicationContext).enqueue(request)
    }

    private fun sendStartData() {
        val request = OneTimeWorkRequestBuilder<LogWorkManager>()
            .setInputData(
                workDataOf(
                    DATE_APP_START_KEY to Calendar.getInstance().time.toString(),
                )
            ).build()
        WorkManager.getInstance(applicationContext).enqueue(request)
    }

    override fun onDestroy() {
        unregisterReceiver(chargeReceiver)
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParcelableTheme {
        Greeting("Android")
    }
}

