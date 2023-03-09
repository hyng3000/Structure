package com.improbable.structure.data.room

import android.util.Log
import androidx.room.*
import com.improbable.structure.ui.views.buildScreens.RoutineBuilder
import kotlinx.coroutines.flow.*

@Dao
abstract class StructureDao {
    /*
    setters
    */

    @Query("SELECT MAX(workoutId) from Workout")
    abstract fun getLastWorkOutID(): Flow<Int?>

    @Query("SELECT MAX(id) from Movement")
    abstract fun getLastMovementId(): Flow<Int>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRoutine(routine: Routine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWorkout(workout: Workout)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertMovement(movement: Movement)

    @Transaction
    @Insert
    suspend fun insertRoutineWithWorkoutsAndMovements(routineBuilder: RoutineBuilder) {

        insertRoutine(routineBuilder.toRoutine(isCurrent = false))

        var workoutId = getLastWorkOutID().first() ?: 0
        var movementId = getLastMovementId().first()

        for (workoutBuilder in routineBuilder.workouts) {
            workoutId += 1
            insertWorkout(workoutBuilder.toWorkout(workoutId = workoutId, parentRoutineName = routineBuilder.name))

            for (skeleton in workoutBuilder.skeletonsAndSets.keys) {
                movementId += 1
                insertMovement(workoutBuilder.skeletonAndSetToMovement(skeleton, workoutId, movementId))
            }
        }
    }

    /*
    getters
    */

    /*Routine*/

    @Query("SELECT * FROM Routine")
    abstract fun loadAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM Routine JOIN Workout ON routineName = parentRoutineName")
    abstract fun loadRoutineAndWorkouts(): Flow<Map<Routine, List<Workout>>>

    @Query("SELECT * FROM Routine JOIN Workout ON routineName = parentRoutineName WHERE isCurrent = 1")
    abstract fun loadCurrentRoutine(): Flow<Map<Routine, List<Workout>>>

    @Transaction
    @Query("")
    suspend fun deleteRoutineWithWorkoutsAndMovements(routine: Routine) {
        loadAllWorkouts(routine.routineName)
            .forEach { deleteMovements(it.workoutId); deleteWorkout(routine.routineName) }
        deleteRoutine(routine.routineName)
        Log.i("DAO", "CALLED")
    }

    @Query("DELETE FROM Routine WHERE :name = routineName")
    abstract suspend fun deleteRoutine(name: String)

    /*Workout*/

    @Query("SELECT * FROM Workout WHERE parentRoutineName = :routineName")
    abstract fun loadAllWorkoutsAsFlow(routineName: String): Flow<List<Workout>>

    @Query("SELECT * FROM Workout WHERE parentRoutineName = :routineName")
    abstract fun loadAllWorkouts(routineName: String): List<Workout>

    @Query("DELETE FROM Workout WHERE :routineName = parentRoutineName")
    abstract suspend fun deleteWorkout(routineName: String)

    /*Movement*/

    @Query("SELECT * FROM Movement WHERE parentWorkoutId = :workoutID")
    abstract fun loadAllMovements(workoutID: Int): Flow<List<Movement>>


    @Query("DELETE FROM Movement WHERE :workoutID = parentWorkoutId")
    abstract suspend fun deleteMovements(workoutID: Int)

    @Query("SELECT * FROM Movement WHERE parentWorkoutId = :workoutID")
    abstract fun loadAllMovementsWithUserData(workoutID: Int): Flow<List<Movement>>

    /*MovementUserData*/

    @Insert
    abstract suspend fun insertMovementUserData(data: List<MovementUserData>)

    @Update()
    abstract suspend fun updateMovementUserData(data: List<MovementUserData>)

    suspend fun insertMovementUserDataWithTimeStamp(data: List<MovementUserData>){
        val time = System.currentTimeMillis()
        insertMovementUserData(
            data.map { it.apply {
                created = time
                updated = time
                    }
            }
        )
    }

    @Query("SELECT *\n" +
            "FROM MovementUserData\n" +
            "WHERE movementId = :movementId AND created IN (\n" +
            "    SELECT created\n" +
            "    FROM MovementUserData\n" +
            "    GROUP BY created\n" +
            "    LIMIT :numberOfWorkouts\n" +
            ")" +
            "ORDER BY created DESC")
    abstract suspend fun getMovementUserDataForGiven(movementId: Int, numberOfWorkouts: Int): MutableList<MovementUserData>

    /*MovementSkeletons*/
    @Query("SELECT * FROM MovementSkeleton ORDER BY muscleGroup")
    abstract fun loadAllMovementSkeletons(): Flow<List<MovementSkeleton>>

}
