package com.whoami.litlteinstagram.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.whoami.litlteinstagram.R;
import com.whoami.litlteinstagram.entities.Post;
import java.util.List;

public class ListPostAdapter extends ArrayAdapter<Post>
{
    Context context;
    List<Post> posts;

    public ListPostAdapter(Context context, int resource, List<Post> posts)
    {
        super(context, resource, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public int getCount()
    {
        if(posts!=null)
        {
            return posts.size();
        }else{
            return 0;
        }
    }

    @Override
    public Post getItem(int position)
    {
        if(posts!=null)
        {
            return posts.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position)
    {
        if(posts!=null){
            return posts.get(position).hashCode();
        }else{
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;
        if(convertView==null)
        {
            holder = new Holder();
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.post_list, null);
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        Post post = getItem(position);
        holder.desc.setText(post.getDesc());
        Picasso.with(context).load(post.getImageLocation()).fit().into(holder.photo);
        return convertView;
    }

    private class Holder{
        TextView desc;
        ImageView photo;

    }
}
