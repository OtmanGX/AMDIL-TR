package com.example.systemeamdiltr.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.Date;


@Entity(tableName = "temperatures")
public class Temperature {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public float min = 10.0f;
    public float max = 30.0f;
    public float normal = 20.0f;
    public Date date;
    public double value;

    public Temperature(float min, float max, float normal, Date date, double value) {
        this.min = min;
        this.max = max;
        this.normal = normal;
        this.date = date;
        this.value = value;
    }
}
