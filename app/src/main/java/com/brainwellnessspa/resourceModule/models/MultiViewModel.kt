package com.brainwellnessspa.resourceModule.models

class MultiViewModel(var type: Int, var text: String, var data: Int) {
    companion object {
        const val TYPE_BANNER = 0
        const val TYPE_IMAGE_WITH_TEXT = 1
    }
}