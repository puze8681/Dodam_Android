package kr.puze.dodam.Data

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kr.puze.dodam.Utils.LoadImages

class DebateThemeData(var blue: String, var red: String, var id: String, var deadline: String, var image: String){
    fun setimg() : Any{
        var image = LoadImages(image)
        image.start()
        image.join()
        var d = image.getimg()
        val data : Drawable = BitmapDrawable(d)
        return data!!
    }
}