package ru.myproject.cooktimer.ui.main

sealed interface TimerStateI {
    data class Stopped(val time: String) : TimerStateI
    data class Running(val time: String) : TimerStateI
    object Done: TimerStateI
}