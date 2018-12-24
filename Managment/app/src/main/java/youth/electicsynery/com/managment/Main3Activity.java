package youth.electicsynery.com.managment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {
    private TextView textView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private String seven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        textView = findViewById(R.id.num_to_gen);
        Button button = findViewById(R.id.gen_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                String number = textView.getText().toString();
                String work = lastFour(number.trim());
                seven = firstSeven(number);
                System.out.println(work);
                boolean c = plusHun(work);
                //boolean b = minusHun(work);
                if (c){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", sendToNumbers(arrayList));
                    //intent.putExtra("sms_body","Body of Message");
                    startActivity(intent);
                }
            }
        });

        Button button2 = findViewById(R.id.gen_send_lower);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.clear();
                String number = textView.getText().toString();
                String work = lastFour(number.trim());
                seven = firstSeven(number);
                System.out.println(work);
                //boolean c = plusHun(work);
                boolean b = minusHun(work);
                if (b){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("address", sendToNumbers(arrayList));
                    //intent.putExtra("sms_body","Body of Message");
                    startActivity(intent);
                }
            }
        });


    }

    private boolean plusHun(String number) {
        int num = Integer.parseInt(number);
        int track = num;
        for (int x = 0; x<num; x++){
            if (x == 100){
                break;
            }
            else {
                track++;
                arrayList.add(seven+String.valueOf(track));
            }
        }
        System.out.println(arrayList);
        return true;
    }

    private boolean minusHun(String number) {
        int num = Integer.parseInt(number);
        int track = num;
        for (int x = 0; x<num; x++){
            if (x == 100){
                break;
            }
            else {
                track--;
                arrayList.add(seven+String.valueOf(track));
            }
        }
        System.out.println(arrayList.toString());
        return true;
    }

    private String sendToNumbers(ArrayList<String> list) {
        StringBuilder numbers = new StringBuilder();
        for (int i = 0; i<list.size(); i++){
            numbers.append(list.get(i).trim()).append(";");
        }
        return numbers.toString();
    }

    public String firstSeven(String str) {
        return str.length() < 7 ? str : str.substring(0, 7);
    }

    public String lastFour(String str) {
        if (str.length() == 4) {
            return str;
        } else if (str.length() > 4) {
            return str.substring(str.length() - 4);
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has less than 4 characters!");
        }
    }
}
