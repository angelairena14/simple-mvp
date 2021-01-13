package com.timeromannews

import android.app.Activity
import android.content.Context
import com.timeromannews.di.ActivityContext
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    internal fun provideActivity(): Activity {
        return activity
    }

    @Provides
    @ActivityContext
    internal fun provideContext(): Context {
        return activity
    }
}