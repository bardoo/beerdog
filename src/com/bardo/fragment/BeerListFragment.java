package com.bardo.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.bardo.R;
import com.bardo.activity.EditBeerActivity;
import com.bardo.activity.StartActivity;
import com.bardo.contentprovider.BeerContentProvider;
import com.bardo.database.BeerTableHelper;
import com.bardo.helper.FileHelper;

import static com.bardo.database.BeerTableHelper.*;

/**
 */
public class BeerListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String beerNameSortOrder;
    private SimpleCursorAdapter cursorAdapter;
    private CursorLoader cursorLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (beerNameSortOrder == null) {
            beerNameSortOrder = StartActivity.SORT_ORDER_ASC;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        String[] from = {BEER_NAME, BREWERY, BEER_IMAGE};
        int[] to = {R.id.row_name, R.id.row_brewery, R.id.beer_thumbnail};
        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.beer_row, null, from, to, 0) {

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView deleteBeerButton = (ImageView) view.findViewById(R.id.deleteBeerImage);
                if (deleteBeerButton != null) {
                    deleteBeerButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            deleteBeer(position);
                        }
                    });
                }
                String imageName = getCursor().getString(getCursor().getColumnIndexOrThrow(BeerTableHelper.BEER_IMAGE));
                ImageView thumb = (ImageView) view.findViewById(R.id.beer_thumbnail);
                if (!TextUtils.isEmpty(imageName)) {
                    thumb.setImageURI(FileHelper.getImageUriFrom(imageName));
                } else {
                    thumb.setImageResource(R.drawable.beer_dog_default);
                }

                return view;
            }
        };

        cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                cursorLoader.setSelection(BEER_NAME + " like '%" + constraint + "%'");
                return cursorLoader.loadInBackground();
            }
        });
        setListAdapter(cursorAdapter);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Intent editBeerIntent = new Intent(getActivity(), EditBeerActivity.class);
        Uri beerUri = Uri.parse(BeerContentProvider.CONTENT_URI + "/" + id);
        editBeerIntent.putExtra(BeerContentProvider.CONTENT_ITEM_TYPE, beerUri);

        startActivity(editBeerIntent);
    }

    public void deleteBeer(int position) {
        long itemId = cursorAdapter.getItemId(position);
        Uri uri = ContentUris.withAppendedId(BeerContentProvider.CONTENT_URI, itemId);
        getActivity().getContentResolver().delete(uri, null, null);
    }

    public String getBeerNameSortOrder() {
        return beerNameSortOrder;
    }

    public void setBeerNameSortOrderAndReload(String sortOrder) {
        beerNameSortOrder = sortOrder;
        getLoaderManager().restartLoader(0, null, this);
    }

    public void filterList(String filterText) {
        cursorAdapter.getFilter().filter(filterText);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BEER_ID, BEER_NAME, BREWERY, BEER_IMAGE};
        cursorLoader = new CursorLoader(getActivity(), BeerContentProvider.CONTENT_URI, projection, null, null, BEER_NAME + " " + beerNameSortOrder);
        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> objectLoader) {
        cursorAdapter.swapCursor(null);
    }

}
