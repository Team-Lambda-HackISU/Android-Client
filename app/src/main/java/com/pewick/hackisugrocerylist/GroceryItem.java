package com.pewick.hackisugrocerylist;

import android.icu.math.BigDecimal;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Double2;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Chris on 10/21/2017.
 */

public class GroceryItem implements Parcelable{
    private String name;
    private int price;
    private boolean inCart;
    private URL itemImage;

    public GroceryItem(String name, int price) {
        this.name = name;
        this.price = price;
        this.inCart = false;
    }

    public void setURL(String url){
        try {
            itemImage = new URL(url);
            Log.i("setURL", "success");
        } catch (MalformedURLException e){
            e.printStackTrace();
            itemImage = null;
        }
    }

    public URL getItemImage() {
        return itemImage;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    public GroceryItem(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.name = data[0];
        String priceStr = data[1];
        this.price = Integer.valueOf(priceStr);
        if(data[2].equalsIgnoreCase("true")){
            this.inCart = true;
        } else {
            this.inCart = false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        String cartStatus;
        if(this.inCart){
            cartStatus = "true";
        } else{
            cartStatus= "false";
        }
        parcel.writeStringArray(new String[] {this.name,
                String.valueOf(this.price),
                cartStatus});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GroceryItem createFromParcel(Parcel in) {
            return new GroceryItem(in);
        }

        public GroceryItem[] newArray(int size) {
            return new GroceryItem[size];
        }
    };
}
