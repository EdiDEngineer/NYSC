package com.example.android.nysccorpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.nysccorpers.data.NYSCCorperContract.NYSCCORPEREntry;

public class EditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the nysc data loader */
    private static final int EXISTING_Corper_LOADER = 0;

    /** Content URI for the existing nysc (null if it's a new nysc) */
    private Uri editCurrentCorperUri;

    private EditText editDate_of_birth;
    final Calendar myCalendar = Calendar.getInstance();
    private EditText editName;
    private EditText editCall_up_no;
    private EditText editAddress;
    private EditText editPhone_no;
    private ImageView editImage;
    private int editGender ;
    private final static int REQUEST_CAMERA_PERMISSIONS = 1;

    /** Boolean flag that keeps track of whether the nysc has been edited (true) or not (false) */
    private boolean corperHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mnyscHasChanged boolean to true.
     */
    private View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            corperHasChanged = true;
            return false;
        }
    };


   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new nysc or editing an existing one.
        Intent intent = getIntent();
        editCurrentCorperUri = intent.getData();

        // If the intent DOES NOT contain a nysc content URI, then we know that we are
        // creating a new nysc.
        if (  editCurrentCorperUri == null) {
            // This is a new nysc, so change the app bar to say "Add a nysc"
            setTitle(getString(R.string.add_name));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a nysc that hasn't been created yet.)
        } else {
            // Otherwise this is an existing nysc, so change app bar to say "Edit nysc"
            //getLoaderManager().initLoader(EXISTING_nysc_LOADER, null, this);
            getLoaderManager().initLoader(EXISTING_Corper_LOADER, null, this);
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED||(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSIONS);


        }

        // Find all relevant views that we will need to read user input from
        editName = findViewById(R.id.edit_name);
        editCall_up_no = findViewById(R.id.edit_call_up_no);
        editPhone_no = findViewById(R.id.edit_phone_no);
        editAddress = findViewById(R.id.edit_address);
        editDate_of_birth = findViewById(R.id.edit_date_of_birth);
        editImage = findViewById(R.id.edit_pic);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        editName.setOnTouchListener(myTouchListener);
        editCall_up_no.setOnTouchListener(myTouchListener);
        editPhone_no.setOnTouchListener(myTouchListener);
        editAddress.setOnTouchListener(myTouchListener);
        editDate_of_birth.setOnTouchListener(myTouchListener);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();
            }

        };
        editDate_of_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditActivity.this, android.R.style.Theme_Material_Dialog_MinWidth, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }



    private static final int TAKE_PICTURE = 1;
    String currentPhotoPath;

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Corpers\tPicture");
        storageDir.mkdirs();

        File image  = File.createTempFile( imageFileName, ".png", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    public void takePhoto(View view)  {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                File  photo = createImageFile();
                if (photo != null) {
                    Uri photo_uri;
                    photo_uri = FileProvider.getUriForFile(this, "com.example.android.fileproviders", photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,photo_uri);
                    startActivityForResult(intent, TAKE_PICTURE);
                }

            }
            catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error occurred while creating the File", Toast.LENGTH_LONG).show();
                Log.e("Camera", ex.toString());
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE &&resultCode == Activity.RESULT_OK) {
           // if (resultCode == Activity.RESULT_OK) {
                corperHasChanged = true;
                try {
                    Glide.with(this)
                            .load(currentPhotoPath)
                            .apply(RequestOptions.circleCropTransform())

                            .into(editImage);
                    Toast.makeText(this, currentPhotoPath,Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Toast.makeText(this, "Failed to Load Pic", Toast.LENGTH_SHORT).show();
                    Log.e("IMAGE LOAD:", e.toString());
                }

            //}
        }
    }

    //edit
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {

            case R.id.edit_gender_male:
                if (checked)
                    corperHasChanged = true;
                    editGender = NYSCCORPEREntry.GENDER_MALE;
                    break;
            case R.id.edit_gender_female:
                if (checked)
                    corperHasChanged = true;
                    editGender = NYSCCORPEREntry.GENDER_FEMALE;
                // Ninjas rule
                    break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    //edit
    private void saveCorper() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = editName.getText().toString().trim();
        String callupnoString = editCall_up_no.getText().toString().trim();
        String phonenoString = editPhone_no.getText().toString().trim();
        String addressString = editAddress.getText().toString().trim();
        String dateofbirthString = editDate_of_birth.getText().toString().trim();
        String picstring = currentPhotoPath;
        // Check if this is supposed to be a new nysc
        // and check if all the fields in the editor are blank
        if (editCurrentCorperUri == null &&(
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(callupnoString) ||
                TextUtils.isEmpty(phonenoString) ||
                TextUtils.isEmpty(addressString)||
                TextUtils.isEmpty(dateofbirthString)||(editGender == -1))) {
            // Since no fields were modified, we can return early without creating a new nysc.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and nysc attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_NAME, nameString);
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_PHONE_NUMBER, phonenoString);
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_GENDER, editGender);
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_DATE_OF_BIRTH, dateofbirthString);
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_CALL_UP_NO, callupnoString);
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_ADDRESS, addressString);
        values.put(NYSCCORPEREntry.COLUMN_NYSCCORPER_PIC, picstring);

        // If the weight is not provided by the user, don't try to parse the string into an

        // Determine if this is a new or existing nysc by checking if mCurrentnyscUri is null or not
        if (editCurrentCorperUri == null) {
            // This is a NEW nysc, so insert a new nysc into the provider,
            // returning the content URI for the new nysc.
            Uri newUri = getContentResolver().insert(NYSCCORPEREntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error with saving Corper",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this,"Corper Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            // Otherwise this is an EXISTING nysc, so update the nysc with content URI: mCurrentnyscUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentnyscUri will already identify the correct row in the database that
            // we want to modify.

                int rowsAffected = getContentResolver().update(editCurrentCorperUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating Corper",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this,"Corper updated",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save_info:
                // Save nysc to database
                saveCorper();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case android.R.id.home:
                // If the nysc hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!corperHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * This method is called when the back button is pressed.
*/
     @Override
     public void onBackPressed() {
     // If the nysc hasn't changed, continue with handling back button press
      if (!corperHasChanged) {
     super.onBackPressed();
     return;
     }

     // Otherwise if there are unsaved changes, setup a dialog to warn the user.
     // Create a click listener to handle the user confirming that changes should be discarded.
     DialogInterface.OnClickListener discardButtonClickListener =
     new DialogInterface.OnClickListener() {
     @Override
     public void onClick(DialogInterface dialogInterface, int i) {
     // User clicked "Discard" button, close the current activity.
     finish();
     }
     };

     // Show dialog that there are unsaved changes
     showUnsavedChangesDialog(discardButtonClickListener);
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
                editCurrentCorperUri,         // Query the content URI for the current nysc
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
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
            String pic =  cursor.getString(picColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            setTitle(getString(R.string.edit_name));

            // Update the views on the screen with the values from the database
            editName.setText(name);
            editPhone_no.setText(phoneno);
            editCall_up_no.setText(callupno);
            editDate_of_birth.setText(dateofbirth);
            editAddress.setText(address);
            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            Glide.with(this)
                    .load(pic)
                    .apply(RequestOptions.circleCropTransform())
                    .into(editImage);
            RadioButton radiobutton;
            editGender = gender;
            currentPhotoPath =  pic;
            switch (gender) {
                case NYSCCORPEREntry.GENDER_MALE:
                   radiobutton= findViewById(R.id.edit_gender_male);

                   radiobutton.setChecked (true);
                    break;
                case NYSCCORPEREntry.GENDER_FEMALE:
                    radiobutton = findViewById(R.id.edit_gender_female);
                    radiobutton.setChecked (true);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        editName.setText("");
        editDate_of_birth.setText("");
        editAddress.setText("");
        editCall_up_no.setText("");
        editPhone_no.setText("");
        editImage.setImageResource(R.mipmap.ic_launcher);
        }

    private void showUnsavedChangesDialog(
     DialogInterface.OnClickListener discardButtonClickListener) {
     // Create an AlertDialog.Builder and set the message, and click listeners
     // for the postivie and negative buttons on the dialog.
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setMessage("Discard your changes and quit editing");
     builder.setPositiveButton("discard", discardButtonClickListener);
     builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
     public void onClick(DialogInterface dialog, int id) {
     // User clicked the "Keep editing" button, so dismiss the dialog
     // and continue editing the nysc.
     if (dialog != null) {
     dialog.dismiss();
     }
     }
     });

     // Create and show the AlertDialog
     AlertDialog alertDialog = builder.create();
     alertDialog.show();
     }

     private void updateLabel() {
     String myFormat = "dd/MM/yy"; //In which you need put here
     SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
     editDate_of_birth.setText(sdf.format(myCalendar.getTime()));
     }

}
