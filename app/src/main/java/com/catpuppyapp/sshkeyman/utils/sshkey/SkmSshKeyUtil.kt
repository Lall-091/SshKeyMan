package com.catpuppyapp.sshkeyman.utils.sshkey

import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Provider
import java.security.Security


object SkmSshKeyUtil {
//    val jschKeyGen = JschSkmKeyPairGen()
    val sshtoolKeyGen = SshtoolsSkmKeyPairGen()

    fun createSshKeyEntity(name:String, algorithm: String, passphrase: String, email: String): SshKeyEntity {
        val sshKeyPair = when (algorithm) {
            SkmKeyPairGenerator.ED25519 -> sshtoolKeyGen.generateKeyPair(passphrase, algorithm, 256, email)
            SkmKeyPairGenerator.ECDSA_P256 -> sshtoolKeyGen.generateKeyPair(passphrase, algorithm, 256, email) // 使用 P-256 曲线
            SkmKeyPairGenerator.RSA2048 -> sshtoolKeyGen.generateKeyPair(passphrase, algorithm, 2048, email)
            SkmKeyPairGenerator.RSA4096 -> sshtoolKeyGen.generateKeyPair(passphrase, algorithm, 4096, email)
            else -> throw IllegalArgumentException("doesn't support algorithm: $algorithm")
        }

        return SshKeyEntity(
            publicKey = sshKeyPair.publicKey,
            privateKey = sshKeyPair.privateKey,
            passphrase = passphrase,
            email = email,
            name = name,
            algo = algorithm
        )
    }




    /**
     * 经过测试：android 默认会注册BC导致你自己注册的失败
     * 参见：https://stackoverflow.com/questions/2584401/how-to-add-bouncy-castle-algorithm-to-android#66323575
     */
    fun isBouncyCastleRegistered(): Boolean {
        // 获取所有已注册的安全提供者
        val providers: Array<Provider> = Security.getProviders()
        for (provider in providers) {
            if (provider.name.equals(BouncyCastleProvider.PROVIDER_NAME, ignoreCase = true)) {
//            if (provider.name.equals("BC", ignoreCase = true)) { // or "BC"
                return true // Bouncy Castle 已注册
            }
        }
        return false // Bouncy Castle 未注册
    }

}