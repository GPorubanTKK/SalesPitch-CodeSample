package com.rld.datingapp.data

import com.google.gson.annotations.Expose
import com.rld.datingapp.util.exposeAwareGson

data class Match(
    @Expose val user1: User,
    @Expose val user2: User
): Serializable {
    override fun serialize(): String = exposeAwareGson().toJson(this, Match::class.java)
    fun other(loggedIn: User): User = if(loggedIn.email == user1.email) user2 else user1
    companion object: Deserializable<Match> {
        override fun deserialize(serialized: String): Match = exposeAwareGson().fromJson(serialized, Match::class.java)
    }
}
data class MatchWrapper(
    @Expose val matches: List<Match>
)
