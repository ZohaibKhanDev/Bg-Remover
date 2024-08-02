package com.example.bgremover.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bgremover.presentation.ui.screens.BgDetail
import com.example.bgremover.presentation.ui.screens.BgRemover

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.BgRemover.route) {
        composable(Screens.BgRemover.route) {
            BgRemover(navController)
        }
     /*   composable(
            route = Screens.BgDetail.route + "/{imageUrl}",
            arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val imageurl = backStackEntry?.arguments?.getString("imageUrl")
            BgDetail(
                navController = navController,
                imageurl
            )
        }*/

    }
}

sealed class Screens(
    val route: String
) {

    object BgRemover : Screens("BgRemover")
    object BgDetail : Screens("BgDetail")

}