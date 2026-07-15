package com.example.consumer

import com.example.middle.Middle
import com.example.middle.leaf

fun useLeaf(): Int = Middle().leaf().value()
