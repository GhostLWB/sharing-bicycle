package com.project.liwenbin.sharing_bicycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 宋羽珩 on 2017/5/16.
 */
public class SearchAdapter extends BaseAdapter {

    private List<HashMap<String,String>> addressData;
    private LayoutInflater layoutInflater;
    private List<Tip> list;
    public SearchAdapter(Context context,List<HashMap<String,String>> addressData,List<Tip> list) {
        layoutInflater=LayoutInflater.from(context);
        this.addressData=addressData;
        this.list = list;
    }

    @Override
    public int getCount() {
        return addressData.size();
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
        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_item, null);
            vh.title = (TextView) convertView.findViewById(R.id.textView2);
            vh.text = (TextView) convertView.findViewById(R.id.textView3);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.title.setText(addressData.get(position).get("name"));
        vh.text.setText(addressData.get(position).get("address"));

        return convertView;
    }

    private final class ViewHolder{
        private TextView title;
        private TextView text;

        public ViewHolder() {
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getText() {
            return text;
        }

        public void setText(TextView text) {
            this.text = text;
        }
    }
}
