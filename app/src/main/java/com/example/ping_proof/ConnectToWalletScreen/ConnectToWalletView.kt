package com.example.ping_proof.ConnectToWalletScreen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.ping_proof.PingProofEntryScreen.AppButton
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

@Composable
fun ConnectToWalletView(viewModel: ConnectToWalletViewModel,sender: ActivityResultSender) {
    AppButton(onClick = {
        viewModel.initiateWalletConnection(sender = sender)
    }, text = "Connect To Wallet")
}