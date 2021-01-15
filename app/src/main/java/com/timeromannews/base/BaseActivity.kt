package com.timeromannews.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.timeromannews.ActivityModule
import com.timeromannews.BaseApplication
import com.timeromannews.di.ActivityComponent
import com.timeromannews.di.DaggerActivityComponent
import com.timeromannews.ui.LoginActivity
import de.adorsys.android.securestoragelibrary.SecurePreferences

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

    fun showToastMessage(message : String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    fun redirectToLogin(context: Context){
        startActivity(Intent(context,LoginActivity::class.java))
        finish()
    }

    fun isLoggedIn() : Boolean {
        return SecurePreferences.getStringValue("token","")?.isNotEmpty() == true
    }
}