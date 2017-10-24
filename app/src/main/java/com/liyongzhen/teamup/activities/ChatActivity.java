package com.liyongzhen.teamup.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liyongzhen.teamup.R;
import com.liyongzhen.teamup.adapters.UserAdapter;
import com.liyongzhen.teamup.model.Profile;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<Profile> searchedUsers;
    private RecyclerView mRecyclerView;
    private UserAdapter adapter;
    private ProgressBar mProgressBar;

    private String currentUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchedUsers = new ArrayList<>();

        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mRecyclerView = (RecyclerView) findViewById(R.id.friendsView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new UserAdapter(this, searchedUsers);
        mRecyclerView.setAdapter(adapter);

        searchUsersInFirebase("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
//                    if (!newText.isEmpty())
                        searchUsersInFirebase(newText);
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void searchUsersInFirebase(String name) {

        mProgressBar.setVisibility(View.VISIBLE);
        Query query = FirebaseDatabase.getInstance().getReference().child("profiles")
                .orderByChild("username").startAt(name).endAt(name + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchedUsers.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Profile user = item.getValue(Profile.class);
                    user.setId(item.getKey());
                    if (!currentUserId.equals(user.getId()))
                        searchedUsers.add(user);
                }
                adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

}
