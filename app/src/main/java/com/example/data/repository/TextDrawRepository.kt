package com.example.data.repository

import com.example.data.local.ElementEntity
import com.example.data.local.ProjectEntity
import com.example.data.local.TextDrawDao
import com.example.model.TextDrawElement
import com.example.model.TextDrawProject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TextDrawRepository(private val dao: TextDrawDao) {

    val allProjects: Flow<List<TextDrawProject>> = dao.getAllProjects().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun getProject(id: Long): TextDrawProject? = dao.getProjectById(id)?.toDomain()

    suspend fun saveProject(project: TextDrawProject): Long {
        val entity = ProjectEntity(
            id = project.id,
            name = project.name,
            description = project.description,
            isPlayerTextDraw = project.isPlayerTextDraw,
            createdAt = project.createdAt,
            updatedAt = System.currentTimeMillis()
        )
        return dao.insertProject(entity)
    }

    suspend fun deleteProject(id: Long) = dao.deleteProjectById(id)

    fun getElements(projectId: Long): Flow<List<TextDrawElement>> =
        dao.getElementsByProjectId(projectId).map { list ->
            list.map { it.toDomain() }
        }

    suspend fun getElementsSync(projectId: Long): List<TextDrawElement> =
        dao.getElementsSync(projectId).map { it.toDomain() }

    suspend fun saveElements(projectId: Long, elements: List<TextDrawElement>) {
        val entities = elements.map { it.toEntity(projectId) }
        dao.replaceAllElements(projectId, entities)
    }

    suspend fun deleteElement(id: String) = dao.deleteElementById(id)

    private fun ProjectEntity.toDomain() = TextDrawProject(
        id = id,
        name = name,
        description = description,
        isPlayerTextDraw = isPlayerTextDraw,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun ElementEntity.toDomain() = TextDrawElement(
        id = id,
        varName = varName,
        text = text,
        font = font,
        posX = posX,
        posY = posY,
        letterSizeX = letterSizeX,
        letterSizeY = letterSizeY,
        textSizeX = textSizeX,
        textSizeY = textSizeY,
        alignment = alignment,
        color = color,
        useBox = useBox,
        boxColor = boxColor,
        backgroundColor = backgroundColor,
        outline = outline,
        shadow = shadow,
        proportional = proportional,
        selectable = selectable,
        modelId = modelId,
        modelRotX = modelRotX,
        modelRotY = modelRotY,
        modelRotZ = modelRotZ,
        modelZoom = modelZoom,
        isLocked = isLocked,
        isVisible = isVisible,
        zIndex = zIndex
    )

    private fun TextDrawElement.toEntity(projectId: Long) = ElementEntity(
        id = id,
        projectId = projectId,
        varName = varName,
        text = text,
        font = font,
        posX = posX,
        posY = posY,
        letterSizeX = letterSizeX,
        letterSizeY = letterSizeY,
        textSizeX = textSizeX,
        textSizeY = textSizeY,
        alignment = alignment,
        color = color,
        useBox = useBox,
        boxColor = boxColor,
        backgroundColor = backgroundColor,
        outline = outline,
        shadow = shadow,
        proportional = proportional,
        selectable = selectable,
        modelId = modelId,
        modelRotX = modelRotX,
        modelRotY = modelRotY,
        modelRotZ = modelRotZ,
        modelZoom = modelZoom,
        isLocked = isLocked,
        isVisible = isVisible,
        zIndex = zIndex
    )
}
