package com.improbable.structure.ui.views.buildScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.improbable.structure.R
import com.improbable.structure.ui.reusables.LazyListButton
import com.improbable.structure.ui.reusables.RoundedRectangleFromBottomScreenSurface
import com.improbable.structure.ui.reusables.SaveButton
import com.improbable.structure.ui.reusables.TopSection
import com.improbable.structure.ui.views.navigation.NavigationDestination
import com.improbable.structure.viewmodel.ViewModelProvider

/**
 * Destination route for the BuildNewRoutineScreen
 * */
object BuildNewRoutineDestination: NavigationDestination {
    override val route: String
        get() = "build_new_routine_screen"
}

/**
 * Entire BuilderNewRoutineScreen view.
 * */
@Composable
fun BuildNewRoutineScreen(
    viewModel: ViewModelBuildRoutine = viewModel(factory = ViewModelProvider.Factory),
    onBackButtonClick: () -> Unit,
    onWorkoutButtonClick: () -> Unit,
    onAddNewWorkoutButtonClick: () -> Unit
) {
    val state = viewModel.getNewRoutineState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopSection(
            mainTitle = stringResource(id = R.string.build_routine_title),
            navigateUp = onBackButtonClick,
            rightSideButton = {}
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .height(90.dp)
            .padding(vertical = 20.dp)
        ) {
            Text(text = "name: ", modifier = Modifier.padding(horizontal = 5.dp))
            val focusManager = LocalFocusManager.current
            TextField(
                value = state.value.name,
                onValueChange = { name -> viewModel.setRoutineName(name)},
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.clearFocus()}
                )
            )
        }
        NewWorkoutsList(
            workouts = state.value.workouts,
            onWorkoutButtonClick = onWorkoutButtonClick,
            onAddNewWorkoutButtonClick = onAddNewWorkoutButtonClick,
            onSaveButtonClick = {viewModel.saveRoutine(); onBackButtonClick()},
            modifier = Modifier
        )
    }
}

/**
 * List of new Workouts added to the WorkoutBuilder state instance in the SharedViewModel.
 * */
@Composable
fun NewWorkoutsList(
    workouts: SnapshotStateList<WorkoutBuilder>,
    onWorkoutButtonClick: () -> Unit,
    onAddNewWorkoutButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    modifier: Modifier
) {
    RoundedRectangleFromBottomScreenSurface(topPadding = 0.dp, modifier = modifier) {
        Box(contentAlignment = Alignment.BottomEnd) {
            LazyColumn(reverseLayout = false, modifier = Modifier.fillMaxSize()) {
                items(workouts) { movement ->
                    LazyListButton(buttonLabel = movement.name, onCLick = onWorkoutButtonClick)
                }
                item {
                    LazyListButton(
                        buttonLabel = "Add Workout +",
                        onCLick = onAddNewWorkoutButtonClick,
                        withBorder = true, fontSize = 24,
                        color = colorResource(id = R.color.struct_black)
                    )
                }
            }
            SaveButton(onSaveButtonClick, modifier = Modifier.size(100.dp))
        }
    }
}

object BuildRoutinesConstants {
    val standardPadding: Dp = 5.dp
}