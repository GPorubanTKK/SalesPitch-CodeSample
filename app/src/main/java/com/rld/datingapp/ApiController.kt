package com.rld.datingapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.rld.datingapp.data.Match
import com.rld.datingapp.data.MatchWrapper
import com.rld.datingapp.data.User
import com.rld.datingapp.data.ViewModel
import com.rld.datingapp.util.exposeAwareGson
import com.rld.datingapp.util.toByteArray
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.message.BasicNameValuePair
import java.io.BufferedInputStream

class ApiController(private val viewModel: ViewModel) {
    private val restEndpoint = "http://10.0.2.2:8080/app/api"
    //val restEndpoint = "http://10.23.102.199:8080/app/api"
    private val client: CloseableHttpClient = HttpClients.createDefault()
    private var getNextTracker = 0;
    private val gson = exposeAwareGson()

    suspend fun attemptLogin(email: String, password: String): User? {
        try {
            val user = client.execute(
                postWith(
                    "$restEndpoint/login",
                    BasicNameValuePair("email", email),
                    BasicNameValuePair("password", password)
                )
            ) { response -> decodeUser(response) }
            Log.d(LOGGERTAG, "Fetching matches")
            with(viewModel.matches) {
                clear()
                addAll(getMatches(user!!))
            }
            require(viewModel.verifySocketConnection(password))
            return user
        } catch (e: Exception) { return null }
    }

    suspend fun sendPasswordReset(email: String): Boolean = client.execute(
        postWith(
            "$restEndpoint/password/send",
            BasicNameValuePair("email", email)
        )
    ) { it }.code == HTTP_OK

    suspend fun verifyResetCode(email: String, code: String): Boolean = client.execute(
        postWith(
            "$restEndpoint/password/validate",
            BasicNameValuePair("email", email),
            BasicNameValuePair("code", code)
        )
    ) { it }.code == HTTP_OK

    suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Boolean = client.execute(
        postWith(
            "$restEndpoint/password/reset",
            BasicNameValuePair("email", email),
            BasicNameValuePair("code", resetCode),
            BasicNameValuePair("password", newPassword)
        )
    ) { it }.code == HTTP_OK

    suspend fun attemptMakeAccount(
        firstName: String,
        lastName: String,
        password: String,
        email: String,
        phoneNumber: String,
        profilePicture: Bitmap
    ): Boolean {
        val request = HttpPost("$restEndpoint/signup").apply {
            entity = MultipartEntityBuilder.create()
                .addTextBody("firstname", firstName)
                .addTextBody("lastname", lastName)
                .addTextBody("password", password)
                .addTextBody("email", email)
                .addTextBody("phone", phoneNumber)
                .addBinaryBody(
                    "profilePicture",
                    profilePicture.toByteArray(),
                    ContentType.APPLICATION_OCTET_STREAM,
                    "picture.jpg"
                )
                .build()
        }
        return client.execute(request) { response -> response.code == HTTP_OK }
    }

    suspend fun getNextUser(): User? {
        try {
            val request = HttpPost("$restEndpoint/getnext").apply {
                entity = UrlEncodedFormEntity(
                    listOf(
                        BasicNameValuePair("index", "$getNextTracker")
                    )
                )
            }
            getNextTracker++
            return client.execute(request) { response ->
                val user = decodeUser(response)
                Log.d(LOGGERTAG, (user == null).toString())
                if(user == null || user.email == viewModel.loggedInUser!!.email) null else user
            }
        } catch (e: Exception) {
            Log.e(LOGGERTAG, "Caught error when fetching user", e)
            return null
        }
    }

    suspend fun matchWith(currentUser: User, matchWith: User): Boolean {
        val request = HttpPost("$restEndpoint/match").apply {
            entity = UrlEncodedFormEntity(listOf(
                BasicNameValuePair("email", currentUser.email),
                BasicNameValuePair("match", matchWith.email)
            ))
        }
        return client.execute(request) { response -> response.code == HTTP_OK }
    }

    suspend fun getMatches(currentUser: User): List<Match> {
        val request = HttpPost("$restEndpoint/matches").apply {
            entity = UrlEncodedFormEntity(listOf(
                BasicNameValuePair("email", currentUser.email)
            ))
        }
        return client.execute(request) { response ->
            if(response.code != HTTP_OK) return@execute listOf()
            else {
                val matchResponse = response.entity.content.bufferedReader().readText()
                Log.e(LOGGERTAG, "Got: $matchResponse")
                val wrapper = gson.fromJson(matchResponse, MatchWrapper::class.java)
                return@execute wrapper.matches
            }
        }
    }

    private fun decodeUser(response: ClassicHttpResponse): User? {
        if(response.code != HTTP_OK) throw Exception("Failed request with code ${response.code}")
        val ds = ByteArrayDataSource(
            BufferedInputStream(response.entity.content),
            "application/octet-stream"
        )
        val multiPart = MimeMultipart(ds)
        if(multiPart.count != 2) throw Exception("Failed request. Response had ${multiPart.count} parts")
        val user = gson.fromJson(
            multiPart.getBodyPart(0).content as String,
            User::class.java
        )
        user.profilePicture = BitmapFactory.decodeStream(multiPart.getBodyPart(1).inputStream)
        return user
    }

    companion object {
        const val HTTP_OK = 200
    }

    private fun postWith(url: String, vararg body: NameValuePair = arrayOf()): HttpPost = HttpPost(url).apply {
        entity = UrlEncodedFormEntity(body.asList())
    }
}