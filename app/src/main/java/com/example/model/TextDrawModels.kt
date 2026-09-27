package com.example.model

/**
 * TextDraw Element representation matching SA-MP / Open.MP TextDraw Engine.
 * Coordinates are mapped to virtual 640x480 resolution.
 */
data class TextDrawElement(
    val id: String,
    val varName: String,
    val text: String = "New TextDraw",
    val font: Int = 1, // 0: Bank Gothic, 1: Standard Chalet, 2: Pricedown GTA, 3: Diploma Old English, 4: Sprite, 5: Model Preview
    val posX: Float = 320f,
    val posY: Float = 240f,
    val letterSizeX: Float = 0.45f,
    val letterSizeY: Float = 1.8f,
    val textSizeX: Float = 120f, // Width/End-X in SA-MP
    val textSizeY: Float = 30f,  // Height
    val alignment: Int = 1, // 1: Left, 2: Center, 3: Right
    val color: Long = 0xFFFFFFFFL, // 0xRRGGBBAA format
    val useBox: Boolean = false,
    val boxColor: Long = 0x00000080L, // Semi-transparent black
    val backgroundColor: Long = 0x000000FFL,
    val outline: Int = 1, // 0 or size
    val shadow: Int = 0,
    val proportional: Boolean = true,
    val selectable: Boolean = false,
    // For Font 5 (Model 3D Preview)
    val modelId: Int = 0, // Skin or Vehicle or Object ID
    val modelRotX: Float = 0f,
    val modelRotY: Float = 0f,
    val modelRotZ: Float = 0f,
    val modelZoom: Float = 1.0f,
    // Layer and lock properties
    val isLocked: Boolean = false,
    val isVisible: Boolean = true,
    val zIndex: Int = 0
)

/**
 * Project model containing multiple TextDraw elements
 */
data class TextDrawProject(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val isPlayerTextDraw: Boolean = false, // false: TextDraw (Global), true: PlayerTextDraw
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
