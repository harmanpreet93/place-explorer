package in.placo.placo;



import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class CategoryRecyclerViewAdapter extends RecyclerView
        .Adapter<CategoryRecyclerViewAdapter
        .DataObjectHolder> {

    private ArrayList<CategoryDataObject> mDataset;
//    private static MyClickListener myClickListener;
    private Activity mActivity;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView type;
        TextView address;
        ImageView bgImage;

        public DataObjectHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            type = (TextView) itemView.findViewById(R.id.category_property);
            address = (TextView) itemView.findViewById(R.id.address);
            bgImage = (ImageView) itemView.findViewById(R.id.category_image);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            myClickListener.onItemClick(getPosition(), v);
//        }
    }

//    public void setOnItemClickListener(MyClickListener myClickListener) {
//        this.myClickListener = myClickListener;
//    }

    public CategoryRecyclerViewAdapter(ArrayList<CategoryDataObject> myDataset, Activity activity) {
        mDataset = myDataset;
        this.mActivity = activity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_category_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.categoryName.setText(mDataset.get(position).getCategoryName());
        holder.type.setText(mDataset.get(position).getType());
        holder.address.setText(mDataset.get(position).getAddress());

        AsyncImageLoader loadBgStyleImage = new AsyncImageLoader(mActivity,holder.bgImage);

        Log.v("wtf","iconUrl: " + mDataset.get(position).getIconUrl());
        loadBgStyleImage.execute(mDataset.get(position).getIconUrl());
    }

    public void addItem(CategoryDataObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public class AsyncImageLoader extends AsyncTask<String,Void,Void> {

        private ImageView mImageView;
        private Activity callingActivity;
        private Bitmap imageBitmap = null;

        public AsyncImageLoader(Activity callingActivity, ImageView imageView) {
            this.callingActivity = callingActivity;
            this.mImageView = imageView;
        }

        @Override
        protected Void doInBackground(String... url) {

//            Log.v("wtf", "in doInBackground");

            // Loading image using glide
            try {
                imageBitmap = Glide.
                        with(callingActivity)
                        .load(url[0])
                        .asBitmap()
//                        .thumbnail(0.1f)
                        .dontAnimate()
//                        .error(R.drawable.placeholder)
                        .into(-1, -1)
                        .get();
            }
            catch (InterruptedException | ExecutionException e) {
                // e.printStackTrace();
                Log.e("wtf", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            Log.v("wtf","In onPostExecute");

            if(isCancelled()) {
                Log.v("wtf", "Async task already cancelled");
            }
            if (imageBitmap != null) {
                mImageView.setImageBitmap(imageBitmap);
//                Log.d("wtf", "Image loaded");
            }
            else {
                Log.d("wtf", "Image downloading failed in recyclerView adapter, bitmap still null");
                Bitmap placeHolder = BitmapFactory.decodeResource(callingActivity.getResources(), R.drawable.icon);
                mImageView.setImageBitmap(placeHolder);
            }
            super.onPostExecute(aVoid);
        }
    }
}