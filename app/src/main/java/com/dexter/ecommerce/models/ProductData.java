package com.dexter.ecommerce.models;

import java.util.ArrayList;

public class ProductData {
    public Long id;
    public String name;
    public ArrayList<String> images = new ArrayList<String>();
    public Double price;
    public String status;
    public String desc;
    public int stock;
}
