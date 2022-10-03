package com.tutlane.bakalarka;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;

import java.io.File;
import java.io.FileInputStream;

import static com.tutlane.bakalarka.StartPage.getResId;

/*
    Trida je pro ovladani robota pomoci aplikace. Pomoci trid AnimationBuilder a AnimateBuilder se spusti pripravena animace na robotovi.
    Take pomoci tridy MediaPlayer spolu s tancem se prehraje zvolena uzivatelem hudba.
*/

public class RobotPepperActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private Animate animate;
    private String danceScript = "";
    MediaPlayer player;

    public RobotPepperActivity(){

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QiSDK.register(this, this);
    }
    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        danceScript = readFromFile("dance_script.txt");
        String selectedSong = readFromFile("selected_song.txt");
        Animation animation = AnimationBuilder.with(qiContext)
                .withTexts(danceScript)
                .build();
        animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation)
                .build();
        Future<Void> animateFuture = animate.async().run();
        String TAG = "";
        animate.addOnStartedListener(() -> Log.i(TAG, "Animation started."));
        animate.addOnLabelReachedListener((label, time) -> {
            player = MediaPlayer.create(getApplicationContext(), getResId(selectedSong.toLowerCase(), R.raw.class));
            player.start();
        });
        animateFuture.thenConsume(future -> {
            player.stop();
            if (future.isSuccess()) {
                Log.i(TAG, "Animation finished with success.");
            } else if (future.hasError()) {
                Log.e(TAG, "Animation finished with error.", future.getError());
            }
        });
        player.stop();
    }

    @Override
    public void onRobotFocusLost() {
        if (animate != null) {
            animate.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    private String readFromFile(String fileName){
        ContextWrapper cw = new ContextWrapper(this);
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);
        Log.d("path", file.toString());
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            return builder.toString();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
