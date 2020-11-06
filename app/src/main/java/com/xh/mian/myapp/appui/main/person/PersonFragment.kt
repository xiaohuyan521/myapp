package com.xh.mian.myapp.appui.main.person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.lowagie.text.pdf.PdfFileSpecification.url
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation


class PersonFragment : Fragment() {
    private lateinit var perViewModel: PersonViewModel
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        perViewModel = ViewModelProvider(this).get(PersonViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_person, container, false)

        var blurImageView: ImageView = root.findViewById(R.id.iv_blur);
        var avatarImageView: ImageView = root.findViewById(R.id.iv_avatar);
        //Glide.get(MainActivity.mContex).clearMemory()清除内存缓存
        //Glide.get(MainActivity.mContex).clearDiskCache()清除磁盘缓存
        Glide.with(MainActivity.mContex).load(R.drawable.personbg) //设置高斯模糊
            // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
            .apply(bitmapTransform(BlurTransformation(15, 1)))
            .into(blurImageView)

        Glide.with(MainActivity.mContex).load(R.drawable.person)
            .apply(bitmapTransform(CropCircleTransformation()))
            .into(avatarImageView)
        return root
    }
}