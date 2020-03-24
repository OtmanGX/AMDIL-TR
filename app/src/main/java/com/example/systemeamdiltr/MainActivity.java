package com.example.systemeamdiltr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;

import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.systemeamdiltr.db.DatabaseHelper;
import com.example.systemeamdiltr.entities.Temperature;
import com.example.systemeamdiltr.utils.AppExecutors;
import com.example.systemeamdiltr.utils.MyCountDown;
import com.example.systemeamdiltr.utils.StorageHelper;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.github.mikephil.charting.charts.LineChart;


public class MainActivity extends AppCompatActivity implements tempDialog.ExampleDialogListener {

    //Connection Declaration.
    private String dataStock="";
    private Physicaloid mPhysicaloid;
    private byte[] buf ;
    private Thread thread;
    private Handler mHandler = new Handler();
    private Button connect;
    public static int tP ;
    private Button btnClose;

    //Declaration Charts
    private LineChart mChart2;
    private ArrayList<String> timeArraylist = new ArrayList<String>();

    public static ArrayList<String> timeArraylist2 = new ArrayList<String>();
    public static ArrayList<Integer> tempArraylist = new ArrayList<Integer>();
    public static ArrayList<Integer> pressArraylist = new ArrayList<Integer>();


    public static ArrayList<Integer> dayAVGlist = new ArrayList<Integer>();
    private ArrayList<String> daysAVGName = new ArrayList<String>();
    public static ArrayList<Integer> monthAVGlist = new ArrayList<Integer>();
    public static ArrayList<Integer> helplist = new ArrayList<Integer>();
    private ArrayList<String> monthsAVGName = new ArrayList<String>();


    //***************************************************************************************


    public static ArrayList<Integer> lastMonthList = new ArrayList<Integer>();
    public static ArrayList<String> lastMonthListX = new ArrayList<String>();

    public static ArrayList<Integer> lastYearList = new ArrayList<Integer>();
    public static ArrayList<String> lastYearListX = new ArrayList<String>();


    public static ArrayList<Integer> lastMonthListP = new ArrayList<Integer>();
    public static ArrayList<String> lastMonthListXP = new ArrayList<String>();

    public static ArrayList<Integer> lastYearListP = new ArrayList<Integer>();
    public static ArrayList<String> lastYearListXP = new ArrayList<String>();

    private MyCountDown countDown;

    int x=0,y=0 ;
    int x2=0,y2=0 ;
    int x3=0,y3=0 ;

    // les vaiables des modes
    Boolean mode;
    int connexion = 0;

    Button mRec;
    Button mEmet;


    //Declaration des champ
    TextView tempTV,
            presTV,
            dateTV;

    TextView redImgMax,
            greenImgMax,
            orangImgMax,
            sImgMax,
            redImgmin,
            greenImgmin,
            orangImgmin,
            sImgmin;

    TextView redImgMaxP,
           greenImgMaxP,
           orangImgMaxP,
            sImgMaxP,
             redImgminP,
           greenImgminP,
           orangImgminP,
            sImgminP;

    float   t5Value = 25,
            t4Value = 24,
            t3Value =23,
            t2Value =22,
            t1Value =21;

    float   p5Value = 5,
            p4Value = 4,
            p3Value =3,
            p2Value =2,
            p1Value =1;


    ImageView redImg,
             sImg,
            greenImg,
            orangeImg;

    ImageView redImgP,
             sImgP,
            greenImgP,
            orangeImgP;


    Button updatBtn ;

//    Button pConfig;
    Button tConfig;
    private DatabaseHelper instance;
    private GraphUtils graph;

// on creat ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Local Storage
        StorageHelper.prepare(this);
//        storeTemperatures();
        getTemperatures();
        instance = DatabaseHelper.getInstance(this);
        AppExecutors.getInstance().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
//                        instance.tempDao().clear();
                    }
                }
        );

        // Countdown
        countDown = new MyCountDown(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                dateTV.setText(getDateTime());
            }

            @Override
            public void onFinish() {
                countDown.start();
            }
        };
        countDown.start();

   // Connection Declaration;
        mPhysicaloid = new Physicaloid(this);
        mPhysicaloid.setBaudrate(9600);
        connect=(Button) findViewById(R.id.connect_id);

   // Declaration des Champs
        tempTV =(TextView) findViewById(R.id.tempTV);
