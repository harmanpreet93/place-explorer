package in.placo.placo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
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

public class CategoryActivity extends AppCompatActivity {


    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ProgressBar progressBar;
    RecyclerView.Adapter mAdapter;

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
            String category = extras.getString("category");
            String location = extras.getString("location");

            // send request to endpoint
            if (category != null && !category.equals("")) {
                String categoryId = "";
                switch (category) {
                    case "shop":
                        categoryId = "4bf58dd8d48988d103951735";
                        break;
                    case "restaurant":
                        categoryId = "4d4b7105d754a06374d81259";
                        break;
                    case "night_club":
                        categoryId = "4bf58dd8d48988d116941735";
                        break;
                }

                if (!categoryId.equals("")) {
                    new RequestTask().execute(categoryId,location);
                }
                // else show error dialog if something went wrong
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

    public interface ClickListener {
        void onClick(View view, int position);

    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CategoryActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CategoryActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void openNewActivity(Class activityName,String id) {
        Intent intent = new Intent(this,activityName);
        intent.putExtra("venue_id",id);
        startActivity(intent);
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
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private class RequestTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String categoryId = params[0];
            String location = params[1];
            String PREFIX = "https://api.foursquare.com/v2/venues/search";
            String CLIENT_ID = "W4K5CPH3IGHFS3Y13IDRECGXZYTRBB1EDB2XDEBFRLPVY5AR";
            String CLIENT_SECRET = "LS2UY2RY3UP5ZNCBYMY5M4CZWGR3KYACI40T0GSDUVW3MOM0";
            String url = PREFIX + "?client_id="+ CLIENT_ID +
                    "&client_secret="+ CLIENT_SECRET +
                    "&v=20140806"+
                    "&m=foursquare"+
                    "&ll=" + location +
                    "&limit=10"+
                    "&categoryId="+categoryId +
                    "&radius=10000";

            Log.v("wtf","URL");

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
                JSONArray categoryResponse = response.getJSONObject("response").getJSONArray("venues");

                if(categoryResponse.length() > 0) {
                    final ArrayList<CategoryDataObject> results = new ArrayList<>();
                    String name,id;
                    for (int i=0;i<categoryResponse.length();i++) {
                        CategoryDataObject obj;
                        name = categoryResponse.getJSONObject(i).getString("name");
                        id = categoryResponse.getJSONObject(i).getString("id");
                        if(categoryResponse.getJSONObject(i).getJSONArray("categories").length()>0) {
                            JSONObject icon =  categoryResponse.getJSONObject(i).
                                    getJSONArray("categories").getJSONObject(0).
                                    getJSONObject("icon");

                            String iconUrl = icon.getString("prefix") + "bg_32" + icon.getString("suffix");
                            obj = new CategoryDataObject(id,name,
                                    categoryResponse.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getString("name"),
                                    categoryResponse.getJSONObject(i).getJSONObject("location").getString("formattedAddress"),
                                    iconUrl);
                        }
                        else {
                            obj = new CategoryDataObject(id,name,"",
                                    categoryResponse.getJSONObject(i).getJSONObject("location").getString("formattedAddress")
                                    ,"");
                        }
                        results.add(i,obj);
                    }

                    progressBar.setVisibility(View.GONE);
                    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.category_list);
                    mRecyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mAdapter = new CategoryRecyclerViewAdapter(results, CategoryActivity.this);
                    mRecyclerView.setAdapter(mAdapter);

                    mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            CategoryDataObject obj = results.get(position);
                            openNewActivity(VenueReviewsActivity.class,obj.getId());
                        }

                    }));
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
