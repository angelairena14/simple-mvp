package com.timeromannews.di

import android.content.Context
import com.timeromannews.ActivityModule
import com.timeromannews.base.BaseActivity
import com.timeromannews.ui.LoginActivity
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {
    @ActivityContext
    fun context(): Context
    fun inject(baseActivity: BaseActivity)
    fun inject(activity: LoginActivity)
}