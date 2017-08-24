package xyz.dokup.bitmapsample

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import okhttp3.*
import java.io.IOException
import java.io.InputStream

/**
 * Created by e10dokup on 2017/08/23.
 */
class SuperImageLoader private constructor(private val context: Context) {

    private var url: String? = null

    companion object {
        fun with(context: Context): SuperImageLoader {
            return SuperImageLoader(context)
        }
    }

    fun load(url: String): SuperImageLoader {
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
                // 画像サイズ取得
                val imageOptions = BitmapFactory.Options()
                imageOptions.inJustDecodeBounds = true
                BitmapFactory.decodeStream(body.byteStream(), null, imageOptions)
                val bitmap = getScaledBitmap(target, body.byteStream())
                (context as Activity).runOnUiThread {
//                    adjustImageViewSize(target, imageOptions)
                    target.scaleType = ImageView.ScaleType.FIT_CENTER
                    target.setImageBitmap(bitmap)
                }
            }
        })
    }

    private fun getAspectRetio(target: ImageView, imageOptions: BitmapFactory.Options): Float {

        "%s, %s".format(imageOptions.outHeight.toString(), imageOptions.outWidth.toString())

        // viewの横幅を取得
        val viewWidth = target.width

        // アスペクト比を取得
        return imageOptions.outHeight.toFloat() / imageOptions.outWidth.toFloat()
    }

    private fun adjustImageViewSize(target: ImageView, aspectRatio: Float) {
        // viewのサイズをアスペクト比に合わせる
        target.minimumHeight = (target.width * aspectRatio).toInt()
    }

    private fun getScaledBitmap(target: ImageView, stream: InputStream): Bitmap {
        return BitmapFactory.decodeStream(stream)
    }

}