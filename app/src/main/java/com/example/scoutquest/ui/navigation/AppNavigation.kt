@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.scoutquest.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.scoutquest.ui.views.general.RegisterView
import com.example.scoutquest.viewmodels.general.RegisterViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scoutquest.ui.views.taskscreators.*
import com.example.scoutquest.ui.views.gamesession.GameMapView
import com.example.scoutquest.ui.views.general.BrowseGamesView
import com.example.scoutquest.ui.views.general.CreateNewGameView
import com.example.scoutquest.ui.views.general.OpenWorldMenu
import com.example.scoutquest.ui.views.general.LoginView
import com.example.scoutquest.ui.views.general.MainScreenView
import com.example.scoutquest.ui.views.general.AdventureGameMenuView
import com.example.scoutquest.ui.views.general.ProfileView
import com.example.scoutquest.ui.views.general.SettingsView
import com.example.scoutquest.ui.views.general.UserGamesBrowserView
import com.example.scoutquest.viewmodels.tasktypes.*
import com.example.scoutquest.viewmodels.gamesession.GameSessionViewModel
import com.example.scoutquest.viewmodels.general.BrowseGamesViewModel
import com.example.scoutquest.viewmodels.general.CreateNewGameViewModel
import com.example.scoutquest.viewmodels.general.JoinGameViewModel
import com.example.scoutquest.viewmodels.general.LoginViewModel
import com.example.scoutquest.viewmodels.general.ProfileViewModel
import com.example.scoutquest.viewmodels.general.SettingsViewModel
import com.example.scoutquest.viewmodels.general.UserViewModel

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
    val gameSessionViewModel: GameSessionViewModel = viewModel()
    val openQuestionViewModel: OpenQuestionViewModel = viewModel()
    val photoViewModel: PhotoViewModel = viewModel()


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
                OpenWorldMenu()
            }
            composable(route = Settings) {
                SettingsView(settingsViewModel)
            }
            composable(route = NewGame) {
                AdventureGameMenuView()
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
                    trueFalseViewModel = trueFalseViewModel,
                    openQuestionViewModel = openQuestionViewModel,
                    photoViewModel = photoViewModel
                )
            }
            composable(route = CreateQuiz) {
                CreateQuizView(
                    quizViewModel = quizViewModel,
                    navController = navController
                )
            }
            composable(route = CreateOpenQuestion){
                CreateOpenQuestionView(openQuestionViewModel,
                    navController)
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
            composable(route = CreatePhotoTask) {
                CreatePhotoTaskView(
                    photoViewModel = photoViewModel,
                    navController = navController
                )
            }
            composable(route = Browser) {
                BrowseGamesView(
                    browseGamesViewModel = browseGamesViewModel,
                    onPlayGame = { game ->
                        gameSessionViewModel.setGame(game)
                        navController.navigate(GameMap)
                    }
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
                } else {
                    navController.navigate(Login)
                }
            }
            composable(route = GameMap) {
                GameMapView(
                    viewModel = gameSessionViewModel,
                    onGameEnd = {
                        navController.navigate(MainScreenRoute) {
                            popUpTo(MainScreenRoute) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
