package com.example.clienttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.io.OutputStream;
import java.net.Socket;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //定义界面上的两个文本框
    EditText input;
    TextView show;
    //定义界面上的一个按钮
    Button send;
    OutputStream os;
    Handler handler;
    //定义与服务器通信的子线程
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText)findViewById(R.id.et_main_content);
        send = (Button) findViewById(R.id.btn_main_send);
        show = (TextView) findViewById(R.id.tv_receive_content);
        Socket s;
        handler = new Handler()	{
            @Override
            public void handleMessage(Message msg){
                // 如果消息来自于子线程
                if (msg.what == 0x123){
                    // 将读取的内容追加显示在文本框中
                    show.append("\n" + msg.obj.toString());
                }
            }
        };
        try{
            s = new Socket("127.0.0.1", 40000);
            // 客户端启动ClientThread线程不断读取来自服务器的数据
            new Thread(new ClientThread(s, handler)).start(); // ①
            os = s.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }
        send.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    // 将用户在文本框内输入的内容写入网络
                    os.write((input.getText().toString() + "\r\n")
                            .getBytes("utf-8"));
                    // 清空input文本框
                    input.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
