package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TextDrawElement
import com.example.model.TextDrawProject
import com.example.ui.theme.ButtonDark
import com.example.ui.theme.ButtonDarkBorder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.InputFieldBg
import com.example.ui.theme.InputFieldBorder
import com.example.ui.theme.InspectorBg
import com.example.ui.theme.InspectorBorder
import com.example.ui.theme.InspectorHeader
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.util.PawnCodeGenerator

@Composable
fun PawnExportDialog(
    project: TextDrawProject,
    elements: List<TextDrawElement>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val generatedCode = PawnCodeGenerator.generatePawnScript(project, elements)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(540.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(InspectorBg)
                .border(1.dp, InspectorBorder, RoundedCornerShape(6.dp))
                .testTag("dialog_pawn_export")
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
                            text = "Export: ${project.name}.pwn",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${elements.size} elements | PAWN 0.3.7 / open.mp ready",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.padding(0.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Code View Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(InputFieldBg)
                        .border(1.dp, InputFieldBorder, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = generatedCode,
                        color = Color(0xFF68D391),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }

                // Actions Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("SA-MP TextDraw Pawn Code", generatedCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).testTag("btn_copy_pawn_code")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "${project.name}.pwn")
                                putExtra(Intent.EXTRA_TEXT, generatedCode)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Script"))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = ButtonDark),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ButtonDarkBorder)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).testTag("btn_share_pawn_code")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = GoldAccent)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share .pwn", color = GoldAccent, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
