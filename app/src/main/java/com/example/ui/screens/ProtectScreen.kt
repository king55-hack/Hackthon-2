package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.theme.*
import com.example.ui.components.EmergencyActionPanel
import com.example.ui.components.GoogleMapView

@Composable
fun ProtectScreen(
    phase: Phase,
    lang: Language,
    myStatus: String,
    checklist: Triple<Boolean, Boolean, Boolean>,
    nearestShelter: FacilityItem?,
    userCoords: Pair<Double, Double>,
    familyPhone: String?,
    onSetSafeStatus: () -> Unit,
    onSetNotSafeStatus: () -> Unit,
    onToggleChecklist: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDisaster = phase == Phase.HASSAI
    val scrollState = rememberScrollState()

    val shelterName = nearestShelter?.name ?: Localization.t("shName1", lang)
    val shelterLat = nearestShelter?.lat ?: 35.6558
    val shelterLon = nearestShelter?.lon ?: 139.7431
    val shelterDistKm = nearestShelter?.distKm ?: 0.35

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Evacuation Judgment Banner Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isDisaster) RedAlertSoft else GreenOkSoft
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = if (isDisaster) RedAlert else GreenOk,
                    shape = RoundedCornerShape(12.dp)
                )
                .testTag("evac_judgment_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDisaster) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isDisaster) RedAlert else GreenOk,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isDisaster) Localization.t("judgeGo", lang) else Localization.t("judgeStay", lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDisaster) RedAlertDark else GreenOk
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isDisaster) Localization.t("judgeGoS", lang) else Localization.t("judgeStayS", lang),
                    fontSize = 13.sp,
                    color = Ink
                )
            }
        }

        // Prominent Emergency Action Panel (1. I'm Safe / 2. I'm Not Safe / Emergency Calls / Location Share)
        EmergencyActionPanel(
            lang = lang,
            myStatus = myStatus,
            userCoords = userCoords,
            familyPhone = familyPhone,
            onSetSafeStatus = onSetSafeStatus,
            onSetNotSafeStatus = onSetNotSafeStatus
        )

        // Google Maps Interactive Navigation View (Replaces basic canvas with full Google Maps)
        GoogleMapView(
            lat = shelterLat,
            lon = shelterLon,
            title = shelterName,
            distKm = shelterDistKm,
            lang = lang
        )

        // Action Checklist Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("action_checklist_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.t("actions", lang),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )

                Spacer(modifier = Modifier.height(10.dp))

                ChecklistItemRow(
                    checked = checklist.first,
                    label = Localization.t("act1", lang),
                    onToggle = { onToggleChecklist(1) },
                    testTag = "checklist_item_1"
                )

                HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 4.dp))

                ChecklistItemRow(
                    checked = checklist.second,
                    label = Localization.t("act2", lang),
                    onToggle = { onToggleChecklist(2) },
                    testTag = "checklist_item_2"
                )

                HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 4.dp))

                ChecklistItemRow(
                    checked = checklist.third,
                    label = Localization.t("act3", lang),
                    onToggle = { onToggleChecklist(3) },
                    testTag = "checklist_item_3"
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun ChecklistItemRow(
    checked: Boolean,
    label: String,
    onToggle: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = TealAccent,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 13.5.sp,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
            color = if (checked) TealAccent else Ink
        )
    }
}
