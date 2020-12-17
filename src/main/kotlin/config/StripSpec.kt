package me.geek.tom.shimmer.config

import com.uchuhimo.konf.ConfigSpec

object StripSpec : ConfigSpec() {
    val host by required<String>()
    val port by required<Int>()
}
