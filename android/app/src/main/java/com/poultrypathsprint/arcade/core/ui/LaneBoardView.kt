package com.poultrypathsprint.arcade.core.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.core.config.GameConfig
import com.poultrypathsprint.arcade.domain.model.ObstacleKind
import com.poultrypathsprint.arcade.domain.model.RoadRow
import com.poultrypathsprint.arcade.domain.model.RowType
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min

class LaneBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface BoardListener {
        fun onBoardHop()
        fun onBoardLane(delta: Int)
    }

    var listener: BoardListener? = null

    private val density = resources.displayMetrics.density
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private val image = Paint(Paint.FILTER_BITMAP_FLAG)
    private val cell = RectF()

    private val colorGrass = ContextCompat.getColor(context, R.color.sage)
    private val colorGrassAlt = ContextCompat.getColor(context, R.color.sage_deep)
    private val colorRoad = ContextCompat.getColor(context, R.color.road_slate)
    private val colorRail = ContextCompat.getColor(context, R.color.road_slate_deep)
    private val colorCorn = ContextCompat.getColor(context, R.color.corn)
    private val colorTerracotta = ContextCompat.getColor(context, R.color.terracotta)
    private val colorSand = ContextCompat.getColor(context, R.color.sand_muted)
    private val colorCream = ContextCompat.getColor(context, R.color.cream_deep)
    private val colorInk = ContextCompat.getColor(context, R.color.ink)

    private var rows: List<RoadRow> = emptyList()
    private var henRow: Int = GameConfig.START_ROW
    private var henLane: Int = 1
    private var cameraRow: Float = GameConfig.START_CAMERA_ROW
    private var henScale: Float = 1f
    private var featherPhase: Float = 0f
    private var warningPhase: Float = 0f
    private var highContrast: Boolean = false
    private var henTint: Int = 0

    private var frame = 0f
    private var laneWidth = 0f
    private var rowHeight = GameConfig.ROW_HEIGHT_DP * density
    private var playBottom = 0f
    private val swipeThreshold = GameConfig.SWIPE_THRESHOLD_DP * density

    private var henBitmap: Bitmap? = null
    private var wagonBitmap: Bitmap? = null
    private var trainBitmap: Bitmap? = null
    private var featherBitmap: Bitmap? = null
    private var puddleBitmap: Bitmap? = null
    private var gateBitmap: Bitmap? = null
    private var barnBitmap: Bitmap? = null

    private var downX = 0f
    private var downY = 0f

    init {
        isClickable = true
        isFocusable = true
        stroke.style = Paint.Style.STROKE
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        loadBitmaps()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        frame = GameConfig.BOARD_FRAME_DP * density
        laneWidth = floor((w - 2f * frame) / GameConfig.LANES)
        rowHeight = GameConfig.ROW_HEIGHT_DP * density
        playBottom = h - GameConfig.CONTROL_RESERVE_DP * density
    }

    fun update(
        newRows: List<RoadRow>,
        row: Int,
        lane: Int,
        camera: Float,
        scale: Float,
        bob: Float,
        contrast: Boolean,
        tint: Int
    ) {
        rows = newRows
        henRow = row
        henLane = lane
        cameraRow = camera
        henScale = scale
        featherPhase = bob
        warningPhase = bob
        highContrast = contrast
        henTint = tint
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (laneWidth <= 0f) {
            return
        }
        val boardWidth = laneWidth * GameConfig.LANES
        val left = frame
        fill.color = colorCream
        canvas.drawRect(left, 0f, left + boardWidth, height.toFloat(), fill)

        for (row in rows) {
            val bottom = playBottom - (row.index - cameraRow) * rowHeight
            val top = bottom - rowHeight
            if (bottom < -rowHeight || top > height.toFloat()) {
                continue
            }
            drawRow(canvas, row, left, boardWidth, top, bottom)
        }
        drawLaneDividers(canvas, left, boardWidth)
        drawHen(canvas, left)
    }

    private fun drawRow(canvas: Canvas, row: RoadRow, left: Float, boardWidth: Float, top: Float, bottom: Float) {
        fill.color = when (row.type) {
            RowType.GRASS -> if (row.index % 2 == 0) colorGrass else colorGrassAlt
            RowType.BARN -> colorGrass
            RowType.DIRT_ROAD -> colorRoad
            RowType.RAIL -> colorRail
            RowType.PUDDLE -> colorSand
            RowType.GATE -> colorRoad
        }
        canvas.drawRect(left, top, left + boardWidth, bottom, fill)

        if (row.type == RowType.DIRT_ROAD) {
            stroke.color = colorCorn
            stroke.alpha = if (highContrast) 210 else 130
            stroke.strokeWidth = 3f * density
            val mid = (top + bottom) / 2f
            var x = left + laneWidth * 0.15f
            while (x < left + boardWidth) {
                canvas.drawLine(x, mid, x + laneWidth * 0.3f, mid, stroke)
                x += laneWidth * 0.6f
            }
        }

        if (row.type == RowType.RAIL) {
            fill.color = colorCorn
            fill.alpha = if (row.warning) (140 + (warningPhase * 110f).toInt()).coerceIn(0, 255) else 190
            var x = left + 6f * density
            val sleeperW = 10f * density
            while (x < left + boardWidth) {
                canvas.drawRect(x, top + rowHeight * 0.26f, x + sleeperW, bottom - rowHeight * 0.26f, fill)
                x += sleeperW * 2.4f
            }
            fill.alpha = 255
        }

        if (row.type == RowType.BARN) {
            fill.color = colorCorn
            fill.alpha = 210
            canvas.drawRect(left, top, left + boardWidth, top + 8f * density, fill)
            fill.alpha = 255
            barnBitmap?.let { bitmap ->
                drawSprite(canvas, bitmap, left + boardWidth - laneWidth * 0.5f, bottom, rowHeight * 0.96f, laneWidth)
            }
        }

        for (obstacle in row.obstacles) {
            when (obstacle.kind) {
                ObstacleKind.WAGON -> wagonBitmap?.let {
                    drawSprite(
                        canvas,
                        it,
                        left + (obstacle.position + obstacle.width / 2f) * laneWidth,
                        bottom - rowHeight * 0.08f,
                        rowHeight * 0.78f,
                        obstacle.width * laneWidth
                    )
                }
                ObstacleKind.TRAIN -> trainBitmap?.let {
                    drawSprite(
                        canvas,
                        it,
                        left + (obstacle.position + obstacle.width / 2f) * laneWidth,
                        bottom - rowHeight * 0.06f,
                        rowHeight * 0.88f,
                        obstacle.width * laneWidth
                    )
                }
                ObstacleKind.PUDDLE -> puddleBitmap?.let {
                    drawSprite(
                        canvas,
                        it,
                        left + (obstacle.position + obstacle.width / 2f) * laneWidth,
                        bottom - rowHeight * 0.12f,
                        rowHeight * 0.66f,
                        obstacle.width * laneWidth
                    )
                }
                ObstacleKind.GATE -> drawGate(canvas, row.gateOpen, left, boardWidth, top, bottom)
            }
        }

        if (row.hasFeather) {
            featherBitmap?.let {
                val bob = featherPhase * 8f * density
                drawSprite(
                    canvas,
                    it,
                    left + (row.featherLane + 0.5f) * laneWidth,
                    bottom - rowHeight * 0.24f - bob,
                    rowHeight * 0.34f,
                    laneWidth * 0.5f
                )
            }
        }
    }

    private fun drawGate(canvas: Canvas, open: Boolean, left: Float, boardWidth: Float, top: Float, bottom: Float) {
        if (open) {
            stroke.color = colorTerracotta
            stroke.alpha = if (highContrast) 220 else 150
            stroke.strokeWidth = 4f * density
            canvas.drawLine(left, top + 4f * density, left + boardWidth, top + 4f * density, stroke)
            canvas.drawLine(left, bottom - 4f * density, left + boardWidth, bottom - 4f * density, stroke)
            return
        }
        val bitmap = gateBitmap
        if (bitmap == null) {
            fill.color = colorTerracotta
            canvas.drawRect(left, top + rowHeight * 0.2f, left + boardWidth, bottom - rowHeight * 0.2f, fill)
            return
        }
        for (lane in 0 until GameConfig.LANES) {
            drawSprite(
                canvas,
                bitmap,
                left + (lane + 0.5f) * laneWidth,
                bottom - rowHeight * 0.06f,
                rowHeight * 0.84f,
                laneWidth
            )
        }
    }

    private fun drawLaneDividers(canvas: Canvas, left: Float, boardWidth: Float) {
        stroke.color = colorInk
        stroke.alpha = if (highContrast) 90 else 40
        stroke.strokeWidth = 2f * density
        for (lane in 1 until GameConfig.LANES) {
            val x = left + lane * laneWidth
            canvas.drawLine(x, 0f, x, height.toFloat(), stroke)
        }
        stroke.alpha = 255
    }

    private fun drawHen(canvas: Canvas, left: Float) {
        val bitmap = henBitmap ?: return
        val bottom = playBottom - (henRow - cameraRow) * rowHeight - rowHeight * 0.1f
        if (bottom < -rowHeight || bottom > height + rowHeight) {
            return
        }
        val centerX = left + (henLane + 0.5f) * laneWidth
        val targetH = rowHeight * 0.74f * henScale
        if (henTint != 0) {
            fill.color = henTint
            fill.alpha = 70
            canvas.drawCircle(centerX, bottom - targetH * 0.42f, targetH * 0.56f, fill)
            fill.alpha = 255
        }
        drawSprite(canvas, bitmap, centerX, bottom, targetH, laneWidth * 0.92f)
    }

    private fun drawSprite(canvas: Canvas, bitmap: Bitmap, centerX: Float, bottomY: Float, maxH: Float, maxW: Float) {
        val bw = bitmap.width.toFloat()
        val bh = bitmap.height.toFloat()
        if (bw <= 0f || bh <= 0f) {
            return
        }
        val scale = min(maxH / bh, maxW / bw)
        val drawW = bw * scale
        val drawH = bh * scale
        cell.set(centerX - drawW / 2f, bottomY - drawH, centerX + drawW / 2f, bottomY)
        canvas.drawBitmap(bitmap, null, cell, image)
    }

    private fun loadBitmaps() {
        if (henBitmap != null) {
            return
        }
        henBitmap = decode(R.drawable.sprite_hen_runner)
        wagonBitmap = decode(R.drawable.sprite_wagon)
        trainBitmap = decode(R.drawable.sprite_train)
        featherBitmap = decode(R.drawable.sprite_feather)
        puddleBitmap = decode(R.drawable.sprite_puddle)
        gateBitmap = decode(R.drawable.sprite_gate)
        barnBitmap = decode(R.drawable.sprite_barn)
    }

    private fun decode(resId: Int): Bitmap? {
        return try {
            val bounds = BitmapFactory.Options()
            bounds.inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, resId, bounds)
            val largest = maxOf(bounds.outWidth, bounds.outHeight)
            var sample = 1
            while (largest > 0 && largest / sample > TARGET_SPRITE_PX) {
                sample *= 2
            }
            val options = BitmapFactory.Options()
            options.inSampleSize = sample
            BitmapFactory.decodeResource(resources, resId, options)
        } catch (e: Exception) {
            null
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        listener = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - downX
                val dy = event.y - downY
                if (abs(dx) >= swipeThreshold && abs(dx) > abs(dy)) {
                    listener?.onBoardLane(if (dx > 0f) 1 else -1)
                } else {
                    performClick()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        listener?.onBoardHop()
        return true
    }

    companion object {
        private const val TARGET_SPRITE_PX = 320
    }
}
