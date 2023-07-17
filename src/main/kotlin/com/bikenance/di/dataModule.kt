package com.bikenance.di

import com.bikenance.data.database.BikeDao
import com.bikenance.data.database.BikeRideDao
import com.bikenance.data.database.ProfileDao
import com.bikenance.data.database.UserDao
import com.bikenance.data.database.mongodb.*
import com.bikenance.data.repository.UserRepository
import org.koin.dsl.module

val dataModule = module {

    single<DB> {
        DB(createDatabase(get()))
    }

    single<DAOS> { DAOS(get()) }


    single<UserDao> {
        MongoUserDao(get())
    }
    single<ProfileDao> {
        MongoProfileDao(get())
    }

    single<BikeDao> {
        MongoBikeDao(get())
    }

    single<BikeRideDao> {
        MongoBikeRideDao(get())
    }

    single<UserRepository> {
        UserRepository(get())
    }
}