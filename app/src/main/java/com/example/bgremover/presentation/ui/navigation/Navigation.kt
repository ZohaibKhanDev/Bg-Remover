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
        composable(
            route = Screens.BgDetail.route + "/{imageUrl}/{bgremoveimage}",
            arguments =
            listOf(
                navArgument("imageUrl") {
                    type = NavType.StringType
                },
                navArgument("bgremoveimage") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val imageurl = backStackEntry.arguments?.getString("imageUrl")
            val bgremoveimage = backStackEntry.arguments?.getString("bgremoveimage")
            BgDetail(navController = navController, imageurl, bgremoveimage)
        }
    }
}


sealed class Screens(
    val route: String
) {

    object BgRemover : Screens("BgRemover")
    object BgDetail : Screens("BgDetail")

}