//        presTV =(TextView) findViewById(R.id.pressTV);
        dateTV =(TextView) findViewById(R.id.dateCountdown);

        redImgMax  = (TextView) findViewById(R.id.redMAXTVT) ;
        greenImgMax= (TextView) findViewById(R.id.greenMAXTVT) ;
        orangImgMax= (TextView) findViewById(R.id.orangeMAXTVT) ;
        sImgMax = (TextView) findViewById(R.id.bleuMAXTVT) ;
        redImgmin  = (TextView) findViewById(R.id.redminTVT) ;
        greenImgmin= (TextView) findViewById(R.id.greenminTVT) ;
        orangImgmin= (TextView) findViewById(R.id.orangeminTVT) ;
        sImgmin=  (TextView) findViewById(R.id.bleuminTVT) ;

//        redImgMaxP  = (TextView) findViewById(R.id.redMAXTVP) ;
//        greenImgMaxP= (TextView) findViewById(R.id.greenMAXTVP) ;
//        orangImgMaxP= (TextView) findViewById(R.id.orangeMAXTVP) ;
//        sImgMaxP = (TextView) findViewById(R.id.bleuMAXTVP) ;
//        redImgminP  = (TextView) findViewById(R.id.redeminTVP) ;
//        greenImgminP= (TextView) findViewById(R.id.greenminTVP) ;
//        orangImgminP= (TextView) findViewById(R.id.orangeminTVP) ;
//        sImgminP=  (TextView) findViewById(R.id.bleuminTVP) ;


        redImg =(ImageView) findViewById(R.id.redIMGid) ;
        orangeImg=(ImageView) findViewById(R.id.orangeIMGid) ;
        greenImg=(ImageView) findViewById(R.id.greenIMGid) ;
        sImg=(ImageView) findViewById(R.id.sIMGid) ;


//        redImgP =(ImageView) findViewById(R.id.redIMGidP) ;
//        orangeImgP=(ImageView) findViewById(R.id.orangeIMGidP) ;
//        greenImgP=(ImageView) findViewById(R.id.greenIMGidP) ;
//        sImgP=(ImageView) findViewById(R.id.sIMGidP) ;


        mRec =(Button) findViewById(R.id.recbtn);
        mEmet = (Button) findViewById(R.id.emetrec);


    // update les valeurs
        updatBtn =(Button) findViewById(R.id.updatBtn);
        updatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateValues();
            }
        });



    // les préferences
        setUp();
        updateValues();


     //Déclaration des chart
        //Chart1
        initChart();


   //  Etablir la connection

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connexion++;
                if(connexion<3){
                    if(mPhysicaloid.open()) {
                        if(connexion==2){
                            connect.setText("Fermer la connexion");
                            connect.setTextColor(getApplication().getResources().getColor(R.color.GreenAccent));
                        }

                        mPhysicaloid.addReadListener(new ReadLisener() {
                            @Override
                            public void onRead(int size) {

                                buf = new byte[size];

                                mPhysicaloid.read(buf, size);


                                for(int i=0;i<size;i++)
                                {
                                    char a=(char)buf[i];

                                    dataStock += a;
                                    if(a=='F' )
                                    {
                                        final String data[] = dataStock.split(";",-2);
                                        if(data[0].indexOf("S")>1){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    affectation(data[1],data[2],data[3]) ;
                                                }
                                            });
                                        }
                                        dataStock="";
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Cannot open", Toast.LENGTH_LONG).show();
                    }
                }
                if(connexion==3){
                    if(mPhysicaloid.close()) {
                        mPhysicaloid.clearReadListener();
                    }
                    connect.setText("Etablir la connexion");
                    connect.setTextColor(getApplication().getResources().getColor(R.color.primaryLightColor));
                    connexion=1;
                }
            }
        });

        // Température Configuration
//        pConfig = (Button) findViewById(R.id.configPbtn) ;
        tConfig = (Button) findViewById(R.id.configTbtn) ;
        tConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tP = 1;
                openDialog();
            }
        });
