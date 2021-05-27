package com.example.mytodolist.myroomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {ToDoListTable.class}, version = 1, exportSchema = false)
public abstract class MyRoomDatabase extends RoomDatabase{
    public abstract MyDaoInterface myDaoInterface();
    public static MyRoomDatabase mInstance;

    public static MyRoomDatabase getInstance(Context context){
        if (mInstance == null){
            mInstance = Room.databaseBuilder(context, MyRoomDatabase.class, "DatabaseName")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return mInstance;
    }

}
