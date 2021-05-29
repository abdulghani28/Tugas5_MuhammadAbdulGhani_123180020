package com.example.mytodolist;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodolist.myroomdatabase.MyRoomDatabase;
import com.example.mytodolist.myroomdatabase.ToDoListTable;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ToDoListRecyclerAdapter extends RecyclerView.Adapter<ToDoListRecyclerAdapter.MyViewHolder> {
    Context context;
    List<ToDoListTable> toDoListTableList;
    MyRoomDatabase myRoomDatabase;
    AlertDialog alertDialog;
    public ToDoListRecyclerAdapter(Context context, List<ToDoListTable> toDoModeList, MyRoomDatabase myRoomDatabase) {
        this.toDoListTableList = toDoModeList;
        this.myRoomDatabase = myRoomDatabase;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoListTable toDoModel = toDoListTableList.get(position);
        holder.toDoItem.setText(toDoModel.getItem());
        final SimpleDateFormat dateFormat= new SimpleDateFormat("yyy-MM-dd", Locale.ENGLISH);
        final SimpleDateFormat hFormat = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
        final String strDate = dateFormat.format(toDoModel.getDate());
        holder.toDoDate.setText(strDate);
        String event_Time = hFormat.format(toDoModel.getTime());
        holder.toDoTime.setText(event_Time);
        holder.delete.setOnClickListener((v) -> {
            myRoomDatabase.myDaoInterface().delete(toDoModel);
            toDoListTableList = myRoomDatabase.myDaoInterface().collectList();
            notifyDataSetChanged();
        });
        holder.complete.setChecked(toDoModel.isCompleted());

        if (toDoModel.isCompleted()){
            holder.toDoItem.setPaintFlags(holder.toDoItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.toDoItem.setPaintFlags(0);
        }

        holder.setItemClickListener((new MyOnClickListener() {
            @Override
            public void onClickListener(View view, int itemPosition) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()){
                    toDoModel.setCompleted(true);
                }else{
                    toDoModel.setCompleted(false);
                }
                myRoomDatabase.myDaoInterface().update(toDoModel);
                myRoomDatabase.myDaoInterface().collectList();
                notifyDataSetChanged();
            }
        }));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View nView = LayoutInflater.from(context).inflate(R.layout.updateitem_layout, null);
                final EditText itemName = nView.findViewById(R.id.itemName);
                itemName.setText(toDoModel.getItem());
                final TextView toDoDate = nView.findViewById(R.id.toDoDate);
                toDoDate.setText(dateFormat.format(toDoModel.getDate()));
                final TextView toDoTime = nView.findViewById(R.id.toDoTime);
                toDoTime.setText(hFormat.format(toDoModel.getTime()));

                Button setToDoDate = nView.findViewById(R.id.toDoDateBtn);
                setToDoDate.setOnClickListener((v1) -> {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    int year = calendar.get(java.util.Calendar.YEAR);
                    int month = calendar.get(java.util.Calendar.MONTH);
                    int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
                        final Calendar c = Calendar.getInstance(Locale.ENGLISH);
                        c.set(Calendar.YEAR, year1);
                        c.set(Calendar.MONTH, month1);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        final String strDate = dateFormat.format(c.getTime());
                        toDoDate.setText(strDate);
                        toDoModel.setDate(c.getTime());
                    }, year, month, day);
                    datePickerDialog.show();
                });

                Button setToDoTime = nView.findViewById(R.id.toDoTimeBtn);
                setToDoTime.setOnClickListener((v1) -> {
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutes = calendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.Theme_AppCompat_Dialog, (view, hourOfDay, minute) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                        String event_Time = hformate.format(c.getTime());
                        toDoTime.setText(event_Time);
                        toDoModel.setTime(c.getTime());
                    }, hours, minutes, false);
                    timePickerDialog.show();
                });

                Button updateItem = nView.findViewById(R.id.saveNewItemBtn);
                updateItem.setOnClickListener((v1) ->  {
                    if (toDoModel.getDate() == null){
                        Toast.makeText(context, "Select Date", Toast.LENGTH_SHORT).show();
                    }else if (toDoModel.getTime() == null){
                        Toast.makeText(context, "Select Time", Toast.LENGTH_SHORT).show();
                    }else{
                        String item = itemName.getText().toString();
                        if (TextUtils.isEmpty(item)){
                            Toast.makeText(context, "Insert Schedule Name", Toast.LENGTH_SHORT).show();
                        }else{
                            toDoModel.setItem(item);
                            myRoomDatabase.myDaoInterface().collectList();
                            myRoomDatabase.myDaoInterface().update(toDoModel);
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    }
                });
                builder.setView(nView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return toDoListTableList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView toDoItem, toDoDate, toDoTime;
        Button edit, delete;
        CheckBox complete;
        private MyOnClickListener myOnClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            toDoItem = itemView.findViewById(R.id.toDoItem);
            toDoDate = itemView.findViewById(R.id.toDoDate);
            toDoTime = itemView.findViewById(R.id.toDoTime);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            complete = itemView.findViewById(R.id.complete);
            complete.setOnClickListener(this);
        }

        void setItemClickListener(MyOnClickListener myOnClickListener){
            this.myOnClickListener = myOnClickListener;

        }

        @Override
        public void onClick(View v) {
            this.myOnClickListener.onClickListener(v, getLayoutPosition());
        }
    }
}
