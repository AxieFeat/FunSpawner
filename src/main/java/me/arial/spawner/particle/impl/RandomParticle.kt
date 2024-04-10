package me.arial.spawner.particle.impl

import me.arial.spawner.Main
import me.arial.spawner.particle.Particle
import me.arial.spawner.particle.ParticleType
import me.arial.spawner.particle.serializable.ParticleSerializable
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class RandomParticle (
    private var map: Map<String, Any>
) : Particle, ParticleSerializable {

    private var ticks = -1
    private var timeUpdate = 0
    private var used = false
    private var stop = false
    private var particle: ArrayList<org.bukkit.Particle>? = null
    private var radius = 0.0
    private var count = 0
    private var loc: Location? = null
    private var ser = false

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", -1) as Number).toInt()
        val particles = map.getOrDefault("particle", ArrayList<String>()) as List<String>
        particle = ArrayList()
        for (particle in particles) {
            this.particle!!.add(org.bukkit.Particle.valueOf(particle))
        }
    }

    private constructor(map: Map<String, Any>, ser: Boolean): this(map) {
        this.ser = ser
        this.map = map
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", -1) as Number).toInt()
        val particles = map.getOrDefault("particle", java.util.ArrayList<String>()) as List<String>

        particle = java.util.ArrayList()
        for (particle in particles) {
            this.particle!!.add(org.bukkit.Particle.valueOf(particle))
        }

        loc = map.getOrDefault("loc", null) as Location
        used = map.getOrDefault("used", false) as Boolean
        stop = map.getOrDefault("stop", false) as Boolean
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
                for (pr in particle!!) {
                    val x = ThreadLocalRandom.current().nextDouble(-radius, radius)
                    val y = ThreadLocalRandom.current().nextDouble(radius)
                    val z = ThreadLocalRandom.current().nextDouble(-radius, radius)
                    loc!!.world.spawnParticle(pr, loc!!.clone().add(x, y, z), count)
                }
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

    override fun clone(): Particle {
        if (ser) return RandomParticle(map, true)
        return RandomParticle(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.RANDOM_PARTICLE
    }

    override fun serialize(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()

        map["class"] = this.javaClass.name
        map["ticks"] = ticks
        map["timeUpdate"] = timeUpdate
        map["used"] = used
        map["stop"] = stop
        map["radius"] = radius
        map["count"] = count
        val part: MutableList<String> = java.util.ArrayList()
        particle!!.forEach(Consumer { p: org.bukkit.Particle -> part.add(p.name) })
        map["particle"] = part
        map["loc"] = loc!!

        return map
    }
    
    companion object {
        fun deserialize(map: Map<String, Any>): Particle {
            val randomParticle = RandomParticle(map, true)
            if (!randomParticle.stop && randomParticle.used && randomParticle.loc != null && randomParticle.loc!!.world != null) {
                randomParticle.run()
            }
            return randomParticle
        }
    }
}