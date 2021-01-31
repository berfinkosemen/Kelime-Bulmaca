package com.example.kelimebulmaca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LevelTamamlandi extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_tamamlandi);

        String time, wrong, right;
        TextView scoreTV = (TextView) findViewById(R.id.puanBitis);
        TextView timeTV = (TextView) findViewById(R.id.zamanBitis);
        TextView rightTV = (TextView) findViewById(R.id.dogruCevaplarBitis);
        TextView wrongTV = (TextView) findViewById(R.id.yanlisCevaplarBitis);

        Intent intent = getIntent();

        final String levelStr, sublevelStr;
        final String score = intent.getStringExtra("score");
        scoreTV.setText(getText(R.string.puan_bitis).toString() + score);

        time = intent.getStringExtra("time");
        timeTV.setText(getText(R.string.zaman_bitis).toString() + time);

        right = intent.getStringExtra("numberOfRights");
        rightTV.setText(getText(R.string.dogru_cevaplar).toString() + right);

        wrong = intent.getStringExtra("numberOfWrongs");
        wrongTV.setText(getText(R.string.yanlis_cevaplar).toString() + wrong);

        levelStr = intent.getStringExtra("level");
        sublevelStr = intent.getStringExtra("sublevel");

        final int levelInt = Integer.parseInt(levelStr);
        final int sublevelInt = Integer.parseInt(sublevelStr);

        Button returnMainMenu = (Button) findViewById(R.id.anaMenuyeDonBtn);
        returnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelTamamlandi.this, MainActivity.class);

                final String username = getIntent().getStringExtra("username");
                intent.putExtra("score", score);
                intent.putExtra("username", username);

                boolean flagIsExist = false;
                String filepath = "high_scores.txt";

                try {
                    FileOutputStream fileout = openFileOutput("high_scores.txt", MODE_APPEND);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

                    outputWriter.write(score + "\t" + username + "\t" + (levelStr + sublevelStr) + "\n");
                    outputWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                BufferedReader reader = null;
                Context mContext = LevelTamamlandi.this;
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

                for (int k = 0; k < completedLevels.length; k++) {
                    if (completedLevels[k].equals(levelInt + "\t" + sublevelInt) == true) {
                        flagIsExist = true;
                        break;
                    }
                }

                if (flagIsExist == false) {
                    try {
                        FileOutputStream fileout = openFileOutput("gecilenleveller.txt", MODE_APPEND);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        outputWriter.write(levelInt + "\t" + sublevelInt + "\n");
                        outputWriter.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LevelTamamlandi.this);

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
}
