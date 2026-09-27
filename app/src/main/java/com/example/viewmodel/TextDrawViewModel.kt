package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.TextDrawRepository
import com.example.model.TextDrawElement
import com.example.model.TextDrawProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface EditorUiState {
    data object ProjectList : EditorUiState
    data class ActiveProject(val project: TextDrawProject) : EditorUiState
}

class TextDrawViewModel(private val repository: TextDrawRepository) : ViewModel() {

    val allProjects: StateFlow<List<TextDrawProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<EditorUiState>(EditorUiState.ProjectList)
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val _elements = MutableStateFlow<List<TextDrawElement>>(emptyList())
    val elements: StateFlow<List<TextDrawElement>> = _elements.asStateFlow()

    private val _selectedElementId = MutableStateFlow<String?>(null)
    val selectedElementId: StateFlow<String?> = _selectedElementId.asStateFlow()

    private val _showGrid = MutableStateFlow(true)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    private val _showGuides = MutableStateFlow(true)
    val showGuides: StateFlow<Boolean> = _showGuides.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<List<TextDrawElement>>()
    private val redoStack = mutableListOf<List<TextDrawElement>>()

    fun openProject(project: TextDrawProject) {
        _uiState.value = EditorUiState.ActiveProject(project)
        viewModelScope.launch {
            val list = repository.getElementsSync(project.id)
            _elements.value = list
            _selectedElementId.value = list.firstOrNull()?.id
            undoStack.clear()
            redoStack.clear()
        }
    }

    fun closeProject() {
        val currState = _uiState.value
        if (currState is EditorUiState.ActiveProject) {
            viewModelScope.launch {
                repository.saveElements(currState.project.id, _elements.value)
            }
        }
        _uiState.value = EditorUiState.ProjectList
        _elements.value = emptyList()
        _selectedElementId.value = null
    }

