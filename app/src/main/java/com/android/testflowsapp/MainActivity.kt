package com.android.testflowsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.testflowsapp.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            stateFlowCounterButton.setOnClickListener { viewModel.increment() }
            sharedFlowCounterButton.setOnClickListener { viewModel.squareNumber(sharedFlowCounterButton.text.toString().toInt()) }
        }

        collectLatestLifecycleFlow(viewModel.stateFlow) {
            println("StateFlow: Received: $it")
            binding.stateFlowCounterButton.text = "Counter: $it"
        }

        collectLifecycleFlow(viewModel.sharedFlow) {
            println("SharedFlow: Received: $it")
            binding.sharedFlowCounterButton.text = it.toString()
        }
    }

    fun <T> AppCompatActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collect)
            }
        }
    }
    fun <T> AppCompatActivity.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect {
                    collect(it)
                }
            }
        }
    }
}