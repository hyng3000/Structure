package com.improbable.structure

import android.app.Application
import com.improbable.structure.data.AppContainer
import com.improbable.structure.data.AppDataContainer

class StructureApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

}
