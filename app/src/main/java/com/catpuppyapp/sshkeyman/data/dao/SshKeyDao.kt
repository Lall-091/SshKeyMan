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

package com.catpuppyapp.sshkeyman.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity

/**
 * Database access object to access the Inventory database
 */
@Dao
interface SshKeyDao {

    @Insert
    suspend fun insert(item: SshKeyEntity)

    //update by id
    @Update
    suspend fun update(item: SshKeyEntity)

    //delete by id
    @Delete
    suspend fun delete(item: SshKeyEntity)


    @Query("SELECT id from sshkey where name=:name LIMIT 1")
    suspend fun getIdByName(name:String): String?

    /**
     * 更新时用来检查仓库名是否冲突的，如果其他条目存在相同仓库名，就冲突，排除id是需要更新的仓库的id，修改仓库信息时，自己的名字可以和自己的名字一样，所以检查时需要排除自己的id
     * for check sshkey name exists, excludeId used for exclude update item, because update a sshkey to same name is ok, in that case, should exclude itself id when checking name exist
     */
    @Query("SELECT id from sshkey where id!=:excludeId and name=:name LIMIT 1")
    suspend fun getIdByNameAndExcludeId(name:String, excludeId:String): String?

    @Query("SELECT * from sshkey where id=:id")
    suspend fun getById(id:String): SshKeyEntity?


//    @Query("SELECT * from sshkey WHERE baseIsDel=0 ORDER BY baseCreateTime DESC")
    @Query("SELECT * from sshkey ORDER BY baseCreateTime DESC")
    suspend fun getAll(): List<SshKeyEntity>


    @Query("update sshkey set name = :name where id = :id")
    suspend fun updateName(id:String, name: String)

}
