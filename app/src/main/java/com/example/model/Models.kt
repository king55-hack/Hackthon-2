package com.example.model

import kotlinx.serialization.Serializable

enum class Language(val code: String, val label: String, val subtitle: String) {
    JA("ja", "日本", "日本語"),
    EASY("easy", "やさしい", "やさしい日本語"),
    EN("en", "EN", "English"),
    NE("ne", "ने", "नेपाली")
}

enum class Phase(val id: String) {
    HEIJI("heiji"),
    HASSAI("hassai")
}

enum class AppTab(val id: String) {
    HOME("home"),
    PREPARE("prepare"),
    PROTECT("protect"),
    RECOVER("recover"),
    CONNECT("connect")
}

enum class FacilityCategory(val id: String) {
    SHELTER("shelter"),
    WATER("water"),
    SUPPLY("supply"),
    FOOD("food")
}

@Serializable
data class FacilityItem(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val distKm: Double,
    val open: Boolean = true
)

data class QuizItem(
    val q: String,
    val opts: List<String>,
    val correct: Int
)

enum class SafetyStatus(val id: String) {
    SAFE("safe"),
    HELP("help"),
    UNKNOWN("unknown")
}

enum class ContactRelation(val id: String) {
    FAMILY("family"),
    FRIEND("friend")
}

@Serializable
data class Contact(
    val id: String,
    val name: String,
    val phone: String = "",
    val email: String = "",
    val relation: String = "family",
    val group: String = "",
    val status: String = "unknown",
    val statusUpdatedAt: Long? = null
)

@Serializable
data class EvacPlan(
    val meetingPoint: String = "",
    val contactName: String = "",
    val contactPhone: String = "",
    val notes: String = "",
    val updatedAt: Long? = null
)

@Serializable
data class UserAccount(
    val name: String,
    val phone: String,
    val email: String,
    val passHash: String,
    val passSalt: String,
    val createdAt: Long
)

@Serializable
data class WeatherData(
    val tempC: Int,
    val code: Int,
    val precip: Double,
    val windKph: Int,
    val fetchedAt: Long
)

@Serializable
data class GroupMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isMe: Boolean = false,
    val isSos: Boolean = false,
    val isSafeNotification: Boolean = false
)

@Serializable
data class FamilyGroup(
    val id: String,
    val name: String,
    val description: String = "",
    val memberCount: Int = 1
)

data class EmergencyHotline(
    val title: String,
    val number: String,
    val description: String,
    val iconName: String = "phone"
)
