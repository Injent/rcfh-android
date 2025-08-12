package ru.rcfh

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.rcfh.di.AppModule

class GlpmApplication : Application() {
    val appScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GlpmApplication)
            modules(AppModule)
        }
    }
}