package com.example.systemeamdiltr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import android.os.Handler;

import android.os.IBinder;
import android.os.Message;
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

import com.felhr.usbserial.UsbSerialDevice;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import com.github.mikephil.charting.charts.LineChart;


public class MainActivity extends AppCompatActivity implements tempDialog.ExampleDialogListener {

    //Connection Declaration.
    private String dataStock="";
    private Physicaloid mPhysicaloid;
    private byte[] buf ;
    private Thread thread;
    private Handler mHandler2 = new Handler();
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

    // Serial
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    public final String ACTION_USB_PERMISSION = "confoosball.lmu.mff.confoosball.USB_PERMISSION";

    // new
    private UsbService usbService;
    private MyHandler mHandler;
    String data = "";


    // on creat ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new MyHandler((MainActivity) this);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        setFilters();
        startService(UsbService.class, usbConnection, null);
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

//        connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mPhysicaloid = new Physicaloid(getApplicationContext());
//                mPhysicaloid.setBaudrate(9600);
//                connexion++;
//                if(connexion<3){
//                    if(mPhysicaloid.open()) {
//                        if(connexion==2){
//                            connect.setText("Fermer la connexion");
//                            connect.setTextColor(getApplication().getResources().getColor(R.color.GreenAccent));
//                        }
//
//                        mPhysicaloid.addReadListener(new ReadLisener() {
//                            @Override
//                            public void onRead(int size) {
//
//                                buf = new byte[size];
//
//                                mPhysicaloid.read(buf, size);
//
//
//                                for(int i=0;i<size;i++)
//                                {
//                                    char a=(char)buf[i];
//
//                                    dataStock += a;
//                                    if(a=='F' )
//                                    {
//                                        final String data[] = dataStock.split(";",-2);
//                                        if(data[0].indexOf("S")>1){
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    affectation(data[1],data[2],data[3]) ;
//                                                }
//                                            });
//                                        }
//                                        dataStock="";
//                                    }
//                                }
//                            }
//                        });
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Cannot open", Toast.LENGTH_LONG).show();
//                    }
//                }
//                if(connexion==3){
//                    if(mPhysicaloid.close()) {
//                        mPhysicaloid.clearReadListener();
//                    }
//                    connect.setText("Etablir la connexion");
//                    connect.setTextColor(getApplication().getResources().getColor(R.color.primaryLightColor));
//                    connexion=1;
//                }
//            }
//        });

        // Température Configuration
//        pConfig = (Button) findViewById(R.id.configPbtn) ;
        tConfig = (Button) findViewById(R.id.configTbtn);
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
        try {
            unregisterReceiver(broadcastReceiver);
            unbindService(usbConnection);
        } catch (Exception e) {}

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
        graph = new GraphUtils(this, mChart2, 2000, 5000);
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
                for (int i = 0; i < 200; i++) {

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
        DecimalFormat df = new DecimalFormat(".##");
//        final  float tempF  = Float.valueOf(String.format("%.2f", tempInit));
        final  float tempF = Float.valueOf(df.format(tempInit).replace(',','.'));
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
        mHandler2.post(new Runnable() {
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
        orangImgMax.setText("< " + Float.toString(t3Value) + "°C");
        greenImgMax.setText("< " + Float.toString(t2Value) + "°C");
        sImgMax.setText("< " + Float.toString(t1Value) + "°C");
        redImgmin.setText(Float.toString(t3Value) + "°C <");
        greenImgmin.setText(Float.toString(t1Value) + "°C <");
        orangImgmin.setText(Float.toString(t2Value) + "°C <");
        sImgmin.setText("");
    }

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

    @Override
    protected void onResume() {
//        setFilters();  // Start listening notifications from UsbService
//        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//        unbindService(usbConnection);
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(broadcastReceiver, filter);
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        String dataStock = "";
        String data_split[] ;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            data += (String) msg.obj;
                            int index = data.indexOf('S');
                            if (index>=0 && data.indexOf('F')>index) {
                                dataStock = data.substring(index);
                                data_split = dataStock.split(";");
                                affectation(data_split[1],data_split[2],data_split[3]);
                                data = "";
                            };
                        }
                    });
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}