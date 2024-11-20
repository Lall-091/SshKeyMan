package com.catpuppyapp.sshkeyman.utils.sshkey
//
//import com.jcraft.jsch.JSch
//import java.io.ByteArrayOutputStream
//
//
//class JschSkmKeyPairGen:SkmKeyPairGenerator {
//    // if generate rsa-4096 key, may need enlarge once, for other types, should enough
//    private val byteBufferSize=2048
//
//    private fun getKeyTypeByAlgorithm(algorithm:String):Int {
//        return when (algorithm) {
//            SkmKeyPairGenerator.ED25519 -> com.jcraft.jsch.KeyPair.ED25519
//            SkmKeyPairGenerator.ECDSA_P256 -> com.jcraft.jsch.KeyPair.ECDSA
//            SkmKeyPairGenerator.RSA2048 -> com.jcraft.jsch.KeyPair.RSA
//            SkmKeyPairGenerator.RSA4096 -> com.jcraft.jsch.KeyPair.RSA
//            else -> throw IllegalArgumentException("doesn't support algorithm: $algorithm")
//        }
//    }
//
//    override fun generateKeyPair(passphrase: String, algorithm:String, keyLen: Int, comment: String):SkmKeyPair {
//        val jsch = JSch()
//        val keyType = getKeyTypeByAlgorithm(algorithm)
//        val keyPair: com.jcraft.jsch.KeyPair = com.jcraft.jsch.KeyPair.genKeyPair(jsch, keyType, keyLen)
//        val privateKeyOutStream = ByteArrayOutputStream(byteBufferSize)
//        val publicKeyOutStream = ByteArrayOutputStream(byteBufferSize)
//
//        try {
//            if(passphrase.isNotEmpty()) {
//                keyPair.writePrivateKey(privateKeyOutStream, passphrase.toByteArray())
//            }else {
//                keyPair.writePrivateKey(privateKeyOutStream)
//            }
//
//            keyPair.writePublicKey(publicKeyOutStream, comment)
//
//            return SkmKeyPair(
//                privateKey = privateKeyOutStream.toString(SkmKeyPairGenerator.defaultCharset),
//                publicKey = publicKeyOutStream.toString(SkmKeyPairGenerator.defaultCharset),
//            )
//        }finally {
//            try {
//                privateKeyOutStream.close()
//                publicKeyOutStream.close()
//            }catch (_:Exception) {
//
//            }
//        }
//    }
//}
