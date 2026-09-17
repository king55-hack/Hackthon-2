package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocalStorageRepository
import com.example.data.SecurityHelper
import com.example.model.*
import com.example.service.WeatherAndFacilityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LocalStorageRepository(application)

    private val _language = MutableStateFlow(repository.getLanguage())
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _phase = MutableStateFlow(repository.getPhase())
    val phase: StateFlow<Phase> = _phase.asStateFlow()

    private val _tab = MutableStateFlow(AppTab.HOME)
    val tab: StateFlow<AppTab> = _tab.asStateFlow()

    private val _points = MutableStateFlow(repository.getPoints())
    val points: StateFlow<Int> = _points.asStateFlow()

    private val _isRestocked = MutableStateFlow(repository.isRestocked())
    val isRestocked: StateFlow<Boolean> = _isRestocked.asStateFlow()

    private val _quizIndex = MutableStateFlow(0)
    val quizIndex: StateFlow<Int> = _quizIndex.asStateFlow()

    private val _evacPlan = MutableStateFlow(repository.getEvacPlan())
    val evacPlan: StateFlow<EvacPlan?> = _evacPlan.asStateFlow()

    private val _isPlanEditing = MutableStateFlow(false)
    val isPlanEditing: StateFlow<Boolean> = _isPlanEditing.asStateFlow()

    private val _userCoords = MutableStateFlow(WeatherAndFacilityService.TOKYO_COORDS)
    val userCoords: StateFlow<Pair<Double, Double>> = _userCoords.asStateFlow()

    private val _placeName = MutableStateFlow("東京都港区 芝公園付近")
    val placeName: StateFlow<String> = _placeName.asStateFlow()

    private val _weather = MutableStateFlow<WeatherData?>(null)
    val weather: StateFlow<WeatherData?> = _weather.asStateFlow()

    private val _weatherStatus = MutableStateFlow("loading")
    val weatherStatus: StateFlow<String> = _weatherStatus.asStateFlow()

    private val _facilityCategory = MutableStateFlow(FacilityCategory.SHELTER)
    val facilityCategory: StateFlow<FacilityCategory> = _facilityCategory.asStateFlow()

    private val _facilityList = MutableStateFlow<List<FacilityItem>>(emptyList())
    val facilityList: StateFlow<List<FacilityItem>> = _facilityList.asStateFlow()

    private val _checklist = MutableStateFlow(repository.getChecklist())
    val checklist: StateFlow<Triple<Boolean, Boolean, Boolean>> = _checklist.asStateFlow()

    private val _account = MutableStateFlow(repository.getAccount())
    val account: StateFlow<UserAccount?> = _account.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(repository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _myStatus = MutableStateFlow(repository.getMyStatus())
    val myStatus: StateFlow<String> = _myStatus.asStateFlow()

    private val _contacts = MutableStateFlow(repository.getContacts())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _selectedContactGroup = MutableStateFlow("all")
    val selectedContactGroup: StateFlow<String> = _selectedContactGroup.asStateFlow()

    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    private val _connectSubView = MutableStateFlow(if (repository.isLoggedIn()) "home" else "promo")
    val connectSubView: StateFlow<String> = _connectSubView.asStateFlow()

    private val _pendingSignup = MutableStateFlow<Map<String, String>?>(null)
    val pendingSignup: StateFlow<Map<String, String>?> = _pendingSignup.asStateFlow()

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Family groups and group chat
    private val _familyGroups = MutableStateFlow(repository.getFamilyGroups())
    val familyGroups: StateFlow<List<FamilyGroup>> = _familyGroups.asStateFlow()

    private val _groupMessages = MutableStateFlow(repository.getGroupMessages())
    val groupMessages: StateFlow<List<GroupMessage>> = _groupMessages.asStateFlow()

    init {
        loadWeather()
        updateFacilities(FacilityCategory.SHELTER)
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
        repository.setLanguage(lang)
    }

    fun setTab(t: AppTab) {
        _tab.value = t
    }

    fun toggleDemoPhase() {
        val next = if (_phase.value == Phase.HEIJI) Phase.HASSAI else Phase.HEIJI
        _phase.value = next
        repository.setPhase(next)
    }

    fun loadWeather() {
        viewModelScope.launch {
            _weatherStatus.value = "loading"
            val coords = _userCoords.value
            val result = WeatherAndFacilityService.fetchWeather(coords.first, coords.second)
            if (result != null) {
                _weather.value = result
                _weatherStatus.value = "live"
            } else {
                _weather.value = WeatherData(tempC = 21, code = 0, precip = 0.0, windKph = 8, fetchedAt = System.currentTimeMillis())
                _weatherStatus.value = "fallback"
            }
        }
    }

    fun updateFacilities(category: FacilityCategory) {
        _facilityCategory.value = category
        val coords = _userCoords.value
        val list = WeatherAndFacilityService.getFacilities(category, coords.first, coords.second)
        _facilityList.value = list
    }

    fun restock() {
        if (!_isRestocked.value) {
            repository.setRestocked(true)
            _isRestocked.value = true
            val newPts = repository.addPoints(5)
            _points.value = newPts
            showToast("+5 pt獲得！")
        }
    }

    fun answerQuiz(isCorrect: Boolean) {
        val lang = _language.value
        if (isCorrect) {
            val newPts = repository.addPoints(10)
            _points.value = newPts
            _quizIndex.value = _quizIndex.value + 1
            showToast(Localization.t("quizCorrect", lang))
        } else {
            showToast(Localization.t("quizWrong", lang))
        }
    }

    fun savePlan(meetingPoint: String, contactName: String, contactPhone: String, notes: String) {
        val plan = EvacPlan(
            meetingPoint = meetingPoint.trim(),
            contactName = contactName.trim(),
            contactPhone = contactPhone.trim(),
            notes = notes.trim(),
            updatedAt = System.currentTimeMillis()
        )
        repository.saveEvacPlan(plan)
        _evacPlan.value = plan
        _isPlanEditing.value = false
        showToast("避難計画を保存しました")
    }

    fun setPlanEditing(editing: Boolean) {
        _isPlanEditing.value = editing
    }

    fun toggleChecklist(itemIndex: Int) {
        val cur = _checklist.value
        val next = when (itemIndex) {
            1 -> cur.copy(first = !cur.first)
            2 -> cur.copy(second = !cur.second)
            3 -> cur.copy(third = !cur.third)
            else -> cur
        }
        _checklist.value = next
        repository.setChecklist(next.first, next.second, next.third)
    }

    fun setSafeStatus() {
        val lang = _language.value
        _myStatus.value = "safe"
        repository.setMyStatus("safe")
        val senderName = _account.value?.name ?: "Me"
        val safeText = "${Localization.t("im_safe", lang)} (GPS: 35.6586, 139.7454)"
        val msg = repository.addGroupMessage(safeText, isMe = true, isSos = false, isSafeNotification = true, senderName = senderName)
        _groupMessages.value = repository.getGroupMessages()
        showToast(Localization.t("safe_sent_toast", lang))
    }

    fun setNotSafeStatus() {
        val lang = _language.value
        _myStatus.value = "help"
        repository.setMyStatus("help")
        val senderName = _account.value?.name ?: "Me"
        val sosText = "🚨 【SOS】 ${Localization.t("im_not_safe", lang)}! (GPS: 35.6586, 139.7454)"
        val msg = repository.addGroupMessage(sosText, isMe = true, isSos = true, isSafeNotification = false, senderName = senderName)
        _groupMessages.value = repository.getGroupMessages()
        showToast(Localization.t("sos_sent_toast", lang))
    }

    fun sendSafeStatus() {
        setSafeStatus()
    }

    fun setMyStatus(status: String) {
        if (status == "safe") {
            setSafeStatus()
        } else if (status == "help") {
            setNotSafeStatus()
        } else {
            _myStatus.value = status
            repository.setMyStatus(status)
        }
    }

    fun sendGroupMessage(text: String, isSos: Boolean = false, isSafeNotification: Boolean = false) {
        if (text.isBlank()) return
        val senderName = _account.value?.name ?: "You"
        repository.addGroupMessage(text.trim(), isMe = true, isSos = isSos, isSafeNotification = isSafeNotification, senderName = senderName)
        _groupMessages.value = repository.getGroupMessages()
    }

    fun createFamilyGroup(name: String, desc: String) {
        if (name.isBlank()) return
        repository.addFamilyGroup(name.trim(), desc.trim())
        _familyGroups.value = repository.getFamilyGroups()
        showToast("नयाँ समूह सिर्जना गरियो")
    }

    fun setConnectSubView(view: String) {
        _connectSubView.value = view
        _formError.value = null
    }

    fun setSelectedContactGroup(group: String) {
        _selectedContactGroup.value = group
    }

    fun selectContact(contact: Contact?) {
        _selectedContact.value = contact
        if (contact != null) {
            _connectSubView.value = "contact_detail"
        }
    }

    fun startSignup(name: String, phone: String, email: String, pass: String, confirmPass: String) {
        val lang = _language.value
        if (name.isBlank()) {
            _formError.value = Localization.t("errNameRequired", lang)
            return
        }
        if (pass.length < 6) {
            _formError.value = Localization.t("errPasswordShort", lang)
            return
        }
        if (pass != confirmPass) {
            _formError.value = Localization.t("errPasswordMismatch", lang)
            return
        }
        val code = SecurityHelper.generatePrototypeCode()
        _pendingSignup.value = mapOf(
            "name" to name.trim(),
            "phone" to phone.trim(),
            "email" to email.trim(),
            "password" to pass,
            "code" to code
        )
        _formError.value = null
        _connectSubView.value = "verify"
    }

    fun confirmSignup(enteredCode: String) {
        val pending = _pendingSignup.value ?: return
        val expectedCode = pending["code"] ?: ""
        val lang = _language.value
        if (enteredCode.trim() != expectedCode) {
            _formError.value = Localization.t("verifyWrong", lang)
            return
        }
        val salt = SecurityHelper.randomSaltHex()
        val passHash = SecurityHelper.hashPassword(pending["password"] ?: "", salt)
        val acc = UserAccount(
            name = pending["name"] ?: "",
            phone = pending["phone"] ?: "",
            email = pending["email"] ?: "",
            passHash = passHash,
            passSalt = salt,
            createdAt = System.currentTimeMillis()
        )
        repository.saveAccount(acc)
        _account.value = acc
        _isLoggedIn.value = true
        _pendingSignup.value = null
        _formError.value = null
        _connectSubView.value = "home"
        showToast(Localization.t("accountCreated", lang))
    }

    fun attemptLogin(id: String, pass: String) {
        val lang = _language.value
        val acc = repository.getAccount()
        if (acc == null) {
            _formError.value = Localization.t("errLoginFailed", lang)
            return
        }
        val idTrimmed = id.trim()
        val matchId = idTrimmed.equals(acc.phone.trim(), ignoreCase = true) || idTrimmed.equals(acc.email.trim(), ignoreCase = true)
        val hash = SecurityHelper.hashPassword(pass, acc.passSalt)
        if (!matchId || hash != acc.passHash) {
            _formError.value = Localization.t("errLoginFailed", lang)
            return
        }
        repository.setLoggedIn(true)
        _isLoggedIn.value = true
        _account.value = acc
        _formError.value = null
        _connectSubView.value = "home"
    }

    fun logout() {
        repository.setLoggedIn(false)
        _isLoggedIn.value = false
        _connectSubView.value = "promo"
    }

    fun deleteAccount() {
        repository.deleteAccount()
        _account.value = null
        _isLoggedIn.value = false
        _contacts.value = emptyList()
        _connectSubView.value = "promo"
    }

    fun addContact(name: String, phone: String, email: String, relation: String, group: String) {
        val lang = _language.value
        if (name.isBlank()) {
            _formError.value = Localization.t("errNameRequired", lang)
            return
        }
        val c = Contact(
            id = System.currentTimeMillis().toString(),
            name = name.trim(),
            phone = phone.trim(),
            email = email.trim(),
            relation = relation,
            group = group.trim(),
            status = "unknown",
            statusUpdatedAt = null
        )
        repository.addContact(c)
        _contacts.value = repository.getContacts()
        _formError.value = null
        _connectSubView.value = "home"
    }

    fun updateContactStatus(contactId: String, status: String) {
        repository.updateContactStatus(contactId, status)
        _contacts.value = repository.getContacts()
        if (_selectedContact.value?.id == contactId) {
            _selectedContact.value = _selectedContact.value?.copy(status = status, statusUpdatedAt = System.currentTimeMillis())
        }
    }

    fun deleteContact(contactId: String) {
        repository.deleteContact(contactId)
        _contacts.value = repository.getContacts()
        _selectedContact.value = null
        _connectSubView.value = "home"
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
