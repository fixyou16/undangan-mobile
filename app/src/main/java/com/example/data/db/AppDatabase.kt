package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        ThemeEntity::class,
        OrderEntity::class,
        InvitationEntity::class,
        GuestEntity::class,
        GreetingEntity::class,
        PaymentEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun themeDao(): ThemeDao
    abstract fun orderDao(): OrderDao
    abstract fun invitationDao(): InvitationDao
    abstract fun guestDao(): GuestDao
    abstract fun greetingDao(): GreetingDao
    abstract fun paymentEventDao(): PaymentEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "undangan_saas.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
