package com.improbable.structure.ui.views.home_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.Routine
import com.improbable.structure.data.room.Workout
import com.improbable.structure.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Database request state for a list of Movements.
 * */
sealed interface HomeScreenDataState {
    data class Success(val state: HomeScreenState) : HomeScreenDataState
    object Loading : HomeScreenDataState
}

/**
 * State class for the HomeScreenState.
 * */
data class HomeScreenState(val currentRoutine: Routine = Routine()) {
    constructor(currentRoutine: Routine, currentWorkouts: List<Workout>):
            this(currentRoutine.copy(workouts = currentWorkouts))
}

/**
 * ViewModel for the homeScreen.
 * */
class ViewModelHomeScreen(
    repository: StructureRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {

    val homeScreenState: StateFlow<HomeScreenDataState> = repository
        .loadCurrentRoutine()
        .flowOn(Dispatchers.Default)
        .filterNotNull()
        .map { map ->
            Log.d("VIEWMODELHomeScreen", "Got Routine: ${map.keys}")
            HomeScreenDataState.Success(
            HomeScreenState(map.keys.first(), map.values.first())
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeScreenDataState.Loading)

    /**
     * Assigns the given workout as the currentRoutine in the StructureUIState instance inside the
     * SharedViewModel.
     * */
    fun selectWorkout(workout: Workout) {
        sharedViewModel.selectWorkout(workout)
    }
}
