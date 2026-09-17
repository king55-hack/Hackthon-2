package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.GoogleMapView

@Composable
fun RecoverScreen(
    lang: Language,
    selectedCategory: FacilityCategory,
    facilities: List<FacilityItem>,
    userCoords: Pair<Double, Double>,
    onSelectCategory: (FacilityCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var focusedFacilityId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        Pair(FacilityCategory.SHELTER, "shelter"),
        Pair(FacilityCategory.WATER, "water"),
        Pair(FacilityCategory.SUPPLY, "supply"),
        Pair(FacilityCategory.FOOD, "food")
    )

    val activeFacility = facilities.firstOrNull { it.id == focusedFacilityId } ?: facilities.firstOrNull()
    val targetLat = activeFacility?.lat ?: userCoords.first
    val targetLon = activeFacility?.lon ?: userCoords.second
    val targetTitle = activeFacility?.name ?: Localization.t("nearest", lang)
    val targetDistKm = activeFacility?.distKm

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Multilingual Info Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BlueRecoverSoft),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BlueRecover.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = BlueRecover, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.t("langInfo", lang),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BlueRecover
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { (cat, key) ->
                    val isSelected = cat == selectedCategory
                    val chipBg = if (isSelected) BlueRecover else Color.White
                    val chipTextColor = if (isSelected) Color.White else Ink

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = chipBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BlueRecover else LineBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onSelectCategory(cat)
                                focusedFacilityId = null
                            }
                            .testTag("cat_chip_${cat.id}")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = Localization.t(key, lang),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = chipTextColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Category Note (e.g. for supply or food)
            if (selectedCategory == FacilityCategory.SUPPLY || selectedCategory == FacilityCategory.FOOD) {
                val noteKey = if (selectedCategory == FacilityCategory.SUPPLY) "supplyNote" else "foodNote"
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "※ ${Localization.t(noteKey, lang)}",
                    fontSize = 11.5.sp,
                    color = Muted,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Google Maps Integration (Interactive Map + Satellite / Roadmap toggle + Direct app open)
            GoogleMapView(
                lat = targetLat,
                lon = targetLon,
                title = targetTitle,
                distKm = targetDistKm,
                lang = lang
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        // List of Facilities
        items(facilities) { facility ->
            val isFocused = facility.id == activeFacility?.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isFocused) BlueRecoverSoft.copy(alpha = 0.5f) else CardBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isFocused) BlueRecover else LineBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { focusedFacilityId = facility.id }
                    .testTag("facility_card_${facility.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = facility.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Ink
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "${Localization.t("dist", lang)}: ${(facility.distKm * 1000).toInt()}m",
                                fontSize = 12.sp,
                                color = Muted
                            )
                            if (facility.open) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GreenOkSoft
                                ) {
                                    Text(
                                        text = Localization.t("open", lang),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenOk,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Open navigation directly in Google Maps
                    IconButton(
                        onClick = {
                            val uri = Uri.parse("geo:${facility.lat},${facility.lon}?q=${facility.lat},${facility.lon}(${Uri.encode(facility.name)})")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${facility.lat},${facility.lon}"))
                                context.startActivity(webIntent)
                            }
                        },
                        modifier = Modifier.testTag("nav_btn_${facility.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = "Navigate in Google Maps",
                            tint = BlueRecover
                        )
                    }
                }
            }
        }

        // Official Links Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = PanelBg),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = Localization.t("resOfficial", lang),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OfficialLinkRow(
                        title = Localization.t("resJMA", lang),
                        url = "https://www.jma.go.jp/bosai/warning/",
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jma.go.jp/bosai/warning/")))
                        }
                    )

                    HorizontalDivider(color = LineBorder, modifier = Modifier.padding(vertical = 6.dp))

                    OfficialLinkRow(
                        title = Localization.t("resHazard", lang),
                        url = "https://disaportal.gsi.go.jp/",
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://disaportal.gsi.go.jp/")))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun OfficialLinkRow(
    title: String,
    url: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.5.sp,
            color = Navy,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = Muted,
            modifier = Modifier.size(16.dp)
        )
    }
}
