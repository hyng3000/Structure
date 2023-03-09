package com.improbable.structure.data.room

import android.content.Context
import androidx.room.*

/**
 * Room Database definition, pulls default data from default database on start.
 * */
@Database(entities = [MovementSkeleton::class, Routine::class, Workout::class, Movement::class, MovementUserData::class], version = 1, exportSchema = false)
abstract class StructureDatabase: RoomDatabase() {

    abstract fun structureDao(): StructureDao

    companion object {

        @Volatile
        private var Instance: StructureDatabase? = null

        fun getDatabase(context: Context): StructureDatabase {

            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, StructureDatabase::class.java, "StructureDefault")
                    .fallbackToDestructiveMigration()
                    .createFromAsset("database/StructureDefault.db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}