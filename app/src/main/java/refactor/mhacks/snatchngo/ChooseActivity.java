package refactor.mhacks.snatchngo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

/**
 * Created by abc96_000 on 2016-02-20.
 */
public class ChooseActivity extends ActionBarActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);
        Firebase.setAndroidContext(this);
        Firebase rootRef = new Firebase("https://snatch-and-go.firebaseio.com/web/data");


    }
}