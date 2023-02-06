package com.example.mvvmtest.view

interface Intent<T> {
    fun reduce(oldState: T): T
}

fun <T> intent(block: T.() -> T) : Intent<T> {
    return object : Intent<T> {
        override fun reduce(oldState: T): T {
            return block(oldState)
        }
    }
}

fun <T> sideEffect(block: T.() -> Unit) : Intent<T> {
    return object : Intent<T> {
        override fun reduce(oldState: T): T {
            return oldState.apply(block)
        }
    }
}

fun main() {
    val intent = object : Intent<String> {
        override fun reduce(oldState: String): String {
            return "aaa"
        }
    }

    val t : Intent<String> = intent(String::toString)
    t.reduce("eelel")
}