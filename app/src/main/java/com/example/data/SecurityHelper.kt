package com.example.data

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityHelper {
    fun randomSaltHex(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        return sha256Hex("$salt:$password")
    }

    fun generateDemoCode(): String {
        val code = 100000 + SecureRandom().nextInt(900000)
        return code.toString()
    }

    fun generatePrototypeCode(): String = generateDemoCode()
}
