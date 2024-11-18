package com.catpuppyapp.sshkeyman.utils

import com.catpuppyapp.sshkeyman.data.entity.SshKeyEntity
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.math.ec.rfc8032.Ed25519
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
import org.bouncycastle.pkcs.jcajce.JcaPKCS8EncryptedPrivateKeyInfoBuilder
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder
import org.bouncycastle.util.encoders.Base64
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Security
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object SshKeyUtil {
    val charset = "UTF-8"

    val bouncyCastleProviderStr="BC"

    val ED25529 = "Ed25519"  // recommend
    val ECDSAP256 = "ECDSA(P256)"
    val RSA2048 = "RSA(2048)"

    val algoList = listOf(ED25529, ECDSAP256, RSA2048)

//    fun main() {
//        for (algorithm in algoList) {
//            try {
//                val keyPair = generateKeyPair(algorithm)
//                getEncodedKey("${algorithm}_private_key", keyPair.private.encoded, true)
//                getEncodedKey("${algorithm}_public_key", keyPair.public.encoded, false)
//                println("$algorithm 密钥对已生成并保存。")
//            } catch (e: Exception) {
//                println("生成 $algorithm 密钥对时出错: ${e.message}")
//            }
//        }
//    }



    fun generateKeyPair(name:String, algorithm: String, passphrase: String, email: String): SshKeyEntity {
        val privateOut = ByteArrayOutputStream()
        val publicOut = ByteArrayOutputStream()
        when (algorithm) {
            ED25529 -> generateRSAKeyPair(privateOut, publicOut, passphrase, KeyPair.ED25519, 256, email)
            ECDSAP256 -> generateRSAKeyPair(privateOut, publicOut, passphrase, KeyPair.ECDSA, 256, email) // 使用 P-256 曲线
            RSA2048 -> generateRSAKeyPair(privateOut, publicOut, passphrase, KeyPair.RSA, 2048, email)
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
    private fun generateRSAKeyPair(privateKeyOutStream: OutputStream, publicKeyOutStream: OutputStream, passphrase: String, keyType:Int, keyLen:Int, email:String) {
        val jsch = JSch()
        val keyPair: KeyPair = KeyPair.genKeyPair(jsch, keyType, keyLen)
        keyPair.writePrivateKey(privateKeyOutStream, passphrase.toByteArray())
        keyPair.writePublicKey(publicKeyOutStream, email)
    }







}