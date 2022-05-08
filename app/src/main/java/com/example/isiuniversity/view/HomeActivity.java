package com.example.isiuniversity.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isiuniversity.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private String username;
    private String password;
    protected LinearLayout homeLayout,lnNoProcessClaim;
    private ProcessDefinitionTask ProcessDefinitionTaskObject = null;
    private ProcessDefinitionTask2 ProcessDefinitionTaskObject2 = null;
    private ClaimTask claimTask = null;
    private UnClaimTask unclaimTask = null;
    private UserGetInfo UserGetInfoObject = null;
    private CircularImageView profileIv;
    private ImageButton btnSync,btnSettings;
    private TextView nameTv,typeTv,emailTv,tabMyTaskIv,tabGroupIv,txtNameClaimTo;
    private LinearLayout lnTab;
    private TextView txtCount;

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
        tabMyTaskIv = findViewById(R.id.tabMyTaskIv);
        tabGroupIv = findViewById(R.id.tabGroupIv);
        lnTab = findViewById(R.id.lnTab);
        txtCount = findViewById(R.id.txtCount);
        txtNameClaimTo = findViewById(R.id.txtNameClaimTo);

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
        lnNoProcessClaim = findViewById(R.id.lnNoProcessClaim);
        if(username.equals("agent")){
            profileIv.setImageResource(R.drawable.agent);
            lnTab.setVisibility(View.VISIBLE);
        } else if(username.equals("enseignant")){
            profileIv.setImageResource(R.drawable.enseignant);
            lnTab.setVisibility(View.VISIBLE);
        } else if(username.equals("etudiant")){
            profileIv.setImageResource(R.drawable.etudiant);
        }
        ProcessDefinitionTaskObject = new ProcessDefinitionTask();
        ProcessDefinitionTaskObject.execute();
        tabMyTaskIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyTaskUI();
                homeLayout.removeAllViews();
                ProcessDefinitionTaskObject2 = new ProcessDefinitionTask2();
                ProcessDefinitionTaskObject = new ProcessDefinitionTask();
                ProcessDefinitionTaskObject.execute();
            }
        });
        tabGroupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeLayout.removeAllViews();
                showGroupUI();
                ProcessDefinitionTaskObject = new ProcessDefinitionTask();
                ProcessDefinitionTaskObject2 = new ProcessDefinitionTask2();
                ProcessDefinitionTaskObject2.execute();
            }
        });
        UserGetInfoObject = new UserGetInfo();
        UserGetInfoObject.execute();
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, AccountActivity.class);
                intent1.putExtra("email",emailTv.getText());
                startActivity(intent1);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HomeActivity.this, AccountActivity.class);
                intent1.putExtra("email",emailTv.getText());
                startActivity(intent1);
            }
        });
        if(username.equals("enseignant")){
            btnSync.setVisibility(View.GONE);
        }
        else{
            btnSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    homeLayout.setVisibility(View.VISIBLE);
                    Intent intent2 = new Intent(HomeActivity.this, ProcessInstanceActivity.class);
                    startActivity(intent2);
                }
            });
        }
    }

    private void showMyTaskUI() {
        //show products ui and hide orders ui
        tabMyTaskIv.setTextColor(getResources().getColor(R.color.black));
        tabMyTaskIv.setBackgroundResource(R.drawable.shape_rect04);

        tabGroupIv.setTextColor(getResources().getColor(R.color.white));
        tabGroupIv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showGroupUI() {
        //show orders ui and hide orders ui

        tabMyTaskIv.setTextColor(getResources().getColor(R.color.white));
        tabMyTaskIv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabGroupIv.setTextColor(getResources().getColor(R.color.black));
        tabGroupIv.setBackgroundResource(R.drawable.shape_rect04);
    }

    public void processStart(CardView view) {
        Intent intent = new Intent(this, ProcessActivity.class);
        intent.putExtra("process_id", (String) view.getTag(R.string.process_key));
        System.out.println("process_id="+(String) view.getTag(R.string.process_key));
        intent.putExtra("process_name", (String) view.getTag(R.string.process_name));
        intent.putExtra("color", (Integer) view.getTag(R.string.color_key));
        ImageView iv = view.findViewById(R.id.imageView);
        intent.putExtra("icon", (Integer) iv.getTag());
        String transitionName = getString(R.string.transition_string);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, transitionName);
        this.startActivity(intent, options.toBundle());
    }

    public class ProcessDefinitionTask extends AsyncTask<Void, Void, Boolean> {
        JSONArray processDefinitions;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                if(username.equals("agent")||username.equals("enseignant")){
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
                } else{
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
                    //Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.colorThird;
                        int color_lighter = R.color.colorSecond;
                        int icon = R.drawable.ic_assignment;
                        int count=0;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            if(obj.getString("assignee").equals("agent")) {
                                if (obj.getString("name").equals("Verification demande")) {
                                    color = R.color.colorThird;
                                    color_lighter = R.color.colorSecond;
                                    icon = R.drawable.ic_assignment;
                                }
                                else if (obj.getString("name").equals("Ajouter commentaire Justificatif")) {
                                    color = R.color.orange_500;
                                    color_lighter = R.color.orange_200;
                                    icon = R.drawable.ic_copy;
                                }
                                else if (obj.getString("name").equals("Preparer Attestation")) {
                                    color = R.color.teal_700;
                                    color_lighter = R.color.teal_200;
                                    icon = R.drawable.ic_baseline_assignment;
                                }
                                homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter, icon,
                                        obj.getString("name"), obj.getString("description"),
                                        obj.getString("assignee")));
                                count++;
                            }
                        }
                        if(count==0){
                            txtCount.setVisibility(View.GONE);
                            lnNoProcessClaim.setVisibility(View.VISIBLE);
                            txtNameClaimTo.setText("No Process Claim to "+username);
                        } else {
                            lnNoProcessClaim.setVisibility(View.GONE);
                            txtCount.setVisibility(View.VISIBLE);
                            txtCount.setText("Number of tasks= " + count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(username.equals("enseignant")){
                    //Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.colorThird;
                        int color_lighter = R.color.colorSecond;
                        int icon = R.drawable.ic_assignment;
                        int count=0;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            if(obj.getString("assignee").equals("enseignant")) {
                                if (obj.getString("name").equals("Vérifier la copie")) {
                                    color = R.color.colorThird;
                                    color_lighter = R.color.colorSecond;
                                    icon = R.drawable.ic_assignment;
                                } else if (obj.getString("name").equals("Justificatif")) {
                                    color = R.color.orange_500;
                                    color_lighter = R.color.orange_200;
                                    icon = R.drawable.ic_copy;
                                }
                                homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter, icon,
                                        obj.getString("name"), obj.getString("description"),
                                        obj.getString("assignee")));
                                count++;
                            }
                        }
                        if(count==0){
                            txtCount.setVisibility(View.GONE);
                            lnNoProcessClaim.setVisibility(View.VISIBLE);
                            txtNameClaimTo.setText("No Process Claim to "+username);
                        } else {
                            lnNoProcessClaim.setVisibility(View.GONE);
                            txtCount.setVisibility(View.VISIBLE);
                            txtCount.setText("Number of tasks= "+count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    //Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
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
                                color = R.color.colorSecond;
                                color_lighter = R.color.colorThird;
                                icon = R.drawable.ic_copy;
                            }
                            homeLayout.addView(generateSimpleCard(obj.getString("id"), color, color_lighter,
                                    icon, obj.getString("name"), obj.getString("description")));
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

    public class ProcessDefinitionTask2 extends AsyncTask<Void, Void, Boolean> {

        JSONArray processDefinitions;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                if(username.equals("agent")||username.equals("enseignant")){
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
                } else{
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
                    //Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.colorThird;
                        int color_lighter = R.color.colorSecond;
                        int icon = R.drawable.ic_assignment;
                        int count=0;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            System.out.println("assignee="+obj.getString("assignee"));
                            if(!obj.getString("assignee").equals("agent")) {
                                if (obj.getString("name").equals("Verification demande")) {
                                    color = R.color.colorThird;
                                    color_lighter = R.color.colorSecond;
                                    icon = R.drawable.ic_assignment;
                                } else if (obj.getString("name").equals("Ajouter commentaire Justificatif")) {
                                    color = R.color.orange_500;
                                    color_lighter = R.color.orange_200;
                                    icon = R.drawable.ic_copy;
                                } else if (obj.getString("name").equals("Preparer Attestation")) {
                                    color = R.color.teal_700;
                                    color_lighter = R.color.teal_200;
                                    icon = R.drawable.ic_baseline_assignment;
                                }
                                homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter,
                                        icon, obj.getString("name"), obj.getString("description"),
                                        obj.getString("assignee")));
                                count++;
                            }
                        }
                        if(count==0){
                            txtCount.setVisibility(View.GONE);
                            lnNoProcessClaim.setVisibility(View.VISIBLE);
                            txtNameClaimTo.setText("No Process UnClaim");
                        }
                        else{
                            lnNoProcessClaim.setVisibility(View.GONE);
                            txtCount.setVisibility(View.VISIBLE);
                            txtCount.setText("Number of tasks= "+count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(username.equals("enseignant")){
                    //Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject obj;
                        int color = R.color.colorThird;
                        int color_lighter = R.color.colorSecond;
                        int icon = R.drawable.ic_assignment;
                        int count=0;
                        for (int i = 0; i < processDefinitions.length(); i++) {
                            obj = processDefinitions.getJSONObject(i);
                            if(!obj.getString("assignee").equals("enseignant")) {
                                if (obj.getString("name").equals("Vérifier la copie")) {
                                    color = R.color.colorThird;
                                    color_lighter = R.color.colorSecond;
                                    icon = R.drawable.ic_assignment;
                                } else if (obj.getString("name").equals("Justificatif")) {
                                    color = R.color.orange_500;
                                    color_lighter = R.color.orange_200;
                                    icon = R.drawable.ic_copy;
                                }
                                homeLayout.addView(generateCard(obj.getString("id"), color, color_lighter,
                                        icon, obj.getString("name"), obj.getString("description"),
                                        obj.getString("assignee")));
                                count++;
                            }
                        }
                        if(count==0){
                            txtCount.setVisibility(View.GONE);
                            lnNoProcessClaim.setVisibility(View.VISIBLE);
                            txtNameClaimTo.setText("No Process UnClaim to "+username);
                        }
                        else{
                            lnNoProcessClaim.setVisibility(View.GONE);
                            txtCount.setVisibility(View.VISIBLE);
                            txtCount.setText("Number of tasks= "+count);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    //Toast.makeText(HomeActivity.this, "Process Definitions added successfully !", Toast.LENGTH_SHORT).show();
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
                                color = R.color.colorSecond;
                                color_lighter = R.color.colorThird;
                                icon = R.drawable.ic_copy;
                            }
                            homeLayout.addView(generateSimpleCard(obj.getString("id"), color, color_lighter,
                                    icon, obj.getString("name"), obj.getString("description")));
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

    public class UserGetInfo extends AsyncTask<Void, Void, Boolean> {
        JSONArray infoObject;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                    Request request = new Request.Builder()
                            .url("http://digitalisi.tn:8080/engine-rest/user")
                            .get()
                            .addHeader("Authorization", AUTH)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        String json = response.body().string();
                        Log.v("JSON Response body", json);
                        try {
                            infoObject = new JSONArray(json);
                            System.out.println("email="+infoObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
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
                    try {
                        JSONObject obj;
                        obj = infoObject.getJSONObject(0);
                        String firstName=obj.getString("firstName");
                        String lastName=obj.getString("lastName");
                        nameTv.setText(firstName+" "+lastName);
                        typeTv.setText(firstName);
                        emailTv.setText(obj.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public CardView generateCard(String id, int color, int color_lighter, int icon, String name, String description,String assigned) {
        CardView cv = new CardView(this);
        cv.setClickable(true);
        cv.setFocusable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPx(100));
        params.setMargins(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
        cv.setLayoutParams(params);
        cv.setCardBackgroundColor(getResources().getColor(color));
        cv.setRadius(convertDpToPx(30));
        cv.setTransitionName(getResources().getString(R.string.transition_string));
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processStart((CardView) view);
            }
        });
        cv.setId(this.getResources().getIdentifier(id, "string", this.getPackageName()));
        cv.setTag(R.string.process_key, id);
        cv.setTag(R.string.process_name, name);
        cv.setTag(R.string.color_key, color);

        LinearLayout linearLayout_1 = new LinearLayout(this);
        linearLayout_1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout_1.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayout_2 = new LinearLayout(this);
        linearLayout_2.setOrientation(LinearLayout.VERTICAL);
        linearLayout_2.setGravity(Gravity.CENTER);
        linearLayout_2.setBackgroundResource(color_lighter);
        linearLayout_2.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPx(100), LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView imageView_1 = new ImageView(this);
        imageView_1.setImageResource(icon);
        imageView_1.setTag(icon);
        imageView_1.setId(R.id.imageView);
        imageView_1.setPaddingRelative(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
        imageView_1.setContentDescription(getResources().getString(R.string.icon));
        imageView_1.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPx(64), convertDpToPx(64)));
        linearLayout_2.addView(imageView_1);
        linearLayout_1.addView(linearLayout_2);

        if(!assigned.equals("null")){
            RelativeLayout linearLayout_3 = new RelativeLayout(this);
            //linearLayout_3.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout_3.setGravity(Gravity.CENTER_VERTICAL);
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.setMarginStart(convertDpToPx(10));
            linearLayout_3.setLayoutParams(layout);

            TextView textView_2 = new TextView(this);
            textView_2.setTypeface(Typeface.DEFAULT_BOLD);
            textView_2.setTextColor(getResources().getColor(android.R.color.white));
            textView_2.setText(name);
            textView_2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            ImageButton btnClaim = new ImageButton(this);
            btnClaim.setBackgroundResource(R.drawable.ic_baseline_stop_circle_24);
            RelativeLayout.LayoutParams parmsimgD = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            parmsimgD.addRule(RelativeLayout.ALIGN_PARENT_END);
            parmsimgD.setMarginEnd(30);
            linearLayout_3.addView(textView_2);
            linearLayout_3.addView(btnClaim, parmsimgD);
            /*TextView textView_3 = new TextView(this);
            textView_3.setGravity(Gravity.CENTER);
            textView_3.setText(description);
            textView_3.setPaddingRelative(convertDpToPx(0), convertDpToPx(0), convertDpToPx(0), convertDpToPx(0));
            textView_3.setTextColor(getResources().getColor(R.color.lightgray));
            textView_3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout_3.addView(textView_3);*/
            linearLayout_1.addView(linearLayout_3);
            cv.addView(linearLayout_1);
            btnClaim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unclaimTask = new UnClaimTask(id);
                    unclaimTask.execute();
                    cv.setVisibility(View.GONE);
                }
            });
        } else {
            RelativeLayout linearLayout_3 = new RelativeLayout(this);
            //linearLayout_3.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout_3.setGravity(Gravity.CENTER_VERTICAL);
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.setMarginStart(convertDpToPx(10));
            linearLayout_3.setLayoutParams(layout);

            TextView textView_2 = new TextView(this);
            textView_2.setTypeface(Typeface.DEFAULT_BOLD);
            textView_2.setTextColor(getResources().getColor(android.R.color.white));
            textView_2.setText(name);
            textView_2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            ImageButton btnClaim = new ImageButton(this);
            btnClaim.setBackgroundResource(R.drawable.ic_baseline_play_circle_24);
            RelativeLayout.LayoutParams parmsimgD = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            parmsimgD.addRule(RelativeLayout.ALIGN_PARENT_END);
            parmsimgD.setMarginEnd(30);
            linearLayout_3.addView(textView_2);
            linearLayout_3.addView(btnClaim, parmsimgD);
            /*TextView textView_3 = new TextView(this);
            textView_3.setGravity(Gravity.CENTER);
            textView_3.setText(description);
            textView_3.setPaddingRelative(convertDpToPx(0), convertDpToPx(0), convertDpToPx(0), convertDpToPx(0));
            textView_3.setTextColor(getResources().getColor(R.color.lightgray));
            textView_3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout_3.addView(textView_3);*/
            linearLayout_1.addView(linearLayout_3);
            cv.addView(linearLayout_1);
            btnClaim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    claimTask = new ClaimTask(id);
                    claimTask.execute();
                    cv.setVisibility(View.GONE);
                }
            });
        }
        return cv;
    }

    public CardView generateSimpleCard(String id, int color, int color_lighter, int icon, String name, String description) {
        CardView cv = new CardView(this);
        cv.setClickable(true);
        cv.setFocusable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, convertDpToPx(100));
        params.setMargins(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
        cv.setLayoutParams(params);
        cv.setCardBackgroundColor(getResources().getColor(color));
        cv.setRadius(convertDpToPx(30));
        cv.setTransitionName(getResources().getString(R.string.transition_string));
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processStart((CardView) view);
            }
        });
        cv.setId(this.getResources().getIdentifier(id, "string", this.getPackageName()));
        cv.setTag(R.string.process_key, id);
        cv.setTag(R.string.process_name, name);
        cv.setTag(R.string.color_key, color);

        LinearLayout linearLayout_1 = new LinearLayout(this);
        linearLayout_1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout_1.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayout_2 = new LinearLayout(this);
        linearLayout_2.setOrientation(LinearLayout.VERTICAL);
        linearLayout_2.setGravity(Gravity.CENTER);
        linearLayout_2.setBackgroundResource(color_lighter);
        linearLayout_2.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPx(100), LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView imageView_1 = new ImageView(this);
        imageView_1.setImageResource(icon);
        imageView_1.setTag(icon);
        imageView_1.setId(R.id.imageView);
        imageView_1.setPaddingRelative(convertDpToPx(10), convertDpToPx(10), convertDpToPx(10), convertDpToPx(10));
        imageView_1.setContentDescription(getResources().getString(R.string.icon));
        imageView_1.setLayoutParams(new LinearLayout.LayoutParams(convertDpToPx(64), convertDpToPx(64)));
        linearLayout_2.addView(imageView_1);
        linearLayout_1.addView(linearLayout_2);
        LinearLayout linearLayout_3 = new LinearLayout(this);
        linearLayout_3.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout_3.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setMarginStart(convertDpToPx(10));
        linearLayout_3.setLayoutParams(layout);

        TextView textView_2 = new TextView(this);
        textView_2.setTypeface(Typeface.DEFAULT_BOLD);
        textView_2.setTextColor(getResources().getColor(android.R.color.white));
        textView_2.setText(name);
        textView_2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout_3.addView(textView_2);
        /*TextView textView_3 = new TextView(this);
        textView_3.setGravity(Gravity.CENTER);
        textView_3.setText(description);
        textView_3.setPaddingRelative(convertDpToPx(0), convertDpToPx(0), convertDpToPx(0), convertDpToPx(0));
        textView_3.setTextColor(getResources().getColor(R.color.lightgray));
        textView_3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout_3.addView(textView_3);*/
        linearLayout_1.addView(linearLayout_3);
        cv.addView(linearLayout_1);
        return cv;
    }


    public class ClaimTask extends AsyncTask<Void, Void, Boolean> {
        private String process_id;

        public ClaimTask(String process_id) {
            this.process_id=process_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String json_string = "{\n\"userId\":";
                json_string += "\"" + username + "\"\n}";
                Log.v("JSON Request Body", json_string);
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, json_string);
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                System.out.println("process_id="+process_id);
                Request request = new Request.Builder()
                        .url("http://digitalisi.tn:8080/engine-rest/task/" + process_id + "/claim")
                        .post(body)
                        .addHeader("Authorization", AUTH)
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 204) {
                    return true;
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
                Toast.makeText(HomeActivity.this, "Claim sent successfully !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeActivity.this, "Claim Failure !", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            ProcessDefinitionTaskObject = null;
        }
    }

    public class UnClaimTask extends AsyncTask<Void, Void, Boolean> {
        private String process_id;

        public UnClaimTask(String process_id) {
            this.process_id=process_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String json_string = "{\n\"userId\":";
                json_string += "\"" + username + "\"\n}";
                Log.v("JSON Request Body", json_string);
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, json_string);
                String AUTH = "Basic " + android.util.Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
                System.out.println("process_id="+process_id);
                Request request = new Request.Builder()
                        .url("http://digitalisi.tn:8080/engine-rest/task/" + process_id + "/unclaim")
                        .post(body)
                        .addHeader("Authorization", AUTH)
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 204) {
                    return true;
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
                Toast.makeText(HomeActivity.this, "UnClaim sent successfully !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HomeActivity.this, "UnClaim Failure !", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            ProcessDefinitionTaskObject = null;
        }
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / getResources().getDisplayMetrics().DENSITY_DEFAULT));
    }
}