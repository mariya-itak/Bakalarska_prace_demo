package com.tutlane.bakalarka;

import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

import kotlin.NotImplementedError;

import static com.tutlane.bakalarka.StartPage.getResId;

public class MovesSettings extends Fragment implements RobotLifecycleCallbacks {
    public String moveId;
    private Animate animate;
    MediaPlayer player  = null;

    public MovesSettings(String moveId){
        this.moveId = moveId;
    }
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.move_settings, container, false);

        ImageView moveImage = view.findViewById(R.id.move_image);
        moveImage.setImageResource(getResId(moveId, R.drawable.class));
        QiSDK.register(getActivity(), this);

        SeekBar intensSeekbar = view.findViewById(R.id.intens_seekbar);
        SeekBar smoothSeekbar = view.findViewById(R.id.smooth_seekbar);
        SeekBar repeatSeekbar = view.findViewById(R.id.rep_seekbar);

        setCounter(intensSeekbar, view.findViewById(R.id.intens_counter));
        setCounter(smoothSeekbar, view.findViewById(R.id.smooth_counter));
        setCounter(repeatSeekbar, view.findViewById(R.id.rep_counter));

        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] moveParam = new int[]{intensSeekbar.getProgress(), smoothSeekbar.getProgress(), repeatSeekbar.getProgress()};
                ((NavigationHost) getActivity()).navigateTo(new StartPage(moveId, moveParam), true);
            }
        });
        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new MovesList(), true);
            }
        });
        return view;
    }

    private void writeToTheFile(String data, String fileName){
        ContextWrapper cw = new ContextWrapper(this.getActivity().getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("path", file.toString());
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void setCounter(SeekBar sb, TextView tv){
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String seekbarValue = String.valueOf(progress);
                tv.setText(seekbarValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //throw new NotImplementedError();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //throw new NotImplementedError();
            }
        });
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Animation animation = AnimationBuilder.with(qiContext)
                .withResources(getResId(moveId, R.raw.class))
                .build();
        animate = AnimateBuilder.with(qiContext)
                .withAnimation(animation)
                .build();
        Future<Void> animateFuture = animate.async().run();
        String TAG = "";
        animate.addOnStartedListener(() -> Log.i(TAG, "Animation started."));
        animate.addOnLabelReachedListener((label, time) -> {
            player = MediaPlayer.create(getContext(), R.raw.gagnam);
            player.start();
        });
        animateFuture.thenConsume(future -> {
            if (future.isSuccess()) {
                player.stop();
                Log.i(TAG, "Animation finished with success.");
            } else if (future.hasError()) {
                Log.e(TAG, "Animation finished with error.", future.getError());
            }
        });
    }

    @Override
    public void onRobotFocusLost() {
        if (animate != null) {
            animate.removeAllOnStartedListeners();
        }
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        throw new NotImplementedError();
    }
}
