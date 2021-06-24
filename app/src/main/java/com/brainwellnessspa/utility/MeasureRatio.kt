package com.brainwellnessspa.utility

class MeasureRatio(var widthImg: Float,
    var height: Float,
    var ratio: Float,
    var proportion: Float) {
    var innerMargin = 0f
    fun setWidth(width: Int) {
        widthImg = width.toFloat()
    }

    fun setHeight(height: Int) {
        this.height = height.toFloat()
    }
}