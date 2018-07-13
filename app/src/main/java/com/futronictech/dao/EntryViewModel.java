package com.futronictech.dao;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;


import com.futronictech.data.EntryRepository;
import com.futronictech.model.BWStaff_Datum;

import java.util.List;

public class EntryViewModel extends AndroidViewModel {

    private EntryRepository mRepository;

    private LiveData<List<BWStaff_Datum>> mEntries;

    public EntryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new EntryRepository(application);
        mEntries = mRepository.getAllEntries();
    }

    public LiveData<List<BWStaff_Datum>> getAllEntries() { return mEntries; }

    public long insert(BWStaff_Datum entry) { return mRepository.insert(entry); }

    public int delete(BWStaff_Datum... entry) {return mRepository.delete(entry);}

    public int update(BWStaff_Datum entry) {return mRepository.update(entry);}


}

