package in.fairshare.Data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.fairshare.Activities.SharedVideosActivity;
import in.fairshare.Activities.VideoPlayerActivity;
import in.fairshare.R;

public class SharedVideosAdapter extends RecyclerView.Adapter<SharedVideosAdapter.ViewHolder> {

    RecyclerView  recyclerView;
    Context context;
    ArrayList<String> sharedVideoTitle = new ArrayList<>();
    ArrayList<String> sharedVideoDescp = new ArrayList<>();
    ArrayList<String> sharedVideoUrl = new ArrayList<>();
    ArrayList<String> sharedVideoKey = new ArrayList<>();
    ArrayList<String> sharedVideoFileName = new ArrayList<>();
    ArrayList<String> sharedVideoUsername = new ArrayList<>();
    ArrayList<String> sharedVideoDate = new ArrayList<>();

    public SharedVideosAdapter(RecyclerView recyclerView, Context context, ArrayList<String> sharedVideoTitle, ArrayList<String> sharedVideoDescp, ArrayList<String> sharedVideoUrl, ArrayList<String> sharedVideoKey, ArrayList<String> sharedVideoFileName, ArrayList<String> sharedVideoUsername, ArrayList<String> sharedVideoDate) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.sharedVideoTitle = sharedVideoTitle;
        this.sharedVideoDescp = sharedVideoDescp;
        this.sharedVideoUrl = sharedVideoUrl;
        this.sharedVideoKey = sharedVideoKey;
        this.sharedVideoFileName = sharedVideoFileName;
        this.sharedVideoUsername = sharedVideoUsername;
        this.sharedVideoDate = sharedVideoDate;
    }

    public void updateShare(String sharedVideoTitles, String sharedVideoDescps, String sharedVideoUrls, String sharedVideoKeys, String sharedVideoFileNames, String sharedVideoUsernames, String sharedVideoDates) {

        sharedVideoTitle.add(sharedVideoTitles);
        sharedVideoDescp.add(sharedVideoDescps);
        sharedVideoUrl.add(sharedVideoUrls);
        sharedVideoKey.add(sharedVideoKeys);
        sharedVideoFileName.add(sharedVideoFileNames);
        sharedVideoUsername.add(sharedVideoUsernames);
        sharedVideoDate.add(sharedVideoDates);
        notifyDataSetChanged(); // Refreshes the recyclerView automatically
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.sharedvideos_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        // This method is called for every cardview or data
        // It gets information of video in each and every respected cardview
        viewHolder.sharedVideoTitle.setText(sharedVideoTitle.get(i));
        viewHolder.sharedVideoDescp.setText(sharedVideoDescp.get(i));
        viewHolder.sharedVideoUsername.setText(sharedVideoUsername.get(i));
        viewHolder.sharedVideoDate.setText(sharedVideoDate.get(i));

        viewHolder.sharedVideoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create popup menu which has option of delete shared video
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.sharedVideoOption);
                popupMenu.inflate(R.menu.menu_sharevideo);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu_deletesharevideo:

                                // This send filename of video which you want to delete from shared video
                                SharedVideosActivity sharedVideosActivity = new SharedVideosActivity();
                                sharedVideosActivity.deleteShareVideo(sharedVideoFileName.get(i));
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sharedVideoFileName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sharedVideoTitle;
        private TextView sharedVideoDescp;
        private TextView sharedVideoOption;
        private TextView sharedVideoUsername;
        private TextView sharedVideoDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sharedVideoTitle = itemView.findViewById(R.id.sharedVideoTitleID);
            sharedVideoDescp = itemView.findViewById(R.id.sharedVideoDescpID);
            sharedVideoOption = itemView.findViewById(R.id.sharedVideoOptionID);
            sharedVideoUsername = itemView.findViewById(R.id.sharedVideoUsernameID);
            sharedVideoDate = itemView.findViewById(R.id.sharedVideoDateID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = recyclerView.getChildLayoutPosition(v);

                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("URL", sharedVideoUrl.get(position));
                    intent.putExtra("STRING_KEY", sharedVideoKey.get(position));
                    context.startActivity(intent);
                }
            });
        }
    }
}
