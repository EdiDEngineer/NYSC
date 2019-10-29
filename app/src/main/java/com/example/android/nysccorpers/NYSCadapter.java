package com.example.android.nysccorpers;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;

import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.nysccorpers.data.NYSCCorperContract.NYSCCORPEREntry;


public class NYSCadapter extends CursorAdapter  {


    /**
     * Constructs a new {@link.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    NYSCadapter(Context context, Cursor c) {

        super(context, c, 0 /* flags */);

    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml

        return LayoutInflater.from(context).inflate(R.layout.nysc_corper_card, parent, false);
    }

    /**
     * This method binds the nysc data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current nysc can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView card_view_name = view.findViewById(R.id.card_view_name);
        TextView card_view_call_up_no = view.findViewById(R.id.card_view_call_up_no);
        ImageView card_view_pic = view.findViewById(R.id.card_view_pic);
        final ImageButton imageButton = view.findViewById(R.id.edit_deletes);
        // Find the columns of nysc attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_NAME);
        int callupnoColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_CALL_UP_NO);
        int idColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry._ID);
        int picColumnIndex = cursor.getColumnIndex(NYSCCORPEREntry.COLUMN_NYSCCORPER_PIC);
        // Read the nysc attributes from the Cursor for the current nysc
        String corperName = cursor.getString(nameColumnIndex);
        String corperNo = cursor.getString(callupnoColumnIndex);
        long id = cursor.getLong(idColumnIndex);
        String corperpic = cursor.getString(picColumnIndex);
        final Uri thisuri = ContentUris.withAppendedId(NYSCCORPEREntry.CONTENT_URI, id);

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                context.getContentResolver().delete(thisuri, null, null);
                notifyDataSetChanged();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.setData(thisuri);
                context.startActivity(intent);
            }
        });
        // Update the TextViews with the attributes for the current nysc
        card_view_name.setText(corperName);
        card_view_call_up_no.setText(corperNo);
        Glide.with(context)
                .load(corperpic)
                .apply(RequestOptions.circleCropTransform())
                .into(card_view_pic);
    }




/**
 * Click listener for popup menu items
 */

}

