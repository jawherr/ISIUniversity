package com.example.isiuniversity.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.isiuniversity.R;
import com.example.isiuniversity.adapter.ProcessAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProcessInstanceActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProcessInstanceTask ProcessInstanceTaskObject = null;
    private String[] mDataset = new String[0];
    private String username;
    private String password;
    private int auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_instance);
        SharedPreferences sharedPref = getSharedPreferences("ISIUniversityPerf", Context.MODE_PRIVATE);
        auth = sharedPref.getInt("auth", 0);
        username = sharedPref.getString("username", "");
        password = sharedPref.getString("password", "");
        if (auth == 0) {
            Intent start = new Intent(this, LoginActivity.class);
            startActivity(start);
            finish();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.process_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ProcessInstanceTaskObject = new ProcessInstanceTask();
        ProcessInstanceTaskObject.execute();
    }

    public void back(View view) {
        finish();
    }

    public class ProcessInstanceTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray processInstances;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                if(username.equals("agent")){
                    Request request = new Request.Builder()
                            .url("http://digitalisi.tn:8080/engine-rest/process-instance/?businessKey=etudiant")
                            .get()
                            .addHeader("Authorization", AUTH)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        Log.v("JSON Response body", json);
                        try {
                            processInstances = new JSONArray(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else if(username.equals("etudiant")){
                    Request request = new Request.Builder()
                            .url("http://digitalisi.tn:8080/engine-rest/process-definition?startableBy=" + username)
                            .get()
                            .addHeader("Authorization", AUTH)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        Log.v("JSON Response body", json);
                        try {
                            processInstances = new JSONArray(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                //Toast.makeText(ProcessInstanceActivity.this, "Process Instances added successfully !", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject obj;
                    mDataset = new String[processInstances.length()];
                    for (int i = 0; i < processInstances.length(); i++) {
                        obj = processInstances.getJSONObject(i);
                        mDataset[i] = obj.getString("id");
                        Log.v("Process Instances List", mDataset[i]);
                    }
                    mAdapter = new ProcessAdapter(mDataset);
                    mRecyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(ProcessInstanceActivity.this, "Process Definitions Failure !", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            ProcessInstanceTaskObject = null;
        }

    }
}
        