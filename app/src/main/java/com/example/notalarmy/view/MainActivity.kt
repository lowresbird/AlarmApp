package com.example.notalarmy.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notalarmy.R
import com.example.notalarmy.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MainActivity : AppCompatActivity() {
    private lateinit var alarmTimePicker: TimePicker
    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        alarmTimePicker = findViewById(R.id.alarmTimePicker)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
    }

    fun onToggleClicked(view: View) {
        val toggle = view as ToggleButton

        if (toggle.isChecked) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarmTimePicker.hour)
                set(Calendar.MINUTE, alarmTimePicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val intent = Intent(this, AlarmReceiver::class.java)

            pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            var time = calendar.timeInMillis
            if (System.currentTimeMillis() > time) {
                time += 24 * 60 * 60 * 1000
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, time, 60_000L, pendingIntent
            )

            Toast.makeText(this, "Alarm on, set for ${timeFormatter.format(calendar.time)}", Toast.LENGTH_SHORT).show()
        } else {
            alarmManager.cancel(pendingIntent)
            Toast.makeText(this, "Alarm off", Toast.LENGTH_SHORT).show()
        }
    }
}