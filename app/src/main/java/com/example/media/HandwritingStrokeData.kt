package com.example.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class HandwritingStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

object HandwritingStrokeHelper {

    fun serializeStrokes(strokes: List<HandwritingStroke>): String {
        if (strokes.isEmpty()) return ""
        val jsonArray = JSONArray()
        for (stroke in strokes) {
            if (stroke.points.isEmpty()) continue
            val obj = JSONObject()
            obj.put("color", stroke.color.toArgb())
            obj.put("width", stroke.strokeWidth.toDouble())
            obj.put("eraser", stroke.isEraser)

            val pointsStr = stroke.points.joinToString(";") { pt ->
                "${"%.1f".format(pt.x)},${"%.1f".format(pt.y)}"
            }
            obj.put("pts", pointsStr)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    fun deserializeStrokes(jsonStr: String?): List<HandwritingStroke> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        val strokes = mutableListOf<HandwritingStroke>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val colorArgb = obj.getInt("color")
                val width = obj.getDouble("width").toFloat()
                val isEraser = obj.optBoolean("eraser", false)
                val ptsStr = obj.optString("pts", "")

                if (ptsStr.isNotBlank()) {
                    val points = ptsStr.split(";").mapNotNull { coordStr ->
                        val parts = coordStr.split(",")
                        if (parts.size == 2) {
                            val x = parts[0].toFloatOrNull()
                            val y = parts[1].toFloatOrNull()
                            if (x != null && y != null) Offset(x, y) else null
                        } else null
                    }
                    if (points.isNotEmpty()) {
                        strokes.add(
                            HandwritingStroke(
                                points = points,
                                color = Color(colorArgb),
                                strokeWidth = width,
                                isEraser = isEraser
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strokes
    }

    fun saveHandwritingToImage(
        context: Context,
        strokes: List<HandwritingStroke>,
        width: Int,
        height: Int
    ): String? {
        if (strokes.isEmpty() || width <= 0 || height <= 0) return null
        return try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)

            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }

            for (stroke in strokes) {
                if (stroke.isEraser || stroke.points.size < 2) continue
                paint.color = stroke.color.toArgb()
                paint.strokeWidth = stroke.strokeWidth

                val path = AndroidPath()
                val first = stroke.points.first()
                path.moveTo(first.x, first.y)
                for (i in 1 until stroke.points.size) {
                    val pt = stroke.points[i]
                    path.lineTo(pt.x, pt.y)
                }
                canvas.drawPath(path, paint)
            }

            val dir = File(context.filesDir, "my_corner_media/handwriting").apply {
                if (!exists()) mkdirs()
            }
            val targetFile = File(dir, "hw_${System.currentTimeMillis()}.png")
            FileOutputStream(targetFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
