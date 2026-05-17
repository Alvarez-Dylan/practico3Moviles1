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
import com.example.taskmanager.ui.screens.TaskDetailScreen
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
                    val destino = if (taskId == null) "task_form/0" else "task_form/$taskId"
                    navController.navigate(destino)
                },
                onNavigateToLabels = {
                    navController.navigate("labels")
                },
                onNavigateToDetail = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }

        // Pantalla de formulario (crear/editar tarea)
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

        // Pantalla de detalle de tarea (NUEVA)
        composable(
            route = "task_detail/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailScreen(
                application = application,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = {
                    navController.navigate("task_form/$taskId")
                }
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