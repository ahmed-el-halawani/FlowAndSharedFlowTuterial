package com.newcore.flowandsharedflowtuterial

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.newcore.flowandsharedflowtuterial.databinding.ActivityMainBinding
import com.newcore.flowandsharedflowtuterial.extensions.observeLatest
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {
    private val vm: MainViewModel by viewModels()

    // Coroutine listening for countDown
    private var countDownJob: Job? = null

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        /**
         * u have 2 ways to collect the data from flow
         * first one is using asLiveData and it convertFlow to live data and observe it
         */
        vm.counterFlow.asLiveData().observe(this) {
            binding.tvWithLiveData.text = it.toString()
        }

        /**
         * second one need to use lifecyle scope to make it because flow is coroutines
         * Collects from the flow when the View is at least STARTED and
         * SUSPENDS the collection when the lifecycle is STOPPED.
         * Collecting the flow cancels when the View is DESTROYED.
         */
        countDownJob = lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.counterFlow.collect {
                    binding.tvWithLifeCycle.text = it.toString()
                }
            }
        }

        /**
         * convert hot flow to live data
         */

        vm.stateFlow.observeLatest(this) {
            binding.tvHotLiveData.text = it.toString()
        }

        binding.tvHotLiveData.setOnClickListener {
            vm.incrementCounterForHotStateFlow()
        }

    }

    override fun onStop() {
        countDownJob?.cancel()
        super.onStop()
    }
}

