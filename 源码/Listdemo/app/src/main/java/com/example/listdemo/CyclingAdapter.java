package com.example.listdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 宋羽珩 on 2017/5/15.
 */
public class CyclingAdapter extends BaseAdapter {
    private List<Cycling_record> data;
    private int layout;
    private Context context;
    public CyclingAdapter(List<Cycling_record> data, int layout, Context context) {
        this.data = data;
        this.layout = layout;
        this.context = context;
    }

    public void refresh(List<Cycling_record> d){
        data=d;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjectClass objectClass=null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            objectClass=new ObjectClass();
            objectClass.setTextView1((TextView)convertView.findViewById(R.id.text1));
            objectClass.setTextView2((TextView)convertView.findViewById(R.id.text2));
            objectClass.setTextView3((TextView)convertView.findViewById(R.id.text3));
            convertView.setTag(objectClass);
        }else {
            objectClass=(ObjectClass)convertView.getTag();
        }
        objectClass.textView1.setText(data.get(position).getDate_time());
        objectClass.textView2.setText("自行车编号："+data.get(position).getBike_id());
        objectClass.textView3.setText(" 骑行时间："+Integer.toString(data.get(position).getRiding_time())+" 分钟");
        return convertView;
    }

    private final class  ObjectClass{
        public TextView textView1=null;
        public TextView textView2=null;
        public TextView textView3=null;

        public ObjectClass() {
        }

        public void setTextView1(TextView textView1) {
            this.textView1 = textView1;
        }

        public void setTextView2(TextView textView2) {
            this.textView2 = textView2;
        }

        public void setTextView3(TextView textView3) {
            this.textView3 = textView3;
        }
    }
}
