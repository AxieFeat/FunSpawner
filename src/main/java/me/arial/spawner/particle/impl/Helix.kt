package me.arial.spawner.particle.impl

import me.arial.spawner.Main
import me.arial.spawner.particle.Particle
import me.arial.spawner.particle.ParticleType
import me.arial.spawner.particle.serializable.ParticleSerializable
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class Helix (
    private var map: Map<String, Any>
) : Particle, ParticleSerializable {

    private var radius = 0.0
    private var height = 0.0
    private var step = 0.0
    private var loc: Location? = null
    private var color: Color? = null
    private var size = 0.0
    private var particle: org.bukkit.Particle? = null
    private var offsets: Vector? = null
    private var count = 0
    private var timeUpdate = 0
    private var ticks = 0
    private var used = false
    private var stop = false
    private var ser = false

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        particle = org.bukkit.Particle.valueOf((map.getOrDefault("particle", "FLAME") as String))
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        height = (map.getOrDefault("height", 0) as Number).toDouble()
        count = (map.getOrDefault("count", 0) as Number).toInt()
        step = (map.getOrDefault("step", 0) as Number).toDouble()
        offsets = Vector(
            (map.getOrDefault("offset-x", 0) as Number).toDouble(),
            (map.getOrDefault("offset-y", 0) as Number).toDouble(),
            (map.getOrDefault("offset-z", 0) as Number).toDouble()
        )
        size = (map.getOrDefault("size", 1) as Number).toDouble()
        color = Color.fromBGR(
            (map.getOrDefault("color-rgb-b", 255) as Number).toInt(),
            (map.getOrDefault("color-rgb-g", 255) as Number).toInt(),
            (map.getOrDefault("color-rgb-r", 255) as Number).toInt()
        )
    }

    private constructor(map: Map<String, Any>, ser: Boolean): this(map) {
        this.map = map
        this.ser = ser
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        particle = org.bukkit.Particle.valueOf((map.getOrDefault("particle", "FLAME") as String))
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        height = (map.getOrDefault("height", 0) as Number).toDouble()
        count = (map.getOrDefault("count", -1) as Number).toInt()
        step = (map.getOrDefault("step", 0) as Number).toDouble()
        offsets = map.getOrDefault("offsets", Vector(0, 0, 0)) as Vector
        size = (map.getOrDefault("size", 1) as Number).toDouble()
        color = map.getOrDefault("color", Color.fromRGB(255, 255, 255)) as Color
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

    fun run() {
        object : BukkitRunnable() {
            override fun run() {

                var y = 0.0
                while (y <= height) {
                    val x = radius * cos(y)
                    val z = radius * sin(y)
                    if (particle!!.name == "REDSTONE") loc!!.world.spawnParticle(
                        particle!!, loc!!.clone().add(
                            offsets!!
                        ).add(x, y, z), count, org.bukkit.Particle.DustOptions(color!!, size.toFloat())
                    )
                    else loc!!.world.spawnParticle(particle!!, loc!!.clone().add(offsets!!).add(x, y, z), count)

                    y += step
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

    override fun isUsed(): Boolean {
        return used
    }

    override fun clone(): Particle {
        if (ser) return Helix(map, true)
        return Helix(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.HELIX
    }

    override fun serialize(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()

        map["class"] = this.javaClass.name
        map["ticks"] = ticks
        map["timeUpdate"] = timeUpdate
        map["used"] = used
        map["stop"] = stop
        map["particle"] = particle!!.name
        map["radius"] = radius
        map["count"] = count
        map["step"] = step
        map["offsets"] = offsets!!
        map["size"] = size
        map["color"] = color!!
        map["loc"] = loc!!
        map["height"] = height

        return map
    }

    companion object {
        fun deserialize(map: Map<String, Any>): Particle {
            val helix = Helix(map, true)
            if (!helix.stop && helix.used && helix.loc != null && helix.loc!!.world != null) {
                helix.run()
            }
            return helix
        }
    }
}