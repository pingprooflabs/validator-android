package com.example.ping_proof
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ping_proof.APIUtils.ApiClient
import kotlinx.coroutines.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.widget.Toast


class MQTTBackgroundService : Service() {

    private var validationCount = 0
    private var serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onBind(intent: Intent?): IBinder? = null


    override fun onCreate() {
        super.onCreate()
        Log.d("MQTTService", "Service created")

        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PingProof::MQTTWakeLock")
            wakeLock.acquire()
            val initialCount = PreferenceManger.getNumberOfValidations()
            val notification = createNotification("Starting...", initialCount)

            // ✅ Must be called IMMEDIATELY and SUCCESSFULLY
            startForeground(1, notification)
            Log.d("MQTTService", "Foreground notification started")
        } catch (e: Exception) {
            Log.e("MQTTService", "Failed to startForeground: ${e.message}", e)
            GlobalToast.show("Failed to start validation")
            stopSelf() // gracefully exit if something failed
            return
        }

        // Only now do the heavier work
        try {
            val userDetails = PreferenceManger.getUserDetails()
            MQTTManager.init(userDetails, applicationContext)
            startNotificationUpdater()
        } catch (e: Exception) {
            GlobalToast.show("Failed to Connect")
            Log.e("MQTTService", "Startup logic failed: ${e.message}")
        }
    }


    private fun startNotificationUpdater() {
        serviceScope.launch {
            while (true) {
                delay(10_000)
                validationCount = PreferenceManger.getNumberOfValidations()
                updateNotification("Running", validationCount)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_VALIDATOR") {
            serviceScope.launch {
                val userDetails = PreferenceManger.getUserDetails()
                val result = ApiClient.stopValidating(userDetails.userId)
                PreferenceManger.setIsValidating(status = false)
                MQTTManager.disconnect()
                withContext(Dispatchers.Main) { stopSelf() }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (::wakeLock.isInitialized && wakeLock.isHeld) {
            wakeLock.release()
        }
        MQTTManager.disconnect()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun createNotification(status: String, count: Int): Notification {
        val channelId = "pingproof_validator_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "PingProof Validator",
                NotificationManager.IMPORTANCE_LOW
            ).apply{
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                enableLights(true)
                enableVibration(true)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, MQTTBackgroundService::class.java).apply {
            action = "STOP_VALIDATOR"
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("PingProof Validator")
            .setContentText("$status • Validations: $count")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_stop,
                    "Stop",
                    stopPendingIntent
                ).build()
            )
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(status: String, count: Int) {
        val notification = createNotification(status, count)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
}
