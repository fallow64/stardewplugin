package dev.fallow.stardew.services

object ServiceManager {
    private val services = mutableListOf<StardewService>()

    fun register(vararg services: StardewService) {
        services.forEach {
            this.services.add(it)
            it.init()
        }
    }

    fun unregister(service: StardewService) {
        service.shutdown()
        services.remove(service)
    }

    fun unregisterAll() {
        services.forEach { it.shutdown() }
        services.clear()
    }
}
