package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.mytodolist.myroomdatabase.MyRoomDatabase;
import com.example.mytodolist.myroomdatabase.ToDoListTable;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class NewItemActivity extends AppCompatActivity {
    private MaterialEditText itemName;
    private TextView toDoDate, toDoTime;
    private MyRoomDatabase myRoomDatabase;
    private Button saveNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener((v) -> {
            Intent intent = new Intent(NewItemActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        itemName = findViewById(R.id.itemName);
        toDoDate = findViewById(R.id.toDoDate);
        toDoTime = findViewById(R.id.toDoTime);

        saveNewItem = findViewById(R.id.saveNewItemBtn);
        myRoomDatabase = MyRoomDatabase.getInstance(getApplicationContext());

        final ToDoListTable toDoTable = new ToDoListTable();

        final SimpleDateFormat dateFormat= new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
        final SimpleDateFormat hFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);

        Button setToDoDate = findViewById(R.id.toDoDateBtn);
        setToDoDate.setOnClickListener((v1) -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewItemActivity.this, (view, year1, month1, dayOfMonth) -> {
                final Calendar c = Calendar.getInstance(Locale.ENGLISH);
                c.set(Calendar.YEAR, year1);
                c.set(Calendar.MONTH, month1);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                final String strDate = dateFormat.format(c.getTime());
                toDoDate.setText(strDate);
                toDoTable.setDate(c.getTime());
            }, year, month, day);
            datePickerDialog.show();
        });

        Button setToDoTime = findViewById(R.id.toDoTimeBtn);
        setToDoTime.setOnClickListener((v1) -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(NewItemActivity.this, R.style.Theme_AppCompat_Dialog, (view, hourOfDay, minute) -> {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                String event_Time = hformate.format(c.getTime());
                toDoTime.setText(event_Time);
                toDoTable.setTime(c.getTime());
            }, hours, minutes, false);
            timePickerDialog.show();
        });

        saveNewItem.setOnClickListener((v)->{
            if (toDoTable.getDate() == null){
                Toast.makeText(NewItemActivity.this, "Select Date", Toast.LENGTH_SHORT).show();
            }else if(toDoTable.getTime() == null){
                Toast.makeText(NewItemActivity.this, "Select Time", Toast.LENGTH_SHORT).show();
            }else{
                String item = itemName.getText().toString();
                if (TextUtils.isEmpty(item)){
                    Toast.makeText(NewItemActivity.this, "Insert Schedule Name", Toast.LENGTH_SHORT).show();
                }else{
                    toDoTable.setItem(item);
                    toDoTable.setCompleted(false);
                    myRoomDatabase.myDaoInterface().insert(toDoTable);
                    Toast.makeText(NewItemActivity.this, "Schedule has been added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewItemActivity.this, MainActivity.class));
                    finish();

                }
            }
        });

    }
}