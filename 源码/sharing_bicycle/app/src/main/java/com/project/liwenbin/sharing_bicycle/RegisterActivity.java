package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwenbin on 2017/4/3 0003.
 */
public class RegisterActivity extends Activity {
    private EditText input_account;
    private EditText input_password;
    private EditText confirm_password;
    private ImageButton regist;
    private String account;
    private String password1;
    private String password2;
    private Context context;
    private Animation scrollBackgroundAnimation;
    private ImageView scrollBackgroundImage;
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context=this;
        input_account=(EditText)findViewById(R.id.register_input_account);
        input_password=(EditText)findViewById(R.id.register_input_password);
        confirm_password=(EditText)findViewById(R.id.register_confirm_password);
        regist=(ImageButton)findViewById(R.id.regist_Button);
        scrollBackgroundImage=(ImageView)findViewById(R.id.register_scroll_background);
        backButton=(ImageButton)findViewById(R.id.register_back);

        scrollBackgroundAnimation=new TranslateAnimation(-1500,0,0,0);
        scrollBackgroundAnimation.setDuration(25000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation.setRepeatMode(Animation.REVERSE);    //反方向执行
        scrollBackgroundImage.setAnimation(scrollBackgroundAnimation);             //设置动画效果
        scrollBackgroundAnimation.startNow();                      //启动动画

        if (this.getIntent().getExtras()!=null){
            Bundle bundle=this.getIntent().getExtras();
            account=bundle.getString("account_from_login");
            Log.d("LoginActivity","extrac from bundle is"+account);
            input_account.setText(account);
        }

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=input_account.getText().toString();
                password1=input_password.getText().toString();
                password2=confirm_password.getText().toString();
                Log.d("register","password1 is"+password1);
                Log.d("register","password2 is"+password2);
                if(password1.equals(password2)){
                    //do something
                    String url = "http://123.206.80.243:8080/sharing_bicycle/register.do";
                    RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest =new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            net.sf.json.JSONObject js = net.sf.json.JSONObject.fromObject(s);
                            boolean flag =js.getBoolean("flag");
                            boolean hav_register=js.getBoolean("hav_register");
                            if((flag==true)&&(hav_register==false)) {
                                Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
                            }
                            else if (hav_register==true)
                            {
                                Toast.makeText(context,"已注册",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context,"fail",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(context,"连接失败",Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> map =new HashMap<String, String>();
                            map.put("id",account);
                            map.put("password",password1);
                            return  map;
                        }
                    };
                    mQueue.add(stringRequest);
                }else{
                    Toast prompt=Toast.makeText(context,"密码输入不一致！",Toast.LENGTH_SHORT);
                    prompt.setGravity(Gravity.CENTER,100,200);
                    prompt.show();
                    input_password.setText("");
                    confirm_password.setText("");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
