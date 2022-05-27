package com.newcore.flowandsharedflowtuterial

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    /**
     * cold flow
     * if no subscriber it will not emit any thing
     */
    val counterFlow = flow {
        var currentValue = 10
        while (currentValue > 0) {
            delay(100L)
            emit(currentValue--)
        }
    }

    private val counterFlow2 = flow<Flow<Int>> {
        var currentValue = 10
        while (currentValue > 0) {
            delay(100L)
            emit(flow {
                delay(500L)
                emit(currentValue--)
            })
        }
    }


    /**
     * hot flow
     * it's act as live data but with flow
     * it's save state of current screen throw the lifecycle
     */
    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    /**
     * hot flow shared flow
     * it single time flow
     * u can list to it one time
     * but can control of emits new listeners can listen to it
     */
    private val _sharedFlow = MutableSharedFlow<Int>(1)
    val sharedFlow = _sharedFlow.asSharedFlow()


    init {
        //        basicOperators()
        //        terminalOperators()
        //        flatMapOperators()
        viewModelScope.launch {
            _sharedFlow.collect {
                delay(1000)
                Log.i("flowOperators", "_sharedFlow collector 1: $it")
            }
        }
        viewModelScope.launch {
            _sharedFlow.collect {
                delay(2000)
                Log.i("flowOperators", "_sharedFlow collector 2: $it")
            }
        }

        /**
         * all subscriber before the emit will listen to it
         * and all subscriber after the emit will listen to cashed emits that we control of size
         * of them
         */
        squareNumberForHotSharedFlow(5)

        viewModelScope.launch {
            _sharedFlow.collect {
                delay(3000)
                Log.i("flowOperators", "_sharedFlow collector 3: $it")
            }
        }
    }

    fun incrementCounterForHotStateFlow() {
        _stateFlow.value += 4
    }

    private fun squareNumberForHotSharedFlow(num: Int) = viewModelScope.launch {
        _sharedFlow.emit(num * num)
        _sharedFlow.emit(num * num * num)
    }

    private fun flatMapOperators() {
        viewModelScope.launch {

            /**
             * flatmap like lists it flatten the flow from flow<flow<int>> to flow<Int>
             */

            /**
             * flatMapConcat it concat flows one by one
             */
            counterFlow2.flatMapConcat { it }
                .collect {
                    Log.i("flowOperators", "flatMapConcat: $it")
                }

            /**
             * flatMapConcat it concat flows all at same time
             */
            counterFlow2.flatMapMerge { it }
                .collect {
                    Log.i("flowOperators", "flatMapMerge: $it")
                }
        } //end viewModelScope
    }

    private fun terminalOperators() {
        viewModelScope.launch {
            /**
             * this terminals make some operations on data the comes from the flow and return
             * result after flow finish
             */
            val count = counterFlow.count {
                it % 2 == 0
            }

            val reduce = counterFlow.reduce { acc, value ->
                acc + value
            }

            /**
             * like reduce but it start from init value
             */
            val fold = counterFlow.fold(100) { acc, value ->
                acc + value
            }

            Log.i("flowOperators", "count: $count")
            Log.i("flowOperators", "reduce: $reduce")
            Log.i("flowOperators", "fold: $fold")
        } //end viewModelScope
    }


    private fun basicOperators() {
        /**
         * some operators u can use with flow
         */
        viewModelScope.launch {
            counterFlow.filter {
                /**
                 * decide either continue to collect method or not
                 */
                it % 2 == 0
            }.map {
                /**
                 *  regular map method
                 */
                "hi from map: $it"
            }.onEach {
                /**
                 * it is middle war method u can print or do any thing without end the flow
                 * and it continue returning flow
                 */
                println("hi from middle flow where 1")
            }.onEach {
                /**
                 * it is middle war method u can print or do any thing without end the flow
                 * and it continue returning flow
                 */
                println("hi from middle flow where 2")
            }.collect {
                /**
                 * end statement of the flow operator and it used for output the result of
                 * operators above
                 */
                Log.i("flowOperators", "collector: $it ")
            }
        } //end viewModelScope

        /**
         *  this way is equal to the above way but without surround it with coroutine view model scope
         *  but u cant use collect statement use onEach instead
         */
        counterFlow.onEach { }.onEach { }.launchIn(viewModelScope)
    }
}