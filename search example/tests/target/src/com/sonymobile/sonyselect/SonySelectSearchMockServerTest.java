
package com.sonymobile.sonyselect;

import java.util.concurrent.CountDownLatch;

import javax.net.ssl.HttpsURLConnection;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.sonymobile.sdk.testutils.ResourceUtil;
import com.sonymobile.sdk.testutils.httpd.PathMappingDispatcher;
import com.sonymobile.sonyselect.net.SearchApi;
import com.sonymobile.sonyselect.net.VolleySingelton;
import com.sonymobile.sonyselect.net.domain.RootResponse;
import com.sonymobile.sonyselect.util.StringUtil;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

/**
 *
 */
public class SonySelectSearchMockServerTest extends InstrumentationTestCase {

    private static final String LOG_TAG = SonySelectSearchMockServerTest.class.getCanonicalName();

    private MockWebServer server;

    private PathMappingDispatcher dispatcher;

    private SearchApi api;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        server = new MockWebServer();
        dispatcher = new PathMappingDispatcher();
        server.setDispatcher(dispatcher);
        server.play(8085);

        Context context = getInstrumentation().getTargetContext();
        VolleySingelton.initialize(context, 20);
        api = VolleySingelton.getInstance().getSearchApi();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        server.shutdown();
    }

    public void testGetRootSuccess() throws InterruptedException {
        // Set up some resources
        dispatcher.on("/")//
                .sendResource("/mock/good_root.json");

        String url = "http://localhost:8085/";

        TestListener<RootResponse> listener = new TestListener<RootResponse>();
        api.getRoot(listener, listener, url);

        RootResponse res = listener.getResultData();
        assertNotNull(res);
        assertEquals(false, StringUtil.isEmpty(res.getSearchUrl()));
    }

    public void testGetRootReturnsKillSwitch() throws InterruptedException {
        // Set up some resources

        MockResponse mr = new MockResponse();
        mr.setBody(ResourceUtil.toString("/mock/root_kill_switch.json"));
        mr.setResponseCode(HttpsURLConnection.HTTP_GONE);
        dispatcher.on("/").send(mr);

        String url = "http://localhost:8085/";

        TestListener<RootResponse> listener = new TestListener<RootResponse>();
        api.getRoot(listener, listener, url);

        Log.d(LOG_TAG, "getResult status code: " + listener.getResult());
        VolleyError res = listener.getErrorResponse();
        assertNotNull(res);
        assertNotNull(res.networkResponse);
        assertEquals(HttpsURLConnection.HTTP_GONE, res.networkResponse.statusCode);
//        SearchSuperErrorListener sel = new SearchSuperErrorListener(new SearchResultsActivity());
//        SearchKillSwitchResponse sksr = sel.parseSearchKillSwitchResponse(res);
//        assertTrue(sksr.systemShutdown);

    }

    class TestListener<T> implements Listener<T>, ErrorListener {
        private CountDownLatch latch;

        private int resultCode;

        private T resultData;

        private VolleyError errorResponse;

        public TestListener() {
            latch = new CountDownLatch(1);
        }

        public int getResult() throws InterruptedException {
            latch.await();
            return resultCode;
        }

        public T getResultData() throws InterruptedException {
            latch.await();
            return resultData;
        }

        @Override
        public void onErrorResponse(VolleyError response) {
            Log.d(LOG_TAG, "onErrorResponse");
            errorResponse = response;
            latch.countDown();
        }

        @Override
        public void onResponse(T response) {
            Log.d(LOG_TAG, "onResponse");
            resultData = ((T) response);
            resultCode = 200;
            latch.countDown();
        }

        public VolleyError getErrorResponse() {
            return errorResponse;
        }

    }
}
