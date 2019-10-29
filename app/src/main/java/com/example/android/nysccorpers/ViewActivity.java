package com.example.android.nysccorpers;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.nysccorpers.data.NYSCCorperContract.NYSCCORPEREntry;

public class ViewActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
  //edit
    private TextView viewName;
    private TextView viewCall_up_no;
    private TextView viewPhone_no;
    private TextView viewAddress;
    private TextView viewDate_of_birth;
    private TextView viewgender;
    private ImageView viewImage;
    private Uri viewCurrentCorperUri;
    private static final int EXISTING_Corper_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        viewName = findViewById(R.id.view_name);
        viewCall_up_no = findViewById(R.id.view_call_up_no);
        viewPhone_no = findViewById(R.id.view_phone_no);
        viewAddress = findViewById(R.id.view_address);
        viewDate_of_birth = findViewById(R.id.view_date_of_birth);
        viewgender= findViewById(R.id.view_gender);
        viewImage = findViewById(R.id.view_pic);
        Intent intent = getIntent();
        viewCurrentCorperUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_Corper_LOADER, null, this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_view, menu);

        return true;
    }

     private void editCorper(){
        Intent intent = new Intent(ViewActivity.this, EditActivity.class);
        intent.setData(viewCurrentCorperUri);
        startActivity(intent);
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        // Respond to a click on the "Insert dummy data" menu option
        if (item.getItemId() == R.id.edit_info) {
            editCorper();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all nysc attributes, define a projection that contains
        // all columns from the nysc table
        String[] projection = {
                NYSCCORPEREntry._ID,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_NAME,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_CALL_UP_NO,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_PHONE_NUMBER,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_ADDRESS,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_DATE_OF_BIRTH,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_GENDER,
                NYSCCORPEREntry.COLUMN_NYSCCORPER_PIC

        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                viewCurrentCorperUri,         // Query the content URI for the current nysc
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        viewName.setText("");
        viewDate_of_birth.setText("");
        viewAddress.setText("");
        viewCall_up_no.setText("");
        viewPhone_no.setText("");
        viewgender.setText("");
        viewImage.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of nysc attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_NAME);
            int addressColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_ADDRESS);
            int genderColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_GENDER);
            int dateofbirthColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_DATE_OF_BIRTH);
            int phonenoColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_PHONE_NUMBER);
            int callupnoColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_CALL_UP_NO);
            int picColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_PIC);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String dateofbirth=cursor.getString(dateofbirthColumnIndex);
            String phoneno = cursor.getString(phonenoColumnIndex);
            String callupno =  cursor.getString(callupnoColumnIndex);
            String pic=  cursor.getString(picColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            setTitle( getString(R.string.view_name) );

            // Update the views on the screen with the values from the database
            viewName.setText(name);
            viewPhone_no.setText(phoneno);
            viewCall_up_no.setText(callupno);
            viewDate_of_birth.setText(dateofbirth);
            viewAddress.setText(address);
            Glide.with(this)
                    .load(pic)
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewImage);            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (gender) {
                case NYSCCORPEREntry.GENDER_MALE:
                    viewgender.setText("Male");
                    break;
                case NYSCCORPEREntry.GENDER_FEMALE:
                    viewgender.setText("Female");
                    break;
            }
        }
    }


}
