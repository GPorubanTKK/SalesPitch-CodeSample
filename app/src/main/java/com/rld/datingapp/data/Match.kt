package com.rld.datingapp.data

import com.google.gson.annotations.Expose

data class Match(
    @Expose val user1: User,
    @Expose val user2: User
)

data class MatchWrapper(
    @Expose val matches: List<Match>
)
