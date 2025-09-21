package com.example.ping_proof

import com.solana.mobilewalletadapter.clientlib.RpcCluster

/**
 * Default environment configuration.
 *
 * ⚠️ Do not commit real secrets here.
 * Use only empty strings or Devnet/test values in git.
 * Update locally if you need to run against real infra.
 */
enum class Environment(
    val baseUrl: String,
    val MQTT_BROKER_URL: String,
    val MQTT_USERNAME: String,
    val MQTT_PASSWORD: String,
    val SOLANA_CLUSTER: RpcCluster,
) {
    DEV(
        baseUrl = "",
        MQTT_BROKER_URL = "",
        MQTT_USERNAME = "",
        MQTT_PASSWORD = "",
        SOLANA_CLUSTER = RpcCluster.Devnet
    ),
    PROD(
        baseUrl = "",
        MQTT_BROKER_URL = "",
        MQTT_USERNAME = "",
        MQTT_PASSWORD = "",
        SOLANA_CLUSTER = RpcCluster.Devnet
    );
}