package me.geek.tom.shimmer.simpleparticle.simple

import me.geek.tom.shimmer.simpleparticle.Emitter
import me.geek.tom.shimmer.simpleparticle.ParticleSystem
import me.geek.tom.thread.api.util.Colour
import java.util.*
import kotlin.random.asKotlinRandom

class FadeParticleEmitter(
        random: Random,
        private val colours: Array<Colour>,
) : Emitter(random) {
    override fun tick(system: ParticleSystem, interval: Double): Boolean {
        if (random.nextInt(100) <= 90) {
            system.spawnParticle(SimpleFadeParticle(
                    random.nextInt(system.getLedThreadLength()),
                    this.colours.random(this.random.asKotlinRandom())
            ))
        }

        return true // This is a permanent emitter, and so never has the chance to 'die'
    }
}
