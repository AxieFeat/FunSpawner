package me.arial.spawner.particle

import org.bukkit.Location

interface Particle {
    fun start(location: Location)
    fun end()
    fun isUsed(): Boolean
    fun clone(): Particle
    fun getType(): ParticleType
}