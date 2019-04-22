package com.example.todoapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.todoapp.data.Contract;
import java.util.Calendar;

public class AddNote extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView time , date ;
    EditText todo, discription;
    FloatingActionButton save ;
    Switch s ;
    int day,month,year,min,hour ;
    String pm_am ;
    Calendar calendar ;
    Uri uri ;
    boolean is_checked ;
    DatePickerDialog datePickerDialog ;
    TimePickerDialog timePickerDialog ;
    NotificationManager notificationManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
         uri = getIntent().getData();
         int id = getIntent().getIntExtra("ID",-1);

         notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (uri==null){
            setTitle("Add ToDo ");
            time.setVisibility(View.GONE);
            date.setVisibility(View.GONE);
        }else{
            setTitle("Edit ToDo ");
            if (id != -1){
                notificationManager.cancel(id);
            }

        }

        todo= findViewById(R.id.note_title);
        discription=findViewById(R.id.the_note);
        save = findViewById(R.id.save_note);


        s= findViewById(R.id.remind_me);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_checked = isChecked ;
                if (isChecked){
                    time.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    getTimeAndDate();
                }else{
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                }
            }
        });

        SaveNote();


        getLoaderManager().initLoader(0,null,this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (uri!=null){
            getMenuInflater().inflate(R.menu.delete_list,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.delete_note:
                deletenote();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletenote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("warning !")
                .setMessage("delete note ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(uri, null, null);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog!=null){
                            dialog.dismiss();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    int event_min,event_hour,event_day,even_month,event_year ;

    public void getTimeAndDate(){
        calendar = Calendar.getInstance();
        hour= calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month= calendar.get(Calendar.MONTH);
        year= calendar.get(Calendar.YEAR);



        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(AddNote.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int updateTime ;
                        event_hour = hourOfDay ;
                        event_min=minute ;
                        if (hourOfDay>=12){
                            pm_am = "PM";
                            updateTime = hourOfDay-12 ;
                        }else {
                            pm_am="AM";
                            updateTime=hourOfDay ;
                        }

                        time.setText(String.format("%02d:%02d %s", updateTime, minute, pm_am));
                    }
                },hour,min,false);

                timePickerDialog.show();
            }
        });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog= new DatePickerDialog(AddNote.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        event_day = dayOfMonth ;
                        even_month = month;
                        event_year = year ;
                        date.setText(String.format("%02d-%02d-%d", dayOfMonth, month+1, year));
                    }
                },year,month,day);
                datePickerDialog.show();

            }
        });

        calendar.clear();
    }


    public void RemindMe(Uri uri){


        String title =null ;
       Cursor cursor= getContentResolver().query(uri,null,null,null,null);
       if (cursor.moveToFirst()){
           title =cursor.getString(cursor.getColumnIndex(Contract.note.NOTE_TITLE));
       }

        ComponentName receiver = new ComponentName(this, RemindReseiver.class);

        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent = new Intent(AddNote.this,RemindReseiver.class);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("title",title);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddNote.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Calendar c = Calendar.getInstance();
        c.set(event_year,even_month,event_day,event_hour,event_min);
        if (c.before(Calendar.getInstance())){
            Toast.makeText(this,"date is already passed can`t remind you ",Toast.LENGTH_SHORT).show();
        }else{

            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis()

                    , pendingIntent);

           // alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
        }


    }


   public void SaveNote() {

       save.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Uri current_uri = null;

               calendar = Calendar.getInstance();
               day = calendar.get(Calendar.DAY_OF_MONTH);
               month= calendar.get(Calendar.MONTH);
               year= calendar.get(Calendar.YEAR);
               hour= calendar.get(Calendar.HOUR_OF_DAY);
               min = calendar.get(Calendar.MINUTE);


               if (!TextUtils.isEmpty(todo.getText())) {
                   String todo_title = todo.getText().toString();
                   String todo_discription = discription.getText().toString();
                   int checkResult = IsCheckedTrue(is_checked);
                   String A_P ;
                   int updateTime ;
                   if (hour>=12){
                       A_P= " PM";
                       updateTime=hour-12;
                   }else {
                       A_P=" AM";
                       updateTime=hour;
                   }

                   String currentDate =String.format("%02d-%02d-%d",day,month+1,year);
                   String currentTime=String.format("%02d:%02d %s",updateTime,min,A_P);

                   ContentValues values = new ContentValues();
                   values.put(Contract.note.NOTE_TITLE, todo_title);
                   values.put(Contract.note.BODY, todo_discription);
                   values.put(Contract.note.REMINDER,checkResult);
                   values.put(Contract.note.TIME,currentTime);
                   values.put(Contract.note.DATE,currentDate);


                   if (uri==null){
                      current_uri = getContentResolver().insert(Contract.note.CONTENT_URI, values);
                       Toast.makeText(AddNote.this,"Saved",Toast.LENGTH_SHORT).show();
                   }else{
                       getContentResolver().update(uri,values,null,null);
                       current_uri=uri ;
                       Toast.makeText(AddNote.this,"Updated ",Toast.LENGTH_SHORT).show();
                   }

                   calendar.clear();
                   finish();
               }else{
                   Toast.makeText(AddNote.this,"no thing todo!",Toast.LENGTH_SHORT).show();
               }

               if (is_checked){
                   Log.e("AddNote","run remind me method");
                   RemindMe(current_uri);
               }
           }


       });
   }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[]= {Contract.note.NOTE_ID, Contract.note.NOTE_TITLE, Contract.note.BODY, Contract.note.REMINDER};
        if (uri==null){
            return null;
        }
        return new CursorLoader(this,uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            todo.setText(data.getString(data.getColumnIndex(Contract.note.NOTE_TITLE)));
            discription.setText(data.getString(data.getColumnIndex(Contract.note.BODY)));
            int ckeck = Integer.parseInt(data.getString(data.getColumnIndex(Contract.note.REMINDER)));
            if (ckeck==Contract.note.REMINDER_TRUE){
                s.setChecked(true);
                time.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
            }else{
                s.setChecked(false);
                time.setVisibility(View.GONE);
                date.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public int IsCheckedTrue(boolean b){
        if (b){
            return Contract.note.REMINDER_TRUE;
        }else{
            return Contract.note.REMINDER_FALSE;
        }
    }
}
