package com.bardo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.bardo.R;

public class StartActivity extends FragmentActivity {

    public static final int NEW_BEER_REQ_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void addNewBeer(View view) {
        Intent newBeerIntent = new Intent(StartActivity.this, EditBeerActivity.class);
        StartActivity.this.startActivityForResult(newBeerIntent, NEW_BEER_REQ_CODE);
    }

}
