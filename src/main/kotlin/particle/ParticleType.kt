package me.geek.tom.shimmer.particle

interface ParticleType<P : Particle, C : ParticleTypeConfig> {

    fun getName(): String
    fun configure(params: Map<String, String>): P

}
