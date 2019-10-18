package com.example.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private final static long REPETITIVE_TASK_DELAY_IN_MILLIS = 1000;

    private TextView textViewStatus;

    private RepetitiveTask timeCounterRepetitiveTask;

    private long startTime;

    private DecimalFormat decimalFormat;


    private Button buttonStart;
    private Button buttonStop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTime = SystemClock.elapsedRealtime();
        textViewStatus = findViewById(R.id.txtTimer);
        timeCounterRepetitiveTask = createTimeCounterRepetitiveTask();
        decimalFormat = new DecimalFormat("0.000");



        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);

        startTime = SystemClock.elapsedRealtime();


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!timeCounterRepetitiveTask.isRunning()) {

                    Toast.makeText(MainActivity.this, ""+!timeCounterRepetitiveTask.isRunning(), Toast.LENGTH_SHORT).show();

                    timeCounterRepetitiveTask.start(true);
                }
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timeCounterRepetitiveTask.isRunning()) {
                    timeCounterRepetitiveTask.stop();

                }
            }
        });
    }

    private RepetitiveTask createTimeCounterRepetitiveTask() {
        return new RepetitiveTask(new Runnable() {
            @Override
            public void run() {
                final long elapsedTimeInMillis = (SystemClock.elapsedRealtime() - startTime);

                updateTextViewStatus(elapsedTimeInMillis);


            }
        }, REPETITIVE_TASK_DELAY_IN_MILLIS);
    }

    private void updateTextViewStatus(final long elapsedTimeInMillis) {
        final String formattedElapsedTime = decimalFormat.format(elapsedTimeInMillis / 1000.0);
        textViewStatus.setText(formattedElapsedTime);
    }


}
