package youth.electicsynery.com.managment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import youth.electicsynery.com.managment.Adapters.MyAdapter;
import youth.electicsynery.com.managment.Dialogs.NewMemberDialog;
import youth.electicsynery.com.managment.Objects.ListItem;

import static youth.electicsynery.com.managment.Constants.checkVerified;
import static youth.electicsynery.com.managment.Constants.setLog;

public class Main2Activity extends AppCompatActivity {
    private CardView cardView;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ListItem> listItems;
    private DataStorage db;
    private String UserID;
    public static String TAG = "Management DB ";
    private boolean fab_clicked = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PopupMenu.OnMenuItemClickListener pop;

    //boolean flag to know if main FAB is in open or closed state.
    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;

    //Linear layout holding the Save submenu
    private LinearLayout layoutFabEmail;

    //Linear layout holding the Edit submenu
    private LinearLayout layoutFabSMS;
    private LinearLayout layoutFabPerson;
    private LinearLayout layoutFabGroup;

    private SearchView searchView;
    InputStream inputStream;
    String[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //inputStream = getResources().openRawResource(R.raw.youths);
        db = new DataStorage(this);
        boolean gotten = getYouth();
        mRecyclerView = findViewById(R.id.my_recycler_view);
        TextView title = findViewById(R.id.toolbar_title);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Passager.ttf");

        title.setTypeface(face);

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

        /*// Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                ids=csvLine.split(",");
                try{
                    if (isNumber(ids[0])){
                        System.out.println(ids[0]+"|"+ids[1]+"|"+ids[2]+"|"+ids[3]+"|"+ids[4]);
                        Log.e("Column 1 ",""+ids[0]) ;
                        writeNewUser(ids[1],ids[2],ids[3],ids[4],ids[0]);
                    }
                }catch (Exception e){
                    Log.e("Unknown",e.toString());
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }*/






        searchView = findViewById(R.id.m_searchview);
        searchView.setQueryHint("Type name to search ...");
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //MyAdapter myAdapter = new MyAdapter(listItems,getBaseContext());
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        layoutFabEmail = this.findViewById(R.id.layoutFabEmail);
        layoutFabSMS = this.findViewById(R.id.layoutFabBulkText);
        layoutFabPerson = this.findViewById(R.id.layoutFabAddPerson);
        layoutFabGroup = this.findViewById(R.id.layoutFabGroup);

        fabSettings = this.findViewById(R.id.fab);
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_clicked = true;
                if (fabExpanded){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        ImageButton menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Main2Activity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return on_menuItemClick(item);
                    }
                });
                inflater.inflate(R.menu.menu_main, popup.getMenu());
                popup.show();
            }
        });

        FloatingActionButton fabAdd = this.findViewById(R.id.fabNewMember);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_clicked = true;
                // Write a message to the database
                createDialog(listItems);
                //writeNewUser();
                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                //DatabaseReference myRef = database.getReference();
                //DatabaseReference cineIndustryRef = myRef.child("youth").push();

                //String key = cineIndustryRef.getKey();
               // Map<String, Object> map = new HashMap<>();
               // map.put(key, "Hollywood");
                //System.out.println(key);
                //and os on
                //cineIndustryRef.updateChildren(map);

                //myRef.setValue("Hello, World!");
            }
        });

        FloatingActionButton fabEmail = this.findViewById(R.id.fabEmail);
        fabEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_clicked = true;
                List<String> list = db.getAllEmail();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                System.out.println(Arrays.toString(list.toArray()));
                intent.putExtra(Intent.EXTRA_BCC, sendTo(list));
                //intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                //intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        FloatingActionButton fabText = this.findViewById(R.id.fabText);
        fabText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_clicked = true;
                List<String> list = dataPhoneSET();
                System.out.println(Arrays.toString(list.toArray()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("address", sendToNumbers(list));
                //intent.putExtra("sms_body","Body of Message");
                startActivity(intent);

            }
        });

        FloatingActionButton fabGroup = findViewById(R.id.fabGroup);
        fabGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_clicked = true;
                Intent intent = new Intent(Main2Activity.this,GroupActivity.class);
                intent.putExtra("user_id",UserID);
                startActivity(intent);

            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
            void refreshItems() {
                // Load items
                // ...
                if (getYouth()){
                    // Load complete
                    onItemsLoadComplete();
                }
            }

            void onItemsLoadComplete() {
                // Update the adapter and notify data set changed
                // ...
                //mAdapter = new MyAdapter(myDataset);
                if (mAdapter!=null){
                    mAdapter.notifyDataSetChanged();

                    ArrayList<ListItem> newItems;
                    List<String> stringList = dataSET();
                    //newItems = getList(stringList);
                    System.out.println("See this: "+stringList.size());
                    int oldListItemscount = listItems.size();
                    createList(stringList,listItems);
                    System.out.println("See this: "+listItems.size());
                    System.out.println("See this: "+oldListItemscount);
                    //listItems.clear();
                    //listItems.addAll(newItems);
                    mRecyclerView.getAdapter().notifyItemInserted(oldListItemscount - 1);
                    //mRecyclerView.getAdapter().notifyItemChanged(oldListItemscount+1);
                }
                // Stop refresh animation
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listItems = new ArrayList<>();

        List<String> stringList = dataSET();
        List<String> phoneList = dataPhoneSET();

        if (gotten){
            createList(stringList, listItems);
            // specify an adapter (see also next example)
            mAdapter = new MyAdapter(listItems,this);
            //mAdapter = new MyAdapter(myDataset);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {

        }
    }

    private boolean isNumber(String id) {
        try {
            Integer.parseInt(id);
            return true;
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean writeNewUser(String name, String number, String email, String address, String i) {
        ListItem user = new ListItem(name, number, email.toLowerCase(), address);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        //DatabaseReference myRef = database.getReference("youth");

        System.out.println(db.getFreeID());
        Task task = myRef.child("youth").child(i).setValue(user);
        return task.isSuccessful();
    }

    private void createList(List<String> stringList, ArrayList<ListItem> listItems) {
        this.listItems.removeAll(listItems);
        for (int i = 0; i<stringList.size(); i++){
            String name = stringList.get(i);

            //String number = phoneList.get(i);
            String number = db.getNumber(name);
            String email = db.getEmail(name);
            String address = db.getNumber(name);
            ListItem listItem = new ListItem(name,number, email, address);
            this.listItems.add(listItem);
        }
    }

    private void createDialog(ArrayList<ListItem> listItems) {
        DialogFragment newFragment = new NewMemberDialog();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        newFragment.setCancelable(false);
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", listItems);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "add_a_member");
    }

    private String[] sendTo(List<String> list) {
        String emails = "";
        for (int i = 0; i<list.size(); i++){
            emails += list.get(i)+",";
        }
        return new String[] { emails };
    }

    private String sendToNumbers(List<String> list) {
        String numbers = "";
        for (int i = 0; i<list.size(); i++){
            numbers += list.get(i).trim()+";";
        }
        return numbers;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Only main FAB is visible in the beginning
        closeSubMenusFab();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if(!fab_clicked){
            closeSubMenusFab();
        }
    }

    private boolean getYouth(){
        Log.d(TAG, "Get youth method called");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("youth");
        DatabaseReference contact = database.getReference("contact");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG,dataSnapshot.toString());
                Log.d(TAG,dataSnapshot.getValue().toString());
                Log.d(TAG,String.valueOf(dataSnapshot.getChildrenCount()));
                Log.d(TAG,String.valueOf(dataSnapshot.getChildren()));

                for (DataSnapshot v : dataSnapshot.getChildren()){
                    Log.d(TAG,String.valueOf("number of items: "+v.getChildrenCount()));
                    Log.d(TAG,String.valueOf(v.child("number")));
                    Log.d(TAG,String.valueOf(v.child("name")));
                    Log.d(TAG,String.valueOf(v.child("email")));
                    Log.d(TAG,String.valueOf(v.child("address")));
                    String value = v.child("name").getValue(String.class);
                    String valueNum = v.child("number").getValue(String.class);
                    String valueEmail = v.child("email").getValue(String.class);
                    String valueAdd = v.child("address").getValue(String.class);
                    Log.d(TAG, "Name is: " + value + " Number is: "+ valueNum + " Email is: "+ valueEmail + " Address is: "+ valueAdd);
                    System.out.println(("Name is: " + value + " Number is: "+ valueNum + " Email is: "+ valueEmail + " Address is: "+ valueAdd));
                    // database handler
                    //db = new DataStorage(Main2Activity.this);
                    boolean exist = db.checkValueExist(value);
                    if (!exist){
                        db.insertData(value);
                        db.insertDataNumbers(valueNum,value);
                        db.insertDataEmail(valueEmail,value);
                        db.insertDataAddress(valueAdd,value);
                    }
                    else {
                        Log.d(TAG, value + " update exist");
                    }
                }
                //long m = dataSnapshot.getChildrenCount();
                //Log.d(TAG,String.valueOf("number of items saved: "+m));
                //db.insertDataYouthID(m);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        contact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dataSnapshot.getChildren();
                for (DataSnapshot v : dataSnapshot.getChildren()){
                    String value = v.getValue(String.class);
                    Log.d(TAG, "Phone values: " + value);
                    // database handler
                    //DataStorage db = new DataStorage(Main2Activity.this);
                    boolean exist = db.checkValueExist(value);
                    if (!exist){
                        //db.insertData(value);
                    }
                    else {
                        Log.d(TAG, value + " update exist");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        return true;
    }

    private List<String> dataSET(){
        // database handler
       // db = new DataStorage(this);

        // Spinner Drop down elements
        List<String> lables = db.getAllNames();
        return lables;
    }

    private List<String> dataPhoneSET(){
        // database handler
        // = new DataStorage(this);
        // Spinner Drop down elements
        List<String> labels = db.getPhoneNumbers();

        Log.d(TAG, "number labels: "+labels);
        return labels;
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabEmail.setVisibility(View.INVISIBLE);
        layoutFabSMS.setVisibility(View.INVISIBLE);
        layoutFabPerson.setVisibility(View.INVISIBLE);
        layoutFabGroup.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.ic_share_black_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        final long Duration = 2000;
        long Interval = 100;
        CountDownTimer countDownTimer = new CountDownTimer(Duration, Interval) {
            long mTick = Duration;
            public void onTick(long millisUntilFinished) {
                mTick = Duration - millisUntilFinished;
                System.out.println("Current Tick afte subr : "+mTick);
                if (mTick > 500){
                    layoutFabEmail.setVisibility(View.VISIBLE);
                    System.out.println("Current Tick for email : "+mTick);
                }
                if (mTick > 1000){
                    layoutFabSMS.setVisibility(View.VISIBLE);
                    System.out.println("Current Tick for sms : "+mTick);
                }
                if (mTick > 1500){
                    layoutFabPerson.setVisibility(View.VISIBLE);
                    System.out.println("Current Tick for person : "+mTick);
                }
            }

            public void onFinish() {
                layoutFabGroup.setVisibility(View.VISIBLE);
            }
        }.start();
        //Change settings icon to 'X' icon
        fabSettings.setImageResource(R.drawable.ic_clear_black_24dp);
        fabExpanded = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.menuSearch);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //MyAdapter myAdapter = new MyAdapter(listItems,getBaseContext());
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean on_menuItemClick(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                setLog(Main2Activity.this,false);
                checkVerified = false;
                Intent logOff = new Intent(Main2Activity.this,LoginActivity.class);
                startActivity(logOff);
                finish();
                return true;
            case R.id.gen_menu:
                Intent gen = new Intent(Main2Activity.this,Main3Activity.class);
                startActivity(gen);
                return true;
            default:
                return pop.onMenuItemClick(item);
        }
    }

    class Initialize extends AsyncTask<Class<?>, String, Long> {

        @Override
        protected Long doInBackground(Class<?>... params) {
            Class<?> classactivity = params[0];

            return null;

        }

        // This is executed in the context of the main GUI thread
        protected void onPostExecute(Long result) {

        }
    }
}
