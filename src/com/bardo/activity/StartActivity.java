package com.bardo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.bardo.R;

public class StartActivity extends Activity {

    public static final int NEW_BEER_REQ_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_menu, menu);
        return true;
    }


    public void addNewBeer(MenuItem menuItem) {
        Intent newBeerIntent = new Intent(StartActivity.this, EditBeerActivity.class);
        StartActivity.this.startActivityForResult(newBeerIntent, NEW_BEER_REQ_CODE);
    }

}
