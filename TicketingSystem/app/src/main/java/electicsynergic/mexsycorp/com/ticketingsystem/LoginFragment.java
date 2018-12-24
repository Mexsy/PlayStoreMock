package electicsynergic.mexsycorp.com.ticketingsystem;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import electicsynergic.mexsycorp.com.ticketingsystem.Objects.Ticket;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private EditText editText;
    private ProgressDialog bar;
    private String StateCode;
    private TextView textView;
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

    private Context context = getContext();

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_BLUETOOTH = 0;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editText = (EditText) view.findViewById(R.id.editText);

        textView = (TextView) view.findViewById(R.id.printing_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar = new ProgressDialog(context);//creates a new progress dialog.
                bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//sets what direction the bar runs in.
                bar.setMessage("Processing data");//shows a message.


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
                }
                if (StateCode.trim().length()<7) {
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
                } else {
                    Toast.makeText(context, "Please wait. Printing Ticket", Toast.LENGTH_SHORT).show();
                    Log.d("Was button pressed","Yes");
                    findBT();
                    try {
                        // Use this if device has api above 6.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.BLUETOOTH)
                                    != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.BLUETOOTH},
                                        REQUEST_BLUETOOTH);
                            } else {
                                openBT();
                            }
                        }
                        Ticket ticket = new Ticket(StateCode,1);
                        sendData(ticket);
                        //closeBT();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        /*
        printButton = (Button) findViewById( R.id.print_button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    sendData();
                    //closeBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        */

        return view;
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

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("MTP_3-2")) {
                        mmDevice = device;
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

    // this will send text data to be printed by the bluetooth printer
    int sendData(Ticket ticket) throws IOException {
        textView.setText("Printing");
        String new_divider = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
        String new_single_divider = "------------------------------------------------";
        try {
            String ticketNo = "Ticket Number:               ";
            int ticketNumber = 1; //getFreeNum
            String msg = "\n"+"NYSC"+ "\n"+new_divider+ "\n"+ ticketNo+ticketNumber+"\n" + "\n"+new_single_divider;
            msg += "\n";

            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            //myLabel.setText("Data sent.");
            //sendButton.setBackgroundColor(Color.RED);

            System.out.println("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
            return 4;
        }
        return 5;
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            Log.d(TAG,"closed button clicked");
            System.out.println("closed button clicked");
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            // myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
