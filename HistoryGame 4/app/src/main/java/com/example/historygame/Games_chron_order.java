package com.example.historygame;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
//Комментарий для фетча
public class Games_chron_order extends AppCompatActivity {


    int N = 0;// = array.check.length; //длина данного массива, т.е. размерность исходных данных
    Random random = new Random();
    public int count;
    public int clear; //счетчик для обнуления игры, пересборки
    public int[] number = new int[4]; //массив индексов, в которые будут записаны числа, выбранные рандомом
    public int[] numberD = new int[4]; //то же самое но для даты
    ArrayList<Integer> items = new ArrayList<>(); //массив для связи2 массивов: number и numberD
    public int[] control = new int[4]; //массив индексов события для контроля соответствия события и даты
    public int[] controlD = new int[4];//массив индексов даты для контроля соответствия события и даты
    public int[] controlColor = new int[4]; //массив для событий для контроля блокированности кнопок, когда отвтеили праивльно
    public int[] controlColorD = new int[4]; //массив для дат для контроля блокированности кнопок, когда отвтеили праивльно

    Connection connection;

    ArrayList<String> events = new ArrayList<String>();
    ArrayList<String> years = new ArrayList<String>();
    ArrayList<Integer> ids = new ArrayList<Integer>();
    int levelCount = 1;

    String queryParam;
    Integer gameParam;
    Integer numOfLevels;

