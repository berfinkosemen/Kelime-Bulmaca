package com.example.kelimebulmaca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Score extends AppCompatActivity {

    static final int READ_BLOCK_SIZE = 100;
    String sc, nm;

    int levelInt = 0;
    int sublevelInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Button clickBtn = (Button) findViewById(R.id.skor_goruntule);
        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int typedLevel = levelInt;
                int typedSublevel = sublevelInt;

                try {
                    sortHighScores(typedLevel, typedSublevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Spinner spinnerLevels = findViewById(R.id.spinnerLevelsScore);
        ArrayAdapter<CharSequence> adapterLevels = ArrayAdapter
                .createFromResource(this, R.array.levelsScore, android.R.layout.simple_spinner_item);

        adapterLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLevels.setAdapter(adapterLevels);


        Spinner spinnerSubLevels = findViewById(R.id.spinnerSubLevelsScore);
        ArrayAdapter<CharSequence> adapterSubLevels = ArrayAdapter
                .createFromResource(this, R.array.subLevelsScore, android.R.layout.simple_spinner_item);

        adapterSubLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubLevels.setAdapter(adapterSubLevels);


        spinnerLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String level = parent.getItemAtPosition(position).toString();

                if (level.equals("Kolay - 3 Harf") == true) {
                    levelInt = 1;
                } else if (level.equals("Orta - 4 Harf") == true) {
                    levelInt = 2;
                } else if (level.equals("Zor - 5+ Harf") == true) {
                    levelInt = 3;
                } else if (level.equals("Tümünü görüntüle") == true) {
                    levelInt = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSubLevels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String subLevel = parent.getItemAtPosition(position).toString();

                if (subLevel.equals("Level 1") == true) {
                    sublevelInt = 1;
                } else if (subLevel.equals("Level 2") == true) {
                    sublevelInt = 2;
                } else if (subLevel.equals("Level 3") == true) {
                    sublevelInt = 3;
                } else if (subLevel.equals("Level 4") == true) {
                    sublevelInt = 4;
                } else if (subLevel.equals("Level 5") == true) {
                    sublevelInt = 5;
                } else if (subLevel.equals("Level 6") == true) {
                    sublevelInt = 6;
                } else if (subLevel.equals("Tümünü görüntüle") == true) {
                    sublevelInt = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void sortHighScores(int typedLevel, int typedSublevel) throws IOException {
        String filepath = "high_scores.txt";

        sc = "";
        nm = "";

        TextView highScores = (TextView) findViewById(R.id.yuksekScorlarTxt);
        TextView names = (TextView) findViewById(R.id.isimlerTxt);

        FileInputStream fileIn = openFileInput("high_scores.txt");
        InputStreamReader InputRead = new InputStreamReader(fileIn);

        char[] inputBuffer = new char[READ_BLOCK_SIZE];
        String s = "";
        int charRead;

        while ((charRead = InputRead.read(inputBuffer)) > 0) {
            String readstring = String.copyValueOf(inputBuffer, 0, charRead);
            s += readstring;
        }
        InputRead.close();

        boolean flag = false;

        int u = 3; //puan + isim + level bölümü

        if (s.equals("") != true) {

            String[] nameParts = s.split("[\t\n]");
            int ln = nameParts.length;

            int[] scoresInt = new int[nameParts.length / u];
            String[] namesStr = new String[nameParts.length / u];
            int[] levelsInt = new int[nameParts.length / u];
            int[] sublevelsInt = new int[nameParts.length / u];

            int j = 0;

            for (int i = 0; i < ln; i += u) {
                scoresInt[j] = Integer.parseInt(nameParts[i]);
                namesStr[j] = nameParts[i + 1];
                levelsInt[j] = Integer.parseInt(String.valueOf(nameParts[i + 2].charAt(0)));
                sublevelsInt[j] = Integer.parseInt(String.valueOf(nameParts[i + 2].charAt(1)));

                j++;
            }

            int n = scoresInt.length;


            for (int i = 0; i < n - 1; i++) {
                int min_idx = i;
                for (j = i + 1; j < n; j++)
                    if (scoresInt[j] > scoresInt[min_idx])
                        min_idx = j;

                int temp = scoresInt[min_idx];
                scoresInt[min_idx] = scoresInt[i];
                scoresInt[i] = temp;

                String tmp = namesStr[min_idx];
                namesStr[min_idx] = namesStr[i];
                namesStr[i] = tmp;

                temp = levelsInt[min_idx];
                levelsInt[min_idx] = levelsInt[i];
                levelsInt[i] = temp;

                temp = sublevelsInt[min_idx];
                sublevelsInt[min_idx] = sublevelsInt[i];
                sublevelsInt[i] = temp;
            }

            int counter = 0;
            for (int i = 0; i < n; i++) {
                if (counter > 10) {
                    break;
                } else {
                    if (typedLevel == 0 && typedSublevel == 0) {
                        if (sc == null) {
                            counter++;
                            flag = true;
                        } else {
                            counter++;
                            flag = true;
                        }
                    } else if (typedLevel == levelsInt[i] && typedSublevel == 0) {
                        if (sc == null) {
                            if (typedLevel == levelsInt[i] && typedSublevel == 0) {
                                counter++;
                                flag = true;
                            }
                        } else {
                            if (typedLevel == levelsInt[i] && typedSublevel == 0) {
                                counter++;
                                flag = true;
                            }
                        }
                    } else {
                        if (sc == null) {
                            if (typedLevel == levelsInt[i] && typedSublevel == sublevelsInt[i]) {
                                counter++;
                                flag = true;
                            }
                        } else {
                            if (typedLevel == levelsInt[i] && typedSublevel == sublevelsInt[i]) {
                                counter++;
                                flag = true;
                            }
                        }
                    }
                }
            }

            int counter2 = 0;
            for (int i = 0; i < n; i++) {
                if (counter2 >= 10) {
                    break;
                } else {
                    if (typedLevel == 0 && typedSublevel == 0) {
                        if (sc == null) {
                            if (Integer.toString(scoresInt[i]).length() == 3) {
                                sc = Integer.toString(scoresInt[i]) + "       " + levelsInt[i] + " - " + sublevelsInt[i];
                            } else if (Integer.toString(scoresInt[i]).length() == 2) {
                                sc = Integer.toString(scoresInt[i]) + "         " + levelsInt[i] + " - " + sublevelsInt[i];
                            } else {
                                sc = Integer.toString(scoresInt[i]) + "           " + levelsInt[i] + " - " + sublevelsInt[i];
                            }
                            nm = (counter2 + 1) + ".  " + namesStr[i];
                            counter2++;
                        } else {
                            if (Integer.toString(scoresInt[i]).length() == 3) {
                                sc = sc + "\n" + scoresInt[i] + "       " + levelsInt[i] + " - " + sublevelsInt[i];
                            } else if (Integer.toString(scoresInt[i]).length() == 2) {
                                sc = sc + "\n" + scoresInt[i] + "         " + levelsInt[i] + " - " + sublevelsInt[i];
                            } else {
                                sc = sc + "\n" + scoresInt[i] + "           " + levelsInt[i] + " - " + sublevelsInt[i];
                            }
                            nm = nm + "\n" + (counter2 + 1) + ".  " + namesStr[i];
                            counter2++;
                        }
                    } else if (typedLevel == levelsInt[i] && typedSublevel == 0) {
                        if (sc == null) {
                            if (typedLevel == levelsInt[i] && typedSublevel == 0) {
                                if (Integer.toString(scoresInt[i]).length() == 3) {
                                    sc = Integer.toString(scoresInt[i]) + "       " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else if (Integer.toString(scoresInt[i]).length() == 2) {
                                    sc = Integer.toString(scoresInt[i]) + "         " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else {
                                    sc = Integer.toString(scoresInt[i]) + "           " + levelsInt[i] + " - " + sublevelsInt[i];
                                }
                                nm = (counter2 + 1) + ".  " + namesStr[i];
                                counter2++;
                            }
                        } else {
                            if (typedLevel == levelsInt[i] && typedSublevel == 0) {
                                if (Integer.toString(scoresInt[i]).length() == 3) {
                                    sc = sc + "\n" + scoresInt[i] + "       " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else if (Integer.toString(scoresInt[i]).length() == 2) {
                                    sc = sc + "\n" + scoresInt[i] + "         " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else {
                                    sc = sc + "\n" + scoresInt[i] + "           " + levelsInt[i] + " - " + sublevelsInt[i];
                                }
                                nm = nm + "\n" + (counter2 + 1) + ".  " + namesStr[i];
                                counter2++;
                            }
                        }

                    } else {
                        if (sc == null) {
                            if (typedLevel == levelsInt[i] && typedSublevel == sublevelsInt[i]) {
                                if (Integer.toString(scoresInt[i]).length() == 3) {
                                    sc = Integer.toString(scoresInt[i]) + "       " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else if (Integer.toString(scoresInt[i]).length() == 2) {
                                    sc = Integer.toString(scoresInt[i]) + "         " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else {
                                    sc = Integer.toString(scoresInt[i]) + "           " + levelsInt[i] + " - " + sublevelsInt[i];
                                }
                                nm = (counter2 + 1) + ".  " + namesStr[i];
                                counter2++;
                            }
                        } else {
                            if (typedLevel == levelsInt[i] && typedSublevel == sublevelsInt[i]) {
                                if (Integer.toString(scoresInt[i]).length() == 3) {
                                    sc = sc + "\n" + scoresInt[i] + "       " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else if (Integer.toString(scoresInt[i]).length() == 2) {
                                    sc = sc + "\n" + scoresInt[i] + "         " + levelsInt[i] + " - " + sublevelsInt[i];
                                } else {
                                    sc = sc + "\n" + scoresInt[i] + "           " + levelsInt[i] + " - " + sublevelsInt[i];
                                }
                                nm = nm + "\n" + (counter2 + 1) + ".  " + namesStr[i];
                                counter2++;
                            }
                        }
                    }
                }
            }

            highScores.setText(sc);
            names.setText(nm);
        } else {
            highScores.setText("Veri yok");
            names.setText("Veri yok");
        }

        if (flag == false || (nm == null && sc == null)) {
            highScores.setText("Veri yok");
            names.setText("Veri yok");
        }
    }
}
