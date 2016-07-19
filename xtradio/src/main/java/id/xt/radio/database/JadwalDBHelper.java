package id.xt.radio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import id.xt.radio.model.JadwalAcaraItem;
import id.xt.radio.model.JadwalItem;

/**
 * Created by ahmad on 19/07/2016.
 */
public class JadwalDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = "XTRadio";

    private static final String DATABASE_NAME = "xt.db";
    private static final int DATABSE_VERSION = 1;

    public JadwalDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    private SQLiteDatabase dbWrite, dbRead;
    public void attachDB(){
        dbRead = this.getReadableDatabase();
        dbWrite = this.getWritableDatabase();
    }
    public void detachDB(){
        dbRead.close();
        dbWrite.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "Create database");
        String SQL_CREATE_JADWAL_TABLE = "CREATE TABLE "+XTContract.JadwalEntry.TABLE_JADWAL
                +" ("
                + XTContract.JadwalEntry.JADWAL_ID+" INTEGER PRIMARY KEY NOT NULL,"
                + XTContract.JadwalEntry.JADWAL_HARI+ " TEXT NOT NULL, "
                + XTContract.JadwalEntry.JADWAL_ACARA+ " TEXT NOT NULL, "
                + XTContract.JadwalEntry.JADWAL_ACARA_KETERANGAN+ " TEXT NOT NULL, "
                + XTContract.JadwalEntry.JADWAL_ACARA_CLOCK_START+ " INT NOT NULL, "
                + XTContract.JadwalEntry.JADWAL_ACARA_CLOCK_FINISH+ " INT NOT NULL, "
                + XTContract.JadwalEntry.JADWAL_ACARA_PJ+ " TEXT NOT NULL "
                +" );";

        sqLiteDatabase.execSQL(SQL_CREATE_JADWAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ XTContract.JadwalEntry.TABLE_JADWAL);

        onCreate(sqLiteDatabase);
    }

    public void addJadwal(JadwalAcaraItem jadwal){
        ContentValues values = new ContentValues();
        values.put(XTContract.JadwalEntry.JADWAL_HARI, jadwal.getHari());
        values.put(XTContract.JadwalEntry.JADWAL_ACARA, jadwal.getNamaAcara());
        values.put(XTContract.JadwalEntry.JADWAL_ACARA_KETERANGAN, jadwal.getKeteranganAcara());
        values.put(XTContract.JadwalEntry.JADWAL_ACARA_CLOCK_START, jadwal.getStartAcara());
        values.put(XTContract.JadwalEntry.JADWAL_ACARA_CLOCK_FINISH, jadwal.getFinishAcara());
        values.put(XTContract.JadwalEntry.JADWAL_ACARA_PJ, jadwal.getPj());

        dbWrite.insert(XTContract.JadwalEntry.TABLE_JADWAL, null, values);
    }

    public void deleteRecord(){
        dbWrite.execSQL("DELETE FROM "+ XTContract.JadwalEntry.TABLE_JADWAL);
        dbWrite.execSQL("VACUUM");
    }

    public void addJadwalByHari(JadwalItem jadwalItem){
        for(int i=0;i<jadwalItem.getListAcara().size();i++){
            addJadwal(jadwalItem.getListAcara().get(i));
        }
    }

    public JadwalItem getJadwalByHari(String hari){
        JadwalItem jadwalItem = new JadwalItem();
        jadwalItem.setHari(hari);
        jadwalItem.setListAcara(new ArrayList<JadwalAcaraItem>());

        Cursor cr = dbRead.query(XTContract.JadwalEntry.TABLE_JADWAL, new String[]{
                        XTContract.JadwalEntry.JADWAL_ACARA,
                        XTContract.JadwalEntry.JADWAL_ACARA_KETERANGAN,
                        XTContract.JadwalEntry.JADWAL_ACARA_CLOCK_START,
                        XTContract.JadwalEntry.JADWAL_ACARA_CLOCK_FINISH,
                        XTContract.JadwalEntry.JADWAL_ACARA_PJ
                }, XTContract.JadwalEntry.JADWAL_HARI + "=?",
                new String[] { hari }, null, null, null, null);
        if (cr != null){
            if(cr.getCount()>0){
                cr.moveToFirst();
                do{
                    JadwalAcaraItem item = new JadwalAcaraItem();
                    item.setHari(hari);
                    item.setNamaAcara(cr.getString(0));
                    item.setKeteranganAcara(cr.getString(1));
                    item.setStartAcara(cr.getString(2));
                    item.setFinishAcara(cr.getString(3));
                    item.setPj(cr.getString(4));

                    jadwalItem.getListAcara().add(item);
                }while(cr.moveToNext());
            }
        }
        cr.close();
        return jadwalItem;
    }

    public ArrayList<JadwalItem> getJadwal(){
        ArrayList<JadwalItem> items = new ArrayList<JadwalItem>();
        //SQLiteDatabase db = getReadableDatabase("",null);

        items.add(getJadwalByHari("Senin"));
        items.add(getJadwalByHari("Selasa"));
        items.add(getJadwalByHari("Rabu"));
        items.add(getJadwalByHari("Kamis"));
        items.add(getJadwalByHari("Jum'at"));
        return items;
    }
}
