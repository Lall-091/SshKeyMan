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

import androidx.room.withTransaction
import com.catpuppyapp.sshkeyman.data.dao.PassEncryptDao
import com.catpuppyapp.sshkeyman.data.entity.PassEncryptEntity
import com.catpuppyapp.sshkeyman.utils.AppModel
import com.catpuppyapp.sshkeyman.utils.encrypt.PassEncryptHelper

private val TAG = "PassEncryptRepositoryImpl"
class PassEncryptRepositoryImpl(private val dao: PassEncryptDao) : PassEncryptRepository {
    override suspend fun insert(item: PassEncryptEntity) {
        dao.insert(item)
    }

    override suspend fun delete(item: PassEncryptEntity) = dao.delete(item)

    override suspend fun update(item: PassEncryptEntity) = dao.update(item)

    override suspend fun getById(id: Int): PassEncryptEntity? {
        return dao.getById(id)
    }

    override suspend fun getOrInsertIdOne(): PassEncryptEntity {
        val item = getById(1)
        if(item!=null) {
            return item
        }

        //如果item==null，则插入
        val passEncryptEntity = PassEncryptEntity()
        passEncryptEntity.id=1  //固定常量
        passEncryptEntity.ver=PassEncryptHelper.passEncryptCurrentVer  //有可能变化，但这个变量永远指向最新版本

        insert(passEncryptEntity)  //写入数据库

        return passEncryptEntity
    }


    override suspend fun migrateIfNeed(sshkeyDb:SshKeyRepository) {
        val passEncryptEntity = getOrInsertIdOne()

        val ver = passEncryptEntity.ver
        if(ver != PassEncryptHelper.passEncryptCurrentVer) {  //ver不等于当前ver说明是旧版加密方式，迁移一下，否则无需执行操作
            //为迁移密码准备新旧密钥
            val oldKey = PassEncryptHelper.keyMap.get(ver)!!  //旧密钥
            val oldEncryptor = PassEncryptHelper.encryptorMap.get(ver)!!  //旧加密器

            //获取数据库中的原始内容，没加密也没解密的版本，正常来说取出的应该是加密过的pass字段，除非存的时候调错了方法
            val allCredentialList = sshkeyDb.getAllNoDecrypt()

            //开事务，避免部分成功部分失误导致密码乱套，如果乱套只能把credential全删了重建了
            AppModel.singleInstanceHolder.dbContainer.db.withTransaction {
                //迁移密码
                for(c in allCredentialList) {
                    //忽略空字符串
                    if(c.passphrase.isNullOrEmpty()) {
                        continue
                    }
                    val raw = oldEncryptor.decrypt(c.passphrase, oldKey)  //解密密码
                    c.passphrase = PassEncryptHelper.encryptWithCurrentEncryptor(raw)  //用新加密器加密密码
                    sshkeyDb.updateNoEncrypt(c)  //更新db
                }

                //更新passEncryptEntity
                passEncryptEntity.ver = PassEncryptHelper.passEncryptCurrentVer  //更新版本，下次就不会再执行迁移了，直到版本再度更新
                update(passEncryptEntity)  //更新db
            }
        }

    }

}
