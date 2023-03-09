package com.improbable.structure.ui.views.my_routines_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.Routine
import com.improbable.structure.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * Database request state for a list of all Routines stored in the Database.
 * */
sealed interface MyRoutinesState {
    data class Success(val routines: List<Routine>): MyRoutinesState
    object Loading: MyRoutinesState
}

/**
 * ViewModel for the MyRoutineScreen view.
 * */
class ViewModelMyRoutinesScreen(
    private val repository: StructureRepository,
    private val sharedViewModel: SharedViewModel
    ): ViewModel() {

    val myRoutinesState: StateFlow<MyRoutinesState> = repository
        .loadAllRoutines()
        .flowOn(Dispatchers.Default)
        .filterNotNull()
        .map {
            MyRoutinesState.Success(it)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), MyRoutinesState.Loading)

    /**
     * Assigns the given workout as the currentRoutine in the StructureUIState instance inside the
     * SharedViewModel.
     * */
    fun selectRoutine(routine: Routine) {
        sharedViewModel.selectRoutine(routine)
    }

    /**
     * Deletes the given Routine, and corresponding Workout and Movement data from the Database.
     * */
    fun deleteRoutine(routine: Routine){
        runBlocking(context = Dispatchers.IO) {
            repository.deleteRoutineWithWorkoutsAndMovements(routine = routine)
        }
    }
}
