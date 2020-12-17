package me.geek.tom.shimmer.simpleparticle

import kotlinx.coroutines.runBlocking
import me.geek.tom.shimmer.workerExecutor
import me.geek.tom.thread.api.LedThread
import me.geek.tom.thread.api.util.Colour
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors


/**
 * Basic particle system. [Emitter]s are passed to the constructor, which are used to spawn particles.
 * The particle system is updated at a consistent interval and the ticking is ordered like this:
 * - Tick all [Emitter]s on the main thread. Permanent first, then temporary
 * - Remove all dead emitters.
 * - Tick all the [Particle]s using worker threads as there may be a lot of particles.
 * - Collect up a list of all 'dead' particles that will then be removed.
 * - Render all the particles synchronously to a buffered thread.
 * - Draw the buffered thread onto the remote set of pixels.
 *
 * ## Emitters
 * Each particle system has two types of emitters, permanent and temporary.
 * - Permanent emitters are passed in a list to the constructor, and cannot be added or removed after that.
 * These are essentially the 'base' of the [ParticleSystem]
 * - Temporary emitters act more like particles in the behaviour of their lifecycle, as they are created by other
 * emitters and can 'die' after a period of time.
 *
 * ## Particles
 * Particles are created by emitters while the animation is running, and are the visual part of a [ParticleSystem].
 */
class ParticleSystem(
        private val emitters: List<Emitter>,
        private val thread: LedThread,
) {

    // TODO: Move these values to the constructor and load them from config.
    companion object {
        /**
         * From my testing it appears that this value should be below the [TARGET_FPS] otherwise there are issues
         */
        const val TARGET_UPS = 10

        /**
         * 15 FPS seemed a good value from testing as larger values created a backlog of packets on the server that could
         * leave the animation running slower than expected.
         */
        const val TARGET_FPS = 15
    }

    private val temporaryEmitters: MutableList<Emitter> = ArrayList()
    private val particles: MutableList<Particle> = ArrayList()

    private var mainThread = Thread.currentThread()

    fun runSystem() {
        val timer = Timer()
        timer.init()
        var elapsedTime: Double
        var accumulator = 0.0
        var lastTickTime: Double = timer.time
        val interval: Double = 1.0 / TARGET_UPS

        val running = true
        while (running && thread.isReady()) {
            elapsedTime = timer.elapsedTime
            accumulator += elapsedTime
            while (accumulator >= interval) {
                tick(interval)
                lastTickTime = timer.time
                accumulator -= interval
            }

            draw(accumulator / interval)
            sync(timer)
        }
    }

    private fun sync(timer: Timer) {
        val loopSlot: Float = 1f / TARGET_FPS
        val endTime: Double = timer.lastLoopTime + loopSlot
        while (timer.time < endTime) {
            try {
                Thread.sleep(1)
            } catch (ie: InterruptedException) {
            }
        }
    }

    /**
     * With the default loop implementation, this will be called 30 times a second to update the state of the
     * [emitters][Emitter] and [particles][Particle]
     */
    private fun tick(interval: Double) {
        // Ensure we know what thread the tick was called on to ensure that the
        val cThread = Thread.currentThread()
        if (mainThread != cThread)
            this.mainThread = cThread

        // First tick regular emitters.
        emitters.forEach { it.tick(this, interval) }

        // Then tick all the temporary emitters, and collect up all the 'dead' ones.
        val deadEmitters = ArrayList<Emitter>()
        temporaryEmitters.forEach {
            if (it.tick(this, interval)) { // Tick returns true if the emitter is dead, like particles do.
                deadEmitters += it
            }
        }

        // Remove all the dead emitters from the list, so they aren't ticked again.
        temporaryEmitters.removeAll(deadEmitters)

        // Tick the particles in parallel, as we may have many of them.
        // We shouldn't need to do this for emitters, as there is typically a lot less.
        val deadParticles = particles.stream().map { particle ->
            CompletableFuture.supplyAsync({ particle.tick(this, interval) }, workerExecutor).thenApply { dead ->
                when (dead) {
                    true -> particle
                    else -> null
                }
            }
        }.map { it.join() }.filter { it != null }.map { it!! }.collect(Collectors.toList())

        // Do this on the main thread, as it could cause a ConcurrentModificationException if done asynchronously
        this.particles.removeAll(deadParticles)
    }

    private fun draw(partialTicks: Double) {
        // Clear the strip (set all pixels to black)
        this.thread.fillThread(Colour(0))

        // Draw!
        this.particles.forEach { particle ->
            particle.draw(this.thread, partialTicks)
        }

        // Render to the LedThread
        // This is a coroutine and so must be wrapped with runBlocking in order to block until it is complete.
        runBlocking {
            thread.render()
        }
    }

    fun getLedThreadLength(): Int {
        return this.thread.getLength()
    }

    /**
     * Spawns a new particle in this system.
     */
    fun spawnParticle(particle: Particle) {
        if (Thread.currentThread() != this.mainThread) throw IllegalStateException("Called off-thread!")
        this.particles += particle
    }

    internal class Timer {
        var lastLoopTime = 0.0
            private set

        fun init() {
            lastLoopTime = time
        }

        val time: Double
            get() = System.nanoTime() / 1000000000.0

        val elapsedTime: Double
            get() {
                val time = time
                val elapsedTime = time - lastLoopTime
                lastLoopTime = time
                return elapsedTime
            }
    }
}
