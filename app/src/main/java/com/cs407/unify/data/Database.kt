package com.cs407.unify.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cs407.unify.R
import androidx.room.Database

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deleteDao(): DeleteDao
    companion object {
        // Singleton prevents multiple instances of database
// opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
// If INSTANCE is not null, return it,
// otherwise create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.getString(R.string.database),
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}