package ru.myproject.cooktimer.ui.main

import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.myproject.cooktimer.R

class MainViewModel : ViewModel() {

    private val _timerState = MutableStateFlow<TimerStateI>(TimerStateI.Stopped(""))
    val timerState: StateFlow<TimerStateI> = _timerState
    private var timer: CountDownTimer = createTimer(TimerState.Soft)
    private var soundPool: SoundPool? = null
    private var alarm = 0

    fun initSound(fd: AssetFileDescriptor) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
            .apply { alarm = load(fd, 1) }
    }

    private fun createTimer(state: TimerState): CountDownTimer {
        _timerState.value = TimerStateI.Stopped(getTime(state.time))
        return object : CountDownTimer(state.time, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerState.value = TimerStateI.Running(getTime(millisUntilFinished))
            }

            override fun onFinish() {
                _timerState.value = TimerStateI.Done
                if (alarm > 0) {
                    soundPool?.play(alarm, 1f, 1f, 1, 0, 1f)
                }
            }
        }
    }

    private fun getTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / 1_000
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }


    fun onStartBtnClick() {
        if (_timerState.value is TimerStateI.Running) {
            timer.cancel()
            _timerState.value = TimerStateI.Done
        } else {
            timer.start()
        }
        soundPool?.stop(alarm)
    }


    fun onItemSelected(itemId: Int) {
        when (itemId) {
            R.id.action_soft -> installTimer(TimerState.Soft)
            R.id.action_medium -> installTimer(TimerState.Medium)
            R.id.action_rare -> installTimer(TimerState.Rare)
        }
    }

    private fun installTimer(state: TimerState) {
        timer.cancel()
        timer = createTimer(state)


    }

}
