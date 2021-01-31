package com.example.kelimebulmaca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelSec extends AppCompatActivity {
    Button startGameBtn;
    String level = "";
    String subLevel = "";
    Spinner spinnerSubLevels;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_sec);

        startGameBtn = (Button) findViewById(R.id.oyunaBaslaBtn);
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelSec.this, Oyun.class);
                intent.putExtra("level", level);
                intent.putExtra("subLevel", subLevel);

                final String username = getIntent().getStringExtra("username");
                intent.putExtra("username", username);


                intent.putExtra("devamOyunuMu", "false");
                startActivity(intent);
                finish();
            }
        });

        Spinner spinnerLevels = (Spinner) findViewById(R.id.spinnerLevels);
        adapter = new CustomAdapter(this,
                android.R.layout.simple_spinner_item,
                populateTick("level", 0));
        spinnerLevels.setAdapter(adapter);

        spinnerSubLevels = (Spinner) findViewById(R.id.spinnerSubLevels);
        adapter = new CustomAdapter(this,
                android.R.layout.simple_spinner_item,
                populateTick("sublevel", 0));
        spinnerSubLevels.setAdapter(adapter);

        spinnerLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                level = text;
                int levelInt = 0;
                if (level.equals("Kolay - 3 Harf") == true) {
                    levelInt = 0;
                } else if (level.equals("Orta - 4 Harf") == true) {
                    levelInt = 1;
                } else if (level.equals("Zor - 5+ Harf") == true) {
                    levelInt = 2;
                }

                spinnerSubLevels = (Spinner) findViewById(R.id.spinnerSubLevels);
                adapter = new CustomAdapter(LevelSec.this, android.R.layout.simple_spinner_item,
                        populateTick("sublevel", Integer.parseInt(String.valueOf(levelInt))));
                spinnerSubLevels.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSubLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subLevel = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LevelSec.this);

        builder.setMessage(R.string.cikmak_istiyor_musunuz);
        builder.setCancelable(true);
        builder.setNegativeButton(R.string.evet_kapat, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton(R.string.hayır_kapatma, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private ArrayList<CompleteTick> populateTick(String type, int index) {
        final ArrayList<CompleteTick> tick = new ArrayList<CompleteTick>();

        BufferedReader reader = null;
        Context mContext = LevelSec.this;
        InputStreamReader is;
        String completedLevelsTxt = "";

        try {
            FileInputStream file = mContext.openFileInput("gecilenleveller.txt");
            is = new InputStreamReader(file);
            reader = new BufferedReader(is);
            String line = reader.readLine();
            completedLevelsTxt = completedLevelsTxt + line + "\n";
            while (line != null) {
                line = reader.readLine();
                completedLevelsTxt = completedLevelsTxt + line + "\n";
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] completedLevels = completedLevelsTxt.split("\n");

        List<List<Integer>> asamalar = new ArrayList<>();
        asamalar.add(new ArrayList<Integer>());
        asamalar.add(new ArrayList<Integer>());
        asamalar.add(new ArrayList<Integer>());

        Boolean[][] isSublevelCompleted = {{false, false, false, false, false, false},
                {false, false, false, false, false, false},
                {false, false, false, false, false, false}};
        Boolean[] isLevelCompleted = {false, false, false};

        int lvl = 0, sublvl = 0;


        for (int i = 0; i < completedLevels.length; i++) {
            if (completedLevels[i].equals("null") == false) {
                lvl = Integer.parseInt(Character.toString(completedLevels[i].charAt(0)));
                sublvl = Integer.parseInt(Character.toString(completedLevels[i].charAt(2)));

                isSublevelCompleted[lvl - 1][sublvl - 1] = true;
            }
        }

        boolean isLevelFinished = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                if (isSublevelCompleted[i][j] == false) {
                    isLevelFinished = false;
                }
            }

            if (isLevelFinished != false) {
                isLevelCompleted[i] = true;
            }

            isLevelFinished = true;
        }

        String[] levelNames = {"Kolay - 3 Harf", "Orta - 4 Harf", "Zor - 5+ Harf"};
        String[] subLevelNames = {"Level 1", "Level 2", "Level 3", "Level 4", "Level 5", "Level 6"};

        if (type.equals("level") == true) {
            for (int k = 0; k < 3; k++) {
                if (isLevelCompleted[k] == true) {
                    tick.add(new CompleteTick(levelNames[k], R.drawable.ic_yes));
                } else {
                    tick.add(new CompleteTick(levelNames[k], R.drawable.ic_no));
                }
            }
        } else {
            for (int k = 0; k < 6; k++) {
                if (isSublevelCompleted[index][k] == true) {
                    tick.add(new CompleteTick(subLevelNames[k], R.drawable.ic_yes));
                } else {
                    tick.add(new CompleteTick(subLevelNames[k], R.drawable.ic_no));
                }
            }
        }
        return tick;
    }
}
