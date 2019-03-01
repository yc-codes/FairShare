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

import java.util.ArrayList;

import in.fairshare.Activities.VideoPlayerActivity;
import in.fairshare.Activities.VideosActivity;
import in.fairshare.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    RecyclerView  recyclerView;
    Context context;
    ArrayList<String> videoTitle = new ArrayList<>();
    ArrayList<String> videoDescp = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<String> key = new ArrayList<>();
    ArrayList<String> fileName = new ArrayList<>();

    public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> videoTitle, ArrayList<String> videoDescp, ArrayList<String> url, ArrayList<String> key, ArrayList<String> fileName) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.videoTitle = videoTitle;
        this.videoDescp = videoDescp;
        this.url = url;
        this.key = key;
        this.fileName = fileName;
    }

    public void update(String videoTitles, String videoDescps, String videoUrls, String keys, String fileNames) {

        videoTitle.add(videoTitles);
        videoDescp.add(videoDescps);
        url.add(videoUrls);
        key.add(keys);
        fileName.add(fileNames);
        notifyDataSetChanged(); // Refreshes the recyclerView automatically
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.videos_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.videoTitle.setText(videoTitle.get(i));
        viewHolder.videoDescp.setText(videoDescp.get(i));
        // viewHolder.fileName.setText(fileName.get(i));

        viewHolder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // final int position = recyclerView.getChildLayoutPosition(v);

                final PopupMenu popupMenu = new PopupMenu(context, viewHolder.option);
                popupMenu.inflate(R.menu.menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu_deletevideo:

                                VideosActivity videosActivity = new VideosActivity();
                                videosActivity.delete(fileName.get(i));
                                break;

                            case R.id.menu_sharevideo:

                                String shareVideoTitle = videoTitle.get(i);
                                String shareVideoDescp = videoDescp.get(i);
                                String shareVideoUrl = url.get(i);
                                String shareVideoKey  = key.get(i);
                                String shareVideoFileName = fileName.get(i);

                                VideosActivity videosActivity1 = new VideosActivity();
                                videosActivity1.share(shareVideoTitle, shareVideoDescp, shareVideoUrl,shareVideoKey, shareVideoFileName);
                                break;

                            case R.id.menu_rtvshracs:
                                // Toast.makeText(context, "Retrieve Share Access", Toast.LENGTH_SHORT).show();
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
        return fileName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView videoTitle;
        private TextView videoDescp;
        private TextView option;
        // private TextView fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoTitle = itemView.findViewById(R.id.videoTitleID);
            videoDescp = itemView.findViewById(R.id.videoDescpID);
            option = itemView.findViewById(R.id.optionID);
            // fileName = itemView.findViewById(R.id.fileNameID);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO: Tap on card to play video

                    int position = recyclerView.getChildLayoutPosition(v);

                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("URL", url.get(position));
                    intent.putExtra("STRING_KEY", key.get(position));
                    context.startActivity(intent);
                }
            });
        }
    }
}
