package com.timeromannews.helper

import com.timeromannews.util.testing.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler

class TestSchedulerProvider : SchedulerProvider {
    val testScheduler: TestScheduler = TestScheduler()


    override fun ui(): Scheduler = testScheduler
    override fun io(): Scheduler = testScheduler
}