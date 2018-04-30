package com.fitn.blog.Data;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitn.blog.Model.Blog;
import com.fitn.blog.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(BlogRecyclerAdapter.ViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        String imageURL = null;

        DateFormat dateFormat = DateFormat.getInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimeStamp())).getTime());

        holder.title.setText(blog.getTitle());
        holder.description.setText(blog.getDescription());
        holder.timeStamp.setText(formattedDate);
        imageURL = blog.getImage();

        Picasso.get().load(imageURL).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView timeStamp;
        public ImageView image;
        String userID;

        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;

            title = (TextView) view.findViewById(R.id.postTitle);
            description = (TextView) view.findViewById(R.id.postBody);
            timeStamp = (TextView) view.findViewById(R.id.postDate);
            image = (ImageView) view.findViewById(R.id.postImage);
            userID = null;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
