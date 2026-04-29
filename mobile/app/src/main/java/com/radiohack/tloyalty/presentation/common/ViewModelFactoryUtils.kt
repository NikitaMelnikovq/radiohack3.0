package com.radiohack.tloyalty.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.radiohack.tloyalty.TloyaltyApplication

val Context.appContainer
    get() = (applicationContext as TloyaltyApplication).container

inline fun <VM : ViewModel> viewModelFactory(crossinline create: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
    }
}
