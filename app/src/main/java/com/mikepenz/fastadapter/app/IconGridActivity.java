package com.mikepenz.fastadapter.app;

import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.app.items.ExpandableItem;
import com.mikepenz.fastadapter.app.items.IconItem;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.mikepenz.materialize.MaterializeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IconGridActivity extends AppCompatActivity {
    //save our FastAdapter
    private FastItemAdapter fastItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findViewById(android.R.id.content).setSystemUiVisibility(findViewById(android.R.id.content).getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //as we use an icon from Android-Iconics via xml we add the IconicsLayoutInflater
        //https://github.com/mikepenz/Android-Iconics
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.sample_icon_grid);

        //style our ui
        new MaterializeBuilder().withActivity(this).build();

        //create our FastAdapter which will manage everything
        fastItemAdapter = new FastItemAdapter();

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        //init our gridLayoutManager and configure RV
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (fastItemAdapter.getItemViewType(position)) {
                    case R.id.fastadapter_expandable_item_id:
                        return 3;
                    case R.id.fastadapter_icon_item_id:
                        return 1;
                    default:
                        return -1;
                }
            }
        });

        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new SlideDownAlphaAnimator());
        rv.setAdapter(fastItemAdapter);

        //order fonts by their name
        List<ITypeface> mFonts = new ArrayList<>(Iconics.getRegisteredFonts(this));
        Collections.sort(mFonts, new Comparator<ITypeface>() {
            @Override
            public int compare(final ITypeface object1, final ITypeface object2) {
                return object1.getFontName().compareTo(object2.getFontName());
            }
        });

        //add all icons of all registered Fonts to the list
        ArrayList<ExpandableItem> items = new ArrayList<>(Iconics.getRegisteredFonts(this).size());
        for (ITypeface font : mFonts) {
            ExpandableItem expandableItem = new ExpandableItem().withName(font.getFontName());

            ArrayList<IItem> icons = new ArrayList<>();
            for (String icon : font.getIcons()) {
                icons.add(new IconItem().withIcon(font.getIcon(icon)).withSelectable(false));
            }
            expandableItem.withSubItems(icons);

            items.add(expandableItem);
        }

        //fill with some sample data
        fastItemAdapter.add(items);

        //expand one item to make sample look a bit more interesting
        fastItemAdapter.expand(2);

        //restore selections (this has to be done after the items were added
        fastItemAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastItemAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
