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

package com.catpuppyapp.sshkeyman.data.repository

import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity

/**
 * passphrase 写的时候加密，查的时候自动解密
 */
interface SshKeyRepository {
    /**
     * Insert item in the data source
     */
    suspend fun insert(item: SshKeyEntity)

    /**
     * Delete item from the data source
     * 注：requireTransaction: 我不确定room的事务策略是怎样，不知道若已存在事务是加入还是新建，所以用这个变量来控制，若外部调用者有事务，传false即可
     */
    suspend fun delete(item: SshKeyEntity, requireDelFilesOnDisk:Boolean=false, requireTransaction: Boolean=true)
    suspend fun getIdByNameAndExcludeId(name:String, excludeId:String): String?
    suspend fun isNameAlreadyUsedByOtherItem(name:String, excludeId:String): Boolean

    /**
     * this method expect item with decrypted passphrase!
     */
    suspend fun update(item: SshKeyEntity, requeryAfterUpdate:Boolean=true)

    suspend fun isNameExist(name:String): Boolean

    suspend fun getById(id:String): SshKeyEntity?


    suspend fun getAll(): List<SshKeyEntity>


    suspend fun updateName(id:String, name: String)


}
