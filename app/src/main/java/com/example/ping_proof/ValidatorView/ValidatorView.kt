package com.example.ping_proof.ValidatorView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ping_proof.PingProofEntryScreen.AppButton
import com.example.ping_proof.PreferenceManger

@Composable
fun ValidatorView(viewModel: ValidatorViewModel) {
        val context = LocalContext.current
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AppButton(onClick = {
                        if (viewModel.userDetails.isValidating) {
                                viewModel.stopValidating(context = context)
                        } else {
                                viewModel.startValidating(context = context)
                        }
                }, text = ( if(viewModel.userDetails.isValidating) "Stop Validating" else "Start Validating"), loading = viewModel.userDetails.isValidating)

                AppButton(onClick = {
                        PreferenceManger.setWalletAddress("")
                        PreferenceManger.setUserID("")
                }, text = "Disconnect Wallet")

                Text("Total Validations: ${viewModel.userDetails.totalValidations}", color = Color.White, fontSize = 24.sp)
       }

}