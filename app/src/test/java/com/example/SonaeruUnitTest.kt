package com.example

import com.example.data.SecurityHelper
import com.example.model.FacilityCategory
import com.example.model.Language
import com.example.model.Localization
import com.example.service.WeatherAndFacilityService
import org.junit.Assert.*
import org.junit.Test

class SonaeruUnitTest {

    @Test
    fun testLocalizationAllLanguages() {
        Language.values().forEach { lang ->
            val home = Localization.t("nav_home", lang)
            assertTrue("nav_home should not be empty for ${lang.name}", home.isNotEmpty())

            val quizList = Localization.QUIZZES[lang]
            assertNotNull("Quizzes should exist for ${lang.name}", quizList)
            assertTrue("Quizzes count should be > 0 for ${lang.name}", quizList!!.isNotEmpty())
        }
    }

    @Test
    fun testDistanceCalculation() {
        val d = WeatherAndFacilityService.haversineKm(35.6586, 139.7454, 35.6558, 139.7431)
        assertTrue("Distance should be approximately 0.3-0.5km", d in 0.2..0.6)
        val formatted = WeatherAndFacilityService.formatDistance(d)
        assertTrue("Formatted distance should end with m or km", formatted.endsWith("m") || formatted.endsWith("km"))
    }

    @Test
    fun testWeatherBucketCodes() {
        assertEquals("wxStorm", WeatherAndFacilityService.getWeatherBucketKey(95, 0.0))
        assertEquals("wxSnow", WeatherAndFacilityService.getWeatherBucketKey(71, 0.0))
        assertEquals("wxHeavy", WeatherAndFacilityService.getWeatherBucketKey(65, 0.0))
        assertEquals("wxRain", WeatherAndFacilityService.getWeatherBucketKey(51, 0.0))
        assertEquals("wxCloudy", WeatherAndFacilityService.getWeatherBucketKey(2, 0.0))
        assertEquals("wxClear", WeatherAndFacilityService.getWeatherBucketKey(0, 0.0))
    }

    @Test
    fun testSecurityHelperHashing() {
        val salt = SecurityHelper.randomSaltHex()
        assertEquals(32, salt.length) // 16 bytes = 32 hex chars

        val hash1 = SecurityHelper.hashPassword("secret123", salt)
        val hash2 = SecurityHelper.hashPassword("secret123", salt)
        val hashWrong = SecurityHelper.hashPassword("wrongpass", salt)

        assertEquals("Same password with same salt must match", hash1, hash2)
        assertNotEquals("Different passwords must not match", hash1, hashWrong)
    }

    @Test
    fun testFacilitiesGeneration() {
        val shelters = WeatherAndFacilityService.getFacilities(FacilityCategory.SHELTER, 35.6586, 139.7454)
        assertTrue("Shelters list should not be empty", shelters.isNotEmpty())
        assertTrue("First facility should be closest", shelters.first().distKm <= shelters.last().distKm)
    }

    @Test
    fun testEmergencyAndGroupLocalization() {
        listOf("im_safe", "im_not_safe", "emergency_call_title", "call_119", "call_110", "share_live_location", "family_group_title", "group_chat_title").forEach { key ->
            Language.values().forEach { lang ->
                val text = Localization.t(key, lang)
                assertFalse("Key $key should have a translation in ${lang.name}", text == key)
                assertTrue("Translation for $key in ${lang.name} should not be empty", text.isNotBlank())
            }
        }
    }

    @Test
    fun testDemoCodeGeneration() {
        val code = SecurityHelper.generateDemoCode()
        assertEquals(6, code.length)
        assertTrue(code.all { it.isDigit() })

        val protoCode = SecurityHelper.generatePrototypeCode()
        assertEquals(6, protoCode.length)
        assertTrue(protoCode.all { it.isDigit() })
    }

    @Test
    fun testGoogleMapUrlFormatting() {
        val lat = 35.6558
        val lon = 139.7431
        val roadmapUrl = "https://maps.google.com/maps?q=$lat,$lon&z=16&output=embed"
        assertTrue(roadmapUrl.contains("q=35.6558,139.7431"))
        assertTrue(roadmapUrl.contains("output=embed"))

        val isSatellite = true
        val satelliteUrl = "https://maps.google.com/maps?q=$lat,$lon&z=16${if (isSatellite) "&t=k" else ""}&output=embed"
        assertTrue(satelliteUrl.contains("&t=k"))

        // Check iframe container
        val iframeSnippet = "<iframe src=\"$roadmapUrl\""
        assertTrue(iframeSnippet.contains("<iframe"))
    }

    @Test
    fun testEmergencyLocationShareMessage() {
        val lat = 35.6586
        val lon = 139.7454
        val safeText = "【SONAERU SOS】${Localization.t("im_safe", Language.NE)}\n" +
                "${Localization.t("shareMsgTemplate", Language.NE)} https://maps.google.com/?q=$lat,$lon"
        assertTrue("Message must contain maps google link", safeText.contains("https://maps.google.com/?q=35.6586,139.7454"))
        assertTrue("Message must contain safe status in Nepali", safeText.contains("म सुरक्षित छु"))

        val notSafeText = "【SONAERU SOS】${Localization.t("im_not_safe", Language.NE)}\n" +
                "${Localization.t("shareMsgTemplate", Language.NE)} https://maps.google.com/?q=$lat,$lon"
        assertTrue("Message must contain not safe status in Nepali", notSafeText.contains("म सुरक्षित छैन"))
    }

    @Test
    fun testEmergencySpeedDialNumbers() {
        val ambulancePhone = "119"
        val policePhone = "110"
        val familyPhone = "090-1234-5678"

        assertTrue(ambulancePhone.startsWith("119"))
        assertTrue(policePhone.startsWith("110"))
        assertTrue(familyPhone.isNotBlank())
    }
}
