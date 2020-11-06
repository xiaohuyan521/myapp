package com.xh.mian.myapp.appui.main.project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.xh.mian.myapp.R
import com.xh.mian.myapp.appui.main.MainActivity

class ProAdapter(var mList: List<ProBean>) : RecyclerView.Adapter<ProAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout: View = LayoutInflater.from(parent.context).inflate(R.layout.pro_adapter_view, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var value = mList.get(position)
        holder.tv.text = value.title
        holder.subtitle.text = value.subtitle
        holder.itemView.setOnClickListener {
            try{
                var clazz: Class<*>? = value.obj
                val intent = Intent(MainActivity.mContex, clazz)
                MainActivity.mContex?.startActivity(intent)
                MainActivity.mContex?.overridePendingTransition(
                        R.anim.global_in, R.anim.global_out);
            }catch (e:Exception){
                Toast.makeText(MainActivity.mContex, "暂未开放", Toast.LENGTH_SHORT).show()
                e.message
            }

        }

    }

    override fun getItemCount(): Int {
        var len = mList.size
        return len
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv: TextView
        var subtitle: TextView
        init {
            tv = itemView.findViewById<View>(R.id.title) as TextView
            subtitle = itemView.findViewById<View>(R.id.subtitle) as TextView
        }
    }

}