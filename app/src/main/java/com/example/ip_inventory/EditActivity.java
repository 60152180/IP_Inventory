package com.example.ip_inventory;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




import java.util.HashMap;

public class EditActivity extends AppCompatActivity {
    private TextView mId, mName, mPrice;
    private EditText mAmount;

    //private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Initialize EditText View
        mId = (TextView) findViewById(R.id.id);
        mName = (TextView) findViewById(R.id.name);
        mPrice = (TextView) findViewById(R.id.price);
        mAmount = (EditText) findViewById(R.id.amount);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null){
            mId.setText(bundle.getString("ID"));
            mName.setText(bundle.getString("NAME"));
            mPrice.setText(bundle.getString("PRICE"));
            mAmount.setText(bundle.getString("AMOUNT"));
        }
    }

    public void updateInven(View view) {
        String id = mId.getText().toString();
        String name = mName.getText().toString();
        String price = mPrice.getText().toString();
        String amount = mAmount.getText().toString();

        HashMap<String, String> requestedParams = new HashMap<>();
        requestedParams.put("id", id);
        requestedParams.put("name", name);
        requestedParams.put("price", price);
        requestedParams.put("amount", amount);
        Log.d("HashMap", requestedParams.get("id"));
        Toast.makeText(getApplicationContext(), "Success!! Inventory Updated ID : " + requestedParams.get("id"), Toast.LENGTH_LONG).show();

        PostRequestHandler postRequestHandler = new PostRequestHandler(Constant.UPDATE, requestedParams);
        postRequestHandler.execute();

        listInven(view);
    }



    public void listInven(View view) {
        Intent intent = new Intent(EditActivity.this, ViewActivity.class);
        startActivity(intent);
    }

}