package com.example.ping_proof.ConnectToWalletScreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ping_proof.APIUtils.ApiClient
import com.example.ping_proof.Environment
import com.example.ping_proof.GlobalToast
import com.example.ping_proof.PreferenceManger
import com.example.ping_proof.getEnvironment

import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.RpcCluster
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import com.solana.publickey.SolanaPublicKey
import kotlinx.coroutines.launch
val identityUri = "https://pingproof.io"
val iconUri = "favicon.ico"
val identityName = "PingProof"

class ConnectToWalletViewModel: ViewModel() {

    fun initiateWalletConnection(sender: ActivityResultSender) {
        val walletAdapter = MobileWalletAdapter(connectionIdentity = ConnectionIdentity(
            identityUri = identityUri.toUri(),
            iconUri = iconUri.toUri(),
            identityName = identityName,
        ))
        Log.e("Initiate", "Wallet connection initiated")
        walletAdapter.rpcCluster = getEnvironment().SOLANA_CLUSTER
        viewModelScope.launch {
            try {
                val result = walletAdapter.connect(sender)
                when(result) {
                    is TransactionResult.Success -> {
                        Log.e("Wallet connection", "Success in wallet connection")
                        val bytePublicKey = result.authResult.publicKey
                        val publickey = SolanaPublicKey(bytePublicKey)
                        val registerApiResult = ApiClient.registerWallet(publickey)
                        val validatorId = registerApiResult.message
                        val getcountofAddress = ApiClient.getCount(validatorId)
                        val getAllPayments = ApiClient.getAllTransactions(validatorId)
                        PreferenceManger.setWalletAddress(publickey.base58())
                        PreferenceManger.setUserID(registerApiResult.message)
                        PreferenceManger.setNumberOfValidations(getcountofAddress.count)
                        PreferenceManger.setAllPayments(getAllPayments)
                    }
                    is TransactionResult.Failure -> {
                        GlobalToast.show("Wallet Connection and Failed")
                        Log.e("WalletConnect", "Connection failed $result")
                    }
                    is TransactionResult.NoWalletFound -> {
                        GlobalToast.show("No Wallet Found")
                        Log.e("WalletConnect", "No Wallet Found $result")
                    }
                }
            } catch (e: Exception) {
                GlobalToast.show("Error in Connecting to Wallet")
                Log.e("WalletConnect", "exception $e")
            }
        }
    }

    private fun openPhantomWallet(context: Context) {
        Log.i("WalletConnect", "Initiating wallet connection")
    }
}