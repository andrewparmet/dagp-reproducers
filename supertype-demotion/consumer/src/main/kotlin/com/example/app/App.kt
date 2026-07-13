package com.example.app

import com.example.client.GuestClient

class App {
    fun run(): String = GuestClient().guests()
}
