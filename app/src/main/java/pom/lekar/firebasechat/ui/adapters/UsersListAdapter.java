package pom.lekar.firebasechat.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pom.lekar.firebasechat.Constants;
import pom.lekar.firebasechat.R;
import pom.lekar.firebasechat.models.User;
import pom.lekar.firebasechat.ui.activities.ChatActivity;

/**
 * Created by lekar on 09.05.17.
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserViewHolder> {
    private List<User> mUsers;
    private Context    mContext;

    public UsersListAdapter( Context mContext, List<User> mUsers) {
        this.mUsers   = mUsers;
        this.mContext = mContext;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView       personName;
        ImageView      personPhoto;
        RelativeLayout mRelativeLayout;

        UserViewHolder(View itemView) {
            super(itemView);

            personName      = (TextView)itemView.findViewById(R.id.usersListTextView);
            personPhoto     = (ImageView)itemView.findViewById(R.id.userListImageView);
            mRelativeLayout = (RelativeLayout)itemView.findViewById(R.id.conteiner_user_item);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users , parent, false);
        UserViewHolder pvh = new UserViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {

        holder.personName.setText(mUsers.get(position).getName());

        Picasso.with(mContext)
                .load(mUsers.get(position).getPhotoUrl() )
                .into(holder.personPhoto);

        holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,ChatActivity.class);
                intent.putExtra(Constants.EXTRA_ID,mUsers.get(position).getUid());
                intent.putExtra(Constants.EXTRA_USER_NAME,mUsers.get(position).getName());
                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
