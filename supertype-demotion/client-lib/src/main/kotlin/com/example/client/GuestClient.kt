package com.example.client

import com.example.base.BaseApiClient

class GuestClient : BaseApiClient() {
    fun guests(): String = get("/guests")
}
