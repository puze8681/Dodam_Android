package kr.puze.dodam.Data

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kr.puze.dodam.Utils.LoadImages

class StudySelectData(var image: String, var title: String, var subTitle: String){
    fun setimg() : Any{
        val image = LoadImages(image)
        image.start()
        image.join()
        val d = image.getimg()
        val data : Drawable = BitmapDrawable(d)
        return data
    }
}