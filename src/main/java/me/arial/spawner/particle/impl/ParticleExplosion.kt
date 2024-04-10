package me.arial.spawner.particle.impl

import me.arial.spawner.Main
import me.arial.spawner.particle.ParticleType
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class ParticleExplosion (
    private var map: Map<String, Any>
) : me.arial.spawner.particle.Particle {

    private var ticks = -1
    private var used = false
    private var stop = false
    private var particle: Particle? = null
    private var radius = 0.0
    private var count = 0
    private var offsets: Vector? = null
    private var loc: Location? = null
    private var speed = 0.0
    private var timeUpdate = 0

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        particle = Particle.valueOf((map.getOrDefault("particle", "FLAME") as String))
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", 0) as Number).toInt()
        offsets = Vector(
            (map.getOrDefault("offset-x", 0) as Number).toDouble(),
            (map.getOrDefault("offset-y", 0) as Number).toDouble(),
            (map.getOrDefault("offset-z", 0) as Number).toDouble()
        )
        speed = (map.getOrDefault("speed", 0) as Number).toDouble()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
    }

    override fun start(location: Location) {
        loc = location.clone()
        used = true
        run()
    }

    override fun end() {
        stop = true
    }

    override fun isUsed(): Boolean {
        return used
    }

    fun run() {
        object : BukkitRunnable() {
            override fun run() {
                loc!!.world.spawnParticle(
                    particle!!,
                    loc!!.clone().add(offsets!!),
                    count,
                    radius,
                    radius,
                    radius,
                    speed
                )
                if (stop) cancel()
                if (ticks != -1) {
                    if ((ticks - timeUpdate) > 0) {
                        ticks -= timeUpdate
                    } else {
                        end()
                        cancel()
                    }
                }
            }
        }.runTaskTimerAsynchronously(Main.instance!!, timeUpdate.toLong(), timeUpdate.toLong())
    }

    override fun clone(): me.arial.spawner.particle.Particle {
        return ParticleExplosion(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.PARTICLE_EXPLOSION
    }

}