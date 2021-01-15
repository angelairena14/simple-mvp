package com.timeromannews.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.timeromannews.R
import com.timeromannews.base.BaseActivity
import com.timeromannews.model.LoginResponse
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.util.Constant.HTTP_RESPONSE_CODE.RESPONSE_422
import com.timeromannews.util.testing.AppSchedulerProvider
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginContract.View{
    @Inject
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getActivityComponent()?.inject(this)
        loginPresenter.attachView(this)
        loginPresenter.setScheduleProvider(AppSchedulerProvider())
        loginPresenter.listenError()
        viewListener()
        if (isLoggedIn()) openNewsPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::loginPresenter.isInitialized) loginPresenter.dispose()
    }

    override fun onSuccessLogin(response: LoginResponse) {
        showToastMessage("login success")
        SecurePreferences.setValue("token",response.token)
        SecurePreferences.setValue("username",et_email.text.toString())
        SecurePreferences.setValue("password",et_password.text.toString())
        SecurePreferences.setValue("expired_at",response.expires_at)
        SecurePreferences.setValue("scheme",response.scheme)
        openNewsPage()
    }

    override fun setError(message: String) {
    }

    override fun showLoading() {
        progressbar_login.visibility = View.VISIBLE
        btn_login.text = ""
        btn_login.isClickable = false
    }

    override fun hideLoading() {
        progressbar_login.visibility = View.GONE
        btn_login.text = getString(R.string.login)
        btn_login.isClickable = true
    }

    private fun openNewsPage(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun clearError(){
        input_layout_password.isErrorEnabled = false
        input_layout_password.error = ""
        input_layout_email.isErrorEnabled = false
        input_layout_email.error = ""
    }


    override fun onServiceError(field : RetrofitResponse){
        when(field.code){
            RESPONSE_422 -> {
                when (field.name){
                    "username" -> { input_layout_email.error = field.error }
                    "password" -> { input_layout_password.error = field.error }
                }
            }
            else -> { showToastMessage(field.message)}
        }
    }

    private fun viewListener(){
        btn_login.setOnClickListener {
            clearError()
            loginPresenter.login(et_email.text.toString(),et_password.text.toString())
        }
    }
}
