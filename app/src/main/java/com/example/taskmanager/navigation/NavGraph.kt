package com.example.taskmanager.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskmanager.TaskManagerApplication
import com.example.taskmanager.ui.screens.HomeScreen
import com.example.taskmanager.ui.screens.LabelsScreen
import com.example.taskmanager.ui.screens.TaskFormScreen

@Composable
fun NavGraph(
    application: TaskManagerApplication,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Pantalla principal
        composable("home") {
            HomeScreen(
                application = application,
                onNavigateToForm = { taskId ->

                    val destino =
                        if (taskId == null)
                            "task_form/0"
                        else
                            "task_form/$taskId"

                    navController.navigate(destino)
                },
                onNavigateToLabels = {
                    navController.navigate("labels")
                }
            )
        }

        // Pantalla de formulario
        composable(
            route = "task_form/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
                ?.let { if (it == 0) null else it }
            TaskFormScreen(
                application = application,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Pantalla de etiquetas
        composable("labels") {
            LabelsScreen(
                application = application,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}