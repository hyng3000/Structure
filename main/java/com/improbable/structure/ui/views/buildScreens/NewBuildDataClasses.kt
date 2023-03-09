package com.improbable.structure.ui.views.buildScreens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.improbable.structure.data.room.Movement
import com.improbable.structure.data.room.MovementSkeleton
import com.improbable.structure.data.room.Routine
import com.improbable.structure.data.room.Workout

/**
 * Temporary class for building a new Routine.
 * */
data class RoutineBuilder(
    var name: String = "",
    var workouts: SnapshotStateList<WorkoutBuilder> = mutableStateListOf()
) {
    fun toRoutine(isCurrent: Boolean): Routine{
        return Routine(name, isCurrent)
    }
}

/**
 * Temporary class for building a new Workout.
 * */
data class WorkoutBuilder(
    var name: String = "",
    var skeletonsAndSets: SnapshotStateMap<MovementSkeleton, Int> = mutableStateMapOf()
) {
    fun toWorkout(workoutId: Int, parentRoutineName: String, ): Workout {
        return Workout(workoutId = workoutId, parentRoutineName = parentRoutineName, name = name)
    }
    fun skeletonAndSetToMovement(skeleton: MovementSkeleton, parentWorkoutId: Int, movementId: Int): Movement {
        return Movement(name = skeleton.name, parentWorkoutId = parentWorkoutId, sets = skeletonsAndSets[skeleton] ?: error("No such skeleton in map"), muscleGroup = skeleton.muscleGroup, id = movementId)
    }
}

/**
 * Temporary class for building a new Movement.
 * */
data class NewMovementSkeleton(
    var name: String = "",
    var muscleGroup: String = ""
)