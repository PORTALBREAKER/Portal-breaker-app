package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ChapterEntity::class, ReadingSettingsEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PortalBreakerDatabase : RoomDatabase() {
    abstract fun chapterDao(): ChapterDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: PortalBreakerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PortalBreakerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PortalBreakerDatabase::class.java,
                    "portal_breaker_arun_vault"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            scope.launch(Dispatchers.IO) {
                                INSTANCE?.let { database ->
                                    database.settingsDao().saveSettings(ReadingSettingsEntity())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
