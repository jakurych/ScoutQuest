package com.example.scoutquest.ui.navigation

import RegisterView
import RegisterViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scoutquest.ui.views.*
import com.example.scoutquest.ui.views.tasktypes.*
import com.example.scoutquest.viewmodels.*
import com.example.scoutquest.viewmodels.tasktypes.*

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
    val trueFalseViewModel: TrueFalseViewModel = viewModel()
    val browseGamesViewModel: BrowseGamesViewModel = viewModel()

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
                    createNewGameViewModel = createNewGameViewModel,
                    onEditTask = { task ->
                        createNewGameViewModel.setTaskToEdit(task)
                        navController.navigate(AddTask)
                    },
                    onNavigateToMainScreen = {
                        navController.navigate(MainScreenRoute)
                    }
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
                    noteViewModel = noteViewModel,
                    trueFalseViewModel = trueFalseViewModel
                )
            }
            composable(route = CreateQuiz) {
                CreateQuizView(
                    quizViewModel = quizViewModel,
                    navController = navController
                )
            }
            composable(route = CreateNote) {
                CreateNoteView(
                    noteViewModel = noteViewModel,
                    navController = navController
                )
            }
            composable(route = CreateTrueFalse) {
                CreateTrueFalseView(
                    trueFalseViewModel = trueFalseViewModel,
                    navController = navController
                )
            }
            composable(route = Browser) {
                BrowseGamesView(
                    browseGamesViewModel = browseGamesViewModel
                )
            }
            composable(route = UserBrowser) {
                val userId = userViewModel.getCurrentUserId()
                if (userId != null) {
                    UserGamesBrowserView(
                        userId = userId,
                        browseGamesViewModel = browseGamesViewModel,
                        onEditGame = { game ->
                            createNewGameViewModel.loadGame(game)
                            navController.navigate(Creator)
                        }
                    )
                }
            }
        }
    }
}
