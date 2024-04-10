package me.arial.spawner.particle.serializable

import me.arial.spawner.particle.Particle

class ParticleDeserialize {
    companion object {
        private val deserializable = HashMap<String?, Class<*>?>()

        fun register(clazz: Class<out ParticleSerializable?>) {
            deserializable[clazz.name] = clazz
        }

        fun deserialize(map: Map<String?, Any?>): Particle? {
            try {
                val clazzName = map["class"] as String?
                val clazz = deserializable.getOrDefault(clazzName, null)
                    ?: throw NullPointerException("unknown class: $clazzName")

                val methodName = "deserialize"
                val parameterTypes = arrayOf<Class<*>>(MutableMap::class.java)
                val method = clazz.getMethod(methodName, *parameterTypes)

                val result = method.invoke(null, map)
                return result as Particle
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}