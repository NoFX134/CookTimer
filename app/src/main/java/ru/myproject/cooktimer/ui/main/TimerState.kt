package ru.myproject.cooktimer.ui.main

enum class TimerState(val time: Long) {
    Soft(1*10*1_000), //TODO change time
    Medium(5*60*1_000),
    Rare(10*60*1_000)
}