package me.androidbox.run.data.di

import me.androidbox.core.domain.run.SyncRunScheduler
import me.androidbox.run.data.scheduler.SyncRunWorkerSchedulerImp
import me.androidbox.run.data.workers.CreateRunWorker
import me.androidbox.run.data.workers.DeleteRunWorker
import me.androidbox.run.data.workers.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)

    singleOf(::SyncRunWorkerSchedulerImp).bind<SyncRunScheduler>()
}