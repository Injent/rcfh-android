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
            .fallbackToDestructiveMigration()
            .build()

        instance.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER IF NOT EXISTS clear_refs_fts AFTER DELETE ON refs
            BEGIN
                DELETE FROM refs_fts WHERE rowid = old.rowid;
            END;
        """)

        return@single instance
    }

    factory { get<AppDatabase>().handbookDao() }
    factory { get<AppDatabase>().referenceDao() }
    factory { get<AppDatabase>().formDao() }
    factory { get<AppDatabase>().documentDao() }
}