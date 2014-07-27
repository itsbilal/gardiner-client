package com.bilal.gardinerclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bilal on 27/07/14.
 */
public class ContactsListAdapter extends ArrayAdapter<Contact> {
    public ContactsListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ContactsListAdapter(Context context, int resource, Contact[] objects) {
        super(context, resource, objects);
    }

    public ContactsListAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        convertView = li.inflate(R.layout.listitem_contacts_friend, null);

        Contact contact = getItem(position);

        ((TextView) convertView.findViewById(R.id.contact_name)).setText(contact.getName());

        return convertView;
    }
}
