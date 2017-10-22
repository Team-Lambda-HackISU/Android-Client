package com.pewick.hackisugrocerylist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 10/21/2017.
 */

public class ShoppingListActivity extends AppCompatActivity {

    private ArrayList<GroceryItem> shoppingList;
    private int totalCost;
    private TextView totalCostView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        //So, get back from server in previous activity names and prices
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            shoppingList = bundle.getParcelableArrayList("list");
        }

        ListView shoppingListView = (ListView)findViewById(R.id.shopping_list_view);
        ShoppingListAdapter adapter = new ShoppingListAdapter(this, shoppingList);
        shoppingListView.setAdapter(adapter);
        totalCost = 0;
        totalCostView = (TextView)findViewById(R.id.total_cost_view);
        totalCostView.setText("Total cost: $0.00");
    }

    private String getTotalCostString(){
        String str = String.valueOf(totalCost);
        if(str.length() == 1){
            return "0.0"+str;
        } else if(str.length() == 2){
            return "0."+str;
        } else {
            return str.substring(0, str.length() - 2) + "." + str.substring(str.length() - 2);
        }
    }

    public void updateTotalCost(){
        totalCost = 0;
        for(GroceryItem item : shoppingList){
            if(item.isInCart()) {
                totalCost += item.getPrice();
            }
        }
        totalCostView.setText("Total cost: $"+getTotalCostString());
    }
}