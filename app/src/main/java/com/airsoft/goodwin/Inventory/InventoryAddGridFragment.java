package com.airsoft.goodwin.Inventory;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.airsoft.goodwin.R;

import java.util.List;

public class InventoryAddGridFragment extends DialogFragment {
    private GridView itemsGrid;
    private ProgressBar loadingSpinner;

    public static InventoryAddGridFragment newInstance() {
        return new InventoryAddGridFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_add_grid, container, false);
        itemsGrid = (GridView) view.findViewById(R.id.inventory_add_grid);
        loadingSpinner = (ProgressBar) view.findViewById(R.id.inventory_add_grid_loading);
        itemsGrid.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);

        return view;
    }

    public void initializeItems(List<InventoryThing> items) {
        itemsGrid.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.GONE);

        InventoryGridAdapter adapter = new InventoryGridAdapter(getActivity());
        itemsGrid.setAdapter(adapter);

        adapter.addAllThings(items);
    }
}
