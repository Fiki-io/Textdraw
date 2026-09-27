package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.TextDrawRepository
import com.example.ui.screens.EditorStudioScreen
import com.example.ui.screens.ProjectListScreen
import com.example.ui.theme.SAMPTextDrawStudioTheme
import com.example.viewmodel.EditorUiState
import com.example.viewmodel.TextDrawViewModel
import com.example.viewmodel.TextDrawViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: TextDrawViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = TextDrawRepository(database.textDrawDao())
        val factory = TextDrawViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TextDrawViewModel::class.java]

        setContent {
            SAMPTextDrawStudioTheme {
                val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
                val projects = viewModel.allProjects.collectAsStateWithLifecycle().value
                val elements = viewModel.elements.collectAsStateWithLifecycle().value
                val selectedId = viewModel.selectedElementId.collectAsStateWithLifecycle().value
                val showGrid = viewModel.showGrid.collectAsStateWithLifecycle().value
                val showGuides = viewModel.showGuides.collectAsStateWithLifecycle().value

                when (uiState) {
                    is EditorUiState.ProjectList -> {
                        ProjectListScreen(
                            projects = projects,
                            onOpenProject = { viewModel.openProject(it) },
                            onCreateProject = { name, isPlayer -> viewModel.createNewProject(name, isPlayer) },
                            onDeleteProject = { id -> viewModel.deleteProject(id) }
                        )
                    }
                    is EditorUiState.ActiveProject -> {
                        EditorStudioScreen(
                            project = uiState.project,
                            viewModel = viewModel,
                            elements = elements,
                            selectedElementId = selectedId,
                            showGrid = showGrid,
                            showGuides = showGuides,
                            onBack = { viewModel.closeProject() }
                        )
                    }
                }
            }
        }
    }
}
