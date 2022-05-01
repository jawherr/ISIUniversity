package com.example.isiuniversity.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.isiuniversity.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private String username;
    private String password;
    protected LinearLayout homeLayout;
    private ProcessDefinitionTask ProcessDefinitionTaskObject = null;
    private CircularImageView profileIv;
    private ImageButton btnSync,btnSettings;
    private TextView nameTv,typeTv,emailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profileIv = findViewById(R.id.profileIv);
        btnSync = findViewById(R.id.btnSync);
        btnSettings = findViewById(R.id.btnSettings);
        nameTv = findViewById(R.id.nameTv);
        typeTv = findViewById(R.id.typeTv);
        emailTv = findViewById(R.id.emailTv);

        SharedPreferences sharedPref = getSharedPreferences("ISIUniversityPerf", Context.MODE_PRIVATE);
        int auth = sharedPref.getInt("auth", 0);
        username = sharedPref.getString("username", null);
        password = sharedPref.getString("password", null);
        if (auth == 0) {
            Intent start = new Intent(this, LoginActivity.class);
            startActivity(start);
            finish();
        }

        homeLayout = findViewById(R.id.home_layout);

        ProcessDefinitionTaskObject = new ProcessDefinitionTask();
        ProcessDefinitionTaskObject.execute();
        if(username.equals("agent")){
            profileIv.setImageResource(R.drawable.agent);
        } else if(username.equals("enseignant")){
            profileIv.setImageResource(R.drawable.enseignant);
        } else if(username.equals("etudiant")){
            profileIv.setImageResource(R.drawable.etudiant);
        }
        nameTv.setText(username);
        typeTv.setText(username);
        emailTv.setText(username+"01@bpm.tn");
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, AccountActivity.class);
                startActivity(intent1);
            }
        });
        if(username.equals("enseignant")){
            btnSync.setVisibility(View.GONE);
        }else{
            btnSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent2 = new Intent(HomeActivity.this, ProcessInstanceActivity.class);
                    startActivity(intent2);
                }
            });
        }
        /*btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent3);
            }
        });*/
    }

    public void processStart(CardView view) {
        Intent intent = new Intent(this, ProcessActivity.class);
        intent.putExtra("process_id", (String) view.getTag(R.string.process_key));
        System.out.println("process_id="+(String) view.getTag(R.string.process_key));
        intent.putExtra("color", (Integer) view.getTag(R.string.color_key));
        ImageView iv = view.findViewById(R.id.imageView);
        intent.putExtra("icon", (Integer) iv.getTag());
        String transitionName = getString(R.string.transition_string);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, transitionName);
        //Start the Intent
        this.startActivity(intent, options.toBundle());
    }

    public class ProcessDefinitionTask extends AsyncTask<Void, Void, Boolean> {

        JSONArray processDefinitions;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                if(username.equals("agent")){
                    Request request = new Request.Builder()
                            .url("http://digitalisi.tn:8080/engine-rest/task")
                            .get()
                            .addHeader("Authorization", AUTH)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        Log.v("JSON Response body", json);
                        try {
                            processDefinitions = new JSONArray(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else if(username.equals("enseignant")){
                    Request request = new Request.Builder()
                            .url("http://digitalisi.tn:8080/engine-rest/task")
                            .get()
                            .addHeader("Authorization", AUTH)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        Log.v("JSON Response body", json);
                        try {
                            processDefinitions = new JSONArray(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        return false;
                    }
                }else{
                    Request request = new Request.Builder()
                            .url("http://digitalisi.tn:8080/engine-rest/process-definition?latest=true&active=true&startableInTasklist=true&startablePermissionCheck=true&firstResult=0&maxResults=15")
                            .get()
                            .addHeader("Authorization", AUTH)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        Log.v("JSON Response body", json);
                        try {
                            processDefinitions = new JSONArray(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                if(username.equals("agent")){
                    Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.purple_200;
                        int color_lighter = R.color.purple_700;
                        int icon = R.drawable.ic_assignment;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            if (obj.getString("name").equals("Verification demande")) {
                                color = R.color.purple_200;
                                color_lighter = R.color.purple_700;
                                icon = R.drawable.ic_assignment;
                            } else if (obj.getString("name").equals("Ajouter commentaire Justificatif")) {
                                color = R.color.orange_500;
                                color_lighter = R.color.orange_200;
                                icon = R.drawable.ic_copy;
                            }else if (obj.getString("name").equals("Preparer Attestation")) {
                                color = R.color.teal_700;
                                color_lighter = R.color.teal_200;
                                icon = R.drawable.ic_baseline_assignment;
                            }
                            homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter, icon, obj.getString("name"), obj.getString("description")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(username.equals("enseignant")){
                    Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.purple_200;
                        int color_lighter = R.color.purple_700;
                        int icon = R.drawable.ic_assignment;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            if (obj.getString("name").equals("Vérifier la copie")) {
                                color = R.color.purple_200;
                                color_lighter = R.color.purple_700;
                                icon = R.drawable.ic_assignment;
                            } else if (obj.getString("name").equals("Justificatif")) {
                                color = R.color.orange_500;
                                color_lighter = R.color.orange_200;
                                icon = R.drawable.ic_copy;
                            }
                            homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter, icon, obj.getString("name"), obj.getString("description")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.teal_200;
                        int color_lighter = R.color.teal_700;
                        int icon = R.drawable.ic_assignment;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            if (obj.getString("name").equals("Demande Attestation de presence")) {
                                color = R.color.teal_200;
                                color_lighter = R.color.teal_700;
                                icon = R.drawable.ic_assignment;
                            } else if (obj.getString("name").equals("Demande Verification Note")) {
                                color = R.color.purple_500;
                                color_lighter = R.color.purple_700;
                                icon = R.drawable.ic_copy;
                            }
                            homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter, icon, obj.getString("name"), obj.getString("description")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(HomeActivity.this, "Process Definitions Failure !", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            ProcessDefinitionTaskObject = null;
        }

    }

    public CardView generateCard(String id, int color, int color_lighter, int icon, String name, String description) {
        CardView cv = new CardView(this);
        cv.setClickable(true);
        cv.setFocusable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPx(100));
        params.setMargins(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
        cv.setLayoutParams(params);
        cv.setCardBackgroundColor(getResources().getColor(color));
        cv.setRadius(convertDpToPx(4));
        cv.setTransitionName(getResources().getString(R.string.transition_string));
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processStart((CardView) view);
            }
        });
        cv.setId(this.getResources().getIdentifier(id, "string", this.getPackageName()));
        cv.setTag(R.string.process_key, id);
        cv.setTag(R.string.color_key, color);

        LinearLayout linearLayout_645 = new LinearLayout(this);
        linearLayout_645.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout_645.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayout_951 = new LinearLayout(this);
        linearLayout_951.setOrientation(LinearLayout.VERTICAL);
        linearLayout_951.setGravity(Gravity.CENTER);
        linearLayout_951.setBackgroundResource(color_lighter);
        linearLayout_951.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPx(100), LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView imageView_280 = new ImageView(this);
        imageView_280.setImageResource(icon);
        imageView_280.setTag(icon);
        imageView_280.setId(R.id.imageView);
        imageView_280.setPaddingRelative(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
        imageView_280.setContentDescription(getResources().getString(R.string.icon));
        imageView_280.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPx(64), convertDpToPx(64)));
        linearLayout_951.addView(imageView_280);
        linearLayout_645.addView(linearLayout_951);

        LinearLayout linearLayout_854 = new LinearLayout(this);
        linearLayout_854.setOrientation(LinearLayout.VERTICAL);
        linearLayout_854.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams layout_938 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout_938.setMarginStart(convertDpToPx(10));
        linearLayout_854.setLayoutParams(layout_938);

        TextView textView_954 = new TextView(this);
        textView_954.setTypeface(Typeface.DEFAULT_BOLD);
        textView_954.setTextColor(getResources().getColor(android.R.color.white));
        textView_954.setText(name);
        textView_954.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout_854.addView(textView_954);

        TextView textView_373 = new TextView(this);
        textView_373.setGravity(Gravity.CENTER);
        textView_373.setText(description);
        textView_373.setPaddingRelative(convertDpToPx(0), convertDpToPx(0), convertDpToPx(0), convertDpToPx(0));
        textView_373.setTextColor(getResources().getColor(R.color.lightgray));
        textView_373.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout_854.addView(textView_373);
        linearLayout_645.addView(linearLayout_854);
        cv.addView(linearLayout_645);
        return cv;
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / getResources().getDisplayMetrics().DENSITY_DEFAULT));
    }
}