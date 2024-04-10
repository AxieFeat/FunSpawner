package me.arial.spawner.util

import me.arial.spawner.Main
import org.bukkit.Sound
import org.bukkit.entity.Player

class Extension {
    companion object {
        fun Player.send(key: String) {
            if (player == null) return

            sendMessage(
                Color.parseString(
                    Main.instance!!.config.getString(key) ?: " "
                )
            )
        }

        fun Player.playSoundFromConfig(key: String) {
            if (player == null) return

            val section = Main.instance!!.config.getConfigurationSection(key) ?: return

            if (!section.getBoolean("enabled")) return

            player!!.playSound(
                location,
                Sound.valueOf(
                    section.getString("type") ?: "ENTITY_DOLPHIN_PLAY"
                ),
                section.getDouble("volume").toFloat(), section.getDouble("pitch").toFloat()
            )
        }
    }
}