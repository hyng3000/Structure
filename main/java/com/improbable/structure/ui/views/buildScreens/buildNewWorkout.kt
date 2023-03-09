package com.improbable.structure.ui.views.buildScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.improbable.structure.R
import com.improbable.structure.data.room.MovementSkeleton
import com.improbable.structure.ui.reusables.*
import com.improbable.structure.ui.views.navigation.NavigationDestination
import com.improbable.structure.viewmodel.ViewModelProvider

/**
 * Destination route for BuildNewWorkoutScreen.
 * */
object BuildNewWorkoutScreenDestination: NavigationDestination {
    override val route: String
        get() = "build_new_workout_screen"

}

/**
 * Entire BuildNewWorkoutScreen view.
 * */
@Composable
fun BuildNewWorkoutScreen(
    viewModel: ViewModelBuildWorkout = viewModel(factory = ViewModelProvider.Factory,),
    navigateUp: () -> Unit,
    onSaveNavigate: () -> Unit,
) {
    val newWorkoutState = viewModel.newWorkoutsState.value
    val searchBarData = viewModel.searchBarData.collectAsState()

    Column(verticalArrangement = Arrangement.SpaceBetween) {
        TopSection(
            mainTitle = "New Workout",
            navigateUp = navigateUp,
            rightSideButton = { SaveButton({ viewModel.saveWorkout(); onSaveNavigate() }, modifier = Modifier.size(50.dp))
            }
        )
        WorkoutNameEntry(textFieldValue = newWorkoutState.name, onValueChange = { value -> viewModel.setWorkoutName(value) })

        SelectedMovementsColumn(
            selectedMovements = newWorkoutState.skeletonsAndSets,
            increaseSets = { viewModel.increaseSets(it) },
            decreaseSets = { viewModel.decreaseSets(it) },
            onClickRemove = { viewModel.onClickRemove(it) },
            modifier = Modifier.weight(0.5f)
        )

        Column(modifier = Modifier
            .weight(1.2f)
            .padding(0.dp, 5.dp, 0.dp, 0.dp)) {

            when (val skeletons = searchBarData.value) {
                SearchBarDataState.Loading -> Text(text = "Loading...")
                is SearchBarDataState.Success -> MovementList(
                    skeletons = skeletons.searchBar.results,
                    onMovementButtonClick = { viewModel.addMovementToNewWorkout(it) },
                    modifier = Modifier.weight(1f),
                    textFieldOnValueChange = { s -> skeletons.searchBar.search(s) },
                    textFieldValue = skeletons.searchBar.searchTerm.value,
                )
            }
        }
    }
}


/**
 * Text entry for WorkoutBuilder name parameter.
 * */
@Composable
fun WorkoutNameEntry(textFieldValue: String, onValueChange: (String) -> Unit ){
    val focusManager = LocalFocusManager.current
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .height(60.dp)
        .padding(vertical = 5.dp)) {
        Text(text = "Name:", modifier = Modifier.padding(horizontal = 15.dp))
        TextField(
            value = textFieldValue,
            onValueChange = {name -> onValueChange(name) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {focusManager.clearFocus(true)},
            )
        )
    }
}

/**
 * Formatted Version of 'SelectedMovements()'
 * */
@Composable
fun SelectedMovementsColumn(
    selectedMovements: SnapshotStateMap<MovementSkeleton, Int>,
    increaseSets: (MovementSkeleton) -> Unit,
    decreaseSets: (MovementSkeleton) -> Unit,
    onClickRemove: (MovementSkeleton) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .padding(vertical = 5.dp))
    {
        SelectedMovements(
            selectedMovements = selectedMovements,
            increaseSets = increaseSets,
            decreaseSets = decreaseSets,
            onClickRemove = onClickRemove )
    }
}

/**
 * List of all movement skeletons in the Database.
 * */
@Composable
fun MovementList(
    skeletons: List<MovementSkeleton>,
    onMovementButtonClick: (MovementSkeleton) -> Unit,
    textFieldValue: String,
    textFieldOnValueChange: (String) -> Unit,
    modifier: Modifier
) {
    Column() {

        val localFocusManager = LocalFocusManager.current

        Box(contentAlignment = Alignment.BottomCenter) {
                RoundedRectangleFromBottomScreenSurface(topPadding = 0.dp) {
                    LazyColumn(modifier = Modifier) {
                        items(skeletons) { skeleton ->
                            LazyListButton(
                                buttonLabel = skeleton.name,
                                onCLick = { onMovementButtonClick(skeleton); localFocusManager.clearFocus() })
                        }
                    }
                }
            SearchBar(
                focusManager = localFocusManager,
                textFieldValue = textFieldValue,
                textFieldOnValueChange = {value -> textFieldOnValueChange(value)},
                modifier = Modifier
            )
        }
    }
}

/**
 * List of selected Movement with buttons for increasing / decreasing sets.
 * */
@Composable
fun SelectedMovements(
    selectedMovements: SnapshotStateMap<MovementSkeleton, Int>,
    increaseSets: (MovementSkeleton) -> Unit,
    decreaseSets: (MovementSkeleton) -> Unit,
    onClickRemove: (MovementSkeleton) -> Unit
){
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp))  {
        items(selectedMovements.keys.toList()) {movement ->
            SelectedMovement(
                selectedMovement = movement,
                increaseSets = {increaseSets(movement)},
                decreaseSets = {decreaseSets(movement)},
                numberOfSets = selectedMovements[movement] ?: 0,
                onClickRemove = onClickRemove
            )
        }
    }
}

/**
 * Row of the 'SelectedMovements' composable
 * */
@Composable
fun SelectedMovement(
    selectedMovement: MovementSkeleton,
    increaseSets: () -> Unit, decreaseSets: () -> Unit,
    numberOfSets: Int, onClickRemove: (MovementSkeleton) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Column() {
            Text(text = selectedMovement.name, modifier = Modifier.fillMaxWidth(0.5f))
        }
        Column() {
            NumberSelector(increase = increaseSets, decrease = decreaseSets, number = numberOfSets)
        }
        RemoveSelectedMovement(onClick = { onClickRemove(selectedMovement) }, modifier = Modifier.padding(horizontal = 10.dp))
    }
}

/**
 * Remove Button.
 * */
@Composable
fun RemoveSelectedMovement(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        Icons.Filled.Cancel,
        "Increase Sets",
        modifier = modifier.clickable { onClick() })
}

/**
 * Number Selector for 'SelectedMovements'
 * */
@Composable
fun NumberSelector(increase: () -> Unit, decrease: () -> Unit, number: Int){
    Row(verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = stringResource(id = R.string.sets), modifier = Modifier.padding(horizontal = 5.dp))
        Button(shape = CircleShape, onClick = decrease, contentPadding = PaddingValues(0.dp), modifier = Modifier
            .width(40.dp)
            .aspectRatio(1f),) {
            Icon(Icons.Filled.Remove, "Decrease Sets")
        }
        Text(text = number.toString(), modifier = Modifier.padding(horizontal = 5.dp))
        Button(shape = CircleShape, onClick = increase, contentPadding = PaddingValues(0.dp), modifier = Modifier
            .width(40.dp)
            .aspectRatio(1f)) {
            Icon(Icons.Filled.Add, "Increase Sets")
        }
    }
}


