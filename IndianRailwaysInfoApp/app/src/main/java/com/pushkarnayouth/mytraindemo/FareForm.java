package com.pushkarnayouth.mytraindemo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FareForm extends AppCompatActivity {
    EditText trainNo,age1,date;
    AutoCompleteTextView source,dest;
    Spinner trainClass1;
    Button fareButton;

    ArrayAdapter<String> adapter;
    List<String> responseList = new ArrayList<String>();
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_form);

        trainNo = (EditText)findViewById(R.id.txtTrainNo);
        source = (AutoCompleteTextView) findViewById(R.id.editText2);
        dest = (AutoCompleteTextView) findViewById(R.id.editText3);
        age1 = (EditText)findViewById(R.id.editText5);
        date = (EditText)findViewById(R.id.editText6);
        trainClass1 = (Spinner)findViewById(R.id.spinner2);
        fareButton = (Button)findViewById(R.id.fareButton);

        List<String> classList = new ArrayList<String>();
        classList.add("Select Class");
        classList.add("1A");
        classList.add("2A");
        classList.add("3A");
        classList.add("SL");
        classList.add("FC");
        classList.add("CC");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classList);
        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        trainClass1.setAdapter(dataAdapter1);

        //Code For AutoComplete TextView.
        new HttpGetTask().execute();
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, responseList);
        source.setAdapter(adapter);
        dest.setAdapter(adapter);

        //Code For DatePicker and hide Keyboard.
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(date.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                final Calendar c = Calendar.getInstance();
                int myear = c.get(Calendar.YEAR);
                int mmonth = c.get(Calendar.MONTH);
                int mdate = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(FareForm.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date.setText(i2 + "-0" + (i1 + 1) + "-" + i);
                    }
                },mdate,mmonth,myear);
                datePickerDialog.show();
            }
        });

        fareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String TrainCode,ClassCode,TrainDate,age;
                TrainCode = trainNo.getText().toString();
                age = age1.getText().toString();
                TrainDate = date.getText().toString();
                ClassCode = trainClass1.getSelectedItem().toString();


                String str1 = "(";
                String trainsourcecodes = source.getText().toString();
                int m = str1.length();
                int n = trainsourcecodes.length();
                String SourceCode = "";
                boolean res = issubstring(str1,trainsourcecodes,m,n);
                if(res) {
                    SourceCode = trainsourcecodes.substring(trainsourcecodes.indexOf("(") + 1, trainsourcecodes.indexOf(")"));
                }
                else{
                    SourceCode = trainsourcecodes;
                }
                String traindestinationcodes = dest.getText().toString();
                int o = traindestinationcodes.length();
                String DestCode = "";
                boolean res1 = issubstring(str1,traindestinationcodes,m,o);
                if(res1) {
                    DestCode = traindestinationcodes.substring(traindestinationcodes.indexOf("(") + 1, traindestinationcodes.indexOf(")"));
                }
                else{
                    DestCode = traindestinationcodes;
                }

                Intent i = new Intent(getApplicationContext(),FareActivity.class);
                i.putExtra("trainCode",TrainCode);
                i.putExtra("sourceCode",SourceCode);
                i.putExtra("destCode",DestCode);
                i.putExtra("Age",age);
                i.putExtra("trainDate",TrainDate);
                i.putExtra("classCode",ClassCode);
                startActivity(i);
            }
        });
    }

    private boolean issubstring(String str1, String trainsourcecodes, int m, int n) {
        if (m == 0)
            return true;
        if (n == 0)
            return false;
        if (str1.charAt(m-1) == trainsourcecodes.charAt(n-1))
            return issubstring(str1, trainsourcecodes, m-1, n-1);
        return issubstring(str1, trainsourcecodes, m, n-1);
    }

    //Code to get data from Api.
    private class HttpGetTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //URL url = new URL("http://192.168.43.127:3000/nametocode"); //Api when server is on in laptop.
                URL url = new URL("https://api.myjson.com/bins/163dj9");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line ="";
                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray JA = new JSONArray(data);
                for(int i =0 ;i <=JA.length() ; i++){
                    final JSONObject e = JA.getJSONObject(i);
                    String name = e.getString("trainname");
                    responseList.add(name);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
