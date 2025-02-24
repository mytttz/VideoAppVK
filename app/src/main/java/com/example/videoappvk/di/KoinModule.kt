package com.example.videoappvk.di

import com.example.videoappvk.database.AppDatabase
import com.example.videoappvk.network.ApiService
import com.example.videoappvk.network.ConnectivityChecker
import com.example.videoappvk.network.VideoRepository
import com.example.videoappvk.videolist.VideoListViewModel
import com.example.videoappvk.videoscreen.VideoScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { provideApiService() }
    single { VideoRepository(get(), get(), get()) }
    viewModel { VideoListViewModel(get(), get()) }
    viewModelOf(::VideoScreenViewModel)
    single { AppDatabase.getInstance(get()) }
    single { get<AppDatabase>().videoDao() }
    single { ConnectivityChecker(get()) }
}

fun provideApiService(): ApiService {
    return ApiService.create()
}