package com.sonymobile.sonyselect;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

public class ResourceOverlayTest extends InstrumentationTestCase {

    private Instrumentation instrumentation;

    @Override
    protected void setUp() throws Exception {
        instrumentation = getInstrumentation();
    }

    /**
     * Tests that the resources that may be configured by Sony using resource
     * overlays hasn't changed names.
     */
    public void testOverlayableResourcesHaveCorrectNames() {
        checkResourceNameHasCorrectIdentifier("server_uri", R.string.server_uri);
        checkResourceNameHasCorrectIdentifier("api_key", R.string.api_key);
    }

    public void checkResourceNameHasCorrectIdentifier(String name, int id) {
        Context context = instrumentation.getTargetContext();
        Resources resources = context.getResources();

        int serverUriId = resources.getIdentifier("server_uri", "string", context.getPackageName());
        assertEquals("Resource '" + name + "' doesn't exist or has the wrong id.", R.string.server_uri, serverUriId);
    }

}