//        pConfig.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tP = 2;
//                openDialog();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        storeTemperatures();
        if (countDown!=null) countDown.cancel();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        storeTemperatures();
        if (countDown!=null) countDown.cancel();
        super.onStop();
    }

    private void storeTemperatures() {
        Log.w("Store temperature", String.valueOf(t1Value));
        StorageHelper.editor.putFloat("t1", t1Value).apply();
        StorageHelper.editor.putFloat("t2", t2Value).apply();
        StorageHelper.editor.putFloat("t3", t3Value).apply();
    }

    private void getTemperatures() {
        t1Value =StorageHelper.sharedPreferences.getFloat("t1", t1Value);
        t2Value =StorageHelper.sharedPreferences.getFloat("t2", t2Value);
        t3Value =StorageHelper.sharedPreferences.getFloat("t3", t3Value);
    }

    private void initChart(){
        //Chart2
        mChart2 = (LineChart) findViewById(R.id.chart);
        graph = new GraphUtils(this, mChart2, 1000, 10000);
        graph.initGraph(System.currentTimeMillis(), "today");
//        feedMultiple();
    }


    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                float yValue = (float) (Math.random() * 15) + 10f;
                affectation(Float.toString(yValue*100), "100", "100");
            }
        };

        thread = new Thread() {
            public void run() {
            for (int i = 0; i < 10; i++) {

                // Don't generate garbage runnables inside the loop.
                runOnUiThread(runnable);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        };

        thread.start();
    }


    //Fonction d'affectation
    private void affectation(final String temp, final String press, final String msg
                             ) {

        // les afficheur des valeurs
        Float tempInit=(Float.parseFloat(temp))/100;
        final  float tempF = Float.valueOf(String.format("%.2f", tempInit));
        final Float pressF=(Float.parseFloat(press))/100;

        tvAppend(tempTV,tempF+" °C");
//        tvAppend(presTV,(Float.toString(pressF))+" Bar");

        // la gestion de signalisation
        signalisation(tempF,pressF);

        long diff = System.currentTimeMillis()-graph.referenceTimestamp;

        graph.addEntry(diff, tempF, t1Value, t3Value, t2Value);
        showMode(Integer.parseInt(msg));


        AppExecutors.getInstance().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        instance.tempDao().insert(new Temperature(t1Value, t3Value, t2Value, new Date(), tempF));
                    }
                }
        );


    }

    //Text handler
    private void tvAppend(TextView tv, final CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ftv.setText(text);
            }
        });
    }


    private void updateValues() {
        String str = getConfiguration();	//get text from EditText
        if(str.length()>0) {
            byte[] buf = str.getBytes();	//convert string to byte array
            mPhysicaloid.write(buf, buf.length);	//write data to arduino
        }


    }
    private void signalisation(float temp, float press) {
    //  Temperature signalisation
        //  Red signalisation
        if (temp>= t2Value){
            redImg.setImageResource(R.drawable.ledred);
        }
        if (temp < t2Value){
            redImg.setImageResource(R.drawable.ledoff);
        }
         //  Orange signalisation
        if (temp>= t1Value && temp< t2Value ){
            orangeImg.setImageResource(R.drawable.ledorange);
        } else orangeImg.setImageResource(R.drawable.ledoff);

        //  Green signalisation
        if (temp< t1Value){
            greenImg.setImageResource(R.drawable.ledgreen);
        } else greenImg.setImageResource(R.drawable.ledoff);

        //  Bleu signalisation
        if (temp>= t3Value){
            sImg.setImageResource(R.drawable.alarmon);
            vibration();
        }
        if (temp < t3Value){
            sImg.setImageResource(R.drawable.alarmoff);
        }


    //  Pression signalisation
        //  Red signalisation
//        if (press>= p2Value){
//            redImgP.setImageResource(R.drawable.ledred);
//        }
//        if (press < p2Value){
//            redImgP.setImageResource(R.drawable.ledoff);
//        }
//        //  Orange signalisation
//        if (press>= p1Value && press< p2Value ){
//            orangeImgP.setImageResource(R.drawable.ledorange);
//        } else orangeImgP.setImageResource(R.drawable.ledoff);
//
//        //  Green signalisation
//        if (press< p1Value){
//            greenImgP.setImageResource(R.drawable.ledgreen);
//        } else greenImgP.setImageResource(R.drawable.ledoff);
//
//        //  Bleu signalisation
//        if (press>= p3Value){
//            sImgP.setImageResource(R.drawable.alarmon);
//        }
//        if (press< p3Value){
//            sImgP.setImageResource(R.drawable.alarmoff);
//        }
    }


    private void setUp() {

        redImgMax.setText("");
        orangImgMax.setText("< "+ Float.toString(t3Value)+ "°C");
        greenImgMax.setText("< "+ Float.toString(t2Value)+ "°C");
        sImgMax.setText("< "+ Float.toString(t1Value)+ "°C");
        redImgmin.setText(Float.toString(t3Value)+ "°C <");
        greenImgmin.setText(Float.toString(t1Value)+ "°C <");
        orangImgmin.setText(Float.toString(t2Value)+ "°C <");
        sImgmin.setText("");

//        redImgMaxP.setText("");
//        orangImgMaxP.setText("< "+ Float.toString(p3Value)+ "bar");
//        greenImgMaxP.setText("< "+Float.toString(p2Value)+ "bar");
//        sImgMaxP.setText("< "+ Float.toString(p1Value)+ "bar");
//        redImgminP.setText(Float.toString(p3Value)+ "bar <");
//        greenImgminP.setText(Float.toString(p1Value)+ "bar <");
//        orangImgminP.setText(Float.toString(p2Value)+ "bar <");
//        sImgminP.setText("");

    }
