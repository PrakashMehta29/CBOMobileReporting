package utils_new;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import services.CboServices;
import services.Up_Dwn_interface;
import utils.adapterutils.PobAdapter;
import utils.adapterutils.PobModel;
import utils.networkUtil.NetworkUtil;
import utils.upload_download;

public class Dr_Sample_Dialog implements Up_Dwn_interface {

    Handler h1;
    Integer response_code;
    Bundle Msg;


    ListView mylist;
    Button save;
    Custom_Variables_And_Method customVariablesAndMethod;
    int PA_ID = 0;
    String dr_id="0";
    ResultSet rs;
    CBO_DB_Helper cbohelp;
    String item_id = "", item_name = "", item_qty = "", item_pob = "";
    ArrayAdapter<PobModel> adapter;
    List<PobModel> list = new ArrayList<PobModel>();
    ArrayList<String> data1, data2, data3, data4, data5;
    StringBuilder itemid, itemname, itemqty, itempob;
    StringBuilder sb2, sb3, sb4, sb5;
    double mainval = 0.0;
    String callFromRcpa;
    boolean check, check_Rx;
    Context context;
    EditText search;
    Boolean checkIfPOB_Entered=false;

    String sample_name="",sample_pob="",sample_sample="",sample_noc="";


    Dialog dialog;
    public ProgressDialog progress1;
    private  static final int MESSAGE_INTERNET=1;


    public Dr_Sample_Dialog(@NonNull Context context, Handler hh, Bundle Msg, Integer response_code) {

        this.context = context;
        h1=hh;
        this.response_code=response_code;
        this.Msg=Msg;
    }


    public void Show() {
        //this.onNameChange = onNameChange;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dr_sample, null, false);

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.color.White_new);
        window.setGravity(Gravity.CENTER);


        TextView hader_text = (TextView) view.findViewById(R.id.hadder_text_1);
        hader_text.setText("Sample/POB");
        // hader_text.setText( Msg.getString("header"));

        TextView prescribe_text = (TextView) view.findViewById(R.id.check_text_prescribe);

        if (Msg != null) {
            sample_name = Msg.getString("sample_name");
            sample_pob = Msg.getString("sample_pob");
            sample_sample = Msg.getString("sample_sample");
            sample_noc= Msg.getString("sample_noc");
        }

        search= (EditText) view.findViewById(R.id.search);
        mylist = (ListView) view.findViewById(R.id.dr_sample_list);
        save = (Button) view.findViewById(R.id.dr_sample_save);
        cbohelp = new CBO_DB_Helper(context);
        mylist.setItemsCanFocus(true);
        mylist.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        PA_ID = Custom_Variables_And_Method.PA_ID;
        dr_id=Custom_Variables_And_Method.DR_ID;
        data1 = new ArrayList<String>();
        data2 = new ArrayList<String>();
        data3 = new ArrayList<String>();
        data4 = new ArrayList<String>();
        data5 = new ArrayList<String>();
        sb2 = new StringBuilder();
        sb3 = new StringBuilder();
        sb4 = new StringBuilder();
        sb5 = new StringBuilder();
        itemid = new StringBuilder();
        itemname = new StringBuilder();
        itemqty = new StringBuilder();
        itempob = new StringBuilder();

        customVariablesAndMethod=Custom_Variables_And_Method.getInstance();

