package com.improbable.structure.data.room

import androidx.room.*

@Entity
data class Routine(
    @PrimaryKey val routineName: String = "",
    val isCurrent: Boolean = false,
    @Ignore var workouts: List<Workout> = emptyList()
) {
    constructor(routineName: String, isCurrent: Boolean):
            this(routineName, isCurrent, listOf())
}

@Entity
data class Workout(
    @PrimaryKey var workoutId: Int = 0,
    var parentRoutineName: String = "",
    var name: String = "",
    @Ignore var movements: List<Movement> = emptyList()
) {
    constructor(workoutId: Int, parentRoutineName: String, name: String):
            this(workoutId, parentRoutineName, name, listOf())
}

@Entity
data class Movement(
    val name: String = "",
    val parentWorkoutId: Int = 0,
    var sets: Int = 0,
    val muscleGroup: String = "",
    @PrimaryKey()
    var id: Int?
) {
}

@Entity
data class MovementUserData(
    val movementId: Int,
    var reps: String,
    var weight: String,
    var setIndex: Int,
    var created: Long?,
    var updated: Long?,
    @Ignore
    val dateString: String?
) {
    constructor(movementId: Int, reps: String, weight: String, setIndex: Int) :
            this(movementId, reps, weight, setIndex, created = null, updated = null, dateString = null)

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0;
}

@Entity
data class MovementSkeleton(
    @PrimaryKey
    val name: String,
    val muscleGroup: String,
    val secondaryMuscleGroup: String?,
    val quaternaryMuscleGroup: String?
    )