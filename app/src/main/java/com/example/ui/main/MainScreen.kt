package com.example.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.theme.*
import com.example.ui.MainViewModel
import com.example.ui.components.BottomNavBar
import com.example.ui.components.DemoFloatingButton
import com.example.ui.components.HeaderBar
import com.example.ui.components.PhaseBanner
import com.example.ui.screens.*

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val phase by viewModel.phase.collectAsStateWithLifecycle()
    val tab by viewModel.tab.collectAsStateWithLifecycle()
    val points by viewModel.points.collectAsStateWithLifecycle()
    val isRestocked by viewModel.isRestocked.collectAsStateWithLifecycle()
    val quizIndex by viewModel.quizIndex.collectAsStateWithLifecycle()
    val evacPlan by viewModel.evacPlan.collectAsStateWithLifecycle()
    val isPlanEditing by viewModel.isPlanEditing.collectAsStateWithLifecycle()
    val userCoords by viewModel.userCoords.collectAsStateWithLifecycle()
    val placeName by viewModel.placeName.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val weatherStatus by viewModel.weatherStatus.collectAsStateWithLifecycle()
    val facilityCategory by viewModel.facilityCategory.collectAsStateWithLifecycle()
    val facilityList by viewModel.facilityList.collectAsStateWithLifecycle()
    val checklist by viewModel.checklist.collectAsStateWithLifecycle()
    val account by viewModel.account.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val myStatus by viewModel.myStatus.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val familyGroups by viewModel.familyGroups.collectAsStateWithLifecycle()
    val groupMessages by viewModel.groupMessages.collectAsStateWithLifecycle()
    val selectedContactGroup by viewModel.selectedContactGroup.collectAsStateWithLifecycle()
    val selectedContact by viewModel.selectedContact.collectAsStateWithLifecycle()
    val connectSubView by viewModel.connectSubView.collectAsStateWithLifecycle()
    val pendingSignup by viewModel.pendingSignup.collectAsStateWithLifecycle()
    val formError by viewModel.formError.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearToast()
        }
    }

    val isDisaster = phase == Phase.HASSAI
    val bgColor = if (isDisaster) StateBgDisaster else StateBgNormal

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgColor,
        topBar = {
            Column {
                HeaderBar(
                    phase = phase,
                    currentLanguage = language,
                    onSelectLanguage = { viewModel.setLanguage(it) }
                )
                PhaseBanner(
                    phase = phase,
                    tab = tab,
                    lang = language
                )
            }
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = tab,
                onTabSelected = { viewModel.setTab(it) },
                lang = language
            )
        },
        floatingActionButton = {
            DemoFloatingButton(
                phase = phase,
                lang = language,
                onToggle = { viewModel.toggleDemoPhase() }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(bgColor)
        ) {
            when (tab) {
                AppTab.HOME -> {
                    HomeScreen(
                        phase = phase,
                        lang = language,
                        points = points,
                        weather = weather,
                        weatherStatus = weatherStatus,
                        placeName = placeName,
                        onNavigateTab = { viewModel.setTab(it) },
                        onRefreshWeather = { viewModel.loadWeather() }
                    )
                }
                AppTab.PREPARE -> {
                    val nearestShelterName = facilityList.firstOrNull()?.name ?: Localization.t("shName1", language)
                    PrepareScreen(
                        lang = language,
                        isRestocked = isRestocked,
                        quizIndex = quizIndex,
                        evacPlan = evacPlan,
                        isPlanEditing = isPlanEditing,
                        contacts = contacts,
                        nearestShelterName = nearestShelterName,
                        onRestock = { viewModel.restock() },
                        onAnswerQuiz = { viewModel.answerQuiz(it) },
                        onSetPlanEditing = { viewModel.setPlanEditing(it) },
                        onSavePlan = { mp, cn, cp, notes -> viewModel.savePlan(mp, cn, cp, notes) }
                    )
                }
                AppTab.PROTECT -> {
                    val nearest = facilityList.firstOrNull()
                    val emergencyPhone = evacPlan?.contactPhone?.ifBlank { null }
                        ?: contacts.firstOrNull { it.relation == "family" && it.phone.isNotBlank() }?.phone
                    ProtectScreen(
                        phase = phase,
                        lang = language,
                        myStatus = myStatus,
                        checklist = checklist,
                        nearestShelter = nearest,
                        userCoords = userCoords,
                        familyPhone = emergencyPhone,
                        onSetSafeStatus = { viewModel.setSafeStatus() },
                        onSetNotSafeStatus = { viewModel.setNotSafeStatus() },
                        onToggleChecklist = { viewModel.toggleChecklist(it) }
                    )
                }
                AppTab.RECOVER -> {
                    RecoverScreen(
                        lang = language,
                        selectedCategory = facilityCategory,
                        facilities = facilityList,
                        userCoords = userCoords,
                        onSelectCategory = { viewModel.updateFacilities(it) }
                    )
                }
                AppTab.CONNECT -> {
                    ConnectScreen(
                        lang = language,
                        isLoggedIn = isLoggedIn,
                        account = account,
                        myStatus = myStatus,
                        contacts = contacts,
                        familyGroups = familyGroups,
                        groupMessages = groupMessages,
                        selectedContactGroup = selectedContactGroup,
                        selectedContact = selectedContact,
                        connectSubView = connectSubView,
                        pendingSignup = pendingSignup,
                        formError = formError,
                        userCoords = userCoords,
                        onSetSubView = { viewModel.setConnectSubView(it) },
                        onSetSelectedGroup = { viewModel.setSelectedContactGroup(it) },
                        onSelectContact = { viewModel.selectContact(it) },
                        onSetMyStatus = { viewModel.setMyStatus(it) },
                        onSetSafeStatus = { viewModel.setSafeStatus() },
                        onSetNotSafeStatus = { viewModel.setNotSafeStatus() },
                        onSendGroupMessage = { text, isSos, isSafe ->
                            viewModel.sendGroupMessage(text, isSos, isSafe)
                        },
                        onCreateGroup = { name, desc ->
                            viewModel.createFamilyGroup(name, desc)
                        },
                        onStartSignup = { name, phone, email, pass, conf ->
                            viewModel.startSignup(name, phone, email, pass, conf)
                        },
                        onConfirmSignup = { code -> viewModel.confirmSignup(code) },
                        onAttemptLogin = { id, pass -> viewModel.attemptLogin(id, pass) },
                        onLogout = { viewModel.logout() },
                        onDeleteAccount = { viewModel.deleteAccount() },
                        onAddContact = { name, phone, email, rel, grp ->
                            viewModel.addContact(name, phone, email, rel, grp)
                        },
                        onUpdateContactStatus = { id, status -> viewModel.updateContactStatus(id, status) },
                        onDeleteContact = { id -> viewModel.deleteContact(id) }
                    )
                }
            }
        }
    }
}
