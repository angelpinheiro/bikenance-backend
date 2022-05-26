package com.bikenance.modules

import com.bikenance.database.UserDao
import com.bikenance.database.UserDaoFacade
import com.bikenance.repository.UserRepository
import org.koin.dsl.module


val appModule = module {
    single<UserDaoFacade> { UserDao() }
    single { UserRepository(get()) }
}