// core/navigation/NavGraph.kt
package com.example.note_app_kotllin.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.note_app_kotllin.ui.aichat.AiChatScreen
import com.example.note_app_kotllin.ui.screens.auth.login.LoginScreen
import com.example.note_app_kotllin.ui.screens.auth.register.RegisterScreen
import com.example.note_app_kotllin.ui.screens.home.HomeScreen
import com.example.note_app_kotllin.ui.screens.notedetail.NoteDetailScreen
import com.example.note_app_kotllin.ui.screens.notes.NotesScreen
import com.example.note_app_kotllin.ui.screens.settings.SettingsScreen
import com.example.note_app_kotllin.ui.screens.splash.SplashScreen
import com.example.note_app_kotllin.ui.screens.todo.TodoScreen


@Composable
fun NoteAppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Splash) {
        composable <Splash>{ SplashScreen(navController) }
        composable<Home> { HomeScreen(navController) }
        composable<Register> { RegisterScreen(navController) }
        composable<Login> { LoginScreen(navController) }
        composable<Notes> { NotesScreen(navController) }
        composable<Todos> { TodoScreen(navController) }
        composable<Settings> { SettingsScreen(navController) }
        composable<AiChat> { AiChatScreen(navController) }

        composable<NoteDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<NoteDetail>()
            NoteDetailScreen(args.id, args.title, args.subtitle, args.isSynced,navController)
        }
    }
}