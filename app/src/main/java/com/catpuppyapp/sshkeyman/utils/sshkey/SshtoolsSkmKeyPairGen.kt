package com.catpuppyapp.sshkeyman.utils.sshkey

import com.sshtools.common.publickey.SshKeyPairGenerator
import com.sshtools.common.publickey.SshPrivateKeyFileFactory
import com.sshtools.common.publickey.SshPublicKeyFileFactory


class SshtoolsSkmKeyPairGen:SkmKeyPairGenerator {
    private fun getKeyTypeByAlgorithm(algorithm:String):String {
        return when (algorithm) {
            SkmKeyPairGenerator.ED25529 -> SshKeyPairGenerator.ED25519
            SkmKeyPairGenerator.ECDSAP256 -> SshKeyPairGenerator.ECDSA
            SkmKeyPairGenerator.RSA2048 -> SshKeyPairGenerator.SSH2_RSA
            SkmKeyPairGenerator.RSA4096 -> SshKeyPairGenerator.SSH2_RSA
            else -> throw IllegalArgumentException("doesn't support algorithm: $algorithm")
        }
    }

    /**
     * @param passphrase if null or empty, no encrypt, else will encrypt the private key
     */
    override fun generateKeyPair(passphrase: String, algorithm: String, keyLen: Int, comment: String):SkmKeyPair {
//        val privateKeyFile: File = FsUtils.createTempKeyFile("privKey")
//        val publicKeyFile: File = FsUtils.createTempKeyFile("pubKey")

        val keyType = getKeyTypeByAlgorithm(algorithm)

        val pair = SshKeyPairGenerator.generateKeyPair(keyType, keyLen)
//        SshKeyUtils.createPublicKeyFile(pair.publicKey, comment, publicKeyFile)
//        SshKeyUtils.createPrivateKeyFile(pair, passphrase, privateKeyFile)
        val publicKeyFile = SshPublicKeyFileFactory.create(pair.publicKey, comment, SshPublicKeyFileFactory.OPENSSH_FORMAT)
        //调试了下，确定passphrase若为null或空字符串，则不加密，否则加密
        val privateKeyFile = SshPrivateKeyFileFactory.create(pair, passphrase, SshPrivateKeyFileFactory.OPENSSH_FORMAT)

        val publicKey = String(publicKeyFile.formattedKey, SkmKeyPairGenerator.defaultCharsetObj)
        val privateKey = String(privateKeyFile.formattedKey, SkmKeyPairGenerator.defaultCharsetObj)

//        privateKeyFile.delete()
//        publicKeyFile.delete()

        return SkmKeyPair(
            privateKey = privateKey,
            publicKey = publicKey,
        )
    }
}
