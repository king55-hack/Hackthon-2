package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalStorageRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sonaeru_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val KEY_LANG = "sonaeru_lang"
        private const val KEY_PHASE = "sonaeru_phase"
        private const val KEY_POINTS = "sonaeru_points"
        private const val KEY_RESTOCKED = "sonaeru_restocked"
        private const val KEY_EVAC_PLAN = "sonaeru_evacplan"
        private const val KEY_ACCOUNT = "sonaeru_account"
        private const val KEY_LOGGED_IN = "sonaeru_loggedIn"
        private const val KEY_MY_STATUS = "sonaeru_myStatus"
        private const val KEY_CONTACTS = "sonaeru_contacts"
        private const val KEY_ACT1 = "sonaeru_act1"
        private const val KEY_ACT2 = "sonaeru_act2"
        private const val KEY_ACT3 = "sonaeru_act3"
        private const val KEY_GROUPS = "sonaeru_groups"
        private const val KEY_MESSAGES = "sonaeru_messages"
    }

    fun getLanguage(): Language {
        val code = prefs.getString(KEY_LANG, Language.JA.code) ?: Language.JA.code
        return Language.values().firstOrNull { it.code == code } ?: Language.JA
    }

    fun setLanguage(lang: Language) {
        prefs.edit().putString(KEY_LANG, lang.code).apply()
    }

    fun getPhase(): Phase {
        val id = prefs.getString(KEY_PHASE, Phase.HEIJI.id) ?: Phase.HEIJI.id
        return Phase.values().firstOrNull { it.id == id } ?: Phase.HEIJI
    }

    fun setPhase(phase: Phase) {
        prefs.edit().putString(KEY_PHASE, phase.id).apply()
    }

    fun getPoints(): Int = prefs.getInt(KEY_POINTS, 120)

    fun addPoints(delta: Int): Int {
        val current = getPoints()
        val next = current + delta
        prefs.edit().putInt(KEY_POINTS, next).apply()
        return next
    }

    fun isRestocked(): Boolean = prefs.getBoolean(KEY_RESTOCKED, false)

    fun setRestocked(value: Boolean) {
        prefs.edit().putBoolean(KEY_RESTOCKED, value).apply()
    }

    fun getEvacPlan(): EvacPlan? {
        val str = prefs.getString(KEY_EVAC_PLAN, null) ?: return null
        return try {
            json.decodeFromString<EvacPlan>(str)
        } catch (e: Exception) {
            null
        }
    }

    fun saveEvacPlan(plan: EvacPlan) {
        prefs.edit().putString(KEY_EVAC_PLAN, json.encodeToString(plan)).apply()
    }

    fun getAccount(): UserAccount? {
        val str = prefs.getString(KEY_ACCOUNT, null) ?: return null
        return try {
            json.decodeFromString<UserAccount>(str)
        } catch (e: Exception) {
            null
        }
    }

    fun saveAccount(account: UserAccount) {
        prefs.edit()
            .putString(KEY_ACCOUNT, json.encodeToString(account))
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false) && getAccount() != null

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()
    }

    fun deleteAccount() {
        prefs.edit()
            .remove(KEY_ACCOUNT)
            .remove(KEY_LOGGED_IN)
            .remove(KEY_MY_STATUS)
            .remove(KEY_CONTACTS)
            .remove(KEY_GROUPS)
            .remove(KEY_MESSAGES)
            .apply()
    }

    fun getMyStatus(): String = prefs.getString(KEY_MY_STATUS, "unknown") ?: "unknown"

    fun setMyStatus(status: String) {
        prefs.edit().putString(KEY_MY_STATUS, status).apply()
    }

    fun getContacts(): List<Contact> {
        val str = prefs.getString(KEY_CONTACTS, null)
        if (str != null) {
            try {
                val list = json.decodeFromString<List<Contact>>(str)
                if (list.isNotEmpty()) return list
            } catch (_: Exception) {}
        }
        // Seed initial family contacts
        val initialContacts = listOf(
            Contact(id = "c_1", name = "Aama (आमा)", phone = "090-1234-5678", relation = "family", group = "Family", status = "safe"),
            Contact(id = "c_2", name = "Buwa (बुवा)", phone = "080-9876-5432", relation = "family", group = "Family", status = "safe"),
            Contact(id = "c_3", name = "Bhai (भाइ - Ramesh)", phone = "070-5555-1111", relation = "family", group = "Family", status = "unknown"),
            Contact(id = "c_4", name = "Sunil (साथी)", phone = "090-4444-2222", relation = "friend", group = "Tokyo Friends", status = "safe")
        )
        saveContacts(initialContacts)
        return initialContacts
    }

    fun saveContacts(list: List<Contact>) {
        prefs.edit().putString(KEY_CONTACTS, json.encodeToString(list)).apply()
    }

    fun addContact(contact: Contact) {
        val list = getContacts().toMutableList()
        list.add(contact)
        saveContacts(list)
    }

    fun updateContactStatus(id: String, status: String) {
        val list = getContacts().map {
            if (it.id == id) it.copy(status = status, statusUpdatedAt = System.currentTimeMillis()) else it
        }
        saveContacts(list)
    }

    fun deleteContact(id: String) {
        val list = getContacts().filter { it.id != id }
        saveContacts(list)
    }

    fun getFamilyGroups(): List<FamilyGroup> {
        val str = prefs.getString(KEY_GROUPS, null)
        if (str != null) {
            try {
                val list = json.decodeFromString<List<FamilyGroup>>(str)
                if (list.isNotEmpty()) return list
            } catch (_: Exception) {}
        }
        val defaultGroups = listOf(
            FamilyGroup(id = "g_1", name = "मेरो परिवार (My Family Circle)", description = "घरका सदस्यहरूको आपतकालीन समूह", memberCount = 4),
            FamilyGroup(id = "g_2", name = "टोकियो साथीहरू (Friends Circle)", description = "जापानमा रहेका साथीहरू", memberCount = 6)
        )
        saveFamilyGroups(defaultGroups)
        return defaultGroups
    }

    fun saveFamilyGroups(groups: List<FamilyGroup>) {
        prefs.edit().putString(KEY_GROUPS, json.encodeToString(groups)).apply()
    }

    fun addFamilyGroup(name: String, desc: String): FamilyGroup {
        val current = getFamilyGroups().toMutableList()
        val newGroup = FamilyGroup(
            id = "g_${System.currentTimeMillis()}",
            name = name,
            description = desc,
            memberCount = 1
        )
        current.add(newGroup)
        saveFamilyGroups(current)
        return newGroup
    }

    fun getGroupMessages(): List<GroupMessage> {
        val str = prefs.getString(KEY_MESSAGES, null)
        if (str != null) {
            try {
                val list = json.decodeFromString<List<GroupMessage>>(str)
                if (list.isNotEmpty()) return list
            } catch (_: Exception) {}
        }
        val now = System.currentTimeMillis()
        val initialMessages = listOf(
            GroupMessage(
                id = "m_1",
                senderName = "Aama (आमा)",
                text = "सबै जना सुरक्षित छौ? यहाँ ठूलो पानी परिरहेको छ।",
                timestamp = now - 3600000 * 2,
                isMe = false,
                isSafeNotification = false
            ),
            GroupMessage(
                id = "m_2",
                senderName = "Bhai (भाइ)",
                text = "म कलेजबाट निस्किएर स्टेसनतर्फ जाँदै छु। म ठीक छु।",
                timestamp = now - 3600000,
                isMe = false,
                isSafeNotification = true
            ),
            GroupMessage(
                id = "m_3",
                senderName = "Buwa (बुवा)",
                text = "मैले घरको ग्यास र बिजुली बन्द गरेर सुरक्षित राखेको छु।",
                timestamp = now - 1800000,
                isMe = false,
                isSafeNotification = false
            )
        )
        saveGroupMessages(initialMessages)
        return initialMessages
    }

    fun saveGroupMessages(messages: List<GroupMessage>) {
        prefs.edit().putString(KEY_MESSAGES, json.encodeToString(messages)).apply()
    }

    fun addGroupMessage(text: String, isMe: Boolean = true, isSos: Boolean = false, isSafeNotification: Boolean = false, senderName: String = "You"): GroupMessage {
        val list = getGroupMessages().toMutableList()
        val msg = GroupMessage(
            id = "m_${System.currentTimeMillis()}",
            senderName = senderName,
            text = text,
            timestamp = System.currentTimeMillis(),
            isMe = isMe,
            isSos = isSos,
            isSafeNotification = isSafeNotification
        )
        list.add(msg)
        saveGroupMessages(list)
        return msg
    }

    fun getChecklist(): Triple<Boolean, Boolean, Boolean> {
        return Triple(
            prefs.getBoolean(KEY_ACT1, false),
            prefs.getBoolean(KEY_ACT2, false),
            prefs.getBoolean(KEY_ACT3, false)
        )
    }

    fun setChecklist(act1: Boolean, act2: Boolean, act3: Boolean) {
        prefs.edit()
            .putBoolean(KEY_ACT1, act1)
            .putBoolean(KEY_ACT2, act2)
            .putBoolean(KEY_ACT3, act3)
            .apply()
    }
}
