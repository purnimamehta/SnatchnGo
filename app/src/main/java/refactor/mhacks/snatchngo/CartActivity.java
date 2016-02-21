package refactor.mhacks.snatchngo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by abc96_000 on 2016-02-20.
 */
public class CartActivity extends ActionBarActivity {
    public String[] Meal;
    public int[] Location;
    public ListView mealChoice;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        TextView t2 = (TextView) findViewById(R.id.titleCart);
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        final String number = tm.getLine1Number();
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://snatch-and-go.firebaseio.com/locations");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView t = (TextView) findViewById(R.id.costTot);
                int tot = 0;
                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                    if (snapshot.child(""+(i+1)).child("orders").child("" + number).exists()) {
                        Log.d("ahlkawehg","awjheglakwe");
                        tot += snapshot.child(""+(i+1)).child("orders").child("" + number).child("items").getChildrenCount();
                    }
                }
                Meal = new String[tot];
                Location = new int[tot];
                double totalCost = 0;
                int counter = 0;
                for (int i = 0; i < snapshot.getChildrenCount(); i++) {
                    if (snapshot.child(""+(i+1)).child("orders").child("" + number).exists()) {
                        for (DataSnapshot o : snapshot.child(""+(i+1)).child("orders").child("" + number).child("items").getChildren()) {
                            Meal[counter] = (String) o.getKey();
                            Location[counter]=i;
                            for (DataSnapshot k : snapshot.child(""+(i+1)).child("categories").getChildren()) {
                                if (k.child(Meal[counter]).exists()) {
                                    totalCost += (double) k.child(Meal[counter]).child("cost").getValue();
                                    }
                            }
                            counter++;
                        }
                    }
                }
                Log.d("debug",counter+" "+tot);
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
                                                                          Log.d("debug","https://snatch-and-go.firebaseio.com/locations/"+(Location[POS]+1)+"/orders/"+number.replace("+","%2B")+"/items/"+Meal[POS].replace(" ","%20"));
                                                                          Firebase REF = new Firebase("https://snatch-and-go.firebaseio.com/locations/"+(Location[POS]+1)+"/orders/"+number.replace("+","%2B")+"/items/"+Meal[POS].replace(" ","%20"));
                                                                          REF.setValue(null);
                                                                          REF.removeValue();
                                                                          Log.d("debug","yess");
                                                                          finish();
                                                                          startActivity(getIntent());

                                                                      }
                                                                  })
                                                                  .setNegativeButton("No", null)
                                                                  .show();
                                                      }
                                                  }
                );
                t.setText("$" + Math.round(totalCost));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

}