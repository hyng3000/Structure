package com.improbable.structure.ui.views.buildScreens


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.MovementSkeleton
import com.improbable.structure.ui.reusables.SearchBarData
import com.improbable.structure.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * Database request state for a list of MovementSkeletons stored in a SearchBar instance.
 * */
sealed interface SearchBarDataState {
    data class Success( val searchBar: SearchBarData<MovementSkeleton>): SearchBarDataState
    object Loading: SearchBarDataState
}

/**
 * ViewModel for the BuildWorkout Screen.
 * */
class ViewModelBuildWorkout(
    repository: StructureRepository,
    private val sharedViewModel: SharedViewModel
    ): ViewModel() {

    var newWorkoutsState = mutableStateOf(WorkoutBuilder())
        private set

    val searchBarData: StateFlow<SearchBarDataState> = repository
        .loadAllMovementSkeletons()
        .flowOn(Dispatchers.Default)
        .map {
            SearchBarDataState.Success(
                SearchBarData(it.toMutableStateList()) { s -> searchBarIdentityFun(s)}
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            SearchBarDataState.Loading
        )

    /**
     * Assigns the the workout name to local WorkoutBuilder Instance (newWorkoutState).
     * @return Unit
     * */
    fun setWorkoutName(name: String) {
        newWorkoutsState.value = newWorkoutsState.value.copy(name = name)
    }

    /**
     * Adds a Movement Skeleton and it's number of 'sets' to the newWorkoutState's Map
     * @return Unit
     * */
    fun addMovementToNewWorkout(skeleton: MovementSkeleton) {
        val map = newWorkoutsState.value.skeletonsAndSets
        map[skeleton] = 0
        newWorkoutsState.value = newWorkoutsState.value.copy(skeletonsAndSets = map)
    }

    /**
     * Increases the number of sets for a given MovementSkeleton in the local WorkoutBuilder
     * instance.
     * @return Unit
     * */
    fun increaseSets(skeleton: MovementSkeleton) {
        val current = newWorkoutsState.value.skeletonsAndSets[skeleton] ?: error("MovementSkeleton not in map")
        newWorkoutsState.value.skeletonsAndSets[skeleton] = current + 1
    }

    /**
     * Decreases the number of sets for a given MovementSkeleton in the local WorkoutBuilder
     * instance.
     * @return Unit
     * */
    fun decreaseSets(skeleton: MovementSkeleton) {
        val current = newWorkoutsState.value.skeletonsAndSets[skeleton] ?: 0
        if (current > 0) {
            newWorkoutsState.value.skeletonsAndSets[skeleton] = current - 1
        }
    }

    /**
     * Removes a given Skeleton from the local WorkoutBuilder instance.
     * @return Unit
     * */
    fun onClickRemove(skeleton: MovementSkeleton) {
        newWorkoutsState.value.skeletonsAndSets.remove(skeleton)
    }

    /**
     * Adds the current workout to the sharedViewModel RoutineBuilder instance.
     * @return Unit
     * */
    fun saveWorkout() {
        sharedViewModel.newRoutineState.value.workouts.add(newWorkoutsState.value.copy())
        newWorkoutsState.value = WorkoutBuilder()
    }

    /**
     * Helper function for SearchBar instance to retrieve the string used in the search function.
     * @return Unit
     * */
    private fun searchBarIdentityFun(movement: MovementSkeleton): String {
        return movement.name.lowercase()
    }

}