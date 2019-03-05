package in.fairshare.Data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import in.fairshare.Activities.VideosActivity;
import in.fairshare.R;

public class SharedVideosUsersAdapter extends RecyclerView.Adapter<SharedVideosUsersAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Context context;
    ArrayList<String> username = new ArrayList<>();
    ArrayList<String> userID = new ArrayList<>();

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    public SharedVideosUsersAdapter(RecyclerView recyclerView, Context context, ArrayList<String> username, ArrayList<String> userID) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.username = username;
        this.userID = userID;
    }

    public void updateUser(String usernames, String userIDs) {

        username.add(usernames);
        userID.add(userIDs);
        notifyDataSetChanged(); // Refreshes the recyclerView automatically
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.shared_videos_users_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final CheckBox checkBox = new CheckBox(context);
        checkBox.setText(username.get(i));
        checkBox.setTextSize(22);
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {context.getResources().getColor(R.color.blue), context.getResources().getColor(R.color.blue)};
        CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        checkBox.setTextColor(context.getResources().getColor(R.color.blue));
        viewHolder.shareVideosLinearLayout.addView(checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    MyAdapter myAdapter = new MyAdapter();
                    myAdapter.usersData(username.get(i), userID.get(i));
                } else {
                    MyAdapter myAdapter = new MyAdapter();
                    myAdapter.usersDataShareAccessDelete(username.get(i), userID.get(i));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout shareVideosLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shareVideosLinearLayout = itemView.findViewById(R.id.shareVideosLinearLayoutID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    //Toast.makeText(context, userID.get(position), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
