package com.improbable.structure.ui.views.my_routines_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.improbable.structure.R
import com.improbable.structure.data.room.Workout
import com.improbable.structure.ui.reusables.TopSection
import com.improbable.structure.ui.views.home_screen.CurrentRoutineWorkouts
import com.improbable.structure.ui.views.navigation.NavigationDestination
import com.improbable.structure.ui.views.workout_screen.LoadingScreen
import com.improbable.structure.viewmodel.ViewModelProvider

/**
 * Destination route for the SingleRoutineScreen.
 * */
object SingleRoutineScreenDestination: NavigationDestination {
    override val route: String
        get() = "single_routine_screen"
}

/**
 * Entire SingleRoutineScreen view.
 * */
@Composable
fun SingleRoutineScreen(
    viewModel: ViewModelSingleRoutineScreen = viewModel(factory = ViewModelProvider.Factory),
    onClickWorkoutButton: () -> Unit,
    onBackButtonClick: () -> Unit) {
    
    val singleRoutine = viewModel.singleRoutinesState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopSection(
            mainTitle = stringResource(id = R.string.my_routines),
            navigateUp = onBackButtonClick
        )
        when (val routine = singleRoutine.value) {
            is SingleRoutineState.Loading -> LoadingScreen(title = stringResource(id = R.string.my_routines)) {}
            is SingleRoutineState.Success ->
                CurrentRoutineWorkouts(
                    currentRoutine = routine.routine,
                    onClickWorkout = { workout -> viewModel.selectWorkout(workout as Workout); onClickWorkoutButton() })
        }
    }
}

