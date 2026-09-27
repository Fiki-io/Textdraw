package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextDrawElement
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.InspectorBg
import com.example.ui.theme.InspectorBorder
import com.example.ui.theme.InspectorHeader
import com.example.ui.theme.TextMuted

/**
 * Compact Floating & Movable Inspector Window (230dp width)
 * Keeps the 640x480 canvas spacious and unobstructed on mobile landscape!
 */
@Composable
fun FloatingMovableInspector(
    element: TextDrawElement,
    onDrag: (Float, Float) -> Unit,
    onClose: () -> Unit,
    onElementChanged: (TextDrawElement) -> Unit,
    onOpenSpriteCatalog: () -> Unit,
    onOpenModelCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(250.dp) // Optimized width: perfectly balanced for mobile landscape
            .height(360.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(InspectorBg)
            .border(1.dp, InspectorBorder, RoundedCornerShape(6.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Drag handle header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InspectorHeader)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount.x, dragAmount.y)
                        }
                    }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Inspector: ${element.varName}",
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Scrollable inspector body with all original controls
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                ElementInspectorPanel(
                    element = element,
                    onElementChanged = onElementChanged,
                    onOpenSpriteCatalog = onOpenSpriteCatalog,
                    onOpenModelCatalog = onOpenModelCatalog
                )
            }
        }
    }
}
