package com.example.kelimebulmaca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    static final int READ_BLOCK_SIZE = 100;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    Button continueBtn, highScoresBtn, resetGameBtn, completedLevels;
    String username = "";
    EditText usernameEdt;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String username = getIntent().getStringExtra("username");
        final String score = getIntent().getStringExtra("score");

        highScoresBtn = (Button) findViewById(R.id.yuksekPuanlarBtn);
        highScoresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Score.class);
                intent.putExtra("username", username);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });

        resetGameBtn = (Button) findViewById(R.id.oyunuSifirlaBtn);
        resetGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("Tüm verileri sıfırlamak istiyor musunuz");
                builder.setCancelable(true);
                builder.setNegativeButton("Sıfırla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                });

                builder.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        final String nameOfTheGame = getText(R.string.oyun_adi).toString();
        String[] nameOfTheGameParts = nameOfTheGame.split(" ");
        String part1 = nameOfTheGameParts[0];
        String part2 = nameOfTheGameParts[1];

        String[] nameOfTheGame1 = part1.split("");
        String[] nameOfTheGame2 = part2.split("");

        LinearLayout linearLayout = findViewById(R.id.oyunAdi);
        LinearLayout linearLayout2 = findViewById(R.id.oyunAdi2);

        linearLayout.removeAllViews();
        linearLayout2.removeAllViews();

        for (int i = 1; i < nameOfTheGame1.length; i++) {
            String key = nameOfTheGame1[i];
            addView((LinearLayout) findViewById(R.id.oyunAdi), key);
        }

        for (int i = 1; i < nameOfTheGame2.length; i++) {
            String key = nameOfTheGame2[i];
            addView((LinearLayout) findViewById(R.id.oyunAdi2), key);
        }

        continueBtn = (Button) findViewById(R.id.devamEtBtn);
        usernameEdt = (EditText) findViewById(R.id.adiniYaz);
        Button resumeBtn = (Button) findViewById(R.id.resumeGameBtn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelSec.class);
                MainActivity.this.username = usernameEdt.getText().toString();

                if (isNameEmpty() == false && isNameLonger() == false) {
                    intent.putExtra("username", MainActivity.this.username);
                    intent.putExtra("devamOyunuMu", "false");
                    startActivity(intent);
                    finish();
                }
            }
        });

        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataOfIncompleteGame = loadData();
                if (dataOfIncompleteGame.equals("finished\n") || dataOfIncompleteGame.equals("") == true || dataOfIncompleteGame == null
                        || dataOfIncompleteGame.startsWith("fin") == true) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("Yarım bırakılan oyun yok");
                    builder.setCancelable(true);

                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Oyun.class);
                    MainActivity.this.username = usernameEdt.getText().toString();

                    if (isNameEmpty() == false && isNameLonger() == false) {
                        intent.putExtra("username", MainActivity.this.username);
                        intent.putExtra("dataOfIncompleteGame", dataOfIncompleteGame);
                        intent.putExtra("devamOyunuMu", "true");

                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addView(LinearLayout viewParent, final String key) {
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lineLayoutParams.rightMargin = 30;

        final TextView textView = new TextView(this);

        textView.setLayoutParams(lineLayoutParams);
        textView.setWidth(115);
        textView.setHeight(130);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(this.getResources().getColor(R.color.siyah));
        textView.setText(key);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setTextSize(32);
        textView.setBackground(this.getResources().getDrawable(R.drawable.butonacikyesil));
        textView.setPadding(5, 5, 5, 5);

        viewParent.addView(textView);
    }

    public void resetGame() {
        String filepath = "high_scores.txt";

        try {
            FileOutputStream fileout = openFileOutput("high_scores.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write("");
            outputWriter.close();

            fileout = openFileOutput("gecilenleveller.txt", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write("");
            outputWriter.close();

            Toast.makeText(getBaseContext(), "Oyun verileri sıfırlandı!",
                    Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isNameEmpty() {
        continueBtn = (Button) findViewById(R.id.devamEtBtn);
        username = usernameEdt.getText().toString();

        if (username.equals("") == true) {
            Toast.makeText(getBaseContext(), "İsim boş bırakılamaz!"
                    , Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean isNameLonger() {
        continueBtn = (Button) findViewById(R.id.devamEtBtn);
        username = usernameEdt.getText().toString();

        if (username.length() > 10) {
            Toast.makeText(getBaseContext(), "İsim 10 karakterden uzun olamaz!"
                    , Toast.LENGTH_SHORT).show();

            return true;
        }
        return false;
    }


    public String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String text;
        text = sharedPreferences.getString(TEXT, "");

        return text;
    }
}
