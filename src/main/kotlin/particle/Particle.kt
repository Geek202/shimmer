package me.geek.tom.shimmer.particle

import me.geek.tom.thread.api.LedThread

abstract class Particle {

    abstract fun update()
    abstract fun render(thread: LedThread)

}
