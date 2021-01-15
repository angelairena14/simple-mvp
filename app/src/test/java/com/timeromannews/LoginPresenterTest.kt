package com.timeromannews

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.timeromannews.helper.TestSchedulerProvider
import com.timeromannews.model.LoginResponse
import com.timeromannews.model.ProfileResponse
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.model.newsresponse.NewsResponse
import com.timeromannews.network.NetworkService
import com.timeromannews.ui.HomeContract
import com.timeromannews.ui.HomePresenter
import com.timeromannews.ui.LoginContract
import com.timeromannews.ui.LoginPresenter
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LoginPresenterTest {
    @Mock
    lateinit var service: NetworkService

    @Mock
    lateinit var view: LoginContract.View
    private lateinit var presenter: LoginPresenter
    private lateinit var scheduler: TestSchedulerProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        this.service = mock()
        this.view = mock()
        this.scheduler = TestSchedulerProvider()
        this.presenter = LoginPresenter(this.service)
        this.presenter.scheduler = this.scheduler
    }

    @Test
    fun `success login`(){
        val mock = mockk<LoginResponse>()
        var map = HashMap<String, Any?>()
        var username = "tester"
        var password = "tester123"
        map["username"] = username
        map["password"] = password
        Mockito.`when`(this.service.postLogin(map))
            .thenReturn(Observable.create {
                it.onNext(mock)
            })
        this.presenter.attachView(this.view)
        this.presenter.login(username,password)
        this.scheduler.testScheduler.triggerActions()

        verify(this.view).onSuccessLogin(mock)
    }
}