    Dialog dialog;
    TextView dialogCloseButton;
    Button dialogRepeatButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chron_order);
        //тут просто находим по индексам кнопки событий и инициализируем их
        final TextView txt1A = findViewById(R.id.textViewG1);
        final TextView txt2A = findViewById(R.id.textViewG2);
        final TextView txt3A = findViewById(R.id.textViewG3);
        final TextView txt4A = findViewById(R.id.textViewG4);
        //тут создается массив предыдущей штуки, чтобы потом можно было обрааться по индексу
        TextView[] txt = new TextView[4];
        txt[0]=txt1A;
        txt[1]=txt2A;
        txt[2]=txt3A;
        txt[3]=txt4A;
        //То же самое, что было для событий, но теперь для кнопок даты
        final TextView txt1D = findViewById(R.id.textViewD1);
        final TextView txt2D = findViewById(R.id.textViewD2);
        final TextView txt3D = findViewById(R.id.textViewD3);
        final TextView txt4D = findViewById(R.id.textViewD4);

        TextView[] txtD = new TextView[4];
        txtD[0]=txt1D;
        txtD[1]=txt2D;
        txtD[2]=txt3D;
        txtD[3]=txt4D;

        Bundle arguments = getIntent().getExtras();
        queryParam = arguments.get("century").toString();
        gameParam = (Integer) arguments.get("gameParam");
        numOfLevels = (Integer) arguments.get("numOfLevels");

        Database db = new Database();
        connection = db.conclass();
        if (db != null) {
            try {
                String query = "SELECT level_event, event_year FROM chronologyofevents" + queryParam;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String event;
                    String year;
                    event = resultSet.getString("level_event");
                    year = resultSet.getString("event_year");
                    events.add(event);
                    years.add(year);
                    ids.add(N);
                    N++;
                }
                connection.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        //Создаем диалоговое окно и команды для него
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (gameParam == 1) {
            dialog.setContentView(R.layout.end_game_dialog);
            dialogCloseButton = (TextView)dialog.findViewById(R.id.button_close);
            dialogCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Games_chron_order.this, Games_centuries.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                    }
                    dialog.hide();
                }
            });
            dialogRepeatButton = (Button) dialog.findViewById(R.id.repeat_game);
            dialogRepeatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    levelCount = 1;
                    dialog.hide();
                }
            });
        }
        else {
            dialog.setContentView(R.layout.super_game_end_dialog);
            dialogRepeatButton = (Button) dialog.findViewById(R.id.repeat_game);
            dialogRepeatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Games_chron_order.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                    }
                    dialog.hide();
                }
            });
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);


        //Кнопка назад
        Button button_back = (Button) findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Games_chron_order.this, Games_centuries.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
            }
        });
        //Конец кода для кнопки назад

        ArrayList<Integer> indices = new ArrayList<>();//Создается массив индексов
        for (int i = 0; i < N; ++i) {
            indices.add(i);}//Заолняем его цифрами от 0 до размерности исходных данных

        for (int i=0; i<4; i++){
            int k = random.nextInt(indices.size());//Выбираем число рандомно из массива индексов(от 0 до N)
            number[i]=indices.get(k);//Запоминаем выбранное число в массив number, который для событий
            indices.remove(k); //Убираем это число из массива, чтобы функция Random больше не брала это число
            txt[i].setText(events.get(number[i]));
            control[i] = ids.get(number[i]);
        }

        items.add(number[0]);//Записываем выбранные Randomom числа для событий в массив, чтобы из этого массива рандомом выбирать числа для даты
        items.add(number[1]);//Это сделано, чтобы даты соответствовали событиям, т.е. не было такого, что выбраны события, а нужных дат к ним нет
        items.add(number[2]);
        items.add(number[3]);
        //Такая же схема, что была для событий
        ArrayList<Integer> indicesD = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            indicesD.add(i);}

        for (int i = 0; i < items.size(); ++i) {
            int k = random.nextInt(indicesD.size());
            numberD[i] = items.get(indicesD.get(k));
            indicesD.remove(k);
            txtD[i].setText(years.get(numberD[i]));
            controlD[i] = ids.get(numberD[i]);
        }

        count=0;
        clear=0;//обнуляем счетчик для контроля праивльных ответов

        for (int i = 0; i < 4; i++){controlColor[i]=0;controlColorD[i]=0;}//обнуляем массив контроля цвета, т.е. правильного ответа

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){//Происходит тык на кнопку событий, т.к. кнопки дат заблокированы, и рассматриваем, какая из кнопок была нажата
                    case R.id.textViewG1://Если первая
                        for (int i = 0; i < 4; i++){//Разрешаем тыкать кнопки дат
                            if (controlColorD[i]==0){txtD[i].setEnabled(true);}
                        }
                        txt[1].setEnabled(false);//Запрещаем тыкать другие кнопки событий
                        txt[2].setEnabled(false);
                        txt[3].setEnabled(false);
                        txt1A.setBackgroundColor((getResources().getColor(R.color.teal_200)));//Меняем цвет,чтобы было видно, что выбрано

                        for (int j = 0; j < 4; j++) {//Берем цикл для просмотра всех кнопок дат
                            int k=j;
                            txtD[k].setOnTouchListener(new View.OnTouchListener() {//Другая ункция нажатия, но по сути такая же проверка на так
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //строка обороботки ивента - касания кнопки
                                        //Если коснулся кнопки
                                        txtD[k].setBackgroundColor((getResources().getColor(R.color.teal_700)));//Поменялся цвет на время тыка

                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {//Когда отпустил кнопку
                                        if (control[0]==controlD[k]){//Если индекс контроля событий и индекс контроля даты равны, т.е. событие соотвествует дате
                                            txt[0].setBackgroundColor((getResources().getColor(R.color.green)));//Красим обе кнопки в зеелный
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.green)));
                                            controlColor[0]=1;//Меняем значение контроля правильных ответов для этой кнопки события
                                            controlColorD[k]=1; //Меняем значение контроля правильных ответов для этой кнопки даты
                                            txt[0].setEnabled(false);//Блокируем эту кнопку, чтобы её больше не трогать, пока не обновим игру
                                            txtD[0].setEnabled(false);//Снова блокируем все кнопки дат, чтобы можно было дальше ыткнуть только кнопку событий
                                            txtD[1].setEnabled(false);
                                            txtD[2].setEnabled(false);
                                            txtD[3].setEnabled(false);
                                            for (int i = 0; i < 4; i++){//Проверяем, если индекс контроля правильных ответов равен 0, т.е. на этот индекс не было дан правильный ответ
                                                if (controlColor[i]==0){txt[i].setEnabled(true);}//Делаем, чтобы конпка снова была кликабельной, если на неё не был дан парвильный ответ
                                            }

                                            clear=clear+1;//В счетчик правильных ответов добавляем 1
                                            if (clear==4){//Проверка, если даны 4 из 4 правильных ответа, т.е. надо обновлять игру
                                                if (levelCount == numOfLevels) {
                                                        dialog.show();
                                                }
                                                txt[0].setEnabled(true);//Делаем все кнопки событий снова кликабельными
                                                txt[1].setEnabled(true);
                                                txt[2].setEnabled(true);
                                                txt[3].setEnabled(true);
                                                for (int i = 0; i < 4; i++){controlColor[i]=0;controlColorD[i]=0;}//Обнуляем индексы правильных ответов
                                                //А дальше тупо код до цикла
                                                items = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {//А ну тут ещё цвет возвращаем
                                                    txt[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                    txtD[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }

                                                ArrayList<Integer> indices = new ArrayList<>();
                                                for (int i = 0; i < N; ++i) {
                                                    indices.add(i);}

                                                for (int i=0; i<4; i++){
                                                    int k = random.nextInt(indices.size());
                                                    number[i]=indices.get(k);
                                                    indices.remove(k);
//                                                    txt[i].setText(array.text[number[i]]);
//                                                    control[i]=array.check[number[i]];
                                                    txt[i].setText(events.get(number[i]));
                                                    control[i] = ids.get(number[i]);
                                                }

                                                items.add(number[0]);
                                                items.add(number[1]);
                                                items.add(number[2]);
                                                items.add(number[3]);

                                                ArrayList<Integer> indicesD = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    indicesD.add(i);}


                                                for (int i = 0; i < items.size(); ++i) {
                                                    int k = random.nextInt(indicesD.size());
                                                    numberD[i] = items.get(indicesD.get(k));
                                                    indicesD.remove(k);
//                                                    txtD[i].setText(array.data[numberD[i]]);
//                                                    controlD[i]=array.check[numberD[i]];
                                                    txtD[i].setText(years.get(numberD[i]));
                                                    controlD[i] = ids.get(numberD[i]);
                                                }
                                                clear=0;//Ну и обнуляем счетчик праивльных ответов
                                                levelCount++;
                                            }
                                        }else{//Если индекс контроля событий и индекс контроля даты не равны, т.е. событие не соотвествует дате
                                            txt[0].setBackgroundColor((getResources().getColor(R.color.red)));//Красим в красный
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.red)));
                                            final Handler handler = new Handler();//Код задержки, чтобы оно немного побыло красным, а потом вернуло желтый цвет
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txt1A.setBackgroundColor((getResources().getColor(R.color.yellow_200_90))); //тут возвращается цвет кнопки
                                                    txtD[k].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }
                                            }, 500);
                                        }
                                    }
                                    return true;
                                }
                            });
                        }

                        break;
                        //Дальше для всех других 3 кнопок то же самое
                    case R.id.textViewG2:
                        txt2A.setBackgroundColor((getResources().getColor(R.color.teal_200)));
                        for (int i = 0; i < 4; i++){//Разрешаем тыкать кнопки дат
                            if (controlColorD[i]==0){txtD[i].setEnabled(true);}
                        }
                        txt[0].setEnabled(false);
                        txt[2].setEnabled(false);
                        txt[3].setEnabled(false);
                        for (int j = 0; j < 4; j++) {
                            int k=j;
                            txtD[k].setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //строка обороботки ивента - касания кнопки
                                        //Если коснулся кнопки
                                        txtD[k].setBackgroundColor((getResources().getColor(R.color.teal_700)));
                                        //finalD = k;
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                        if (control[1]==controlD[k]){
                                            txt[1].setBackgroundColor((getResources().getColor(R.color.green)));
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.green)));
                                            txt[1].setEnabled(false);
                                            txtD[0].setEnabled(false);
                                            txtD[1].setEnabled(false);
                                            txtD[2].setEnabled(false);
                                            txtD[3].setEnabled(false);
                                            controlColor[1]=1;
                                            controlColorD[k]=1;
                                            for (int i = 0; i < 4; i++){
                                                if (controlColor[i]==0){txt[i].setEnabled(true);}
                                            }
                                            clear=clear+1;
                                            if (clear==4){
                                                if (levelCount == numOfLevels) {
                                                        dialog.show();
                                                }
                                                txt[0].setEnabled(true);
                                                txt[1].setEnabled(true);
                                                txt[2].setEnabled(true);
                                                txt[3].setEnabled(true);
                                                for (int i = 0; i < 4; i++){controlColor[i]=0;controlColorD[i]=0;}
                                                items = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    txt[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                    txtD[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }

                                                ArrayList<Integer> indices = new ArrayList<>();
                                                for (int i = 0; i < N; ++i) {
                                                    indices.add(i);}

                                                for (int i=0; i<4; i++){
                                                    int k = random.nextInt(indices.size());
                                                    number[i]=indices.get(k);
                                                    indices.remove(k);
//                                                    txt[i].setText(array.text[number[i]]);
//                                                    control[i]=array.check[number[i]];
                                                    txt[i].setText(events.get(number[i]));
                                                    control[i] = ids.get(number[i]);
                                                }



                                                items.add(number[0]);
                                                items.add(number[1]);
                                                items.add(number[2]);
                                                items.add(number[3]);

                                                ArrayList<Integer> indicesD = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    indicesD.add(i);}


                                                for (int i = 0; i < items.size(); ++i) {
                                                    int k = random.nextInt(indicesD.size());
                                                    numberD[i] = items.get(indicesD.get(k));
                                                    indicesD.remove(k);
//                                                    txtD[i].setText(array.data[numberD[i]]);
//                                                    controlD[i]=array.check[numberD[i]];
                                                    txtD[i].setText(years.get(numberD[i]));
                                                    controlD[i] = ids.get(numberD[i]);
                                                }
                                                clear=0;
                                                levelCount++;
                                            }
                                        }else{
                                            txt[1].setBackgroundColor((getResources().getColor(R.color.red)));
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.red)));
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txt2A.setBackgroundColor((getResources().getColor(R.color.yellow_200_90))); //тут возвращается цвет кнопки
                                                    txtD[k].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }
                                            }, 500);
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                        break;
                    case R.id.textViewG3:
                        txt3A.setBackgroundColor((getResources().getColor(R.color.teal_200)));
                        for (int i = 0; i < 4; i++){//Разрешаем тыкать кнопки дат
                            if (controlColorD[i]==0){txtD[i].setEnabled(true);}
                        }
                        txt[0].setEnabled(false);
                        txt[1].setEnabled(false);
                        txt[3].setEnabled(false);

                        for (int j = 0; j < 4; j++) {
                            int k=j;
                            txtD[k].setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //строка обороботки ивента - касания кнопки
                                        //Если коснулся кнопки
                                        txtD[k].setBackgroundColor((getResources().getColor(R.color.teal_700)));
                                        //finalD = k;
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                        if (control[2]==controlD[k]){
                                            txt[2].setBackgroundColor((getResources().getColor(R.color.green)));
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.green)));
                                            txt[2].setEnabled(false);
                                            txtD[0].setEnabled(false);
                                            txtD[1].setEnabled(false);
                                            txtD[2].setEnabled(false);
                                            txtD[3].setEnabled(false);
                                            controlColor[2]=1;
                                            controlColorD[k]=1;
                                            for (int i = 0; i < 4; i++){
                                                if (controlColor[i]==0){txt[i].setEnabled(true);}
                                            }
                                            clear=clear+1;
                                            if (clear==4){
                                                if (levelCount == numOfLevels) {
                                                        dialog.show();
                                                }
                                                txt[0].setEnabled(true);
                                                txt[1].setEnabled(true);
                                                txt[2].setEnabled(true);
                                                txt[3].setEnabled(true);
                                                for (int i = 0; i < 4; i++){controlColor[i]=0;controlColorD[i]=0;}
                                                items = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    txt[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                    txtD[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }

                                                ArrayList<Integer> indices = new ArrayList<>();
                                                for (int i = 0; i < N; ++i) {
                                                    indices.add(i);}

                                                for (int i=0; i<4; i++){
                                                    int k = random.nextInt(indices.size());
                                                    number[i]=indices.get(k);
                                                    indices.remove(k);
//                                                    txt[i].setText(array.text[number[i]]);
//                                                    control[i]=array.check[number[i]];
                                                    txt[i].setText(events.get(number[i]));
                                                    control[i] = ids.get(number[i]);
                                                }

                                                items.add(number[0]);
                                                items.add(number[1]);
                                                items.add(number[2]);
                                                items.add(number[3]);

                                                ArrayList<Integer> indicesD = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    indicesD.add(i);}


                                                for (int i = 0; i < items.size(); ++i) {
                                                    int k = random.nextInt(indicesD.size());
                                                    numberD[i] = items.get(indicesD.get(k));
                                                    indicesD.remove(k);
//                                                    txtD[i].setText(array.data[numberD[i]]);
//                                                    controlD[i]=array.check[numberD[i]];
                                                    txtD[i].setText(years.get(numberD[i]));
                                                    controlD[i] = ids.get(numberD[i]);
                                                }
                                                clear=0;
                                                levelCount++;
                                            }
                                        }else{
                                            txt[2].setBackgroundColor((getResources().getColor(R.color.red)));
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.red)));
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txt3A.setBackgroundColor((getResources().getColor(R.color.yellow_200_90))); //тут возвращается цвет кнопки
                                                    txtD[k].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }
                                            }, 500);
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                        break;
                    case R.id.textViewG4:
                        txt4A.setBackgroundColor((getResources().getColor(R.color.teal_200)));
                        for (int i = 0; i < 4; i++){//Разрешаем тыкать кнопки дат
                            if (controlColorD[i]==0){txtD[i].setEnabled(true);}
                        }
                        txt[0].setEnabled(false);
                        txt[1].setEnabled(false);
                        txt[2].setEnabled(false);
                        for (int j = 0; j < 4; j++) {
                            int k=j;
                            txtD[k].setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //строка обороботки ивента - касания кнопки
                                        //Если коснулся кнопки
                                        txtD[k].setBackgroundColor((getResources().getColor(R.color.teal_700)));
                                        //finalD = k;
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                        if (control[3]==controlD[k]){
                                            txt[3].setBackgroundColor((getResources().getColor(R.color.green)));
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.green)));
                                            txt[3].setEnabled(false);
                                            txtD[0].setEnabled(false);
                                            txtD[1].setEnabled(false);
                                            txtD[2].setEnabled(false);
                                            txtD[3].setEnabled(false);
                                            controlColor[3]=1;
                                            controlColorD[k]=1;
                                            for (int i = 0; i < 4; i++){
                                                if (controlColor[i]==0){txt[i].setEnabled(true);}
                                            }
                                            clear=clear+1;
                                            if (clear==4){
                                                if (levelCount == numOfLevels) {
                                                        dialog.show();
                                                }
                                                txt[0].setEnabled(true);
                                                txt[1].setEnabled(true);
                                                txt[2].setEnabled(true);
                                                txt[3].setEnabled(true);
                                                for (int i = 0; i < 4; i++){controlColor[i]=0;controlColorD[i]=0;}
                                                items = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    txt[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                    txtD[i].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }
                                                ArrayList<Integer> indices = new ArrayList<>();
                                                for (int i = 0; i < N; ++i) {
                                                    indices.add(i);}

                                                for (int i=0; i<4; i++){
                                                    int k = random.nextInt(indices.size());
                                                    number[i]=indices.get(k);
                                                    indices.remove(k);
//                                                    txt[i].setText(array.text[number[i]]);
//                                                    control[i]=array.check[number[i]];
                                                    txt[i].setText(events.get(number[i]));
                                                    control[i] = ids.get(number[i]);
                                                }

                                                items.add(number[0]);
                                                items.add(number[1]);
                                                items.add(number[2]);
                                                items.add(number[3]);

                                                ArrayList<Integer> indicesD = new ArrayList<>();
                                                for (int i = 0; i < 4; ++i) {
                                                    indicesD.add(i);}

                                                for (int i = 0; i < items.size(); ++i) {
                                                    int k = random.nextInt(indicesD.size());
                                                    numberD[i] = items.get(indicesD.get(k));
                                                    indicesD.remove(k);
//                                                    txtD[i].setText(array.data[numberD[i]]);
//                                                    controlD[i]=array.check[numberD[i]];
                                                    txtD[i].setText(years.get(numberD[i]));
                                                    controlD[i] = ids.get(numberD[i]);
                                                }
                                                clear=0;
                                                levelCount++;
                                            }
                                        }else{
                                            txt[3].setBackgroundColor((getResources().getColor(R.color.red)));
                                            txtD[k].setBackgroundColor((getResources().getColor(R.color.red)));
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txt4A.setBackgroundColor((getResources().getColor(R.color.yellow_200_90))); //тут возвращается цвет кнопки
                                                    txtD[k].setBackgroundColor((getResources().getColor(R.color.yellow_200_90)));
                                                }
                                            }, 500);
                                        }
                                    }
                                    return true;
                                }
                            });
                        }
                        break;
                }
            }
        };

        for (int i = 0; i < 4; i++){//Этот цикл и вызывает функцию Click, т.е. это как бы и проверяет была ли тыкнута кнопка
            txt[i].setOnClickListener(onClickListener);
            txtD[i].setOnClickListener(onClickListener);
        }

    }


    //Тут код делает так, чтобы системной кнопкой назад было назад
    @Override
    public void onBackPressed(){
        try{
            Intent intent=new Intent(Games_chron_order.this,Games_centuries.class);
            startActivity(intent); finish();
        }catch(Exception e) {
        }
    }
    //Законичлся апдейт кода
}