package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "td_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val isPlayerTextDraw: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "td_elements",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class ElementEntity(
    @PrimaryKey
    val id: String,
    val projectId: Long,
    val varName: String,
    val text: String,
    val font: Int,
    val posX: Float,
    val posY: Float,
    val letterSizeX: Float,
    val letterSizeY: Float,
    val textSizeX: Float,
    val textSizeY: Float,
    val alignment: Int,
    val color: Long,
    val useBox: Boolean,
    val boxColor: Long,
    val backgroundColor: Long,
    val outline: Int,
    val shadow: Int,
    val proportional: Boolean,
    val selectable: Boolean,
    val modelId: Int,
    val modelRotX: Float,
    val modelRotY: Float,
    val modelRotZ: Float,
    val modelZoom: Float,
    val isLocked: Boolean,
    val isVisible: Boolean,
    val zIndex: Int
)
