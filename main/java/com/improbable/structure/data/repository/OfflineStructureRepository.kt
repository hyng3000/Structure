package com.improbable.structure.data.repository

import com.improbable.structure.data.room.*
import com.improbable.structure.ui.views.buildScreens.RoutineBuilder
import kotlinx.coroutines.flow.Flow

class OfflineStructureRepository(private val structureDao: StructureDao): StructureRepository {

    override suspend fun insertRoutine(routine: Routine) = structureDao.insertRoutine(routine)

    override suspend fun insertWorkout(workout: Workout) = structureDao.insertWorkout(workout)

    override suspend fun insertMovement(movement: Movement) = structureDao.insertMovement(movement)

    override suspend fun insertMovementUserDataWithTimeStamp(data: List<MovementUserData>) =
        structureDao.insertMovementUserDataWithTimeStamp(data)

    override suspend fun insertRoutineWithWorkoutsAndMovements(routineBuilder: RoutineBuilder) =
        structureDao.insertRoutineWithWorkoutsAndMovements(routineBuilder)

    override fun loadAllWorkouts(routineName: String): Flow<List<Workout>> =
        structureDao.loadAllWorkoutsAsFlow(routineName)

    override fun loadAllMovements(workoutId: Int): Flow<List<Movement>> =
        structureDao.loadAllMovements(workoutId)

    override fun loadAllRoutines(): Flow<List<Routine>> = structureDao.loadAllRoutines()

    override fun getLastWorkOutID(): Flow<Int?> = structureDao.getLastWorkOutID()

    override fun loadCurrentRoutine(): Flow<Map<Routine, List<Workout>>> =
        structureDao.loadCurrentRoutine()

    override fun loadAllMovementsWithUserData(workoutID: Int): Flow<List<Movement>> =
        structureDao.loadAllMovementsWithUserData(workoutID)

    override suspend fun getMovementUserDataForGiven(movementId: Int, numberOfWorkouts: Int) =
        structureDao.getMovementUserDataForGiven(movementId, numberOfWorkouts)

    override fun loadAllMovementSkeletons(): Flow<List<MovementSkeleton>> =
        structureDao.loadAllMovementSkeletons()

    override suspend fun deleteRoutineWithWorkoutsAndMovements(routine: Routine) =
        structureDao.deleteRoutineWithWorkoutsAndMovements(routine = routine)
}