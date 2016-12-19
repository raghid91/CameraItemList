package suchitra.example.com.pluralsightdatabaselist;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ListDatabase extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LIST_DATABASE";
    private static final String DATABASE_TABLE = "Todo_List";

    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_URI_PATH = "uriPath";
    private static final String KEY_IS_DONE = "is_done";

    private int taskCount;

    public ListDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + DATABASE_TABLE +"("
                + KEY_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_URI_PATH + "TEXT, "
                + KEY_IS_DONE + " TEXT" + ")";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addItem(TodoItems items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DESCRIPTION, items.getDescription());
        values.put(KEY_URI_PATH, items.getUriPath());
        if(items.getIs_done())
        {
            values.put(KEY_IS_DONE,1);
        }
        else{
            values.put(KEY_IS_DONE, 0);
        }
        db.insert(DATABASE_TABLE, null, values);
        taskCount++;
        db.close();
    }

    public List<TodoItems> getAllTasks() {

        List<TodoItems> todoList = new ArrayList<TodoItems>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TodoItems items = new TodoItems();
                items.setTask_id(cursor.getInt(0));
                items.setDescription(cursor.getString(1));
                items.setUriPath(cursor.getString(2));
                if(cursor.getInt(3) == 0)
                {
                    items.setIs_done(false);
                }
                else
                {
                    items.setIs_done(true);
                }
                todoList.add(items);
            } while (cursor.moveToNext());
        }
        return todoList;
    }

    public void updateTask(TodoItems items) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, items.getDescription());
        values.put(KEY_URI_PATH, items.getUriPath());
        if(items.getIs_done())
        {
            values.put(KEY_IS_DONE, 1);
        }
        else
        {
            values.put(KEY_IS_DONE, 0);
        }

        db.update(DATABASE_TABLE, values, KEY_TASK_ID + " = ?", new String[]{String.valueOf(items.getTask_id())});
        db.close();
    }

    public void deleteTask()
    {
        List<TodoItems> todoList = getAllTasks();
        SQLiteDatabase database = this.getReadableDatabase();
        for(int i = 0; i < todoList.size(); i++)
        {
            TodoItems item = todoList.get(i);
            if(item.getIs_done() == true)
            {
                database.delete(DATABASE_TABLE, KEY_TASK_ID + " = ?",
                        new String[]
                                {String.valueOf(item.getTask_id())});
            }
        }
        database.close();
    }

    public void clearTodo() {
        //list.clear();
        SQLiteDatabase database =  this.getWritableDatabase();
        database.delete(DATABASE_TABLE,null,null);
        database.close();
    }

}
