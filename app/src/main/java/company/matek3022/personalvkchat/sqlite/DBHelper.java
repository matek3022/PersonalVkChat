package company.matek3022.personalvkchat.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "dialogsDb";

    //dialogs tables
    public static final String TABLE_DIALOGS = "dialogs";
    public static final String TABLE_USERS = "users";

    //friends tables
    public static final String TABLE_FRIENDS = "friends";

    //messages tables
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_USERS_IN_MESSAGES = "usersInMessages";


    public static final String KEY_ID = "_id";
    public static final String KEY_ID_USER = "id";
    public static final String KEY_TIME_MESSAGES = "time";
    public static final String KEY_ID_DIALOG = "dialogId";
    public static final String KEY_OBJ = "json";

    private static DBHelper instance;


    public static void init(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
    }

    public static DBHelper getInstance() {
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_DIALOGS + "(" + KEY_ID
                + " integer," + KEY_OBJ + " text," + KEY_ID_USER + " integer" + ")");
        db.execSQL("create table " + TABLE_USERS + "(" + KEY_ID
                + " integer," + KEY_OBJ + " text," + KEY_ID_USER + " integer" + ")");
        db.execSQL("create table " + TABLE_FRIENDS + "(" + KEY_ID
                + " integer," + KEY_OBJ + " text," + KEY_ID_USER + " integer" + ")");
        db.execSQL("create table " + TABLE_MESSAGES + "(" + KEY_ID
                + " integer," + KEY_OBJ + " text," + KEY_ID_DIALOG + " integer," + KEY_TIME_MESSAGES + " integer" + ")");
        db.execSQL("create table " + TABLE_USERS_IN_MESSAGES + "(" + KEY_ID
                + " integer," + KEY_OBJ + " text," + KEY_ID_DIALOG + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DIALOGS);
        db.execSQL("drop table if exists " + TABLE_USERS);
        db.execSQL("drop table if exists " + TABLE_FRIENDS);
        db.execSQL("drop table if exists " + TABLE_MESSAGES);
        db.execSQL("drop table if exists " + TABLE_USERS_IN_MESSAGES);
        onCreate(db);
    }
}
