package com.rld.datingapp.data

interface Serializable {
    fun serialize(): String
}

interface Deserializable<T> {
    fun deserialize(serialized: String): T
}