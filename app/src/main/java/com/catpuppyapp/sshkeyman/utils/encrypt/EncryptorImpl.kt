package com.catpuppyapp.sshkeyman.utils.encrypt

val defaultEncryptor = object : Encryptor {
    override fun encrypt(raw: String, key: String): String {
        if(raw.isNullOrEmpty()) {
            return raw
        }

        return EncryptUtil.encryptString(raw,key)
    }

    override fun decrypt(encrypted: String, key: String): String {
        if(encrypted.isNullOrEmpty()) {
            return encrypted
        }

        return EncryptUtil.decryptString(encrypted, key)
    }

}

val encryptor_ver_1 = defaultEncryptor
