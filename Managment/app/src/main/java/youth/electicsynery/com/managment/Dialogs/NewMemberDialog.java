package youth.electicsynery.com.managment.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import youth.electicsynery.com.managment.DataStorage;
import youth.electicsynery.com.managment.Main2Activity;
import youth.electicsynery.com.managment.Objects.ListItem;
import youth.electicsynery.com.managment.R;

public class NewMemberDialog extends DialogFragment {
    private Context context;
    private Dialog dialog;
    private Button button;
    private TextView textView;
    private ArrayList<ListItem> arrayList = new ArrayList<>();
    private ListItem listItem;
    private TextView textView1;
    private EditText nameEntered,addressEntered,emailEntered,numberEntered;
    private View view;
    private String name,email,number,address;
    private DataStorage db;
    private boolean emailExsits = false;


    public NewMemberDialog() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        if (mArgs!=null){
            arrayList = mArgs.getParcelableArrayList("list");
            System.out.println(arrayList);
        }
        else {
            System.out.println("Arg empty");
        }
        dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_member_dialog, container, false);
        context = getContext();
        db = new DataStorage(context);


        button = view.findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = saveUser(view);
                if (i == 0){
                    Toast toast = Toast.makeText(context,"Youth member successfully added",Toast.LENGTH_LONG);
                    toast.show();
                    if (listItem!=null){
                        arrayList.add(listItem);
                    }
                    else {
                        System.out.println("An error has occurred");
                    }
                    dialog.cancel();
                }
                else {
                    Toast toast = Toast.makeText(context,"Failed to add youth member, Try again" + i,Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        Button x_button = view.findViewById(R.id.exit);
        x_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return view;
    }

    private boolean getYouth(String email){
        Log.d(Main2Activity.TAG, "Get youth method called");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("youth");
        DatabaseReference contact = database.getReference("contact");
        final String emailTocheck = email.toLowerCase().trim();

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot v : dataSnapshot.getChildren()){
                    Log.d(Main2Activity.TAG,String.valueOf(v.child("email")));
                    String valueEmail = v.child("email").getValue(String.class);
                    Log.d(Main2Activity.TAG, "Email is: "+ valueEmail);
                    System.out.println("Email is: "+ valueEmail);
                    if (emailTocheck.trim().toLowerCase().equals(valueEmail.trim())){
                        emailExsits = true;
                        System.out.println("Email from db: "+ valueEmail + "Email entered: "+ emailTocheck);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(Main2Activity.TAG, "Failed to read value.", error.toException());
            }
        });
        return emailExsits;
    }

    public int saveUser(View view){
        nameEntered = view.findViewById(R.id.et_full_name);
        addressEntered = view.findViewById(R.id.et_address);
        numberEntered = view.findViewById(R.id.phoneNumber);
        emailEntered = view.findViewById(R.id.email_address_form);

        boolean e_done,n_done,a_done,num_done=false;
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        name = nameEntered.getText().toString();
        email = emailEntered.getText().toString();
        number = numberEntered.getText().toString();
        address = addressEntered.getText().toString();
        e_done = checkEmailError(email);
        n_done = checkNameError(name);
        a_done = checkAddressError(address);
        num_done = checkNumberError(number);
        if (e_done && n_done && a_done && num_done){
            if (writeNewUser(name,number,email,address)){
                listItem = new ListItem(name,number,email,address);
                return 0;
            }
        }
        else {
            return 1;
        }
        return 11;
    }

    private boolean checkNumberError(String number) {
        if (number.trim().isEmpty()) {
            numberEntered.setError("Please enter a phone number");
            numberEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    numberEntered.setError(null);
                }
            });
        }
        else if (number.trim().length() < 11) {
            numberEntered.setError("Phone number must contain 11 digits");
            numberEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    numberEntered.setError(null);
                }
            });
        }
        else {
            return true;
        }
        return false;
    }

    private boolean checkAddressError(String address) {
        if (address.trim().isEmpty()) {
            addressEntered.setError("Please enter an address");
            addressEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    addressEntered.setError(null);
                }
            });
        }
        else {
            return true;
        }
        return false;
    }

    private boolean checkNameError(String name) {
        if (name.trim().isEmpty()) {
            nameEntered.setError("Please enter name of youth");
            nameEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    nameEntered.setError(null);
                }
            });
        }
        else {
            return true;
        }
        return false;
    }

    private boolean checkEmailError(String email) {
        CharSequence emailToComp = email.trim();
        if (email.trim().isEmpty()) {
            emailEntered.setError("Please enter an email address");
            emailEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    emailEntered.setError(null);
                }
            });
        }
        else if (!isValidEmail(emailToComp)) {
            emailEntered.setError("Please enter a valid email address");
            emailEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    emailEntered.setError(null);
                }
            });
        }
        else if (getYouth(email)){
            emailEntered.setError("This email address is already registered");
            emailEntered.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    emailEntered.setError(null);
                }
            });
        }
        else {
            return true;
        }
        return false;
    }

    private boolean writeNewUser(String name, String number, String email, String address) {
        ListItem user = new ListItem(name, number, email.toLowerCase(), address);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        System.out.println(db.getFreeID());
        Task task = myRef.child("youth").child(String.valueOf(db.getFreeID())).setValue(user);
        return task.isSuccessful();
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
