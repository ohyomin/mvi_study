package com.example.mvvmtest.model

import com.example.mvvmtest.view.Intent
import io.reactivex.Observable

interface Model<S> {
    fun process(intent: Intent<S>)
    fun modelState(): Observable<S>
}