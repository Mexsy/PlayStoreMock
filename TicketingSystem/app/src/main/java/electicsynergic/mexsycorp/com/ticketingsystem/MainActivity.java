package electicsynergic.mexsycorp.com.ticketingsystem;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import electicsynergic.mexsycorp.com.ticketingsystem.Adapters.NothingSelectedSpinnerAdapter;
import electicsynergic.mexsycorp.com.ticketingsystem.Objects.Ticket;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private EditText editText;
    String mmDeviceName;
    TextView eText;
    TextView textViewDevice;
    private ProgressDialog bar;
    private String StateCode;
    private final String code = "State Code:";
    private TextView textView;
    private PopupMenu.OnMenuItemClickListener pop;
    private AdView mAdView;
    private Spinner spinner;
    private String itemSelected = "";
    int x = 1;
    private String plea = "Please wait your turn. Hold your ticket and present it when called forward";
    private String plea58 = "Please wait your turn." +
            "Present ticket when called forward";
    // android built in classes for bluetooth operations
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    private static String TAG = "Test";
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private Button printButton;
    private int currentTicketNum = 0;
    private List<String> list = new ArrayList<>();
    private Print pTask;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_BLUETOOTH = 0;
    private String Size = "80";
    TextView title;
    TextView entermsg;
    private TextView resetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        title = (TextView) findViewById(R.id.toolbar_title);
        entermsg = (TextView) findViewById(R.id.entermsg);
        resetText = (TextView) findViewById(R.id.resetTV);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Passager.ttf");
        Typeface face1= Typeface.createFromAsset(getAssets(), "fonts/Quango.otf");
        title.setTypeface(face);
        entermsg.setTypeface(face1);
        resetText.setTypeface(face1);

        editText = (EditText) findViewById(R.id.state_editText );

        eText = (TextView) findViewById(R.id.current_ticket_number);
        textViewDevice = (TextView) findViewById(R.id.connectionStat);


        ImageButton resetButton = (ImageButton) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x = 1;
                eText.setText(String.valueOf(x));
            }
        });

        textView = (TextView) findViewById(R.id.printing_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar = new ProgressDialog(MainActivity.this);//creates a new progress dialog.
                bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//sets what direction the bar runs in.
                bar.setMessage("Processing data");//shows a message.
                bar.show();


                StateCode = editText.getText().toString();
                if (StateCode.trim().isEmpty()) {
                    editText.setError("Please enter a state code number");
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            editText.setError(null);
                        }
                    });
                    bar.dismiss();
                }
                if (StateCode.trim().length()<2) {
                    editText.setError("Please enter a valid state code");
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            editText.setError(null);
                        }
                    });
                    bar.dismiss();
                } else {
                    bar.setMessage("Please wait. Printing Ticket");//shows a message.

                    Toast.makeText(MainActivity.this, "Please wait. Printing Ticket", Toast.LENGTH_SHORT).show();
                    Log.d("Was button pressed","Yes");
                    findBT();
                    try {
                        // Use this if device has api above 6.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.BLUETOOTH)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.BLUETOOTH},
                                        REQUEST_BLUETOOTH);
                            } else {
                                openBT();
                            }
                        }
                        else {
                            openBT();
                        }

                        list = new ArrayList<>();
                        list.add(StateCode);
                        pTask = new Print();
                        pTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        //int y = sendData(list.size());

                        // Write a message to the database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("message");

                        myRef.setValue("Hello, World!");
                        //closeBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        textView.setTypeface(face1);

        ImageButton menu_button = (ImageButton) findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Access Key");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        // Loading spinner data from database
        loadSpinnerData();
        // Update local db
        updateSpinner();
    }

    private void updateSpinner() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("spinner data");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dataSnapshot.getChildren();
                for (DataSnapshot v : dataSnapshot.getChildren()){
                    String value = v.getValue(String.class);
                    Log.d(TAG, "Spinner Value is: " + value);
                    // database handler
                    DataStorage db = new DataStorage(MainActivity.this);
                    boolean exist = db.checkValueExist(value);
                    if (!exist){
                        db.insertData(value);
                    }
                    else {
                        Log.d(TAG, value + " update exist");
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Function to load the spinner data from Fire base database
     * */
    private int loadSpinnerData() {
        // database handler
        DataStorage db = new DataStorage(this);

        // Spinner Drop down elements
        List<String> lables = db.getAllStateCodes();

        Log.d(TAG, "lables: "+lables);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        dataAdapter,
                        R.layout.batch_prefix_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        MainActivity.this));
        return 1;
    }

    public boolean on_menuItemClick(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent logOff = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(logOff);
                return true;
            default:
                return pop.onMenuItemClick(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroying");
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                Log.d("null blue","No bluetooth adapter available");
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    System.out.println(device.getName());
                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    //"MTP_3-2"
                    if (device.getName().equals("BlueTooth Printer")) {
                        mmDevice = device;
                        System.out.println("Name of the device connected is: "+ mmDevice.getName());
                        break;
                    }
                }
            }
            Log.d("null blue","Bluetooth device found.");

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            if (mmSocket.isConnected()){
                mmDeviceName = mmDevice.getName();
                textViewDevice.setText("Connected to "+mmDeviceName);
                System.out.println("Connected to "+mmDeviceName);
            }
            else {
                System.out.println("Failed to connect");
            }
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            Log.d("is blue open","Bluetooth Opened");
            //myLabel.setText("");
            //myLabel.setTextColor(Color.BLUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */

    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.d("Your data has been sent",data);
                                                //Snackbar.make(view, "Customer has been registered", Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener() {
                                                // @Override
                                                //public void onClick(View v) {
                                                //     finish();
                                            }
                                            //  }).show();
                                            //myLabel.setText(data);
                                            //}
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.fiftyERadio:
                if (checked)
                    Size = "58";

                break;
            case R.id.EigthyRadio:
                if (checked)
                    Size = "80";
                break;
            //default:

        }
    }

    private String getDateOnly() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c.getTime());
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            Log.d(TAG,"closed bluetooth called");
            System.out.println("closed bluetooth called");
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            // myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printHeader() throws IOException {
        byte[] cmd = new byte[2];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        //cmd[2] |= 0x10;
        //mmOutputStream.write(cmd);           //Double width, double height mode
        String billheader = "\nLAGOS STATE GOVERNMENT\nCentral Billing System " +
                "[Lagos State CBS]";
        String billheader1 = "LAGOS STATE GOVERNMENT\nCentral Billing System " +
                "[Lagos State CBS]";
        mmOutputStream.write(billheader.getBytes());
        //mService.sendMessage("\nLAGOS STATE GOVERNMENT", "GBK");
        //cmd[2] &= 0xEF;
        //mmOutputStream.write(cmd);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(spinner != null && spinner.getSelectedItem() != null){
            itemSelected = spinner.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class Print extends AsyncTask<Void,Void,Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("Printing");
            bar.setMessage("Printing");//shows a message.
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int y = 0;
            try {
                y = sendData(list.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return y;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 5){
                bar.setMessage("Done");//shows a message.
                bar.dismiss();
                textView.setText("Print");
                eText.setText(String.valueOf(x));
            }
        }

        // this will send text data to be printed by the bluetooth printer
        int sendData(int num) throws IOException {
            if (Size.equals("80")){
                System.out.println("doing 80"+ "device is: "+mmDeviceName);
                String new_divider = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
                String new_single_divider = "------------------------------------------------";
                String space = "                          ";

                Ticket ticket = new Ticket(StateCode,num);
                ArrayList<Ticket> list = new ArrayList<>();
                list.add(ticket);
                try {

                    String ticketNo = "Ticket Number:               ";
                    currentTicketNum = list.size();
                    int ticketNumber = 1; //getFreeNum
                    String msg = "\n"+"NYSC"+"                                "+getDateOnly()+ "\n"+new_divider+ "\n"
                            + ticketNo+x +"\n" +code+"                  "+itemSelected+StateCode+ "\n"
                            +"\n" +plea+ "\n"+new_single_divider;
                    msg += "\n" + "\n" + "\n";

                    mmOutputStream.write(msg.getBytes());

                    // tell the user data were sent
                    //myLabel.setText("Data sent.");
                    //sendButton.setBackgroundColor(Color.RED);

                    System.out.println("Data sent.");
                    x++;

                } catch (Exception e) {
                    e.printStackTrace();
                    return 4;
                }
            }
            else{
                System.out.println("doing 58");
                String new_divider = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
                String new_single_divider = "--------------------------------";
                String space = "                          ";

                Ticket ticket = new Ticket(StateCode,num);
                ArrayList<Ticket> list = new ArrayList<>();
                list.add(ticket);
                try {

                    String ticketNo = "Ticket Number:       ";
                    currentTicketNum = list.size();
                    int ticketNumber = 1; //getFreeNum
                    String msg = "\n"+"NYSC"+"                 "+getDateOnly()+ "\n"+new_divider+ "\n"
                            + ticketNo+x +"\n" +code+"          "+itemSelected+StateCode+ "\n"
                            +"\n" +plea58+ "\n"+new_single_divider;
                    msg += "\n" + "\n" + "\n";

                    mmOutputStream.write(msg.getBytes());

                    // tell the user data were sent
                    //myLabel.setText("Data sent.");
                    //sendButton.setBackgroundColor(Color.RED);

                    System.out.println("Data sent.");
                    x++;

                } catch (Exception e) {
                    e.printStackTrace();
                    return 4;
                }
            }

            return 5;
        }
    }
}