    fun createNewProject(name: String, isPlayerTextDraw: Boolean = false) {
        viewModelScope.launch {
            val project = TextDrawProject(
                name = name.ifBlank { "Untitled TD" },
                isPlayerTextDraw = isPlayerTextDraw
            )
            val newId = repository.saveProject(project)
            val created = project.copy(id = newId)
            openProject(created)
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    fun selectElement(id: String?) {
        _selectedElementId.value = id
    }

    fun toggleGrid() {
        _showGrid.value = !_showGrid.value
    }

    fun toggleGuides() {
        _showGuides.value = !_showGuides.value
    }

    fun updateElement(updated: TextDrawElement) {
        saveSnapshotForUndo()
        _elements.value = _elements.value.map { if (it.id == updated.id) updated else it }
        autoSave()
    }

    fun moveElement(id: String, newX: Float, newY: Float) {
        _elements.value = _elements.value.map {
            if (it.id == id && !it.isLocked) {
                if (it.font == 4 || it.font == 5) {
                    // SA-MP sprites/models: textSizeX = endX, textSizeY = endY
                    // Shift endX and endY by the same delta to keep size constant
                    val deltaX = newX - it.posX
                    val deltaY = newY - it.posY
                    it.copy(
                        posX = newX,
                        posY = newY,
                        textSizeX = (it.textSizeX + deltaX).coerceAtMost(640f),
                        textSizeY = (it.textSizeY + deltaY).coerceAtMost(480f)
                    )
                } else {
                    it.copy(posX = newX, posY = newY)
                }
            } else it
        }
        autoSave()
    }

    fun nudgeSelectedElement(deltaX: Float, deltaY: Float) {
        val currentId = _selectedElementId.value ?: return
        val current = _elements.value.find { it.id == currentId } ?: return
        if (current.isLocked) return

        saveSnapshotForUndo()
        val nextX = (current.posX + deltaX).coerceIn(0f, 640f)
        val nextY = (current.posY + deltaY).coerceIn(0f, 480f)

        val updated = if (current.font == 4 || current.font == 5) {
            // SA-MP sprites/models: keep size by also shifting endX/endY
            current.copy(
                posX = nextX,
                posY = nextY,
                textSizeX = (current.textSizeX + deltaX).coerceIn(0f, 640f),
                textSizeY = (current.textSizeY + deltaY).coerceIn(0f, 480f)
            )
        } else {
            current.copy(posX = nextX, posY = nextY)
        }
        updateElement(updated)
    }

    fun addNewElement(
        varName: String = "TD_Element_${_elements.value.size + 1}",
        text: String = "GTA San Andreas",
        font: Int = 1  // Font 1 = Standard/Chalet — most common SA-MP TextDraw font
    ) {
        saveSnapshotForUndo()
        val newEl = TextDrawElement(
            id = UUID.randomUUID().toString(),
            varName = varName,
            text = text,
            font = font,
            posX = 320f,
            posY = 240f,
            letterSizeX = 0.5f,
            letterSizeY = 2.0f,
            textSizeX = 140f,
            textSizeY = 32f,
            zIndex = _elements.value.size
        )
        _elements.value = _elements.value + newEl
        _selectedElementId.value = newEl.id
        autoSave()
    }

    fun addNewSpriteElement(spriteTag: String = "hud:radar_light") {
        saveSnapshotForUndo()
        val count = _elements.value.size + 1
        // Offset slightly so sprites don’t stack on each other
        val offset = (count * 15f) % 120f
        val spX = 260f + offset
        val spY = 180f + offset
        val spW = 40f  // desired sprite width in SA-MP virtual units
        val spH = 40f  // desired sprite height
        val newEl = TextDrawElement(
            id = UUID.randomUUID().toString(),
            varName = "TD_Sprite_$count",
            text = spriteTag,
            font = 4,
            posX = spX,
            posY = spY,
            letterSizeX = 0.0f,
            letterSizeY = 0.0f,
            // SA-MP: textSizeX = END-X (right edge), textSizeY = END-Y (bottom edge)
            textSizeX = spX + spW,
            textSizeY = spY + spH,
            color = 0xFFFFFFFFL,
            zIndex = _elements.value.size
        )
        _elements.value = _elements.value + newEl
        _selectedElementId.value = newEl.id
        autoSave()
    }

    fun addNewModelElement(modelId: Int = 411) {
        saveSnapshotForUndo()
        val count = _elements.value.size + 1
        val offset = (count * 15f) % 120f
        val mdX = 260f + offset
        val mdY = 180f + offset
        val mdW = 65f  // desired model frame width
        val mdH = 65f  // desired model frame height
        val newEl = TextDrawElement(
            id = UUID.randomUUID().toString(),
            varName = "TD_Model_$count",
            text = "LD_SPAC:white",
            font = 5,
            modelId = modelId,
            posX = mdX,
            posY = mdY,
            letterSizeX = 0.0f,
            letterSizeY = 0.0f,
            // SA-MP: textSizeX = END-X, textSizeY = END-Y
            textSizeX = mdX + mdW,
            textSizeY = mdY + mdH,
            color = 0xFFFFFFFFL,
            zIndex = _elements.value.size
        )
        _elements.value = _elements.value + newEl
        _selectedElementId.value = newEl.id
        autoSave()
    }

    fun duplicateElement(element: TextDrawElement) {
        saveSnapshotForUndo()
        val cloned = element.copy(
            id = UUID.randomUUID().toString(),
            varName = "${element.varName}_copy",
            posX = (element.posX + 10f).coerceAtMost(630f),
            posY = (element.posY + 10f).coerceAtMost(470f),
            zIndex = _elements.value.size
        )
        _elements.value = _elements.value + cloned
        _selectedElementId.value = cloned.id
        autoSave()
    }

    fun deleteElement(id: String) {
        saveSnapshotForUndo()
        _elements.value = _elements.value.filter { it.id != id }
        if (_selectedElementId.value == id) {
            _selectedElementId.value = _elements.value.firstOrNull()?.id
        }
        autoSave()
    }

    fun toggleLock(id: String) {
        _elements.value = _elements.value.map {
            if (it.id == id) it.copy(isLocked = !it.isLocked) else it
        }
        autoSave()
    }

    fun toggleVisibility(id: String) {
        _elements.value = _elements.value.map {
            if (it.id == id) it.copy(isVisible = !it.isVisible) else it
        }
        autoSave()
    }

    fun swapZIndex(zIndexA: Int, zIndexB: Int) {
        saveSnapshotForUndo()
        val itemA = _elements.value.find { it.zIndex == zIndexA }
        val itemB = _elements.value.find { it.zIndex == zIndexB }
        if (itemA != null && itemB != null) {
            _elements.value = _elements.value.map {
                when (it.id) {
                    itemA.id -> it.copy(zIndex = zIndexB)
                    itemB.id -> it.copy(zIndex = zIndexA)
                    else -> it
                }
            }
            autoSave()
        }
    }

    fun importElements(imported: List<TextDrawElement>) {
        if (imported.isEmpty()) return
        saveSnapshotForUndo()
        val startIndex = _elements.value.size
        val indexed = imported.mapIndexed { idx, el -> el.copy(zIndex = startIndex + idx) }
        _elements.value = _elements.value + indexed
        _selectedElementId.value = indexed.firstOrNull()?.id
        autoSave()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_elements.value)
            val previous = undoStack.removeAt(undoStack.lastIndex)
            _elements.value = previous
            autoSave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_elements.value)
            val next = redoStack.removeAt(redoStack.lastIndex)
            _elements.value = next
            autoSave()
        }
    }

    private fun saveSnapshotForUndo() {
        undoStack.add(_elements.value)
        redoStack.clear()
        if (undoStack.size > 25) {
            undoStack.removeAt(0)
        }
    }

    private fun autoSave() {
        val currState = _uiState.value
        if (currState is EditorUiState.ActiveProject) {
            viewModelScope.launch {
                repository.saveElements(currState.project.id, _elements.value)
            }
        }
    }
}

class TextDrawViewModelFactory(private val repository: TextDrawRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TextDrawViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TextDrawViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
