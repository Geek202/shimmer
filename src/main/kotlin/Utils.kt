package me.geek.tom.shimmer

import me.geek.tom.thread.api.util.Colour
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

private val nextThreadId = AtomicInteger()

val workerExecutor: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
//{
//    val t = Thread("Particle-Worker-${nextThreadId.incrementAndGet()}")
//    t.isDaemon = true
//    t
//}

fun colourLerp(colA: Colour, colB: Colour, k: Double): Colour {
    // Shortcut if we are at either input value.
    if (k == 0.0) return colA
    if (k == 1.0) return colB

    val r = lerp(colA.red,   colB.red,   k)
    val g = lerp(colA.green, colB.green, k)
    val b = lerp(colA.green, colB.green, k)
    return Colour(r, g, b)
}

fun lerp(a: Int, b: Int, k: Double): Int {
    return (a * (1 - k) + b * k).roundToInt()
}
