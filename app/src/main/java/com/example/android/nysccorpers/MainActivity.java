package com.example.android.nysccorpers;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.example.android.nysccorpers.data.NYSCCorperContract.NYSCCORPEREntry;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int Corper_LOADER = 0;
    NYSCadapter corpersadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(getString(R.string.app_name));
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);


        // Setup FAB to open EditActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the nysc data
        final ListView nyscListView = findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        nyscListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of nysc data in the Cursor.
        // There is no nysc data yet (until the loader finishes) so pass in null for the Cursor.
        corpersadapter = new NYSCadapter(this, null);
        nyscListView.setAdapter(corpersadapter);
        // Kick off the loader
        getLoaderManager().initLoader(Corper_LOADER, null, this);

    }

    //edit
    private void deleteAllCorpers() {
        getContentResolver().delete(NYSCCORPEREntry.CONTENT_URI, null, null);
        Toast.makeText(this, "Corpers Deleted", Toast.LENGTH_SHORT).show();

    }

    private void sortAllCorpers() {
        String order = NYSCCORPEREntry.COLUMN_NYSCCORPER_NAME + " ASC";
        corpersadapter.swapCursor(getContentResolver().query(NYSCCORPEREntry.CONTENT_URI, null, null, null, order));
        Toast.makeText(this, "Corpers Sorted", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.delete_info:
                deleteAllCorpers();
                return true;
            case R.id.sort_info:
                sortAllCorpers();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                NYSCCORPEREntry._ID,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_NAME,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_CALL_UP_NO,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_PIC
        };

        View indicator = findViewById(R.id.loading_indicator);
        indicator.setVisibility(View.VISIBLE);

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                NYSCCORPEREntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link nyscCursorAdapter} with this new cursor containing updated nysc data

         View indicator = findViewById(R.id.loading_indicator);
        indicator.setVisibility(View.GONE);
        corpersadapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        corpersadapter.swapCursor(null);

    }
}