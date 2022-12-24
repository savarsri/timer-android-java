package com.example.stopwatch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.stopwatch.Notification.CHANNEL_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView totalTime, timeRemainingTextView;
    private Button startButton, stopButton, stopButton1;
    private EditText hourText, minuteText, secondText;
    private ProgressBar pbTimer;
    private String hourString, minuteSting, secondString;
    private int hourValue, minuteValue, secondValue;
    private long timeRemaining;
    private CountDownTimer countDownTimer, timeRemainingCounter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout timerUpRelLayout;
    public static MediaPlayer player;
    public static NotificationManagerCompat notificationManagerCompat;
    private long timeLeftInMillis;
    private boolean onFinish=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Timer");

        onFinish=false;

        notificationManagerCompat = NotificationManagerCompat.from(this);

        timeRemaining = ((hourValue + minuteValue + secondValue));

        timerUpRelLayout = findViewById(R.id.timerUpRelLayout);
        timeRemainingTextView = findViewById(R.id.timeRemainingTextView);

        totalTime = findViewById(R.id.totalTime);

        stopButton1 = findViewById(R.id.stopButton1);
        stopButton1.setOnClickListener(this);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        hourText = findViewById(R.id.hourText);
        minuteText = findViewById(R.id.minuteText);
        secondText = findViewById(R.id.secondText);
        pbTimer = findViewById(R.id.pbTimer);

        hourText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 24)});
        minuteText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 60)});
        secondText.setFilters(new InputFilter[]{new InputFilterMinMax(1, 60)});

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startButton:

                hourString = hourText.getText().toString();
                minuteSting = minuteText.getText().toString();
                secondString = secondText.getText().toString();

                if (!"".equals(hourString)) {
                    hourValue = (Integer.parseInt(hourString)) * 60 * 60;
                }
                if (!"".equals(minuteSting)) {
                    minuteValue = Integer.parseInt(minuteSting) * 60;
                }
                if (!"".equals(secondString)) {
                    secondValue = Integer.parseInt(secondString);
                }

                pbTimer.setMax(hourValue + minuteValue + secondValue);
                pbTimer.setProgress(hourValue + minuteValue + secondValue);

                timeLeftInMillis = ((hourValue + minuteValue + secondValue) + 1) * 1000;

                if (secondValue != 0) {
                    if (minuteValue != 0) {
                        if (hourValue != 0) {
                            totalTime.setText(("Total " + hourString + " Hour " + minuteSting + " minute " + secondString + " seconds").toString());
                            totalTime.setVisibility(View.VISIBLE);
                        }
                        if (hourValue == 0) {
                            totalTime.setText(("Total " + minuteSting + " minute " + secondString + " seconds").toString());
                            totalTime.setVisibility(View.VISIBLE);
                        }

                    }
                    if (minuteValue == 0) {
                        if (hourValue != 0) {
                            totalTime.setText(("Total " + hourString + " Hour " + secondString + " seconds").toString());
                            totalTime.setVisibility(View.VISIBLE);
                        }
                        if (hourValue == 0) {
                            totalTime.setText(("Total " + secondString + " seconds").toString());
                            totalTime.setVisibility(View.VISIBLE);
                        }

                    }

                } else if (secondValue == 0) {
                    if (secondValue == 0) {
                        if (hourValue == 0) {
                            if (minuteValue != 0) {
                                totalTime.setText(("Total " + minuteSting + " minute").toString());
                                totalTime.setVisibility(View.VISIBLE);
                            }
                            if (minuteValue == 0) {
                                totalTime.setVisibility(View.INVISIBLE);
                            }

                        }
                        if (hourValue != 0 && minuteValue != 0) {
                            totalTime.setText(("Total " + hourString + " hour " + minuteSting + " minutes").toString());
                            totalTime.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    totalTime.setVisibility(View.INVISIBLE);
                }


                if ((hourValue + minuteValue + secondValue) != 0) {
                    countDownTimer = new CountDownTimer(((hourValue + minuteValue + secondValue)) * 1000, 1000) {
                        @Override
                        public void onTick(long l) {
                            pbTimer.incrementProgressBy(-1);
                        }

                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void onFinish() {
                            //Intent intent = new Intent(MainActivity.this, timerUp.class);
                            //startActivity(intent);

                            totalTime.setText("".toString());
                            pbTimer.setMax(100);
                            pbTimer.setProgress(100);
                            timerUpRelLayout.setVisibility(View.VISIBLE);

                            player = MediaPlayer.create(MainActivity.this, R.raw.clock);
                            if (player.isPlaying()) {
                                player.stop();
                            } else {
                                player.setLooping(true);
                                player.start();
                            }

                            startButton.setVisibility(View.VISIBLE);
                            stopButton.setVisibility(View.INVISIBLE);

                            if (onFinish){
                                sendNotification();
                            }


                        }
                    }.start();


                    timeRemainingCounter = new CountDownTimer(timeLeftInMillis, 1000) {
                        @Override
                        public void onTick(long l) {

                            timeLeftInMillis = l;
                            updateCountDownText();
                        }

                        @Override
                        public void onFinish() {

                        }
                    }.start();


                    startButton.setVisibility(View.INVISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                } else {
                    pbTimer.setMax(100);
                    pbTimer.setProgress(100);
                    totalTime.setText(("Enter time").toString());
                    totalTime.setVisibility(View.VISIBLE);
                }


                hourValue = 0;
                minuteValue = 0;
                secondValue = 0;

                hourText.setText("");
                minuteText.setText("");
                secondText.setText("");
                break;

            case R.id.stopButton1:

                timerUpRelLayout.setVisibility(View.INVISIBLE);

                player.stop();
                player.reset();

                break;

            case R.id.stopButton:
                countDownTimer.cancel();
                timeRemainingCounter.cancel();
                timeRemainingTextView.setText(("00:00:00").toString());
                pbTimer.setMax(100);
                pbTimer.setProgress(100);
                totalTime.setText("".toString());
                stopButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                break;
            default:

        }
    }

    public void sendNotification() {

        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Intent stopIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        android.app.Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText("Time's Up")
                .setSmallIcon(R.drawable.ic_timer)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .setColor(Color.YELLOW)
                .addAction(R.drawable.ic_timer,"Stop",actionIntent)
                .setAutoCancel(true)
                .build();

        notificationManagerCompat.notify(1, notification);

    }

    private void updateCountDownText() {

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeLeftInMillis),
                (TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeftInMillis))), (TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis))));


        timeRemainingTextView.setText(timeLeftFormatted);
    }


    @Override
    protected void onStart(){
        super.onStart();
        onFinish=false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        onFinish=false;
    }

    @Override
    protected void onPause(){
        super.onPause();
        onFinish=true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        onFinish=true;
    }

}

