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


public class ReviewRecyclerViewAdapter extends RecyclerView
        .Adapter<ReviewRecyclerViewAdapter
        .DataObjectHolder> {

    private ArrayList<ReviewDataObject> mDataset;
//    private static MyClickListener myClickListener;
    private Activity mActivity;


    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView review;
        TextView user_name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            review = (TextView) itemView.findViewById(R.id.review);
            user_name = (TextView) itemView.findViewById(R.id.user);
        }

    }

//    public void setOnItemClickListener(MyClickListener myClickListener) {
//        this.myClickListener = myClickListener;
//    }

    public ReviewRecyclerViewAdapter(ArrayList<ReviewDataObject> myDataset, Activity activity) {
        mDataset = myDataset;
        this.mActivity = activity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_review_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.review.setText(mDataset.get(position).getReview());
        holder.user_name.setText(mDataset.get(position).getUser_name());

    }

    public void addItem(ReviewDataObject dataObj, int index) {
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

//    public interface MyClickListener {
//        void onItemClick(int position, View v);
//    }

   }