//
//
//    private void addDataToChart(float tempIn, float pressIn) {
//
//        LineData data = mChart2.getData();
//        if (data != null) {
//
//            ILineDataSet setPress = data.getDataSetByIndex(0);
//            ILineDataSet setTemp = data.getDataSetByIndex(1);
//
//
//            // setTension.addEntry(...); // can be called as well
//
//            if (setPress == null) {
//                setPress = createSet("Pression",getResources().getColor(R.color.colorPrimary));
//                data.addDataSet(setPress);
//
//            }
//            if (setTemp == null) {
//                setTemp = createSet("Température",getResources().getColor(R.color.colorAccent));
//                data.addDataSet(setTemp);
//            }
//
//
//            data.addEntry(new Entry(x, (float) tempIn), 0);
//            data.addEntry(new Entry(x, (float) pressIn), 1);
//
//            XAxis xl = mChart2.getXAxis();
//            addCurrentTime();
//            final Date currentTime = getInstance().getTime();
//            xl.setTextColor(getResources().getColor(R.color.colorPrimary));
//            xl.setDrawGridLines(true);
//            xl.setAvoidFirstLastClipping(true);
//            xl.setEnabled(true);
////            xl.setValueFormatter(new IAxisValueFormatter() {
////                @Override
////                public String getFormattedValue(float value, AxisBase axis) {
////                    return timeArraylist.get((int) value); // xVal is a string array
////                }
////
////
////            });
//
//            data.notifyDataChanged();
//            x++;
//            y++;
//
//            // let the chart know it's data has changed
//            mChart.notifyDataSetChanged();
//
//            // limit the number of visible entries
//            mChart.setVisibleXRangeMaximum(8);
//            // mChart.setVisibleYRange(30, AxisDependency.LEFT);
//
//            // move to the latest entry
//            mChart.moveViewToX(data.getEntryCount());
//
//        }
//
//    }
//
//    private void addDatatoChartMonth(float temp, float press) {
//
//        LineData data = mChart2.getData();
//        if (data != null) {
//
////            ILineDataSet setPress = data.getDataSetByIndex(0);
//            ILineDataSet setTemp = data.getDataSetByIndex(0);
//
//
//            // setTension.addEntry(...); // can be called as well
//
//            if (setTemp == null) {
//                setTemp = createSet2("Température",getResources().getColor(R.color.red));
//                data.addDataSet(setTemp);
//
//            }
////            if (setPress == null) {
////                setPress = createSet2("Pression",getResources().getColor(R.color.colorAccent));
////                data.addDataSet(setPress);
////            }
//
//
//            data.addEntry(new Entry(x2, (float) temp), 0);
////            data.addEntry(new Entry(x2, (float) press), 1);
//            XAxis xl = mChart2.getXAxis();
//
//            LimitLine upper_limit = new LimitLine(t3Value, "Max");
//            upper_limit.setLineWidth(1f);
//
//            upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//            upper_limit.setTextSize(10f);
//            upper_limit.setLineColor(getResources().getColor(R.color.red));
//
//            LimitLine lower_limit = new LimitLine(t1Value, "min");
//            lower_limit.setLineWidth(1f);
//
//            lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//            lower_limit.setTextSize(10f);
//            upper_limit.setLineColor(getResources().getColor(R.color.GreenAccent));
//
//            YAxis leftAxis = mChart2.getAxisLeft();
//                // reset all limit lines to avoid overlapping lines
//            leftAxis.removeAllLimitLines();
//            leftAxis.addLimitLine(upper_limit);
//            leftAxis.addLimitLine(lower_limit);
//            leftAxis.setDrawLimitLinesBehindData(true);
//            leftAxis.setEnabled(true);
//
//            xl.setTextColor(getResources().getColor(R.color.colorPrimary));
//            xl.setDrawGridLines(true);
//            xl.setAvoidFirstLastClipping(true);
//            xl.setEnabled(true);
////            xl.setValueFormatter(new IAxisValueFormatter() {
////                @Override
////                public String getFormattedValue(float value, AxisBase axis) {
////                    return lastMonthListX.get((int) value); // xVal is a string array
////                }
////
////
////            });
//
//            data.notifyDataChanged();
//            x2++;
//            y++;
//
//            // let the chart know it's data has changed
//            mChart2.notifyDataSetChanged();
//
//            // move to the latest entry
//            mChart2.moveViewToX(data.getEntryCount());
//
//        }
//
//    }
//
//    private void addDatatoChartMonthNew(float temp, float press) {
//
//        LineData data = mChart2.getData();
//        if (data != null) {
//
//            ILineDataSet setPress = data.getDataSetByIndex(0);
//            ILineDataSet setTemp = data.getDataSetByIndex(0);
//
//
//            // setTension.addEntry(...); // can be called as well
//
//            if (setTemp == null) {
//                setTemp = createSet2("Température",getResources().getColor(R.color.red));
//                data.addDataSet(setTemp);
//
//            }
////            if (setPress == null) {
////                setPress = createSet2("Pression",getResources().getColor(R.color.colorAccent));
////                data.addDataSet(setPress);
////            }
//
//
//            data.addEntry(new Entry(x2, (float) temp), 0);
//            data.addEntry(new Entry(x2, (float) press), 1);
//            XAxis xl = mChart2.getXAxis();
//
//            lastMonthListX.add(getCurrentTime());
//
//            xl.setTextColor(getResources().getColor(R.color.colorPrimary));
//            xl.setDrawGridLines(true);
//            xl.setAvoidFirstLastClipping(true);
//            xl.setEnabled(true);
////            xl.setValueFormatter(new IAxisValueFormatter() {
////                @Override
////                public String getFormattedValue(float value, AxisBase axis) {
////                    return lastMonthListX.get((int) value); // xVal is a string array
////                }
////
////
////            });
//
//            data.notifyDataChanged();
//            x2++;
//            y++;
//
//            // let the chart know it's data has changed
//            mChart2.notifyDataSetChanged();
//
//            // move to the latest entry
//            mChart2.moveViewToX(data.getEntryCount());
//
//        }
//
//    }



