package com.example.apitestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiActivity extends AppCompatActivity {

    private Retrofit retrofit;

    private Button[] buttons = new Button[3];

    private ListView listview;
    private List<String> list = new ArrayList<>();

    //private final String BASE_URL = "http://ymmh.iptime.org";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        buttons[0] = findViewById(R.id.getButton);
        buttons[1] = findViewById(R.id.postButton);
        buttons[2] = findViewById(R.id.deleteButton);

        listview = findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        init();


        // API 인터페이스 생성
        final RetrofitHttp api_server = retrofit.create(RetrofitHttp.class);

        final Call<Void> delete_call = api_server.delete_user(18);

        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> tmp_call = delete_call.clone();//call은 한번만 호출 가능하기 때문에 재호출하려면 clone으로 만들어야 함.
                tmp_call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(ApiActivity.this, "정보 제거 성공", Toast.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ApiActivity.this, "정보 제거 실패", Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });

        final Call<UserLocation> post_call = api_server.create_user("new_yms5", 12.7, 12.8);
        buttons[1].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Call<UserLocation> tmp_call = post_call.clone();
                tmp_call.enqueue(new Callback<UserLocation>() {
                    @Override
                    public void onResponse(Call<UserLocation> call, Response<UserLocation> response) {
                        Toast.makeText(ApiActivity.this, "정보 추가 성공", Toast.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onFailure(Call<UserLocation> call, Throwable t) {
                        Toast.makeText(ApiActivity.this, "정보 추가 실패", Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });


        //GET
        // 인터페이스에 구현한 메소드인 contributors에 param 값을 넘기는 요청 만들기
        final Call<List<UserLocation>> get_call = api_server.user_locations("v1");

        buttons[0].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Call<List<UserLocation>> tmp_call = get_call.clone();
                list.clear();

                // 앞서만든 요청을 수행
                tmp_call.enqueue(new Callback<List<UserLocation>>() {
                    @Override
                    // 성공시
                    public void onResponse(Call<List<UserLocation>> call, Response<List<UserLocation>> response) {
                        Toast.makeText(ApiActivity.this, "정보받아오기 성공", Toast.LENGTH_LONG).show();
                        List<UserLocation> contributors = response.body();

                        // 받아온 리스트 순회
                        for (UserLocation contributor : contributors) {
                            list.add(contributor.name);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    // 실패시
                    public void onFailure(Call<List<UserLocation>> call, Throwable t) {
                        Toast.makeText(ApiActivity.this, "정보받아오기 실패", Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
    }

    public void init() {

        String url = getString(R.string.base_url);

        // Create REST adaptor using GSON converter
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}