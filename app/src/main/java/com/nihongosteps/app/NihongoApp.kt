package com.nihongosteps.app

import android.app.Application
import com.nihongosteps.app.data.ProgressRepository
import com.nihongosteps.app.util.Speaker

class NihongoApp : Application() {
    lateinit var repository: ProgressRepository
        private set
    lateinit var speaker: Speaker
        private set

    override fun onCreate() {
        super.onCreate()
        repository = ProgressRepository(this)
        speaker = Speaker(this)
    }
}
