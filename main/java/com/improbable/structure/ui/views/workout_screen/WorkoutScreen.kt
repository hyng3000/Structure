package com.improbable.structure.ui.views.workout_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.improbable.structure.R
import com.improbable.structure.data.room.Movement
import com.improbable.structure.data.room.MovementUserData
import com.improbable.structure.ui.reusables.SaveButton
import com.improbable.structure.ui.reusables.TopSection
import com.improbable.structure.ui.views.navigation.NavigationDestination
import com.improbable.structure.viewmodel.ViewModelProvider

/**
 * Destination route for WorkoutScreen.
 * */
object WorkoutScreenDestination: NavigationDestination {
    override val route: String
        get() = "workout_screen"

}

/**
 * Load screen placeholder for use when no data is yet available.
 * */
@Composable
fun LoadingScreen(title: String, onBackButtonClicked: () -> Unit) {
    TopSection(mainTitle = title, navigateUp = onBackButtonClicked) {}
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Loading...", modifier = Modifier.padding(vertical = 250.dp))
    }

}

/**
 * The entire WorkoutScreen view.
 * */
@Composable
fun WorkoutScreen(
    viewModel: ViewModelWorkoutScreen = viewModel(factory = ViewModelProvider.Factory),
    onBackButtonClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit
) {
    Column(verticalArrangement = Arrangement.SpaceBetween) {

        TopSection(
            mainTitle = viewModel.currentWorkout.name,
            navigateUp = onBackButtonClicked,
            rightSideButton = { SaveButton({viewModel.saveWorkout(); onSaveButtonClicked()}, Modifier.size(50.dp))
            }
        )

        when( val movementsState = viewModel.movementState.collectAsState().value.movementsState) {
            is MovementsState.Loading -> LoadingScreen(viewModel.currentWorkout.name, onBackButtonClicked)
            is MovementsState.Success -> MovementJournalBlock(
                viewModel = viewModel,
                movements = movementsState.movements,
                state = viewModel.workoutScreenState.collectAsState().value)
        }
    }
}

/**
 * The list of Movements and their corresponding UserMovementData.
 * */
@Composable
fun MovementJournalBlock(
    viewModel: ViewModelWorkoutScreen,
    movements: List<Movement>,
    state: WorkoutScreenState
) {
    Surface(
        shape = RoundedCornerShape(WorkoutScreenConstants.roundedCornerInDp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(0.dp, WorkoutScreenConstants.topAndMiddlePadding, 0.dp, 0.dp),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(WorkoutScreenConstants.lazyRowSpacing),
            contentPadding = PaddingValues(WorkoutScreenConstants.standardPadding),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(movements) { movement ->
                JournalEntry(
                    movement = movement,
                    viewModel = viewModel,
                    state = state
                )
            }
        }
    }
}

/**
 * One row of the MovementJournalBlock composable.
 * */
@Composable
fun JournalEntry(
    movement: Movement,
    viewModel: ViewModelWorkoutScreen,
    state: WorkoutScreenState
){
    var height by remember {
        mutableStateOf(WorkoutScreenConstants.movementPortHeight)
    }
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Surface(
        shape = RoundedCornerShape(WorkoutScreenConstants.roundedCornerInDp),
        color = Color.Gray, /* COLOR */
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Column {
            Text(
                text = movement.name,
                modifier = Modifier
                    .padding(
                        WorkoutScreenConstants.standardPadding + 5.dp,
                        WorkoutScreenConstants.standardPadding,
                        WorkoutScreenConstants.standardPadding, 0.dp
                    )
                    .clickable {
                        if (!isExpanded) {
                            viewModel.getMovementHistory(movement)
                        }; isExpanded = isExpanded.not()
                    }
                )
            LazyRow(
                horizontalArrangement =  Arrangement.spacedBy(WorkoutScreenConstants.lazyRowSpacing),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier,
                contentPadding = PaddingValues(WorkoutScreenConstants.standardPadding)
            ){
                items(movement.sets) { it ->
                    WeightAndRepsTextInput(
                        movement,
                        viewModel,
                        it,
                    )
                }
            }
            if (isExpanded) {
                height = WorkoutScreenConstants.movementPortHeight * 2

                when (val userData = state.userDataHistory) {
                    UserDataHistoryState.Loading -> Text("Loading..")
                    is UserDataHistoryState.Success -> AllMovementHistory(
                        userData = userData.movementsUserData,
                        moreData = viewModel::getMovementHistory,
                        movement = movement
                    )
                }
            } else {
                height = WorkoutScreenConstants.movementPortHeight
            }
        }
    }
}

