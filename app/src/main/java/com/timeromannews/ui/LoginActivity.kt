package com.timeromannews.ui

import android.content.Intent
import android.os.Bundle
import com.timeromannews.R
import com.timeromannews.base.BaseActivity
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.model.LoginResponse
import com.timeromannews.util.Constant.HTTP_RESPONSE_CODE.RESPONSE_401
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
        loginPresenter.listenError()
        if (isLoggedIn()) openNewsPage()

        btn_login.setOnClickListener {
            clearError()
            loginPresenter.login(et_email.text.toString(),et_password.text.toString())
        }
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
        openNewsPage()
    }

    override fun setError(message: String) {
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    private fun openNewsPage(){
        startActivity(Intent(this, NewsActivity::class.java))
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
            RESPONSE_401 -> { showToastMessage(field.message)}
            else -> {
                when (field.name){
                    "username" -> { input_layout_email.error = field.error }
                    "password" -> { input_layout_password.error = field.error }
                }
            }
        }
    }
}
