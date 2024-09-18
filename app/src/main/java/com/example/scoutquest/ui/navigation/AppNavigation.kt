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
import com.example.scoutquest.ui.views.AddTaskView
import com.example.scoutquest.ui.views.BrowseGamesView
import com.example.scoutquest.ui.views.CreateNewGameView
import com.example.scoutquest.ui.views.JoinGameView
import com.example.scoutquest.ui.views.LoginView
import com.example.scoutquest.ui.views.MainScreenView
import com.example.scoutquest.ui.views.NewGameView
import com.example.scoutquest.ui.views.ProfileView
import com.example.scoutquest.ui.views.SettingsView
import com.example.scoutquest.ui.views.UserGamesBrowserView
import com.example.scoutquest.ui.views.tasktypes.CreateNoteView
import com.example.scoutquest.ui.views.tasktypes.CreateQuizView
import com.example.scoutquest.ui.views.tasktypes.CreateTrueFalseView
import com.example.scoutquest.viewmodels.BrowseGamesViewModel
import com.example.scoutquest.viewmodels.CreateNewGameViewModel
import com.example.scoutquest.viewmodels.JoinGameViewModel
import com.example.scoutquest.viewmodels.LoginViewModel
import com.example.scoutquest.viewmodels.ProfileViewModel
import com.example.scoutquest.viewmodels.SettingsViewModel
import com.example.scoutquest.viewmodels.UserViewModel
import com.example.scoutquest.viewmodels.tasktypes.NoteViewModel
import com.example.scoutquest.viewmodels.tasktypes.QuizViewModel
import com.example.scoutquest.viewmodels.tasktypes.TrueFalseViewModel

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

                    //typy taskow
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
                    UserGamesBrowserView(userId = userId, browseGamesViewModel = browseGamesViewModel)
                }
            }

        }
    }
}
