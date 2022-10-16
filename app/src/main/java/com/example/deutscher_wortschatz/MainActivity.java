package com.example.deutscher_wortschatz;

import static com.example.deutscher_wortschatz.DBHelper.TABLE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    //   private static final String DATA_SD = ;
    ProgressBar ProgressBar;
    TextView work_stroke1;
    TextView work_stroke2;
    TextView textscore1;
    TextView textscore2;
    CheckBox CheckBox3;
    CheckBox CheckBox1;
    CheckBox CheckBox2;
    DBHelper dbHelper;
    int index;
    String indexstr;
    int counter;
    String dataForSaving="";
    private static final String LOG_TAG = "==Setting==";
    private static final String FILENAME = "deutsch_database.txt";
    private static final String DIR_SD = "Download";

    String readFromFile="";
    MediaPlayer mediaPlayer;
    AudioManager am;
    boolean isplay;
    boolean presskeyenable=true;
    boolean playenable;
    String workstroke1;
    String workstroke2;
    String name_sound_file_u;
    String name_sound_file_d;

    int totalcount;
    int index_id;
    int index_lesson;
    int index_ourtext;
    int index_deutschtext;
    int index_oursound;
    int index_deutschsound;
    int progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_main);

        ProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        Spinner spinner = findViewById(R.id.spinner);

        CheckBox1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox2 = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox3 = (CheckBox) findViewById(R.id.checkBox3);
        work_stroke1 = (TextView) findViewById(R.id.workstroke1);
        work_stroke2 = (TextView) findViewById(R.id.workstroke2);
        textscore1 = (TextView) findViewById(R.id.score1);
        textscore2 = (TextView) findViewById(R.id.score2);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        work_stroke1.setOnClickListener(this);
        work_stroke2.setOnClickListener(this);

        dbHelper = new DBHelper(this);

        // Настраиваем адаптер
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.lessons,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Вызываем адаптер
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                String[] choose = getResources().getStringArray(R.array.lessons);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ваш выбор: " + choose[selectedItemPosition], Toast.LENGTH_SHORT);
                toast.show();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onClick(View view) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();


        // подключаемся к БД
        //    SQLiteDatabase db = dbHelper.getWritableDatabase();

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("anfangtable", null, null, null, null, null, null);


        switch (view.getId()) {
            case R.id.button1:
                Log.d(LOG_TAG, "--- Insert in mytable: ---");



                if(presskeyenable) {
                    presskeyenable = false;
                    playenable = true;
                    work_stroke1.setText("---");
                    work_stroke2.setText("---");

                    if (index > 1) {
                        index = index - 1;
                    }

                    zeigen();
                }
                break;
            case R.id.button2:
                Log.d(LOG_TAG, "Button 2 Pressed");
                if(presskeyenable) {
                    presskeyenable = false;
                    playenable = true;
                    work_stroke1.setText("---");
                    work_stroke2.setText("---");


                    totalcount = 0;
                    if (cursor.moveToFirst()) {
                        do {
                            totalcount = totalcount + 1;
                        } while (cursor.moveToNext());
                        Log.d(LOG_TAG, "num = " + totalcount);
                    } else
                        Log.d("mLog", "0 rows");

                    SecureRandom random = new SecureRandom();
                    index = random.nextInt(totalcount);

                    zeigen();
                }
                break;
            case R.id.button3:
                Log.d(LOG_TAG, "Button Next Pressed");
                if(presskeyenable){
                    presskeyenable = false;
                    playenable = true;
                    work_stroke1.setText("---");
                work_stroke2.setText("---");

                index = index + 1;
                zeigen();
                }
                break;
            case R.id.button4:
                Log.d(LOG_TAG, "Button Sound");
                if(presskeyenable) play();
                break;
            case R.id.button5:
                Log.d(LOG_TAG, "Button Stop");
                presskeyenable = true;
                playenable = false;
                counter=0;
                int resID=getResources().getIdentifier("s", "raw", getPackageName());

                MediaPlayer mediaPlayer=MediaPlayer.create(this,resID);
                mediaPlayer.start();

                break;
            case R.id.workstroke1:
                work_stroke1.setText(workstroke1);
                break;
            case R.id.workstroke2:
                work_stroke2.setText(workstroke2);
                play();
                break;
        }
        // закрываем подключение к БД
        cursor.close();
        dbHelper.close();



    }

    private void play() {
        isplay=true;
        if(true){

            presskeyenable = false;

        mediaPlayer = new MediaPlayer();
        final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + "/" + name_sound_file_d + ".mp3";

        try {
            mediaPlayer.setDataSource(DATA_SD);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(this);

        mediaPlayer.start();
        }
    }

    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){

            case R.id.update:
                Toast.makeText(this, "update Clicked", Toast.LENGTH_SHORT).show();
                //    Intent intent = new Intent(this, NewActivity.class);
                //    startActivity(intent);
                read_file_from_SD();
                break;
            case R.id.about:
                Toast.makeText(this, "about Clicked", Toast.LENGTH_SHORT).show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    void read_file_from_SD() {
        readFromFile="";
        Log.d(LOG_TAG, "read_DB_from_SD");
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }



        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        //File sdPath = Environment.getExternalStoragePublicDirectory("Install");
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        //   sdPath = new File("/storage/2A35-1DF5/Download");
        //  формируем объект File, который содержит путь к файлу
        Log.d(LOG_TAG, "Read from: " + String.valueOf(sdPath));
        File sdFile = new File(sdPath, FILENAME);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                readFromFile=readFromFile+str+"&";
            }
            Log.d(LOG_TAG, "readFromFile = " + readFromFile);
            //Toast.makeText(NewActivity.this, readFromFile, Toast.LENGTH_LONG).show();
            ParseFile(readFromFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void ParseFile(String inputMassage){

        //    SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        //    Toast.makeText(MainActivity.this, inputMassage, Toast.LENGTH_LONG).show();
        //Log.d(LOG_TAG, "input Processing Massage1: "+ inputMassage);
        //   if (!inputMassage.matches("\\{\\{.*\\}\\}")) {}
        // if (!inputMassage.matches("\\[(\\[\"-?\\d{10}\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\",\"-?\\d\\d?\\d?\\.\\d\"\\],?)+\\]")) { }

        //  if (str.matches("[0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}[-:][0-9A-Fa-f]{2}.*")) {}
        //inputMassage = inputMassage.trim().replaceAll(" +", " ");

   //     if (inputMassage.matches("(.*;.*;.*;.*;.*&)")) { //\u0009
        if (inputMassage.matches("(.*\\u0009.*\\u0009.*\\u0009.*\\u0009.*\\u0009.*&)")) {
            inputMassage = inputMassage.trim().replaceAll(" +", " ");
            Log.d(LOG_TAG,"begin");
            String line[] = inputMassage.split("&");  // зазделяем по записи "&"
            int count=0;

            database.delete(TABLE_NAME,null,null);

            while (count < line.length){
                String subline[] = line[count].split("\\u0009");

                Log.d(LOG_TAG,"----------------------------------------------------");
                Log.d(LOG_TAG, subline[0]+"   "+subline[1]+"   "+subline[2]+"   "+subline[3]+"   "+subline[4]+"   "+subline[5]);

                contentValues.put(DBHelper.KEY_LESSON, subline[1]);
                contentValues.put(DBHelper.KEY_OURTEXT, subline[2]);
                contentValues.put(DBHelper.KEY_DEUTSCHTEXT, subline[3]);
                contentValues.put(DBHelper.KEY_OURSOUND, subline[4]);
                contentValues.put(DBHelper.KEY_DEUTSCHSOUND, subline[5]);

                database.insert(
                        "anfangtable",
                        null,
                        contentValues);

                count++;
                //  Log.d(LOG_TAG, "Position = " + String.valueOf(cursor2.getPosition()));
            }
            Toast.makeText(MainActivity.this, "Database changed!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(MainActivity.this, "File format is wrong!", Toast.LENGTH_LONG).show();
        }
        dbHelper.close();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isplay=false;
        mediaPlayer.release();
        counter = counter + 1;
        textscore2.setText(String.valueOf(counter));
        ProgressBar.setProgress(counter);

        if(playenable) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(playenable) {
                        if (counter < 6) {
                            play();
                        } else {
                            counter = 0;
                            index = index + 1;
                            zeigen();
                        }
                    }
                }
            }, 4000);
        }
    }

    public void zeigen(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("anfangtable", null, null, null, null, null, null);

        indexstr = String.valueOf(index);
        textscore1.setText(String.valueOf(index));
        //database = dbHelper.getWritableDatabase();
        cursor = database.query(
                "anfangtable",
                null,
                "_id = ?",
                new String[] {indexstr},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            index_id = cursor.getColumnIndex(DBHelper.KEY_ID);
            index_lesson = cursor.getColumnIndex(DBHelper.KEY_LESSON);
            index_ourtext = cursor.getColumnIndex(DBHelper.KEY_OURTEXT);
            index_deutschtext = cursor.getColumnIndex(DBHelper.KEY_DEUTSCHTEXT);
            index_oursound = cursor.getColumnIndex(DBHelper.KEY_OURSOUND);
            index_deutschsound = cursor.getColumnIndex(DBHelper.KEY_DEUTSCHSOUND);

            Log.d(LOG_TAG, "ID = " + cursor.getInt(index_id) +
                    ", lesson = " + cursor.getString(index_lesson) +
                    ", ourtext = " + cursor.getString(index_ourtext) +
                    ", deutschtext = " + cursor.getString(index_deutschtext) +
                    ", oursound = " + cursor.getString(index_oursound) +
                    ", deutschsound = " + cursor.getString(index_deutschsound));

            String dataForSavingPrepare = cursor.getInt(index_id) + ";" +
                    cursor.getString(index_lesson) + ";" +
                    cursor.getString(index_ourtext) + ";" +
                    cursor.getString(index_deutschtext) + ";" +
                    cursor.getString(index_oursound) + ";" +
                    cursor.getString(index_deutschsound) + "\n";
            dataForSaving = dataForSavingPrepare;

            Log.d(LOG_TAG,"///////////////////////////////////////////");
            Log.d(LOG_TAG,"dataForSavingPrepare = " + dataForSaving);

            workstroke1=cursor.getString(index_ourtext);
            workstroke2=cursor.getString(index_deutschtext);
            if (CheckBox1.isChecked()) work_stroke1.setText(cursor.getString(index_ourtext));
            if (CheckBox2.isChecked()) work_stroke2.setText(cursor.getString(index_deutschtext));
            name_sound_file_u = cursor.getString(index_oursound);
            name_sound_file_d = cursor.getString(index_deutschsound);

        } else
            Log.d("mLog","0 rows");

        if (CheckBox3.isChecked()) play();



        // удаляем все записи
        //int clearCount = db.delete("mytable", null, null);
        //Log.d(LOG_TAG, "deleted rows count = " + clearCount);

    }
}