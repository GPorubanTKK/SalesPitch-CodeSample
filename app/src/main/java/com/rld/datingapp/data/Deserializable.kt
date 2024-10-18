package com.rld.datingapp.data

interface Deserializable<T> {
    fun deserialize(serialized: String): T
}