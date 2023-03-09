package com.improbable.structure.ui.views.home_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.improbable.structure.R
import com.improbable.structure.data.room.Routine
import com.improbable.structure.data.room.Workout
import com.improbable.structure.ui.reusables.LargeSquareContentButton
import com.improbable.structure.ui.reusables.LazyListButton
import com.improbable.structure.ui.reusables.RoundedRectangleFromBottomScreenSurface
import com.improbable.structure.ui.views.navigation.NavigationDestination
import com.improbable.structure.viewmodel.ViewModelProvider

/**
 * Destination route for WorkoutScreen.
 * */
object HomeScreenDestination : NavigationDestination {
    override val route = "home_screen"
}

/**
 * Entire HomeScreen view where state is determined by the HomeScreenState.
 * */
@Composable
fun HomeScreen(
    onWorkoutClicked: () -> Unit,
    onManageRoutinesButtonCLicked: () -> Unit,
    onRoutineClicked: (Routine?) -> Unit,
    onTimerButtonClicked: () -> Unit,
    viewModel: ViewModelHomeScreen = viewModel(factory = ViewModelProvider.Factory)
){
when (val homeScreenUIState = viewModel.homeScreenState.collectAsState().value) {
    is HomeScreenDataState.Loading -> HomeLoadingScreen()
    is HomeScreenDataState.Success -> HomeScreen(
        homeScreenUIState.state,
        onWorkoutClicked,
        onManageRoutinesButtonCLicked,
        onRoutineClicked,
        )
    }
}

/**
 * HomeScreen called when HomeScreenState returns success.
 * */
@Composable
fun HomeScreen(
    homeScreenUIState: HomeScreenState,
    onWorkoutClicked: () -> Unit,
    onManageRoutinesButtonCLicked: () -> Unit,
    onRoutineClicked: (Routine?) -> Unit,
    viewModel: ViewModelHomeScreen = viewModel(factory = ViewModelProvider.Factory)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
    ) {
        RoutinesMenu(
            firstRoutine = null,
            secondRoutine = null,
            onRoutineClick = onRoutineClicked,
            onManageRoutinesClick = onManageRoutinesButtonCLicked,
            modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp,
            )
        )
        CurrentRoutineWorkouts(
            currentRoutine = homeScreenUIState.currentRoutine,
            onClickWorkout = { workout -> viewModel.selectWorkout(workout as Workout); onWorkoutClicked()},
        )
    }
}

/**
 * Routine Management block.
 * */
@Composable
fun RoutinesMenu(
    onRoutineClick: (Routine?) -> Unit,
    firstRoutine: Routine?,
    secondRoutine: Routine?,
    modifier: Modifier = Modifier,
    onManageRoutinesClick: () -> Unit
) {
    Row(modifier = modifier) {
        LargeSquareContentButton(
            {onRoutineClick(firstRoutine)},
            firstRoutine?.routineName ?: null,
            colorResource(R.color.struct_green),
            modifier.weight(1f),
        )
        LargeSquareContentButton(
            {onRoutineClick(secondRoutine)},
            secondRoutine?.routineName ?: null,
            colorResource(R.color.struct_green),
            modifier.weight(1f),
        )
        LargeSquareContentButton(
            onManageRoutinesClick,
            "Manage Routines",
            colorResource(R.color.struct_white),
            modifier.weight(1f),
        )
    }
}

/**
 * List of workouts in the currentRoutine.
 * */
@Composable
fun CurrentRoutineWorkouts(currentRoutine: Routine, onClickWorkout: (Any) -> Unit){
    RoundedRectangleFromBottomScreenSurface(Modifier.padding(0.dp)) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Column() {
                Text(
                    text = "Current Routine: ",
                    modifier = Modifier.padding(21.dp, 21.dp, 21.dp, 5.dp)
                )
                Text(
                    text = currentRoutine.routineName,
                    modifier = Modifier.padding(21.dp, 5.dp, 21.dp, 21.dp),
                    fontSize = 30.sp
                )
                LazyColumn {
                    items(currentRoutine.workouts) { workout ->
                        LazyListButton(
                            buttonLabel = workout.name,
                            onCLick = {onClickWorkout(workout)}
                        )
                    }
                }
            }
        }
    }
}

/**
 * Place holder loading screen for use when waiting for data.
 * */
@Composable
fun HomeLoadingScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
    ) {

        RoutinesMenu(
            firstRoutine = null,
            secondRoutine = null,
            onRoutineClick = {},
            onManageRoutinesClick = {},
            modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp
            )
        )

        CurrentRoutineWorkouts(
            currentRoutine = Routine("", true),
            onClickWorkout = {},
        )
    }
}

object HomeScreenConstants {
    val roundedCornerInDp: Dp = 20.dp
    val elevation: Dp = 4.dp
}
