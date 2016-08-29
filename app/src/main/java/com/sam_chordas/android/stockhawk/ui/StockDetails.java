package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StockDetails extends Activity {

    public TextView stockLabel;
    public String currentStockSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        currentStockSymbol = getIntent().getStringExtra("stockName");
        stockLabel = (TextView) findViewById(R.id.stockNameLabel);
        stockLabel.setText(currentStockSymbol);

    }

    public void getSomething(View v) {
        new GetHistoricalData().execute(currentStockSymbol, null, null);
    }


//  https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20
// where%20symbol%20%3D%20%22DELL%22%20and%20startDate%20%3D%20%222009-09-11%22%20and%20endDate%20
// %3D%20%222010-03-10%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org
// %2Falltableswithkeys&callback=

    class GetHistoricalData extends AsyncTask<String, Void, Void> {

        public StringBuilder total = new StringBuilder();
        public StringBuilder quoteString = new StringBuilder();
        private JSONObject results;
        private JSONArray quotes;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param stockSymbol The parameters of the task.  Stock symbol for now.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(String... stockSymbol) {
            Log.e("StockDetails Async", "doinBackground Running");
            URL url = null;
            try {
//                https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22tsla%22%20and%20startDate%20%3D%20%222009-09-11%22%20and%20endDate%20%3D%20%222010-03-10%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=

                url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22" + stockSymbol[0] + "%22%20and%20startDate%20%3D%20%222016-01-01%22%20and%20endDate%20%3D%20%222016-02-01%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // preparing a reader to go through the response
            BufferedReader r = null;
            try {
                r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // below allows for controlled reading of potentially large text
            String line;
            try {
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            JSON processing

            try {
                results = new JSONObject(total.toString());

                results = results.getJSONObject("query");
                results = results.getJSONObject("results");
                quotes = results.getJSONArray("quote");
                Log.e("results are", quotes.toString());
//                quotes = results.getJSONArray("quote");
                quoteString.append("Something else but this");
                for (int i = 0; i < quotes.length(); i++) {
                    Log.e("StockDetails Async", "Loop ran: "+i+" times");
                    quoteString.append("\n ***** \n");
                    JSONObject newQuote = quotes.getJSONObject(i);
                    quoteString.append(newQuote.getString("Date"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


//

            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param aVoid The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Void aVoid) {

//            stockLabel.setText(total.toString());
            stockLabel.setText(quoteString.toString());
        }
    }
}