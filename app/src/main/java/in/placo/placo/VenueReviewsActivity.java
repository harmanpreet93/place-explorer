package in.placo.placo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VenueReviewsActivity extends AppCompatActivity {


    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ProgressBar progressBar;

    // Connection detector class
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(this, "No Internet Connection","You don't have internet connection.");
        }

        // get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String venue_id = extras.getString("venue_id");

            if (venue_id != null && !venue_id.equals("")) {
                Log.v("wtf","venue id: " + venue_id);
                new RequestTask().execute(venue_id);
            }
            else {
                showAlertDialog(this,"Venue Name Error","Couldn't find venue ID");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
//                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        // Setting OK Button
        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private class RequestTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String venue_id = params[0];
//            String venue_id = "40a55d80f964a52020f31ee3";
            String PREFIX = "https://api.foursquare.com/v2/venues/";
            String CLIENT_ID = "W4K5CPH3IGHFS3Y13IDRECGXZYTRBB1EDB2XDEBFRLPVY5AR";
            String CLIENT_SECRET = "LS2UY2RY3UP5ZNCBYMY5M4CZWGR3KYACI40T0GSDUVW3MOM0";
            String url = PREFIX + venue_id + "/tips"+
                    "?client_id="+ CLIENT_ID +
                    "&client_secret="+ CLIENT_SECRET +
                    "&v=20140806"+
                    "&m=foursquare"+
                    "&limit=6"+
                    "&sort=recent";

            // Request a string response
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // Result handling
                            // Log.v("wtf",response.toString());
                            setUI(response);


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // Error handling
                    Log.v("wtf","Something went wrong! " + error.toString());
                }
            });

            // Add the request to the queue
            Volley.newRequestQueue(getApplicationContext()).add(jsonRequest);
            return null;
        }

        private void setUI(JSONObject response) {
            try {
                JSONObject r = response.getJSONObject("response");

                if(r!= null) {
                    JSONArray reviewResponse = response.getJSONObject("response").getJSONObject("tips").getJSONArray("items");
                    if(reviewResponse.length() > 0) {
                        final ArrayList<ReviewDataObject> results = new ArrayList<>();
                        String review,user_name="";
                        for (int i=0;i<reviewResponse.length();i++) {
                            ReviewDataObject obj;
                            review = reviewResponse.getJSONObject(i).getString("text");
                            JSONObject userObj = reviewResponse.getJSONObject(i).getJSONObject("user");
                            if(userObj != null && userObj.getString("firstName") != null) {
                                user_name = userObj.getString("firstName");
//                                if(userObj.getString("lastName") != null) {
//                                    user_name = user_name + " " + userObj.getString("lastName");
//                                }
                            }
                            obj = new ReviewDataObject(review,user_name);
                            results.add(i,obj);
                        }

                        progressBar.setVisibility(View.GONE);
                        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.category_list);
                        mRecyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        RecyclerView.Adapter mAdapter = new ReviewRecyclerViewAdapter(results, VenueReviewsActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Log.v("wtf","No results found");
                        TextView noResult = (TextView)findViewById(R.id.error);
                        noResult.setText("No results found");
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Log.v("wtf","No results found");
                    TextView noResult = (TextView)findViewById(R.id.error);
                    noResult.setText("No results found");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}
