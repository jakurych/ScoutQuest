package com.example.scoutquest.ui.navigation

import RegisterView
import RegisterViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scoutquest.ui.views.*
import com.example.scoutquest.ui.views.tasktypes.CreateQuizView
import com.example.scoutquest.ui.views.tasktypes.CreateNoteView
import com.example.scoutquest.viewmodels.*
import com.example.scoutquest.data.models.tasktypes.Note
import com.example.scoutquest.viewmodels.tasktypes.NoteViewModel
import com.example.scoutquest.viewmodels.tasktypes.QuizViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val userViewModel: UserViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val createNewGameViewModel: CreateNewGameViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val joinGameViewModel: JoinGameViewModel = viewModel()
    val quizViewModel: QuizViewModel = viewModel()
    val noteViewModel: NoteViewModel = viewModel()

    CompositionLocalProvider(LocalNavigation provides navController) {
        NavHost(navController = navController, startDestination = MainScreenRoute) {
            composable(route = MainScreenRoute) {
                MainScreenView(userViewModel)
            }
            composable(route = Profile) {
                ProfileView(profileViewModel, userViewModel)
            }
            composable(route = Register) {
                RegisterView(registerViewModel)
            }
            composable(route = Login) {
                LoginView(loginViewModel, userViewModel)
            }
            composable(route = JoinGame) {
                JoinGameView(joinGameViewModel)
            }
            composable(route = Settings) {
                SettingsView(settingsViewModel)
            }
            composable(route = NewGame) {
                NewGameView()
            }
            composable(route = Creator) {
                CreateNewGameView(
                    viewModel = createNewGameViewModel,
                    onEditTask = { task ->
                        createNewGameViewModel.setTaskToEdit(task)
                        navController.navigate(AddTask)
                    },
                    navController = navController
                )
            }
            composable(route = AddTask) {
                val taskToEdit by createNewGameViewModel.taskToEdit.collectAsState()
                val mapMarkers by createNewGameViewModel.tasks.collectAsState()
                AddTaskView(
                    viewModel = createNewGameViewModel,
                    navController = navController,
                    taskToEdit = taskToEdit,
                    mapMarkers = mapMarkers,
                    quizViewModel = quizViewModel,
                    noteViewModel = noteViewModel
                )
            }
            composable(route = CreateQuiz) {
                val taskToEdit by createNewGameViewModel.taskToEdit.collectAsState()
                CreateQuizView(
                    quizViewModel = quizViewModel,
                    navController = navController,
                    onSaveQuiz = { quiz ->
                        taskToEdit?.let {
                            val quizTask = it.copy(taskDetails = quiz)
                            createNewGameViewModel.addOrUpdateTask(quizTask)
                        }
                        navController.navigate(AddTask)
                    }
                )
            }
            composable(route = CreateNote) {
                val taskToEdit by createNewGameViewModel.taskToEdit.collectAsState()
                CreateNoteView(
                    navController = navController,
                    noteViewModel = noteViewModel,
                    onSaveNote = { noteText ->
                        taskToEdit?.let {
                            val noteTaskDetails = Note(noteText)
                            val noteTask = it.copy(taskDetails = noteTaskDetails)
                            createNewGameViewModel.addOrUpdateTask(noteTask)
                        }
                        navController.navigate(AddTask)
                    }
                )
            }
        }
    }
}
