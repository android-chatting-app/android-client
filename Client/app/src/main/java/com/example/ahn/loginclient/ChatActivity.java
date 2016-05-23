package com.example.ahn.loginclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    ListView m_ListView;
    CustomAdapter m_Adapter;
    String id = null;
    int clientIdx = -1;
    boolean critical = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        Button send = (Button)findViewById(R.id.button2);

        // 커스텀 어댑터 생성
        m_Adapter = new CustomAdapter();

        // Xml에서 추가한 ListView 연결
        m_ListView = (ListView) findViewById(R.id.listView1);

        // ListView에 어댑터 연결
        m_ListView.setAdapter(m_Adapter);

        //m_Adapter.add(id+" 님이 입장하셨습니다.",2);

        lock();
        try {
            clientIdx = new AsyncTask<Integer, Void, Integer>() {
                @Override
                protected Integer doInBackground(Integer... params) {
                    int idx = params[0];
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://180.65.243.142:8080/LoginWebServer/GetData?act=syn&idx=-1").build();
                    try {
                        Response response = client.newCall(request).execute();
                        idx = Integer.parseInt(response.body().string());
                        return idx;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return 0;
                }
            }.execute(-1).get();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        unlock();


        Thread thread = new Thread(new Runnable() {
            OkHttpClient client = new OkHttpClient();
            int numOfNewChat=0;
            @Override
            public void run() {
                while(true) {
                    SystemClock.sleep(2000);
                    lock();
                    Request request = new Request.Builder().url("http://180.65.243.142:8080/LoginWebServer/GetData?act=syn&idx=" + clientIdx).build();
                    try {
                        Response response = client.newCall(request).execute();
                        numOfNewChat = Integer.parseInt(response.body().string());

                        for (int i = 0; i < numOfNewChat; i++) {
                            String chat = response.body().string();
                            Bundle data = new Bundle();
                            data.putString("data", chat);
                            Message msg = Message.obtain();
                            msg.setData(data);
                            handler.sendMessage(msg);
                            clientIdx++;
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    unlock();
                }
            }
            private Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    String hchat = msg.getData().getString("data");
                    refresh(hchat, 0);
                }
            };
        });
        thread.start();






        send.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.editText1);
                String inputValue = editText.getText().toString();
                editText.setText("");
                refresh(inputValue, 1);

                inputValue = id + ": " + inputValue;
                lock();
                clientIdx++;
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        String chat = params[0];
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url("http://180.65.243.142:8080/LoginWebServer/GetData?act=send&chat=" + chat).build();
                        try {
                            client.newCall(request).execute();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }
                }.execute(inputValue);
                unlock();
            }
        });

    }

    private void refresh (String inputValue, int _str) {
        m_Adapter.add(inputValue, _str) ;
        m_Adapter.notifyDataSetChanged();
    }

    private void lock(){
        while(critical){}critical=true;
    }
    private void unlock(){
        critical=false;
    }

}