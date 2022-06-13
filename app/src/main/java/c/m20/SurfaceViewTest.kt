package c.m20

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.SurfaceView
import java.lang.Runnable
import java.lang.Thread
import android.view.SurfaceHolder
import kotlin.jvm.Volatile
import java.lang.InterruptedException

@Suppress("DEPRECATION")
class MainActivity : Activity() {
    var renderView: FastRenderView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        renderView = FastRenderView(this)
        setContentView(renderView)
    }

    override fun onResume() {
        super.onResume()
        renderView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        renderView!!.pause()
    }

    inner class FastRenderView(context: Context?) : SurfaceView(context), Runnable {
        var renderThread: Thread? = null
        var surfaceHolder: SurfaceHolder

        @Volatile
        var running = false

        init {
            surfaceHolder = getHolder()
        }

        fun resume() {
            running = true
            renderThread = Thread(this)
            Log.d("DEBUG", "Starting thread at resume()")
            renderThread!!.start()
        }

        override fun run() {
            while (running) {
                if (!surfaceHolder.surface.isValid) continue
                val canvas = surfaceHolder.lockCanvas()
                canvas.drawRGB(255, 0, 0)
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }

        fun pause() {
            running = false
            while (true) {
                try {
                    renderThread!!.join()
                    break
                } catch (e: InterruptedException) {
                    // retry
                }
            }
        }
    }
}