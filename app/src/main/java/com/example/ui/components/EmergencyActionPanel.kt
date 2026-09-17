package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Localization
import com.example.theme.*

@Composable
fun EmergencyActionPanel(
    lang: Language,
    myStatus: String,
    userCoords: Pair<Double, Double>,
    familyPhone: String?,
    onSetSafeStatus: () -> Unit,
    onSetNotSafeStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Pulsing animation for Not Safe button when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, LineBorder, RoundedCornerShape(14.dp))
            .testTag("emergency_action_panel")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (myStatus == "help") RedAlert else if (myStatus == "safe") GreenOk else Amber)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1. I'm Safe / 2. Not Safe",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (myStatus) {
                        "safe" -> GreenOkSoft
                        "help" -> RedAlertSoft
                        else -> Amber.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = when (myStatus) {
                            "safe" -> Localization.t("im_safe_short", lang)
                            "help" -> Localization.t("im_not_safe_short", lang)
                            else -> Localization.t("statusUnknown", lang)
                        },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (myStatus) {
                            "safe" -> GreenOk
                            "help" -> RedAlert
                            else -> Amber
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two Big Primary Action Buttons: 1. I'M SAFE / 2. I'M NOT SAFE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: I'M SAFE (Green)
                Button(
                    onClick = onSetSafeStatus,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (myStatus == "safe") GreenOk else GreenOk.copy(alpha = 0.88f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .testTag("btn_im_safe")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "1. I'm Safe",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = Localization.t("im_safe", lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }

                // Button 2: I'M NOT SAFE / NEED HELP (Red)
                Button(
                    onClick = onSetNotSafeStatus,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedAlert
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .then(if (myStatus == "help") Modifier.scale(pulseScale) else Modifier)
                        .testTag("btn_im_not_safe")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "2. Not Safe",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = Localization.t("im_not_safe_short", lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = LineBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Emergency Call & Hotline Row
            Text(
                text = Localization.t("emergency_call_title", lang),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Muted
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call 119 (Ambulance / Fire)
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RedAlert.copy(alpha = 0.6f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_119_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = RedAlert,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "119",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedAlert
                    )
                }

                // Call 110 (Police)
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:110"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Navy.copy(alpha = 0.6f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_110_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = null,
                        tint = Navy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "110",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy
                    )
                }

                // Speed Dial Family Contact
                if (!familyPhone.isNullOrBlank()) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$familyPhone"))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("call_family_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = Localization.t("call_family", lang),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealAccent,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Share Live Location Button
            Button(
                onClick = {
                    val lat = userCoords.first
                    val lon = userCoords.second
                    val statusLabel = if (myStatus == "help") Localization.t("im_not_safe", lang) else Localization.t("im_safe", lang)
                    val shareText = "【SONAERU SOS】$statusLabel\n" +
                            "${Localization.t("shareMsgTemplate", lang)} https://maps.google.com/?q=$lat,$lon"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("emergency_share_loc_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ShareLocation,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.t("share_live_location", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
