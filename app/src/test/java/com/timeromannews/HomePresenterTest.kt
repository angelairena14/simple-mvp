package com.timeromannews

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.timeromannews.helper.TestSchedulerProvider
import com.timeromannews.model.ProfileResponse
import com.timeromannews.model.RetrofitResponse
import com.timeromannews.model.newsresponse.NewsResponse
import com.timeromannews.network.NetworkService
import com.timeromannews.ui.HomeContract
import com.timeromannews.ui.HomePresenter
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class HomePresenterTest {
    @Mock
    lateinit var service: NetworkService

    @Mock
    lateinit var view: HomeContract.View
    private lateinit var presenter: HomePresenter
    private lateinit var scheduler: TestSchedulerProvider

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        this.service = mock()
        this.view = mock()
        this.scheduler = TestSchedulerProvider()
        this.presenter = HomePresenter(this.service)
        this.presenter.scheduler = this.scheduler
    }

    @Test
    fun `success fetch profile`(){
        val mock = mockk<ProfileResponse>()
        Mockito.`when`(this.service.fetchProfile())
            .thenReturn(Observable.create {
                it.onNext(mock)
            })
        this.presenter.attachView(this.view)
        this.presenter.getProfile()
        this.scheduler.testScheduler.triggerActions()

        verify(this.view).onSuccessGetProfile(mock)
    }

    @Test
    fun `success fetch news`(){
        val mock = mockk<NewsResponse>()
        Mockito.`when`(this.service.fetchNews())
            .thenReturn(Observable.create {
                it.onNext(mock)
            })
        this.presenter.attachView(this.view)
        this.presenter.getNews()
        this.scheduler.testScheduler.triggerActions()
        verify(this.view).onSuccessGetNews(mock)
    }
}