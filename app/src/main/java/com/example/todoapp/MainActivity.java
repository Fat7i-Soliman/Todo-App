package com.example.todoapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import com.example.todoapp.data.Contract;
import com.example.todoapp.data.NoteHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    FloatingActionButton add_bu ;
    ListView notesList ;
    RelativeLayout empty ;
    NotesCursor notesCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notesList = findViewById(R.id.notes_list);
        empty= findViewById(R.id.empty_view);
        notesList.setEmptyView(empty);

        notesCursor = new NotesCursor(this,null);
        notesList.setAdapter(notesCursor);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(MainActivity.this,AddNote.class);
                Uri uri = ContentUris.withAppendedId(Contract.note.CONTENT_URI,id);
                intent.setData(uri);
                startActivity(intent);
            }
        });



        getLoaderManager().initLoader(0,null,this);
        AddNoteIntent();
    }

    public void AddNoteIntent(){
        add_bu = findViewById(R.id.floatingActionButton_add);
        add_bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddNote.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor c = Search(newText);
                notesCursor.swapCursor(c);
                return true;
            }
        });
        return true ;
    }

    public Cursor Search(String txt){
        NoteHelper helper = new NoteHelper(this);
        Cursor c = helper.SearchNotes(txt);
        return c ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.delete_all:
                deletePets();
                //delete notes\
            case R.id.app_bar_search:
                //search
            case R.id.exit:
                //close app
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletePets(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("warning !")
                .setMessage("delete all pets ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(Contract.note.CONTENT_URI, null, null);
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




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] ={Contract.note.NOTE_ID,Contract.note.NOTE_TITLE, Contract.note.TIME, Contract.note.DATE};
        return new CursorLoader(this, Contract.note.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        notesCursor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        notesCursor.swapCursor(null);
    }
}
