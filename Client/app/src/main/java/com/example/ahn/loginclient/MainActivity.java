package com.example.ahn.loginclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        Button btnReg = (Button)findViewById(R.id.btnReg);

        btnReg.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View view){
                Intent intentReg = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentReg);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText editID = (EditText)findViewById(R.id.mainID);
                EditText editPW = (EditText)findViewById(R.id.mainPW);
                boolean formFilled = false;

                formFilled = formFilledCheck(editID,editPW);

                if(formFilled) {
                    String id = editID.getText().toString();
                    String pw = editPW.getText().toString();

                    new AsyncTask<String, Void, Response>() {
                        String id, pw;
                        OkHttpClient client = new OkHttpClient();

                        @Override
                        protected void onPreExecute() {
                        }

                        @Override
                        protected Response doInBackground(String... params) {
                            id = params[0];
                            pw = params[1];

                            Request request = new Request.Builder()
                                    .url("http://180.65.243.142:8080/LoginWebServer/GetData?act=login&id="+id+"&pw="+pw)
                                    .build();
                            try{
                                Response response = client.newCall(request).execute();
                                return response;
                            }catch(IOException ex){
                                ex.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Response response) {
                            try {
                                if (response.body().string().equals("success")){
                                    //채팅 테스트
                                    Intent intentChat = new Intent(MainActivity.this, ChatActivity.class);
                                    intentChat.putExtra("id",id);
                                    startActivity(intentChat);
                                } else
                                    Toast.makeText(getApplicationContext(), "ID or Password 가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            }catch (IOException ex){
                                ex.printStackTrace();
                            }
                        }
                    }.execute(id, pw);
                }
            }
        });
    }

    public boolean formFilledCheck(EditText editID, EditText editPW){
        if (editID.getText().toString().length() == 0) {
            editID.setError("ID 를 입력하세요");
            return false;
        } else if (editPW.getText().toString().length() == 0) {
            editID.setError(null);
            editPW.setError("Password 를 입력하세요");
            return false;
        } else {
            editPW.setError(null);
            editID.setError(null);
            return true;
        }
    }
}
