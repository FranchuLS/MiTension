package com.fxn.mitension.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fxn.mitension.ui.screens.CalendarioScreen
import com.fxn.mitension.ui.screens.DiaDetalleScreen
import com.fxn.mitension.ui.screens.MedicionScreen

// Definimos las rutas para evitar errores de escritura
object AppDestinations {
    const val MEDICION = "medicion"
    const val CALENDARIO = "calendario"
    const val DIA_DETALLE_ROUTE = "dia_detalle"
    const val ANIO_ARG = "anio"
    const val MES_ARG = "mes"
    const val DIA_ARG = "dia"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppDestinations.MEDICION
    ) {
        composable(AppDestinations.MEDICION) {
            MedicionScreen(
                onNavigateToCalendario = {
                    navController.navigate(AppDestinations.CALENDARIO)
                }
            )
        }
        composable(AppDestinations.CALENDARIO) {
            CalendarioScreen(
                onNavigateToMedicion = { navController.popBackStack() },
                onNavigateToDiaDetalle = { anio, mes, dia ->
                    navController.navigate("${AppDestinations.DIA_DETALLE_ROUTE}/$anio/$mes/$dia")
                }
            )
        }

        composable(
            route = "${AppDestinations.DIA_DETALLE_ROUTE}/{${AppDestinations.ANIO_ARG}}/{${AppDestinations.MES_ARG}}/{${AppDestinations.DIA_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.ANIO_ARG) { type = NavType.IntType },
                navArgument(AppDestinations.MES_ARG) { type = NavType.IntType },
                navArgument(AppDestinations.DIA_ARG) { type = NavType.IntType }
            )
        ) {
            DiaDetalleScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
    