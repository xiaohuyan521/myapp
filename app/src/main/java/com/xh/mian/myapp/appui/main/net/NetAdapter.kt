package com.xh.mian.myapp.appui.main.net

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity

class NetAdapter : BaseAdapter() {
    private var listdata: List<NetBean> = mutableListOf()
    fun updata(list: List<NetBean>) {
        listdata = list
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return listdata!!.size
    }

    override fun getItem(position: Int): Any? {
        return listdata!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val itemView: View = LayoutInflater.from(MainActivity.mContex).inflate(R.layout.net_view_item, null)
        var bean:NetBean = listdata.get(position)
        var author = itemView.findViewById<View>(R.id.author) as TextView
        var date = itemView.findViewById<View>(R.id.date) as TextView
        var title = itemView.findViewById<View>(R.id.title) as TextView
        var desc = itemView.findViewById<View>(R.id.desc) as TextView
        var thumbnail = itemView.findViewById<View>(R.id.thumbnail) as ImageView
        var chapter = itemView.findViewById<View>(R.id.chapter) as TextView

        author.text = bean.author
        date.text = bean.date
        title.text = bean.title
        desc.text = bean.desc
        chapter.text = bean.chapter
        thumbnail.setImageResource(R.mipmap.ic_launcher)
        if(null!=bean.thumbnail && !"".equals(bean.thumbnail)){
            Glide.with(MainActivity.mContex).load(bean.thumbnail).into(thumbnail);
        }

        itemView.setOnClickListener {
            val intent = Intent(MainActivity.mContex, WebActivity::class.java)
            intent.putExtra("url", bean.url);
            intent.putExtra("title", bean.title);
            MainActivity.mContex?.startActivity(intent)
            MainActivity.mContex?.overridePendingTransition(
                    R.anim.global_in, R.anim.global_out);
        }
        return itemView
    }


}
