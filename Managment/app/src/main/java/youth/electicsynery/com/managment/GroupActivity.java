package youth.electicsynery.com.managment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import youth.electicsynery.com.managment.Adapters.GroupAdapter;
import youth.electicsynery.com.managment.Adapters.MyAdapter;
import youth.electicsynery.com.managment.Objects.GroupItem;
import youth.electicsynery.com.managment.Objects.ListItem;

public class GroupActivity extends AppCompatActivity {
    private String UserID;
    private FloatingActionButton fab_add;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseDatabase database;
    private RecyclerView mRecyclerView;
    private GroupAdapter mAdapter;
    private ArrayList<GroupItem> listItems;
    private RecyclerView.LayoutManager mLayoutManager;
    private DataStorage dataStorage;
    public static String TAG = "Management DB ";
    ListItem mlistItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                UserID = null;
            } else {
                UserID = extras.getString("user_id");
            }
        } else {
            UserID= savedInstanceState.getString("user_id");
        }

        listItems = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.group_recycler_view);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        database = FirebaseDatabase.getInstance();
        dataStorage = new DataStorage(this);
        getGroup();

        fab_add = (FloatingActionButton) this.findViewById(R.id.fabAdd);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("user_id is: " + UserID);
                getGroup();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent);

        // specify an adapter (see also next example)
        mAdapter = new GroupAdapter(listItems,this);
        //mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

    }

    private boolean getGroup(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("group").child(UserID);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,String.valueOf(dataSnapshot.getChildrenCount()));
                for (DataSnapshot group : dataSnapshot.getChildren()) {
                    //Log.d(TAG, String.valueOf(v));
                    Log.d(TAG, String.valueOf(group.child("group_member1")));
                    Log.d(TAG, String.valueOf(group.child("group_member2")));
                    getMembersFromGroup(group);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return true;
    }

    private void getMembersFromGroup(DataSnapshot group) {
        long person;
        String group_name = group.getKey();
        ArrayList<ListItem> listItems1 = new ArrayList<>();
        for (DataSnapshot member : group.getChildren()) {
            //Log.d(TAG, String.valueOf(v));
            System.out.println(String.valueOf(member.getValue(Long.class)));
            person = member.getValue(Long.class);

            ListItem listItem = createListItem(person);
            //ListItem listItem = new ListItem();
            listItems1.add(listItem);
        }
        GroupItem groupItem = new GroupItem(group_name, listItems1);
        listItems.add(groupItem);
    }

    private ListItem createListItem(final Long group_member1) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference youth = database.getReference("youth");
        youth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
                Log.d(TAG, dataSnapshot.getValue().toString());
                Log.d(TAG, dataSnapshot.child(group_member1.toString()).getValue().toString());
                String value = dataSnapshot.child(group_member1.toString()).child("name").getValue(String.class);
                Log.d(TAG, value);
                String valueNum = dataSnapshot.child(group_member1.toString()).child("number").getValue(String.class);
                String valueEmail = dataSnapshot.child(group_member1.toString()).child("email").getValue(String.class);
                String valueAdd = dataSnapshot.child(group_member1.toString()).child("address").getValue(String.class);
                mlistItem = new ListItem(value,valueNum,valueEmail,valueAdd);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mlistItem;
    }


}
