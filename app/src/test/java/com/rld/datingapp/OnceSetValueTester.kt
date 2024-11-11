package com.rld.datingapp

import com.rld.datingapp.util.OnceSetValue
import org.junit.Test
import kotlin.test.assertFailsWith

class OnceSetValueTester {
    @Test fun ensureWriteWorks() {
        var test: Int by OnceSetValue()
        test = 6
    }

    @Test fun ensureReadWorks() {
        var test: Int by OnceSetValue()
        test = 4
        println("Test is $test")
    }

    @Test fun disallowMultipleWrites() {
        assertFailsWith<IllegalAccessException> {
            var test: Int by OnceSetValue()
            test = 4
            test = 5
        }
    }

    @Test fun ensureMultipleReads() {
        var  test: Int by OnceSetValue()
        test = 5
        var temp = 0
        for(i in 0..100) temp += test
    }
}