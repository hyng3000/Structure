package com.improbable.structure.ui.views.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.improbable.structure.ui.views.buildScreens.BuildNewRoutineDestination
import com.improbable.structure.ui.views.buildScreens.BuildNewRoutineScreen
import com.improbable.structure.ui.views.buildScreens.BuildNewWorkoutScreen
import com.improbable.structure.ui.views.buildScreens.BuildNewWorkoutScreenDestination
import com.improbable.structure.ui.views.home_screen.HomeScreen
import com.improbable.structure.ui.views.home_screen.HomeScreenDestination
import com.improbable.structure.ui.views.my_routines_screen.MyRoutinesScreen
import com.improbable.structure.ui.views.my_routines_screen.MyRoutinesScreenDestination
import com.improbable.structure.ui.views.my_routines_screen.SingleRoutineScreen
import com.improbable.structure.ui.views.my_routines_screen.SingleRoutineScreenDestination
import com.improbable.structure.ui.views.workout_screen.WorkoutScreen
import com.improbable.structure.ui.views.workout_screen.WorkoutScreenDestination


/**
 * NavHost for Structure.
 * */
@Composable
    fun StructureNavHost(
        navController: NavHostController,
        modifier: Modifier = Modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeScreenDestination.route,
            modifier = modifier
        ) {
            composable(route = HomeScreenDestination.route) {
                HomeScreen(
                    onWorkoutClicked = {
                        navController.navigate(WorkoutScreenDestination.route)},
                    onManageRoutinesButtonCLicked = { navController.navigate(
                        MyRoutinesScreenDestination.route) },
                    onRoutineClicked = {},
                    onTimerButtonClicked = {}
                )
            }

            composable(route = WorkoutScreenDestination.route) {
                WorkoutScreen(
                    onBackButtonClicked = { navController.navigateUp() },
                    onSaveButtonClicked = {navController.navigate(HomeScreenDestination.route)})
            }

            composable(route = MyRoutinesScreenDestination.route) {
                MyRoutinesScreen(
                    onClickRoutineButton = { navController.navigate(SingleRoutineScreenDestination.route) },
                    onBackButtonClick = { navController.navigateUp() },
                    onClickNewRoutineButton = { navController.navigate(BuildNewRoutineDestination.route) })
            }

            composable(
                route = SingleRoutineScreenDestination.route
            ) {
                SingleRoutineScreen(
                    onClickWorkoutButton = {navController.navigate(WorkoutScreenDestination.route)},
                    onBackButtonClick = { navController.navigateUp() })
            }

            composable(
                route = BuildNewRoutineDestination.route
            ) {
                BuildNewRoutineScreen(
                    onBackButtonClick = { navController.navigateUp() },
                    onAddNewWorkoutButtonClick = { navController.navigate(BuildNewWorkoutScreenDestination.route) },
                    onWorkoutButtonClick = {}
                )
            }

            composable(route = BuildNewWorkoutScreenDestination.route) {
                BuildNewWorkoutScreen(
                    navigateUp = { navController.navigateUp() },
                    onSaveNavigate = { navController.navigateUp() })
            }

        }
    }

