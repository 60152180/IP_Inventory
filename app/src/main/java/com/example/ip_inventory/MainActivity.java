package com.example.ip_inventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 재고 입력
        Button button1=(Button)findViewById(R.id.btn1);
        button1.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        Intent intent=new Intent(getApplicationContext(),manage.class);
                        startActivity(intent);
                    }
                }
        );

        Button button2=(Button)findViewById(R.id.btn2);
        button2.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        Intent intent=new Intent(getApplicationContext(),state.class);
                        startActivity(intent);
                    }
                }
        );

        Button button3=(Button)findViewById(R.id.btn3);
        button3.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        Intent intent=new Intent(getApplicationContext(),ViewActivity.class);
                        startActivity(intent);
                    }
                }
        );

        Button button4=(Button)findViewById(R.id.btn4);
        button4.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View view){
                        Intent intent=new Intent(getApplicationContext(),setting.class);
                        startActivity(intent);
                    }
                }
        );

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                Log.e("Token",deviceToken);
                HashMap<String, String> requestedParams = new HashMap<>();
                requestedParams.put("token", deviceToken);
                PostRequestHandler postRequestHandler = new PostRequestHandler("http://54.180.116.239/register.php", requestedParams);
                postRequestHandler.execute();
            }
        });




    }
}
