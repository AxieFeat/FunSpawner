package me.arial.spawner.particle

class ParticleFactory {
    companion object {
        var EffectList: HashMap<String, Particle> = HashMap()

        fun getEffect(type: String?): Particle? {
            if (!EffectList.containsKey(type)) return null

            return EffectList[type]!!.clone()
        }
    }
}