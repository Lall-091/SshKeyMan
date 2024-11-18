package com.catpuppyapp.sshkeyman.utils.encrypt

class PassEncryptHelper {
    companion object {
        //如果修改加密实现，更新这个版本号，并添加对应的key和encryptor
        val passEncryptCurrentVer = 1

        //迁移机制大概就是找到旧版本号的密钥和加密解密器，解密数据；然后使用新版本号的密钥和加密解密器加密数据，最后把加密后的数据写入数据库，就完了。
        //所以，很重要的一点就是：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，很重要的一点就是：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，很重要的一点就是：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，很重要的一点就是：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！
        //所以，很重要的一点就是：不要删除任何版本的密钥！否则对应版本的用户密码将全部作废！

        //新版本的app包含旧版的所有密钥和加密器
        //key = ver, value = 密钥
        val keyMap:MutableMap<Int,String> = mutableMapOf(
            Pair(1, "v_mb_z49Wbt7pjFKVN_Jitk2zVtxhKUZTWrrcqmvD"),
            // other...
        )
        //key = ver, value = 加密解密器
        val encryptorMap:MutableMap<Int,Encryptor> = mutableMapOf(
            Pair(1, encryptor_ver_1),
            // other...
        )

        val currentVerKey:String = keyMap[passEncryptCurrentVer]!!
        val currentVerEncryptor:Encryptor = encryptorMap[passEncryptCurrentVer]!!

        fun encryptWithCurrentEncryptor(raw:String):String {
            return currentVerEncryptor.encrypt(raw, currentVerKey)
        }
        fun decryptWithCurrentEncryptor(encryptedStr:String):String {
            return currentVerEncryptor.decrypt(encryptedStr, currentVerKey)
        }
    }
}
