package com.improbable.structure.ui.views.workout_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.Movement
import com.improbable.structure.data.room.MovementUserData
import com.improbable.structure.viewmodel.SharedViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Database request state for a list of Movements.
 * */
sealed interface MovementsState {
    data class Success(val movements: List<Movement>) : MovementsState
    object Loading : MovementsState
}

/**
 * Database request state for a mutableStateList of lists containing MovementUserData.
 * Each nested list contains a MovementUserData object for each set of its corresponding Movement.
 * ('Set' as in the number of times an exercise is performed, not the data structure).
 * */
sealed interface UserDataHistoryState {
    data class Success(val movementsUserData: SnapshotStateList<List<MovementUserData>>) : UserDataHistoryState
    object Loading : UserDataHistoryState
}

/**
 * State class for the WorkoutScreen.
 * */
data class WorkoutScreenState(
    val movementsState : MovementsState = MovementsState.Loading,
    var userDataMap: MutableMap<Movement, SnapshotStateList<MovementUserData>> = mutableMapOf(),
    var userDataHistory: UserDataHistoryState = UserDataHistoryState.Loading
)

const val ViewModelWorkoutScreenTAG = "ViewModelWorkoutScreen"

/**
 * ViewModel for the WorkoutScreen view.
 * */
class ViewModelWorkoutScreen(
    private val repository: StructureRepository,
    private val sharedViewModel: SharedViewModel
    ) : ViewModel() {


    private var _workoutScreenState = MutableStateFlow(WorkoutScreenState())
    val workoutScreenState: StateFlow<WorkoutScreenState> = _workoutScreenState.asStateFlow()


    var currentWorkout = sharedViewModel.appUIState.value.selectedWorkout
        private set

    val movementState: StateFlow<WorkoutScreenState> =
        repository
            .loadAllMovements(currentWorkout.workoutId)
            .flowOn(Dispatchers.Default)
            .map {
                makeUserDataMap(movements = it)
                _workoutScreenState.value = _workoutScreenState.value.copy(movementsState = MovementsState.Success(it))
                WorkoutScreenState(movementsState = MovementsState.Success(it))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), WorkoutScreenState())


    /**
     * Updates the weight for a given Movements Set.
     * @return Unit
     * */
    fun updateWeight(movement: Movement, index: Int, weight: String) {
        _workoutScreenState.value.userDataMap[movement]?.get(index)?.let {
            it.copy(weight = weight).let { it1 ->
                _workoutScreenState.value.userDataMap[movement]?.set(
                    index,
                    it1
                )
            }
        }
    }

    /**
     * Updates the reps for a given Movements 'Set'.
     * @return Unit
     * */
    fun updateReps(movement: Movement, index: Int, reps: String) {
        _workoutScreenState.value.userDataMap[movement]?.get(index)?.let {
            it.copy(reps = reps).let { it1 ->
                _workoutScreenState.value.userDataMap[movement]?.set(
                    index,
                    it1
                )
            }
        }
    }

    /**
     * Gets the MovementUserData for a given movement, with index corresponding to the 'Set' number.
     * The data is stored in a SnapshotStateMap and can be used where mutable state is required.
     * @return MovementUserData
     * */
    fun getMutableUserDataState(movement: Movement, index: Int): MovementUserData {
        return _workoutScreenState.value.userDataMap[movement]?.get(index) ?: error(
            "Attempted to access User Data that does not exist, either no Data is in" +
                    " userDataMap[movement][index], you've used an incorrect index, or the" +
                    " movement does not exist in the userDataMap "
        )
    }

    /**
     * Creates the UserDataMap in place, in the _workoutScreenState.value.userDataMap.
     * @return Unit
     * */
    private fun makeUserDataMap(movements: List<Movement>) {
        for (movement in movements) {
            if (_workoutScreenState.value.userDataMap[movement]?.size != movement.sets) {
                _workoutScreenState.value.userDataMap[movement] = updateUserDataList(movement)
            } else {
                _workoutScreenState.value.userDataMap[movement] = createUserDataList(movement)
            }
        }
    }


    /**
     * Writes the current Workout's MovementUserData to the Database.
     * @return Unit
     * */
    fun saveWorkout(): Unit {
        CoroutineScope(Dispatchers.IO).launch {
            val v = _workoutScreenState.value.userDataMap.values.flatten()
            repository.insertMovementUserDataWithTimeStamp(v)
        }
    }

    /**
     * Retrieves the MovementUserData history for a given Movement from the Database.
     * This data is stored in the UserDataHistoryState object in the WorkoutScreenState instance.
     * @return Unit
     * */
    fun getMovementHistory(movement: Movement, numberOfSessions: Int = 1) {
        _workoutScreenState.value.userDataHistory = UserDataHistoryState.Loading
        runBlocking {
            val result = CoroutineScope(Dispatchers.IO).async {
                repository.getMovementUserDataForGiven(
                    movement.id ?: error("No Movement Id available"),
                    numberOfSessions
                )
                    .map {
                        val timestamp = it.created
                        val date = timestamp?.let { stamp -> Date(stamp) }
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        it.copy(dateString = date?.let { it1 -> dateFormat.format(it1)
                        }
                        )
                    }
                }
            _workoutScreenState.value = _workoutScreenState.value.copy(
                userDataHistory = UserDataHistoryState.Success(
                    result.await()
                        .groupListsInList { data -> getComparableValueOfMovementUserData(data) }
                        .toMutableStateList()))
            }
    }

    /**
     * Helper function for MovementUserData. Retrieves the 'created' timestamp from the
     * incoming MovementUserData.
     * @return Long (Time Stamp in milliseconds)
     * */
    private fun getComparableValueOfMovementUserData(data: MovementUserData): Long {
        return data.created ?: error("No value available for 'created' ")
    }

    /*Helper Methods*/

    /**
     * Helper function for 'makeUserDataMap()', responsible for updating the list of
     * MovementUserData stored in each key / value pair of the userDataMap where a
     * list of MovementUserData already exists, but has been modified in the Database.
     * @return SnapshotStateList<MovementUserData>
     * */
    private fun updateUserDataList(
        movement: Movement,
    ): SnapshotStateList<MovementUserData> {
        val newUserData = mutableStateListOf<MovementUserData>()
        for (i in 0 until movement.sets) {
            newUserData.add(_workoutScreenState.value.userDataMap[movement]?.get(i) ?: makeUserDataObject(movement, i))
        }

        return newUserData
    }

    /**
     * Helper function for 'makeUserDataMap()', creates a list of MovementUserData for the
     * userDataMap where that Movement is not yet stored in the Database.
     * @return Unit
     * */
    private fun createUserDataList(movement: Movement): SnapshotStateList<MovementUserData> {
        val newList = mutableStateListOf<MovementUserData>()

        if (movement !in _workoutScreenState.value.userDataMap) {
            for (i in 0..movement.sets)
                newList.add(makeUserDataObject(movement, i))
        }
        return newList

    }

    /**
     * Helper function for 'makeUserDataMap()', creates a new MovementUserData instance.
     * @return MovementUserData
     * */
    private fun makeUserDataObject(movement: Movement, index: Int): MovementUserData {
        return MovementUserData(
            movementId = movement.id
                ?: error("Cannot create User Data Object, Movement has no ID "),
            reps = "",
            weight = "",
            setIndex = index
        )
    }

}


/**
 * Groups an ordered list into a list of lists based on the 'getComparableValue' function passed in.
 * The 'getComparableValue' function must return a value comparable via '=='
 * @return  List<List<E>>
 * */
fun <E, comparableType> List<E>.groupListsInList( getComparableValue: (E) -> comparableType): List<List<E>>{

    val result = mutableListOf<List<E>>()
    var prev: comparableType? = null
    val currentGroup = mutableListOf<E>()

    for (item in this) {
        if (getComparableValue(item) == prev || prev == null) {
            currentGroup.add(item)
        } else {
            result.add(currentGroup.toList())
            currentGroup.clear()
            currentGroup.add(item)
        }
        prev = getComparableValue(item)
    }
    result.add(currentGroup.toList())
    return result
}






