package com.catpuppyapp.sshkeyman.utils.sshkey

interface SkmKeyPairGenerator {
    companion object {
        val defaultCharset = "UTF-8"

//    val bouncyCastleProviderStr="BC"

        val ED25529 = "Ed25519"  // recommend，但太麻烦，算了，不支持了
        val ECDSAP256 = "ECDSA(P256)"
        val RSA2048 = "RSA(2048)"
        val RSA4096 = "RSA(4096)"

        val algoList = listOf(ED25529, ECDSAP256, RSA2048, RSA4096)

    }

    /**
     * the comment usually is email
     * for ed25519 the keyLen is nonsense, always 256
     */
    fun generateKeyPair(passphrase: String, algorithm:String, keyLen:Int, comment:String) :SkmKeyPair
}
