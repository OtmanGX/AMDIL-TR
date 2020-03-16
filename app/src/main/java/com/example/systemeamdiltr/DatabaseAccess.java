package com.example.systemeamdiltr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

import static com.example.systemeamdiltr.MainActivity.helplist;
import static com.example.systemeamdiltr.MainActivity.lastMonthList;
import static com.example.systemeamdiltr.MainActivity.lastMonthListP;
import static com.example.systemeamdiltr.MainActivity.lastMonthListX;
import static com.example.systemeamdiltr.MainActivity.lastMonthListXP;
import static com.example.systemeamdiltr.MainActivity.lastYearList;
import static com.example.systemeamdiltr.MainActivity.lastYearListP;
import static com.example.systemeamdiltr.MainActivity.lastYearListX;
import static com.example.systemeamdiltr.MainActivity.lastYearListXP;
import static com.example.systemeamdiltr.MainActivity.pressArraylist;
import static com.example.systemeamdiltr.MainActivity.tempArraylist;
import static com.example.systemeamdiltr.MainActivity.timeArraylist2;

public class DatabaseAccess {
    private SQLiteAssetHelper openHelper ;
    private SQLiteDatabase db;
    private static DatabaseAccess instance ;
    Cursor c= null ;
    static ArrayList<String> pArrayList = new ArrayList<String>();

    private DatabaseAccess(Context context){
        this.openHelper= new DatabaseOpenHelper(context);

    }

    public static DatabaseAccess getInstance(Context context){
        if(instance==null){
            instance = new DatabaseAccess(context);
        }
        return  instance;
    }
    public void open() {
        this.db=openHelper.getWritableDatabase();
    }
    public void close(){
        if(db!=null){
            this.db.close();
        }
    }
    public int getPress(){
            int xx=0;
        c=db.rawQuery("select pression from Table1 ",null);
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            int press = c.getInt(0);
            pressArraylist.add(press);


        }
        return xx ;
    }
    public int getTemp(){
        int xx=0;
        c=db.rawQuery("select temperature from Table1 ",null);
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            int press = c.getInt(0);
            tempArraylist.add(press);


        }
        return xx ;
    }
    public String getDate(){

        c=db.rawQuery("select date from Table1 ",new String[]{});
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            String adress = c.getString(0);
            buffer.append(" "+adress);
            timeArraylist2.add(c.getString(0));


        }
        return buffer.toString();
    }

    public int getAVG() {
        int ty;

        c=db.rawQuery("select avg(temperature) from Table1 ",null);
        if (c.moveToFirst()) {
            ty = c.getInt(0);
        } else {
            ty = 2;
        }

        c.close();
        db.close();

        return ty;
    }
    public int getAVGdaY(int value) {
        int ty;

        c=db.rawQuery("select avg(temperature) from Table1 where day="+value,null);
        if (c.moveToFirst()) {
            ty = c.getInt(0);
        } else {
            ty = 2;
        }

        c.close();
        db.close();

        return ty;
    }
    public int getAVGmonth(int value) {
        int ty;

        c=db.rawQuery("select avg(temperature) from Table1 where mois="+value,null);
        if (c.moveToFirst()) {
            ty = c.getInt(0);
        } else {
            ty = 2;
        }

        c.close();
        db.close();

        return ty;
    }

    public int getLastDay() {
        int ty;

        c=db.rawQuery("select day from Table1 ORDER BY date DESC LIMIT 1 ",null);
        if (c.moveToFirst()) {
            ty = c.getInt(0);
        } else {
            ty = 2;
        }

        c.close();
        db.close();

        return ty;
    }
    public int getLastMonth() {
        int ty;

        c=db.rawQuery("select mois from Table1 ORDER BY date DESC LIMIT 1 ",null);
        if (c.moveToFirst()) {
            ty = c.getInt(0);
        } else {
            ty = 2;
        }

        c.close();
        db.close();

        return ty;
    }



    public void addData(String date,String dateymd,int month,int day,float temp,float press){

        ContentValues values = new ContentValues();
        values.put("date",date);
        values.put("dateymd",dateymd);
        values.put("mois",month);
        values.put("day",day);
        values.put("temperature",temp);
        values.put("pression",press);

        db.insert("Table1",null,values);
        db.close();

    }

    public String getTest(int temp){

        c=db.rawQuery("SELECT temperature  FROM Table1\n" +
                "WHERE date >= date('now','start of month','-1 months') "+temp ,new String[]{});
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            String adress = c.getString(0);
            buffer.append(" "+adress);
            timeArraylist2.add(c.getString(0));


        }
        return buffer.toString();
    }
    public int getCmpTemp(String today){
            int cmp;
        c=db.rawQuery("select count(*) from Table1 where dateymd= '"+today+"'",new String[]{});
        c.moveToFirst();
        cmp = c.getInt(0);
        return cmp+1 ;
    }

    public int getMonthTData(){
        int xx=0;
        c=db.rawQuery("SELECT temperature,date FROM Table1\n" + "WHERE dateymd >= date('now','start of month','-1 month') ",null);
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            int temp = c.getInt(0);
            String date = c.getString(1);
            lastMonthList.add(temp);
            lastMonthListX.add(date);


        }
        return xx;
    }


    public int getMonthPData(){
        int xx=0;
        c=db.rawQuery("SELECT pression ,date FROM Table1\n" + "WHERE dateymd >= date('now','start of month','-1 month') ",null);
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            int temp = c.getInt(0);
            String date = c.getString(1);
            lastMonthListP.add(temp);
            lastMonthListXP.add(date);


        }
        return xx;
    }


    public int getYearTData(){
        int xx=0;
        c=db.rawQuery("SELECT temperature,date FROM Table1\n" + "WHERE dateymd >= date('now','start of month','-1 year') ",null);
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            int temp = c.getInt(0);
            String date = c.getString(1);
            lastYearList.add(temp);
            lastYearListX.add(date);


        }
        return xx;
    }

    public int getYearPData(){
        int xx=0;
        c=db.rawQuery("SELECT pression,date FROM Table1\n" + "WHERE dateymd >= date('now','start of month','-1 year') ",null);
        StringBuffer buffer =new StringBuffer();

        while (c.moveToNext()){
            int temp = c.getInt(0);
            String date = c.getString(1);
            lastYearListP.add(temp);
            lastYearListXP.add(date);


        }
        return xx;
    }





}
