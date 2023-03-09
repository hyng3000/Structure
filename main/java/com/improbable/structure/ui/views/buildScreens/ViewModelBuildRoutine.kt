package com.improbable.structure.ui.views.buildScreens

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.MovementSkeleton
import com.improbable.structure.viewmodel.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for the BuildRoutine view.
 * */
class ViewModelBuildRoutine(
    private val repository: StructureRepository,
    private val sharedViewModel: SharedViewModel) : ViewModel()
{
    var newMovementSkeletonState = mutableStateOf(NewMovementSkeleton())
        private set

    /**
     * Retrieves the RoutineBuilder state instance from the sharedViewModel.
     * */
    fun getNewRoutineState(): MutableState<RoutineBuilder>{
        return sharedViewModel.newRoutineState
    }

    /**
     * Assigns the given name to the name parameter of the RoutineBuilder instance in the
     * sharedViewModel.
     * */
    fun setRoutineName(name: String) {
        sharedViewModel
            .newRoutineState
            .value =
            sharedViewModel
                .newRoutineState
                .value
                .copy(name = name)
    }

    /**
     * Writes the RoutineBuilder instance to the Database.
     * */
    fun saveRoutine(){
        runBlocking(context = Dispatchers.IO) {
            repository.insertRoutineWithWorkoutsAndMovements(getNewRoutineState().value)
            val routine = getNewRoutineState().value
            clearRoutine()
        }
    }

    /**
     * Clears the RoutineBuilder instance state in the sharedRoutineState.
     * */
    private fun clearRoutine() {
        sharedViewModel.clearRoutine()
    }
}