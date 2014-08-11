package com.bilal.gardinerclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bilal on 10/08/14.
 */
public class HomeListAdapter extends ArrayAdapter<Friend> {
    public HomeListAdapter(Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
    }

    public HomeListAdapter(Context context, int resource, Friend[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            row = inflater.inflate(R.layout.listitem_home_list, parent, false);
        }

        Friend friend = getItem(position);

        TextView name = (TextView) row.findViewById(R.id.listitem_home_name);
        name.setText(friend.getName());

        ImageView imageView = (ImageView) row.findViewById(R.id.listitem_home_image);
        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_person));

        return row;
    }
}
