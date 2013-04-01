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
    private String currentBeernameSortOrder = SORT_ORDER_ASC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if (currentBeernameSortOrder.equals(SORT_ORDER_ASC)) {
            currentBeernameSortOrder = SORT_ORDER_DESC;
            menuItem.setIcon(R.drawable.ic_action_sort_asc);

        } else {
            currentBeernameSortOrder = SORT_ORDER_ASC;
            menuItem.setIcon(R.drawable.ic_action_sort_desc);
        }
        BeerListFragment beerListFragment = (BeerListFragment) getFragmentManager().findFragmentById(R.id.beerlist);
        beerListFragment.setNewSortOrderAndReload(currentBeernameSortOrder);
    }

}
