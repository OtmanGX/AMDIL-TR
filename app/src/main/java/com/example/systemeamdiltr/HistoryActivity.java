package com.example.systemeamdiltr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.systemeamdiltr.db.DatabaseHelper;
import com.example.systemeamdiltr.entities.Temperature;
import com.example.systemeamdiltr.utils.AppExecutors;
import com.example.systemeamdiltr.utils.Time24HoursValidator;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoryActivity extends AppCompatActivity implements CalendarDialogFragment.NoticeDialogListener {

    LineChart mChart ;
    private DatabaseHelper instance;
    private GraphUtils graph;
    Button buttonCalendar;
    Button buttonCalendar2;
    ToggleButton toggleButton;
    EditText editText;
    EditText editText2;
    Spinner spinner;
    Calendar from = getCurrentDay();
    Calendar to = getCurrentDay();
    Calendar dateSelected;

    // To Draw Line chart
    long diff, lastDiff;

    private String dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        buttonCalendar = findViewById(R.id.buttonCalendar);
        buttonCalendar2 = findViewById(R.id.buttonCalendar2);
        toggleButton = findViewById(R.id.datetoggleButton);
        final String[] array = getResources().getStringArray(R.array.dates);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.w("Date selected", array[position]);
                switch (array[position]) {
                    case "Aujourd'hui" :
                        setDate(getCurrentDay(), true);
                        setDateFormat("today");
                        setTemperatures(0, from.getTime().getTime(),0);
                        break;
                    case "Semaine" :
                        setDate(getCurrentWeek(), true);
                        setDateFormat("week");
                        setTemperatures(0, from.getTime().getTime(),0);
                        break;
                    case "Mois":
                        setDate(getCurrentMonth(), true);
                        setDateFormat("month");
                        setTemperatures(0, from.getTime().getTime(),0);
                        break;
                    case "Année":
                        setDate(getCurrentYear(), true);
                        setDateFormat("month");
                        setTemperatures(0, from.getTime().getTime(), 0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        instance = DatabaseHelper.getInstance(this);
        mChart = findViewById(R.id.chart);
        initChart();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                File imagePath = new File(getApplicationContext().getCacheDir(), "images");
                File newFile = new File(imagePath, "image.jpg");
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.systemeamdiltr.fileprovider", newFile);
                if (contentUri != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType()
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    intent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
//                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_STREAM, contentUri);

//                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");

                    startActivity(Intent.createChooser(intent, "Share"));
                }
            }
        });
    }

    public void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation Dialog").setMessage(R.string.delete);
        // Add the buttons
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AppExecutors.getInstance().diskIO().execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                instance.tempDao().clear();
                            }
                        }
                );
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear:
                showConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveGraph() {
        // save bitmap to cache directory
        try {

            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            mChart.getChartBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Calendar getCurrentDay() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        Log.i("Start of this week:" ,cal.getFirstDayOfWeek()+"");
        Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
        return cal;
    }

    public Calendar getCurrentWeek() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Log.i("Start of this week:" ,cal.getFirstDayOfWeek()+"");
        Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
        return cal;
    }

    public Calendar getCurrentMonth() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

// get start of this month in milliseconds
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Log.i("Start of this week:" ,cal.getMinimalDaysInFirstWeek()+"");
        Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
        return cal;
    }

    public Calendar getCurrentYear() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

// get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        Log.i("Start of this week:" ,cal.getFirstDayOfWeek()+"");
        Log.i("... in milliseconds:" , cal.getTimeInMillis()+"");
        return cal;
    }

    public void setDate(Calendar date, boolean begin) {

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
        if (begin) {
            from = date;
            buttonCalendar.setText(dateFormat2.format(from.getTime()));
        } else {
            to = date;
            buttonCalendar2.setText(dateFormat2.format(to.getTime()));
        }

    }

    public void validate_entries(View view) {
        if (!validate_entry(editText)){
            showEntryError(editText);
            return;
        };
        if(!toggleButton.isChecked() && !validate_entry(editText2)) {
            showEntryError(editText2);
            return;
        };

        long maxTime, minTime;

        String [] timeList = editText.getText().toString().split(":");
        from.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeList[0]));
        from.set(Calendar.MINUTE, Integer.parseInt(timeList[1]));
        minTime =  from.getTime().getTime();
        if (minTime>System.currentTimeMillis())
        {
            showEntryError(editText);
            return;
        }
        if (toggleButton.isChecked())
            maxTime = 0;
        else {
            String [] timeList2 = editText2.getText().toString().split(":");
            to.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeList2[0]));
            to.set(Calendar.MINUTE, Integer.parseInt(timeList2[1]));
            maxTime = to.getTime().getTime();
            if (maxTime>System.currentTimeMillis() || minTime>=maxTime)
            {
                showEntryError(editText2);
                return;
            }
        }
        setTemperatures(0,minTime, maxTime);
    }


    public boolean validate_entry(EditText editText) {
        boolean valid = false;
        String time = editText.getText().toString();
        Log.w("edittext", time);
        if (!time.isEmpty()) {
            Time24HoursValidator timeValidator = new Time24HoursValidator();
            valid = timeValidator.validate(time);
            }
        return valid;
    }

    public void showEntryError(EditText editText) {
        editText.setError("Entrer un temps valide");
    }

    public void initChart() {
        graph = new GraphUtils(this, mChart);

    }

    public void setTemperatures(final int n, final long minTime, final long maxTime) {
        AppExecutors.getInstance().diskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        final List<Temperature> temperatures;
                        if (n!=0) {
                        temperatures = instance.tempDao().getTop(n);}
                        else if (maxTime==0){
                            to  = getCurrentDay();
                            temperatures = instance.tempDao().findTemperaturesGreatherthanDate(new Date(minTime));
                        } else {
                            temperatures = instance.tempDao().findTemperaturesBetweenDates(new Date(minTime), new Date(maxTime));
                        }

                        // Set date format
                        int dateDiff = to.get(Calendar.DAY_OF_YEAR)-from.get(Calendar.DAY_OF_YEAR);
                        if (dateDiff>7)
                            setDateFormat("month");
                        else if (dateDiff>=1)
                            setDateFormat("week");
                        else {
                            setDateFormat("today");
                        }
                        if (temperatures.size()>0) {
                            final long firstdate = temperatures.get(0).date.getTime();


                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        graph.initGraph(firstdate, getDateFormat());
                                        for (final Temperature t : temperatures) {
                                            diff = t.date.getTime() - firstdate;
                                            if ((diff - lastDiff) > 10000) {
                                                graph.dataIndex += 4;
                                            }
                                            graph.addEntry(diff, (float) t.value, t.min, t.max, t.normal);
                                            lastDiff = diff;
                                        }
                                    }
                                });
                            }
                        else graph.initGraph(0, getDateFormat());
                    }
                }
        );
    }

    public void showCalendarDialog(View view) {
        CalendarDialogFragment calendarDialogFragment = new CalendarDialogFragment((Button)view);
        calendarDialogFragment.show(getSupportFragmentManager(), "CalendarDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Button button) {
        dateSelected = dateSelected!=null ? dateSelected:getCurrentDay();
        if (button.getId()==R.id.buttonCalendar) {
            setDate(dateSelected, true);
        } else {
            setDate(dateSelected, false);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public void setDate(Calendar calendar) {
        this.dateSelected = calendar;
    }
}
