package com.catpuppyapp.sshkeyman.utils

import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import java.io.ByteArrayOutputStream
import java.io.OutputStream


object SshKeyUtil {
    val charset = "UTF-8"

//    val bouncyCastleProviderStr="BC"

//    val ED25529 = "Ed25519"  // recommend，但太麻烦，算了，不支持了
    val ECDSAP256 = "ECDSA(P256)"
    val RSA2048 = "RSA(2048)"
//    val RSA4096 = "RSA(4096)"

    val algoList = listOf(RSA2048, ECDSAP256)


    fun createSshKeyEntity(name:String, algorithm: String, passphrase: String, email: String): SshKeyEntity {
        val privateOut = ByteArrayOutputStream()
        val publicOut = ByteArrayOutputStream()
        when (algorithm) {
//            ED25529 -> generateRSAKeyPair(privateOut, publicOut, passphrase, KeyPair.ED25519, 256, email)
            ECDSAP256 -> writePrivateAndPublicKey(privateOut, publicOut, passphrase, KeyPair.ECDSA, 256, email) // 使用 P-256 曲线
            RSA2048 -> writePrivateAndPublicKey(privateOut, publicOut, passphrase, KeyPair.RSA, 2048, email)
            else -> throw IllegalArgumentException("doesn't support algorithm: $algorithm")
        }

        return SshKeyEntity(
            privateKey = privateOut.toString(charset),
            publicKey = publicOut.toString(charset),
            email = email,
            passphrase = passphrase,
            algo = algorithm,
            name = name
        )
    }


    /**
     * the `keyLen` is non-sense for ED25519, whatever you pass the value, it will used 256 ever
     */
    @Throws(Exception::class)
    private fun writePrivateAndPublicKey(privateKeyOutStream: OutputStream, publicKeyOutStream: OutputStream, passphrase: String, keyType:Int, keyLen:Int, email:String) {
        val jsch = JSch()
        val keyPair: KeyPair = KeyPair.genKeyPair(jsch, keyType, keyLen)

        if(passphrase.isNotEmpty()) {
            keyPair.writePrivateKey(privateKeyOutStream, passphrase.toByteArray())
        }else {
            keyPair.writePrivateKey(privateKeyOutStream)
        }

        keyPair.writePublicKey(publicKeyOutStream, email)
    }












//
//    fun generateKeyPair_sshtools(name:String, algorithm: String, passphrase: String, email: String): SshKeyEntity {
//        return when (algorithm) {
////            ED25529 -> generateRSAKeyPair(name, passphrase, SshKeyPairGenerator.ED25519, 256, email)
//            ECDSAP256 -> generateKeyPair(name, passphrase, SshKeyPairGenerator.ECDSA, 256, email) // 使用 P-256 曲线
//            RSA2048 -> generateKeyPair(name, passphrase, SshKeyPairGenerator.SSH2_RSA, 2048, email)
//            else -> throw IllegalArgumentException("doesn't support algorithm: $algorithm")
//        }
//
//    }

//
//    private fun generateKeyPair_sshtools(name: String, passphrase: String, keyType:String, keyLen:Int, email:String):SshKeyEntity {
//        val privateKeyFile: File = FsUtils.createTempKeyFile("privKey")
//        val publicKeyFile: File = FsUtils.createTempKeyFile("pubKey")
//
//        val pair = SshKeyPairGenerator.generateKeyPair(keyType, keyLen)
//        SshKeyUtils.createPublicKeyFile(pair.publicKey, email, publicKeyFile);
//        SshKeyUtils.createPrivateKeyFile(pair, passphrase, privateKeyFile)
//
//        val privateKey = privateKeyFile.readText()
//        val publicKey = publicKeyFile.readText()
//
//        privateKeyFile.delete()
//        publicKeyFile.delete()
//
//        return SshKeyEntity(
//            privateKey = privateKey,
//            publicKey = publicKey,
//            email = email,
//            passphrase = passphrase,
//            algo = keyType,
//            name = name
//        )
//    }





}