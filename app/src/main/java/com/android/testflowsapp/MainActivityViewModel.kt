package com.android.testflowsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    val countDownFlow = flow<Int> {
        val startingValue = 5
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }.flowOn(dispatcherProvider.main)

//    init {
//        viewModelScope.launch(dispatcherProvider.main) {
//            sharedFlow.collect {
//                delay(2000L)
//                println("FirstFlow: The received number is: $it")
//            }
//        }
//        viewModelScope.launch(dispatcherProvider.main) {
//            sharedFlow.collect {
//                delay(3000L)
//                println("SecondFlow: The received number is: $it")
//            }
//        }
//        squareNumber(3)
//    }

    fun increment() {
        _stateFlow.value += 1
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch(dispatcherProvider.main) {
            _sharedFlow.emit(number*number)
        }
    }
}