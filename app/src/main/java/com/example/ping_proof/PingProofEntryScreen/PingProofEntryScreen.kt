package com.example.ping_proof.PingProofEntryScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ping_proof.APIUtils.AllPayments
import com.example.ping_proof.AppColor
import com.example.ping_proof.ConnectToWalletScreen.ConnectToWalletView
import com.example.ping_proof.ConnectToWalletScreen.ConnectToWalletViewModel
import com.example.ping_proof.PreferenceManger
import com.example.ping_proof.ValidatorView.ValidatorView
import com.example.ping_proof.ValidatorView.ValidatorViewModel
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.publickey.SolanaPublicKey

data class UserDetails(
    var userId: String,
    var pubKey: SolanaPublicKey?,
    var isValidating: Boolean = false,
    var totalValidations: Int = 0,
    var listOfAllPayments: List<AllPayments>
)

@Composable
fun PingProofEntryScreen(viewModel: PingProofEntryModel, sender: ActivityResultSender) {
    val userDetails by PreferenceManger.validatorDetails.collectAsState()
    Box(modifier = Modifier.fillMaxSize().background(AppColor.background), contentAlignment = Alignment.Center) {
        //MARK: Add Validator ID logic below
        if (userDetails.pubKey != null && !userDetails.userId.isEmpty()) {
            val validatorVm = ValidatorViewModel(userDetails)
            ValidatorView(validatorVm)
        } else {
            ConnectToWalletView(
                viewModel = ConnectToWalletViewModel(),
                sender = sender
            )
        }
    }
}

@Composable
fun AppButton(onClick: () -> Unit, text: String, loading: Boolean = false) {
    TextButton(onClick = onClick) {
        Box(modifier = Modifier.background(AppColor.buttonBackGround).padding(20.dp), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.Red,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(text, color = Color.White, fontSize = 24.sp)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun EntryScreenPreview() {
//    PingProofEntryScreen(PingProofEntryModel())
//}