package com.darchums.decoscan.core

import java.security.MessageDigest

object AuthManager {
    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun validatePassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }
}
