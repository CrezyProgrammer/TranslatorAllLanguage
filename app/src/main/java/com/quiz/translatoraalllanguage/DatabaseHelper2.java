package com.quiz.translatoraalllanguage;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DatabaseHelper2 extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    String DB_PATH =null;

    private static String DB_NAME = "Bango.sqlite";
  public static SQLiteDatabase myDataBase;
    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper2(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH="/data/data/"+context.getPackageName()+"/"+"databases/";
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        try {
            String myPath = DB_PATH + DB_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY+SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //return cursor
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        return myDataBase.query("Dictionary", null, null, null, null, null, null);
    }

    public ArrayList<Dictionary> getAllWords() {

        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY+SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        ArrayList<Dictionary> arrayList = new ArrayList<Dictionary>();


        myDataBase = this.getReadableDatabase();
        Cursor cursor = myDataBase.rawQuery("SELECT English, Hindi  FROM Dictionary", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            Dictionary item = new Dictionary(cursor.getString(cursor.getColumnIndex("English")),
                    cursor.getString(cursor.getColumnIndex("Hindi")));
            if (!item.isEmpty()) {
                arrayList.add(item);
            }
            cursor.moveToNext();
        }

        return arrayList;
    }

    public  boolean create(){
        File dbFile = new File(DB_PATH + DB_NAME);
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
            Log.i("123321", "created 178");
          return  true;
        }
        else    {
            Log.i("123321", "created 182");
            return  true;
        }
    }
}