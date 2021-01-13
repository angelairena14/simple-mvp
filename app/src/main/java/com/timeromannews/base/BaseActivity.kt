package com.timeromannews.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.timeromannews.ActivityModule
import com.timeromannews.BaseApplication
import com.timeromannews.di.ActivityComponent
import com.timeromannews.di.DaggerActivityComponent

abstract class BaseActivity : AppCompatActivity(){
    private var activityComponent: ActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        getActivityComponent()?.inject(this)
    }

    fun getActivityComponent(): ActivityComponent? {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .applicationComponent(BaseApplication.get(this).getApplicationComponent())
                .build()
        }
        return activityComponent
    }
}