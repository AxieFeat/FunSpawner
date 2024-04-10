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

class Circle(
    private var map: Map<String, Any>
) : Particle, ParticleSerializable {

    private var ticks = -1
    private var timeUpdate = 0
    private var used = false
    private var stop = false
    private var particle: org.bukkit.Particle? = null
    private var radius = 0.0
    private var count = 0
    private var step = 0.0
    private var offsets: Vector? = null
    private var numberOfSteps = 0.0
    private var size = 0.0
    private var color: Color? = null
    private var loc: Location? = null
    private var direction: Vector? = null
    private var speed = 0.0
    private var ser = false

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        particle = org.bukkit.Particle.valueOf((map.getOrDefault("particle", "FLAME") as String))
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", -1) as Number).toInt()
        step = (map.getOrDefault("step", 0) as Number).toDouble()
        offsets = Vector(
            (map.getOrDefault("offset-x", 0) as Number).toDouble(),
            (map.getOrDefault("offset-y", 0) as Number).toDouble(),
            (map.getOrDefault("offset-z", 0) as Number).toDouble()
        )
        direction = Vector(
            (map.getOrDefault("direction-x", 0) as Number).toDouble(),
            (map.getOrDefault("direction-y", 0) as Number).toDouble(),
            (map.getOrDefault("direction-z", 0) as Number).toDouble()
        )
        numberOfSteps = (map.getOrDefault("number-of-steps", 0.5) as Number).toDouble()
        size = (map.getOrDefault("size", 1) as Number).toDouble()
        speed = (map.getOrDefault("speed", 0) as Number).toDouble()

        color = Color.fromBGR(
            (map.getOrDefault("color-rgb-b", 255) as Number).toInt(),
            (map.getOrDefault("color-rgb-g", 255) as Number).toInt(),
            (map.getOrDefault("color-rgb-r", 255) as Number).toInt()
        )
    }

    private constructor(map: Map<String, Any>, ser: Boolean): this(map) {
        this.ser = ser
        this.map = map
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        particle = org.bukkit.Particle.valueOf((map.getOrDefault("particle", "FLAME") as String))
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", -1) as Number).toInt()
        step = (map.getOrDefault("step", 0) as Number).toDouble()
        offsets = map.getOrDefault("offsets", Vector(0, 0, 0)) as Vector
        direction = map.getOrDefault("direction", Vector(0, 0, 0)) as Vector
        numberOfSteps = (map.getOrDefault("number-of-steps", 0.5) as Number).toDouble()
        size = (map.getOrDefault("size", 1) as Number).toDouble()
        speed = (map.getOrDefault("speed", 0) as Number).toDouble()
        color = map.getOrDefault("color", Color.fromRGB(255, 255, 255)) as Color
        loc = map.getOrDefault("loc", null) as Location
        used = map.getOrDefault("used", false) as Boolean
        stop = map.getOrDefault("stop", false) as Boolean
    }

    override fun start(location: Location) {
        this.loc = location.clone()
        used = true
        run()
    }

    fun run() {
        object : BukkitRunnable() {
            override fun run() {
                var y = 0.0

                while (y <= numberOfSteps) {

                    var x: Double = radius * cos(y)
                    val z: Double = radius * sin(y)

                    if (particle!!.name == "REDSTONE") loc!!.world.spawnParticle(
                        particle!!, loc!!.clone().add(
                            offsets!!
                        ).add(x, 0.0, z), count, org.bukkit.Particle.DustOptions(color!!, size.toFloat())
                    )
                    else loc!!.world.spawnParticle(
                        particle!!,
                        loc!!.clone().add(offsets!!).add(x, 0.0, z),
                        count,
                        direction!!.x,
                        direction!!.y,
                        direction!!.z,
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

                    y += step
                }
            }
        }.runTaskTimerAsynchronously(Main.instance!!, timeUpdate.toLong(), timeUpdate.toLong())
    }

    override fun end() {
        stop = true
    }

    override fun isUsed(): Boolean {
        return used
    }

    override fun clone(): Particle {
        if (ser) return Circle(map, true)
        return Circle(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.CIRCLE
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
        map["number-of-steps"] = numberOfSteps
        map["size"] = size
        map["color"] = color!!
        map["loc"] = loc!!
        map["direction"] = direction!!
        map["speed"] = speed

        return map
    }

    companion object {
        fun deserialize(map: Map<String, Any>): org.bukkit.Particle? {
            val circle = Circle(map, true)

            if (!circle.stop && circle.used && circle.loc != null && circle.loc!!.world != null) {
                circle.run()
            }
            return null
        }
    }

}