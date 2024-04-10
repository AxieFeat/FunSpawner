package me.arial.spawner.particle.impl

import me.arial.spawner.Main
import me.arial.spawner.particle.Particle
import me.arial.spawner.particle.ParticleType
import me.arial.spawner.particle.util.RGBHelper.Companion.getColorWithRgb
import me.arial.spawner.particle.serializable.ParticleSerializable
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class FireworkEffect (
    private var map: Map<String, Any>
) : Particle, ParticleSerializable {
    private var ticks = -1
    private var timeUpdate = 0
    private var used = false
    private var stop = false
    private var startHeight = 0.0
    private var endHeight = 0.0
    private var stepHeight = 0.0
    private var loc: Location? = null
    private var colors: List<Color>? = null
    private var offsets: Vector? = null
    private var ser = false

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        val colorStrings = map.getOrDefault("colors", ArrayList<String>()) as List<String>
        val colors: MutableList<Color> = ArrayList()
        for (colorString in colorStrings) {
            colors.add(getColorWithRgb(colorString))
        }
        this.colors = colors
        offsets = Vector(
            (map.getOrDefault("offset-x", 0) as Number).toDouble(),
            (map.getOrDefault("offset-y", 0) as Number).toDouble(),
            (map.getOrDefault("offset-z", 0) as Number).toDouble()
        )
        startHeight = (map.getOrDefault("start-height", 0) as Number).toDouble()
        endHeight = (map.getOrDefault("end-height", 0) as Number).toDouble()
        stepHeight = (map.getOrDefault("step-height", 0) as Number).toDouble()
    }

    private constructor(map: Map<String, Any>, ser: Boolean): this(map) {
        this.ser = ser
        this.map = map
        ticks = map.getOrDefault("ticks", -1) as Int
        timeUpdate = map.getOrDefault("timeUpdate", 1) as Int
        used = map.getOrDefault("used", false) as Boolean
        stop = map.getOrDefault("stop", false) as Boolean
        startHeight = map.getOrDefault("startHeight", 0) as Double
        endHeight = map.getOrDefault("endHeight", 0) as Double
        stepHeight = map.getOrDefault("stepHeight", 1) as Double
        colors = map.getOrDefault("colors", arrayListOf<Color>()) as List<Color>
        offsets = map.getOrDefault("offsets", Vector(0, 0, 0)) as Vector
        loc = map.getOrDefault("loc", null) as Location
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
                val location = Location(loc!!.world, loc!!.x, loc!!.y, loc!!.z)
                location.add(offsets!!)
                val firework =
                    location.world.spawnEntity(location.add(0.0, startHeight, 0.0), EntityType.FIREWORK) as Firework
                val fireworkMeta = firework.fireworkMeta
                fireworkMeta.addEffect(
                    FireworkEffect.builder()
                        .withColor(colors!!).flicker(true)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .build()
                )
                firework.fireworkMeta = fireworkMeta
                fireworkMeta.power = 20
                firework.detonate()

                startHeight += stepHeight
                if (startHeight >= endHeight) cancel()
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
        if (ser) return FireworkEffect(map, true)
        return FireworkEffect(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.FIREWORK
    }

    override fun serialize(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()

        map["class"] = this.javaClass.name
        map["ticks"] = ticks
        map["timeUpdate"] = timeUpdate
        map["used"] = used
        map["stop"] = stop
        map["startHeight"] = startHeight
        map["endHeight"] = endHeight
        map["stepHeight"] = stepHeight
        map["colors"] = colors!!
        map["offsets"] = offsets!!
        map["loc"] = loc!!

        return map
    }
}