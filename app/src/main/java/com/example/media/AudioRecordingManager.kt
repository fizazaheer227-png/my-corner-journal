package com.example.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class AudioRecordingManager {
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    var isRecording: Boolean = false
        private set

    fun startRecording(context: Context): Result<String> {
        return try {
            val audioDir = File(context.filesDir, "my_corner_media/audio").apply {
                if (!exists()) mkdirs()
            }
            val outputFile = File(audioDir, "voice_${System.currentTimeMillis()}.m4a")
            currentOutputFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            isRecording = true
            Result.success(outputFile.absolutePath)
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Failed to start recording", e)
            releaseRecorder()
            Result.failure(e)
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) return null
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            currentOutputFile?.absolutePath
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Failed to stop recording cleanly", e)
            releaseRecorder()
            null
        }
    }

    fun cancelRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
            }
        } catch (e: Exception) {
            // Ignore error on cancel
        } finally {
            releaseRecorder()
            currentOutputFile?.let {
                if (it.exists()) it.delete()
            }
            currentOutputFile = null
        }
    }

    private fun releaseRecorder() {
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            // Ignore
        }
        mediaRecorder = null
        isRecording = false
    }
}
