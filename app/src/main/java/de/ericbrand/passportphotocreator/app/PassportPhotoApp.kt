package de.ericbrand.passportphotocreator.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun PassportPhotoApp(){
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}