package com.example.ralpuchev.mototracking;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by willo on 30/06/2016.
 */
public class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
    private Context context;
    public DrawerItemClickListener(Context context)
    {
        this.context = context;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context,"Me has tocado",Toast.LENGTH_SHORT);
    }
}
