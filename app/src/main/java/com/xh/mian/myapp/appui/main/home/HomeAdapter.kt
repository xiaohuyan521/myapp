package com.xh.mian.myapp.appui.main.home

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.xh.mian.myapp.MyApplication
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity
import com.xh.mian.myapp.tools.db.DbHelper
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoader
import java.net.URL


class HomeAdapter : RecyclerView.Adapter<ViewHolder>(){

    private val TYPE_ITEM = 1 // 普通布局
    private val TYPE_FOOTER = 2// 脚布局
    private val TYPE_HEAD = 3// 头部

    // 当前加载状态，默认为加载完成
    private var loadState = 2
    val LOADING = 1// 正在加载
    val LOADING_COMPLETE = 2// 加载完成
    val LOADING_END = 3// 加载到底
    var mDatas:List<HomeBean> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        // 最后一个item设置为FooterView
        return if (0 == position) {
            TYPE_HEAD
        }else if (position + 1 == itemCount) {
            TYPE_FOOTER
        } else {
            TYPE_ITEM
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_HEAD) {
            val layout: View = LayoutInflater.from(parent.context).inflate(R.layout.banner_view, parent, false)
            return HeadViewHolder(layout)
        }
        if (viewType == TYPE_FOOTER) {
            val layout: View = LayoutInflater.from(parent.context).inflate(R.layout.adpter_footer, parent, false)
            return FootViewHolder(layout)
        }
        val layout: View = LayoutInflater.from(parent.context).inflate(R.layout.fragment_home_item, parent, false)
        return ListHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FootViewHolder) {
            val footViewHolder = holder
            when (loadState) {
                LOADING -> {
                    footViewHolder.pbLoading!!.visibility = View.VISIBLE
                    footViewHolder.tvLoading!!.visibility = View.VISIBLE
                    footViewHolder.llEnd!!.visibility = View.GONE
                }
                LOADING_COMPLETE -> {
                    footViewHolder.pbLoading!!.visibility = View.INVISIBLE
                    footViewHolder.tvLoading!!.visibility = View.INVISIBLE
                    footViewHolder.llEnd!!.visibility = View.GONE
                }
                LOADING_END -> {
                    footViewHolder.pbLoading!!.visibility = View.GONE
                    footViewHolder.tvLoading!!.visibility = View.GONE
                    footViewHolder.llEnd!!.visibility = View.VISIBLE
                }
            }
        }
        if (holder is ListHolder) {
            try {
                var vaule = mDatas.get(position - 1)
                holder.title.text = vaule.title
                holder.desc.text = vaule.item
                holder.date.text = vaule.time
                holder.thumbnail.setImageResource(R.mipmap.ic_launcher)
                if(null!=vaule.image && !"".equals(vaule.image)){
                    Glide.with(MainActivity.mContex).load(vaule.image).into(holder.thumbnail);
                }
                var describe = vaule.describe
                var title = vaule.title
                holder.itemView.setOnClickListener {
                    try {
                        var clazz: Class<*>? = vaule.obj
                        val intent = Intent(MainActivity.mContex, clazz)
                        if(null!=describe){
                            intent.putExtra("describe", describe);
                        }
                        if(null!=title){
                            intent.putExtra("title", title);
                        }
                        MainActivity.mContex?.startActivity(intent)
                        MainActivity.mContex?.overridePendingTransition(
                                R.anim.global_in, R.anim.global_out);
                    }catch (e:Exception){
                        Toast.makeText(MainActivity.mContex, "暂未开放", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e: Exception){
                e.message
            }

        }
        if (holder is HeadViewHolder) {
            initBanner(holder.banner!!)
        }

    }

    override fun getItemCount(): Int {
        return mDatas!!.size+2
    }
    inner class ListHolder(itemView: View) : ViewHolder(itemView) {
        lateinit var thumbnail: ImageView
        lateinit var title: TextView
        lateinit var desc: TextView
        lateinit var date: TextView
        init {
            try {
                thumbnail = itemView.findViewById<View>(R.id.thumbnail) as ImageView
                title = itemView.findViewById<View>(R.id.title) as TextView
                desc = itemView.findViewById<View>(R.id.desc) as TextView
                date = itemView.findViewById<View>(R.id.date) as TextView
            }catch (e: Exception){
                e.message
            }
        }
    }

    inner class HeadViewHolder(itemView: View) : ViewHolder(itemView) {
        var banner:Banner?= null
        init{
            banner = itemView.findViewById<View>(R.id.banner) as Banner
        }
    }
    inner class FootViewHolder(itemView: View) : ViewHolder(itemView) {
        var pbLoading: ProgressBar? = null
        var tvLoading: TextView? = null
        var llEnd: LinearLayout? = null
        init {
            try {
                pbLoading = itemView.findViewById<View>(R.id.pb_loading) as ProgressBar
                tvLoading = itemView.findViewById<View>(R.id.tv_loading) as TextView
                llEnd = itemView.findViewById<View>(R.id.ll_end) as LinearLayout
            }catch (e: Exception){
                e.message
            }
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    fun setLoadState(loadState: Int) {
        this.loadState = loadState
        notifyDataSetChanged()
    }
    fun updata(mList: List<HomeBean>) {
        this.mDatas = mList
        notifyDataSetChanged()
    }


    private fun initBanner(banner: Banner){
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
        //设置图片加载器
        banner.setImageLoader(MyLoader())
        //设置图片集合
        var images:List<String> = arrayListOf()
        var db = DbHelper(MyApplication.instance)
        var list = db.getListByTableName("bannerur", "", null, null)
        for(data in list){
            var str = data.get("url").toString()
            images+=str
        }
        banner.setImages(images)
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.DepthPage)
        //设置标题集合（当banner样式有显示title时）
        var imageTitle = arrayListOf("title1", "title2", "title3")
        banner.setBannerTitles(imageTitle)
        //设置自动轮播，默认为true
        banner.isAutoPlay(true)
        //设置轮播时间
        banner.setDelayTime(20000)
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER)
        //banner设置方法全部调用完毕时最后调用
        banner.setOnBannerListener(
                fun(position: Int) {
                    Log.i("position=", "" + position);
                }
        )
        banner.start()
    }
    private class MyLoader : ImageLoader() {
        override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
            if (context != null && imageView != null) {
                Glide.with(context).load(path as String?).into(imageView)
            }
        }
    }
}


