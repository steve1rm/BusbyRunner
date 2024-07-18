package me.androidbox.core.connectivity.data.di

import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import me.androidbox.core.connectivity.data.WearNodeDiscoveryImp
import me.androidbox.core.connectivity.domain.NodeDiscovery
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    single<CapabilityClient> {
        Wearable.getCapabilityClient(androidContext())
    }

    factoryOf(::WearNodeDiscoveryImp).bind<NodeDiscovery>()
}