/**
 * Text input for one 'set' of a Movement.
 * */
@Composable
fun WeightAndRepsTextInput(
    movement: Movement,
    viewModel: ViewModelWorkoutScreen,
    setIndex: Int,
){
    Surface(
        shape = RoundedCornerShape(WorkoutScreenConstants.roundedCornerInDp),
        color = colorResource(id = R.color.struct_white), /* COLOR */
        modifier = Modifier
            .heightIn(
                min = WorkoutScreenConstants.textInputMinHeight,
                max = WorkoutScreenConstants.textInputMaxHeight
            )
            .widthIn(
                min = WorkoutScreenConstants.textInputMinWidth,
                max = WorkoutScreenConstants.textInputMaxWidth
            )
    ) {
        val focusManager = LocalFocusManager.current

        Column {
            TextField(
                label = { Text(stringResource(id = R.string.weight)) },
                value = viewModel.getMutableUserDataState(movement, setIndex).weight,
                onValueChange = { viewModel.updateWeight(movement, setIndex, it) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.struct_white), /* COLOR */
                    textColor = colorResource(id = R.color.struct_black)
                ),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus()
                    }
                )
            )
            TextField(
                label = { Text(stringResource(id = R.string.reps)) },
                value = viewModel.getMutableUserDataState(movement, index = setIndex).reps,
                onValueChange = { viewModel.updateReps(movement, setIndex, it) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.struct_white),
                    textColor = colorResource(id = R.color.struct_black)
                ),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus()
                    }
                )
            )
        }
    }
}

/**
 * All history currently display for a given Movement.
 * */
@Composable
fun AllMovementHistory(
    userData: List<List<MovementUserData>>,
    moreData: (Movement, Int) -> Unit,
    movement: Movement
) {
    var numberOfSessions by remember {
        mutableStateOf(1)
    }
    if (userData.isEmpty() || userData.first().isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(stringResource(id = R.string.empty_movement_history), textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                MovementHistoryRow(userDataForOneSession = userData.first(), true)
            }
            items(userData.slice(1 until userData.size)) {
                MovementHistoryRow(userDataForOneSession = it, false)
            }
            item {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth(),) {
                    Spacer(modifier = Modifier.height(50.dp))
                        Text("more", modifier = Modifier.clickable  {
                            numberOfSessions += 1; moreData(movement, numberOfSessions)
                        }
                    )
                }
            }
        }
    }
}

/**
 * One row of for a given movements history.
 * */
@Composable
fun MovementHistoryRow(userDataForOneSession: List<MovementUserData>, isFirst: Boolean){
    LazyRow(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            if (isFirst) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(100.dp)
                    )
                {
                    Text("Last")
                    Text(text = "Workout: ")}
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(102.dp)
                ) {
                    userDataForOneSession
                        .first().dateString?.let { Text(text = it, textAlign = TextAlign.Center) }
                }
            }
        }
        items(userDataForOneSession) {
            Surface(
                shape = RoundedCornerShape(WorkoutScreenConstants.roundedCornerInDp),
                color = colorResource(id = R.color.struct_green)
            ) {
                Column(modifier = Modifier.padding(WorkoutScreenConstants.standardPadding)) {
                    Text(text = "${it.weight.ifEmpty { "0" }} ${stringResource(id = R.string.kg)}")
                    Text(text = "${it.reps.ifEmpty { "0" }} ${stringResource(id = R.string.Reps)}")
                }
            }
        }
    }
}


object WorkoutScreenConstants {
    val topAndMiddlePadding: Dp = 20.dp
    val movementPortHeight: Dp = 140.dp
    val textInputMinHeight: Dp = 70.dp
    val textInputMaxHeight: Dp = 140.dp
    val textInputMinWidth: Dp = 80.dp
    val textInputMaxWidth: Dp = 120.dp
    val roundedCornerInDp: Dp = 20.dp
    val standardPadding: Dp = 5.dp
    val lazyRowSpacing: Dp = 15.dp
}

