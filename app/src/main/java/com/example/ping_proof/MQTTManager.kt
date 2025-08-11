package com.example.ping_proof

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ping_proof.APIUtils.ApiClient
import com.example.ping_proof.APIUtils.StatusoFPing
import com.example.ping_proof.APIUtils.TaskResponse
import com.example.ping_proof.APIUtils.URLTasks
import com.example.ping_proof.PingProofEntryScreen.UserDetails
import com.example.ping_proof.PingServices.PingClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.util.UUID

object MQTTManager {
    private val MQTT_BROKER_URL = PreferenceManger.runningEnv.MQTT_BROKER_URL
    private val CLIENT_ID = "PingProofCalidator_" + UUID.randomUUID().toString()
    private lateinit var mqttClient: MqttClient
    private lateinit var appContext: Context


    fun init(userDetails: UserDetails, context: Context) {
        appContext = context
        try {
            mqttClient = MqttClient(MQTT_BROKER_URL, CLIENT_ID, null)

            val options = MqttConnectOptions().apply {
                isAutomaticReconnect = true          // ✅ Critical for background stability
                isCleanSession = false               // ✅ Keeps subscriptions alive
                connectionTimeout = 10               // ✅ Reasonable for mobile
                keepAliveInterval = 20               // ✅ Ping every 20s
                userName = PreferenceManger.runningEnv.MQTT_USERNAME
                password = PreferenceManger.runningEnv.MQTT_PASSWORD.toCharArray()
            }

            mqttClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.i("WalletConnect", "MQTT Message: ${message.toString()} from topic: $topic")
                    handleCallBack(message)
                }

                override fun connectionLost(cause: Throwable?) {
                    val stopIntent = Intent(appContext, MQTTBackgroundService::class.java).apply {
                        action = "STOP_VALIDATOR"
                    }
                    appContext.startService(stopIntent)
                    Log.e("WalletConnect", "MQTT Connection lost: ${cause?.message}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.i("WalletConnect"," Delivery complete")
                }
            })

            mqttClient.connect(options)
            Log.i("WalletConnect","✅ MQTT connected")

            // 🔔 Now subscribe immediately after connection
            subscribe(userDetails.userId) // Use your actual topic

        } catch (e: MqttException) {
            e.printStackTrace()
            Log.e("WalletConnect", "❌ MQTT connect failed: ${e.reasonCode} - ${e.message}")
        }
    }


    private fun subscribe(validatorId: String) {
        val topic = "/validator/$validatorId"
        try {
            mqttClient.subscribe(topic, 1)
            Log.i("WalletConnect", "Successfully Subscribed to MQTT")
        } catch (e: Exception) {
            Log.e("WalletConnect", "Failed to connect to the the topic $topic, $e")
        }
    }

   private fun handleCallBack(message: MqttMessage?) {
        try {
            val payloadString = message.toString()
            val taskRequest = Gson().fromJson(payloadString, TaskResponse::class.java)
            Log.d("WalletConnect", "Received Task ID: ${taskRequest.taskid}")
            CoroutineScope(Dispatchers.IO).launch {
                val updatedTasks = pingUrlsConcurrently(urlTasks = taskRequest.url_tasks)
                val updatedTaskrequest = taskRequest.copy(url_tasks = updatedTasks)
                val taskPayLoad = Gson().toJson(updatedTaskrequest)
                val message = MqttMessage()
                message.payload = taskPayLoad.toByteArray()
                message.qos = 1
                mqttClient.publish("/validator/results", message)
                delay(10_000)
                val results = ApiClient.getCount(PreferenceManger.getUserID())
                PreferenceManger.setNumberOfValidations(results.count)
            }
        } catch (e: Exception) {
            Log.e("WalletConnect", "Error parsing message: ${e.message}", e)
        }
    }

    suspend fun pingUrlsConcurrently(urlTasks: List<URLTasks>): List<URLTasks> {
        return coroutineScope {
            urlTasks.map { task ->
                async {
                    val start = System.currentTimeMillis()
                    try {
                        val response = PingClient.pingUrl(task.endpoint.url)
                        val latency = System.currentTimeMillis() - start

                        task.copy(
                            status = if (response.isSuccessful) StatusoFPing.UP else StatusoFPing.DOWN,
                            latency = if (response.isSuccessful) latency.toDouble() else null,
                            validatedAt = start
                        )
                    } catch (e: Exception) {
                        task.copy(status = StatusoFPing.DOWN, latency = null)
                    }
                }
            }
        }.awaitAll()
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (e: Exception) {
            Log.d("WalletConnect", "Error in Disconecting ${e}")
        }

    }
}