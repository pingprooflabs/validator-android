package com.example.ping_proof

import java.math.BigInteger

object Base58 {
    private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private val BASE = BigInteger.valueOf(ALPHABET.length.toLong())

    fun encode(input: ByteArray): String {
        var intData = BigInteger(1, input)
        val result = StringBuilder()

        while (intData > BigInteger.ZERO) {
            val remainder = intData.mod(BASE)
            intData = intData.divide(BASE)
            result.insert(0, ALPHABET[remainder.toInt()])
        }

        // Add '1' for each leading 0 byte
        for (b in input) {
            if (b.toInt() == 0) result.insert(0, ALPHABET[0])
            else break
        }

        return result.toString()
    }

    fun decode(input: String): ByteArray {
        var intData = BigInteger.ZERO

        for (char in input) {
            val digit = ALPHABET.indexOf(char)
            require(digit >= 0) { "Invalid Base58 character: $char" }
            intData = intData.multiply(BASE).add(BigInteger.valueOf(digit.toLong()))
        }

        val bytes = intData.toByteArray()
        val stripSignByte = bytes.size > 0 && bytes[0] == 0.toByte()

        val result = ByteArray(
            input.takeWhile { it == ALPHABET[0] }.length + bytes.size - if (stripSignByte) 1 else 0
        )
        System.arraycopy(
            bytes, if (stripSignByte) 1 else 0,
            result, result.size - (bytes.size - if (stripSignByte) 1 else 0),
            bytes.size - if (stripSignByte) 1 else 0
        )
        return result
    }
}
