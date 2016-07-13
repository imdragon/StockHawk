package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

import org.w3c.dom.Text;

public class StockDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        TextView stockLabel = (TextView) findViewById(R.id.stockNameLabel);
        stockLabel.setText(getIntent().getStringExtra("stockName"));
    }
}
