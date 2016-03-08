package com.jaragua.avlmobile.persistences;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataSource {

    private static DataSource instance = null;
    private SQLiteDatabase db;

    public DataSource(Context context) {
        DataBaseHelper helper = new DataBaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DataSource getInstance(Context context) {
        if (instance == null) {
            instance = new DataSource(context);
        }
        return instance;
    }

    public ModelInterface create(ModelInterface model) {
        long id = db.insert(model.getModelName(), null, model.getContentValues());
        Cursor cursor = db.query(model.getModelName(), model.getColumns(), model.getModelId() +
                " = ? ", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        model.setFromCursor(cursor);
        cursor.close();
        return model;
    }

    public List<ModelInterface> read(ModelInterface model, String selection, String[] selectionArgs) {
        List<ModelInterface> models = new ArrayList<>();
        Cursor cursor = db.query(model.getModelName(), model.getColumns(), selection, selectionArgs,
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                ModelInterface iteratedModel = (ModelInterface) Class.forName(model.getClass().getName()).newInstance();
                iteratedModel.setFromCursor(cursor);
                models.add(iteratedModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.moveToNext();
        }
        cursor.close();
        return models;
    }

    public boolean delete(ModelInterface model, String selection, String[] selectionArgs) {
        int result = db.delete(model.getModelName(), selection, selectionArgs);
        return (result > 0);
    }

    public boolean update(ModelInterface model, String selection, String[] selectionArgs) {
        int result = db.update(model.getModelName(), model.getContentValues(), selection, selectionArgs);
        return (result > 0);
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
