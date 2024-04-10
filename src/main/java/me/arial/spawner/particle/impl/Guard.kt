package me.arial.spawner.particle.impl

import me.arial.spawner.Main
import me.arial.spawner.particle.Particle
import me.arial.spawner.particle.ParticleType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Zombie
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

class Guard(
    private var map: Map<String, Any>
) : Particle {
    private var ticks = -1
    private var timeUpdate = 0
    private var used = false
    private var stop = false
    private var radius = 0.0
    private var heal = 0.0
    private var count = 0
    private var loc: Location? = null
    private var entityName: String? = null
    private val zombies: List<Zombie> = ArrayList()

    init {
        ticks = (map.getOrDefault("ticks", -1) as Number).toInt()
        timeUpdate = (map.getOrDefault("timeUpdate", 0) as Number).toInt()
        radius = (map.getOrDefault("radius", 0) as Number).toDouble()
        heal = (map.getOrDefault("heal", 0) as Number).toDouble()
        count = (map.getOrDefault("count", 0) as Number).toInt()
        entityName = Objects.requireNonNull(map.getOrDefault("name", "") as String)
    }

    override fun start(location: Location) {
        loc = location.clone()
        used = true
        run()
    }

    override fun end() {
        stop = true
        zombies.forEach(Consumer { obj: Zombie -> obj.remove() })
    }

    override fun isUsed(): Boolean {
        return used
    }

    fun run() {
        object : BukkitRunnable() {
            override fun run() {
                for (i in 0 until count) {
                    val x = ThreadLocalRandom.current().nextDouble(-radius, radius)
                    var y: Double
                    val z = ThreadLocalRandom.current().nextDouble(-radius, radius)

                    y = loc!!.world.getHighestBlockAt(loc!!.clone().add(x, 0.0, z)).location.y

                    val zombie = loc!!.world.spawn(
                        Location(loc!!.world, loc!!.x + x + 0.5, y + 1, loc!!.z + z + 0.5),
                        Zombie::class.java
                    )

                    zombie.customName = entityName
                    zombie.seed = 2
                    zombie.maxHealth = heal
                    zombie.health = heal

                    val boot = ItemStack(Material.DIAMOND_BOOTS)
                    val im = boot.itemMeta
                    im.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, false)
                    im.addEnchant(Enchantment.VANISHING_CURSE, 1, false)
                    boot.setItemMeta(im)
                    zombie.equipment!!.boots = boot

                    val chest = ItemStack(Material.DIAMOND_CHESTPLATE)
                    val im2 = chest.itemMeta
                    im2.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, false)
                    im2.addEnchant(Enchantment.VANISHING_CURSE, 1, false)
                    chest.setItemMeta(im2)
                    zombie.equipment!!.chestplate = chest

                    val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
                    val im3 = leggings.itemMeta
                    im3.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, false)
                    im3.addEnchant(Enchantment.VANISHING_CURSE, 1, false)
                    leggings.setItemMeta(im3)
                    zombie.equipment!!.leggings = leggings

                    val helmet = ItemStack(Material.DIAMOND_HELMET)
                    val im4 = helmet.itemMeta
                    im4.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, false)
                    im4.addEnchant(Enchantment.VANISHING_CURSE, 1, false)
                    helmet.setItemMeta(im4)
                    zombie.equipment!!.helmet = helmet

                    val axe = ItemStack(Material.DIAMOND_AXE)
                    val im5 = axe.itemMeta
                    im5.addEnchant(Enchantment.KNOCKBACK, 2, false)
                    im5.addEnchant(Enchantment.DAMAGE_ALL, 5, false)
                    im5.addEnchant(Enchantment.VANISHING_CURSE, 1, false)
                    axe.setItemMeta(im5)
                    zombie.equipment!!.setItemInMainHand(axe)
                    zombie.lootTable = null
                    zombie.isGlowing = true
                    zombie.isCustomNameVisible = true
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
        }.runTaskTimer(Main.instance!!, timeUpdate.toLong(), timeUpdate.toLong())
    }

    override fun clone(): Particle {
        return Guard(map)
    }

    override fun getType(): ParticleType {
        return ParticleType.SPAWN_GUARD
    }


}