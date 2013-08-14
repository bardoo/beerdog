package com.bardo.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bardo.R;
import com.bardo.contentprovider.BeerContentProvider;
import com.bardo.helper.FileHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.bardo.database.BeerTableHelper.BEER_IMAGE;
import static com.bardo.database.BeerTableHelper.BEER_NAME;
import static com.bardo.database.BeerTableHelper.BREWERY;

/**
 */
public class EditBeerActivity extends Activity {

    public static final int CAMERA_REQUEST_CODE = 345;
    public static final String TAG = "EditBeerActivity";

    private Uri imageUri;
    public Uri beerUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_beer);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            beerUri = extras.getParcelable(BeerContentProvider.CONTENT_ITEM_TYPE);
        } else {
            beerUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState.getParcelable(BeerContentProvider.CONTENT_ITEM_TYPE);
        }

        loadSelectedBeer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageUri != null) {
            Bitmap thumbnail = createThumbnail(imageUri);
            ImageView imageView = (ImageView) findViewById(R.id.BeerImage);
            imageView.setImageBitmap(thumbnail);
        }
    }

    public void addNewBeer(View button) {
        final EditText name = (EditText) findViewById(R.id.Name);
        final EditText brewery = (EditText) findViewById(R.id.Brewery);

        ContentValues values = new ContentValues();
        values.put(BEER_NAME, name.getText().toString());
        values.put(BREWERY, brewery.getText().toString());
        values.put(BEER_IMAGE, imageUri != null ? imageUri.getLastPathSegment() : "");

        if (beerUri == null) {
            getContentResolver().insert(BeerContentProvider.CONTENT_URI, values);
        } else {
            getContentResolver().update(beerUri, values, null, null);
        }

        finish();
    }

    public void addSnapshot(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageUri == null || TextUtils.isEmpty(imageUri.toString())) {
            imageUri = FileHelper.getNewImageFileUri();
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // todo create thumbnail?
            } else if (resultCode == RESULT_CANCELED) {
                imageUri = null;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private Bitmap createThumbnail(Uri location) {
        InputStream inputStream = null;
        Bitmap thumbnail = null;
        try {
            inputStream = getContentResolver().openInputStream(location);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = true;
            opts.inSampleSize = 16;
            // todo: some proper scaling??
            thumbnail = BitmapFactory.decodeStream(inputStream, null, opts);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Didn't find expected image at " + location);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Something went wrong when closing the inputstream");
                }
            }
        }
        return thumbnail;
    }

    private void loadSelectedBeer() {
        if (beerUri != null) {
            EditText beerName = (EditText) findViewById(R.id.Name);
            EditText beerBrewery = (EditText) findViewById(R.id.Brewery);

            Button saveButton = (Button) findViewById(R.id.SaveBeerButton);
            saveButton.setText(R.string.new_beer_update_button);

            String[] projection = {BEER_NAME, BREWERY, BEER_IMAGE};
            Cursor cursor = getContentResolver().query(beerUri, projection, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                beerName.setText(cursor.getString(cursor.getColumnIndexOrThrow(BEER_NAME)));
                beerBrewery.setText(cursor.getString(cursor.getColumnIndexOrThrow(BREWERY)));
                String imageName = cursor.getString(cursor.getColumnIndexOrThrow(BEER_IMAGE));
                imageUri = FileHelper.getImageUriFrom(imageName);

                cursor.close();
            }
        }
    }

}