package youth.electicsynery.com.managment.Objects;

import java.util.ArrayList;

/**
 * Created by Emeka on 10/25/2017.
 */

public class GroupItem {
    private String name;
    private ArrayList<ListItem> listItemArrayList = new ArrayList<>();

    public GroupItem(String name, ArrayList<ListItem> arrayList) {
        this.name = name;
        this.listItemArrayList = arrayList;
    }

    public void addMember(ListItem listItem){
        listItemArrayList.add(listItem);
    }

    public void removeMember(ListItem listItem){
        listItemArrayList.remove(listItem);
    }

    public String getName() {
        return name;
    }

    public String getNumberOfMembers(){
        return String.valueOf(listItemArrayList.size());
    }
}
