package com.catpuppyapp.sshkeyman.utils.encrypt

interface Encryptor {
    //raw:未加密字符串；key:密钥
    fun encrypt(raw:String, key:String):String
    fun decrypt(encrypted:String, key:String):String
}
