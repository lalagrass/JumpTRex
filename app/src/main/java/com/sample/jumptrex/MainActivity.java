package com.sample.jumptrex;

import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {

    private MySurfaceView mySurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySurfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);

        BaseItem.BaseBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sprite100);
        double newWidth = BaseItem.BaseBitmap.getWidth();
        BaseItem.Scale = newWidth / BaseItem.BaseWidth;
    }

    @Override
    protected void onPause() {
        mySurfaceView.Stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.Start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
