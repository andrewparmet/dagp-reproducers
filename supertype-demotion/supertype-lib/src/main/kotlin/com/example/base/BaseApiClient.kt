package com.example.base

abstract class BaseApiClient {
    fun get(path: String): String = "GET $path"
}
