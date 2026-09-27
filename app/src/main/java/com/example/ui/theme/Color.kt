package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// GTA SA-MP Pro Dark Suite Colors
val SampDarkBg = Color(0xFF0F1115)           // Screen Background
val SampCanvasBg = Color(0xFF13161C)         // Inner Virtual 640x480 Canvas Dark Grid
val SampGridLineColor = Color(0xFF1A1F29)    // Subtle Grid
val SampCenterGuideColor = Color(0xFFD69E2E) // Center Orange/Gold Crossline
val SampCanvasBorder = Color(0xFFD69E2E)     // Yellow/Gold 1px Canvas Border

// Floating Inspector Panel Colors (Window Frame with Gold Border)
val InspectorBg = Color(0xEE161A22)          // Semi-translucent dark slate
val InspectorBorder = Color(0xFFD69E2E)      // Gold accent border
val InspectorHeader = Color(0xFF1E232E)      // Header
val InputFieldBg = Color(0xFF0F1218)         // Input background
val InputFieldBorder = Color(0xFF282E3D)     // Input border

// Accent Colors
val GoldAccent = Color(0xFFF6AD55)           // Primary Gold Accent
val CyanSelection = Color(0xFF00E5FF)        // Selected Cyan Box & Tag
val CyanAccent = Color(0xFF00E5FF)
val TextLight = Color(0xFFE2E8F0)
val TextMuted = Color(0xFF8C96A5)
val ButtonDark = Color(0xFF1C212B)
val ButtonDarkBorder = Color(0xFF2E3748)

// SA-MP Color Tag Pills (~r~, ~g~, ~b~, ~y~, ~w~, ~s~, ~h~, ~n~, ~p~)
val SampRed = Color(0xFFE53E3E)
val SampGreen = Color(0xFF38A169)
val SampBlue = Color(0xFF3182CE)
val SampYellow = Color(0xFFD69E2E)
val SampWhite = Color(0xFFEDF2F7)
val SampGray = Color(0xFFA0AEC0)
val SampPurple = Color(0xFF9F7AEA)

// Aliases for compatibility
val NeonCyan = CyanSelection
val AmberAccent = GoldAccent
val DarkNavyCanvas = SampDarkBg
val DarkNavySurface = InspectorBg
val DarkNavySurfaceVariant = InspectorHeader
val DarkNavyBorder = InputFieldBorder
val TextPrimary = TextLight
val TextSecondary = TextMuted
val ErrorRed = SampRed
val SuccessGreen = SampGreen

// ImGui compatibility aliases
val ImGuiBg = SampDarkBg
val ImGuiChildBg = InspectorBg
val ImGuiPopupBg = InspectorBg
val ImGuiBorder = InputFieldBorder
val ImGuiTitleBg = InspectorHeader
val ImGuiTitleBgActive = InspectorHeader
val ImGuiMenuBarBg = SampDarkBg
val ImGuiFrameBg = InputFieldBg
val ImGuiButton = ButtonDark
val ImGuiButtonActive = Color(0xFF2E659A)
val ImGuiButtonHovered = Color(0xFF3D80C4)
val ImGuiCyanAccent = CyanSelection
val ImGuiGoldAccent = GoldAccent
val ImGuiText = TextLight
val ImGuiTextDisabled = TextMuted
val ImGuiRed = SampRed
val ImGuiGreen = SampGreen
val ImGuiHeader = InspectorHeader
val SampCanvasBackground = SampCanvasBg
val SampGridLine = SampGridLineColor
val SampCenterLine = SampCenterGuideColor
val SelectedHandleColor = CyanSelection
