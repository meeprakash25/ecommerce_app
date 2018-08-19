package com.dexter.ecommerce;

import android.graphics.drawable.Drawable;

public class GridViewItem
{
    private String title;
    private Drawable image;

    // Empty Constructor
    public GridViewItem()
    {

    }

    // Constructor
    public GridViewItem(String title, Drawable image)
    {
        super();
        this.title = title;
        this.image = image;
    }

    // Getter and Setter Method
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Drawable getImage()
    {
        return image;
    }

    public void setImage(Drawable image)
    {
        this.image = image;
    }


}