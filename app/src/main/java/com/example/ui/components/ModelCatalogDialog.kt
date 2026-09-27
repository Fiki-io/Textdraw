package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.theme.ButtonDark
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.InputFieldBg
import com.example.ui.theme.InputFieldBorder
import com.example.ui.theme.InspectorBg
import com.example.ui.theme.InspectorBorder
import com.example.ui.theme.InspectorHeader
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.util.SampAssetCatalog

@Composable
fun ModelCatalogDialog(
    onDismiss: () -> Unit,
    onSelectModel: (Int) -> Unit
) {
    val context = LocalContext.current
    val allVehicles = remember { SampAssetCatalog.getVehicles(context) }
    val allSkins = remember { SampAssetCatalog.getSkins(context) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredVehicles = remember(searchQuery) {
        allVehicles.filter { v ->
            searchQuery.isBlank() ||
                    v.name.contains(searchQuery, ignoreCase = true) ||
                    v.id.toString() == searchQuery.trim() ||
                    v.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredSkins = remember(searchQuery) {
        allSkins.filter { s ->
            searchQuery.isBlank() ||
                    s.name.contains(searchQuery, ignoreCase = true) ||
                    s.id.toString() == searchQuery.trim()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(InspectorBg)
                .border(1.dp, InspectorBorder, RoundedCornerShape(6.dp))
                .testTag("dialog_model_catalog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InspectorHeader)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3D Preview Model Catalog",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Tabs (Vehicles & Skins)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ButtonDark,
                    contentColor = GoldAccent
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "Vehicles (${allVehicles.size})",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) GoldAccent else TextMuted
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "Skins (${allSkins.size})",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) GoldAccent else TextMuted
                            )
                        }
                    )
                }

                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name or model ID...", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = GoldAccent, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = InputFieldBorder,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (selectedTab == 0) {
                            items(filteredVehicles, key = { it.id }) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(InputFieldBg)
                                        .border(1.dp, InputFieldBorder, RoundedCornerShape(4.dp))
                                        .clickable {
                                            onSelectModel(item.id)
                                            onDismiss()
                                        }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Vehicle Thumbnail
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(ButtonDark)
                                            .border(1.dp, InputFieldBorder, RoundedCornerShape(3.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = SampAssetCatalog.get3DModelImageUrl(item.id),
                                            contentDescription = item.name,
                                            modifier = Modifier.size(42.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            color = TextLight,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "ID: ${item.id} • Class: ${item.category}",
                                            color = TextMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredSkins, key = { it.id }) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(InputFieldBg)
                                        .border(1.dp, InputFieldBorder, RoundedCornerShape(4.dp))
                                        .clickable {
                                            onSelectModel(item.id)
                                            onDismiss()
                                        }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Skin Thumbnail
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(ButtonDark)
                                            .border(1.dp, InputFieldBorder, RoundedCornerShape(3.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = SampAssetCatalog.get3DModelImageUrl(item.id),
                                            contentDescription = item.name,
                                            modifier = Modifier.size(42.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.name,
                                            color = TextLight,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Skin ID: ${item.id}",
                                            color = TextMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