//
//    private void addDatatoChartYear(float temp, float press) {
//
//        LineData data = mChart3.getData();
//        if (data != null) {
//
//            ILineDataSet setPress = data.getDataSetByIndex(0);
//            ILineDataSet setTemp = data.getDataSetByIndex(0);
//
//
//            // setTension.addEntry(...); // can be called as well
//
//            if (setTemp == null) {
//                setTemp = createSet2("Température",getResources().getColor(R.color.colorPrimary));
//                data.addDataSet(setTemp);
//
//            }
//            if (setPress == null) {
//                setPress = createSet2("Pression",getResources().getColor(R.color.colorAccent));
//                data.addDataSet(setPress);
//            }
//
//
//            data.addEntry(new Entry(x3, (float) temp), 0);
//            data.addEntry(new Entry(x3, (float) press), 1);
//            XAxis xl = mChart3.getXAxis();
//
//            final Date currentTime = getInstance().getTime();
//            LimitLine upper_limit = new LimitLine(t3Value, "Max");
//            upper_limit.setLineWidth(1f);
//
//            upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//            upper_limit.setTextSize(10f);
//            upper_limit.setLineColor(getResources().getColor(R.color.red));
//
//            LimitLine lower_limit = new LimitLine(t1Value, "min");
//            lower_limit.setLineWidth(1f);
//
//            lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//            lower_limit.setTextSize(10f);
//            upper_limit.setLineColor(getResources().getColor(R.color.GreenAccent));
//
//            YAxis leftAxis = mChart3.getAxisLeft();
//            // reset all limit lines to avoid overlapping lines
//            leftAxis.removeAllLimitLines();
//            leftAxis.addLimitLine(upper_limit);
//            leftAxis.addLimitLine(lower_limit);
//            xl.setTextColor(getResources().getColor(R.color.colorPrimary));
//            xl.setDrawGridLines(true);
//            xl.setAvoidFirstLastClipping(true);
//            xl.setEnabled(true);
////            xl.setValueFormatter(new IAxisValueFormatter() {
////                @Override
////                public String getFormattedValue(float value, AxisBase axis) {
////                    return lastYearListX.get((int) value); // xVal is a string array
////                }
////
////
////            });
//            data.notifyDataChanged();
//            x3++;
//            y++;
//
//            // let the chart know it's data has changed
//            mChart3.notifyDataSetChanged();
//            // move to the latest entry
//            mChart3.moveViewToX(data.getEntryCount());
//
//        }
//
//    }


    public  void addCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        timeArraylist.add(strDate) ;


    }
    public  String getCurrentTime() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);


        return strDate ;
    }


    // fonction pour ouvrir le dialog
    public void openDialog() {
        tempDialog tempDialog = new tempDialog();
        tempDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String t1, String t2, String t3) {

     if(tP==1){
         t3Value= Integer.parseInt(t3);
         t2Value= Integer.parseInt(t2);
         t1Value= Integer.parseInt(t1);
     }
        if(tP==2){
            p3Value= Integer.parseInt(t3);
            p2Value= Integer.parseInt(t2);
            p1Value= Integer.parseInt(t1);
        }


        setUp();
    }

    public String getDate(){

        String theDate;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        theDate= dateFormat.format(date);
        return theDate;
    }
    public String getdateydm(){
        String output;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        output= dateFormat.format(date);
        return output;
    }

    public void startHistoryActivity(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);

    }




    // eviter les erreurs de conversion
    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //    --------------------------

    public String  getConfiguration(){
        String text ;
        float a=t1Value*100;
        float z=t2Value*100;
        float k=t3Value*100; 
        text = a +"*"+z+"*"+k ;
        return text ;
    }

    public void showMode(int t1){

        if(t1==0){
           mRec.setTextColor(getApplication().getResources().getColor(R.color.GreenAccent));
//           mRec.setBackgroundTintList(ColorStateList.valueOf(getApplication().getResources().getColor(R.color.primaryLightColor)));
            mEmet.setBackgroundColor(getApplication().getResources().getColor(R.color.primaryLightColor));
           mEmet.setTextColor(getApplication().getResources().getColor(R.color.primaryLightColor));
            mEmet.setBackgroundColor(getApplication().getResources().getColor(R.color.colorAccent));
//           mEmet.setBackgroundTintList(ColorStateList.valueOf(getApplication().getResources().getColor(R.color.colorAccent)));

        }else {
            mEmet.setTextColor(getApplication().getResources().getColor(R.color.GreenAccent));
            mEmet.setBackgroundColor(getApplication().getResources().getColor(R.color.primaryLightColor));
//            mEmet.setBackgroundTintList(ColorStateList.valueOf(getApplication().getResources().getColor(R.color.primaryLightColor)));
            mRec.setTextColor(getApplication().getResources().getColor(R.color.primaryLightColor));
            mEmet.setBackgroundColor(getApplication().getResources().getColor(R.color.colorAccent));
//            mRec.setBackgroundTintList(ColorStateList.valueOf(getApplication().getResources().getColor(R.color.colorAccent)));

        }


    }


    public void vibration(){

        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);

        long[] pattern = {0, 500, 100};
        v.vibrate(pattern, -1);
    }

    }
