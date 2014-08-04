package com.bilal.gardinerclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
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

    private View.OnClickListener getContactAddListener(final Contact contact) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.setClickable(false);
                final Spinner spinner = (Spinner) ((ViewGroup)view.getParent()).findViewById(R.id.contact_list_spinner);
                spinner.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);

                OnNetworkDone onDone = new OnNetworkDone() {
                    @Override
                    public Void call() throws Exception {
                        spinner.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                        if (response.getInt("success") == 1) {
                            ((ImageButton)view).setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_done));
                            Log.d("ContactListAdapter", "Successful reenable");
                        }

                        return null;
                    }
                };

                if (contact.getRequestId() == null) { // Send 'em a request
                    contact.sendRequest((NetworkActivity)getContext(), onDone);
                } else {
                    contact.acceptRequest((NetworkActivity)getContext(), onDone);
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(getContext());
        convertView = li.inflate(R.layout.listitem_contacts_friend, null);

        Contact contact = getItem(position);

        ((TextView) convertView.findViewById(R.id.contact_name)).setText(contact.getName());
        ImageButton addButton = (ImageButton) convertView.findViewById(R.id.contact_add);
        addButton.setOnClickListener(getContactAddListener(contact));

        if (contact.getType() == Contact.Type.FRIEND || contact.getType() == Contact.Type.REQUESTED) {
            addButton.setClickable(false);
            addButton.setVisibility(View.GONE);
        }

        return convertView;
    }
}
