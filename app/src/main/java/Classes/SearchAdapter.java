package Classes;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.paranoidandroid.navigationbar.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;

import Interfaces.AsyncResponse;
import Interfaces.PlayerCallback;
import appClasses.AppInfo;
import appClasses.AsyncCode;
import appMethods.AsyncRequest;
import appMethods.DateToString;

/**
 * Created by YoAtom on 12/20/2016.
 */

public class SearchAdapter extends ArrayAdapter<Post> implements AsyncResponse {
    private PlayerCallback callback;
    private ArrayList<Post> list;
    private User globalUser;
    //HashMap<Integer, Integer> queryLikes = new HashMap<>();

    public SearchAdapter(Context context, ArrayList<Post> urls, User globalUser) {
        super(context, 0, urls);
        list = urls;
        this.globalUser = globalUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Post postTmp = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_search, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.position = position;
            viewHolder.rowTitle = (TextView) convertView.findViewById(R.id.rowTitle);
            viewHolder.rowAuthor = (TextView) convertView.findViewById(R.id.rowAuthor);
            viewHolder.imView = (ImageButton) convertView.findViewById(R.id.rowImage);
            viewHolder.rowRate = (TextView) convertView.findViewById(R.id.rowRate);
            viewHolder.rowRate = (TextView) convertView.findViewById(R.id.rowRate);
            viewHolder.button = (ImageView) convertView.findViewById(R.id.favB);
            viewHolder.repostCount = (TextView) convertView.findViewById(R.id.repostCount);
            viewHolder.buttonRep = (ImageButton) convertView.findViewById(R.id.repostB);

            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        final ViewHolder viewHolderFinal = viewHolder;

        if(postTmp.isLiked() == true) {
            viewHolder.button.setEnabled(false);
            viewHolder.button.setImageDrawable( ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_pink_24dp));
            viewHolder.rowRate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        else {
            viewHolder.button.setEnabled(true);
            viewHolder.button.setImageDrawable( ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp));
            viewHolder.rowRate.setTextColor(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));
        }

        if(postTmp.isPosted() == true) {
            viewHolder.buttonRep.setEnabled(false);
            viewHolder.buttonRep.setImageDrawable( ContextCompat.getDrawable(getContext(), R.drawable.ic_insert_link_pink_24dp));
            viewHolder.repostCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        else {
            viewHolder.buttonRep.setEnabled(true);
            viewHolder.buttonRep.setImageDrawable( ContextCompat.getDrawable(getContext(), R.drawable.ic_insert_link_black_24dp));
            viewHolder.repostCount.setTextColor(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));
        }


        viewHolder.rowTitle.setText(postTmp.getTitle());
        viewHolder.rowAuthor.setText(postTmp.getSinger());
        viewHolder.rowRate.setText(String.valueOf(postTmp.getLikesCount()));
        viewHolder.repostCount.setText(String.valueOf(postTmp.getRepCounts()));


        viewHolderFinal.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewHolderFinal.button.setEnabled(false);
                likeClick(postTmp.getId(), globalUser.getId(), viewHolderFinal, postTmp);
            }
        });

        viewHolderFinal.buttonRep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewHolderFinal.buttonRep.setEnabled(false);
                postClick(postTmp.getId(), globalUser.getId(), viewHolderFinal, postTmp);
            }
        });

        viewHolder.imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.playPressed(postTmp, position, list);
            }
        });

        ImageButton contentImage = (ImageButton) convertView.findViewById(R.id.rowImage);


        //Picasso.with(getContext()).load(postTmp.getHeaderImage()).into(viewHolder.imView);
        Picasso.with(getContext()).load(postTmp.getHeaderImage()).into(contentImage);


        return convertView;

    }

    private void likeClick(int id, int uid, ViewHolder holder, Post postTmp) {


        String uri = AppInfo.serverUri + "/" + AppInfo.serverRequestLike;
        String parameters = "id=" + id + "  &uid=" + uid;

        holder.button.setEnabled(false);
        postTmp.increaseCount();
        holder.button.setImageDrawable( ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_pink_24dp));
        holder.rowRate.setText(String.valueOf(postTmp.getLikesCount()));
        holder.rowRate.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        AsyncRequest asyncRequestObject = new AsyncRequest(this, -1, AsyncCode._SET_LIKE);
        asyncRequestObject.execute(uri, parameters);
    }

    private void postClick(int id, int uid, ViewHolder holder, Post postTmp) {
        String uri = AppInfo.serverUri + "/" + AppInfo.serverRequestPost;
        String parameters = "id=" + id + "  &uid=" + uid;

        holder.buttonRep.setEnabled(false);
        postTmp.increaseCountRep();
        holder.buttonRep.setImageDrawable( ContextCompat.getDrawable(getContext(), R.drawable.ic_insert_link_pink_24dp));
        holder.repostCount.setText(String.valueOf(postTmp.getRepCounts()));
        holder.repostCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        AsyncRequest asyncRequestObject = new AsyncRequest(this, -1, AsyncCode._SET_POST);
        asyncRequestObject.execute(uri, parameters);
    }

    @Override
    public void processFinish(String output, int position, int requestCode) {
        if(requestCode == AsyncCode._SET_LIKE) { // Like Post
            // code goes here if user likes the post
        }
    }

    public void setCallback(PlayerCallback callback) {
        this.callback = callback;
    }

}
