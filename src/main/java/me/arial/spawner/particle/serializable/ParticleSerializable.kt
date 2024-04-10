package me.arial.spawner.particle.serializable

interface ParticleSerializable {
    fun serialize(): Map<String, Any>
}