package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.example.mytodolist.myroomdatabase.MyRoomDatabase;
import com.example.mytodolist.myroomdatabase.ToDoListTable;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Button addNewItem;
    private RecyclerView recyclerView;
    public List<ToDoListTable> toDoModeList = new ArrayList<>();
    private MyRoomDatabase myRoomDatabase;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null){
                getSupportActionBar().setTitle("To Do List");
            }
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        addNewItem = findViewById(R.id.addNewItemActivityBtn);
        addNewItem.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, NewItemActivity.class));
            finish();
        });

        myRoomDatabase = MyRoomDatabase.getInstance(getApplicationContext());
        recyclerView = findViewById(R.id.toDoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        collectItems();
    }


    private void collectItems(){
        swipeRefreshLayout.setRefreshing(true);
        toDoModeList = myRoomDatabase.myDaoInterface().collectList();
        RecyclerView.Adapter adapter = new ToDoListRecyclerAdapter(MainActivity.this, toDoModeList, myRoomDatabase);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        collectItems();

    }

}