package me.geek.tom.shimmer.simpleparticle.simple

import me.geek.tom.shimmer.colourLerp
import me.geek.tom.shimmer.simpleparticle.Particle
import me.geek.tom.shimmer.simpleparticle.ParticleSystem
import me.geek.tom.thread.api.LedThread
import me.geek.tom.thread.api.util.Colour

class SimpleFadeParticle(
        private val pos: Int,
        private val col: Colour,
) : Particle() {

    private var previousAge = 1.0
    private var age = 1.0

    override fun tick(system: ParticleSystem, interval: Double): Boolean {
        previousAge = age
        age -= interval
        return age < 0
    }

    override fun draw(thread: LedThread, partialTicks: Double) {
        val targetColour = col * age
        val previousColour = col * previousAge

        thread.setPixel(pos, colourLerp(previousColour, targetColour, partialTicks))
    }
}
