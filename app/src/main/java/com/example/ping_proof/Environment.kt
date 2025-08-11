package com.example.ping_proof

/**
 *
 *
 * **/
enum class Environment(
    val baseUrl: String,
    val MQTT_BROKER_URL: String,
    val MQTT_USERNAME: String,
    val MQTT_PASSWORD: String,
) {
    DEV(
        baseUrl = "",
        MQTT_BROKER_URL = "",
        MQTT_USERNAME = "",
        MQTT_PASSWORD = "",
    ),
    PROD(
        baseUrl = "",
        MQTT_BROKER_URL = "",
        MQTT_USERNAME = "",
        MQTT_PASSWORD = "",
    );
}
