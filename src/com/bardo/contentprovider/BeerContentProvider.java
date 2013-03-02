package com.bardo.contentprovider;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.bardo.database.BeerTableHelper;
import com.bardo.helper.FileHelper;

import java.io.File;

public class BeerContentProvider extends ContentProvider {
    private BeerTableHelper database;

    // Used for the UriMacher
    private static final int BEERS = 1;
    private static final int BEER_ID = 2;

    private static final String AUTHORITY = "com.bardo.beerdog.provider";
    private static final String BASE_PATH = "beers";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, BEERS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", BEER_ID);
    }

    public static final Uri CONTENT_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/beers";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/beer";


    @Override
    public boolean onCreate() {
        database = new BeerTableHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(BeerTableHelper.BEER_TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case BEERS:
                break;
            case BEER_ID:
                queryBuilder.appendWhere(BeerTableHelper.BEER_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        throw new IllegalStateException("not implemnted");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        switch (uriType) {
            case BEERS:
                id = database.getWritableDatabase().insert(BeerTableHelper.BEER_TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        Cursor cursor;
        switch (uriType) {
            case BEERS:
                cursor = sqlDB.query(BeerTableHelper.BEER_TABLE_NAME, new String[]{BeerTableHelper.BEER_IMAGE}, selection, selectionArgs, null, null, null);
                deleteImages(cursor);
                cursor.close();
                rowsDeleted = sqlDB.delete(BeerTableHelper.BEER_TABLE_NAME, selection, selectionArgs);
                break;
            case BEER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    cursor = sqlDB.query(BeerTableHelper.BEER_TABLE_NAME, new String[]{BeerTableHelper.BEER_IMAGE}, BeerTableHelper.BEER_ID + "=" + id, null, null, null, null);
                    deleteImages(cursor);
                    cursor.close();
                    rowsDeleted = sqlDB.delete(BeerTableHelper.BEER_TABLE_NAME, BeerTableHelper.BEER_ID + "=" + id, null);
                } else {
                    cursor = sqlDB.query(BeerTableHelper.BEER_TABLE_NAME, new String[]{BeerTableHelper.BEER_IMAGE}, BeerTableHelper.BEER_ID + "=" + id+ " and " + selection, selectionArgs, null, null, null);
                    deleteImages(cursor);
                    cursor.close();
                    rowsDeleted = sqlDB.delete(BeerTableHelper.BEER_TABLE_NAME, BeerTableHelper.BEER_ID + "=" + id+ " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection , String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case BEERS:
                rowsUpdated = sqlDB.update(BeerTableHelper.BEER_TABLE_NAME, values, selection, selectionArgs);
                break;
            case BEER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(BeerTableHelper.BEER_TABLE_NAME, values, BeerTableHelper.BEER_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(BeerTableHelper.BEER_TABLE_NAME, values, BeerTableHelper.BEER_ID + "=" + id+ " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void deleteImages(Cursor cursor) {
        if (cursor != null) {
            for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(BeerTableHelper.BEER_IMAGE));
                if (!TextUtils.isEmpty(imageName)) {
                    File imageFile = FileHelper.getImageFileFrom(imageName);
                    if (imageFile != null && imageFile.exists()) {
                        imageFile.delete();
                    }
                }
            }
            getContext().sendBroadcast((new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(FileHelper.getImageFolder()))));
        }

    }


}
