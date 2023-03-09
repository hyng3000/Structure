package com.improbable.structure.data

import android.content.Context
import com.improbable.structure.data.repository.OfflineStructureRepository
import com.improbable.structure.data.repository.StructureRepository
import com.improbable.structure.data.room.Routine
import com.improbable.structure.data.room.StructureDatabase
import com.improbable.structure.data.room.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface AppContainer {
    val structureRepository: StructureRepository

}

class AppDataContainer(private val context: Context) : AppContainer {

    override val structureRepository: StructureRepository by lazy {
        OfflineStructureRepository(StructureDatabase.getDatabase(context).structureDao())
    }

}