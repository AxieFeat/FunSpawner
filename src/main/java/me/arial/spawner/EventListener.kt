package me.arial.spawner

import me.arial.spawner.util.Extension.Companion.playSoundFromConfig
import me.arial.spawner.util.Extension.Companion.send
import me.arial.spawner.particle.ParticleFactory
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.random.Random

class EventListener : Listener {

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.clickedBlock!!.type != Material.SPAWNER) return
        if (e.item == null || !e.item!!.type.toString().endsWith("SPAWN_EGG")) return

        val item = e.item!!

        if (Main.instance!!.config.getStringList("blocked").contains(item.type.toString())) {
            e.isCancelled = true

            e.player.send("messages.notAllowed")
            e.player.playSoundFromConfig("sounds.notAllowed")
            playParticle("particles.notAllowed", e.clickedBlock!!.location)

            return
        }

        val chances = getMap("chances") ?: mutableMapOf()

        if (!chances.keys.contains(item.type.toString())) return

        val random = chanceToBoolean(chances[item.type.toString()]!!)

        if (!random) {
            e.isCancelled = true
            e.player.send("messages.fail")
            e.player.playSoundFromConfig("sounds.fail")
            playParticle("particles.fail", e.clickedBlock!!.location)
            return
        } else {
            e.player.send("messages.success")
            e.player.playSoundFromConfig("sounds.success")
            playParticle("particles.success", e.clickedBlock!!.location)
            return
        }
    }

    private fun getMap(key: String): MutableMap<String, Int>? {
        val section = Main.instance!!.config.getConfigurationSection(key) ?: return null

        val map: MutableMap<String, Int> = mutableMapOf()

        section.getKeys(false).forEach {
            map[it] = section.getInt(it)
        }

        return map
    }

    private fun chanceToBoolean(chance: Int): Boolean {
        if (chance !in 0..100) {
            throw IllegalArgumentException("Шанс должен быть в диапазоне от 0 до 100")
        }
        return Random.nextInt(100) < chance
    }

    private fun playParticle(key: String, loc: Location) {
        val section = Main.instance!!.config.getConfigurationSection(key) ?: return

        if (!section.getBoolean("enabled")) return

        ParticleFactory.getEffect(section.getString("name"))?.start(loc)
    }

}