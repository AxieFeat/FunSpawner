package me.arial.spawner.particle

import org.bukkit.Keyed
import org.bukkit.NamespacedKey

data class ParticleType(
    private var key: NamespacedKey?
) : Keyed {

    companion object {

        private val byKey: HashMap<NamespacedKey, ParticleType?> = HashMap()

        var CIRCLE: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("circle")))

        var EXPANDING_CIRCLE: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("expanding_circle")))

        var FIREWORK: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("firework")))

        var SPAWN_GUARD: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("spawn_guard")))

        var RANDOM_PARTICLE: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("random_particle")))

        var TORUS: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("torus")))

        var WRITHING_HELIX: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("writhing_helix")))

        var HELIX: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("helix")))

        var PARTICLE_EXPLOSION: ParticleType =
            registerEffect(ParticleType(NamespacedKey.fromString("particle_explosion")))

        /**
         * @param key NamespacedKey of the event
         * @return null if the EffectType does not exist, otherwise returns EffectType
         */
        fun getByKey(key: NamespacedKey): ParticleType? {
            return byKey.getOrDefault(key, null)
        }

        /**
         * Registers a custom event
         * @param particleType the effectType to register
         * @return the registered effectType object
         * @throws IllegalArgumentException if this effectType is already registered
         */
        fun registerEffect(particleType: ParticleType): ParticleType {
            require(!byKey.containsKey(particleType.key)) { "Cannot set already-set effectType: " + particleType.key?.key }
            byKey[particleType.key!!] = particleType
            return particleType
        }

        fun hasEffectType(particleType: ParticleType): Boolean {
            return byKey.containsKey(particleType.key)
        }

        fun unRegisterEffectType(particleType: ParticleType) {
            byKey.remove(particleType.key)
        }
    }

    override fun getKey(): NamespacedKey {
        return key!!
    }
}
