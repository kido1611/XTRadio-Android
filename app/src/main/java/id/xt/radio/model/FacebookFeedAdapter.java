package id.xt.radio.model;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.xt.radio.R;
import id.xt.radio.Utility.FacebookUtils;

/**
 * Created by Kido1611 on 11-May-16.
 */
public class FacebookFeedAdapter extends RecyclerView.Adapter<FacebookFeedAdapter.FacebookFeedViewHolder>{

    private Context mContext;
    private LayoutInflater inflater;

    private List<FacebookFeed> mItems = null;

    public FacebookFeedAdapter(Context context, List<FacebookFeed> items){
        mContext = context;
        inflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public FacebookFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.facebook_list_item, parent, false);
        FacebookFeedViewHolder holder = new FacebookFeedViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(FacebookFeedViewHolder holder, int position) {
        FacebookFeed item = mItems.get(position);

        holder.mProfileImage.setProfileId(item.getFrom().getId());

        holder.mNameText.setText(item.getFromName());

        holder.mTimeStampText.setText(FacebookUtils.getDate(item.getCreated_time()));

        if(item.getMessage()==null)
            holder.mStatusText.setVisibility(View.GONE);
        else {
            holder.mStatusText.setVisibility(View.VISIBLE);
            holder.mStatusText.setText(item.getMessage());
        }

        holder.mLikeText.setText("{faw-thumbs_o_up} "+item.getLikes().size());
        holder.mShareText.setText("{faw-share} "+item.getShares());

        if(item.getFull_picture()!=null ){
            if(item.getType().equals("video") || item.getType().equals("photo")) {
                Picasso.with(mContext).load(item.getFull_picture()).placeholder(R.mipmap.ic_launcher).into(holder.mStatusImage);
                holder.mStatusImage.setVisibility(View.VISIBLE);
            }
        }else
            holder.mStatusImage.setVisibility(View.GONE);

//        if(item.getType().equals("link")){
//            holder.mCardLink.setVisibility(View.VISIBLE);
//            holder.mCardLink.setVisibility(View.GONE);
//
//            holder.mTitleLinkText.setText(item.getName());
//            holder.mUrlLinkText.setText(item.getCaption().toUpperCase());
//
//            Picasso.with(mContext).load(item.getFull_picture()).placeholder(R.mipmap.ic_launcher).into(holder.mLinkImage);
//
//        }else{
//            holder.mCardLink.setVisibility(View.GONE);
//            holder.mCardLink.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class FacebookFeedViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.profile_image)
        ProfilePictureView mProfileImage;
        @BindView(R.id.name_text)
        TextView mNameText;
        @BindView(R.id.timestamp_text)
        TextView mTimeStampText;

        @BindView(R.id.status_text)
        TextView mStatusText;
        @BindView(R.id.status_image)
        ImageView mStatusImage;

//        @BindView(R.id.link_layout)
//        CardView mCardLink;
//        @BindView(R.id.link_image)
//        ImageView mLinkImage;
//        @BindView(R.id.link_title)
//        TextView mTitleLinkText;
//        @BindView(R.id.link_url)
//        TextView mUrlLinkText;
//
//        @BindView(R.id.picture_layout)
//        CardView mCardPicture;
//        @BindView(R.id.picture_image)
//        ImageView mPictureImage;
//        @BindView(R.id.picture_title)
//        TextView mTitlePictureText;
//        @BindView(R.id.picture_url)
//        TextView mUrlPictureText;

        @BindView(R.id.likes_text)
        TextView mLikeText;
        @BindView(R.id.share_text)
        TextView mShareText;

        public FacebookFeedViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
