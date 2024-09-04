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
import com.example.scoutquest.viewmodels.*
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
                    }
                )
            }

            composable(route = AddTask) {
                AddTaskView(
                    onBack = { navController.popBackStack() },
                    onSave = { task ->
                        if (task.taskType == "Quiz") {
                            navController.navigate(CreateQuiz)
                        } else {
                            createNewGameViewModel.addOrUpdateTask(task)
                            navController.popBackStack()
                        }
                    },
                    taskToEdit = createNewGameViewModel.taskToEdit.collectAsState().value,
                    initialLatitude = 52.253126,
                    initialLongitude = 20.900157,
                    mapMarkers = createNewGameViewModel.tasks.collectAsState().value
                )
            }

            composable(route = CreateQuiz) {
                CreateQuizView(
                    quizViewModel = quizViewModel,
                    onBack = { navController.popBackStack() },
                    onSaveQuiz = { quiz ->
                        val taskToEdit = createNewGameViewModel.taskToEdit.collectAsState().value
                        if (taskToEdit != null) {
                            val quizTask = taskToEdit.copy(taskDetails = quiz)
                            createNewGameViewModel.addOrUpdateTask(quizTask)
                        }
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}
