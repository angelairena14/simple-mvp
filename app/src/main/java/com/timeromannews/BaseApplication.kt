package com.timeromannews

import android.app.Application
import android.content.Context
import com.timeromannews.di.ApplicationComponent
import com.timeromannews.di.DaggerApplicationComponent

class BaseApplication : Application(){
    companion object {
        @Synchronized
        fun get(context: Context): BaseApplication {
            return context.applicationContext as BaseApplication
        }
    }

    private var applicationComponent: ApplicationComponent? = null
    var baseApplication: BaseApplication = this@BaseApplication

    override fun onCreate() {
        super.onCreate()
    }

    open fun getApplicationComponent(): ApplicationComponent {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(baseApplication))
                .build()
        }

        return applicationComponent!!
    }
}