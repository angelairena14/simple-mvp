package com.timeromannews.di

import android.content.Context
import com.timeromannews.ApplicationModule
import com.timeromannews.network.NetworkService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    @ApplicationContext
    fun context() : Context
    fun api(): NetworkService
}