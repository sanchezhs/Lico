package com.app.lico

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.lico.ui.screens.ShoppingListsScreen
import com.app.lico.ui.screens.NewListScreen
import com.app.lico.ui.screens.ShoppingListDetailScreen


object Routes {
    const val LISTS = "lists"
    const val NEW_LIST = "new_list"
    const val LIST_DETAIL = "list_detail"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.LISTS) {
        composable(Routes.LISTS) {
            ShoppingListsScreen(
                onNavigateNewList = { navController.navigate(Routes.NEW_LIST) },
                onNavigateListDetail = { listId -> navController.navigate("${Routes.LIST_DETAIL}/$listId") }
            )
        }
        composable(Routes.NEW_LIST) {
            NewListScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable("${Routes.LIST_DETAIL}/{listId}") { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")?.toLongOrNull()
            if (listId != null) {
                ShoppingListDetailScreen(
                    listId = listId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
