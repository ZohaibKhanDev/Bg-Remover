package com.example.bgremover.di

import com.example.bgremover.domain.repository.Repository
import com.example.bgremover.presentation.viewmodel.MainViewModel
import org.koin.dsl.module

val appModule = module {
    single { Repository() }
    single { MainViewModel(get()) }
}