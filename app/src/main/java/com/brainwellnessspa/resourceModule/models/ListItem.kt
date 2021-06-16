package com.brainwellnessspa.resourceModule.models

abstract class ListItem {
    abstract val type: Int

    companion object {
        const val TYPE_BANNER = 0
        const val TYPE_GENERAL = 1
    }
}