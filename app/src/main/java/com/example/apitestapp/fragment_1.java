package com.example.apitestapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;
//import android.support.v4.app.Fragment;
//import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fragment_1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_1 extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //---------------------------------------------
    //private Button button;
    AirclueService mService;
    boolean mBound = false;
    ImageView circle;
    Button core_bt;
    Button stop_bt;

    Timer location_timer;
    Timer elapsedtime_timer;
    //---------------------------------------------

    public fragment_1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_1.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_1 newInstance(String param1, String param2) {
        fragment_1 fragment = new fragment_1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d("fragment_1","onCreate()");
    }

    //onCreateView가 정상 수행되면 getView().findViewById로 view 참조가능
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("fragment_1","onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_1, container, false);

        //-----------------------------------------

        // Button listeners
        core_bt = view.findViewById(R.id.core_bt);
        core_bt.setOnClickListener(this);
        stop_bt = view.findViewById(R.id.right_bt);
        stop_bt.setOnClickListener(this);

        circle = view.findViewById(R.id.circle_img);

        return view;
    }

    //서비스 동작 체크
    public boolean isAirclueServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (AirclueService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public void startTimer(int type){
        Log.d("fragment_1", "startTimer()");

        //type : 1(location), 2(elapsed time)

        if(type == 1){
        //location timer start
            location_timer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    Location location;
                    String lat;
                    String lng;
                    String provider;
                    if(mBound){//서비스에 바운드되어 있으면
                        location = mService.getLocation();
                        lat = String.format("%.6f",location.getLatitude());
                        lng = String.format("%.6f",location.getLongitude());
                        provider = location.getProvider();
                    }
                    else{
                        lat = "";
                        lng = "";
                        provider = "";
                    }

                    final String string = "위도 "+lat + ",  경도 " + lng;
                    final TextView textView = getView().findViewById(R.id.position_text);

                    //+
                    final String string2 = provider;
                    final TextView textView2 = getView().findViewById(R.id.provider_text);
                    //+

                    //ui thread가 아닌 thread에서 ui변경을 위한 메소드 호출
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(string);
                            textView2.setText(string2);
                        }
                    });
                }
            };

            location_timer.schedule(tt, 1000, 5000);
        }
        else if(type == 2){
        //elapsedtime timer start
            elapsedtime_timer = new Timer();
            TimerTask tt2 = new TimerTask() {
                @Override
                public void run() {

                    final String string;

                    if(mBound) {
                        long cnt = mService.getElapsedTime();
                        string = GetTime(cnt);
                    }
                    else
                        string = "";

                    final TextView textView = getView().findViewById(R.id.time_text);

                    //ui thread가 아닌 thread에서 ui변경을 위한 메소드 호출
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(string);
                        }
                    });
                }
            };

            elapsedtime_timer.schedule(tt2, 1000, 1000);
        }

    }

    //Create timer here
    @Override
    public void onStart(){
        super.onStart();
        Log.d("fragment_1", "onStart");

        //타이머 시작
        if(isAirclueServiceRunning()){//서비스 동작 중
            Log.d("fragment_1", "AirclueService is running");

            //바운드
            Intent intent = new Intent(getActivity(), AirclueService.class);
            if(!mBound) {
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                mBound = true;
            }

            startTimer(1);
            startTimer(2);


            //애니메이션
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_1);
            circle.setAnimation(animation);
            circle.setVisibility(View.INVISIBLE);//안해주면 안보임
            circle.setVisibility(View.VISIBLE);

            //버튼 설정
            core_bt.setEnabled(false);
            core_bt.setVisibility(View.INVISIBLE);
            stop_bt.setVisibility(View.VISIBLE);
            stop_bt.setEnabled(true);
        }
        else{
            Log.d("fragment_1", "no AirclueService");
        }
    }

    public void stopTimer(int type){

        Log.d("fragment_1", "stopTimer()");

        //type : 1(location), 2(elapsed time)
        if(type == 1){
            if(location_timer != null) {
                location_timer.cancel();
                location_timer.purge();
                location_timer = null;
            }
        }
        else if(type ==2){
            if(elapsedtime_timer != null){
                elapsedtime_timer.cancel();
                elapsedtime_timer.purge();
                elapsedtime_timer = null;
            }
        }
    }

    //Cancel timer here
    @Override
    public void onStop(){
        super.onStop();

        Log.d("fragment_1", "onStop");

        //타이머 취소
        stopTimer(1);
        stopTimer(2);

        //unbind service
        if(mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("fragment_1","onAttach()");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d("fragment_1","onDetach()");
        super.onDetach();
        mListener = null;


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.core_bt) {

            getView().findViewById(R.id.core_bt).setEnabled(false);

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION )
                            != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions( getActivity(),
                        new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 0 );
            }

            //start service
            Intent intent = new Intent(getActivity(), AirclueService.class);
            intent.putExtra("command", 0);
            getActivity().startService(intent);

            if(!mBound)
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            //타이머
            if(location_timer == null){
                startTimer(1);
            }
            if(elapsedtime_timer == null){
                startTimer(2);
            }

            //애니메이션
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_1);
            circle.setAnimation(animation);
            circle.setVisibility(View.INVISIBLE);//안해주면 안보임
            circle.setVisibility(View.VISIBLE);

            getView().findViewById(R.id.core_bt).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.right_bt).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.right_bt).setEnabled(true);
        }
        else if (i == R.id.right_bt){

            getView().findViewById(R.id.right_bt).setEnabled(false);

            if(mBound) {
                Toast.makeText(getActivity(), "unbindService", Toast.LENGTH_SHORT).show();
                getActivity().unbindService(mConnection);
                mBound = false;
            }
            else
                Toast.makeText(getActivity(), "nothing", Toast.LENGTH_SHORT).show();

            //stop service
            Intent intent = new Intent(getActivity(), AirclueService.class);
            intent.putExtra("command", 1);
            getActivity().startService(intent);

            //애니메이션 종료
            circle.clearAnimation();

            //타이머 종료
            stopTimer(1);
            stopTimer(2);

            getView().findViewById(R.id.right_bt).setVisibility(View.INVISIBLE);
            getView().findViewById(R.id.core_bt).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.core_bt).setEnabled(true);
        }
    }

    //경과 시간 변환
    private String GetTime(long msec){

        final long sec_ms = 1000;
        final long minute_ms = sec_ms*60;
        final long hour_ms = minute_ms*60;
        final long day_ms = hour_ms*24;

        long day = msec / day_ms;
        msec -= day*day_ms;

        long hour = msec / hour_ms;
        msec -= hour*hour_ms;

        long minute = msec / minute_ms;
        msec -= minute*minute_ms;

        long sec = msec / sec_ms;

        String date_str = String.format("%02d일 %02d시간 %02d분 %02d초", day, hour, minute, sec);

        return date_str;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d("fragment_1","onServiceConnected()");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AirclueService.AirclueBinder binder = (AirclueService.AirclueBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("fragment_1","onServiceDisconnected()");
            mBound = false;
        }
    };
}
