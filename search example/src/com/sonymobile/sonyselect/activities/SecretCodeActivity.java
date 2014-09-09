
package com.sonymobile.sonyselect.activities;

import com.sonymobile.sonyselect.fragment.PrefsFragment;

import android.app.Activity;
import android.os.Bundle;

public class SecretCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(//
                android.R.id.content, new PrefsFragment()).commit();
    }
}
