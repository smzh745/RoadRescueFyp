package com.road.rescue.app.activities;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.road.rescue.app.R;
import com.road.rescue.app.adapter.ContactAdapter;
import com.road.rescue.app.model.EmergencyContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.road.rescue.app.utils.Constants.TAGI;

public class ShowAllContactsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<EmergencyContact> myComplaintList;
    private boolean isFromContact = false;
    private MaterialTextView empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_contacts);
        recyclerView = findViewById(R.id.recycler_home);
        empty_view = findViewById(R.id.empty_view);
        isFromContact = getIntent().getBooleanExtra("isFromContact", false);
        myComplaintList = new ArrayList<>();
        getAllContacts(getContentResolver());
        initSearchView();
   /*     recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                EmergencyContact emergencyContact = myComplaintList.get(position);
                Log.d(TAGI, "onClick: " + emergencyContact.geteName());
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.d(TAGI, "onLongClick: ");
            }
        }));*/

    }


    private void getAllContacts(ContentResolver contentResolver) {
        try {
            myComplaintList.clear();
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, "display_name asc");
            if (phones != null) {
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d(TAGI, "getAllContacts: " + phoneNumber + "\n");
                    Log.d(TAGI, "getAllContacts: " + name);
                    myComplaintList.add(new EmergencyContact(name, phoneNumber));
                }
            }

            if (phones != null) {
                phones.close();
            }
            setDataOnRecycler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromContact) {
            finish();
        } else {
            finishAffinity();
        }
    }

    private void setDataOnRecycler() {
        ContactAdapter myComplaintAdapter = new ContactAdapter(myComplaintList, ShowAllContactsActivity.this, isFromContact);
        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(myComplaintAdapter);
    }


    private void initSearchView() {
        try {
            SearchView searchView = findViewById(R.id.searchView);
            searchView.onActionViewExpanded();
            searchView.clearFocus();
            searchView.setQueryHint(Html.fromHtml("<font color = #000000>" + getResources().getString(R.string.keyword) + "</font>"));
            searchView.setQueryHint(getResources().getString(R.string.keyword));

            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchByKeyword(query.toLowerCase());
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchByKeyword(newText.toLowerCase());
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchByKeyword(String query) {
        List<EmergencyContact> emergencyContacts = new ArrayList<>();
        for (EmergencyContact d : myComplaintList) {
            if (d.geteName() != null && d.geteName().toLowerCase().contains(query)) {
                Objects.requireNonNull(emergencyContacts).add(new EmergencyContact(d.geteName(), d.geteContact()));
            }

        }
        if (!emergencyContacts.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            empty_view.setVisibility(View.GONE);
            recyclerView.setAdapter(new ContactAdapter(emergencyContacts, ShowAllContactsActivity.this, isFromContact));
        } else {
            recyclerView.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
        }
    }
}
