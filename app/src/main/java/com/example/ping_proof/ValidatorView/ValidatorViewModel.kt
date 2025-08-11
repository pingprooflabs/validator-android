package com.example.ping_proof.ValidatorView

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ping_proof.APIUtils.ApiClient
import com.example.ping_proof.GlobalToast
import com.example.ping_proof.PingProofEntryScreen.UserDetails
import com.example.ping_proof.PreferenceManger
import com.example.ping_proof.MQTTBackgroundService
import kotlinx.coroutines.launch

class ValidatorViewModel(userDetails: UserDetails): ViewModel() {
    val userDetails: UserDetails = userDetails

    fun isVpnActive(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // For API 23 and above (recommended way)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val activeNetwork: Network? = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
                if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    return true
                }
            }
        } else {
            // For older devices
            val networks = connectivityManager.allNetworks
            for (network in networks) {
                val caps = connectivityManager.getNetworkCapabilities(network)
                if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    return true
                }
            }
        }
        return false
    }

    fun startValidating(context: Context) {

        if(isVpnActive(context)) {
            GlobalToast.show("Please Disconnect from VPN to start validation")
            Log.e("MQTTService", "Failed to startForeground becuase ")
            return
        }
        viewModelScope.launch {
            try {
               val result = ApiClient.startValidating(userDetails.userId)
                PreferenceManger.setIsValidating(status = true)
                val intent = Intent(context, MQTTBackgroundService::class.java)
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                GlobalToast.show("Error in Starting Validation")
                Log.e("WalletConnect", "error is $e")
            }
        }
    }

    fun stopValidating(context: Context) {
        viewModelScope.launch {
            try {
                val stopIntent = Intent(context, MQTTBackgroundService::class.java).apply {
                    action = "STOP_VALIDATOR"
                }
                context.startService(stopIntent)
            } catch (e: Exception) {
                GlobalToast.show("Error in Stoping Validation")
                Log.e("WalletConnect", "error is $e")
            }
        }
    }
}