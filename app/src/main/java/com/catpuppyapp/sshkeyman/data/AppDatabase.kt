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
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.catpuppyapp.sshkeyman.data.dao.PassEncryptDao
import com.catpuppyapp.sshkeyman.data.dao.SshKeyDao
import com.catpuppyapp.sshkeyman.data.entity.PassEncryptEntity
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity

/**
 * Database class with a singleton Instance object.
 */
//entities是个数组，多个表多个类可直接写个逗号分隔列表
@Database(entities = [
                        SshKeyEntity::class,
                        PassEncryptEntity::class,
                     ],
    version = 1,
    //如果支持生成迁移sql，必须设置exportSchema为true，不然就得自己写sql了
    //自动迁移是根据导出的方案生成sql的
    exportSchema = true,
    autoMigrations = [
//    AutoMigration(from=4, to=5, spec=From4To5DelRepoColumn::class),
    // other migration if have
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun repoDao(): SshKeyDao
    abstract fun passEncryptDao(): PassEncryptDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null



        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "puppygitdb")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */

                        //迁移失败时删除所有数据重新建表，数据会丢，慎用，调试时频繁改表结构，开启这个很有用，生产环境最好别开，实在不行可通过清除app信息删除旧表
//                    //谨慎使用，最好别用 // ///////.fallbackToDestr///////uctiveMigration()


                    .addMigrations(

                        //more migration if have
                        )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
