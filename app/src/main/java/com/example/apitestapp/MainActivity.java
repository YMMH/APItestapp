package com.example.apitestapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private TextView[] textview = new TextView[4];

    //private final String BASE_URL = "http://ymmh.iptime.org";

    public void minsang(){
        Toast.makeText(MainActivity.this, "민상 메소드", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        minsang();
        init();

        // GitHub API 인터페이스 생성
        RetrofitHttp api_server = retrofit.create(RetrofitHttp.class);
        // 인터페이스에 구현한 메소드인 contributors에 param 값을 넘기는 요청 만들기
        Call<List<UserLocation>> call = api_server.user_locations("v1");

        // 앞서만든 요청을 수행
        call.enqueue(new Callback<List<UserLocation>>() {
            @Override
            // 성공시
            public void onResponse(Call<List<UserLocation>> call, Response<List<UserLocation>> response) {
                Toast.makeText(MainActivity.this, "정보받아오기 성공", Toast.LENGTH_LONG)
                        .show();
                List<UserLocation> contributors = response.body();
                // 받아온 리스트 순회
                int i = 0;
                for (UserLocation contributor : contributors) {

                    // 텍스트 뷰에 정보를 붙임
                    textview[i].append(contributor.name + " ");
                    textview[i].append(contributor.lat + " ");
                    textview[i].append(contributor.lng);
                    //Log.d("yms", "" + contributor.name);
                    //Log.d("yms", String.valueOf(i));
                    i++;
                }
            }

            @Override
            // 실패시
            public void onFailure(Call<List<UserLocation>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "정보받아오기 실패", Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    public void init() {
        textview[0] = (TextView) findViewById(R.id.textView);
        textview[1] = (TextView) findViewById(R.id.textView2);
        textview[2] = (TextView) findViewById(R.id.textView3);
        textview[3] = (TextView) findViewById(R.id.textView4);

        String url = getString(R.string.base_url);

        // Create REST adaptor using GSON converter
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}



