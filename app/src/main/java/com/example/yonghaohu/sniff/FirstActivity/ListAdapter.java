package com.example.yonghaohu.sniff.FirstActivity;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yonghaohu.sniff.SecondActivity.Program;
import com.example.yonghaohu.sniff.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonghaohu on 15/10/18.
 */
public class ListAdapter extends BaseAdapter {
    List<Boolean> mChecked;
    List<Program> listprogram = new ArrayList<Program>();
    LayoutInflater la;
    Context context;

    public ListAdapter(List<Program> list ,Context context){
        this.listprogram = list;
        this.context = context;
        mChecked = new ArrayList<Boolean>();
        for(int i=0;i<list.size();i++){
            mChecked.add(false);
        }
    }

    @Override
    public int getCount() {
        return listprogram.size();
    }
    @Override
    public Object getItem(int position) {
        return listprogram.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder = null;

        if(convertView == null) {
            Log.e("MainActivity", "position1 = " + position);
            la = LayoutInflater.from(context);
            convertView=la.inflate(R.layout.list_item, null);

            holder = new ViewHolder();
            holder.selected = (CheckBox)convertView.findViewById(R.id.list_select);
            holder.image=(ImageView) convertView.findViewById(R.id.image);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            final int p = position;

            holder.selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox)v;
                    Log.d("set checkbox", "is "+cb.isChecked());
                    mChecked.set(p, cb.isChecked());
                }
            });

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final Program pr = (Program)listprogram.get(position);
        holder.selected.setChecked(mChecked.get(position));
        holder.image.setImageDrawable(pr.getIcon());
        holder.text.setText(pr.getName());

        return convertView;
    }
}
class ViewHolder{
    CheckBox selected;
    TextView text;
    ImageView image;
}
