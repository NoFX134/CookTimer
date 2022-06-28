package ru.myproject.cooktimer.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.collect
import ru.myproject.cooktimer.R
import ru.myproject.cooktimer.databinding.FragmentMainBinding
import java.lang.Exception

class MainFragment : Fragment() {

    companion object {

        fun newInstance() = MainFragment()
    }

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
           val fd = requireContext().assets.openFd("alarm.wav")
            viewModel.initSound(fd)
        }
        catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnStartButton.setOnClickListener { viewModel.onStartBtnClick() }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            viewModel.onItemSelected(item.itemId)
            true
        }
        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.timerState
                    .collect { state ->
                        with(binding) {
                            when (state) {

                                is TimerStateI.Stopped -> {
                                    message.text = state.time
                                    btnStartButton.setText(R.string.start)
                                    bottomNavigation.setItemEnabled(true)
                                }
                                is TimerStateI.Running -> {
                                    message.text = state.time
                                    btnStartButton.setText(R.string.stop)
                                    bottomNavigation.setItemEnabled(false)
                                }

                                TimerStateI.Done -> {
                                    message.setText(R.string.done)
                                    btnStartButton.setText(R.string.start)
                                    bottomNavigation.setItemEnabled(true)
                                }
                            }
                        }
                    }
            }

    }
    private fun BottomNavigationView.setItemEnabled(enable: Boolean){
        for(i in 0 until menu.size()){
            menu[i].isEnabled=enable
        }
    }
}