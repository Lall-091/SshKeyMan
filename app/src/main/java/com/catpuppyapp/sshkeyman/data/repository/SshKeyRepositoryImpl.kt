package com.catpuppyapp.sshkeyman.data.repository

import com.catpuppyapp.sshkeyman.data.dao.SshKeyDao
import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.catpuppyapp.sshkeyman.utils.MyLog
import com.catpuppyapp.sshkeyman.utils.decryptIfNotEmpty
import com.catpuppyapp.sshkeyman.utils.encryptIfNotEmpty
import com.catpuppyapp.sshkeyman.utils.getSecFromTime

private val TAG = "SshKeyRepositoryImpl"


class SshKeyRepositoryImpl(private val dao: SshKeyDao) : SshKeyRepository {

    override suspend fun insert(item: SshKeyEntity) {
        val funName = "insert"
        if(isNameExist(item.name)) {
            MyLog.w(TAG, "#$funName: warn: item's name '${item.name}' already exists! operation abort...")

            throw RuntimeException("#$funName err: name already exists")

        }

        item.passphrase = encryptIfNotEmpty(item.passphrase)

        dao.insert(item)
    }

    override suspend fun delete(item: SshKeyEntity, requireDelFilesOnDisk:Boolean, requireTransaction: Boolean) {
        dao.delete(item)  //删除仓库本身
    }


    override suspend fun update(item: SshKeyEntity, requeryAfterUpdate:Boolean) {
        val funName ="update"
        if(isNameAlreadyUsedByOtherItem(item.name, item.id)) {
            MyLog.w(TAG, "#$funName: warn: item's name '${item.name}' already used by other item! operation abort...")
            throw RuntimeException("#$funName err: name already exists")

        }

        item.passphrase = encryptIfNotEmpty(item.passphrase)

        item.baseFields.baseUpdateTime = getSecFromTime()

        dao.update(item)

    }

    override suspend fun isNameExist(name: String): Boolean {
        return dao.getIdByName(name) != null
    }

    override suspend fun getById(id: String): SshKeyEntity? {
        val item = dao.getById(id) ?: return null
        item.passphrase = decryptIfNotEmpty(item.passphrase)
        return item
    }


    override suspend fun getAll(): List<SshKeyEntity> {
        val list = dao.getAll()
        for(i in list) {
            i.passphrase = decryptIfNotEmpty(i.passphrase)
        }
        return list
    }


    override suspend fun updateName(id:String, name: String) {
        dao.updateName(id, name)
    }

    override suspend fun getAllNoDecrypt(): List<SshKeyEntity> {
        return dao.getAll()
    }

    override suspend fun updateNoEncrypt(item: SshKeyEntity) {
        item.baseFields.baseUpdateTime = getSecFromTime()
        dao.update(item)
    }

    override suspend fun getIdByNameAndExcludeId(name: String, excludeId: String): String? {
        return dao.getIdByNameAndExcludeId(name, excludeId)
    }

    override suspend fun isNameAlreadyUsedByOtherItem(name: String, excludeId: String): Boolean {
        return getIdByNameAndExcludeId(name, excludeId) != null
    }
}
