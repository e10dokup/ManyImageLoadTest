package xyz.dokup.bitmapsample

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import okhttp3.*
import java.io.IOException

/**
 * Created by e10dokup on 2017/08/23.
 */
class ImageLoader private constructor(private val context: Context) {

    private var url: String? = null

    companion object {
        fun with(context: Context): ImageLoader {
            return ImageLoader(context)
        }
    }

    fun load(url: String): ImageLoader {
        this.url = url
        return this
    }

    fun into(target: ImageView) {
        url ?: return
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()
                body ?: return
                val bitmap = BitmapFactory.decodeStream(body.byteStream())
                (context as Activity).runOnUiThread {
                    target.scaleType = ImageView.ScaleType.FIT_CENTER
                    target.setImageBitmap(bitmap)
                }
            }
        })
    }

}