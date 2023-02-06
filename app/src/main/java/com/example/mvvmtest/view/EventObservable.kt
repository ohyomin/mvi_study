package com.example.mvvmtest.view

import io.reactivex.Observable

interface EventObservable<E> {
    fun events(): Observable<E>
}