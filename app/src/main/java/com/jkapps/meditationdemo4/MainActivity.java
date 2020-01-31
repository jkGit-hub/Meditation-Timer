package com.jkapps.meditationdemo4;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AskTimeDialog.AskTimeDialogListener {

    TextView tv_pb;
    ProgressBar progressBar;
    TextView textView;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button buttonStartStop;

    public CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private String meditationTime;

    public void checkButton(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        //Toast.makeText(this, radioButton.getText() + "을(를) 선택하셨습니다.", Toast.LENGTH_SHORT).show();
        textView.setVisibility(TextView.INVISIBLE);
        switch (radioButton.getId()) {
            case R.id.rb_10min:
                textView.setText("10");
                break;
            case R.id.rb_20min:
                textView.setText("20");
                break;
            case R.id.rb_30min:
                textView.setText("30");
                break;
            case R.id.rb_free:
                openDialog();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.pb_circle);
        radioGroup = findViewById(R.id.rg_options);
        textView = findViewById(R.id.tv_countDownTimer);
        tv_pb = findViewById(R.id.tv_pb);

        buttonStartStop = findViewById(R.id.btn_startStop);
        buttonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                meditationTime = textView.getText().toString();
                if (meditationTime.length() == 0) {
                    Toast.makeText(MainActivity.this, "시간을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    openDialog();
                    return;
                }
                mTimeLeftInMillis = Long.parseLong(meditationTime) * 60000;
                if (mTimeLeftInMillis == 0) {
                    Toast.makeText(MainActivity.this, "1분 이상의 시간을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    openDialog();
                    return;
                }

                playSound();

                if (mTimerRunning) {
                    stopTimer();
                } else {
                    startTimer();
                }
            }
        });
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                startProgressBar();
            }

            @Override
            public void onFinish() {
                playSoundBell();
                mTimerRunning = false;
                textView.setVisibility(TextView.VISIBLE);
                textView.setText("Great Job!");
            }
        }.start();

        mTimerRunning = true;
        updateUI();
    }
    // ERROR: emulator stop ==> (1)Stop 버튼 눌렀을 때 (2)onFinish 후 다시 Start 버튼 눌렀을 때
    private void stopTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateCountDownText();
        updateUI();
    }


    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        }
        textView.setText(timeLeftFormatted);
    }

    private void openDialog() {
        AskTimeDialog askTimeDialog = new AskTimeDialog();
        askTimeDialog.show(getSupportFragmentManager(), "Ask Time Dialog");
    }

    @Override
    public void applyText(String ownTime) {
        textView.setText(ownTime);
    }

    private void playSound() {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        MediaPlayer mp;
        switch (radioButton.getId()) {
            case R.id.rb_10min:
                mp = MediaPlayer.create(getApplicationContext(), R.raw.meditation10m);
                mp.start();
                break;
            case R.id.rb_20min:
                mp = MediaPlayer.create(getApplicationContext(), R.raw.meditation20m);
                mp.start();
                break;
            case R.id.rb_30min:
                mp = MediaPlayer.create(getApplicationContext(), R.raw.meditation30m);
                mp.start();
                break;
        }
    }

    private void playSoundBell() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
        mp.start();
    }

    private void updateUI() {
        if (mTimerRunning) {
            textView.setVisibility(TextView.VISIBLE);
            buttonStartStop.setText("STOP");
        } else {
            textView.setVisibility(TextView.INVISIBLE);
            buttonStartStop.setText("START");
        }
    }

    private void startProgressBar() {
        int mTimeSec = Integer.parseInt(meditationTime) * 60;
        int timeLeftSec = (int) (long) (mTimeLeftInMillis / 1000);
        float timePastSec = (float)(mTimeSec - timeLeftSec) / mTimeSec;
        int pp = (int)(float)((timePastSec) * 100);
        progressBar.setProgress(pp);
        tv_pb.setText(pp + "%");
    }
}