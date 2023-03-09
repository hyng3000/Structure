package com.improbable.structure.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.improbable.structure.data.room.Routine
import com.improbable.structure.data.room.Workout
import com.improbable.structure.ui.views.buildScreens.RoutineBuilder
import kotlinx.coroutines.flow.*

data class StructureUIState(
    var selectedWorkout: Workout = Workout(),
    var selectedRoutine: Routine = Routine()
)

class SharedViewModel(): ViewModel() {

    /*state*/

    private val _appUIState = MutableStateFlow(StructureUIState())
    val appUIState: StateFlow<StructureUIState> = _appUIState.asStateFlow()

    var newRoutineState = mutableStateOf(RoutineBuilder())
        private set

    /*Workout Methods*/

    fun selectWorkout(workout: Workout) {
        _appUIState.value = _appUIState.value.copy(selectedWorkout = workout)
    }

    /*Routine Methods*/

    fun selectRoutine(routine: Routine) {
        _appUIState.value = _appUIState.value.copy(selectedRoutine = routine)
    }

    fun clearRoutine() {
        newRoutineState.value = RoutineBuilder()
    }

}