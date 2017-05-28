package com.project.liwenbin.sharing_bicycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by liwenbin on 2017/5/11 0011.
 */
public class HelpActivity extends Activity {
    private ImageButton backButton;
    private ListView tutorialList;
    private ListView questionList;
    private ImageView helpBg;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);
        backButton=(ImageButton)findViewById(R.id.help_content_back_button);
        tutorialList=(ListView)findViewById(R.id.help_tutorial_list);
        questionList=(ListView)findViewById(R.id.help_question_list);
        //helpBg=(ImageView)findViewById(R.id.imageView13);
        //helpBg.setImageBitmap(ImageUtils.decodeSampledBitmapFromResource(getResources(),R.drawable.buttum_repo,display.getWidth()+160,display.getHeight()));
        context=this;

        tutorialList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,getTutorialTitle()));
        questionList.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getQuestionTitle()));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mybackintent=new Intent(context,MainActivity.class);
                startActivity(mybackintent);
                finish();

            }
        });
        tutorialList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle helpBundle=new Bundle();
                Intent helpIntent=new Intent(context,ActivityHelpContent.class);
                helpBundle.putInt("HelpContent",position+1);
                helpIntent.putExtras(helpBundle);
                startActivity(helpIntent);
                finish();
            }
        });
        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle helpBundle=new Bundle();
                Intent helpIntent=new Intent(context,ActivityHelpContent.class);
                helpBundle.putInt("HelpContentQ",position+1);
                helpIntent.putExtras(helpBundle);
                startActivity(helpIntent);
                finish();
            }
        });
    }

    private List<String> getTutorialTitle(){
        List<String> title=new ArrayList<>();
        title.add(getString(R.string.help_register));//注册教程
        title.add(getString(R.string.help_login));//登陆教程
        title.add(getString(R.string.help_preorder));//预约教程
        title.add(getString(R.string.help_use));//用车教程
        title.add(getString(R.string.help_return));//还车教程
        title.add(getString(R.string.help_repore));//车辆报修教程
        title.add(getString(R.string.help_analysis));//查询行程分析教程

        return title;
    }
    private List<String> getQuestionTitle(){
        List<String> questions=new ArrayList<>();
        questions.add("预约了却不用会有什么后果？");
        questions.add("如果用车时间没满一分钟，是怎么扣费的？");

        return questions;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(context,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
//        ImageViewUtils.releaseImageViewResouce(helpBg);
//        ReleaseImageViewUtils.releaseImage(helpBg);
        super.onDestroy();
    }
}
