package com.improbable.structure

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.improbable.structure.ui.views.navigation.StructureNavHost

@Composable
fun StructureApp(navController: NavHostController = rememberNavController()) {
    StructureNavHost(navController = navController)
}

