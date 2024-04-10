package me.arial.spawner.particle.util

import org.bukkit.Color

class RGBHelper {
    companion object {
        fun getColorWithRgb(rgb: String): Color {
            val args = rgb.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (args.size != 3) {
                return Color.BLACK
            }
            try {
                return Color.fromRGB(
                    args[0].toInt(),
                    args[1].toInt(),
                    args[2].toInt()

                )
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                return Color.BLACK
            }
        }
    }
}