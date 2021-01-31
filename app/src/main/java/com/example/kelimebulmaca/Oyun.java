package com.example.kelimebulmaca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Oyun extends AppCompatActivity {

    private String[][] minimizedWordsStrArr;
    private int pressCounter = 0;
    private int maxPresCounter = 15;
    private Boolean[] isWordPlaced;
    private String[] answers, keys, grid;
    private String[] dataIncompleteGame = new String[11];
    private CharSequence time; //milisecond

    private String username;
    private String knownWordsStr = "";

    private int numberOfLetters = 4;
    private int numberOfKnownWords = 0;
    private int score = 0;
    private int numberOfWrongs = 0;
    private int levelInt = 1;
    private int sublevelInt = 1;

    private List<String> answersLst = new ArrayList<>();
    private List<String> knownWordsLst = new ArrayList<>();
    private List<List<String>> wordsLetterSortedByGroup = new ArrayList<>();
    private List<String> orientationOfAnswers = new ArrayList<>();
    private List<String> orientationOfKnownWords = new ArrayList<>();
    private List<Integer> xKoord = new ArrayList<>();
    private List<Integer> yKoord = new ArrayList<>();
    private List<Integer> xCoordKnown = new ArrayList<>();
    private List<Integer> yCoordKnown = new ArrayList<>();
    private List<Integer> numOfLettersOfAnswers;

    private Chronometer chronometer;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oyun);

        Intent intent = getIntent();
        String isIncompleteGame = intent.getStringExtra("devamOyunuMu");

        if (isIncompleteGame.equals("true") == true) {
            String dataOfIncompleteGame = intent.getStringExtra("dataOfIncompleteGame");

            dataIncompleteGame = dataOfIncompleteGame.split("\n");

            levelInt = Integer.parseInt(dataIncompleteGame[5]);
            sublevelInt = Integer.parseInt(dataIncompleteGame[6]);
            score = Integer.parseInt(dataIncompleteGame[4]);
            time = dataIncompleteGame[3];

            String[] xCoordStr = dataIncompleteGame[7].split(" ");
            String[] yCoordStr = dataIncompleteGame[8].split(" ");
            String[] orientationStr = dataIncompleteGame[9].split(" ");

            grid = dataIncompleteGame[2].split("-");
            answers = dataIncompleteGame[0].split(" ");

            String[] knownWordsStrArr = dataIncompleteGame[1].split(" ");

            numberOfKnownWords = knownWordsStrArr.length;

            doValidate(levelInt, sublevelInt);

            for (String answer : answers) {
                answersLst.add(answer);
            }

            for (String s : knownWordsStrArr) {
                knownWordsLst.add(s);
            }

            numOfLettersOfAnswers = new ArrayList<>();
            for (String answer : answers) {
                numOfLettersOfAnswers.add(answer.length());
            }

            keys = minimizeString(answers);
            chronometer = (Chronometer) findViewById(R.id.zaman);
            int minute = Character.getNumericValue(time.charAt(0)) * 10 +
                    Character.getNumericValue(time.charAt(1)) * 1;

            int second = Character.getNumericValue(time.charAt(3)) * 10 +
                    Character.getNumericValue(time.charAt(4)) * 1;

            chronometer.setBase(SystemClock.elapsedRealtime() - (minute * 60000 + second * 1000));
            chronometer.start();

            for (int i = 0; i < xCoordStr.length; i++) {

                if (orientationStr[i] != null || orientationStr[i].equals("") == false) {
                    orientationOfAnswers.add(orientationStr[i]);
                }

                if (xCoordStr[i] != null || xCoordStr[i].equals("") == false) {
                    xCoordKnown.add(Integer.parseInt(xCoordStr[i]));
                }

                if (yCoordStr[i] != null || yCoordStr[i].equals("") == false) {
                    yCoordKnown.add(Integer.parseInt(yCoordStr[i]));
                }
            }

            String levelStr = "Kolay - 3 Harf";
            String sublevelStr = "Level 1";

            if (levelInt == 1) {
                levelStr = "Kolay - 3 Harf";
            } else if (levelInt == 2) {
                levelStr = "Orta - 4 Harf";
            } else if (levelInt == 3) {
                levelStr = "Zor - 5+ Harf";
            }

            if (sublevelInt == 1) {
                sublevelStr = "Level 1";
            } else if (sublevelInt == 2) {
                sublevelStr = "Level 2";
            } else if (sublevelInt == 3) {
                sublevelStr = "Level 3";
            } else if (sublevelInt == 4) {
                sublevelStr = "Level 4";
            } else if (sublevelInt == 5) {
                sublevelStr = "Level 5";
            } else if (sublevelInt == 6) {
                sublevelStr = "Level 6";
            }

            TextView levelsTV = (TextView) findViewById(R.id.levels);
            String levelsStr = levelStr + "\n" + sublevelStr;
            levelsTV.setText(levelsStr);

            TextView textView = (TextView) findViewById(R.id.puan);
            textView.setText(getText(R.string.puan).toString() + score);

            visualizeGrid(grid);

            visualizeGridForKnownWords(grid, xCoordKnown, yCoordKnown, orientationOfAnswers, answersLst);

            chronometer = (Chronometer) findViewById(R.id.zaman);
            chronometer.start();


            Button deleteCharacterBtn = (Button) findViewById(R.id.deleteCharacterBtn);
            deleteCharacterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCharacter((EditText) findViewById(R.id.kelimeEdt));
                }
            });


            Button sendBtn = (Button) findViewById(R.id.gonderBtn);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doValidate(levelInt, sublevelInt);

                }
            });

            Button letterBtn = (Button) findViewById(R.id.harfBtn);

            letterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Oyun.this);
                    String answerStr = "";

                    for (String answer : answers) {
                        answerStr = answerStr + answer + "\n";
                    }

                    builder.setMessage(answerStr);
                    builder.setCancelable(true);

                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            keys = shuffleKeys(keys);

            Button shuffle = (Button) findViewById(R.id.karistirBtn);
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keys = shuffleKeys(keys);

                    LinearLayout linearLayout = findViewById(R.id.layoutParent);
                    LinearLayout linearLayout2 = findViewById(R.id.layoutParent2);

                    linearLayout.removeAllViews();
                    linearLayout2.removeAllViews();

                    for (int i = 0; i < keys.length / 2; i++) {
                        String key = keys[i];
                        addView((LinearLayout) findViewById(R.id.layoutParent), key,
                                (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
                    }

                    for (int i = (keys.length / 2); i < keys.length; i++) {
                        String key = keys[i];
                        addView((LinearLayout) findViewById(R.id.layoutParent2), key,
                                (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
                    }
                }
            });

            for (int i = 0; i < keys.length / 2; i++) {
                String key = keys[i];
                addView((LinearLayout) findViewById(R.id.layoutParent), key,
                        (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
            }

            for (int i = (keys.length / 2); i < keys.length; i++) {
                String key = keys[i];
                addView((LinearLayout) findViewById(R.id.layoutParent2), key,
                        (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
            }
            maxPresCounter = 10;

        } else {
            String level, subLevel;
            BufferedReader reader = null;

            Context mContext = Oyun.this;
            InputStream is;

            TextView levels = (TextView) findViewById(R.id.levels);
            intent = getIntent();
            level = intent.getStringExtra("level");
            subLevel = intent.getStringExtra("subLevel");
            levels.setText(level + "\n" + subLevel);


            String wordsStr = "";
            try {
                is = mContext.getAssets().open("words.txt");
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                wordsStr = line + "\n";
                while (line != null) {
                    line = reader.readLine();
                    wordsStr = wordsStr + line + "\n";
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (level.equals("Kolay - 3 Harf") == true) {
                levelInt = 1;
            } else if (level.equals("Orta - 4 Harf") == true) {
                levelInt = 2;
            } else if (level.equals("Zor - 5+ Harf") == true) {
                levelInt = 3;
            }

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
            }

            answers = makePuzzleWords(wordsStr, sublevelInt);
            keys = minimizeString(answers);


            sortTheAnswers();
            grid = createPuzzle();

            visualizeGrid(grid);
            chronometer = (Chronometer) findViewById(R.id.zaman);
            chronometer.start();

            Button sendBtn = (Button) findViewById(R.id.gonderBtn);
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doValidate(levelInt, sublevelInt);
                }
            });


            Button deleteCharacterBtn = (Button) findViewById(R.id.deleteCharacterBtn);
            deleteCharacterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCharacter((EditText) findViewById(R.id.kelimeEdt));
                }
            });


            Button letterBtn = (Button) findViewById(R.id.harfBtn);
            letterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Oyun.this);

                    String answerStr = "";

                    for (String answer : answers) {
                        answerStr = answerStr + answer + "\n";
                    }
                    builder.setMessage(answerStr);
                    builder.setCancelable(true);

                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });


            keys = shuffleKeys(keys);

            Button shuffleBtn = (Button) findViewById(R.id.karistirBtn);
            shuffleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keys = shuffleKeys(keys);

                    LinearLayout linearLayout = findViewById(R.id.layoutParent);
                    LinearLayout linearLayout2 = findViewById(R.id.layoutParent2);

                    linearLayout.removeAllViews();
                    linearLayout2.removeAllViews();

                    for (int i = 0; i < keys.length / 2; i++) {
                        String key = keys[i];
                        addView((LinearLayout) findViewById(R.id.layoutParent), key,
                                (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
                    }

                    for (int i = (keys.length / 2); i < keys.length; i++) {
                        String key = keys[i];
                        addView((LinearLayout) findViewById(R.id.layoutParent2), key,
                                (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
                    }
                }
            });

            for (int i = 0; i < keys.length / 2; i++) {
                String key = keys[i];
                addView((LinearLayout) findViewById(R.id.layoutParent), key,
                        (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
            }

            for (int i = (keys.length / 2); i < keys.length; i++) {
                String key = keys[i];
                addView((LinearLayout) findViewById(R.id.layoutParent2), key,
                        (EditText) findViewById(R.id.kelimeEdt), levelInt, sublevelInt);
            }
            maxPresCounter = 10;

        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Oyun.this);

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

    public void deleteCharacter(final EditText wordEdtTxt) {
        String word = wordEdtTxt.getText().toString();

        if (word.length() == 0) {

        } else {
            String newWord = "";
            for (int i = 0; i < word.length() - 1; i++) {
                newWord = newWord + word.charAt(i);
            }

            wordEdtTxt.setText(newWord);
            pressCounter--;
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addView(LinearLayout viewParent, final String key, final EditText wordEdtTxt,
                         final int level, final int sublevel) {
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lineLayoutParams.rightMargin = 30;

        final TextView textView = new TextView(this);

        textView.setLayoutParams(lineLayoutParams);
        textView.setWidth(120);
        textView.setHeight(135);
        textView.setBackground(this.getResources().getDrawable(R.drawable.butonyesil));
        textView.setTextColor(this.getResources().getColor(R.color.beyaz));
        textView.setGravity(Gravity.CENTER);
        textView.setText(key);
        textView.setPadding(10, 10, 10, 10);
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setTextSize(32);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pressCounter < maxPresCounter) {

                    if (pressCounter == 0) {
                        wordEdtTxt.setText("");
                    }

                    wordEdtTxt.setText(wordEdtTxt.getText().toString() + key);
                    pressCounter++;

                    if (pressCounter == maxPresCounter) {
                        doValidate(level, sublevel);
                    }
                }
            }
        });

        viewParent.addView(textView);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void doValidate(int level, int sublevel) {
        pressCounter = 0;
        int flag = 0;

        EditText wordEdtTxt = findViewById(R.id.kelimeEdt);
        LinearLayout linearLayout = findViewById(R.id.layoutParent);
        LinearLayout linearLayout2 = findViewById(R.id.layoutParent2);

        if (wordEdtTxt.getText().length() == 0) {
            // Toast.makeText(Oyun.this, R.string.bos_olamaz_toast,
            //       Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < numOfLettersOfAnswers.size(); i++) {

                if (wordEdtTxt.getText().toString().equals(answers[i])) {

                    if (alreadyExist(knownWordsLst, answers[i]) == false) {
                        knownWordsLst.add(answers[i]);
                        orientationOfKnownWords.add(orientationOfAnswers.get(i));

                        visualizeGridForKnownWords(grid, xKoord, yKoord, orientationOfKnownWords, knownWordsLst);

                        flag = 1;

                        numberOfKnownWords++;
                        knownWordsStr = knownWordsStr + "\n" + answers[i];

                        if (numberOfKnownWords != numOfLettersOfAnswers.size()) {

                            wordEdtTxt.setText("");

                        } else {
                            Intent intent = getIntent();
                            username = intent.getStringExtra("username");

                            intent = new Intent(Oyun.this, LevelTamamlandi.class);

                            calculatePoint(numberOfKnownWords);
                            time = chronometer.getText();

                            intent.putExtra("score", Integer.toString(score));
                            intent.putExtra("numberOfWrongs", Integer.toString(numberOfWrongs));
                            intent.putExtra("numberOfRights", Integer.toString(numberOfKnownWords));
                            intent.putExtra("level", Integer.toString(level));
                            intent.putExtra("sublevel", Integer.toString(sublevel));
                            intent.putExtra("time", time.toString());
                            intent.putExtra("username", username);

                            wordEdtTxt.setText("");
                            //    knownWordsLst.clear();
                            calculatePoint(numberOfKnownWords);
                            score = 0;
                            numberOfWrongs = 0;

                            startActivity(intent);
                            finish();
                        }
                        calculatePoint(numberOfKnownWords);

                    } else {
                        Toast.makeText(Oyun.this, R.string.zaten_bilindi_toast,
                                Toast.LENGTH_SHORT).show();
                        flag = 2;
                        wordEdtTxt.setText("");
                    }
                }
            }

            if (flag == 0) {
                Toast.makeText(Oyun.this, R.string.yanlis_cevap_toast,
                        Toast.LENGTH_SHORT).show();
                wordEdtTxt.setText("");

                numberOfWrongs++;
                calculatePoint(numberOfKnownWords);
            }

            linearLayout.removeAllViews();
            linearLayout2.removeAllViews();

            for (int i = 0; i < keys.length / 2; i++) {
                String key = keys[i];
                addView(linearLayout, key, wordEdtTxt, level, sublevel);
            }

            for (int i = (keys.length / 2); i < keys.length; i++) {
                String key = keys[i];
                addView(linearLayout2, key, wordEdtTxt, level, sublevel);
            }

            calculatePoint(numberOfKnownWords);
        }
    }


    private boolean alreadyExist(List<String> knownWordsArr, String answer) {
        if (knownWordsArr.contains(answer)) {
            return true;
        }
        return false;
    }


    private String[] shuffleKeys(String[] keys) {
        Random rnd = new Random();

        for (int i = keys.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            String tmp = keys[index];
            keys[index] = keys[i];
            keys[i] = tmp;
        }

        return keys;
    }


    private void calculatePoint(int numberOfKnownWords) {
        time = chronometer.getText();
        String timeStr = time.toString();

        int minute = Character.getNumericValue(timeStr.charAt(0)) * 10 +
                Character.getNumericValue(timeStr.charAt(1)) * 1;

        int second = Character.getNumericValue(timeStr.charAt(3)) * 10 +
                Character.getNumericValue(timeStr.charAt(4)) * 1;

        int timeInSeconds = minute * 60 + second;

        int lostPoint = ((timeInSeconds / 5) * 1) + (numberOfWrongs * 2);

        score = (numberOfKnownWords * numberOfLetters * 5) - lostPoint;

        if (score < 0) {
            score = 0;
        }

        TextView textView = (TextView) findViewById(R.id.puan);
        textView.setText(getText(R.string.puan).toString() + score);
    }


    private String[] minimizeString(String[] words) {
        List<String> letters = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                if (letters.contains(Character.toString(words[i].charAt(j))) == false) {
                    letters.add(Character.toString(words[i].charAt(j)));
                }
            }
        }

        String[] lettersArr = new String[letters.size()];
        lettersArr = letters.toArray(lettersArr);

        return lettersArr;
    }


    public String[] groupWords(String[] words, int num) {
        List<String> threeLetter = new ArrayList<>();
        List<String> fourLetter = new ArrayList<>();
        List<String> fivePlusLetter = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            if (minimizeWord(words[i]).length <= 3) {
                threeLetter.add(words[i]);
            }
            if (minimizeWord(words[i]).length <= 4) {
                fourLetter.add(words[i]);
            }
            fivePlusLetter.add(words[i]);

        }

        String[] threeLetterArr = new String[threeLetter.size()];
        threeLetterArr = threeLetter.toArray(threeLetterArr);

        String[] fourLetterArr = new String[fourLetter.size()];
        fourLetterArr = fourLetter.toArray(fourLetterArr);

        String[] fivePlusLetterArr = new String[fivePlusLetter.size()];
        fivePlusLetterArr = fivePlusLetter.toArray(fivePlusLetterArr);


        if (num == 3) {
            return threeLetterArr;
        }
        if (num == 4) {
            return fourLetterArr;
        }

        return fivePlusLetterArr;


    }


    private String[] minimizeWord(String word) {
        List<String> letters = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            if (letters.contains(Character.toString(word.charAt(i))) == false) {
                letters.add(Character.toString(word.charAt(i)));
            }
        }

        String[] lettersArr = new String[letters.size()];
        lettersArr = letters.toArray(lettersArr);

        return lettersArr;
    }

    private List<String> minimizeWordLst(String word) {
        List<String> lettersList = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            if (lettersList.contains(Character.toString(word.charAt(i))) == false) {
                lettersList.add(Character.toString(word.charAt(i)));
            }
        }

        return lettersList;
    }


    private String minimizeWordStr(String word) {
        List<String> letters = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            if (letters.contains(Character.toString(word.charAt(i))) == false) {
                letters.add(Character.toString(word.charAt(i)));
            }
        }

        String lettersStr = "";
        for (String n : letters)
            lettersStr += n;

        return lettersStr;

    }


    private String[] makePuzzleWords(String s, int sublevel) {
        String[] wordsSortedByGroup;

        String[] words = s.split("[\n ]");

        int numOfLetter;
        if (levelInt == 1) {
            numOfLetter = 3;
        } else if (levelInt == 2) {
            numOfLetter = 4;
        } else {
            numOfLetter = 5;
        }

        numberOfLetters = numOfLetter;

        wordsSortedByGroup = groupWords(words, numOfLetter);

        wordsSortedByGroup = deleteSameWords(wordsSortedByGroup);

        getMinimizedWords(wordsSortedByGroup);

        int numberOfWords;

        if (sublevel == 1) {
            numberOfWords = 2;
        } else if (sublevel == 2) {
            numberOfWords = 3;
        } else if (sublevel == 3) {
            numberOfWords = 4;
        } else if (sublevel == 4) {
            numberOfWords = 5;
        } else if (sublevel == 5) {
            numberOfWords = 6;
        } else {
            numberOfWords = 7;
        }

        wordsSortedByGroup = findWordsWithTheSameLetter(wordsSortedByGroup, numOfLetter, numberOfWords);
        numOfLettersOfAnswers = new ArrayList<Integer>();

        int max = wordsSortedByGroup.length; //gruplanmis kelimelerde max uzunluk

        if (max < 1) {
            max = 1;
        }

        List<String> answers = new ArrayList<>();
        List<Integer> randoms = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < numberOfWords; i++) {
            int rnd = random.nextInt(max);

            if (randoms.contains(rnd)) {
                while (randoms.contains(rnd) != false) {
                    rnd = random.nextInt(max);
                }
            }
            randoms.add(rnd);

            answers.add(wordsSortedByGroup[rnd]);
            numOfLettersOfAnswers.add(wordsSortedByGroup[rnd].length());
        }

        String[] answersArr = new String[answers.size()];
        answersArr = answers.toArray(answersArr);

        return answersArr;
    }


    private String[] findWordsWithTheSameLetter(String[] words, int numOfLetter, int numOfWord) {
        List<List<String>> letterGroups = new ArrayList<>();
        letterGroups.add(new ArrayList<String>());

        List<Integer> wordIndexes = new ArrayList<>();

        int q = 0;
        int f = 0;
        for (int i = 0; i < words.length; i++) {
            List<String> minimizedWordList = new ArrayList<>();
            for (int j = 0; j < minimizedWordsStrArr[i].length; j++) {
                minimizedWordList.add(minimizedWordsStrArr[i][j]);
            }

            //kelimeleri minimize edip eger grup yoksa yeni grup olustururuz.
            //eger grup varsa yeni grup olusturulmaz. kelimeler icin grup indeksi eklenir.
            boolean flag = false;
            for (int k = 0; k < letterGroups.size(); k++) {
                if (letterGroups.get(k).equals(minimizedWordList) == true) {
                    flag = true;
                    f = k;
                }
            }
            if (!flag) {
                q++;
                letterGroups.add(minimizedWordList);
                wordIndexes.add(q);
            } else {
                wordIndexes.add(f);
            }
        }

        //kelimeler grup indeksine göre gruplanır.
        List<List<String>> groups = new ArrayList<>();
        groups.add(new ArrayList<String>());

        for (int i = 0; i < wordIndexes.size() - 1; i++) {

            if (groups.size() < wordIndexes.get(i)) {
                groups.add(new ArrayList<String>());

                if (groups.get(wordIndexes.get(i) - 1).contains(words[i]) == false) {
                    groups.get(wordIndexes.get(i) - 1).add(words[i]);
                }

            } else {
                if (groups.get(wordIndexes.get(i) - 1).contains(words[i]) == false) {
                    groups.get(wordIndexes.get(i) - 1).add(words[i]);
                }
            }
        }

        for (int i = 0; i < groups.size(); i += 2) {

            for (int j = 0; j < wordIndexes.size(); j += 2) {

                if (minimizeWord(words[j]).length <= minimizeWord(groups.get(i).get(0)).length) {
                    if (isSubset(words[j], groups.get(i).get(0)) == true) {
                        if (groups.get(i).contains(words[j]) == false) {
                            groups.get(i).add(words[j]);
                        }
                    }
                }
            }
        }

        List<Integer> indexesForGroups = new ArrayList<>();
        int counter = 0;
        int maximum = 1;

        List<Integer> indexesForWords = new ArrayList<>();

        int indexForWord = 0;
        int indexForGroup = 0;
        for (int d = 0; d < groups.size(); d++) {

            if (groups.get(d).size() >= numOfWord || (numOfLetter >= 5 && groups.get(d).size() >= numOfWord - 1)) {

                for (int v = 0; v < groups.get(d).size(); v++) {

                    if (numOfLetter == 5) {
                        if (minimizeWordStr(groups.get(d).get(v)).length() >= numOfLetter) {
                            indexForWord = v;
                            indexForGroup = d;

                            counter++;
                            maximum = counter;

                            indexesForGroups.add(indexForGroup);
                            indexesForWords.add(indexForWord);
                        }

                    } else {
                        if (minimizeWordStr(groups.get(d).get(v)).length() == numOfLetter) {
                            indexesForGroups.add(d);
                            counter++;
                            maximum = counter;
                            indexesForWords.add(v);
                        }
                    }
                }
            }
        }

        Random random = new Random();
        int rndForGroup = random.nextInt(maximum);

        indexForWord = indexesForWords.get(rndForGroup);
        indexForGroup = indexesForGroups.get(rndForGroup);

        List<String> answersList = new ArrayList<>();

        List<Integer> randomsList = new ArrayList<>();

        int rndForWord;

        answersList.add(groups.get(indexForGroup).get(indexForWord));

        for (int i = 1; i < numOfWord; i++) {
            randomsList.add(indexForWord);
            rndForWord = random.nextInt(groups.get(indexesForGroups.get(rndForGroup)).size());

            if (randomsList.contains(rndForWord) == true) {
                while (randomsList.contains(rndForWord) == true) {
                    rndForWord = random.nextInt(groups.get(indexesForGroups.get(rndForGroup)).size());
                }
            }
            randomsList.add(rndForWord);
            answersList.add(groups.get(indexesForGroups.get(rndForGroup)).get(rndForWord));
        }

        String[] answersArr = answersList.toArray(new String[answersList.size()]);

        return answersArr;
    }


    public void getMinimizedWords(String[] groupedWords) {
        for (int i = 0; i < groupedWords.length; i++) {
            List<String> a = minimizeWordLst(groupedWords[i]);
            wordsLetterSortedByGroup.add(a);
        }

        minimizedWordsStrArr = new String[wordsLetterSortedByGroup.size()][];

        for (int i = 0; i < wordsLetterSortedByGroup.size(); i++) {
            minimizedWordsStrArr[i] = new String[wordsLetterSortedByGroup.get(i).size()];

            String[] word = wordsLetterSortedByGroup.get(i).toArray(minimizedWordsStrArr[i]);
            String wordStr = "";
            for (String n : word)
                wordStr += n;
            char[] wordCharArr = wordStr.toCharArray();

            Arrays.sort(wordCharArr);

            wordStr = String.valueOf(wordCharArr);
            String[] wordStrArr = new String[]{wordStr};
            minimizedWordsStrArr[i] = wordStrArr;
        }
    }


    public String[] deleteSameWords(String[] wordList) {
        List<String> wordsList = new ArrayList<>();
        for (int i = 0; i < wordList.length; i++) {
            if (wordsList.contains(wordList[i]) == false) {
                wordsList.add(wordList[i]);
            }
        }

        String[] wordListArr = wordsList.toArray(new String[wordsList.size()]);

        return wordListArr;
    }

    public boolean isSubset(String s1, String s2) {

        String minimizeS1 = minimizeWordStr(s1);
        String minimizeS2 = minimizeWordStr(s2);

        char[] s1AsChar = minimizeS1.toCharArray();
        char[] s2AsChar = minimizeS2.toCharArray();

        int M = minimizeS1.length();
        int N = minimizeS2.length();

        Arrays.sort(s1AsChar);
        Arrays.sort(s2AsChar);

        minimizeS1 = new String(s1AsChar);
        minimizeS2 = new String(s2AsChar);


        int j = 0;
        if (minimizeS1.length() > minimizeS2.length()) {
            return false;
        } else {
            for (int i = 0; i < N && j < M; i++)
                if (minimizeS1.charAt(j) == minimizeS2.charAt(i))
                    j++;

            return (j == M);
        }
    }

    public String[] createPuzzle() {
        String[] grid = {"##############", "##############", "##############", "##############",
                "##############", "##############", "##############", "##############",
                "##############", "##############", "##############", "##############",
                "##############", "##############"
        };

        int x = 13; //bulmacanın max boyutları
        int y = 13;

        String[] orientation = new String[answers.length];
        for (int i = 0; i < answers.length; i++) {
            orientation[i] = "";
        }

        isWordPlaced = new Boolean[answers.length];
        for (int i = 0; i < answers.length; i++) {
            isWordPlaced[i] = false;
        }

        int counterOfPlacedWords = 0;
        boolean flag2 = false;

        for (int i = 0; i < answers.length; i++) {

            if (isWordPlaced[i] == false) { //cevaplar[i] yerlesmemisse
                if (isGridEmpty(grid) == true) { //grid bos

                    int xCoordForWord1 = (int) Math.ceil((double) (x - answers[i].length()) / 2); //yerlesmeye baslama indexi
                    int yCoordForWord1 = (int) Math.ceil((double) y / 2); //yerlesmeye baslama indexi

                    orientation[i] = "yatay";
                    grid = placeWordOnGrid(grid, xCoordForWord1, yCoordForWord1, orientation[i], answers[i], i);

                    counterOfPlacedWords = 0;
                    for (int h = 0; h < isWordPlaced.length; h++) {
                        if (isWordPlaced[h] == true) {
                            counterOfPlacedWords++; //kac kelime yerlestirildi sayar
                        }
                    }
                } else {

                    String word1 = answers[i];
                    for (int j = counterOfPlacedWords - 1; j >= 0; j--) {
                        if (isWordPlaced[i] == false && counterOfPlacedWords != answers.length) {
                            String word2 = answers[j];

                            if (flag2 == false) { //eger kelime ilk kelimeye yerlestirilemediyse
                                int dimensionX = 14;
                                int dimensionY = 14;
                                for (int u = 0; u < word2.length(); u++) {
                                    for (int v = 0; v < word1.length(); v++) {
                                        if (word1.charAt(v) == word2.charAt(u) && isWordPlaced[i] == false) { //eslesen harf varsa
                                            if (orientation[j].equals("yatay") == true) {
                                                int xCoordWord2 = xKoord.get(j) + u;
                                                int yCoordWord2 = yKoord.get(j) - v;

                                                if (xCoordWord2 < dimensionX && yCoordWord2 < dimensionY && (yCoordWord2 + word1.length()) < dimensionY &&
                                                        xCoordWord2 > 0 && yCoordWord2 > 0 &&
                                                        isEmpty(xCoordWord2, yCoordWord2, word1.length(), "dikey") == true) {

                                                    orientation[i] = "dikey";
                                                    grid = placeWordOnGrid2(grid, xCoordWord2, yCoordWord2, orientation[i], word2, word1, i);

                                                    counterOfPlacedWords = 0;
                                                    for (int h = 0; h < isWordPlaced.length; h++) {
                                                        if (isWordPlaced[h] == true) {
                                                            counterOfPlacedWords++; //kac kelime yerlestirildi sayar
                                                        }
                                                    }
                                                }

                                                if (isWordPlaced[i] == true) {
                                                    break;
                                                }

                                            } else if (orientation[j].equals("dikey") == true) {
                                                int xCoordWord2 = xKoord.get(j) - v;
                                                int yCoordWord2 = yKoord.get(j) + u;

                                                if (xCoordWord2 < dimensionX && yCoordWord2 < dimensionY && (xCoordWord2 + word1.length()) < dimensionY &&
                                                        xCoordWord2 > 0 && yCoordWord2 > 0 &&
                                                        isEmpty(xCoordWord2, yCoordWord2, word1.length(), "yatay") == true) {

                                                    orientation[i] = "yatay";
                                                    grid = placeWordOnGrid2(grid, xCoordWord2, yCoordWord2, orientation[i], word2, word1, i);

                                                    counterOfPlacedWords = 0;
                                                    for (int h = 0; h < isWordPlaced.length; h++) {
                                                        if (isWordPlaced[h] == true) {
                                                            counterOfPlacedWords++; //kac kelime yerlestirildi sayar
                                                        }
                                                    }
                                                }

                                                if (isWordPlaced[i] == true) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        List<Integer> unplacedWordsIndexes = new ArrayList<>();
        int unplacedFlag = 0;
        for (int i = 0; i < isWordPlaced.length; i++) {
            if (isWordPlaced[i] == false) {
                unplacedFlag++;
                unplacedWordsIndexes.add(i);
            }
        }

        String word = "";
        if (unplacedFlag == 1) {
            String word2 = answers[unplacedWordsIndexes.get(0)];
            grid = placeWordOnGrid2(grid, 13, 0, "dikey", word, word2, unplacedWordsIndexes.get(0));
        }
        if (unplacedFlag == 2) {
            String word2 = answers[unplacedWordsIndexes.get(0)];
            grid = placeWordOnGrid2(grid, 13, 0, "dikey", word, word2, unplacedWordsIndexes.get(0));

            word2 = answers[unplacedWordsIndexes.get(1)];
            grid = placeWordOnGrid2(grid, 0, 13, "yatay", word, word2, unplacedWordsIndexes.get(1));
        }
        if (unplacedFlag == 3) {
            String word2 = answers[unplacedWordsIndexes.get(0)];
            grid = placeWordOnGrid2(grid, 0, 0, "dikey", word, word2, unplacedWordsIndexes.get(0));

            word2 = answers[unplacedWordsIndexes.get(1)];
            grid = placeWordOnGrid2(grid, 13, 0, "dikey", word, word2, unplacedWordsIndexes.get(1));

            word2 = answers[unplacedWordsIndexes.get(2)];
            grid = placeWordOnGrid2(grid, 0, 13, "yatay", word, word2, unplacedWordsIndexes.get(2));
        }

        for (int i = 0; i < answers.length; i++) {
            answersLst.add(answers[i]);
            orientationOfAnswers.add(orientation[i]);
            xCoordKnown.add(xKoord.get(i));
            yCoordKnown.add(yKoord.get(i));
        }

        return grid;
    }


    private String[] placeWordOnGrid2(String[] grid, int xCoordStarted, int yCoordStarted, String orientation, String word, String word2, int index) {
        int dimension = 14;
        if (orientation.equals("dikey")) { //eger ikinci kelime dikey olacaksa

            String characterOfGrid = "";
            int numOfDifferentLetters = 0;
            int g = 0;

            for (int o = 0; o < dimension; o++) {
                characterOfGrid = Character.toString(grid[o].charAt(xCoordStarted));
                if (characterOfGrid.equals("#") == false) {
                    int yCoordFinished = yCoordStarted + word2.length();
                    if (o >= yCoordStarted && o < yCoordFinished) {
                        g = o - yCoordStarted;
                        if (characterOfGrid.equals(Character.toString(word2.charAt(g))) == false) {
                            numOfDifferentLetters++;
                        }

                        if (o == yCoordFinished - 1 && o != 13) {
                            String b = Character.toString(grid[o + 1].charAt(xCoordStarted));
                            if (b.equals("#") == false) {
                                numOfDifferentLetters++;
                            }
                        }
                        if (o == yCoordFinished && o != 13) {
                            String b = Character.toString(grid[o + 1].charAt(xCoordStarted));
                            if (b.equals("#") == false) {
                                numOfDifferentLetters++;
                            }
                        }
                        if (o == yCoordStarted && o != 0) {
                            String b = Character.toString(grid[o - 1].charAt(xCoordStarted));
                            if (b.equals("#") == false) {
                                numOfDifferentLetters++;
                            }
                        }
                    }
                }
            }

            if (numOfDifferentLetters < 1) {
                int n = 0;
                for (int j = 0; j < dimension; j++) {
                    String line = grid[j];
                    String newLine = "";

                    for (int m = 0; m < line.length(); m++) {
                        if (m == xCoordStarted) {
                            int yCoordFinished = yCoordStarted + word2.length();
                            if (j >= yCoordStarted && j < yCoordFinished) {
                                newLine += Character.toString(word2.charAt(n));
                                n++;
                            } else {
                                newLine += Character.toString(line.charAt(m));
                            }
                        } else {
                            newLine += Character.toString(line.charAt(m));
                        }
                    }
                    grid[j] = newLine;
                }

                isWordPlaced[index] = true;
                xKoord.add(xCoordStarted);
                yKoord.add(yCoordStarted);
            }
        }
        if (orientation.equals("yatay")) {


            String characterOfGrid = "";
            int numOfDifferentLetters = 0;
            int g = 0;
            for (int o = 0; o < dimension; o++) {
                characterOfGrid = Character.toString(grid[yCoordStarted].charAt(o));
                if (characterOfGrid.equals("#") == false) {
                    int xCoordFinished = xCoordStarted + word2.length();
                    if (o >= xCoordStarted && o < xCoordFinished) {
                        g = o - xCoordStarted;

                        if (characterOfGrid.equals(Character.toString(word2.charAt(g))) == false) {
                            numOfDifferentLetters++;
                        }
                        if (o == xCoordFinished - 1 && o != 13) {
                            String b = Character.toString(grid[yCoordStarted].charAt(o + 1));
                            if (b.equals("#") == false) {
                                numOfDifferentLetters++;
                            }
                        }
                        if (o == xCoordFinished && o != 13) {
                            String b = Character.toString(grid[yCoordStarted].charAt(o + 1));
                            if (b.equals("#") == false) {
                                numOfDifferentLetters++;
                            }
                        }
                        if (o == xCoordStarted && o != 0) {
                            String b = Character.toString(grid[yCoordStarted].charAt(o - 1));
                            if (b.equals("#") == false) {
                                numOfDifferentLetters++;
                            }
                        }
                    }
                }
            }

            if (numOfDifferentLetters < 1) {
                int n = 0;
                String line = grid[yCoordStarted];
                String newLine = "";

                for (int m = 0; m < line.length(); m++) {
                    int xCoordFinished = xCoordStarted + word2.length();

                    if (m >= xCoordStarted && m < xCoordFinished) {
                        newLine += Character.toString(word2.charAt(n));
                        n++;
                    } else {
                        newLine += Character.toString(line.charAt(m));
                    }
                }
                grid[yCoordStarted] = newLine;

                isWordPlaced[index] = true;
                xKoord.add(xCoordStarted);
                yKoord.add(yCoordStarted);
            }
        }
        return grid;
    }

    private String[] placeWordOnGrid(String[] grid, int xCoordStarted, int yCoordStarted, String orientation, String word, int index) {
        String s = "";
        for (int i = 0; i < xCoordStarted; i++) {
            s += "#";
        }

        s += word;

        for (int i = 0; i < (13 - xCoordStarted - word.length() + 1); i++) {
            s += "#";
        }

        if (orientation.equals("yatay")) {
            for (int i = 0; i < word.length(); i++) {
                grid[yCoordStarted] = s;
            }
        }

        isWordPlaced[index] = true;
        xKoord.add(xCoordStarted);
        yKoord.add(yCoordStarted);
        return grid;
    }

    public Boolean isGridEmpty(String[] grid) {
        boolean flag = false;

        for (int i = 0; i < grid.length; i++) {
            if (grid[i].equals("##############") == false) {

                flag = true;
            }
        }

        return !(flag);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void visualizeGrid(String[] grid) {
        LinearLayout viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle0);

        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        for (int i = 0; i < grid.length; i++) {

            if (grid[i].equals("##############") == true) {
                continue;
            }

            if (i == 0) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle0);
            } else if (i == 1) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle1);
            } else if (i == 2) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle2);
            } else if (i == 3) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle3);
            } else if (i == 4) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle4);
            } else if (i == 5) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle5);
            } else if (i == 6) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle6);
            } else if (i == 7) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle7);
            } else if (i == 8) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle8);
            } else if (i == 9) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle9);
            } else if (i == 10) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle10);
            } else if (i == 11) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle11);
            } else if (i == 12) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle12);
            } else if (i == 13) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle13);
            }

            for (int j = 0; j < grid[i].length(); j++) {
                String key = Character.toString(grid[i].charAt(j));

                lineLayoutParams.rightMargin = 2;

                final TextView textView = new TextView(this);

                textView.setLayoutParams(lineLayoutParams);
                textView.setWidth(85);
                textView.setHeight(85);

                if (key.equals("#") == true) {
                    textView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    textView.setBackground(this.getResources().getDrawable(R.drawable.puzzle));
                    textView.setTextColor(this.getResources().getColor(R.color.siyah));
                    textView.setGravity(Gravity.CENTER);
                }

                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setTextSize(16);
                textView.setTypeface(null, Typeface.BOLD);

                textView.setTextColor(this.getResources().getColor(R.color.koyumor));

                viewParent.addView(textView);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void visualizeGridForKnownWords(String[] grid, List<Integer> xCoordKnown, List<Integer> yCoordKnown,
                                           List<String> orientationOfKnown, List<String> knownWrd) {

        LinearLayout viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle0);

        orientationOfKnown = this.orientationOfAnswers;
        xCoordKnown = this.xCoordKnown;
        yCoordKnown = this.yCoordKnown;
        knownWrd = this.answersLst;

        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        viewParent.removeAllViews();

        for (int i = 0; i < grid.length; i++) {

            if (i == 0) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle0);
            } else if (i == 1) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle1);
            } else if (i == 2) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle2);
            } else if (i == 3) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle3);
            } else if (i == 4) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle4);
            } else if (i == 5) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle5);
            } else if (i == 6) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle6);
            } else if (i == 7) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle7);
            } else if (i == 8) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle8);
            } else if (i == 9) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle9);
            } else if (i == 10) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle10);
            } else if (i == 11) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle11);
            } else if (i == 12) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle12);
            } else if (i == 13) {
                viewParent = (LinearLayout) findViewById(R.id.layoutPuzzle13);
            }

            viewParent.removeAllViews();

            if (grid[i].equals("##############") == true) {
                continue;
            }
            for (int k = 0; k < grid[i].length(); k++) {
                String key = Character.toString(grid[i].charAt(k));
                lineLayoutParams.rightMargin = 2;

                final TextView textView = new TextView(this);

                textView.setLayoutParams(lineLayoutParams);
                textView.setWidth(85);
                textView.setHeight(85);

                if (key.equals("#") == true) {
                } else {
                    for (int j = 0; j < orientationOfKnown.size(); j++) {
                        if (orientationOfKnown.get(j).equals("yatay")) {
                            if (knownWordsLst.contains(knownWrd.get(j)) == true) {
                                if (k >= xCoordKnown.get(j) && k < (xCoordKnown.get(j) + knownWrd.get(j).length())) {
                                    if (yCoordKnown.get(j) == i) {
                                        textView.setText(key);
                                        textView.setBackground(this.getResources().getDrawable(R.drawable.puzzle));
                                        textView.setTextColor(this.getResources().getColor(R.color.siyah));
                                        textView.setGravity(Gravity.CENTER);
                                    }
                                }
                            } else {
                                textView.setBackground(this.getResources().getDrawable(R.drawable.puzzle));
                                textView.setTextColor(this.getResources().getColor(R.color.siyah));
                                textView.setGravity(Gravity.CENTER);
                            }

                            textView.setClickable(true);
                            textView.setFocusable(true);
                            textView.setTextSize(16);
                            textView.setTypeface(null, Typeface.BOLD);
                            textView.setTextColor(this.getResources().getColor(R.color.siyah));
                        } else {
                            if (knownWordsLst.contains(knownWrd.get(j)) == true) {
                                if (i >= yCoordKnown.get(j) && i < (yCoordKnown.get(j) + knownWrd.get(j).length())) {
                                    if (k == xCoordKnown.get(j)) {
                                        textView.setText(key);
                                        textView.setBackground(this.getResources().getDrawable(R.drawable.puzzle));
                                        textView.setTextColor(this.getResources().getColor(R.color.siyah));
                                        textView.setGravity(Gravity.CENTER);
                                    }
                                }
                            } else {
                                textView.setBackground(this.getResources().getDrawable(R.drawable.puzzle));
                                textView.setTextColor(this.getResources().getColor(R.color.siyah));
                                textView.setGravity(Gravity.CENTER);
                            }
                            textView.setClickable(true);
                            textView.setFocusable(true);
                            textView.setTextSize(16);
                            textView.setTypeface(null, Typeface.BOLD);
                            textView.setTextColor(this.getResources().getColor(R.color.koyumor));
                        }
                    }
                }
                viewParent.addView(textView);
            }
        }
    }


    public void sortTheAnswers() {
        String temp;
        int max = 0;
        for (int i = 0; i < answers.length - 1; i++) {
            for (int j = i + 1; j < answers.length; j++) {
                max = answers[j].length();
                if (answers[i].length() < max) {
                    temp = answers[j];
                    answers[j] = answers[i];
                    answers[i] = temp;
                }
            }
        }
    }

    public boolean isEmpty(int xCoord, int yCoord, int lenOfWord, String orient) {
        boolean flag = true;
        if (orient.equals("yatay") == true) {
            for (int i = 0; i < xKoord.size(); i++) {
                if (xKoord.get(i) == xCoord - 1) {
                    if (yKoord.get(i) == yCoord - 1) {
                        flag = false;
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord) {
                        if (yKoord.get(i) == yCoord - 1) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord) {
                        if (yKoord.get(i) == yCoord + 1) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord + lenOfWord) {
                        if (yKoord.get(i) == yCoord + 1) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord + lenOfWord) {
                        if (yKoord.get(i) == yCoord) {
                            flag = false;
                        }
                    }
                }
            }
        } else if (orient.equals("dikey") == true) {
            for (int i = 0; i < xKoord.size(); i++) {
                if (xKoord.get(i) == xCoord - 1) {
                    if (yKoord.get(i) == yCoord - 1) {
                        flag = false;
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord - 1) {
                        if (yKoord.get(i) == yCoord) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord + 1) {
                        if (yKoord.get(i) == yCoord) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord + 1) {
                        if (yKoord.get(i) == yCoord + lenOfWord) {
                            flag = false;
                        }
                    }
                }
            }
            if (flag != false) {
                for (int i = 0; i < xKoord.size(); i++) {
                    if (xKoord.get(i) == xCoord) {
                        if (yKoord.get(i) == yCoord + lenOfWord) {
                            flag = false;
                        }
                    }
                }
            }
        }
        return flag;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();

        String filepath = "gameData.txt";
        String allStr = "";

        try {
            FileOutputStream fileout = openFileOutput("gameData.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

            if (isFinished() == true) {
                outputWriter.write("finished");
                allStr = "finished\n";
            } else {
                for (int i = 0; i < answersLst.size(); i++) {
                    if (i == 0) {
                        allStr = allStr + answersLst.get(i);
                    } else {
                        allStr = allStr + " " + answersLst.get(i);

                    }
                }
                outputWriter.write("\n");
                allStr = allStr + "\n";

                for (int i = 0; i < knownWordsLst.size(); i++) {
                    if (i == 0) {
                        allStr = allStr + knownWordsLst.get(i);
                    } else {
                        allStr = allStr + " " + knownWordsLst.get(i);
                    }
                }

                String gridStr = "";

                for (int i = 0; i < grid.length; i++) {
                    if (i == 0) {
                        gridStr = gridStr + grid[i];
                    } else {
                        gridStr = gridStr + "-" + grid[i];
                    }
                }

                String xStr = "";
                String[] xStrArr = new String[xCoordKnown.size()];

                for (int i = 0; i < xCoordKnown.size(); i++) {
                    xStrArr[i] = xCoordKnown.get(i).toString();
                    if (i == 0) {
                        xStr = xStr + xStrArr[i];
                    } else {
                        xStr = xStr + " " + xStrArr[i];
                    }
                }

                String yStr = "";
                String[] yStrArr = new String[yCoordKnown.size()];

                for (int i = 0; i < yCoordKnown.size(); i++) {
                    yStrArr[i] = yCoordKnown.get(i).toString();

                    if (i == 0) {
                        yStr = yStr + yStrArr[i];
                    } else {
                        yStr = yStr + " " + yStrArr[i];
                    }
                }

                String orientationStr = "";

                String[] orientationStrArr = new String[orientationOfAnswers.size()];
                for (int i = 0; i < orientationOfAnswers.size(); i++) {
                    orientationStrArr[i] = orientationOfAnswers.get(i);

                    if (i == 0) {
                        orientationStr = orientationStr + orientationStrArr[i];
                    } else {
                        orientationStr = orientationStr + " " + orientationStrArr[i];
                    }
                }

                time = chronometer.getText();
                allStr = allStr + "\n" + gridStr + "\n" + time + "\n" + score + "\n" + levelInt + "\n" + sublevelInt +
                        "\n" + xStr + "\n" + yStr + "\n" + orientationStr;
                outputWriter.write(allStr);

            }
            outputWriter.close();
            saveData(allStr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() {
        if (knownWordsLst.size() == answersLst.size()) {
            return true;
        } else {
            return false;
        }
    }

    public void saveData(String str) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, str);
        if (str.equals("finished\n")) {
            editor.putBoolean(SWITCH1, false);
        } else {
            editor.putBoolean(SWITCH1, true);
        }

        editor.commit();
    }
}

