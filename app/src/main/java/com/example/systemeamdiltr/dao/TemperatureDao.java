package com.example.systemeamdiltr.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.systemeamdiltr.entities.Temperature;

import java.sql.Date;
import java.util.List;

@Dao
public interface TemperatureDao {
    @Query("SELECT * FROM temperatures order by date")
    List<Temperature> getAll();

    @Query("SELECT * FROM temperatures LIMIT :n")
    List<Temperature> getTop(int n);

    @Query("SELECT * FROM temperatures WHERE id IN (:tmpIds)")
    List<Temperature> loadAllByIds(int[] tmpIds);

    @Query("SELECT * FROM temperatures WHERE date BETWEEN :from AND :to")
    List<Temperature> findTemperaturesBetweenDates(Date from, Date to);

    @Query("SELECT * FROM temperatures WHERE date >= :from")
    List<Temperature> findTemperaturesGreatherthanDate(Date from);

    @Query("DELETE FROM temperatures")
    void clear();

    @Insert
    void insert(Temperature temperature);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Temperature... temperatures);

    @Update
    public void updateTemperatures(Temperature... temperatures);

    @Delete
    void delete(Temperature temperature);

    @Delete
    public void deleteTemperatures(Temperature... temperatures);


}
