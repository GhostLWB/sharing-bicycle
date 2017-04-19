package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appdatasearch.GetRecentContextCall;


import net.sf.json.JSON;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwenbin on 2017/4/3 0003.
 */
public class LoginActivity extends Activity{
    private EditText input_account;
    private EditText input_password;
    private String account;
    private String password;
    private ImageButton login_button;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        input_account=(EditText)findViewById(R.id.register_input_account);
        input_password=(EditText)findViewById(R.id.register_input_password);
        login_button=(ImageButton)findViewById(R.id.regist_Button);
        context=this;

        /**
         * 这一段代码是用来在activity之间传递参数跳转的，暂时不用
         */
//        login_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                account=input_account.getText().toString();
//                password=input_password.getText().toString();
//                Intent intent=new Intent(context,MainActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("account",account);
//                bundle.putString("password",password);
//                intent.putExtras(bundle);
//
//                startActivity(intent);
//            }
//        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://123.206.80.243:8080/sharing_bicycle/user.do";
                RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        net.sf.json.JSONObject js = net.sf.json.JSONObject.fromObject(s);
                        boolean flag = js.getBoolean("flag");
                        if (flag == true) {
                            Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("id", input_account.getText().toString());
                        map.put("password", input_password.getText().toString());
                        return map;
                    }
                };
                mQueue.add(stringRequest);
            }

        });


    }
}
