package com.xh.mian.myapp.appitem.home.c;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.xh.mian.myapp.R;
import com.xh.mian.myapp.tools.db.SharedPreferences;
import com.xh.mian.myapp.tools.uitl.FileUtil;
import com.xh.mian.myapp.tools.view.SurfaceViewTemplate;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by HXL on 16/8/11.
 */
public class ListActivity extends Activity {
    private ListView listView;
    private List<File> list = new ArrayList<>();
    private FileListAdapter adapter;
    private SurfaceViewTemplate surface;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        surface = (SurfaceViewTemplate) findViewById(R.id.tr_line);
        listView = (ListView) findViewById(R.id.listView);
        if ("pcm".equals(getIntent().getStringExtra("type"))) {
            list = FileUtil.getPcmFiles();
        } else {
            list = FileUtil.getWavFiles();
        }

        String str = SharedPreferences.getString("wave");
        List<Integer> ls = StringToList(str);
        surface.setWavs(ls);

        adapter = new FileListAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                try {
                    if (null == parent.getAdapter())
                        return;
                    if (1 > parent.getAdapter().getCount())
                        return;
                    File f = list.get(position);
                    String patch = f.getPath();
                    setPlayer(patch);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPlayer(String filepath){

        File file = new File(filepath);
        if(file.exists()){
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(filepath);//设置播放的数据源。

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();//准备开始播放 播放的逻辑是c代码在新的线程里面执行。
                //mediaPlayer.prepareAsync();

                int duration2 = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int position = mediaPlayer.getCurrentPosition();//获取当前播放的位置
                //mediaPlayer.seekTo(seekBar.getProgress());//在当前位置播放
                //PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
                //playbackParams.setSpeed(speed);
                //mediaPlayer.setPlaybackParams(playbackParams);

                mediaPlayer.start();
                surface.setStart();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        surface.setPause();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"文件不存在，请检查文件的路径",Toast.LENGTH_SHORT).show();
        }
    }

    //设置倍速
    private boolean setPlaySpeed(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PlaybackParams params = mediaPlayer.getPlaybackParams();
                params.setSpeed(speed);
                mediaPlayer.setPlaybackParams(params);
                return true;
            } catch (Exception e) {
                //Log.e(TAG, "setPlaySpeed: ", e);
                return false;
            }
        }
        return false;
    }
    private List<Integer> StringToList(String listText) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        listText = listText.replaceAll("\\[","");
        listText = listText.replaceAll("\\]","");
        listText = listText.replaceAll("\\s*", "");
        List<Integer> list = new LinkedList<>();
        String[] text = listText.split("\\,");
        for (String str : text) {
            try {
                list.add(Integer.parseInt(str));
            }catch (Exception e){
                e.getMessage();
                list.add(0);
            }
        }
        return list;
    }
    public static class FileListAdapter extends BaseAdapter {
        Context context;
        List<File> list;

        public FileListAdapter(Context context, List<File> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_file_list, null);
                convertView.setTag(viewHolder);
                viewHolder.name = (TextView) convertView.findViewById(R.id.adapter_file_list_name);
                viewHolder.size = (TextView) convertView.findViewById(R.id.adapter_file_list_create_size);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(list.get(position).getName());
            viewHolder.size.setText(FormetFileSize(list.get(position).length()));

            return convertView;
        }

        public String FormetFileSize(long fileS) {// 转换文件大小
            DecimalFormat df = new DecimalFormat("#.00");
            String fileSizeString = "";
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + "K";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "M";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "G";
            }
            return fileSizeString;
        }

        static class ViewHolder {
            TextView name;
            TextView size;
        }
    }

}
