package com.rld.datingapp

import android.graphics.Bitmap
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.MatchWrapper
import com.rld.datingapp.data.User
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.util.HTTP_OK
import com.rld.datingapp.util.MimeTypes
import com.rld.datingapp.util.SERVER_ADDR
import com.rld.datingapp.util.exposeAwareGson
import com.rld.datingapp.util.toBitmap
import com.rld.datingapp.util.toByteArray
import com.rld.datingapp.util.too
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.message.BasicNameValuePair

class ApiController(private val viewModel: ViewModel) {
    private val client: CloseableHttpClient = HttpClients.createDefault()
    private var getNextTracker = 0

    suspend fun attemptLogin(email: String, password: String): User? {
        return try {
            val (code, userParts) = client.multipartPost("users/auth", listOf(
                "email" to email,
                "password" to password
            ))
            require(code == HTTP_OK && viewModel.verifySocketConnection(email, password))
            unpackToUser(userParts)
        } catch (e: Exception) { null }
    }

    suspend fun attemptMakeAccount(
        firstName: String,
        lastName: String,
        password: String,
        email: String,
        phoneNumber: String,
        profilePicture: Bitmap,
    ): Boolean =
        client.bodilessPost("users/create", listOf(
            "email" to email,
            "password" to password,
            "firstname" to firstName,
            "lastname" to lastName,
            "phone" to phoneNumber
        ), listOf(
            "picture" to profilePicture.toByteArray() too ContentType.APPLICATION_OCTET_STREAM,
            "video" to byteArrayOf() too ContentType.APPLICATION_OCTET_STREAM
        )) == HTTP_OK

    suspend fun deleteAccount(email: String, password: String): Boolean =
        client.bodilessPost("users/delete", listOf(
            "email" to email,
            "password" to password
        )) == HTTP_OK

    suspend fun sendPasswordReset(email: String): Boolean =
        client.bodilessPost("password/requestreset", listOf("email" to email)) == HTTP_OK

    suspend fun verifyResetCode(email: String, code: String): Boolean =
        client.bodilessPost("password/verify", listOf(
            "email" to email, "code" to code
        )) == HTTP_OK

    suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Boolean =
        client.bodilessPost("password/doreset", listOf(
            "email" to email,
            "newPassword" to newPassword,
            "code" to resetCode
        )) == HTTP_OK

    suspend fun matchWith(currentUser: User, matchWith: User): Boolean =
        client.bodilessPost("matches/matchwith", listOf(
            "from" to currentUser.email,
            "to" to matchWith.email
        )) == HTTP_OK

    suspend fun getMatches(currentUser: User): List<Match> {
        return try {
            val (code, matches) = client.jsonPost<MatchWrapper>("matches/getmatches", listOf(
                "email" to currentUser.email
            ))
            require(code == HTTP_OK)
            matches.matches
        } catch (e: Exception) {
            listOf()
        }
    }

    suspend fun deleteMatch(currentUser: User, target: User): Boolean =
        client.bodilessPost("matches/deletematch", listOf(
            "from" to currentUser.email,
            "to" to target.email
        )) == HTTP_OK

    suspend fun getNextUser(currentUser: User): User? {
        return try {
            val (code, userParts) = client.multipartPost("matchmake/getnextuser", listOf(
                "email" to currentUser.email,
                "index" to getNextTracker.toString()
            ))
            require(code == HTTP_OK)
            getNextTracker++
            unpackToUser(userParts)
        } catch (e: Exception) { null }
    }

    private fun unpackToUser(multipart: MimeMultipart): User {
        require(multipart.count == 2)
        val user = with(multipart.getBodyPart(0)) {
            exposeAwareGson().fromJson(content as String, User::class.java)
        }
        user.profilePicture = with(multipart.getBodyPart(1)) {
            this.inputStream.readBytes().toBitmap()
        }
        return user
    }

    private inline fun <reified T> HttpClient.jsonPost(endpoint: String, entity: List<TextPart> = listOf()): Pair<Int, T> {
        val req = HttpPost("$SERVER_ADDR/$endpoint").setEntity(entity)
        return execute(req) { response ->
            val json = exposeAwareGson().fromJson("", T::class.java)
            return@execute response.code to json
        }
    }

    private fun HttpClient.bodilessPost(endpoint: String, entity: List<TextPart> = listOf()): Int {
        val req = HttpPost("$SERVER_ADDR/$endpoint").setEntity(entity)
        return execute(req) { response -> response.code }
    }

    private fun HttpClient.multipartPost(endpoint: String, entity: List<TextPart> = listOf()): Pair<Int, MimeMultipart> {
        val req = HttpPost("$SERVER_ADDR/$endpoint").setEntity(entity)
        return execute(req) { response ->
            val datasource = ByteArrayDataSource(response.entity.content, MimeTypes.APPLICATION_BINARY.value)
            return@execute response.code to MimeMultipart(datasource)
        }
    }

    private inline fun <reified T> HttpClient.jsonPost(
        endpoint: String,
        text: List<TextPart> = listOf(),
        binary: List<BinaryPart> = listOf()
    ): Pair<Int, T> {
        val req = HttpPost("$SERVER_ADDR/$endpoint").setEntity(text, binary)
        return execute(req) { response ->
            val json = exposeAwareGson().fromJson("", T::class.java)
            return@execute response.code to json
        }
    }

    private fun HttpClient.bodilessPost(
        endpoint: String,
        text: List<TextPart> = listOf(),
        binary: List<BinaryPart> = listOf()
    ): Int {
        val req = HttpPost("$SERVER_ADDR/$endpoint").setEntity(text, binary)
        return execute(req) { response -> response.code }
    }

    private fun HttpClient.multipartPost(
        endpoint: String,
        text: List<TextPart> = listOf(),
        binary: List<BinaryPart> = listOf()
    ): Pair<Int, MimeMultipart> {
        val req = HttpPost("$SERVER_ADDR/$endpoint").setEntity(text, binary)
        return execute(req) { response ->
            val datasource = ByteArrayDataSource(response.entity.content, MimeTypes.APPLICATION_BINARY.value)
            return@execute response.code to MimeMultipart(datasource)
        }
    }

    private fun HttpPost.setEntity(list: List<Pair<String, String>>): HttpPost {
        this.entity = UrlEncodedFormEntity(list.map { (k, v) -> BasicNameValuePair(k, v) })
        return this
    }

    private fun HttpPost.setEntity(
        text: List<TextPart> = listOf(),
        binary: List<BinaryPart>
    ): HttpPost {
        this.entity = run {
            val builder = MultipartEntityBuilder.create()
            for((key, value) in text) builder.addTextBody(key, value)
            for((key, bytes, type) in binary) builder.addBinaryBody(key, bytes, type, "$key.bin")
            builder.build()
        }
        return this
    }

}

private typealias BinaryPart = Triple<String, ByteArray, ContentType>
private typealias TextPart = Pair<String, String>