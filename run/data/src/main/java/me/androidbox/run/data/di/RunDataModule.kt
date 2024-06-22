package me.androidbox.run.data.di

import me.androidbox.run.data.workers.CreateRunWorker
import me.androidbox.run.data.workers.DeleteRunWorker
import me.androidbox.run.data.workers.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)
}