package com.improbable.structure.ui.views.my_routines_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.Routine
import com.improbable.structure.data.room.Workout
import com.improbable.structure.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * Database request state for a single select routine.
 * */
sealed interface SingleRoutineState {
    data class Success(val routine: Routine): SingleRoutineState
    object Loading: SingleRoutineState
}

/**
 * ViewModel for singleRoutineScreen view.
 * */
class ViewModelSingleRoutineScreen(
    repository: StructureRepository,
    private val sharedViewModel: SharedViewModel
): ViewModel() {

    private val selectedRoutine = sharedViewModel.appUIState.value.selectedRoutine

    val singleRoutinesState: StateFlow<SingleRoutineState> = repository
        .loadAllWorkouts(routineName = selectedRoutine.routineName)
        .flowOn(Dispatchers.Default)
        .filterNotNull()
        .map {
            SingleRoutineState.Success(
                Routine(
                    selectedRoutine.routineName,
                    isCurrent = selectedRoutine.isCurrent,
                    workouts = it
                )
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SingleRoutineState.Loading)


    /**
     * Assigns the given workout as the currentWorkout in the StructureUIState instance inside the
     * SharedViewModel.
     * */
    fun selectWorkout(workout: Workout) {
        sharedViewModel.selectWorkout(workout)
    }
}