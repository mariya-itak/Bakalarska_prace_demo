package com.tutlane.bakalarka;

import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.NotImplementedError;

/*
    Je hlavni okno aplikace, obsahuje tlacitka (exemplare trid Button nebo MaterialButton) na volbu hudby, pohybu, prehravani hudby a ukazky vysledneho tance. 
*/

public class StartPage extends Fragment{
    String selectedSong = "";
    MediaPlayer player  = null;
    private String moveItemId;
    private int[] moveParam;
    private String danceScript = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Animation xmlns:editor=\"http://www.ald.softbankrobotics.com/animation/editor\" typeVersion=\"2.0\" editor:fps=\"40\">\n";

    public StartPage(String moveTimelineId, int[] param) {
        this.moveItemId = moveTimelineId;
        moveParam = param;
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_page, container, false);
        if(!moveItemId.equals("")){
            writeToTheFile(moveItemId + " "+ Arrays.toString(moveParam) + "\n", "dance.txt", true);
        }
        if(moveItemId.equals("begin")){
            ContextWrapper cw = new ContextWrapper(this.getActivity().getApplicationContext());
            File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(directory, "dance.txt");
            file.delete();
            file = new File(directory, "selected_song.txt");
            file.delete();
        } else {
            LinearLayout lo = view.findViewById(R.id.linear_layout_on_anim_timeline);
            String danceScript = readFromFile("dance.txt");
            if (danceScript != null) {
                String[] danceMoves = danceScript.split("\n");
                for (String danceMove : danceMoves) {
                    String[] moveAndParam = danceMove.split(" ");
                    int repeat = Integer.parseInt(moveAndParam[3].replaceAll("]", ""));
                    if(repeat > 0){
                        for(int i = 0; i < repeat; i++) {
                            ImageView iv = (ImageView) inflater.inflate(R.layout.image_view, container, false);
                            iv.setImageResource(StartPage.getResId(moveAndParam[0], R.drawable.class));
                            lo.addView(iv);
                        }
                    } else {
                        ImageView iv = (ImageView) inflater.inflate(R.layout.image_view, container, false);
                        iv.setImageResource(StartPage.getResId(moveAndParam[0], R.drawable.class));
                        lo.addView(iv);
                    }

                }
            }
        }
        Spinner spinner = view.findViewById(R.id.songs_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSong = spinner.getSelectedItem().toString();
                writeToTheFile(selectedSong, "selected_song.txt", false);
                if(!selectedSong.equals("")) {
                    player = MediaPlayer.create(getContext(), getResId(selectedSong.toLowerCase(), R.raw.class));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                throw new NotImplementedError();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.songs_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String selecSong = readFromFile("selected_song.txt");
        spinner.setSelection(adapter.getPosition(selecSong));
        Button movesButton = view.findViewById(R.id.open_moves_button);
        movesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new MovesList(), true);
            }
        });

        MaterialButton playMusicButton = view.findViewById(R.id.play_music_button);
        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                    player.setLooping(false);
                }
            }
        });
        view.findViewById(R.id.show_dance_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                /*
                    V tele teto metody se sestavuje vysledny tanec. Script s tancem pro robota je vytvoren pomoci jazyka XML. Zvolene uzivatelem pohyby se nejdriv uchovavaji ve formatu json, pak z toho se vytvari XML script. 
                */
                
                String[] danceMoves = readFromFile("dance.txt").split("\n");
                InputStream is = getResources().openRawResource(getResId("moves_parameters", R.raw.class));
                StringBuilder moveScript = new StringBuilder();
                int c;
                try{
                    while ((c = is.read()) != -1) {
                        moveScript.append((char) c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Gson gson = new Gson();
                Map<String, String> read = gson.fromJson(moveScript.toString(), type);
                type = new TypeToken<Map<String, String[]>>() {}.getType();
                Map<String, ArrayList<Float[]>> dance = new HashMap<>();
                dance.put("HeadYaw", new ArrayList<>());

                InputStream is1 = getResources().openRawResource(getResId("bpms", R.raw.class));
                StringBuilder bpms = new StringBuilder();
                int c1 = 0;
                try{
                    while ((c1 = is1.read()) != -1) {
                        bpms.append((char) c1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int bpm = 0;
                String[] bpmsArray = bpms.toString().split("\n");
                for(String bpmSong : bpmsArray){
                    String[] songAndBpm =  bpmSong.split(":");
                    if(songAndBpm[0].equals(selectedSong.toLowerCase())){
                        float bpmFloat = Float.parseFloat(songAndBpm[1].trim());
                        bpm = (int) bpmFloat;

                    }
                }
                float framePerBeat = 40/((float)bpm/60);
                int frame = Math.round(framePerBeat);
                int intensity = 0;
                int smoothness = 0;
                int repeating = 0;
                List<String> movSmooth = new ArrayList<>();
                for(String move : danceMoves) {
                    String[] moveAndPram = move.split(" ");
                    String moveSet = read.get(moveAndPram[0]);
                    intensity = Integer.parseInt(moveAndPram[1].replaceAll("\\[", "").replaceAll(",", ""));
                    smoothness = Integer.parseInt(moveAndPram[2].replaceAll(",", ""));
                    String interpolName = chooseSmothness(smoothness);
                    repeating = Integer.parseInt(moveAndPram[3].replaceAll("]", ""));
                    if(repeating == 0){
                        repeating++;
                    }
                    for (int j = 0; j < repeating; j++) {
                        movSmooth.add(interpolName);
                        Map<String, String[]> readMove = gson.fromJson(moveSet, type);
                        String[] bodyParts = readMove.get("bodyPartsName");
                        String[] bodyPartsParam = readMove.get("bodyPartsParam");
                        for (int i = 0; i < bodyParts.length; i++) {
                            if (dance.get(bodyParts[i]) == null) {
                                dance.put(bodyParts[i], new ArrayList<>());
                            }
                            dance.get(bodyParts[i]).add(new Float[]{(float) frame - intensity, Float.parseFloat(bodyPartsParam[i])});
                        }
                        frame += framePerBeat;
                    }
                }
                List<String> bodyPartsNames = new ArrayList<String>(dance.keySet());
                for(String bodyPart : bodyPartsNames){
                    if(bodyPart.equals("RHand") || bodyPart.equals("LHand")){
                        danceScript += "    <ActuatorCurve fps=\"40\" actuator=\"" + bodyPart + "\" mute=\"false\" unit=\"dimensionless\">\n";
                    } else {
                        danceScript += "    <ActuatorCurve fps=\"40\" actuator=\"" + bodyPart + "\" mute=\"false\" unit=\"degree\">\n";
                    }
                    List<Float[]> keys = dance.get(bodyPart);
                    for(int i = 0; i < keys.size(); i++){
                        Float[] moveKey = keys.get(i);
                        if(moveKey[1] == 0.0){
                            danceScript += "        <Key value=\"" + moveKey[1].intValue() + "\" frame=\"" + moveKey[0].intValue() + "\">\n";
                        } else {
                            danceScript += "        <Key value=\"" + moveKey[1] + "\" frame=\"" + moveKey[0].intValue() + "\">\n";
                        }
                        danceScript += "            <Tangent side=\"left\" abscissaParam=\"0.0\" ordinateParam=\"0.0\" editor:interpType=\"" + movSmooth.get(i) + "\"/>\n" +
                                "            <Tangent side=\"right\" abscissaParam=\"0\" ordinateParam=\"0\" editor:interpType=\"" + movSmooth.get(i) + "\"/>\n" +
                                "        </Key>\n";
                    }
                    danceScript += "    </ActuatorCurve>\n";
                }
                danceScript += "    <Labels fps=\"40\" editor:groupName=\"Labels\">\n" +
                        "        <Label frame=\"0\">play</Label>\n" +
                        "    </Labels>\n";
                danceScript += "</Animation>\n";
                writeToTheFile(danceScript, "dance_script.txt", false);
                Intent intent = new Intent(getContext(), RobotPepperActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private String chooseSmothness(int smoothness) {
        String interpName = "";
        if(smoothness >= 0 && smoothness <= 20){
            interpName = "linear";
        } else if(smoothness >= 21 && smoothness <= 40){
            interpName = "bezier";
        } else if(smoothness >= 41 && smoothness <= 60){
            interpName = "bezier_auto";
        } else if(smoothness >= 61 && smoothness <= 80){
            interpName = "bezier_symmetrical";
        } else {
            interpName = "bezier_smooth";
        }
        return interpName;
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void writeToTheFile(String data, String fileName, boolean append){
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
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    public String readFromFile(String fileName){
        ContextWrapper cw = new ContextWrapper(this.getContext());
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

