package com.jayway.lab.gradle_lab;

import java.util.Random;

import com.jayway.labs.gradle.supermath.Adder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showAddition();
    }


    private void showAddition() {
		Adder adder = new Adder();
		Random random = new Random();
		int i1 = random.nextInt(100);
		int i2 = random.nextInt(100);
		int i3 = random.nextInt(100);
		int sum = adder.add(i1, i2, i3);
		TextView textView = (TextView) findViewById(R.id.addition);
		textView.setText(i1 + " + " + i2 + " + " + i3 + " = " + sum);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
