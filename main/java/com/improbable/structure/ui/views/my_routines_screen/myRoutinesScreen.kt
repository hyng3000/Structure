package com.improbable.structure.ui.views.my_routines_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.improbable.structure.R
import com.improbable.structure.data.room.Routine
import com.improbable.structure.ui.reusables.LargeSquareContentButton
import com.improbable.structure.ui.reusables.RoundedRectangleFromBottomScreenSurface
import com.improbable.structure.ui.reusables.TopSection
import com.improbable.structure.ui.views.navigation.NavigationDestination
import com.improbable.structure.ui.views.workout_screen.LoadingScreen
import com.improbable.structure.viewmodel.ViewModelProvider

/**
 * Destination route for MyRoutineScreenDestination.
 * */
object MyRoutinesScreenDestination: NavigationDestination {
    override val route: String
        get() = "my_routines_screen"
}

/**
 * Entire MyRoutineScreen View.
 * */
@Composable
fun MyRoutinesScreen(
    viewModel: ViewModelMyRoutinesScreen = viewModel(factory = ViewModelProvider.Factory),
    onClickRoutineButton: () -> Unit,
    onBackButtonClick: () -> Unit,
    onClickNewRoutineButton: () -> Unit) {

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        TopSection(
            mainTitle = stringResource(id = R.string.my_routines),
            navigateUp = onBackButtonClick,
            rightSideButton = {}
        )
        when (val state = viewModel.myRoutinesState.collectAsState().value) {
            is MyRoutinesState.Loading -> LoadingScreen(stringResource(id = R.string.my_routines), onBackButtonClick)
            is MyRoutinesState.Success ->
                AllRoutinesBlock(
                    routines = state.routines,
                    onDeleteRoutine = { routine -> viewModel.deleteRoutine(routine) },
                    onClickRoutineButton = { routine -> viewModel.selectRoutine(routine as Routine); onClickRoutineButton() },
                    onClickNewRoutineButton = onClickNewRoutineButton
                )
            }
        }
    }

/**
 * Grid of all routines.
 * Contains a button for navigating to NewRoutineScreen.
 * Contains a button for editing Routines.
 * */
@Composable
fun AllRoutinesBlock(
    routines: List<Routine>,
    onClickRoutineButton: (Any) -> Unit,
    onClickNewRoutineButton: () -> Unit,
    onDeleteRoutine: (Routine) -> Unit
) {
    var isRoutineDeletable by remember {
        mutableStateOf(false)
    }

    RoundedRectangleFromBottomScreenSurface {
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(ViewRoutinesConstants.standardPadding),
                modifier = Modifier
                    .padding(ViewRoutinesConstants.standardPadding)
                    .fillMaxSize()
            ) {
                items(routines) { routine ->
                    LargeSquareContentButton(
                        onClick = { onClickRoutineButton(routine) },
                        label = routine.routineName,
                        color = Color.Gray, withBorder = false,
                        isDeletable = isRoutineDeletable,
                        onDelete = { onDeleteRoutine(routine) }
                    )
                }
                item {
                    LargeSquareContentButton(
                        onClick = onClickNewRoutineButton,
                        label = "+", color = colorResource(id = R.color.struct_black),
                        withBorder = true,
                        fontSize = 50
                    )
                }
            }
            IconButton(onClick = { isRoutineDeletable = isRoutineDeletable.not() }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .padding(50.dp)
                )
            }
        }
    }
}

object ViewRoutinesConstants {
    val standardPadding: Dp = 5.dp
}