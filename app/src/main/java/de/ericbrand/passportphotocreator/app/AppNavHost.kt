package de.ericbrand.passportphotocreator.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.ericbrand.passportphotocreator.feature.editor.EditorRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "editor"
    ) {
        composable("editor"){
            EditorRoute(
                onNavigateToExport = {navController.navigate("export")}
            )
        }
    }
}