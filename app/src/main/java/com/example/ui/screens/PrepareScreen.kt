package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.theme.*

@Composable
fun PrepareScreen(
    lang: Language,
    isRestocked: Boolean,
    quizIndex: Int,
    evacPlan: EvacPlan?,
    isPlanEditing: Boolean,
    contacts: List<Contact>,
    nearestShelterName: String,
    onRestock: () -> Unit,
    onAnswerQuiz: (Boolean) -> Unit,
    onSetPlanEditing: (Boolean) -> Unit,
    onSavePlan: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var editMeetingPoint by remember(evacPlan, isPlanEditing) { mutableStateOf(evacPlan?.meetingPoint ?: "") }
    var editContactName by remember(evacPlan, isPlanEditing) { mutableStateOf(evacPlan?.contactName ?: "") }
    var editContactPhone by remember(evacPlan, isPlanEditing) { mutableStateOf(evacPlan?.contactPhone ?: "") }
    var editNotes by remember(evacPlan, isPlanEditing) { mutableStateOf(evacPlan?.notes ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Flood Risk Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("prepare_flood_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.t("riskH", lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Amber.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = Localization.t("riskBadge", lang),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Amber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = Localization.t("riskV", lang),
                    fontSize = 13.sp,
                    color = Muted
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://disaportal.gsi.go.jp/"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealAccent),
                    modifier = Modifier.fillMaxWidth().testTag("prepare_hazard_btn")
                ) {
                    Text(text = Localization.t("seeMap", lang), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Rolling Stock Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("rolling_stock_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.t("stock", lang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = TealAccent, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Item 1: Rice
                StockItemRow(
                    name = Localization.t("stockRice", lang),
                    amount = "12 ${Localization.t("unitMeal", lang)}",
                    isWarning = !isRestocked,
                    badgeText = if (!isRestocked) Localization.t("expSoon", lang) else Localization.t("enough", lang)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = LineBorder)

                // Item 2: Water
                StockItemRow(
                    name = Localization.t("stockWater", lang),
                    amount = "6 ${Localization.t("unitBottle", lang)}",
                    isWarning = false,
                    badgeText = Localization.t("enough", lang)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = LineBorder)

                // Item 3: Battery
                StockItemRow(
                    name = Localization.t("stockLight", lang),
                    amount = "2 ${Localization.t("unitPc", lang)}",
                    isWarning = false,
                    badgeText = Localization.t("enough", lang)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onRestock,
                    enabled = !isRestocked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealAccent,
                        disabledContainerColor = LineBorder
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restock_action_btn")
                ) {
                    Text(
                        text = if (isRestocked) Localization.t("stockUpdated", lang) else Localization.t("useReplace", lang),
                        fontWeight = FontWeight.Bold,
                        color = if (isRestocked) Muted else Color.White
                    )
                }
            }
        }

        // Disaster Quiz Card
        val quizList = Localization.QUIZZES[lang] ?: emptyList()
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("disaster_quiz_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.t("quizT", lang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Amber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+10 pt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Amber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (quizIndex < quizList.size) {
                    val currentQuiz = quizList[quizIndex]
                    Text(
                        text = "Q${quizIndex + 1}. ${currentQuiz.q}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    currentQuiz.opts.forEachIndexed { optIndex, optText ->
                        OutlinedButton(
                            onClick = {
                                onAnswerQuiz(optIndex == currentQuiz.correct)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("quiz_opt_$optIndex")
                        ) {
                            Text(
                                text = "${optIndex + 1}. $optText",
                                fontSize = 13.sp,
                                color = Ink
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenOk, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = Localization.t("quizAllDone", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenOk
                        )
                    }
                }
            }
        }

        // Family Evacuation Plan Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("evac_plan_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.t("plan", lang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    if (evacPlan != null && !isPlanEditing) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GreenOkSoft
                        ) {
                            Text(
                                text = Localization.t("planDone", lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenOk,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isPlanEditing || evacPlan == null) {
                    // Edit / Create Form
                    Text(text = Localization.t("meetingPoint", lang), fontSize = 12.sp, color = Muted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editMeetingPoint,
                        onValueChange = { editMeetingPoint = it },
                        singleLine = true,
                        placeholder = { Text("例: 港区立赤羽小学校 体育館") },
                        modifier = Modifier.fillMaxWidth().testTag("plan_meeting_point_input")
                    )

                    // Quick-fill nearest shelter button
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = { editMeetingPoint = nearestShelterName },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${Localization.t("useNearestShelter", lang)} ($nearestShelterName)",
                            fontSize = 12.sp,
                            color = TealAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = Localization.t("fieldName", lang), fontSize = 12.sp, color = Muted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editContactName,
                        onValueChange = { editContactName = it },
                        singleLine = true,
                        placeholder = { Text("例: 母 / 父 / 田中") },
                        modifier = Modifier.fillMaxWidth().testTag("plan_contact_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = Localization.t("outContact", lang), fontSize = 12.sp, color = Muted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editContactPhone,
                        onValueChange = { editContactPhone = it },
                        singleLine = true,
                        placeholder = { Text("090-0000-0000") },
                        modifier = Modifier.fillMaxWidth().testTag("plan_contact_phone_input")
                    )

                    if (contacts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("連絡先から選択:", fontSize = 11.sp, color = Muted)
                            contacts.take(3).forEach { c ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = TealAccentSoft,
                                    modifier = Modifier.clickable {
                                        editContactName = c.name
                                        editContactPhone = c.phone
                                    }
                                ) {
                                    Text(
                                        text = c.name,
                                        fontSize = 11.sp,
                                        color = TealAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = Localization.t("planNotes", lang), fontSize = 12.sp, color = Muted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        maxLines = 3,
                        placeholder = { Text("例: 災害用伝言ダイヤル171も使用する") },
                        modifier = Modifier.fillMaxWidth().testTag("plan_notes_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (evacPlan != null) {
                            OutlinedButton(
                                onClick = { onSetPlanEditing(false) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(Localization.t("backBtn", lang))
                            }
                        }
                        Button(
                            onClick = {
                                onSavePlan(editMeetingPoint, editContactName, editContactPhone, editNotes)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            modifier = Modifier.weight(1f).testTag("plan_save_btn")
                        ) {
                            Text(Localization.t("saveContact", lang), fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Display View
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PlanDetailRow(Localization.t("meetingPoint", lang), evacPlan.meetingPoint.ifBlank { "—" })
                        PlanDetailRow(Localization.t("outContact", lang), "${evacPlan.contactName} (${evacPlan.contactPhone.ifBlank { "—" }})")
                        if (evacPlan.notes.isNotBlank()) {
                            PlanDetailRow(Localization.t("planNotes", lang), evacPlan.notes)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onSetPlanEditing(true) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("plan_edit_btn")
                    ) {
                        Text(
                            text = Localization.t("editPlan", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Navy
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun StockItemRow(
    name: String,
    amount: String,
    isWarning: Boolean,
    badgeText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Ink)
            Text(text = amount, fontSize = 12.sp, color = Muted)
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isWarning) RedAlertSoft else GreenOkSoft
        ) {
            Text(
                text = badgeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isWarning) RedAlert else GreenOk,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun PlanDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label: ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(text = value, fontSize = 13.sp, color = Muted)
    }
}
