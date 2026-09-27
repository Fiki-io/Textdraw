package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TextDrawElement
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.InputFieldBg
import com.example.ui.theme.InputFieldBorder
import com.example.ui.theme.InspectorBg
import com.example.ui.theme.InspectorBorder
import com.example.ui.theme.InspectorHeader
import com.example.ui.theme.SampRed
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.util.PawnCodeParser

@Composable
fun PawnImportDialog(
    onDismiss: () -> Unit,
    onImportParsedElements: (List<TextDrawElement>) -> Unit
) {
    var rawPawnCode by remember { mutableStateOf("") }
    var parseError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(InspectorBg)
                .border(1.dp, InspectorBorder, RoundedCornerShape(6.dp))
                .testTag("dialog_pawn_import")
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
                    Column {
                        Text(
                            text = "Import Pawn Script (.pwn)",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Paste TextDrawCreate / CreatePlayerTextDraw definitions",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Column(modifier = Modifier.weight(1f).padding(8.dp)) {
                    OutlinedTextField(
                        value = rawPawnCode,
                        onValueChange = {
                            rawPawnCode = it
                            parseError = null
                        },
                        placeholder = {
                            Text(
                                "// Example:\nmy_td = TextDrawCreate(320.0, 240.0, \"TextDraw\");\nTextDrawFont(my_td, 2);\nTextDrawColor(my_td, 0xFFFFFFFF);",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(InputFieldBg, RoundedCornerShape(4.dp)),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextLight
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = InputFieldBorder
                        )
                    )
                }

                if (parseError != null) {
                    Text(
                        text = parseError!!,
                        color = SampRed,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

                // Footer Button
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Button(
                        onClick = {
                            if (rawPawnCode.isBlank()) {
                                parseError = "Script is empty."
                                return@Button
                            }
                            val parsed = PawnCodeParser.parseScript(rawPawnCode)
                            if (parsed.isEmpty()) {
                                parseError = "No valid TextDraws found in pasted code."
                            } else {
                                onImportParsedElements(parsed)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_confirm_import")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Import", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Parse & Load Elements",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
