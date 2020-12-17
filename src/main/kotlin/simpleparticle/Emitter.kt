package me.geek.tom.shimmer.simpleparticle

import java.util.*

/**
 * An object that emits [Particle]s into a [ParticleSystem].
 * Each emitter is passed a [Random] that can be used in its tick method to control events.
 */
abstract class Emitter(
        internal val random: Random
) {
    /**
     * Updates the state of this emitter. This will ALWAYS be called on the main thread and so it is safe to modify
     * anything in the passed [ParticleSystem], but it is recommended to
     */
    abstract fun tick(system: ParticleSystem, interval: Double): Boolean
}
