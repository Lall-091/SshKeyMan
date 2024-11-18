/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.catpuppyapp.sshkeyman.data

import android.content.Context
import com.catpuppyapp.sshkeyman.data.repository.PassEncryptRepository
import com.catpuppyapp.sshkeyman.data.repository.PassEncryptRepositoryImpl
import com.catpuppyapp.sshkeyman.data.repository.SshKeyRepository
import com.catpuppyapp.sshkeyman.data.repository.SshKeyRepositoryImpl

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val db:AppDatabase
    val sshKeyRepository: SshKeyRepository
    val passEncryptRepository: PassEncryptRepository
    // other repository write here
}

/**
 * [AppContainer] implementation that provides instance of [SshKeyRepositoryImpl]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val db: AppDatabase = AppDatabase.getDatabase(context)
    /**
     * Implementation for [SshKeyRepository]
     */
    override val sshKeyRepository: SshKeyRepository by lazy {
        SshKeyRepositoryImpl(db.repoDao())
    }

    override val passEncryptRepository: PassEncryptRepository by lazy {
        PassEncryptRepositoryImpl(db.passEncryptDao())
    }

}
