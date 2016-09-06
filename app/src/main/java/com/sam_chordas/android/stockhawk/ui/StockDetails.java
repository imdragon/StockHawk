package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.sam_chordas.android.stockhawk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StockDetails extends Activity {

    private LineChartView myLineChart;
    public TextView stockLabel, startDateLabel, endDateLabel;
    public String currentStockSymbol;

    public LineSet stockPrices;
    public String[] startDate = new String[3];
    public String[] endDate = new String[3];
    public int startEnd = 0;

    Calendar myCalendar = Calendar.getInstance();


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            Log.e("StockDetails", String.valueOf(monthOfYear) + " " + String.valueOf(dayOfMonth) + " " + String.valueOf(year));
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String stringDate = sdf.format(myCalendar.getTime());
            if (startEnd == 0) {
                startDate[0] = stringDate.substring(0, 2);
                startDate[1] = stringDate.substring(3, 5);
                startDate[2] = Integer.toString(year);
                startDateLabel.setText(stringDate);
                Log.e("date", startDate[0] + " " + startDate[1] + " " + startDate[2]);
            }
            if (startEnd == 1) {
                endDate[0] = stringDate.substring(0, 2);
                endDate[1] = stringDate.substring(3, 5);
                endDate[2] = Integer.toString(year);
                endDateLabel.setText(stringDate);
            }
          
//            showDate();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        currentStockSymbol = getIntent().getStringExtra("stockName");
        startDateLabel = (TextView) findViewById(R.id.startDateLabel);
        endDateLabel = (TextView) findViewById(R.id.endDateLabel);

        stockLabel = (TextView) findViewById(R.id.stockNameLabel);
        stockLabel.setText(currentStockSymbol);
        myLineChart = (LineChartView) findViewById(R.id.stockLinechart);


    }

    public void getHistoricalData(View v) {
        new GetHistoricalData().execute(currentStockSymbol, null, null);
    }

    public void startDatePicker(View v) {
        startEnd = 0;

        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();


    }

    public void endDatePicker(View v) {
        startEnd = 1;


        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showDate() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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
        private String[] graphLabels;
        private float[] graphValues;

        final String QUERY = "query";
        final String RESULTS = "results";
        final String QUOTES = "quote";

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

                url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata" +
                        "%20where%20symbol%20%3D%20%22" + stockSymbol[0]
                        + "%22%20and%20startDate%20%3D%20%22" + startDate[2] + "-" + startDate[0] + "-" + startDate[1] + "%22%20and%20" +
                        "endDate%20%3D%20%22" + endDate[2] + "-" + endDate[0] + "-" + endDate[1] + "%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.e("StockDetails", url.toString());
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

                results = results.getJSONObject(QUERY);
                results = results.getJSONObject(RESULTS);
                quotes = results.getJSONArray(QUOTES);
                Log.e("results are", quotes.toString());
//                quotes = results.getJSONArray("quote");
                graphLabels = new String[quotes.length()];
                graphValues = new float[quotes.length()];
                for (int i = quotes.length() - 1; i >= 0; i--) {
                    Log.e("StockDetails Async", "Loop ran: " + i + " times");
                    quoteString.append("\n ***** \n");
                    JSONObject newQuote = quotes.getJSONObject(i);
                    quoteString.append("On: ");
                    quoteString.append(newQuote.getString("Date"));
                    graphLabels[i] = newQuote.getString("Date");
                    quoteString.append(" closed at: $");
                    int trim = newQuote.getString("Close").indexOf(".");
                    quoteString.append(newQuote.getString("Close").substring(0, trim + 3));
                    graphValues[i] = Float.valueOf(newQuote.getString("Close").substring(0, trim + 3));


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
            int[] minMax;
//            stockLabel.setText(total.toString());
            stockLabel.setText(quoteString.toString());
            stockPrices = new LineSet(graphLabels, graphValues);
//            stockPrices = new LineSet(new String[] {"first","second","third"}, new float[]{23.4f, 22.3f, 33.32f});
            minMax = getMinMax(graphValues);

            stockLabel.append("\n" + minMax[0] + " " + minMax[1]);
            //Data setup
            stockPrices.setColor(Color.parseColor("#758cbb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#758cbb"))
                    .setThickness(2)
                    .setDashed(new float[]{10f, 10f})
            ;

            myLineChart.addData(stockPrices);


            // Chart setup
            myLineChart.setBorderSpacing(Tools.fromDpToPx(20))
                    .setAxisBorderValues(0, 20)
                    .setYLabels(AxisController.LabelPosition.OUTSIDE)
                    .setLabelsColor(Color.parseColor("#6a84c3"))
                    .setXAxis(true)
                    .setYAxis(true)
                    .setAxisBorderValues(minMax[0] - 10, minMax[1] + 10);


            Animation anim = new Animation()
                    .setEasing(new BounceEase());

            myLineChart.show(anim);
        }

        public int[] getMinMax(float[] sortMe) {
            float max = 0;
            float min = 0;
            for (int j = 0; j < sortMe.length; j++) {
                if (sortMe[j] > max) {
                    max = sortMe[j];
                }
            }
            min = max;
            for (int j = 0; j < sortMe.length; j++) {
                if (sortMe[j] < min) {
                    min = sortMe[j];
                }
            }

            int[] values = {Math.round(min), Math.round(max)};
            return values;
        }
    }
}