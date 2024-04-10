package me.arial.spawner

import me.arial.spawner.particle.LoadParticles
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {

    companion object {
         var instance: Main? = null
    }

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        val particlesFile = File(dataFolder, "particles.yml")

        if (!particlesFile.exists()) saveResource("particles.yml", false)

        val config: FileConfiguration = YamlConfiguration.loadConfiguration(particlesFile)

        LoadParticles.loadEffect(config)

        server.pluginManager.registerEvents(EventListener(), this)
    }

    override fun onDisable() {

    }
}