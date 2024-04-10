package me.arial.spawner.particle

import me.arial.spawner.particle.ParticleFactory.Companion.EffectList
import me.arial.spawner.particle.ParticleType.Companion.CIRCLE
import me.arial.spawner.particle.ParticleType.Companion.EXPANDING_CIRCLE
import me.arial.spawner.particle.ParticleType.Companion.HELIX
import me.arial.spawner.particle.ParticleType.Companion.RANDOM_PARTICLE
import me.arial.spawner.particle.ParticleType.Companion.getByKey
import me.arial.spawner.particle.impl.*
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import java.util.*

class LoadParticles {
    companion object {
        fun loadEffect(file: FileConfiguration) {
            if (file.getConfigurationSection("particles") == null) return

            for (key in file.getConfigurationSection("particles")!!.getKeys(false)) {
                try {
                    val effType = file.getString(String.format("particles.%s.type", key))!!.replace("-", "_")
                        .lowercase(Locale.getDefault())

                    val particleType = getByKey(NamespacedKey.fromString(effType)!!)
                    val map = file.getConfigurationSection("particles.$key")!!
                        .getValues(false)

                    if (particleType === CIRCLE) {
                        //       EffectFactory.EffectList.put(key, new Circle(file, key));
                        EffectList[key] = Circle(map)
                        continue
                    }
                    if (particleType === HELIX) {
                        // EffectFactory.EffectList.put(key, new Helix(file, key));
                        EffectList[key] = Helix(map)
                        continue
                    }
                    if (particleType === EXPANDING_CIRCLE) {
                        //  EffectFactory.EffectList.put(key, new ExpandingCircle(file, key));
                        EffectList[key] = ExpandingCircle(map)
                        continue
                    }
                    if (particleType === RANDOM_PARTICLE) {
                        // EffectFactory.EffectList.put(key, new RandomParticle(file, key));
                        EffectList[key] = RandomParticle(map)
                        continue
                    }
                    if (particleType === ParticleType.SPAWN_GUARD) {
                        //  EffectFactory.EffectList.put(key, new Guard(file, key));
                        EffectList[key] = Guard(map)
                        continue
                    }
                    if (particleType === ParticleType.FIREWORK) {
                        // EffectFactory.EffectList.put(key, new FireworkEffect(file, key));
                        EffectList[key] = FireworkEffect(map)
                        continue
                    }
                    if (particleType === ParticleType.TORUS) {
                        // EffectFactory.EffectList.put(key, new Torus(file, key));
                        EffectList[key] = Torus(map)
                        continue
                    }
                    if (particleType === ParticleType.WRITHING_HELIX) {
                        // EffectFactory.EffectList.put(key, new WrithingHelix(file, key));
                        EffectList[key] = WrithingHelix(map)
                        continue
                    }
                    if (particleType === ParticleType.PARTICLE_EXPLOSION) {
                        // EffectFactory.EffectList.put(key, new ParticleExplosion(file, key));
                        EffectList[key] = ParticleExplosion(map)
                        continue
                    }
                    throw IllegalArgumentException()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }
}