package com.example.apitestapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements//AppCompatActivity가 FragmentActivity를 extends하고 있음
        fragment_1.OnFragmentInteractionListener,
        fragment_2.OnFragmentInteractionListener,
        fragment_3.OnFragmentInteractionListener{//AppCompatActivity {

    private TextView mTextMessage;
    BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);

                    // item = findViewById(R.id.navigation_home);



                    Fragment newFragment = new fragment_1();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_frame, newFragment, "frag1");
                    //transaction.addToBackStack(null);//뒤로 탐색시 재개됨
                    transaction.commit();

                    return true;
                case R.id.navigation_dashboard:
                    //mTextMessage.setText(R.string.title_dashboard);

                    Fragment newFragment2 = new fragment_2();
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.fragment_frame, newFragment2, "frag2");
                    //transaction2.addToBackStack(null);
                    transaction2.commit();

                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);

                    Fragment newFragment3 = new fragment_3();
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.fragment_frame, newFragment3, "frag3");
                    //transaction3.addToBackStack(null);
                    transaction3.commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        //mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




//처음 fragment가 교체되어도 남아있는 현상 debug 필요 -> 그냥 fragment를 코드로 생성

        //fragment 코드로 생성

        fragment_1 fragment1 = new fragment_1();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_frame, fragment1);
        transaction.commit();

        //툴바 생성
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }


    @Override
    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public void onFragmentInteraction2(Uri uri){

    }

    @Override
    public void onFragmentInteraction3(Uri uri){
    }

    //뒤로가기 처리
    @Override
    public void onBackPressed(){
        if(navView.getSelectedItemId() == R.id.navigation_home){
            super.onBackPressed();
        }
        else{
            navView.setSelectedItemId(R.id.navigation_home);

            //스택에 넣었다면 클리어 필요
        }
    }
}
