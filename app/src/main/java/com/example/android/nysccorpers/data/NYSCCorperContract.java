package com.example.android.nysccorpers.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class NYSCCorperContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private NYSCCorperContract() { }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.nysccorpers";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.NYSCCORPERS/NYSCCORPERS/ is a valid path for
     * looking at NYSCCORPER data. content://com.example.android.NYSCCORPERS/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_NYSCCORPERS = "nysccorpers";

    /**
     * Inner class that defines constant values for the NYSCCORPERS database table.
     * Each entry in the table represents a single NYSCCORPER.
     */
    public static final class NYSCCORPEREntry implements BaseColumns {

        /**
         * The content URI to access the NYSCCORPER data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NYSCCORPERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of NYSCCORPERS.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NYSCCORPERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single NYSCCORPER.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NYSCCORPERS;

        /**
         * Name of database table for NYSCCORPERS
         */
        public final static String TABLE_NAME = "nysccorpers";

        /**
         * Unique ID number for the NYSCCORPER (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NYSCCORPER_CALL_UP_NO = "callupno";

        /**
         * Name of the NYSCCORPER.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NYSCCORPER_NAME = "name";

        /**
         * phone number of the NYSCCORPER.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_NYSCCORPER_PHONE_NUMBER = "phonenumber";


        /**
         * Gender of the NYSCCORPER.
         * <p>
         * The only possible values are {@link }, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NYSCCORPER_GENDER = "gender";

        /**
         * address of the NYSCCORPER.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NYSCCORPER_ADDRESS = "address";
        /**
         * Date of the NYSCCORPER.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_NYSCCORPER_DATE_OF_BIRTH = "dateofbirth";

        /**
         * Possible values for the gender of the NYSCCORPER.
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * edit
         */

        public final static String COLUMN_NYSCCORPER_PIC = "Picture";

        public static final int GENDER_MALE = 0;
        public static final int GENDER_FEMALE = 1;

        /**
         * Returns whether or not the given gender is {@link , {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }
}
