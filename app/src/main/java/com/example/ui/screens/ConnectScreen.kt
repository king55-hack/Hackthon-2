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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.theme.*
import com.example.ui.components.EmergencyActionPanel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConnectScreen(
    lang: Language,
    isLoggedIn: Boolean,
    account: UserAccount?,
    myStatus: String,
    contacts: List<Contact>,
    familyGroups: List<FamilyGroup>,
    groupMessages: List<GroupMessage>,
    selectedContactGroup: String,
    selectedContact: Contact?,
    connectSubView: String,
    pendingSignup: Map<String, String>?,
    formError: String?,
    userCoords: Pair<Double, Double>,
    onSetSubView: (String) -> Unit,
    onSetSelectedGroup: (String) -> Unit,
    onSelectContact: (Contact?) -> Unit,
    onSetMyStatus: (String) -> Unit,
    onSetSafeStatus: () -> Unit,
    onSetNotSafeStatus: () -> Unit,
    onSendGroupMessage: (String, Boolean, Boolean) -> Unit,
    onCreateGroup: (String, String) -> Unit,
    onStartSignup: (String, String, String, String, String) -> Unit,
    onConfirmSignup: (String) -> Unit,
    onAttemptLogin: (String, String) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onAddContact: (String, String, String, String, String) -> Unit,
    onUpdateContactStatus: (String, String) -> Unit,
    onDeleteContact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var activeGroupId by remember { mutableStateOf(familyGroups.firstOrNull()?.id ?: "default") }
    var showCreateGroupDialog by remember { mutableStateOf(false) }

    val firstFamilyPhone = contacts.firstOrNull { it.relation == "family" && it.phone.isNotBlank() }?.phone

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        when (connectSubView) {
            "promo" -> {
                // Free Guest Notice & Feature Unlock Promotion
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LineBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(TealAccentSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = null,
                                tint = TealAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = Localization.t("connectPromoT", lang),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Localization.t("connectPromoS", lang),
                            fontSize = 13.sp,
                            color = Muted,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Guest friendly callout card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GreenOkSoft,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreenOk.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GreenOk,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = Localization.t("guest_notice_title", lang),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenOk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = Localization.t("guest_notice_desc", lang),
                                        fontSize = 11.5.sp,
                                        color = Ink,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Member unlocked feature perks card
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PanelBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Amber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = Localization.t("unlocked_features_title", lang),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Ink
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = Localization.t("unlocked_features_desc", lang),
                                        fontSize = 11.5.sp,
                                        color = Muted,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onSetSubView("signup") },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("promo_signup_btn")
                        ) {
                            Text(Localization.t("btnSignup", lang), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { onSetSubView("login") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("promo_login_btn")
                        ) {
                            Text(
                                Localization.t("btnLogin", lang),
                                fontWeight = FontWeight.SemiBold,
                                color = Navy
                            )
                        }
                    }
                }
            }

            "signup" -> {
                var name by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onSetSubView("promo") }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            Text(Localization.t("signupTitle", lang), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (formError != null) {
                            Text(formError, color = RedAlert, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(Localization.t("fieldName", lang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("signup_name_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(Localization.t("fieldPhone", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("signup_phone_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(Localization.t("fieldEmail", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("signup_email_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(Localization.t("fieldPassword", lang)) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("signup_pass_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(Localization.t("fieldConfirmPassword", lang)) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("signup_confirm_pass_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onStartSignup(name, phone, email, password, confirmPassword) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("signup_submit_btn")
                        ) {
                            Text(Localization.t("btnSignup", lang), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { onSetSubView("login") },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(Localization.t("switchToLogin", lang), fontSize = 12.5.sp, color = TealAccent)
                        }
                    }
                }
            }

            "verify" -> {
                val code = pendingSignup?.get("code") ?: "123456"
                var enteredCode by remember { mutableStateOf(code) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(Localization.t("verifyTitle", lang), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Localization.t("verifyHint", lang), fontSize = 12.5.sp, color = Muted)

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PanelBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Code: $code",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Navy,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (formError != null) {
                            Text(formError, color = RedAlert, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        OutlinedTextField(
                            value = enteredCode,
                            onValueChange = { enteredCode = it },
                            label = { Text(Localization.t("verifyCodeLabel", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("verify_code_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onConfirmSignup(enteredCode) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("verify_submit_btn")
                        ) {
                            Text(Localization.t("verifyBtn", lang), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "login" -> {
                var id by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onSetSubView("promo") }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            Text(Localization.t("loginTitle", lang), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (formError != null) {
                            Text(formError, color = RedAlert, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = id,
                            onValueChange = { id = it },
                            label = { Text("फोन वा इमेल / Phone or Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("login_id_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(Localization.t("fieldPassword", lang)) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("login_pass_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onAttemptLogin(id, password) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("login_submit_btn")
                        ) {
                            Text(Localization.t("btnLogin", lang), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { onSetSubView("signup") },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(Localization.t("switchToSignup", lang), fontSize = 12.5.sp, color = TealAccent)
                        }
                    }
                }
            }

            "add_contact" -> {
                var cName by remember { mutableStateOf("") }
                var cPhone by remember { mutableStateOf("") }
                var cEmail by remember { mutableStateOf("") }
                var cRelation by remember { mutableStateOf("family") }
                var cGroup by remember { mutableStateOf("") }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onSetSubView("home") }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            Text(Localization.t("addContact", lang), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (formError != null) {
                            Text(formError, color = RedAlert, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        OutlinedTextField(
                            value = cName,
                            onValueChange = { cName = it },
                            label = { Text(Localization.t("fieldName", lang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("contact_name_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = cPhone,
                            onValueChange = { cPhone = it },
                            label = { Text(Localization.t("fieldPhone", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("contact_phone_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = cEmail,
                            onValueChange = { cEmail = it },
                            label = { Text(Localization.t("fieldEmail", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Relation Chips
                        Text(Localization.t("contactRelation", lang), fontSize = 12.sp, color = Muted, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = cRelation == "family",
                                onClick = { cRelation = "family" },
                                label = { Text(Localization.t("relFamily", lang)) }
                            )
                            FilterChip(
                                selected = cRelation == "friend",
                                onClick = { cRelation = "friend" },
                                label = { Text(Localization.t("relFriend", lang)) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = cGroup,
                            onValueChange = { cGroup = it },
                            label = { Text(Localization.t("contactGroup", lang)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onAddContact(cName, cPhone, cEmail, cRelation, cGroup) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_contact_btn")
                        ) {
                            Text(Localization.t("saveContact", lang), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "contact_detail" -> {
                val c = selectedContact
                if (c == null) {
                    onSetSubView("home")
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onSelectContact(null); onSetSubView("home") }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                                Text(c.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("फोन: ${c.phone.ifBlank { "なし" }}", fontSize = 13.sp, color = Ink)
                            Text("सम्बन्ध: ${if (c.relation == "family") Localization.t("relFamily", lang) else Localization.t("relFriend", lang)}", fontSize = 13.sp, color = Ink)

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = LineBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(Localization.t("setStatusFor", lang), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusOptionButton(
                                    label = Localization.t("statusSafe", lang),
                                    isSelected = c.status == "safe",
                                    color = GreenOk,
                                    onClick = { onUpdateContactStatus(c.id, "safe") },
                                    modifier = Modifier.weight(1f)
                                )
                                StatusOptionButton(
                                    label = Localization.t("statusHelp", lang),
                                    isSelected = c.status == "help",
                                    color = RedAlert,
                                    onClick = { onUpdateContactStatus(c.id, "help") },
                                    modifier = Modifier.weight(1f)
                                )
                                StatusOptionButton(
                                    label = Localization.t("statusUnknown", lang),
                                    isSelected = c.status == "unknown",
                                    color = Muted,
                                    onClick = { onUpdateContactStatus(c.id, "unknown") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (c.phone.isNotBlank()) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.phone}"))
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("कल गर्नुहोस्", fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val uri = Uri.parse("sms:${c.phone}?body=${Uri.encode("【SONAERU】म सुरक्षित छु। तपाईं कस्तो हुनुहुन्छ?")}")
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Navy),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("SMS सन्देश", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { onDeleteContact(c.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedAlert),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = RedAlert, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(Localization.t("deleteContact", lang), color = RedAlert)
                            }
                        }
                    }
                }
            }

            else -> {
                // LOGGED-IN HOME VIEW (Social Circles + Real-Time Group Chat + SOS Panel)
                val acc = account

                // 1. Account header card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Navy.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Navy)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(acc?.name ?: "User", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
                                Text(acc?.phone?.ifBlank { acc.email } ?: "", fontSize = 12.sp, color = Muted)
                            }
                        }
                        TextButton(onClick = onLogout) {
                            Text(Localization.t("btnLogout", lang), fontSize = 12.sp, color = RedAlert)
                        }
                    }
                }

                // 2. Emergency Action Panel (1. I'm Safe / 2. I'm Not Safe / 119, 110 & Family Call / Location Share)
                EmergencyActionPanel(
                    lang = lang,
                    myStatus = myStatus,
                    userCoords = userCoords,
                    familyPhone = firstFamilyPhone,
                    onSetSafeStatus = onSetSafeStatus,
                    onSetNotSafeStatus = onSetNotSafeStatus
                )

                // 3. Family & Friends Circles Card (Group Switcher & Creation)
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Diversity3, contentDescription = null, tint = TealAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Localization.t("family_group_title", lang),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Ink
                                )
                            }
                            TextButton(onClick = { showCreateGroupDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(Localization.t("create_group_btn", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealAccent)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Circles chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            familyGroups.forEach { grp ->
                                val isSelected = grp.id == activeGroupId
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) Navy else PanelBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Navy else LineBorder),
                                    modifier = Modifier
                                        .clickable { activeGroupId = grp.id }
                                        .testTag("group_chip_${grp.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = grp.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Ink
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) Color.White.copy(alpha = 0.25f) else LineBorder
                                        ) {
                                            Text(
                                                text = "${grp.memberCount}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Muted,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Real-time Group Chat & Safety Discussion
                var chatInputText by remember { mutableStateOf("") }
                val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                        .testTag("group_chat_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Navy, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Localization.t("group_chat_title", lang),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Ink
                                )
                            }
                            Text(
                                text = "${groupMessages.size} messages",
                                fontSize = 11.5.sp,
                                color = Muted
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Message Feed Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp, max = 280.dp)
                                .background(PanelBg, RoundedCornerShape(8.dp))
                                .border(1.dp, LineBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            groupMessages.forEach { msg ->
                                val isMe = msg.isMe
                                val bubbleBg = when {
                                    msg.isSos -> RedAlertSoft
                                    msg.isSafeNotification -> GreenOkSoft
                                    isMe -> Navy.copy(alpha = 0.9f)
                                    else -> Color.White
                                }
                                val textColor = when {
                                    msg.isSos -> RedAlertDark
                                    msg.isSafeNotification -> GreenOk
                                    isMe -> Color.White
                                    else -> Ink
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                    ) {
                                        Text(
                                            text = msg.senderName,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (msg.isSos) RedAlert else Muted
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = timeFormat.format(Date(msg.timestamp)),
                                            fontSize = 9.5.sp,
                                            color = Muted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 10.dp,
                                            topEnd = 10.dp,
                                            bottomStart = if (isMe) 10.dp else 2.dp,
                                            bottomEnd = if (isMe) 2.dp else 10.dp
                                        ),
                                        color = bubbleBg,
                                        border = if (msg.isSos) androidx.compose.foundation.BorderStroke(1.dp, RedAlert) else null,
                                        shadowElevation = 0.5.dp
                                    ) {
                                        Text(
                                            text = msg.text,
                                            fontSize = 12.5.sp,
                                            color = textColor,
                                            fontWeight = if (msg.isSos) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Status Response Chips
                        Text(
                            text = Localization.t("quick_chips_title", lang),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Muted
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                Pair(Localization.t("chip_safe", lang), false),
                                Pair(Localization.t("chip_help", lang), true),
                                Pair(Localization.t("chip_shelter", lang), false),
                                Pair(Localization.t("chip_home", lang), false)
                            ).forEach { (label, isAlert) ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isAlert) RedAlertSoft else PanelBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isAlert) RedAlert else LineBorder),
                                    modifier = Modifier
                                        .clickable {
                                            onSendGroupMessage(label, isAlert, !isAlert)
                                        }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAlert) RedAlert else Ink,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Message Input Field & Send Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = { Text(Localization.t("type_message_hint", lang), fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("group_chat_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (chatInputText.isNotBlank()) {
                                        onSendGroupMessage(chatInputText, false, false)
                                        chatInputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Navy)
                                    .testTag("group_chat_send_btn")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 5. Contacts List & Individual Status Board
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(Localization.t("contactsTitle", lang), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
                            TextButton(onClick = { onSetSubView("add_contact") }) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = TealAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(Localization.t("addContact", lang), fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TealAccent)
                            }
                        }

                        val groups = listOf("all") + contacts.mapNotNull { it.group.ifBlank { null } }.distinct()
                        if (groups.size > 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                groups.forEach { grp ->
                                    val isSel = grp == selectedContactGroup
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { onSetSelectedGroup(grp) },
                                        label = { Text(if (grp == "all") Localization.t("groupAll", lang) else grp) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        val filtered = contacts.filter {
                            selectedContactGroup == "all" || it.group == selectedContactGroup
                        }

                        if (filtered.isEmpty()) {
                            Text(
                                text = Localization.t("noContacts", lang),
                                fontSize = 13.sp,
                                color = Muted,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            filtered.forEach { contact ->
                                ContactItemRow(
                                    contact = contact,
                                    lang = lang,
                                    onClick = { onSelectContact(contact) }
                                )
                                HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }

                // Delete Account
                TextButton(
                    onClick = onDeleteAccount,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(Localization.t("btnDeleteAccount", lang), color = Muted, fontSize = 11.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Dialog: Create New Family/Friends Circle
    if (showCreateGroupDialog) {
        var groupNameInput by remember { mutableStateOf("") }
        var groupDescInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text(Localization.t("create_group_btn", lang), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = groupNameInput,
                        onValueChange = { groupNameInput = it },
                        label = { Text("समूहको नाम / Circle Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = groupDescInput,
                        onValueChange = { groupDescInput = it },
                        label = { Text("विवरण / Description (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupNameInput.isNotBlank()) {
                            onCreateGroup(groupNameInput, groupDescInput)
                            showCreateGroupDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy)
                ) {
                    Text("बनाउनुहोस् / Create", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text("रद्द / Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatusOptionButton(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@Composable
private fun ContactItemRow(
    contact: Contact,
    lang: Language,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(contact.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (contact.relation == "family") TealAccentSoft else BlueRecoverSoft
                ) {
                    Text(
                        text = if (contact.relation == "family") Localization.t("relFamily", lang) else Localization.t("relFriend", lang),
                        fontSize = 10.sp,
                        color = if (contact.relation == "family") TealAccent else BlueRecover,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            if (contact.phone.isNotBlank()) {
                Text(contact.phone, fontSize = 12.sp, color = Muted)
            }
        }

        val statusText = when (contact.status) {
            "safe" -> Localization.t("statusSafe", lang)
            "help" -> Localization.t("statusHelp", lang)
            else -> Localization.t("statusUnknown", lang)
        }
        val statusBg = when (contact.status) {
            "safe" -> GreenOkSoft
            "help" -> RedAlertSoft
            else -> LineBorder.copy(alpha = 0.5f)
        }
        val statusColor = when (contact.status) {
            "safe" -> GreenOk
            "help" -> RedAlert
            else -> Muted
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = statusBg
        ) {
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}
