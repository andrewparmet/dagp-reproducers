package com.example.androidconsumer

import com.example.androidproducer.Placeholder
import com.example.androidproducer.fixtures.FakeService

class ConsumerFixture(
    val fake: FakeService = FakeService(),
) {
    fun placeholderName(): String = Placeholder::class.java.name
}
