package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
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
import com.github.rahatarmanahmed.cpv.CircularProgressView;


import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liwenbin on 2017/4/3 0003.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText input_account;
    private EditText input_password;
    private String account;
    private String password;
    private ImageButton login_button;
    private ImageButton backButton;
    // private ImageView mask;
    private Context context;
    private CircularProgressView circularProgressView;
    private Thread updateThread;
    private ImageView scrollBackground;
    private Animation scrollBackgroundAnimation;
    private Button registerButton;
    private User user;
    private Handler Loginhandler;
    double[] userWalletAndCredit = {0.0, 0.0};
    private String userAccountRegister=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        input_account = (EditText) findViewById(R.id.register_input_account);
        input_password = (EditText) findViewById(R.id.register_input_password);
        login_button = (ImageButton) findViewById(R.id.login_Button);
        backButton = (ImageButton) findViewById(R.id.login_back);
        context = this;
        // mask=(ImageView)findViewById(R.id.mask);
        scrollBackground = (ImageView) findViewById(R.id.login_scroll_background);
        circularProgressView = (CircularProgressView) findViewById(R.id.progress_view);
        registerButton = (Button) findViewById(R.id.regisertButton);

        scrollBackgroundAnimation = new TranslateAnimation(-1500, 0, 0, 0);
        circularProgressView.setVisibility(View.INVISIBLE);
        //mask.setVisibility(View.INVISIBLE);

        scrollBackgroundAnimation.setDuration(25000);               //设置每一次动画的持续时间
        scrollBackgroundAnimation.setRepeatCount(Animation.INFINITE);               //设置重复次数
        scrollBackgroundAnimation.setRepeatMode(Animation.REVERSE);    //反方向执行
        scrollBackground.setAnimation(scrollBackgroundAnimation);             //设置动画效果
        scrollBackgroundAnimation.startNow();                      //启动动画
        Log.d("LoginActivity", "user's balance get from userWalletAndCredit at first is" + userWalletAndCredit[0]);


        /**
         * 接收来自registeractivity的传参
         */
        Intent loginIntent=getIntent();
        Bundle loginBundle=loginIntent.getExtras();
        if (loginBundle!=null){
            userAccountRegister=loginBundle.getString("register");
            input_account.setText(userAccountRegister);
        }
        /**
         * 点击登录的时候触发，向服务器发送请求
         */
        login_button.setOnClickListener(this);
        backButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        //定义handler
        Loginhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        String result =(String)msg.obj;
                        net.sf.json.JSONObject js = net.sf.json.JSONObject.fromObject(result);
                        /**
                         * 可以通过相应的key对返回的JSON对象进行解析，获取对象中封装的值
                         */
                        boolean flag = js.getBoolean("flag");
                        if (flag == true) {

                            /**
                             * 在这里是验证通过之后的代码
                             */
                            circularProgressView.setVisibility(View.INVISIBLE);
                            account = input_account.getText().toString();
                            password = input_password.getText().toString();

                            Intent intent = new Intent(context, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("account", account);
                            bundle.putString("password", password);
                            intent.putExtras(bundle);

                            /**
                             * 生成用户对象
                             */
                            user=User.getUser();
                            user.setAccount(account);
                            userWalletAndCredit[0]=0.0;
                            userWalletAndCredit[1]=0.0;
                            Log.d("LoginActivity","1.user's balance get from userWalletAndCredit is"+userWalletAndCredit[0]);

                            Toast.makeText(context, "欢迎您,"+user.getAccount(), Toast.LENGTH_SHORT).show();
                            /**
                             * 页面跳转
                             */
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(context, "用户名或者密码错误或账号不存在！", Toast.LENGTH_SHORT).show();
                            circularProgressView.setVisibility(View.INVISIBLE);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }

    /**
     * 向服务器查询获取用户的余额和积分
     *
     * @param id
     * @return
     */
    private double[] queryUserWalletAndCredit(String id) {
        final String userid = id;
        final double[] result = new double[2];
        String url = "http://123.206.80.243:8080/sharing_bicycle/wallet_credit.do";
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                net.sf.json.JSONObject js = net.sf.json.JSONObject.fromObject(s);
                result[0] = Double.parseDouble(js.getString("balance"));
                result[1] = Double.parseDouble(js.getString("credit"));
                Log.d("LoginActivity", "user's balance is" + result[0]);
                Log.d("LoginActivity", "user's credit is" + result[1]);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", userid);
                return map;
            }
        };
        mQueue.add(stringRequest);
        return result;
    }

    private void startAnimationThreadStuff(long delay) {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        // Start animation after a delay so there's no missed frames while the app loads up
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!circularProgressView.isIndeterminate()) {
                    circularProgressView.setProgress(0f);
                    // Run thread to update progress every quarter second until full
                    updateThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (circularProgressView.getProgress() < circularProgressView.getMaxProgress() && !Thread.interrupted()) {
                                // Must set progress in UI thread
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        circularProgressView.setProgress(circularProgressView.getProgress() + 10);
                                    }
                                });
                                SystemClock.sleep(250);
                            }
                        }
                    });
                    updateThread.start();
                }
                // Alias for resetAnimation, it's all the same
                circularProgressView.startAnimation();
            }
        }, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scrollBackgroundAnimation.cancel();
        ReleaseImageViewUtils.releaseImage(scrollBackground);
        ImageViewUtils.releaseImageViewResouce(scrollBackground);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_back:
                Intent back_intent=new Intent(context,MainActivity.class);
                startActivity(back_intent);
                finish();
                break;
            case R.id.regisertButton:
                account=input_account.getText().toString();
                Intent intent=new Intent(context,RegisterActivity.class);
                Log.d("LoginActivity","input text is "+account);
                if ((account!=null)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("account_from_login", account);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
                break;
            case R.id.login_Button:
                if (input_account.length()==11) {
                    circularProgressView.setVisibility(View.VISIBLE);
                    //mask.setVisibility(View.VISIBLE);

                    startAnimationThreadStuff(0);

                    circularProgressView.setIndeterminate(true);
                    String url = "http://123.206.80.243:8080/sharing_bicycle/user.do";
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("id", input_account.getText().toString()));
                    params.add(new BasicNameValuePair("password", input_password.getText().toString()));
                    NetUtils.postRequest(url,params,Loginhandler);
                }else
                {
                    Toast.makeText(context,"账户格式错误！",Toast.LENGTH_SHORT).show();
                }


                break;

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(context,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
