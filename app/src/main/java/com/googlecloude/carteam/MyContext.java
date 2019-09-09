package com.googlecloude.carteam;

import android.support.v7.app.AppCompatActivity;

public class MyContext {
    private static AppCompatActivity context = null;

    public static void setContext(AppCompatActivity c){
        context = c;
    }

    public static AppCompatActivity getContext(){
        return context;
    }
}
