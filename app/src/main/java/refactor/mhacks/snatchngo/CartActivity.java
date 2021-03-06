package refactor.mhacks.snatchngo;
import com.firebase.client.ChildEventListener;
import com.stripe.android.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc96_000 on 2016-02-20.
 */
public class CartActivity extends BaseActivity {
    public String[] Meal;
    public int[] Location;
    public ListView mealChoice;
    public Button checkOut;
    public SimpleDateFormat df;
    public String formattedDate;
    public EditText timeLeft;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        TextView t2 = (TextView) findViewById(R.id.titleCart);
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = df.format(c.getTime());
// Now formattedDate have current date/time
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String number = tm.getLine1Number().substring(1);
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://snatch-and-go.firebaseio.com/locations");


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                TextView t = (TextView) findViewById(R.id.costTot);
                int tot = 0;
                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                    if (snapshot.child("" + (i + 1)).child("pending").child("" + number).exists()) {
                        Log.d("ahlkawehg", "awjheglakwe");
                        tot += snapshot.child("" + (i + 1)).child("pending").child("" + number).child("items").getChildrenCount();
                    }
                }
                Meal = new String[tot];
                Location = new int[tot];
                double totalCost = 0;
                int counter = 0;
                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                    if (snapshot.child("" + (i + 1)).child("pending").child("" + number).exists()) {
                        for (DataSnapshot o : snapshot.child("" + (i + 1)).child("pending").child("" + number).child("items").getChildren()) {
                            Meal[counter] = (String) o.getKey();
                            Location[counter] = i;
                            for (DataSnapshot k : snapshot.child("" + (i + 1)).child("categories").getChildren()) {
                                if (k.child(Meal[counter]).exists()) {
                                    totalCost += (double) k.child(Meal[counter]).child("cost").getValue();
                                }
                            }
                            counter++;
                        }
                    }
                }
                Log.d("debug", counter + " " + tot);
                mealChoice = (ListView) findViewById(R.id.cartList);
                ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, Meal);
                mealChoice.setAdapter(adapterCat);
                mealChoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                      @Override
                                                      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                                          final int POS = position;
                                                          new AlertDialog.Builder(CartActivity.this)
                                                                  .setTitle("Remove from Cart")
                                                                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(DialogInterface dialog, int which) {
                                                                          Log.d("debug", "https://snatch-and-go.firebaseio.com/locations/" + (Location[POS] + 1) + "/pending/" + number + "/items");
                                                                          Firebase REF = new Firebase("https://snatch-and-go.firebaseio.com/locations/"+(Location[POS]+1)+"/pending/"+number+"/items");
                                                                          Map<String, Object> nickname = new HashMap<String, Object>();


                                                                          REF.child(Meal[POS]).setValue(null);

                                                                          Log.d("debug", "yess");
                                                                          //Intent intent = new Intent(CartActivity.this,CartActivity.class);
                                                                          //setIntent(intent);

                                                                      }
                                                                  })
                                                                  .setNegativeButton("No", null)
                                                                  .show();
                                                      }
                                                  }
                );
                t.setText("$" + Math.round(totalCost));
                timeLeft = (EditText) findViewById(R.id.editText);
                checkOut = (Button) findViewById(R.id.completeTransaction);
                checkOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CartActivity.this, "Purchase Successful", Toast.LENGTH_SHORT).show();
                        for (int i=0;i<snapshot.getChildrenCount();i++){
                            final int ii=i;
                            final Firebase REF = new Firebase("https://snatch-and-go.firebaseio.com/locations/" + (i+1) + "/pending/" + number);
                            REF.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Firebase REF2 = new Firebase("https://snatch-and-go.firebaseio.com/locations/" + (ii + 1) + "/orders/" + number);
                                    REF2.setValue(snapshot.getValue());
                                    REF2.child("paid").setValue(true);
                                    REF2.child("items").setValue(snapshot.child("items").getValue());
                                    formattedDate = formattedDate.replace(":","-");
                                    formattedDate = formattedDate.replace(" ","-");
                                    REF2.child("name").setValue("Diana Chang");
                                    REF2.child("time_requested").setValue(formattedDate);
                                    REF.setValue(null);
                                    REF.removeEventListener(this);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    System.out.println("The read failed: " + firebaseError.getMessage());
                                }
                            });



                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            Intent intent = new Intent(CartActivity.this,MainActivity.class);
            setIntent(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

