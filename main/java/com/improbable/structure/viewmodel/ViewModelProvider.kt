package com.improbable.structure.viewmodel

import com.improbable.structure.ui.views.workout_screen.ViewModelWorkoutScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.improbable.structure.StructureApplication
import com.improbable.structure.ui.views.buildScreens.ViewModelBuildRoutine
import com.improbable.structure.ui.views.buildScreens.ViewModelBuildWorkout
import com.improbable.structure.ui.views.home_screen.ViewModelHomeScreen
import com.improbable.structure.ui.views.my_routines_screen.ViewModelMyRoutinesScreen
import com.improbable.structure.ui.views.my_routines_screen.ViewModelSingleRoutineScreen


/**
 * Initialise ViewModels and inject dependencies
 * */
object ViewModelProvider {

    private val sharedViewModel: SharedViewModel = SharedViewModel()

    val Factory = viewModelFactory {


        initializer {
            ViewModelHomeScreen(
                structureApplication().container.structureRepository,
                sharedViewModel
            )
        }

        initializer {
            ViewModelWorkoutScreen(
                structureApplication().container.structureRepository,
                sharedViewModel,

            )
        }

        initializer {
            ViewModelMyRoutinesScreen(
                structureApplication().container.structureRepository,
                sharedViewModel
            )
        }

        initializer {
            ViewModelSingleRoutineScreen(
                structureApplication().container.structureRepository,
                sharedViewModel
            )
        }

        initializer {
            ViewModelBuildRoutine(
                structureApplication().container.structureRepository,
                sharedViewModel
            )
        }

        initializer {
            ViewModelBuildWorkout(
                structureApplication().container.structureRepository,
                sharedViewModel
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [StructureApplication].
 */
fun CreationExtras.structureApplication(): StructureApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as StructureApplication)