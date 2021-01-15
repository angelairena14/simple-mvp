package com.timeromannews.util.testing

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun ui(): Scheduler
    fun io(): Scheduler
}