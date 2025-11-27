// PaymentsCompactListUpdated.kt
package com.yourorg.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.ping_proof.APIUtils.AllPayments
import com.example.ping_proof.AppColor
import com.example.ping_proof.getTxClusterURL

/**
 * Updated compact payments list with improved colors and visible ping badge.
 * Place this below your existing UI and pass List<AllPayments>.
 */
@Composable
fun PaymentsCompactList(
    payments: List<AllPayments>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboard: ClipboardManager = LocalClipboardManager.current

    // Colors tuned for better contrast (use your app theme if desired)
    val background = Color.Transparent
    val cardColor = Color(0xFF0F2936)         // slightly lighter than screen bg
    val signatureColor = Color(0xFFF6FBFF)    // almost white for signature
    val subtitleColor = Color(0xFF9AA6B0)     // muted subtitle
    val iconTint = Color(0xFFB7C2CC)          // icon tint
    val badgeBg = AppColor.background           // pale pink badge background (high contrast)
    val badgeNumberColor = Color.White  // dark pink/purple for number (high contrast)
    val badgeTextColor = Color.LightGray    // darker grey for "pings" label

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Recent Payments",
                style = MaterialTheme.typography.titleMedium,
                color = signatureColor,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Total: ${payments.sumOf { it.totalPingCount }}",
                style = MaterialTheme.typography.bodyMedium,
                color = subtitleColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (payments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No payments yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 340.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(payments) { payment ->
                PaymentCompactRowUpdated(
                    signature = payment.transactionSignature,
                    totalPingCount = payment.totalPingCount,
                    onCopy = {
                        clipboard.setText(AnnotatedString(payment.transactionSignature))
                        Toast.makeText(context, "Signature copied", Toast.LENGTH_SHORT).show()
                    },
                    onOpen = {
                        val url = getTxClusterURL(payment.transactionSignature)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    cardColor = cardColor,
                    signatureColor = signatureColor,
                    subtitleColor = subtitleColor,
                    iconTint = iconTint,
                    badgeBg = badgeBg,
                    badgeNumberColor = badgeNumberColor,
                    badgeTextColor = badgeTextColor
                )
            }
        }
    }
}

@Composable
private fun PaymentCompactRowUpdated(
    signature: String,
    totalPingCount: Int,
    onCopy: () -> Unit,
    onOpen: () -> Unit,
    cardColor: Color,
    signatureColor: Color,
    subtitleColor: Color,
    iconTint: Color,
    badgeBg: Color,
    badgeNumberColor: Color,
    badgeTextColor: Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = shortSig(signature),
                        color = signatureColor,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { expanded = !expanded }
                            .semantics { contentDescription = "Transaction signature" },
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(onClick = onOpen, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Filled.OpenInNew,
                            contentDescription = "Open in explorer",
                            tint = iconTint
                        )
                    }

                    IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy signature",
                            tint = iconTint
                        )
                    }
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = signature,
                        color = subtitleColor,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.semantics { contentDescription = "Full transaction signature" }
                    )
                }
            }

            // ping badge — improved visibility: pale bg + dark number
            Surface(
                color = badgeBg,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .defaultMinSize(minWidth = 64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$totalPingCount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = badgeNumberColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (totalPingCount == 1) "ping" else "pings",
                        style = MaterialTheme.typography.bodySmall,
                        color = badgeTextColor
                    )
                }
            }
        }
    }
}

private fun shortSig(sig: String, keep: Int = 6): String {
    return if (sig.length > keep * 2) "${sig.take(keep)}...${sig.takeLast(keep)}" else sig
}

@Preview(showBackground = true)
@Composable
private fun PaymentsCompactListUpdatedPreview() {
    val sample = listOf(
        AllPayments(
            transactionSignature = "2LPN333PVJwhRM82jVpMdUn8wzFyhhSZu1bExampleOneVeryLongSig",
            totalPingCount = 38
        ),
        AllPayments(
            transactionSignature = "oFxGUHVsxRdjh1GcnSHKb5HH8CR55ii8QKExampleTwo",
            totalPingCount = 10
        )
    )

    MaterialTheme {
        PaymentsCompactList(payments = sample, modifier = Modifier.fillMaxWidth())
    }
}
