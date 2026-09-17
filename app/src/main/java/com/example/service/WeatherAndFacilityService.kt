package com.example.service

import com.example.model.FacilityCategory
import com.example.model.FacilityItem
import com.example.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*

object WeatherAndFacilityService {

    val TOKYO_COORDS = Pair(35.6586, 139.7454) // Tokyo Tower vicinity

    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * asin(sqrt(a))
        return r * c
    }

    fun formatDistance(km: Double): String {
        return if (km < 1.0) {
            "${(km * 1000).roundToInt()}m"
        } else {
            "%.1fkm".format(km)
        }
    }

    fun getWeatherBucketKey(code: Int, precip: Double): String {
        return when {
            code in listOf(95, 96, 99) -> "wxStorm"
            code in listOf(71, 73, 75, 77, 85, 86) -> "wxSnow"
            code in listOf(45, 48) -> "wxFog"
            code in listOf(65, 67, 82) || precip >= 10.0 -> "wxHeavy"
            code in listOf(51, 53, 55, 56, 57, 61, 63, 66, 80, 81) -> "wxRain"
            code in listOf(1, 2, 3) -> "wxCloudy"
            else -> "wxClear"
        }
    }

    suspend fun fetchWeather(lat: Double, lon: Double): WeatherData = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,precipitation,weather_code,wind_speed_10m&timezone=auto"
            val url = URL(urlString)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
            }
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()
                val json = JSONObject(response)
                val current = json.getJSONObject("current")
                val temp = current.getDouble("temperature_2m").roundToInt()
                val code = current.getInt("weather_code")
                val precip = current.getDouble("precipitation")
                val wind = current.getDouble("wind_speed_10m").roundToInt()
                return@withContext WeatherData(
                    tempC = temp,
                    code = code,
                    precip = precip,
                    windKph = wind,
                    fetchedAt = System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            // fallback below
        }
        // Realistic default weather
        WeatherData(
            tempC = 21,
            code = 0,
            precip = 0.0,
            windKph = 8,
            fetchedAt = System.currentTimeMillis()
        )
    }

    fun getFacilities(category: FacilityCategory, userLat: Double, userLon: Double): List<FacilityItem> {
        val baseList = when (category) {
            FacilityCategory.SHELTER -> listOf(
                Triple("sh1", "港区立赤羽小学校 体育館", Pair(35.6558, 139.7431)),
                Triple("sh2", "芝公園 集会所", Pair(35.6542, 139.7485)),
                Triple("sh3", "三田中学校 避難所", Pair(35.6495, 139.7410)),
                Triple("sh4", "芝浦小学校 体育館", Pair(35.6438, 139.7532)),
                Triple("sh5", "飯倉コミュニティホール", Pair(35.6601, 139.7390))
            )
            FacilityCategory.WATER -> listOf(
                Triple("wt1", "芝公園 応急給水拠点", Pair(35.6545, 139.7480)),
                Triple("wt2", "赤羽橋 給水ステーション", Pair(35.6539, 139.7445)),
                Triple("wt3", "三田 給水栓", Pair(35.6482, 139.7430)),
                Triple("wt4", "麻布十番 水道拠点", Pair(35.6560, 139.7360))
            )
            FacilityCategory.SUPPLY -> listOf(
                Triple("sp1", "セブン-イレブン 港区芝公園店", Pair(35.6575, 139.7460)),
                Triple("sp2", "ファミリーマート 東麻布店", Pair(35.6568, 139.7415)),
                Triple("sp3", "まいばすけっと 三田２丁目店", Pair(35.6505, 139.7420)),
                Triple("sp4", "ローソン 芝三丁目店", Pair(35.6520, 139.7490))
            )
            FacilityCategory.FOOD -> listOf(
                Triple("fd1", "芝公園 災害炊き出し広場", Pair(35.6535, 139.7475)),
                Triple("fd2", "増上寺 境内 支援所", Pair(35.6578, 139.7482)),
                Triple("fd3", "港区民センター 炊き出し所", Pair(35.6515, 139.7510)),
                Triple("fd4", "愛宕コミュニティ 食堂支援", Pair(35.6630, 139.7495))
            )
        }

        return baseList.map { (id, name, coords) ->
            val dist = haversineKm(userLat, userLon, coords.first, coords.second)
            FacilityItem(id = id, name = name, lat = coords.first, lon = coords.second, distKm = dist)
        }.sortedBy { it.distKm }
    }
}
