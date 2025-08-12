package ru.rcfh.database.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.rcfh.database.AppDatabase

private const val DATABASE_NAME = "rcfh.db"

val DatabaseModule = module {
    single {
        val instance = Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()

        return@single instance
    }

    factory { get<AppDatabase>().formDao() }
    factory { get<AppDatabase>().documentDao() }
}