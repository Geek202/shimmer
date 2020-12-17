package me.geek.tom.shimmer.simpleparticle

import me.geek.tom.thread.api.LedThread

abstract class Particle {
    abstract fun tick(system: ParticleSystem, interval: Double): Boolean
    abstract fun draw(thread: LedThread, partialTicks: Double)
}
