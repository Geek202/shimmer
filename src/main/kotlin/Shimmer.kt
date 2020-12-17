package me.geek.tom.shimmer

import org.slf4j.LoggerFactory
import parsii.eval.Parser
import parsii.eval.Scope

val LOGGER = LoggerFactory.getLogger("Shimmer")

fun main() {
    LOGGER.info("Initialising shimmer...")
    val scope = Scope()
    val a = scope.getVariable("a")
    val expr = Parser().parse("3 * a * (4 + a)", scope)
    a.value = 5.0
    LOGGER.info("When a=5, 3*a*(4+a)={}", expr.evaluate())
    a.value = -2.0
    LOGGER.info("When a=-2, 3*a*(4+a)={}", expr.evaluate())
}
