package com.catpuppyapp.sshkeyman.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.catpuppyapp.sshkeyman.data.entity.common.BaseFields
import com.catpuppyapp.sshkeyman.utils.getFormatTimeFromSec
import com.catpuppyapp.sshkeyman.utils.getShortUUID

@Entity(tableName = "sshkey")
data class SshKeyEntity(
    @PrimaryKey
    var id: String = getShortUUID(),

//    var testMigra:String="",

    // repo push or pull etc time
    /**
     * usually repo name is not blank, but maybe is blank, no enforce check, e.g. if you import a empty name folder as repo, then will create
     *  a RepoEntity with blank name
     */
    var name: String = "",  //字段需唯一
    /**
     * comment, usually filled email to it
     * ps: 若翻译中文，comment翻译成“注释”比较好，note用作“备注”
     */
    var comment:String ="", // former name: `email`
    var publicKey:String="",
    var privateKey:String="",
    var passphrase:String="",
    /**
     * one of SshKeyUtil.algoList item
     */
    var algo:String="",
    /**
     * this is not in the sshkey pair, just save to db for let users write some note
     */
    var note:String="",  //备注

    @Embedded
    var baseFields: BaseFields=BaseFields(),

) {

    @Ignore
    var cachedCreateTime:String? = null

    fun getCreateTimeCached(): String {
        if(cachedCreateTime==null) {
            cachedCreateTime = getFormatTimeFromSec(baseFields.baseCreateTime)
        }

        return cachedCreateTime ?: ""
    }

}
