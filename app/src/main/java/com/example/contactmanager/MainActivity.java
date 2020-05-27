package com.example.contactmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private static final int DELETE = 0;

    EditText nameTxt, phoneTxt, detailsTxt;
    List<Contacts> Contacts = new ArrayList<>();
    ListView contactListView;
    DataBaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contacts> contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        detailsTxt = (EditText) findViewById(R.id.txtDetails);
        dbHandler = new DataBaseHandler(getApplicationContext());
        contactListView = (ListView) findViewById(R.id.listView);

        registerForContextMenu(contactListView);
        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameTxt.getText().toString();
                int phone = Integer.parseInt(phoneTxt.getText().toString());
                String details  = detailsTxt.getText().toString();

                Contacts contact = new Contacts(dbHandler.getContactsCount(), name, phone, details);
                if (!contactExist(contact)) {
                    dbHandler.createContact(contact);
                    Contacts.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), nameTxt.getText().toString() + " contact has been created!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), nameTxt.getText().toString() + " contact already exists! Please create another one", Toast.LENGTH_SHORT).show();
            }
        });


        final Button clearBtn = (Button) findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTxt.setText("");
                phoneTxt.setText("");
                detailsTxt.setText("");
            }
        });



        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addBtn.setEnabled(!nameTxt.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (dbHandler.getContactsCount() != 0)
            Contacts.addAll(dbHandler.getAllContacts());

        populateList();

    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact" );
    }

    public boolean onContextItemSelected(MenuItem item){
        if (item.getItemId() == DELETE) {
            dbHandler.deleteContact(Contacts.get(longClickedItemIndex));
            Contacts.remove(longClickedItemIndex);
            contactAdapter.notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);
    }

    private boolean contactExist(Contacts contact){
        String name = contact.getName();
        int contactsCount = Contacts.size();

        for (int i = 0; i < contactsCount; i++){
            if (name.compareToIgnoreCase(Contacts.get(i).getName()) == 0)
                return true;
        }
        return false;
    }


    private class ContactListAdapter extends ArrayAdapter<Contacts> {
        public ContactListAdapter() {
            super(MainActivity.this, R.layout.contact_list, Contacts);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.contact_list, parent, false);
            }
            Contacts currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phNum);
            phone.setText("Phone: " + currentContact.getPhone());
            TextView details = (TextView) view.findViewById(R.id.detailsContact);
            details.setText("Details: " + currentContact.getDetails());

            return view;


        }


    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }


}




