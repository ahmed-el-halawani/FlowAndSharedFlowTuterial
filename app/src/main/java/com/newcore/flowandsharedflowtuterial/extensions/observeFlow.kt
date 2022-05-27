package com.newcore.flowandsharedflowtuterial.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> Flow<T>.observeLatest(lifecycleOwner: LifecycleOwner, collectFunc: suspend (T) -> Unit) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectLatest {
                collectFunc(it)
            }
        }
    }
}

fun <T> Flow<T>.observe(lifecycleOwner: LifecycleOwner, collectFunc: suspend (T) -> Unit) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect {
                collectFunc(it)
            }
        }
    }
}