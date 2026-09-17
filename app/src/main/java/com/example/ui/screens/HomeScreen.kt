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
import androidx.compose.runtime.Composable
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
import com.example.service.WeatherAndFacilityService
import com.example.theme.*

@Composable
fun HomeScreen(
    phase: Phase,
    lang: Language,
    points: Int,
    weather: WeatherData?,
    weatherStatus: String,
    placeName: String,
    onNavigateTab: (AppTab) -> Unit,
    onRefreshWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDisaster = phase == Phase.HASSAI
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Disaster Alert Card (only visible in disaster mode)
        if (isDisaster) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RedAlertSoft),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, RedAlert, RoundedCornerShape(12.dp))
                    .testTag("disaster_alert_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RedAlert,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.t("hassai_t", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedAlertDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.t("hassai_s", lang),
                        fontSize = 13.sp,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigateTab(AppTab.PROTECT) },
                        colors = ButtonDefaults.buttonColors(containerColor = RedAlert),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_open_protect_btn")
                    ) {
                        Text(
                            text = Localization.t("openProtect", lang),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Disaster Points Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("points_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Amber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Amber,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Localization.t("points", lang),
                            fontSize = 13.sp,
                            color = Muted
                        )
                        Text(
                            text = "$points ${Localization.t("pointsUnit", lang)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenOkSoft,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "Lv. 2",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenOk,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Weather & Location Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("weather_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = TealAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Localization.t("weather", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                    }
                    // Refresh button
                    IconButton(
                        onClick = onRefreshWeather,
                        modifier = Modifier.size(28.dp).testTag("refresh_weather_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = Localization.t("refreshLoc", lang),
                            tint = Muted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (weather != null) {
                    val weatherKey = WeatherAndFacilityService.getWeatherBucketKey(weather.code, weather.precip)
                    val weatherLabel = Localization.t(weatherKey, lang)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${weather.tempC}°C",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink
                            )
                            Text(
                                text = weatherLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealAccent
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${Localization.t("wind", lang)}: ${weather.windKph} km/h",
                                fontSize = 12.5.sp,
                                color = Muted
                            )
                            if (weather.precip > 0.0) {
                                Text(
                                    text = "降水量: ${weather.precip} mm",
                                    fontSize = 12.5.sp,
                                    color = TealAccent
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = Localization.t("weatherLoading", lang),
                        fontSize = 13.sp,
                        color = Muted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = LineBorder)
                Spacer(modifier = Modifier.height(8.dp))

                // Location line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = placeName,
                        fontSize = 12.5.sp,
                        color = Ink,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TealAccentSoft
                    ) {
                        Text(
                            text = Localization.t("live", lang),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Flood Risk Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                .testTag("flood_risk_card")
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

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://disaportal.gsi.go.jp/"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealAccent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_hazard_map_btn")
                ) {
                    Text(
                        text = Localization.t("seeMap", lang),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Quick Navigation Shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onNavigateTab(AppTab.PREPARE) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quick_nav_prepare_btn")
            ) {
                Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(Localization.t("nav_prepare", lang), fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onNavigateTab(AppTab.RECOVER) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueRecover),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quick_nav_recover_btn")
            ) {
                Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(Localization.t("nav_recover", lang), fontWeight = FontWeight.Bold)
            }
        }

        // Official Resources Links
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.t("resOfficial", lang),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jma.go.jp/bosai/warning/"))
                            context.startActivity(intent)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = TealAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Localization.t("resJMA", lang), fontSize = 13.sp, color = Ink, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
                }

                HorizontalDivider(color = LineBorder)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://disaportal.gsi.go.jp/"))
                            context.startActivity(intent)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = TealAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Localization.t("resHazard", lang), fontSize = 13.sp, color = Ink, modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Muted, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
