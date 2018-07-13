package com.futronictech.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.futronictech.dao.EntryDao;
import com.futronictech.model.BWStaff_Datum;

import java.util.List;

public class EntryRepository {

    private EntryDao mEntryDao;
    private LiveData<List<BWStaff_Datum>> mEntries;

    public EntryRepository(Application application) {
        EntryRoomDatabase db = EntryRoomDatabase.getDatabase(application.getApplicationContext());
        mEntryDao = db.entryDao();
        mEntries = mEntryDao.getAllEntries();
    }

    public LiveData<List<BWStaff_Datum>> getAllEntries() {
        return mEntries;
    }

    public long insert(BWStaff_Datum entry) {
       new insertAsyncTask(mEntryDao).execute(entry);
       return 0;
    }

    public int delete(BWStaff_Datum... entry) {
       new deleteAsyncTask(mEntryDao).execute(entry);
       return 0;
    }

    public int update(BWStaff_Datum entry) {
        new updateAsyncTask(mEntryDao).execute(entry);
        return 0;
    }


    private static class insertAsyncTask extends AsyncTask<BWStaff_Datum, Void, Long>{

        private EntryDao mAsyncTaskDao;
        long idAdded = 0;

        insertAsyncTask(EntryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Long doInBackground(BWStaff_Datum... entries) {
            return mAsyncTaskDao.insert(entries[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            idAdded = aLong;
        }

    }

    private static class deleteAsyncTask extends AsyncTask<BWStaff_Datum, Void, Integer>{

        private EntryDao mAsyncTaskDao;
        private Integer rowsAffected = 0;

        deleteAsyncTask(EntryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(BWStaff_Datum... entries) {
            // Return the number of rows deleted
            return  mAsyncTaskDao.deleteStaff(entries);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            rowsAffected = integer;
        }
    }

    private static class updateAsyncTask extends AsyncTask<BWStaff_Datum, Void, Integer> {

        private EntryDao mAsyncTaskDao;
        private int rowsUpdated = 0;

        updateAsyncTask(EntryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(BWStaff_Datum... entries) {
            return mAsyncTaskDao.updateStaff(entries[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            rowsUpdated = integer;
        }
    }
}
