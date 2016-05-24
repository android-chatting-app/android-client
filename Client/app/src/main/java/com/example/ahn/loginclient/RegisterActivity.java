package com.example.ahn.loginclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnSubmit = (Button)findViewById(R.id.btnSumbit);

        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText editID = (EditText)findViewById(R.id.editID);
                EditText editPW = (EditText)findViewById(R.id.editPW);
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
                                    .url("http://10.0.2.2:8080/LoginWebServer/GetData?act=register&id="+id+"&pw="+pw)
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
                                    Intent intentmain = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intentmain);
                                } else
                                    Toast.makeText(getApplicationContext(), "중복된 ID 입니다.", Toast.LENGTH_SHORT).show();
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
