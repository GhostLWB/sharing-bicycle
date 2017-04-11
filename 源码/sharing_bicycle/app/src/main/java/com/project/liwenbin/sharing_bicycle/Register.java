package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by liwenbin on 2017/4/3 0003.
 */
public class Register extends Activity {
    private EditText input_account;
    private EditText input_password;
    private EditText confirm_password;
    private ImageButton regist;
    private String account;
    private String password1;
    private String password2;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        context=this;
        input_account=(EditText)findViewById(R.id.register_input_account);
        input_password=(EditText)findViewById(R.id.register_input_password);
        confirm_password=(EditText)findViewById(R.id.register_confirm_password);
        regist=(ImageButton)findViewById(R.id.regist_Button);

        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=input_account.getText().toString();
                password1=input_password.getText().toString();
                password2=confirm_password.getText().toString();
                if(password1==password2){
                    //do something
                }else{
                    Toast prompt=Toast.makeText(context,"密码输入不一致！",Toast.LENGTH_SHORT);
                    prompt.setGravity(Gravity.CENTER,100,200);
                    prompt.show();
                    input_password.setText("");
                    confirm_password.setText("");
                }
            }
        });
    }
}
