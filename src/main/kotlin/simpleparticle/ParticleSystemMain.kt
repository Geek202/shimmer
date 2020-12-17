package me.geek.tom.shimmer.simpleparticle

import me.geek.tom.shimmer.config.config
import me.geek.tom.shimmer.simpleparticle.simple.FadeParticleEmitter
import me.geek.tom.shimmer.workerExecutor
import me.geek.tom.thread.api.network.NetworkedThread
import me.geek.tom.thread.api.util.Colour
import org.slf4j.LoggerFactory
import java.util.*

val LOGGER = LoggerFactory.getLogger("Shimmer|Init")

fun main() {
    LOGGER.info("Starting shimmer using NetworkedStrip at ${config.host}:${config.port}...")
    val thread = NetworkedThread(config.host, config.port)
    while (!thread.isReady()) { Thread.sleep(5L) } // Wait for strip to connect...

    LOGGER.info("Connected to remote LED thread!")

    // Initialise a random to be used by the emitters
    val random = Random()
    // Create a new ParticleSystem
    val system = ParticleSystem(listOf(
            // Just create a single FadeParticleEmitter for now - this will be expanded and made data-driven later.
            FadeParticleEmitter(random, arrayOf(
                    Colour(255, 0, 0),   // Red
                    Colour(255, 255, 0), // Yellow
                    Colour(0, 255, 0),   // Green
                    Colour(0, 255, 255), // Cyan
                    Colour(0, 0, 255),   // Blue
                    Colour(255, 0, 255), // Magenta
            ))
    ), thread)

    // Start the particle system!
    system.runSystem()

    LOGGER.info("Particle system stopped! Shutting down...")
    workerExecutor.shutdown()
}
