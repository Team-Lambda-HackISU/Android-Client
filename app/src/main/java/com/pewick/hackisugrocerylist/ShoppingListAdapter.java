package com.pewick.hackisugrocerylist;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Chris on 10/21/2017.
 */

public class ShoppingListAdapter extends ArrayAdapter<GroceryItem> {

    private Activity activity;
    private ArrayList<GroceryItem> data;

    public ShoppingListAdapter(Activity activity, ArrayList<GroceryItem> data){
        super(activity, R.layout.list_item_shopping, data);
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final GroceryItem item = this.data.get(position);
        final boolean isInCart = item.isInCart();

        View cellView = convertView;
        GrocerItemVH vh;

        if(convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            cellView = inflater.inflate(R.layout.list_item_shopping, null);

            vh = new GrocerItemVH();
            vh.name = (TextView) cellView.findViewById(R.id.item_name);
            vh.price = (TextView) cellView.findViewById(R.id.item_price);
            vh.itemImage = (ImageView) cellView.findViewById(R.id.item_image);
            vh.addRemoveCart = (ImageView) cellView.findViewById(R.id.add_remove_cart);

            cellView.setTag(vh);
        } else{
            vh = (GrocerItemVH) cellView.getTag();
        }

        vh.name.setText(item.getName());
        vh.price.setText(getCostString(item.getPrice()));

        if(item.isInCart()){
            vh.itemImage.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
            vh.itemImage.setColorFilter(ContextCompat.getColor(activity,R.color.black));
            vh.addRemoveCart.setImageResource(R.drawable.ic_remove_shopping_cart_black_24dp);
        } else{
            vh.itemImage.setImageResource(R.drawable.ic_local_pizza_black_24dp);
//            vh.itemImage.setImageDrawable(loadImageFromWebOperations(item.getItemImage()));
            vh.itemImage.setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary));
            vh.addRemoveCart.setImageResource(R.drawable.ic_add_shopping_cart_black_24dp);
        }

        vh.addRemoveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInCart){
                    //Remove
                    item.setInCart(false);
                } else{
                    item.setInCart(true);
                }
                notifyDataSetChanged();
                ((ShoppingListActivity)activity).updateTotalCost();
            }
        });

        return cellView;
    }

    public static Drawable loadImageFromWebOperations(URL url) {
        try {
            InputStream is = (InputStream) url.getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private String getCostString(int price){
        if(price == 0){
            return "Unknown Price";
        }
        String str = String.valueOf(price);
        if(str.length() == 1){
            return "$0.0"+str;
        } else if(str.length() == 2){
            return "$0."+str;
        } else {
            return "$"+str.substring(0, str.length() - 2) + "." + str.substring(str.length() - 2);
        }
    }

    private static class GrocerItemVH {
        TextView name;
        TextView price;
        ImageView itemImage;
        ImageView addRemoveCart;
    }
}