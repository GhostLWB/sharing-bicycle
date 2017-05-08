package com.project.liwenbin.sharing_bicycle;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Vector;
import com.baidu.mapapi.model.LatLng;
/**
 * Created by 宋羽珩 on 2017/5/3.
 * Bicycle_data 用于进行车辆信息String 的字符串解析
 */

public class Bicycle_data {
    private static Vector<Bicycle> bike_vector;
    public  static Vector<Bicycle> get_data(String bike_data)
    {

        bike_vector = new Vector<Bicycle>();
        JSONArray json = JSONArray.fromObject(bike_data);//解析JSON 数据
        for(int i=0;i<json.size();i++)
        {
            JSONObject js=(JSONObject)json.get(i);
            String gps = js.getString("GPS");
            String[] gps_split=gps.split(",");
            LatLng location=new LatLng(Double.parseDouble(gps_split[1]),Double.parseDouble(gps_split[0]));
            Bicycle bicycle = new Bicycle(js.getInt("bike_id"),location,js.getBoolean("in_use"),js.getBoolean("break_down"),null,js.getBoolean("in_order"),js.getBoolean("in_lock"));
            bike_vector.add(bicycle);//将Bicycle添加到bike_vector中

        }
        return bike_vector;
    }

}
