package com.futronictech.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.futronictech.model.BWStaff_Datum;

import java.util.List;

@Dao
public interface EntryDao {

    @Query("SELECT * FROM staff_database")
    LiveData<List<BWStaff_Datum>> getAllEntries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BWStaff_Datum prescriptions);

    @Delete
    int deleteStaff(BWStaff_Datum... prescriptions);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateStaff(BWStaff_Datum prescription);

}
