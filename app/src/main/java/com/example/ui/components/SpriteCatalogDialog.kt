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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CyanSelection
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
fun SpriteCatalogDialog(
    onDismiss: () -> Unit,
    onSelectSprite: (String) -> Unit
) {
    val context = LocalContext.current
    val allSprites = remember { SampAssetCatalog.getSprites(context) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember {
        listOf("All") + allSprites.map { it.library }.distinct()
    }

    val filteredSprites = remember(searchQuery, selectedCategory) {
        allSprites.filter { sprite ->
            val matchCat = selectedCategory == "All" || sprite.library.equals(selectedCategory, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() ||
                    sprite.fullTag.contains(searchQuery, ignoreCase = true) ||
                    sprite.textureName.contains(searchQuery, ignoreCase = true)
            matchCat && matchQuery
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
                .testTag("dialog_sprite_catalog")
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
                        text = "TXD Sprites Catalog (${allSprites.size})",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Filter sprites (e.g. radar, arrow, cross)...", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextMuted) },
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

                    // Library Filter Chips (Horizontal Scrollable)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 9.sp, fontFamily = FontFamily.Monospace) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = Color.Black,
                                    containerColor = ButtonDark,
                                    labelColor = TextMuted
                                ),
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredSprites, key = { it.fullTag }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(InputFieldBg)
                                    .border(1.dp, InputFieldBorder, RoundedCornerShape(4.dp))
                                    .clickable {
                                        onSelectSprite(item.fullTag)
                                        onDismiss()
                                    }
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Sprite Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(ButtonDark)
                                        .border(1.dp, InputFieldBorder, RoundedCornerShape(3.dp))
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.fullTag,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.fullTag,
                                        color = CyanSelection,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "TXD: ${item.library} • Texture: ${item.textureName}",
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
