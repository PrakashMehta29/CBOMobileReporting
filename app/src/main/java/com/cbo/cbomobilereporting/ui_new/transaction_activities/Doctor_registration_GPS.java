package com.cbo.cbomobilereporting.ui_new.transaction_activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.cbo.cbomobilereporting.emp_tracking.GPSTracker;
import com.cbo.cbomobilereporting.emp_tracking.MyCustomMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import locationpkg.Const;
import services.CboServices;
import services.Sync_service;
import utils.adapterutils.SpinAdapter;
import utils.adapterutils.SpinAdapter_new;
import utils.adapterutils.SpinnerModel;
import utils.networkUtil.NetworkUtil;
import utils_new.Custom_Variables_And_Method;
import utils_new.GPS_Timmer_Dialog;

public class Doctor_registration_GPS extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    TextView loc,address,name_tag, refress_address1;
    Button drname, back,submit;
    ImageView dr_img,show_address;
    Custom_Variables_And_Method customVariablesAndMethod;
    CBO_DB_Helper cbohelp;
    String dr_name = "", dr_id = "", doc_name = "";
    ArrayList<SpinnerModel> docList;
    List<String> data1;
    SpinnerModel[] TitleName;
    ArrayList<SpinnerModel> array_sort;
    ImageView spinImg,refress_address,loc_img;
    Context context;
    NetworkUtil networkUtil;
    MyCustomMethod myCustomMethod;

    private AlertDialog myalertDialog = null;

    Button tab_doctor,tab_chemist,tab_stockist;
    CheckBox address_verified;
    public ProgressDialog progress1;
    private  static final int MESSAGE_INTERNET=1,REGISTRATION=2,REFRESH=3,MESSAGE_INTERNET_ADDRESS=4;
    String lat_long ="",last_lat_long ="",doc_type="D";

    private final int REQUEST_CHECK_SETTINGS = 1000;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_registration__gps);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_hadder);
        TextView hader_text = (TextView) findViewById(R.id.hadder_text_1);
        hader_text.setText("Registration");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
        }
        context = this;
        loc = (TextView) findViewById(R.id.loc);
        address = (TextView) findViewById(R.id.address);
        loc_img= (ImageView) findViewById(R.id.loc_img);
        name_tag= (TextView) findViewById(R.id.name_tag);
        drname = (Button) findViewById(R.id.drname);
        back = (Button) findViewById(R.id.bkfinal_button);
        submit = (Button) findViewById(R.id.add);
        customVariablesAndMethod= Custom_Variables_And_Method.getInstance();
        cbohelp = new CBO_DB_Helper(context);
        progress1 = new ProgressDialog(this);
        data1 = new ArrayList<String>();
        networkUtil = new NetworkUtil(context);
        myCustomMethod = new MyCustomMethod(context);
        address_verified= (CheckBox) findViewById(R.id.address_verified);
        refress_address= (ImageView) findViewById(R.id.refress_address);
        refress_address1= (TextView) findViewById(R.id.refress_address1);
        dr_img= (ImageView) findViewById(R.id.spinner_img_drCall);
        show_address = (ImageView) findViewById(R.id.show_address);

        tab_doctor= (Button) findViewById(R.id.doctor);
        tab_chemist= (Button) findViewById(R.id.chemist);
        tab_stockist= (Button) findViewById(R.id.stokist);

        if(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"SHOW_ADD_REGYN","N").equals("Y")){
            show_address.setVisibility(View.VISIBLE);
        }else{
            show_address.setVisibility(View.GONE);
        }

        Intent intent=getIntent();
        if (intent.getStringExtra("id")!= null){
            dr_id=intent.getStringExtra("id");
            doc_name=intent.getStringExtra("name");
            drname.setText(intent.getStringExtra("name"));
            docList = new ArrayList<SpinnerModel>();
            //new getAddress().execute();
            setLetLong(false);
            if (intent.getStringExtra("type").equals("D")){
                name_tag.setText("Dr. Name");
                doc_type="D";
                tab_chemist.setVisibility(View.INVISIBLE);
                tab_stockist.setVisibility(View.INVISIBLE);
                tab_doctor.setBackgroundResource(R.drawable.tab_selected);

            }else if (intent.getStringExtra("type").equals("C")){
                doc_type="C";
                name_tag.setText("Chemist Name");
                tab_doctor.setVisibility(View.INVISIBLE);
                tab_stockist.setVisibility(View.INVISIBLE);
                tab_chemist.setBackgroundResource(R.drawable.tab_selected);
            }else if (intent.getStringExtra("type").equals("DA") || intent.getStringExtra("type").equals("PA")){
                doc_type="DP";
                if(intent.getStringExtra("type").equals("PA")){
                    name_tag.setText("Poultry Name");
                    tab_doctor.setText("Poultry");
                }else{
                    name_tag.setText("Dairy Name");
                    tab_doctor.setText("Dairy");
                }

                tab_chemist.setVisibility(View.INVISIBLE);
                tab_stockist.setVisibility(View.INVISIBLE);

                tab_doctor.setBackgroundResource(R.drawable.tab_selected);
            }else{
                doc_type="S";
                name_tag.setText("Stockist Name");
                tab_doctor.setVisibility(View.INVISIBLE);
                tab_chemist.setVisibility(View.INVISIBLE);
                tab_stockist.setBackgroundResource(R.drawable.tab_selected);
            }

        }else {

            name_tag.setText("Dr. Name");
            drname.setText("---Select---");
            docList = new ArrayList<SpinnerModel>();

            new DoctorData().execute("D");

            drname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickDrName();
                }
            });
            dr_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickDrName();
                }
            });

            tab_chemist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doc_type="C";
                    drname.setText("---Select---");
                    docList = new ArrayList<SpinnerModel>();
                    name_tag.setText("Chemist Name");
                    new DoctorData().execute("C");
                    tab_chemist.setBackgroundResource(R.drawable.tab_selected);
                    tab_doctor.setBackgroundResource(R.drawable.tab_deselected);
                    tab_stockist.setBackgroundResource(R.drawable.tab_deselected);
                }
            });

            tab_doctor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doc_type="D";
                    drname.setText("---Select---");
                    docList = new ArrayList<SpinnerModel>();
                    name_tag.setText("Dr. Name");
                    new DoctorData().execute("D");
                    tab_chemist.setBackgroundResource(R.drawable.tab_deselected);
                    tab_doctor.setBackgroundResource(R.drawable.tab_selected);
                    tab_stockist.setBackgroundResource(R.drawable.tab_deselected);
                }
            });

            tab_stockist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doc_type="S";
                    drname.setText("---Select---");
                    docList = new ArrayList<SpinnerModel>();
                    name_tag.setText("Stockist Name");
                    new DoctorData().execute("D");
                    tab_chemist.setBackgroundResource(R.drawable.tab_deselected);
                    tab_stockist.setBackgroundResource(R.drawable.tab_selected);
                    tab_doctor.setBackgroundResource(R.drawable.tab_deselected);
                }
            });
        }

        refress_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //customVariablesAndMethod.UpdateGPS_Location_Forcefully(context);
                Custom_Variables_And_Method.GPS_STATE_CHANGED=true;
                Custom_Variables_And_Method.GPS_STATE_CHANGED_TIME=customVariablesAndMethod.get_currentTimeStamp();
                new GPS_Timmer_Dialog(context,mHandler,"Scanning your Location...",REFRESH).show();
            }
        });

        refress_address1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //customVariablesAndMethod.UpdateGPS_Location_Forcefully(context);
                Custom_Variables_And_Method.GPS_STATE_CHANGED=true;
                Custom_Variables_And_Method.GPS_STATE_CHANGED_TIME=customVariablesAndMethod.get_currentTimeStamp();
                new GPS_Timmer_Dialog(context,mHandler,"Scanning your Location...",REFRESH).show();
            }
        });

        show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loc.getText().toString().isEmpty() && !last_lat_long.equals(lat_long)) {
                    last_lat_long = lat_long;

                    String loc_cord[] = lat_long.split(",");
                    if (loc_cord.length==2) {
                        //Start of call to service

                        HashMap<String, String> request = new HashMap<>();
                        request.put("slatitude", loc_cord[0]);
                        request.put("slongitude", loc_cord[1]);
                        request.put("KeyYN", "Y");


                        ArrayList<Integer> tables = new ArrayList<>();
                        tables.add(0);

                        progress1.setMessage("Please Wait.. \n" +
                                "Scan in Progess...");
                        progress1.setCancelable(false);
                        progress1.show();

                        new CboServices(context, mHandler).customMethodForAllServices(request, "AddressByLatLong", MESSAGE_INTERNET_ADDRESS, tables);

                        //End of call to service
                        //new getAddress().execute();
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dr_id.isEmpty()){

                    if (doc_type.equals("D")) {
                        customVariablesAndMethod.getAlert(context,"Select !!!","Please Select a Doctor");
                    }else if (doc_type.equals("DA")) {
                        customVariablesAndMethod.getAlert(context,"Select !!!","Please Select a Dairy");
                    }else if (doc_type.equals("C"))  {
                        customVariablesAndMethod.getAlert(context,"Select !!!","Please Select a Chemist");
                    }else {
                        customVariablesAndMethod.getAlert(context,"Select !!!","Please Select a Stockist");
                    }
                }else if ( loc.getText() !=null  && !loc.getText().equals("")){

                    Custom_Variables_And_Method.GPS_STATE_CHANGED=true;
                    Custom_Variables_And_Method.GPS_STATE_CHANGED_TIME=customVariablesAndMethod.get_currentTimeStamp();
                    new GPS_Timmer_Dialog(context,mHandler,"Registration in Progress...",REGISTRATION).show();


                }else{
                    //customVariablesAndMethod.UpdateGPS_Location_Forcefully(context);
                    Custom_Variables_And_Method.GPS_STATE_CHANGED=true;
                    Custom_Variables_And_Method.GPS_STATE_CHANGED_TIME=customVariablesAndMethod.get_currentTimeStamp();
                    new GPS_Timmer_Dialog(context,mHandler,"Scanning your Location...",REFRESH).show();
                }
            }
        });




    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_INTERNET:
                    progress1.dismiss();
                    if ((null != msg.getData())) {

                        parser_worktype(msg.getData());

                    }
                    break;
                case REGISTRATION:

                    lat_long=customVariablesAndMethod.get_best_latlong(context);
                    cbohelp.updateLatLong(lat_long,dr_id,doc_type,"");

                    if (!networkUtil.internetConneted(context)) {
                        getAlert(context, "Registered", drname.getText() + "\nRegistration Successfully Completed... ");
                    }else {

                        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("SyncComplete"));
                        Sync_service.ReplyYN="Y";
                        progress1.setMessage("Please Wait..\n" +
                                " Fetching data");
                        progress1.setCancelable(false);
                        progress1.show();
                        startService(new Intent(context, Sync_service.class));
                    }
                    break;
                case REFRESH:
                    setLetLong(false);
                    break;
                case MESSAGE_INTERNET_ADDRESS:
                    parser_Address(msg.getData());
                    break;
                case 99:
                    progress1.dismiss();
                    if ((null != msg.getData())) {
                        customVariablesAndMethod.msgBox(context,msg.getData().getString("Error"));
                    }
                    break;
                default:
                    progress1.dismiss();

            }
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contex, Intent intent) {
            if (progress1 != null) {
                progress1.dismiss();
            }

            if (intent.getStringExtra("message").equals("Y")) {
                getAlert(context, "Registered", drname.getText() + "\nRegistration Successfully Completed... ");
            }
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        }
    };

    public void parser_worktype(Bundle result) {
        if (result!=null ) {

            try {

                String table0 = result.getString("Tables0");
                JSONArray jsonArray1 = new JSONArray(table0);
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject c = jsonArray1.getJSONObject(i);
                    //rptName.add(new SpinnerModel(c.getString("PA_NAME"),c.getString("PA_ID")));
                    cbohelp.updateLatLong(lat_long,dr_id,doc_type,"");
                }
                finish();
                progress1.dismiss();
            } catch (JSONException e) {
                Log.d("MYAPP", "objects are: " + e.toString());
                CboServices.getAlert(this,"Missing field error",getResources().getString(R.string.service_unavilable) +e.toString());
                e.printStackTrace();
            }

        }
        //Log.d("MYAPP", "objects are1: " + result);
        progress1.dismiss();

    }

    public void parser_Address(Bundle result) {
        if (result!=null ) {

            try {

                String table0 = result.getString("Tables0");
                JSONArray jsonArray1 = new JSONArray(table0);
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject c = jsonArray1.getJSONObject(i);
                    address.setText(c.getString("Address"));
                }

                progress1.dismiss();
            } catch (JSONException e) {
                Log.d("MYAPP", "objects are: " + e.toString());
                CboServices.getAlert(this,"Missing field error",getResources().getString(R.string.service_unavilable) +e.toString());
                e.printStackTrace();
            }

        }
        //Log.d("MYAPP", "objects are1: " + result);
        progress1.dismiss();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private class DoctorData extends AsyncTask<String, String, ArrayList<SpinnerModel>> {
        ProgressDialog pd;

        @Override
        protected final ArrayList<SpinnerModel> doInBackground(String... params) {

            try {
                docList.clear();
                if (params[0].equals("D")) {
                    Cursor c = cbohelp.getDoctorListLocal();
                    if (c.moveToFirst()) {
                        do {
                            docList.add(new SpinnerModel(c.getString(c.getColumnIndex("dr_name")) + "-" + c.getString(c.getColumnIndex("DR_AREA")),
                                    c.getString(c.getColumnIndex("dr_id")), c.getString(c.getColumnIndex("LAST_VISIT_DATE")),
                                    c.getString(c.getColumnIndex("CLASS")), c.getString(c.getColumnIndex("POTENCY_AMT")),
                                    c.getString(c.getColumnIndex("ITEM_NAME")), c.getString(c.getColumnIndex("ITEM_POB")),
                                    c.getString(c.getColumnIndex("ITEM_SALE")), c.getString(c.getColumnIndex("DR_AREA")),
                                    c.getString(c.getColumnIndex("PANE_TYPE")), c.getString(c.getColumnIndex("DR_LAT_LONG")),
                                    c.getString(c.getColumnIndex("FREQ")), c.getString(c.getColumnIndex("NO_VISITED")),
                                    c.getString(c.getColumnIndex("DR_LAT_LONG2")), c.getString(c.getColumnIndex("DR_LAT_LONG3")),
                                    c.getString(c.getColumnIndex("COLORYN")), c.getString(c.getColumnIndex("CALLYN"))
                                    , c.getString(c.getColumnIndex("CRM_COUNT")), c.getString(c.getColumnIndex("DRCAPM_GROUP"))));
                        } while (c.moveToNext());

                    }
                }else if (params[0].equals("C")){
                    Cursor c = cbohelp.getChemistListLocal();

                    if (c.moveToFirst()) {
                        do {

                            docList.add(new SpinnerModel(c.getString(c.getColumnIndex("chem_name")), c.getString(c.getColumnIndex("chem_id")), c.getString(c.getColumnIndex("LAST_VISIT_DATE")), c.getString(c.getColumnIndex("DR_LAT_LONG")), c.getString(c.getColumnIndex("DR_LAT_LONG2")), c.getString(c.getColumnIndex("DR_LAT_LONG3")), c.getString(c.getColumnIndex("CALLYN"))));
                        } while (c.moveToNext());
                    }
                }else {
                    Cursor c = cbohelp.getStockistListLocal();


                    if (c.moveToFirst()) {
                        do {

                            docList.add(new SpinnerModel(c.getString(c.getColumnIndex("pa_name")), c.getString(c.getColumnIndex("pa_id")),"", c.getString(c.getColumnIndex("PA_LAT_LONG")), c.getString(c.getColumnIndex("PA_LAT_LONG2")), c.getString(c.getColumnIndex("PA_LAT_LONG3")), c.getString(c.getColumnIndex("CALLYN"))));
                        } while (c.moveToNext());
                    }
                }


            } catch (Exception e) {
            }
            return docList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setTitle("CBO");
            pd.setMessage("Processing......." + "\n" + "please wait");
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(ArrayList<SpinnerModel> s) {
            super.onPostExecute(s);
            pd.dismiss();
            try {
                pd.dismiss();
                if ((!s.isEmpty()) || (s.size() < 0)) {
                    TitleName = new SpinnerModel[s.size()];
                    for (int i = 0; i <= s.size(); i++) {
                        TitleName[i] = s.get(i);


                    }

                    array_sort = new ArrayList<SpinnerModel>(Arrays.asList(TitleName));
                } else {
                    customVariablesAndMethod.getAlert(context,"Data not found","No Doctor in Planned Dcr...");

                    spinImg.setClickable(false);
                    drname.setClickable(false);
                    //  spinImg.setClickable(false);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void onClickDrName() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        final ListView listview = new ListView(context);
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
        array_sort = new ArrayList<SpinnerModel>(Arrays.asList(TitleName));
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editText);
        layout.addView(listview);
        myDialog.setView(layout);
        SpinAdapter_new arrayAdapter = new SpinAdapter_new(context, R.layout.spin_row, array_sort,1);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myalertDialog.dismiss();
                dr_id = ((TextView) view.findViewById(R.id.spin_id)).getText().toString();
                doc_name = ((TextView) view.findViewById(R.id.spin_name)).getText().toString().split("-")[0];
                drname.setText(doc_name);
                if (((TextView) view.findViewById(R.id.distance)).getText().toString().equals("Registration pending...")){
                    new getLatLong().execute();
                }else {
                    customVariablesAndMethod.getAlert(context,"Registered ",doc_name +" is already registered \n For any correction Please contact your Head-Office");
                    drname.setText("---Select---");
                    dr_id="";
                    doc_name="";
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                int textlength = editText.getText().length();
                array_sort.clear();
                for (int i = 0; i < TitleName.length; i++) {
                    if (textlength <= TitleName[i].getName().length()) {

                        if (TitleName[i].getName().toLowerCase().contains(editText.getText().toString().toLowerCase().trim())) {
                            array_sort.add(TitleName[i]);
                        }
                    }
                }
                try {
                    listview.setAdapter(new SpinAdapter(context, R.layout.spin_row, array_sort));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        myalertDialog = myDialog.show();
    }


    public void setLetLong(Boolean Skip_Verification) {
        int mode =3;
        Boolean GPS_enabled=false;
        mode = new MyCustomMethod(context).getLocationMode(context);
        GPS_enabled=!myCustomMethod.checkGpsEnable();



        if (GPS_enabled || mode != 3) {
            // showSettings();
            customVariablesAndMethod.msgBox(context,"Please Swicth ON your GPS");
            getGpsSetting();
        }else if(!customVariablesAndMethod.checkIfCallLocationValid(context,true,Skip_Verification)) {
            LocalBroadcastManager.getInstance(context).registerReceiver(mLocationUpdated,
                    new IntentFilter(Const.INTENT_FILTER_LOCATION_UPDATE_AVAILABLE));
            customVariablesAndMethod.msgBox(context,"Verifing Your Location");

        }/* else if (Custom_Variables_And_Method.FORCEFULLY_ACCEPT_GPS_LOCATION || Custom_Variables_And_Method.GLOBAL_LATLON.equalsIgnoreCase("0.0,0.0") || customVariablesAndMethod.IsLocationTooOld(context,0)) {
            new getLatLong().execute();
        }*/else{
            lat_long=customVariablesAndMethod.get_best_latlong(context);
            loc.setText(lat_long);
            setLocImg();
        }




    }

    private BroadcastReceiver mLocationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context contex, Intent intent) {
            Location location = intent.getParcelableExtra(Const.LBM_EVENT_LOCATION_UPDATE);
            setLetLong(true);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocationUpdated);
        }
    };


    public void setLocImg(){
        if(lat_long != null && !lat_long.equals("")){
            loc_img.setImageResource(R.drawable.loc);
            //new getAddress().execute();
        }else{
            loc_img.setImageResource(R.drawable.un_loc);
        }
    }

    //////////////////////////////
    public void getGpsSetting() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
        }

        ////
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                                     @Override
                                     public void onResult(LocationSettingsResult result) {
                                         final Status status = result.getStatus();
                                         final LocationSettingsStates state = result.getLocationSettingsStates();
                                         switch (status.getStatusCode()) {
                                             case LocationSettingsStatusCodes.SUCCESS:
                                                 // All location settings are satisfied. The client can initialize location
                                                 // requests here.

                                                 break;
                                             case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                                 // Location settings are not satisfied. But could be fixed by showing the user
                                                 // a dialog.
                                                 try {
                                                     // Show the dialog by calling startResolutionForResult(),
                                                     // and check the result in onActivityResult().
                                                     status.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTINGS);
                                                 } catch (IntentSender.SendIntentException e) {
                                                     // Ignore the error.
                                                 }
                                                 break;
                                             case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                                 // Location settings are not satisfied. However, we have no way to fix the
                                                 // settings so we won't show the dialog.
                                                 // mycon.msgBox("you are here");
                                                 customVariablesAndMethod.msgBox(context,"Please Swicth ON your GPS from Settings");
                                                 break;
                                         }
                                     }
                                 }


        );


    }


    private class getLatLong extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        GPSTracker tracker=new GPSTracker(context);
        @Override
        protected final String doInBackground(String[] params) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //lat_long=customVariablesAndMethod.get_best_latlong(context);
            return lat_long;
            //return customVariablesAndMethod.getAddressByLatLong(context,lat_long);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setTitle("CBO");
            pd.setMessage("Processing......." + "\n" + "please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            for (int i = 0; i <=10; i++) {
                // Location loc= customVariablesAndMethod.latLongFromInternet(context);
                Location loc= tracker.getLocation();
                if (!networkUtil.internetConneted(context)) {
                    lat_long="";
                    customVariablesAndMethod.Connect_to_Internet_Msg(context);
                    /*Doctor_registration_GPS.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });*/
                    break;

                }else if (loc!=null) {

                    lat_long =loc.getLatitude()+","+loc.getLongitude();

                    if (customVariablesAndMethod.IsValidLocation(context, lat_long,0)) {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "shareLatLong", lat_long);
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "last_location_update_time_in_minites", customVariablesAndMethod.get_currentTimeStamp());
                        customVariablesAndMethod.putObject(context,"currentBestLocation",loc);
                        customVariablesAndMethod.putObject(context,"currentBestLocation_Validated",loc);
                        //customVariablesAndMethod.getAddressByLatLong(context,lat_long);
                        break;
                    }

                }

                if (10 == i) {
                    //Custom_Variables_And_Method.GLOBAL_LATLON = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "shareLatLong");
                    lat_long="";
                }
            }
            pd.dismiss();
            tracker.stopUsingGPS();
            loc.setText(lat_long);
            setLocImg();
            //address_verified.setChecked(false);

        }
    }

    private class getAddress extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        @Override
        protected final String doInBackground(String[] params) {

            //lat_long=customVariablesAndMethod.get_best_latlong(context);
            //return lat_long;
            return customVariablesAndMethod.getAddressByLatLong(context,lat_long);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setTitle("CBO");
            pd.setMessage("Scanning......." + "\n" + "please wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            address.setText(s);
            //address_verified.setChecked(false);

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item != null) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAlert(final Context context, final String title, final String massege ) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogLayout = inflater.inflate(R.layout.alert_view, null);
        final TextView Alert_title= (TextView) dialogLayout.findViewById(R.id.title);

        final TextView Alert_message= (TextView) dialogLayout.findViewById(R.id.message);
        final TableLayout Alert_message_list= (TableLayout) dialogLayout.findViewById(R.id.table_view);
        final Button Alert_Positive= (Button) dialogLayout.findViewById(R.id.positive);
        Alert_title.setText(title);

        final TextView pa_id_txt= (TextView) dialogLayout.findViewById(R.id.PA_ID);
        pa_id_txt.setText(""+Custom_Variables_And_Method.PA_ID);


        Alert_message.setText(massege);
        Alert_message_list.setVisibility(View.GONE);


        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);


        final AlertDialog dialog = builder1.create();

        dialog.setView(dialogLayout);
        Alert_Positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

}
