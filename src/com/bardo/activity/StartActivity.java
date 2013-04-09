package com.bardo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import com.bardo.R;
import com.bardo.fragment.BeerListFragment;

public class StartActivity extends Activity {

    public static final int NEW_BEER_REQ_CODE = 1;
    public static final String SORT_ORDER_ASC = "asc";
    public static final String SORT_ORDER_DESC = "desc";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // workaround for droid bug
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.start_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.searchBeer).getActionView();
        // todo: legg til lytter for søket

        return true;
    }

    public void addNewBeer(MenuItem menuItem) {
        Intent newBeerIntent = new Intent(this, EditBeerActivity.class);
        StartActivity.this.startActivityForResult(newBeerIntent, NEW_BEER_REQ_CODE);
    }

    public void toggleBeernameSorting(MenuItem menuItem) {
        BeerListFragment beerListFragment = (BeerListFragment) getFragmentManager().findFragmentById(R.id.beerlist);
        if (beerListFragment.getBeerNameSortOrder().equals(SORT_ORDER_ASC)) {
            beerListFragment.setBeerNameSortOrderAndReload(SORT_ORDER_DESC);
            menuItem.setIcon(R.drawable.ic_action_sort_asc);

        } else {
            beerListFragment.setBeerNameSortOrderAndReload(SORT_ORDER_ASC);
            menuItem.setIcon(R.drawable.ic_action_sort_desc);
        }
    }
}
