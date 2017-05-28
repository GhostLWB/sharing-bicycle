package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by liwenbin on 2017/5/13 0013.
 */
public class ActivityHelpContent extends Activity {
    private ImageButton back;
    private TextView content;
    private TextView content_title;
    private Vector<String> help_content;
    private Vector<String> help_contentQ;
    private Vector<String> help_contentT;
    private int type=-1;
    private int typeQ=-1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help_content);
        back=(ImageButton)findViewById(R.id.help_content_back_button);
        content=(TextView)findViewById(R.id.help_content);
        content_title=(TextView)findViewById(R.id.help_content_type) ;
        context=this;
        help_content=new Vector<>();
        help_contentQ=new Vector<>();
        help_contentT=new Vector<>();

        Bundle contentBundle=this.getIntent().getExtras();
        type=contentBundle.getInt("HelpContent");
        typeQ=contentBundle.getInt("HelpContentQ");

        help_content.add(HelpDefine.REGISTER_S);
        help_content.add(HelpDefine.LOGIN_S);
        help_content.add(HelpDefine.PREORDER_S);
        help_content.add(HelpDefine.USE_S);
        help_content.add(HelpDefine.RETURN_S);
        help_content.add(HelpDefine.REPORT_S);
        help_content.add(HelpDefine.ANALASYS_S);
        help_contentQ.add(HelpDefine.QONE_S);
        help_contentQ.add(HelpDefine.QTWO_S);

        help_contentT.add(HelpDefine.REGISTER_T);
        help_contentT.add(HelpDefine.LOGIN_T);
        help_contentT.add(HelpDefine.PREORDER_T);
        help_contentT.add(HelpDefine.USE_T);
        help_contentT.add(HelpDefine.RETURN_T);
        help_contentT.add(HelpDefine.REPORT_T);
        help_contentT.add(HelpDefine.ANALASYS_T);
        help_contentT.add(HelpDefine.QONE_T);
        help_contentT.add(HelpDefine.QTWO_T);

        if (type>0){
            content.setText(help_content.get(type-1));
            content_title.setText(help_contentT.get(type-1));
        }
        if (typeQ>0){
            content.setText(help_contentQ.get(typeQ-1));
            Log.d("Content","type is :"+(typeQ-1));
            Log.d("Content","title is :"+(typeQ-1)+help_content.size()+1);
            content_title.setText(help_contentT.get(((typeQ-1)+help_content.size())));
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,HelpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(context,HelpActivity.class);
        startActivity(intent);
        finish();
    }
}
