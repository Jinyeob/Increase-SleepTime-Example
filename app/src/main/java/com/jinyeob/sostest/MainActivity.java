package com.jinyeob.sostest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static TextView textView;
    static TextView textView2;

    Button button;
    Button button2;

    static int workValue = 0;
    boolean running = true;
    static int current = 0;
    static int[] period = {1, 5, 5, 10, 10, 60, 60, 120, 120};
    Handler mainThreadHandler;
    static String formatDate = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textview);
        textView2 = findViewById(R.id.textView2);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);


        button.setOnClickListener(new View.OnClickListener() { //stop
            @Override
            public void onClick(View v) {
                running = false;
                workValue = 0;
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { //start
            @Override
            public void onClick(View v) {
                // 현재시간을 msec 으로 구한다.
                long now = System.currentTimeMillis();
                // 현재시간을 date 변수에 저장한다.
                Date date = new Date(now);
                // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
                // nowDate 변수에 값을 저장한다.
                formatDate += "시작 -> " + sdfNow.format(date) + "\n";

                textView2.setText(formatDate);

                textView.setText(period[current] + "분");
                running = true;
                startCount();
            }
        });
        mainThreadHandler = new MyHandler(this);

    }

    private void startCount() {
        // Start a child thread when button is clicked.
        WorkerThread thread = new WorkerThread();
        thread.start();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                if (msg.what == 1) {
                    textView.setText(period[current] + "분");

                    // 현재시간을 msec 으로 구한다.
                    long now = System.currentTimeMillis();
                    // 현재시간을 date 변수에 저장한다.
                    Date date = new Date(now);
                    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
                    // nowDate 변수에 값을 저장한다.
                    formatDate += "~ " + period[current - 1] + "분 -> " + sdfNow.format(date) + "\n";

                    textView2.setText(formatDate);

                }
            }
        }
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (running) {
                workValue++;  // 작업스레드 값 증가
                try {
                    Thread.sleep(period[current] * 60 * 1000);
                    System.out.println("@@@@@@ thread. sleep: " + java.lang.Thread.currentThread().getName() + "@@@@@@");
                    ++current;
                    if (current == 9) {
                        current = 7;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Create a message in child thread.
                Message childThreadMessage = new Message();
                childThreadMessage.what = 1;
                childThreadMessage.arg1 = workValue;
                // Put the message in main thread message queue.
                mainThreadHandler.sendMessage(childThreadMessage);
            }

        }
    }


}