        if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"DR_RX_ENTRY_YN","N").equals("N")){

            prescribe_text.setVisibility(View.GONE);

        }





        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                for (int l = 0; l < list.size(); l++) {
                    if (!search.getText().toString().equals("") &&  search.getText().length() <= list.get(l).getName().length()) {
                        if (list.get(l).getName().toLowerCase().contains(search.getText().toString().toLowerCase().trim())) {
                            //mylist.smoothScrollToPosition(l);
                            mylist.smoothScrollToPositionFromTop(l,l,500);
                            for (int j = l; j < list.size(); j++) {
                                if (search.getText().length() <= list.get(j).getName().length()) {
                                    if (list.get(j).getName().toLowerCase().contains(search.getText().toString().toLowerCase().trim())) {
                                        list.get(j).setHighlight(true);
                                    }else{
                                        list.get(j).setHighlight(false);
                                    }
                                }else{
                                    list.get(j).setHighlight(false);
                                }
                            }
                            break;
                        }else{
                            list.get(l).setHighlight(false);
                        }
                    }else{
                        list.get(l).setHighlight(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try{
                    String total_pob="";
                    boolean count=false;
                    for (int i = 0; i < list.size(); i++) {
                        check = list.get(i).isSelected();
                        if (check ) {
                            count=true;
                            break;
                        }
                    }
                    if (count) {
                        for (int i = 0; i < list.size(); i++) {
                            check = list.get(i).isSelected();

                            if (check && !list.get(i).getPob().equals("")) {
                                total_pob = list.get(i).getPob();
                                break;
                            }
                        }
                    }
                    if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"SAMPLE_POB_MANDATORY").equals("Y") && total_pob.equals("")) {
                        customVariablesAndMethod.msgBox(context,"POB can't be blank");
                    }else {
                        saveDoctorSample_CheckPob();
                    }
                } catch (Exception e) {
                    List toEmailList = Arrays.asList("mobilereporting@cboinfotech.com".split("\\s*,\\s*"));
                    new SendMailTask().execute("mobilereporting@cboinfotech.com",
                            "mreporting", toEmailList, Custom_Variables_And_Method.COMPANY_CODE + ": " + "Error in DR_sample", context.getResources().getString(R.string.app_name) + "\n Company Code :" + Custom_Variables_And_Method.COMPANY_CODE + "\n DCR ID :" + Custom_Variables_And_Method.DCR_ID + "\n PA ID : " + Custom_Variables_And_Method.PA_ID + "\n App version : " + Custom_Variables_And_Method.VERSION   + "\n Error Alert :" + "Error in DR_sample" + "\n" + e.toString());

                    CboServices.getAlert(context,"Error!!!", e.toString());
                    e.printStackTrace();
                }
            }
        });

        new Doback().execute(dr_id,"0");

    }



    public ArrayList<Integer> getdoclist() {
        ArrayList<Integer> myno = new ArrayList<Integer>();
        ArrayList<String> doclist = cbohelp.getDoctorList();
        for (int i = 0; i < doclist.size(); i++) {
            myno.add(Integer.parseInt(doclist.get(i)));
        }
        return myno;
    }


    @Override
    public void onDownloadComplete() {
        new Doback().execute(dr_id,"1");
    }

    class Doback extends AsyncTask<String, String, List<PobModel>> {
        ProgressDialog pd;
        String who = "0";

        @Override
        protected List<PobModel> doInBackground(String... params) {

            list.clear();
            int i = 0;
            String ItemIdNotIn = "0";
            who = params[1];

            Cursor c = cbohelp.getAllProducts(params[0]);
            if (c.moveToFirst()) {
                do {
                    list.add(new PobModel(c.getString(c.getColumnIndex("item_name")), c.getString(c.getColumnIndex("item_id")), c.getString(c.getColumnIndex("stk_rate")), c.getString(c.getColumnIndex("sn")),
                            c.getInt(c.getColumnIndex("STOCK_QTY")), c.getInt(c.getColumnIndex("BALANCE"))));
                } while (c.moveToNext());
            }

            cbohelp.close();
            return list;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(context);
            pd.setTitle("CBO");
            pd.setMessage("Processing......." + "\n" + "please wait");
            pd.setProgressStyle(android.R.attr.progressBarStyleSmall);
            pd.show();
        }

        @Override
        protected void onPostExecute(List<PobModel> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            adapter = new PobAdapter((Activity) context, result,customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"NOC_HEAD","").isEmpty());

            if (adapter.getCount() != 0) {

                String[] sample_name1= sample_name.split(",");
                String[] sample_qty1= sample_sample.split(",");
                String[] sample_pob1= sample_pob.split(",");
                String[] sample_noc1= sample_noc.split(",");

                for (int i=0;i<sample_name1.length;i++){
                    for (int j=0;j<list.size();j++) {
                        if (sample_name1[i].equals(list.get(j).getName())) {
                            list.get(j).setPob(sample_pob1[i]);
                            list.get(j).setScore(sample_qty1[i]);
                            list.get(j).setNOC(sample_noc1[i]);
                            list.get(j).setBalance( list.get(j).getBalance() + Integer.parseInt(sample_qty1[i]));
                        }
                    }
                }

                mylist.setAdapter(adapter);
                dialog.show();

            } else if(who.equals("0")){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("CBO");
                builder1.setIcon(R.drawable.alert1);
                builder1.setMessage(" No Data In List.." + "\n" + "Please Download Data.....");
                builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetworkUtil networkUtil = new NetworkUtil(context);
                        if (!networkUtil.internetConneted(context)) {
                            customVariablesAndMethod.Connect_to_Internet_Msg(context);
                        } else {

                            new upload_download(context);
                        }
                    }
                });
                builder1.show();
            }

            pd.dismiss();


        }
    }

    public void saveDoctorSample_CheckPob() {

            String Qty = "";
            String POB = "";
            String Rate = "";
            String NOC = "";
            data1.clear();
            data2.clear();
            data3.clear();
            data4.clear();
            data5.clear();
            ArrayList<String> getPrescribeRx_Dr = new ArrayList<String>();
            StringBuilder sb_rx = new StringBuilder();
            int j= 0;
            String seprator ="";
            cbohelp.deletedata(Custom_Variables_And_Method.DR_ID,"");

            for (int i = 0; i < list.size(); i++) {
                check = list.get(i).isSelected();
                check_Rx = list.get(i).isSelected_Rx();
                if (check_Rx) {

                    if (j==0){
                        seprator ="";
                    }else {
                        seprator =",";

                    }

                    sb_rx.append(seprator).append(list.get(i).getId()).append(",");
                    j++;

                }

                if (check) {
                    checkIfPOB_Entered=true;
                    data1.add(list.get(i).getId());
                    data2.add(list.get(i).getScore());
                    data5.add(list.get(i).getName());
                    data3.add(list.get(i).getPob());
                    data4.add(list.get(i).getRate());
                    Qty = list.get(i).getScore();
                    POB = list.get(i).getPob();
                    Rate = list.get(i).getRate();
                    NOC = list.get(i).getNOC();
                    if (Qty.equals("")) {
                        Qty = "0";
                    }
                    if (POB.equals("")) {
                        POB = "0";
                    }
                    if (Rate.equals("")) {
                        Rate = "0";
                    }
                    if (NOC.equals("")) {
                        NOC = "0";
                    }
                    ArrayList<String> doclist = cbohelp.getDoctorList();
                    ArrayList<String> docitems = cbohelp.getDoctorAllItems();
                    ArrayList<String> visual_id = cbohelp.getDoctorVisualId();
                    ArrayList<Integer> actlist = getdoclist();
                    if (actlist.contains(Integer.parseInt(Custom_Variables_And_Method.DR_ID))) {
                        if (visual_id.contains("1")) {
                            //cbohelp.deletedata(Custom_Variables_And_Method.DR_ID, list.get(i).getId());
                            cbohelp.insertdata(Custom_Variables_And_Method.DR_ID, list.get(i).getId(), list.get(i).getName(), Qty, POB, Rate, "1",NOC);
                        } else {

                            Log.e("no updation in sample", "no update");
                            //cbohelp.deletedata(Custom_Variables_And_Method.DR_ID, list.get(i).getId());
                            cbohelp.insertdata(Custom_Variables_And_Method.DR_ID, list.get(i).getId(), list.get(i).getName(), Qty, POB, Rate, "0",NOC);
                        }

                    } else {

                        cbohelp.insertdata(Custom_Variables_And_Method.DR_ID, list.get(i).getId(), list.get(i).getName(), Qty, POB, Rate, "0",NOC);
                    }


                } else {

                    data1.remove(check);
                    data2.remove(check);
                    data3.remove(check);
                    adapter.notifyDataSetChanged();
                }


            }

            for (int k = 0; k < data1.size(); k++) {
                itemid.append(data1.get(k)).append(",");
                itemname.append(data5.get(k)).append(",");
                itemqty.append(data2.get(k)).append(",");
                itempob.append(data3.get(k)).append(",");
            }

            if (!sb_rx.toString().equals("")  ) {
                getPrescribeRx_Dr = cbohelp.getDr_Rx_id(null);
                if (getPrescribeRx_Dr.contains(Custom_Variables_And_Method.DR_ID)) {
                    cbohelp.updateDr_Rx_Data(Custom_Variables_And_Method.DR_ID, "" + sb_rx);
                } else if (getPrescribeRx_Dr.size() >= 0) {
                    cbohelp.insert_drRx_Data(Custom_Variables_And_Method.DR_ID, "" + sb_rx);
                }
            }
            item_id = itemid.toString();
            item_name = itemname.toString();
            item_qty = itemqty.toString();
            item_pob = itempob.toString();
            cbohelp.close();
            if (checkIfPOB_Entered) {

                Bundle i = new Bundle();
                i.putString("val", "");
                i.putString("val2","");
                i.putString("val3", "");
                i.putDouble("resultpob",  mainval);

                threadMsg(i);
             }

        dialog.dismiss();
    }

    private void threadMsg(Bundle Msg) {
        Message msgObj = h1.obtainMessage(response_code);
       /* Bundle b = new Bundle();
        b.putString("Error",Msg);*/
        msgObj.setData(Msg);
        h1.sendMessage(msgObj);
    }


}
