package com.timeromannews.ui

import android.os.Bundle
import android.widget.Toast
import com.timeromannews.R
import com.timeromannews.base.BaseActivity
import com.timeromannews.model.LoginResponse
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginContract.View{

    @Inject
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getActivityComponent()?.inject(this)
        presenter.attachView(this)
        btn_login.setOnClickListener {
            presenter.login(et_email.text.toString(),et_password.text.toString())
        }
    }

    override fun onSuccessLogin(response: LoginResponse) {
        Toast.makeText(this,"login success",Toast.LENGTH_SHORT).show()
        SecurePreferences.setValue("token",response.token)
        SecurePreferences.setValue("username",et_email.text.toString())
        SecurePreferences.setValue("password",et_password.text.toString())
        SecurePreferences.setValue("expired_at",response.expires_at)
    }

    override fun setError(message: String) {
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }
}
