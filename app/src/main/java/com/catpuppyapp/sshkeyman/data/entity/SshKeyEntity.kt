package com.catpuppyapp.sshkeyman.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.catpuppyapp.sshkeyman.data.entity.common.BaseFields
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
    var email:String ="",
    var publicKey:String="",
    var privateKey:String="",
    var passphrase:String="",
    /**
     * one of SshKeyUtil.algoList item
     */
    var algo:String="",
    var note:String="",  //备注

    @Embedded
    var baseFields: BaseFields=BaseFields(),

) {

}
