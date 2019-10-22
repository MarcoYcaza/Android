package com.example.repetitivetask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    /*TODO Define how often the task must repeat*/
    private final static long REPETITIVE_TASK_DELAY_IN_MILLIS = 1000;

    private TextView textViewStatus;
    private RepetitiveTask newRepetitiveTask;

    /*TODO Buttons to start and stop the Task*/
    private Button buttonStart;
    private Button buttonStop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = findViewById(R.id.txtTimer);

        /*TODO Create the task, it will handle its own thread and runnables*/
        newRepetitiveTask = MyRepetitiveTask();


        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*TODO Ask first if its already running
                *  if thats the case then don't re-initiate*/

                if (!newRepetitiveTask.isRunning()) {

                    Toast.makeText(MainActivity.this, ""+!newRepetitiveTask.isRunning(), Toast.LENGTH_SHORT).show();

                    newRepetitiveTask.start(true);
                }
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {

            /*TODO if it is running then stop it.*/

            @Override
            public void onClick(View view) {
                if (newRepetitiveTask.isRunning()) {
                    newRepetitiveTask.stop();

                }
            }
        });
    }

    private RepetitiveTask MyRepetitiveTask() {

        return new RepetitiveTask(new Runnable() {
            int i=0;
            @Override
            public void run() {
                /*TODO
                * Here you write the code of the task
                * In this case its just about updating a
                * TextView with new Text
                * */
                updateTextViewStatus(i);

                i = i+1;
            }
        }, REPETITIVE_TASK_DELAY_IN_MILLIS);
    }

    private void updateTextViewStatus(int i) {
        textViewStatus.setText(""+i);
    }


}
