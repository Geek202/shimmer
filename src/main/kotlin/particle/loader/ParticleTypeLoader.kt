package me.geek.tom.shimmer.particle.loader

import me.geek.tom.shimmer.particle.ParticleType
import org.slf4j.LoggerFactory
import java.util.*

class ParticleTypeLoader {

    private val types: MutableMap<String, ParticleType<*, *>> = TreeMap()

    fun getType(name: String): ParticleType<*, *>? {
        return this.types[name]
    }

    fun clear() {
        this.types.clear()
    }

    fun locateParticleTypes() {
        val loader = ServiceLoader.load(ParticleType::class.java)
        val loadingErrors = HashMap<ParticleType<*, *>, Throwable>()
        loader.forEach { type ->
            try {
                val name = type.getName()
                if (this.types.containsKey(name)) {
                    throw InvalidNameException("$name (from ${type::class.java.name}) is already taken!")
                }

            } catch (e: Exception) {
                loadingErrors[type] = e
            }
        }

        if (loadingErrors.size > 0) {
            LOGGER.warn("The following errors occurred while loading:")
            LOGGER.warn("------------------------------------------------")
            for ((type, e) in loadingErrors) {
                LOGGER.warn("Error in ${type.getName()}:", e)
                LOGGER.warn("------------------------------------------------")
            }
        }
    }

    class InvalidNameException(message: String) : Exception(message)

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ParticleTypeLoader::class.java)
    }
}
