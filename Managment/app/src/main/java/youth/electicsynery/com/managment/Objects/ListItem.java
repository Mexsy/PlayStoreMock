package youth.electicsynery.com.managment.Objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Emeka on 9/27/2017.
 */

public class ListItem implements Parcelable {
    private String name, number, email, address;

    public ListItem(String name, String number, String email, String address) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.address = address;
    }

    public ListItem(Parcel in){
        super();
        readFromParcel(in);
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(number);
        parcel.writeString(address);
    }

    public static final Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>() {
        public ListItem createFromParcel(Parcel in) {
            return new ListItem(in);
        }

        public ListItem[] newArray(int size) {

            return new ListItem[size];
        }

    };

    public void readFromParcel(Parcel in) {
        name = in.readString();
        email = in.readString();
        number = in.readString();
        address = in.readString();
    }
}
