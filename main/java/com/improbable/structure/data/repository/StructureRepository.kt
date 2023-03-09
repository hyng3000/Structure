package com.improbable.structure.data.repository

import com.improbable.structure.data.room.*
import com.improbable.structure.ui.views.buildScreens.RoutineBuilder
import kotlinx.coroutines.flow.Flow

interface StructureRepository {

    suspend fun insertRoutine(routine: Routine)

    suspend fun insertWorkout(workout: Workout)

    suspend fun insertMovement(movement: Movement)

    suspend fun insertMovementUserDataWithTimeStamp(data: List<MovementUserData>)

    suspend fun insertRoutineWithWorkoutsAndMovements(routineBuilder: RoutineBuilder)


    fun getLastWorkOutID(): Flow<Int?>

    fun loadAllRoutines(): Flow<List<Routine>>

    fun loadAllWorkouts(routineName: String): Flow<List<Workout>>

    fun loadAllMovements(workoutId: Int): Flow<List<Movement>>

    fun loadCurrentRoutine(): Flow<Map<Routine, List<Workout>>>

    fun loadAllMovementsWithUserData(workoutID: Int): Flow<List<Movement>>

    suspend fun getMovementUserDataForGiven(movementId: Int, numberOfWorkouts: Int): MutableList<MovementUserData>

    fun loadAllMovementSkeletons(): Flow<List<MovementSkeleton>>

    suspend fun deleteRoutineWithWorkoutsAndMovements(routine: Routine)

}