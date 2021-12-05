package util

import java.math.BigInteger
import java.security.MessageDigest

@Suppress("unused") // probably used later on??
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
