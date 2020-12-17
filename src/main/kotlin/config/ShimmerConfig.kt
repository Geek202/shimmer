package me.geek.tom.shimmer.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml

class ShimmerConfig(
        private val config: Config
) {

    // Strip config
    val host: String get() = config[StripSpec.host]
    val port: Int get() = config[StripSpec.port]

}

val config = ShimmerConfig(Config {
    addSpec(StripSpec)
}.from.toml.watchFile("config.toml"))
