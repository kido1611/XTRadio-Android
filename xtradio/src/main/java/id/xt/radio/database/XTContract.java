package id.xt.radio.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmad on 19/07/2016.
 */
public class XTContract {
    public static final String CONTENT_AUTHORITY = "id.xt.radio";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class JadwalEntry implements BaseColumns{

        public static final String TABLE_JADWAL = "jadwal";

        public static final String JADWAL_ID = "id";
        public static final String JADWAL_HARI = "hari";
        public static final String JADWAL_ACARA = "acara";
        public static final String JADWAL_ACARA_KETERANGAN = "keterangan";
        public static final String JADWAL_ACARA_CLOCK_START = "startClock";
        public static final String JADWAL_ACARA_CLOCK_FINISH = "finishClocK";
        public static final String JADWAL_ACARA_PJ = "pj";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_JADWAL).build();
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + TABLE_JADWAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + TABLE_JADWAL;

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
