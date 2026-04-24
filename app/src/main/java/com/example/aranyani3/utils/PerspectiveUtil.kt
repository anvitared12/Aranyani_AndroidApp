package com.example.aranyani3.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

data class PotCalculationResult(
    val floorWidthCm: Float,
    val floorHeightCm: Float,
    val potsAlongWidth: Int,
    val potsAlongHeight: Int,
    val totalPots: Int,
    val pixelsPerCm: Float,
)

object PerspectiveUtils {

    private fun dist(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    private fun computeWarpDimensions(corners: List<Offset>): Pair<Float, Float> {
        val tl = corners[0]; val tr = corners[1]
        val br = corners[2]; val bl = corners[3]
        val widthTop = dist(tl, tr)
        val widthBottom = dist(bl, br)
        val width = maxOf(widthTop, widthBottom)
        val heightLeft = dist(tl, bl)
        val heightRight = dist(tr, br)
        val height = maxOf(heightLeft, heightRight)
        return Pair(width, height)
    }

    private fun gaussianElimination(a: Array<DoubleArray>, b: DoubleArray): DoubleArray {
        val n = b.size
        val m = Array(n) { i -> DoubleArray(n + 1) { j -> if (j < n) a[i][j] else b[i] } }
        for (col in 0 until n) {
            var maxRow = col
            for (row in col + 1 until n) {
                if (Math.abs(m[row][col]) > Math.abs(m[maxRow][col])) maxRow = row
            }
            val tmp = m[col]; m[col] = m[maxRow]; m[maxRow] = tmp
            if (Math.abs(m[col][col]) < 1e-10) continue
            for (row in col + 1 until n) {
                val factor = m[row][col] / m[col][col]
                for (j in col until n + 1) m[row][j] -= factor * m[col][j]
            }
        }
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            x[i] = m[i][n]
            for (j in i + 1 until n) x[i] -= m[i][j] * x[j]
            if (Math.abs(m[i][i]) > 1e-10) x[i] /= m[i][i]
        }
        return x
    }

    private fun getPerspectiveTransform(src: List<Offset>, dst: List<Offset>): DoubleArray {
        val a = Array(8) { DoubleArray(8) }
        val b = DoubleArray(8)
        for (i in 0 until 4) {
            val sx = src[i].x.toDouble(); val sy = src[i].y.toDouble()
            val dx = dst[i].x.toDouble(); val dy = dst[i].y.toDouble()
            a[2 * i][0] = sx; a[2 * i][1] = sy; a[2 * i][2] = 1.0
            a[2 * i][3] = 0.0; a[2 * i][4] = 0.0; a[2 * i][5] = 0.0
            a[2 * i][6] = -dx * sx; a[2 * i][7] = -dx * sy
            b[2 * i] = dx
            a[2 * i + 1][0] = 0.0; a[2 * i + 1][1] = 0.0; a[2 * i + 1][2] = 0.0
            a[2 * i + 1][3] = sx; a[2 * i + 1][4] = sy; a[2 * i + 1][5] = 1.0
            a[2 * i + 1][6] = -dy * sx; a[2 * i + 1][7] = -dy * sy
            b[2 * i + 1] = dy
        }
        return gaussianElimination(a, b)
    }

    private fun transformPoint(h: DoubleArray, p: Offset): Offset {
        val x = p.x.toDouble(); val y = p.y.toDouble()
        val w = h[6] * x + h[7] * y + 1.0
        val tx = (h[0] * x + h[1] * y + h[2]) / w
        val ty = (h[3] * x + h[4] * y + h[5]) / w
        return Offset(tx.toFloat(), ty.toFloat())
    }

    fun calculate(
        corners: List<Offset>,
        refPoints: List<Offset>,
        refLengthCm: Float,
        potDiameterCm: Float,
    ): PotCalculationResult {
        require(corners.size == 4) { "Need exactly 4 corner points" }
        require(refPoints.size == 2) { "Need exactly 2 reference points" }
        require(refLengthCm > 0) { "Reference length must be positive" }
        require(potDiameterCm > 0) { "Pot diameter must be positive" }

        val (warpW, warpH) = computeWarpDimensions(corners)

        val dst = listOf(
            Offset(0f, 0f),
            Offset(warpW, 0f),
            Offset(warpW, warpH),
            Offset(0f, warpH),
        )
        val h = getPerspectiveTransform(corners, dst)

        val r1w = transformPoint(h, refPoints[0])
        val r2w = transformPoint(h, refPoints[1])
        val refPixelDist = dist(r1w, r2w)

        val pixelsPerCm = refPixelDist / refLengthCm
        val widthCm = warpW / pixelsPerCm
        val heightCm = warpH / pixelsPerCm

        val potsW = (widthCm / potDiameterCm).toInt()
        val potsH = (heightCm / potDiameterCm).toInt()

        return PotCalculationResult(
            floorWidthCm = widthCm,
            floorHeightCm = heightCm,
            potsAlongWidth = potsW,
            potsAlongHeight = potsH,
            totalPots = potsW * potsH,
            pixelsPerCm = pixelsPerCm,
        )
    }
}
