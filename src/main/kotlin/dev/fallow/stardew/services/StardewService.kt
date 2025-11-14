package dev.fallow.stardew.services

import dev.fallow.stardew.StardewPlugin

abstract class StardewService {
    protected val plugin: StardewPlugin
        get() = StardewPlugin.main

    abstract fun init()
    open fun shutdown() {}
}
