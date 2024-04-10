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

class ExpandingCircle (
    private var map: Map<String, Any>
) : Particle, ParticleSerializable {

    private var ticks = -1
    private var timeUpdate = 0
    private var used = false
    private var stop = false
    private var particle: org.bukkit.Particle? = null
    private var radius = 0.0
    private var endRadius = 0.0
    private var stepRadius = 0.0
    private var count = 0
    private var step = 0.0
    private var offsets: Vector? = null
    private var numberOfSteps = 0.0
    private var size = 0.0
    private var color: Color? = null
    private var loc: Location? = null
    private var ser = false

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        particle = org.bukkit.Particle.valueOf((map.getOrDefault("particle", "FLAME") as String))
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        endRadius = (map.getOrDefault("end-radius", 0) as Number).toDouble()
        stepRadius = (map.getOrDefault("step-radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", -1) as Number).toInt()
        step = (map.getOrDefault("step", 0) as Number).toDouble()
        offsets = Vector(
            (map.getOrDefault("offset-x", 0) as Number).toDouble(),
            (map.getOrDefault("offset-y", 0) as Number).toDouble(),
            (map.getOrDefault("offset-z", 0) as Number).toDouble()
        )
        numberOfSteps = (map.getOrDefault("number-of-steps", 0.5) as Number).toDouble()
        size = (map.getOrDefault("size", 1) as Number).toDouble()
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
        endRadius = (map.getOrDefault("end-radius", 0) as Number).toDouble()
        stepRadius = (map.getOrDefault("step-radius", 0) as Number).toDouble()
        count = (map.getOrDefault("count", 0) as Number).toInt()
        step = (map.getOrDefault("step", 0) as Number).toDouble()
        offsets = map.getOrDefault("offsets", Vector(0, 0, 0)) as Vector
        numberOfSteps = (map.getOrDefault("number-of-steps", 0.5) as Number).toDouble()
        size = (map.getOrDefault("size", 1) as Number).toDouble()
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

    override fun end() {
        stop = true
    }

    fun run() {
        object : BukkitRunnable() {
            override fun run() {

                var y = 0.0

                while (y <= numberOfSteps) {
                    val x = radius * cos(y)
                    val z = radius * sin(y)
                    if (particle!!.name == "REDSTONE") loc!!.world.spawnParticle(
                        particle!!, loc!!.clone().add(
                            offsets!!
                        ).add(x, 0.0, z), count, org.bukkit.Particle.DustOptions(color!!, size.toFloat())
                    )
                    else loc!!.world.spawnParticle(particle!!, loc!!.clone().add(offsets!!).add(x, 0.0, z), count)
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
                radius += stepRadius
                if (radius >= endRadius) {
                    end()
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(Main.instance!!, timeUpdate.toLong(), timeUpdate.toLong())
    }

    override fun isUsed(): Boolean {
        return used
    }

    override fun clone(): Particle {
        if (ser) return ExpandingCircle(map, true)
        return ExpandingCircle(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.EXPANDING_CIRCLE
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
        map["end-radius"] = endRadius
        map["step-radius"] = stepRadius
        map["count"] = count
        map["step"] = step
        map["offsets"] = offsets!!
        map["number-of-steps"] = numberOfSteps
        map["size"] = size
        map["color"] = color!!
        map["loc"] = loc!!

        return map
    }

    companion object {
        fun deserialize(map: Map<String, Any>): Particle? {
            val expandingCircle = ExpandingCircle(map, true)
            if (!expandingCircle.stop && expandingCircle.used && expandingCircle.loc != null && expandingCircle.loc!!.world != null) {
                expandingCircle.run()
            }
            return null
        }
    }
}