package ru.rcfh

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.rcfh.di.AppModule

class RcfhApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RcfhApplication)
            modules(AppModule)
        }
    }
}