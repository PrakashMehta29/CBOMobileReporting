package com.cbo.cbomobilereporting.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Array;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import utils.MyConnection;
import utils_new.Custom_Variables_And_Method;

public class CBO_DB_Helper extends SQLiteOpenHelper {
    private SQLiteDatabase sd;
    private static final int DATABASE_VERSION = 36
            ;
    private static final String DATABASE_NAME = "cbodb0017";
    private static final String LOGIN_TABLE = "cbo_login";
    private static final String LOGIN_DETAILS = "logindetail";
    private static final String DOCTOR_ITEM_TABLE = "phdcritem";
    private static final String DOCTOR_PRODUCTS_TABLE = "phitem";
    private static final String PH_DOCTOR_ITEM_TABLE = "phdoctoritem";
    private static final String Dr_PRESCRIBE = "drprescribe";
    private static final String PH_LAT_LONG = "phlatlong";
    private static final String PH_Farmer = "PHFarmer";
    private static final String PH_RCPA = "ph_rcpa";
    public static final String myTable = "lat_lon_details";
    public static final String myTable1MinuteLatLong = "lat_lon_oneminute";
    public static final String DR_RX_TABLE = "dr_rx_table";
    public static final String MenuControl = "menu_contorl";

    public static final String Expenses = "Expenses";
    public static final String Notification = "Notification";
    public static final String Approval_count = "Approval_count";
    public static final String NonListed_call = "NonListed_call";

    public static final String latLong10Minute = "lat_lon_TenMinute";
    public static final String Mail = "mail";
    public static final String Appraisal = "Appraisal";
    public static final String dob_doa = "dob_doa";
    public static final String Expenses_head = "Expenses_head";
    public static final String Tenivia_traker = "Tenivia_traker";
    public static final String Doctor_Call_Remark = "Doctor_Call_Remark";
    public static final String Lat_Long_Reg = "Lat_Long_Reg";
    public static final String PH_DAIRY = "PHDAIRY";
    public static final String PH_DAIRY_PERSON = "PHDAIRY_PERSON";
    public static final String PH_DAIRY_REASON = "PHDAIRY_REASON";
    public static final String PH_DAIRY_DCR = "PHDAIRY_DCR";
    public static final String PH_ITEM_STOCK_DCR = "PHITEM_STOCK_DCR";
    public static final String PH_DCR_ITEM = "PH_DCR_ITEM";
    public static final String VSTOCK = "VSTOCK";
    public static final String PH_STK_ITEM_RATE = "PH_STK_ITEM_RATE";


//
//    String CREATE_VSTOCK = "create view " + VSTOCK + "  as\n" +
//            "select PHITEM_STOCK_DCR.ITEM_ID,PHITEM_STOCK_DCR.STOCK_QTY,ifnull( T1.qty ,0) as QTY," +
//            "(PHITEM_STOCK_DCR.STOCK_QTY- ifnull( T1.qty ,0)) as BALANCE from PHITEM_STOCK_DCR\n" +
//            "left join \n" +
//            "(select T.item_id, sum(T.qty) as qty from \n" +
//            "(select item_id, sum(qty) as qty from phdcritem group by item_id \n" +
//            "union all\n" +
//            " select * from (WITH RECURSIVE split(item_id,id_rest,col, rest) AS (\n" +
//            "\tselect '', allitemid || ',' ,'', allitemqty || ','from phdcrstk\n" +
//            "    UNION ALL\n" +
//            "\tselect '', allgiftid || ',' ,'', allgiftqty || ','from phdcrstk\n" +
//            "    UNION ALL\n" +
//            "\tselect '', allitemid || ',' ,'', allitemqty || ','from phdcrchem\n" +
//            "    UNION ALL\n" +
//            "\tselect '', allgiftid || ',' ,'', allgiftqty || ','from phdcrchem\n" +
//            "    UNION ALL\n" +
//            "    \n" +
//            "    SELECT   substr(id_rest, 0, instr(id_rest, ',')),\n" +
//            "    substr(id_rest, instr(id_rest, ',')+1),\n" +
//            "    substr(rest, 0, instr(rest, ',')),\n" +
//            "    substr(rest, instr(rest, ',')+1)\n" +
//            "    FROM split WHERE rest <> '') \n" +
//            "    SELECT item_id,sum(col) as qty\n" +
//            "    FROM split " +
//            "    WHERE col <> ''  group by item_id)) T group by T.item_id) T1 on T1.item_id = PHITEM_STOCK_DCR.ITEM_ID ";


    String CREATE_VSTOCK = "create view " + VSTOCK + "  as\n"+
            "select PHITEM_STOCK_DCR.ITEM_ID,PHITEM_STOCK_DCR.STOCK_QTY,ifnull( T.QTY ,0) as QTY,\n" +
            "(PHITEM_STOCK_DCR.STOCK_QTY- ifnull( T.QTY ,0)) as BALANCE from PHITEM_STOCK_DCR\n" +
            " left join \n" +
            " (select ITEM_ID, SUM(QTY) as QTY from PH_DCR_ITEM GROUP BY ITEM_ID)T on T.ITEM_ID = PHITEM_STOCK_DCR.ITEM_ID";

    String DCR_ITEM_TABLE = "CREATE TABLE "+PH_DCR_ITEM +" ( id integer primary key,CategoryID text,Category text,ITEM_ID text,QTY integer,ItemType text  )";

    String DROP_VSTOCK = "drop view "+ VSTOCK;

    public CBO_DB_Helper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        getDetailsForOffline();
    }

    public   void getDetailsForOffline() {
        if (Custom_Variables_And_Method.PA_ID==0 || Custom_Variables_And_Method.COMPANY_CODE==null) {
            Custom_Variables_And_Method.PA_ID = Integer.parseInt(getPaid());
            Custom_Variables_And_Method.PA_NAME = getPaName();
            Custom_Variables_And_Method.HEAD_QTR = getHeadQtr();
            Custom_Variables_And_Method.DESIG = getDESIG();
            Custom_Variables_And_Method.pub_desig_id = getPUB_DESIG();
            Custom_Variables_And_Method.COMPANY_NAME = getCOMP_NAME();
            Custom_Variables_And_Method.WEB_URL = getWEB_URL();
            Custom_Variables_And_Method.location_required = getLocationDetail();
            Custom_Variables_And_Method.VISUAL_REQUIRED = getVisualDetail();
            Custom_Variables_And_Method.DCR_ID = getDCR_ID_FromLocal();
            Custom_Variables_And_Method.COMPANY_CODE = getCompanyCode();
            Custom_Variables_And_Method.pub_area = getPUB_AREA();
        }
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO Auto-generated method stub
        String CREATE_LOGIN = "CREATE TABLE " + LOGIN_TABLE + "( myid INTEGER PRIMARY KEY,code TEXT, username TEXT,password TEXT,pin TEXT)";
        String CREATE_LOGIN_DETAIL = "CREATE TABLE " + LOGIN_DETAILS + "( myid INTEGER PRIMARY KEY,company_code text,ols_ip TEXT, ols_db_name TEXT,ols_db_user TEXT,ols_db_password TEXT,ver text,domain text)";
        String CREATE_USER_DETAIL = "CREATE TABLE userdetail ( id integer primary key,pa_id integer,pa_name text,head_qtr text,desid text,pub_desig_id text,compny_name text,web_url text)";
        String CREATE_USER_DETAIL2 = "CREATE TABLE userdetail2 ( id integer primary key,location_required text,visual_required text)";
        String DCR_TABLE = "CREATE TABLE dcrdetail ( id integer primary key,dcr_id text,pub_area text)";

        String CREATE_DOCTOR_SAMPLE = "CREATE TABLE " + DOCTOR_ITEM_TABLE + "(id integer primary key,dr_id text,item_id text,item_name text,qty text,pob text,stk_rate text,visual text, updated text, noc  text DEFAULT '0')";
        String CREATE_DR_RX_TABLE = "CREATE TABLE " + DR_RX_TABLE + "(id integer primary key, dr_id text,item_id text)";
        String CREATE_DOCTOR_PRESCRIBE = "CREATE TABLE " + Dr_PRESCRIBE + "(id integer primary key,dr_id text,item_id text,item_name text,qty text,pob text,stk_rate text,visual text)";
        String PH_ITEM = "CREATE TABLE " + DOCTOR_PRODUCTS_TABLE + "( id integer primary key,item_id text,item_name text,stk_rate double,gift_type text,SHOW_ON_TOP text,SHOW_YN text)";
        String PH_DOCTOR_IETM = "CREATE TABLE " + PH_DOCTOR_ITEM_TABLE + "( id integer primary key,dr_id integer,item_id integer,item_name text)";

        String ALLMST = "CREATE TABLE phallmst ( id integer primary key,allmst_id integer,table_name text,field_name text,remark text )";
        String PARTY = "CREATE TABLE phparty ( id integer primary key,pa_id integer,pa_name text,desig_id integer," +
                "category text,hq_id integer,PA_LAT_LONG text,PA_LAT_LONG2 text,PA_LAT_LONG3 text,SHOWYN text)";
        String RELATION = "CREATE TABLE phrelation ( id integer primary key,pa_id integer,under_id integer,rank integer)";
        String ITEM_SPL = "CREATE TABLE phitemspl ( id integer primary key,item_id text,dr_spl_id text,srno integer)";
        String DCR_CHK = "CREATE TABLE finaldcrcheck ( id integer primary key,chemist text,stockist text,exp text)";
        String FTP_TABLE = "CREATE TABLE ftpdetail ( id integer primary key,ftpip text,username text,password text,port text,path text)";


        String STOCKIST_ADD_TABLE = "CREATE TABLE tempstockist ( id integer primary key,stk_id integer,stk_name text,visit_time text,stk_latLong text)";

        String CHEMIST_SAMPLE_TABLE = "CREATE TABLE chemistsample ( id integer primary key,chem_id text,item_id text,item_name text,qty text,sample text)";
        String STOCKIST_SAMPLE_TABLE = "CREATE TABLE stksample ( id integer primary key,stk_id text,item_id text,item_name text,qty text)";
        String Util_TABLE = "CREATE TABLE utils ( id integer primary key,pub_area text)";
        String VERSION_TABLE = "CREATE TABLE version ( id integer primary key,ver text)";
        String WORK_WITH_TABLE = "CREATE TABLE dr_workwith ( id integer primary key,workwith text,wwid text)";
        String FMCG_STATUS = "CREATE TABLE fmcg_status ( id integer primary key,fmcg text)";

        String DCR_ID = "CREATE TABLE mydcr ( id integer primary key,dcr_id text)";
        String RESIGNED = "CREATE TABLE resigned ( id integer primary key,user_name text,password text,doryn text,dosyn text)";
        String LOCATION_TABLE = "CREATE TABLE location ( id integer primary key,pa_id text,latitude text,longitude text,time text,date text)";
        String Apprisal_TABLE = "CREATE TABLE apprisal ( id integer primary key,graid_id text,grade_name text)";
        String Apprisal_VALUE_TABLE = "CREATE TABLE apprisal_val ( id integer primary key,dr_id text,dr_name text)";

        String Lat_Long_Data = "CREATE TABLE " + PH_LAT_LONG + "(id integer primary key autoincrement,latitude double,longitude double,time text)";
        String PH_Farmar = "CREATE TABLE " + PH_Farmer + "(id integer primary key autoincrement,date text,mcc_owner_name text,Mcc_owner_no text,farmer_attendence text,group_meeting_place text,product_detail text,IH_staff_attendence text,sale_to_farmer text,order_book_for_mcc text,remark text)";
        String myRepa = "CREATE TABLE " + PH_RCPA + "(id integer primary key autoincrement,dcr_id text,paid text,drid text,chemid text,month text,itemid text,qty text)";
        String CREATE_TABLE_LAT_LON = "CREATE TABLE " + myTable + "(id Integer PRIMARY KEY AUTOINCREMENT,lat text,lon text,time text )";
        String CREATE_TABLE_LAT_LON_ONEMINUTE = "CREATE TABLE " + myTable1MinuteLatLong + "(id Integer PRIMARY KEY AUTOINCREMENT,lat text,lon text,time text )";


        String CREATE_TABLE_MENU_CONTROL = "CREATE TABLE " + MenuControl + "(id Integer PRIMARY KEY AUTOINCREMENT,menu text,menu_code text,menu_name text,menu_url text,main_menu_srno text)";
        String CREATE_TABLE_Notification = "CREATE TABLE " + Notification + "(id Integer PRIMARY KEY AUTOINCREMENT,Title text,msg text,logo_url text,content_url text,read_status text,date text,time text)";
        String CREATE_TABLE_Approval_count = "CREATE TABLE " + Approval_count + "(id Integer PRIMARY KEY AUTOINCREMENT,approval_menu_code text,approval_count text)";
        String CHEMIST_TABLE = "CREATE TABLE phchemist ( id integer primary key,chem_id integer,chem_name text,area text," +
                "chem_code text,LAST_VISIT_DATE text,DR_LAT_LONG text,DR_LAT_LONG2 text,DR_LAT_LONG3 text,SHOWYN text)";
        String CREATE_TABLE_Expenses = "CREATE TABLE " + Expenses + "(id Integer PRIMARY KEY AUTOINCREMENT,exp_head_id text,exp_head text,amount text,remark text,FILE_NAME text,exp_ID text,time text)";
        String CREATE_TABLE_NonListed_call = "CREATE TABLE " + NonListed_call + "(id Integer PRIMARY KEY AUTOINCREMENT,sDocType text,sDrName text,sAdd1 text,sMobileNo text,sRemark text,iSplId text,iSpl text,iQflId text,iQfl text,iSrno text,loc text,time text,CLASS text,POTENCY_AMT text,AREA text)";
        String MASTER_DOCTOR = "CREATE TABLE phdoctor ( id integer primary key,dr_id integer,dr_name text,dr_code text,area text,spl_id integer,LAST_VISIT_DATE text" +
                ",CLASS text,PANE_TYPE text,POTENCY_AMT text,ITEM_NAME text,ITEM_POB text,ITEM_SALE text,DR_AREA text,DR_LAT_LONG text,FREQ text,NO_VISITED text" +
                ",DR_LAT_LONG2 text,DR_LAT_LONG3 text,COLORYN text,CRM_COUNT text,DRCAPM_GROUP text,SHOWYN text)";
        String DOCTOR_IN_LOCAL = "CREATE TABLE phdcrdr_more ( id integer primary key,dr_id text,dr_name text,ww1 text,ww2 text,ww3 text,loc text,time text)";

        String CREATE_TABLE_LAT_LON_TEN = "CREATE TABLE " + latLong10Minute + "(id Integer PRIMARY KEY AUTOINCREMENT,lat text,lon text,time text,km text,updated text ,LOC_EXTRA text)";
        String CHEMIST_ADD_TABLE = "CREATE TABLE chemisttemp ( id integer primary key,chem_id integer,chem_name text,visit_time text,chem_latLong text,chem_address text,updated text,chem_km text,srno text,LOC_EXTRA text)";
        String CREATE_DOCTOR = "CREATE TABLE phdcrdr ( myid2 INTEGER PRIMARY KEY,dr_id TEXT,dr_name TEXT,work_with1 TEXT,work_with2 TEXT,work_with3 TEXT,location TEXT,time TEXT,flag TEXT,LOC_EXTRA text)";
        String tempdr = "CREATE TABLE tempdr ( id integer primary key,dr_id text,dr_name text,visit_time text,batteryLevel text,dr_latLong text,dr_address text,dr_remark text,updated text,dr_km text,srno text,work_with_name text,DR_AREA text,file text,call_type text,LOC_EXTRA text,Ref_latlong text)";
        String RC_DOCTOR = "CREATE TABLE phdcrdr_rc ( id integer primary key,dcr_id text,dr_id text,address text,time text,latLong text,updated text,rc_km text,srno text,batteryLevel text,remark text,file text,LOC_EXTRA text,Ref_latlong text)";
        String CHEMIST_SUBMIT_TABLE = "CREATE TABLE phdcrchem ( id integer primary key,dcr_id text,chem_id text,pob_amt text," +
                "allitemid text,allitemqty text,address text,allgiftid text,allgiftqty text,time text,battery_level text,sample text," +
                "remark text,file text,LOC_EXTRA text,Ref_latlong text,rate text)";
        String STOCKIST_SUBMIT_TABLE = "CREATE TABLE phdcrstk ( id integer primary key,dcr_id integer,stk_id text,pob_amt text," +
                "allitemid text,allitemqty text,address text,time text,battery_level text,latLong text,updated text,stk_km text," +
                "srno text,sample text,remark text,file text,LOC_EXTRA text,allgiftid text,allgiftqty text,Ref_latlong text,rate text)";

        String MAIL_TABLE = "CREATE TABLE "+Mail +" ( id integer primary key,mail_id integer,who_id text,who_name text,date text,time text," +
                "is_read text,category text,type text,subject text,remark text,file_name text,file_path text)";

        String Dcr_Appraisal = "CREATE TABLE " +Appraisal+" ( id integer primary key,PA_ID text,PA_NAME text,DR_CALL text,DR_AVG text,CHEM_CALL text,CHEM_AVG text,FLAG text,sAPPRAISAL_ID_STR text,sAPPRAISAL_NAME_STR text,sGRADE_STR text,sGRADE_NAME_STR text,sOBSERVATION text,sACTION_TAKEN text)";

        String CREATE_TABLE_dob_doa = "CREATE TABLE " + dob_doa + "(id Integer PRIMARY KEY AUTOINCREMENT,PA_NAME text,DOB text,DOA text,MOBILE text,TYPE text)";
        String CREATE_TABLE_Expenses_head = "CREATE TABLE " + Expenses_head + "(id Integer PRIMARY KEY AUTOINCREMENT,FIELD_NAME text,FIELD_ID text,MANDATORY text,DA_ACTION text)";
        String CREATE_TABLE_Tenivia_traker = "CREATE TABLE " + Tenivia_traker + "(id Integer PRIMARY KEY AUTOINCREMENT,DR_ID text,DR_NAME text,QTY text,AMOUNT text,QTY_CAPTION text,ITEM_ID text,AMOUN_CAPTION text,TIME text,REMARK text)";
        String CREATE_TABLE_Doctor_Call_Remark = "CREATE TABLE " + Doctor_Call_Remark + "(id Integer PRIMARY KEY AUTOINCREMENT,FIELD_ID text,FIELD_NAME text)";

        String CREATE_TABLE_Lat_Long_Reg = "CREATE TABLE " + Lat_Long_Reg + "(id Integer PRIMARY KEY ,DCS_ID text,LAT_LONG text,DCS_TYPE text,DCS_ADD text,DCS_INDES text,UPDATED text)";


        String DAIRY_TABLE = "CREATE TABLE " + PH_DAIRY+ " ( id integer primary key,DAIRY_ID integer,DAIRY_NAME text,DOC_TYPE text,LAST_VISIT_DATE text,DR_LAT_LONG text,DR_LAT_LONG2 text,DR_LAT_LONG3 text)";
        String DAIRY_PERSON_TABLE = "CREATE TABLE " + PH_DAIRY_PERSON+ " ( id integer primary key,DAIRY_ID integer,PERSON_ID integer,PERSON_NAME text)";
        String DAIRY_REASON_TABLE = "CREATE TABLE " + PH_DAIRY_REASON+ " ( id integer primary key,PA_ID integer,PA_NAME text)";
        String DAIRY_DCR_TABLE = "CREATE TABLE "+PH_DAIRY_DCR +" ( id integer primary key,DAIRY_ID text,DOC_TYPE text,DAIRY_NAME text,visit_time text,batteryLevel text,dr_latLong text,dr_address text,dr_remark text,updated text,dr_km text,srno text,person_name text,person_id text,pob_amt text,allitemid text,allitemqty text,sample text,allgiftid text,allgiftqty text,file text,LOC_EXTRA text,IS_INTRESTED text,Ref_latlong text)";
        String ITEM_STOCK_DCR_TABLE = "CREATE TABLE "+PH_ITEM_STOCK_DCR +" ( id integer primary key,ITEM_ID text,STOCK_QTY integer )";

        String PH_STK_ITEM_RATE_TABLE = "CREATE TABLE "+PH_STK_ITEM_RATE +" ( id integer primary key,STK_ID text,ITEM_ID text,RATE text )";


        db.execSQL(DAIRY_TABLE);
        db.execSQL(DAIRY_PERSON_TABLE);
        db.execSQL(DAIRY_REASON_TABLE);
        db.execSQL(DAIRY_DCR_TABLE);
        db.execSQL(ITEM_STOCK_DCR_TABLE);
        db.execSQL(DCR_ITEM_TABLE);


        db.execSQL(CREATE_TABLE_LAT_LON);
        db.execSQL(CREATE_LOGIN);
        db.execSQL(CREATE_DOCTOR);
        db.execSQL(CREATE_DOCTOR_SAMPLE);
        db.execSQL(CREATE_DR_RX_TABLE);
        db.execSQL(DOCTOR_IN_LOCAL);
        db.execSQL(PH_ITEM);
        db.execSQL(DCR_ID);
        db.execSQL(RESIGNED);
        db.execSQL(tempdr);
        db.execSQL(CREATE_DOCTOR_PRESCRIBE);
        db.execSQL(FMCG_STATUS);
        db.execSQL(Util_TABLE);
        db.execSQL(Apprisal_TABLE);
        db.execSQL(Apprisal_VALUE_TABLE);
        db.execSQL(VERSION_TABLE);
        db.execSQL(PH_DOCTOR_IETM);
        db.execSQL(MASTER_DOCTOR);
        db.execSQL(ALLMST);
        db.execSQL(LOCATION_TABLE);
        db.execSQL(WORK_WITH_TABLE);
        db.execSQL(RC_DOCTOR);
        db.execSQL(PARTY);
        db.execSQL(RELATION);
        db.execSQL(ITEM_SPL);
        db.execSQL(DCR_CHK);
        db.execSQL(FTP_TABLE);
        db.execSQL(CHEMIST_TABLE);
        db.execSQL(CHEMIST_ADD_TABLE);
        //db.execSQL(STOCKIST_TABLE);
        db.execSQL(CREATE_LOGIN_DETAIL);
        db.execSQL(CREATE_USER_DETAIL);
        db.execSQL(CREATE_USER_DETAIL2);
        db.execSQL(DCR_TABLE);
        db.execSQL(CHEMIST_SUBMIT_TABLE);
        db.execSQL(STOCKIST_SUBMIT_TABLE);
        db.execSQL(STOCKIST_ADD_TABLE);
        db.execSQL(CHEMIST_SAMPLE_TABLE);
        db.execSQL(STOCKIST_SAMPLE_TABLE);
        db.execSQL(Lat_Long_Data);
        db.execSQL(PH_Farmar);
        db.execSQL(myRepa);
        db.execSQL(CREATE_TABLE_LAT_LON_TEN);
        db.execSQL(CREATE_TABLE_LAT_LON_ONEMINUTE);

        db.execSQL(CREATE_TABLE_MENU_CONTROL);
        db.execSQL(CREATE_TABLE_Notification);
        db.execSQL(CREATE_TABLE_Approval_count);
        db.execSQL(CREATE_TABLE_Expenses);
        db.execSQL(CREATE_TABLE_NonListed_call);
        db.execSQL(MAIL_TABLE);
        db.execSQL(Dcr_Appraisal);
        db.execSQL(CREATE_TABLE_dob_doa);
        db.execSQL(CREATE_TABLE_Expenses_head);
        db.execSQL(CREATE_TABLE_Tenivia_traker);
        db.execSQL(CREATE_TABLE_Doctor_Call_Remark);
        db.execSQL(CREATE_TABLE_Lat_Long_Reg);

        db.execSQL(PH_STK_ITEM_RATE_TABLE);

        db.execSQL(CREATE_VSTOCK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        switch(oldVersion) {
            case 1:

                db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + Dr_PRESCRIBE);
                db.execSQL("DROP TABLE IF EXISTS phdcrdr");
                db.execSQL("DROP TABLE IF EXISTS " + DOCTOR_ITEM_TABLE);
                //db.execSQL("drop table if exist "+Dr_PRESCRIBE);
                db.execSQL("DROP TABLE IF EXISTS " + DOCTOR_PRODUCTS_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + PH_DOCTOR_ITEM_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + DR_RX_TABLE);
                db.execSQL("DROP TABLE IF EXISTS   phdoctor");
                db.execSQL("DROP TABLE IF EXISTS   fmcg_status");
                db.execSQL("DROP TABLE IF EXISTS   version");
                db.execSQL("DROP TABLE IF EXISTS   dcrworkwith");
                db.execSQL("DROP TABLE IF EXISTS   apprisal_val");
                db.execSQL("DROP TABLE IF EXISTS   phdcrdr_more");
                db.execSQL("DROP TABLE IF EXISTS   phallmst");
                db.execSQL("DROP TABLE IF EXISTS   phparty");
                db.execSQL("DROP TABLE IF EXISTS  mydcr");
                db.execSQL("DROP TABLE IF EXISTS  tempdr");
                db.execSQL("DROP TABLE IF EXISTS  drprescribe");
                db.execSQL("DROP TABLE IF EXISTS   phrelation");
                db.execSQL("DROP TABLE IF EXISTS   phitemspl");
                db.execSQL("DROP TABLE IF EXISTS  finaldcrcheck");
                db.execSQL("DROP TABLE IF EXISTS  ftpdetail");
                db.execSQL("DROP TABLE IF EXISTS  resigned");
                db.execSQL("DROP TABLE IF EXISTS  phchemist");
                db.execSQL("DROP TABLE IF EXISTS  apprisal");
                db.execSQL("DROP TABLE IF EXISTS   dr_workwith");
                db.execSQL("DROP TABLE IF EXISTS   logindetail");
                db.execSQL("DROP TABLE IF EXISTS  userdetail");
                db.execSQL("DROP TABLE IF EXISTS  userdetail2");
                db.execSQL("DROP TABLE IF EXISTS  dcrdetail");
                db.execSQL("DROP TABLE IF EXISTS  location");
                db.execSQL("DROP TABLE IF EXISTS  phdcrchem");
                db.execSQL("DROP TABLE IF EXISTS  phdcrstk");
                db.execSQL("DROP TABLE IF EXISTS  chemisttemp");
                db.execSQL("DROP TABLE IF EXISTS   tempstockist");
                db.execSQL("DROP TABLE IF EXISTS   chemistsample");
                db.execSQL("DROP TABLE IF EXISTS   stksample");
                db.execSQL("DROP TABLE IF EXISTS  utils");
                db.execSQL("DROP TABLE IF EXISTS   phdcrdr_rc");
                db.execSQL("DROP TABLE IF EXISTS " + PH_LAT_LONG);
                db.execSQL("DROP TABLE IF EXISTS  PHFarmer");
                db.execSQL("DROP TABLE IF EXISTS  " + PH_RCPA);
                db.execSQL("DROP TABLE IF EXISTS  " + myTable);
                db.execSQL("DROP TABLE IF EXISTS " + latLong10Minute);
                db.execSQL("DROP TABLE IF EXISTS " + myTable1MinuteLatLong);
                db.execSQL("DROP TABLE IF EXISTS " + MenuControl);

                db.execSQL("DROP TABLE IF EXISTS " + Notification);

                this.onCreate(db);
                break;
            case 2:
                db.execSQL("ALTER TABLE "+"phdcrdr_rc"+" ADD COLUMN batteryLevel text DEFAULT '0'");
            case 3:
                String CREATE_TABLE_Approval_count = "CREATE TABLE " + Approval_count + "(id Integer PRIMARY KEY AUTOINCREMENT,approval_menu_code text,approval_count text)";
                db.execSQL(CREATE_TABLE_Approval_count);
            case 4:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN LAST_VISIT_DATE text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phchemist"+" ADD COLUMN LAST_VISIT_DATE text DEFAULT ''");
                String CREATE_TABLE_Expenses = "CREATE TABLE " + Expenses + "(id Integer PRIMARY KEY AUTOINCREMENT,exp_head_id text,exp_head text,amount text,remark text,FILE_NAME text,exp_ID text,time text)";
                db.execSQL(CREATE_TABLE_Expenses);
            case 5:
                String CREATE_TABLE_NonListed_call = "CREATE TABLE " + NonListed_call + "(id Integer PRIMARY KEY AUTOINCREMENT,sDocType text,sDrName text,sAdd1 text,sMobileNo text,sRemark text,iSplId text,iSpl text,iQflId text,iQfl text,iSrno text,loc text,time text)";
                db.execSQL(CREATE_TABLE_NonListed_call);
            case 6:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN CLASS text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN PANE_TYPE text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN POTENCY_AMT text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN ITEM_NAME text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN ITEM_POB text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN ITEM_SALE text DEFAULT ''");
            case 7:
                db.execSQL("ALTER TABLE "+"tempdr"+" ADD COLUMN work_with_name text DEFAULT ''");
            case 8:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN DR_AREA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"tempdr"+" ADD COLUMN DR_AREA text DEFAULT ''");
            case 9:
                db.execSQL("ALTER TABLE "+"phdcrdr_rc"+" ADD COLUMN remark text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrdr_rc"+" ADD COLUMN file text DEFAULT ''");

                db.execSQL("ALTER TABLE "+"tempdr"+" ADD COLUMN file text DEFAULT ''");

                db.execSQL("ALTER TABLE "+"phdcrchem"+" ADD COLUMN remark text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrchem"+" ADD COLUMN file text DEFAULT ''");

                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN remark text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN file text DEFAULT ''");
            case 10:
                db.execSQL("ALTER TABLE "+"tempdr"+" ADD COLUMN call_type text DEFAULT ''");
                db.execSQL("ALTER TABLE "+NonListed_call+" ADD COLUMN CLASS text DEFAULT ''");
                db.execSQL("ALTER TABLE "+NonListed_call+" ADD COLUMN POTENCY_AMT text DEFAULT ''");
            case 11:
                db.execSQL("ALTER TABLE "+NonListed_call+" ADD COLUMN AREA text DEFAULT ''");
            case 12:
                String MAIL_TABLE = "CREATE TABLE "+Mail +" ( id integer primary key,mail_id integer,who_id text,who_name text,date text,time text," +
                        "is_read text,category text,type text,subject text,remark text,file_name text,file_path text)";
                db.execSQL(MAIL_TABLE);
            case 13:
                String Dcr_Appraisal = "CREATE TABLE " +Appraisal+" ( id integer primary key,PA_ID text,PA_NAME text,DR_CALL text,DR_AVG text,CHEM_CALL text,CHEM_AVG text,FLAG text,sAPPRAISAL_ID_STR text,sAPPRAISAL_NAME_STR text,sGRADE_STR text,sGRADE_NAME_STR text,sOBSERVATION text,sACTION_TAKEN text)";
                db.execSQL(Dcr_Appraisal);
                db.execSQL("DROP TABLE IF EXISTS dcrworkwith");
            case 14:
                String CREATE_TABLE_dob_doa = "CREATE TABLE " + dob_doa + "(id Integer PRIMARY KEY AUTOINCREMENT,PA_NAME text,DOB text,DOA text,MOBILE text,TYPE text)";
                db.execSQL(CREATE_TABLE_dob_doa);
            case 15:
                db.execSQL("ALTER TABLE "+latLong10Minute+" ADD COLUMN updated text DEFAULT '0'");
                db.execSQL("ALTER TABLE "+DOCTOR_ITEM_TABLE+" ADD COLUMN updated text DEFAULT '0'");
            case 16:
                String CREATE_TABLE_Expenses_head = "CREATE TABLE " + Expenses_head + "(id Integer PRIMARY KEY AUTOINCREMENT,FIELD_NAME text,FIELD_ID text,MANDATORY text)";
                db.execSQL(CREATE_TABLE_Expenses_head);
            case 17:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN DR_LAT_LONG text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phchemist"+" ADD COLUMN DR_LAT_LONG text DEFAULT ''");
            case 18:
                db.execSQL("ALTER TABLE "+"phdcrdr_rc"+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"tempdr"+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrchem"+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"chemisttemp"+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrdr"+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
                db.execSQL("ALTER TABLE "+latLong10Minute+" ADD COLUMN LOC_EXTRA text DEFAULT ''");
            case 19:
                String CREATE_TABLE_Tenivia_traker = "CREATE TABLE " + Tenivia_traker + "(id Integer PRIMARY KEY AUTOINCREMENT,DR_ID text,DR_NAME text,QTY text,AMOUNT text,QTY_CAPTION text,ITEM_ID text,AMOUN_CAPTION text,TIME text,REMARK text)";
                db.execSQL(CREATE_TABLE_Tenivia_traker);
            case 20:
                String CREATE_TABLE_Doctor_Call_Remark = "CREATE TABLE " + Doctor_Call_Remark + "(id Integer PRIMARY KEY AUTOINCREMENT,FIELD_ID text,FIELD_NAME text)";
                db.execSQL(CREATE_TABLE_Doctor_Call_Remark);
            case 21:
                db.execSQL("ALTER TABLE "+DOCTOR_ITEM_TABLE+" ADD COLUMN noc text DEFAULT '0'");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN FREQ text DEFAULT '0'");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN NO_VISITED text DEFAULT '0'");
            case 22:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN DR_LAT_LONG2 text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phchemist"+" ADD COLUMN DR_LAT_LONG2 text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN DR_LAT_LONG3 text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phchemist"+" ADD COLUMN DR_LAT_LONG3 text DEFAULT ''");
            case 23:
                String CREATE_TABLE_Lat_Long_Reg = "CREATE TABLE " + Lat_Long_Reg + "(id Integer PRIMARY KEY ,DCS_ID text,LAT_LONG text,DCS_TYPE text,DCS_ADD text,DCS_INDES text,UPDATED text)";
                db.execSQL(CREATE_TABLE_Lat_Long_Reg);
            case 24:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN COLORYN text DEFAULT '0'");
                db.execSQL("ALTER TABLE "+"phparty"+" ADD COLUMN PA_LAT_LONG text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phparty"+" ADD COLUMN PA_LAT_LONG2 text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phparty"+" ADD COLUMN PA_LAT_LONG3 text DEFAULT ''");
            case 25:
                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN allgiftqty text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN allgiftid text DEFAULT ''");
            case 26:
                db.execSQL("ALTER TABLE "+"phitem"+" ADD COLUMN SHOW_ON_TOP text DEFAULT 'N'");
            case 27:
                String DAIRY_TABLE = "CREATE TABLE " + PH_DAIRY+ " ( id integer primary key,DAIRY_ID integer,DAIRY_NAME text,DOC_TYPE text,LAST_VISIT_DATE text,DR_LAT_LONG text,DR_LAT_LONG2 text,DR_LAT_LONG3 text)";
                String DAIRY_PERSON_TABLE = "CREATE TABLE " + PH_DAIRY_PERSON+ " ( id integer primary key,DAIRY_ID integer,PERSON_ID integer,PERSON_NAME text)";
                String DAIRY_REASON_TABLE = "CREATE TABLE " + PH_DAIRY_REASON+ " ( id integer primary key,PA_ID integer,PA_NAME text)";
                String DAIRY_DCR_TABLE = "CREATE TABLE "+PH_DAIRY_DCR +" ( id integer primary key,DAIRY_ID text,DOC_TYPE text,DAIRY_NAME text,visit_time text,batteryLevel text,dr_latLong text,dr_address text,dr_remark text,updated text,dr_km text,srno text,person_name text,person_id text,pob_amt text,allitemid text,allitemqty text,sample text,allgiftid text,allgiftqty text,file text,LOC_EXTRA text)";

                db.execSQL(DAIRY_TABLE);
                db.execSQL(DAIRY_PERSON_TABLE);
                db.execSQL(DAIRY_REASON_TABLE);
                db.execSQL(DAIRY_DCR_TABLE);
            case 28:
                db.execSQL("ALTER TABLE "+"phitem"+" ADD COLUMN SHOW_YN text DEFAULT 'Y'");
                db.execSQL("ALTER TABLE "+PH_DAIRY_DCR+" ADD COLUMN IS_INTRESTED text DEFAULT ''");
            case 29:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN CRM_COUNT text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN DRCAPM_GROUP text DEFAULT ''");
            case 30:
                db.execSQL("ALTER TABLE "+"phdoctor"+" ADD COLUMN SHOWYN text DEFAULT '1'");
                db.execSQL("ALTER TABLE "+"phparty"+" ADD COLUMN SHOWYN text DEFAULT '1'");
                db.execSQL("ALTER TABLE "+"phchemist"+" ADD COLUMN SHOWYN text DEFAULT '1'");
            case 31:
                db.execSQL("ALTER TABLE "+"tempdr"+" ADD COLUMN Ref_latlong text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrchem"+" ADD COLUMN Ref_latlong text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN Ref_latlong text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrdr_rc"+" ADD COLUMN Ref_latlong text DEFAULT ''");
                db.execSQL("ALTER TABLE "+PH_DAIRY_DCR+" ADD COLUMN Ref_latlong text DEFAULT ''");

                String ITEM_STOCK_DCR_TABLE = "CREATE TABLE "+PH_ITEM_STOCK_DCR +" ( id integer primary key,ITEM_ID text,STOCK_QTY integer )";
                db.execSQL(ITEM_STOCK_DCR_TABLE);
            case 32:
                db.execSQL(DCR_ITEM_TABLE);
                db.execSQL(CREATE_VSTOCK);
            case 33:
                db.execSQL("ALTER TABLE "+Expenses_head+" ADD COLUMN DA_ACTION text DEFAULT '0'");
            case 34:
                String PH_STK_ITEM_RATE_TABLE = "CREATE TABLE "+PH_STK_ITEM_RATE +" ( id integer primary key,STK_ID text,ITEM_ID text,RATE text )";
                db.execSQL(PH_STK_ITEM_RATE_TABLE);
            case 35:
                db.execSQL("ALTER TABLE "+"phdcrchem"+" ADD COLUMN rate text DEFAULT ''");
                db.execSQL("ALTER TABLE "+"phdcrstk"+" ADD COLUMN rate text DEFAULT ''");


        }
    }


    public void insertdata(String mycode, String uname, String pw, String pin) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("code", mycode);
        cv.put("username", uname);
        cv.put("password", pw);
        cv.put("pin", pin);
        sd.insert(LOGIN_TABLE, null, cv);
    }

    public String getCompanyCode() {
       sd = this.getWritableDatabase();
        String code = "";
        Cursor c = sd.rawQuery("select code from cbo_login", null);
        if (c.moveToFirst()) {
            do {
                code = c.getString(c.getColumnIndex("code"));
            } while (c.moveToNext());
        }
        c.close();
        return code;
    }

    public String getUserName() {
        sd = this.getWritableDatabase();
        String username = "";
        Cursor c = sd.rawQuery("select username from cbo_login", null);
        if (c.moveToFirst()) {
            do {
                username = c.getString(c.getColumnIndex("username"));
            } while (c.moveToNext());
        }
        c.close();
        return username;
    }

    public String getPin() {
        sd = this.getWritableDatabase();
        String mypin = "";

        Cursor c = sd.query(LOGIN_TABLE, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                mypin = c.getString(c.getColumnIndex("pin"));
            } while (c.moveToNext());
        }
        c.close();
        return mypin;
    }

    public void deleteLogin() {
        sd = this.getWritableDatabase();
        sd.delete(LOGIN_TABLE, null, null);
    }

    public Cursor getLoginDetail() {
        sd = this.getWritableDatabase();
        return sd.query(LOGIN_TABLE, null, null, null, null, null, null);
    }

    //===========================================================Login Finish====================================================================================
    public long insertDoctorInLocal(String dr_id, String dr_name, String ww1, String ww2, String ww3, String loc, String time, String LOC_EXTRA) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_id", dr_id);
        cv.put("dr_name", dr_name);
        cv.put("work_with1", ww1);
        cv.put("work_with2", ww2);
        cv.put("work_with3", ww3);
        cv.put("location", loc);
        cv.put("time", time);
        cv.put("LOC_EXTRA", LOC_EXTRA);
        long l= sd.insert("phdcrdr", null, cv);

        return l;
    }


    public void updateDr_Location(String loc, String drId) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("location", loc);
        long result = sd.update("phdcrdr", cv, "dr_id =" + drId, null);

    }
    public void updateDr_LocExtra(String LOC_EXTRA, String drId) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("LOC_EXTRA", LOC_EXTRA);
        long result = sd.update("phdcrdr", cv, "dr_id =" + drId, null);

    }

    public String getDoctorLocationFromSqlite(String drid) {
        sd = this.getWritableDatabase();
        String drname = "";
        Cursor c = sd.rawQuery("select location from phdcrdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    drname = c.getString(c.getColumnIndex("location"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return drname;
    }


    public String getDoctorLocExtraFromSqlite(String drid) {
        sd = this.getWritableDatabase();
        String drname = "";
        Cursor c = sd.rawQuery("select LOC_EXTRA from phdcrdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    drname = c.getString(c.getColumnIndex("LOC_EXTRA"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return drname;
    }

    public String getDoctorww3FromSqlite(String drid) {
        sd = this.getWritableDatabase();
        String drname = "";
        Cursor c = sd.rawQuery("select work_with3 from phdcrdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    drname = c.getString(c.getColumnIndex("work_with3"));
                    if (drname.equals("")) {

                        return "0";
                    }
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return drname;
    }

    public String getDoctorww2FromSqlite(String drid) {
        sd = this.getWritableDatabase();
        String drname = "";
        Cursor c = sd.rawQuery("select work_with2 from phdcrdr where dr_id='" + drid + "'", null);
       try {
           if (c.moveToFirst()) {
               do {
                   drname = c.getString(c.getColumnIndex("work_with2"));
                   if (drname.equals("")) {

                       return "0";
                   }
               } while (c.moveToNext());
           }
        } finally {
            c.close();
        }
        return drname;
    }

    public String getDoctorww1FromSqlite(String drid) {
        sd = this.getWritableDatabase();
        String drname = "";
        Cursor c = sd.rawQuery("select work_with1 from phdcrdr where dr_id='" + drid + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     drname = c.getString(c.getColumnIndex("work_with1"));
                     if (drname.equals("")) {

                         return "0";
                     }
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return drname;
    }

    public String getDoctorTimeFromSqlite(String drid) {
        sd = this.getWritableDatabase();
        String drname = "";
        Cursor c = sd.rawQuery("select time from phdcrdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    drname = c.getString(c.getColumnIndex("time"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return drname;
    }


    public ArrayList<String> getDoctor() {

        sd = this.getWritableDatabase();
        ArrayList<String> doclist = new ArrayList<String>();
        //String qurey="select * from phdcrdr";
        Cursor c = sd.rawQuery("select dr_id from phdcrdr", null);
        try {
            if (c.moveToFirst()) {
                do {
                    doclist.add(c.getString(c.getColumnIndex("dr_id")));
                }
                while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return doclist;
    }


    public Cursor getDoctorName() {
        SQLiteDatabase sd = this.getWritableDatabase();
        //return sd.query("phdcrdr", null, null, null, null, null, null);
        return sd.rawQuery("select * from tempdr where call_type = '0' or call_type='2'", null);
        //return sd.query("tempdr", null, null, null, null, null, null);
    }

    public void deleteDoctor() {
        sd = this.getWritableDatabase();
        sd.delete("phdcrdr", null, null);
    }
    ///////////////////////DR_RX_TABLE ////////////////////////

//		String CREATE_DR_RX_TABLE ="CREATE TABLE "+DR_RX_TABLE +"(id integer primary key, dr_id text,item_id text)";

    public void insert_drRx_Data(String drid, String item) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("item_id", item);


        long result = sd.insert(DR_RX_TABLE, null, cv);
    }

    public ArrayList<String> getDr_Rx_id(String updated) {

        ArrayList<String> dr_id = new ArrayList<String>();
        sd = this.getWritableDatabase();
        //String query = "select dr_id from " + DR_RX_TABLE;
        Cursor c = null;
        if (updated==null) {
            c = sd.rawQuery("select dr_id from " + DR_RX_TABLE, null);
        }else{
            c = sd.rawQuery("select tempdr.dr_id from tempdr inner join  "+DR_RX_TABLE +" on dr_rx_table.dr_id=tempdr.dr_id  where updated ='"+updated+"'", null);
        }
       // Cursor c = sd.rawQuery(query, null);

        try {
            if (c.moveToFirst()) {
                do {
                    dr_id.add(c.getString(c.getColumnIndex("dr_id")));
                } while (c.moveToNext());

            }
        } finally {
            c.close();
        }
        return dr_id;
    }


    public String getDr_Rx_itemId(String dr_id) {

        String item_id = "";
        sd = this.getReadableDatabase();
        String query = "select item_id from " + DR_RX_TABLE + " where dr_id =" + dr_id;
        Cursor c = sd.rawQuery(query, null);

        try {
            if (c.moveToFirst()) {
                do {
                    item_id = (c.getString(c.getColumnIndex("item_id")));
                } while (c.moveToNext());

            }
        } finally {
            c.close();
        }
        return item_id;
    }


    public void updateDr_Rx_Data(String dr_id, String item_id) {

        ContentValues cv = new ContentValues();
        cv.put("dr_id", dr_id);
        cv.put("item_id", item_id);
       sd = this.getWritableDatabase();

        long result = sd.update(DR_RX_TABLE, cv, "dr_id =" + dr_id, null);

    }

    public void delete_Rx_Table() {

        sd = this.getWritableDatabase();
        long result = sd.delete(DR_RX_TABLE, null, null);

    }

    //	=========================



    public void insertdata(String drid, String item, String item_name, String qty, String pob, String stk_rate, String visual, String noc) {
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("item_id", item);
        cv.put("item_name", item_name);
        cv.put("qty", qty);
        cv.put("pob", pob);
        cv.put("stk_rate", stk_rate);
        cv.put("visual", visual);
        cv.put("noc", noc);
        cv.put("updated", "0");
        sd = this.getWritableDatabase();
        sd.insert(DOCTOR_ITEM_TABLE, null, cv);

        delete_DCR_Item(drid,item,stk_rate.equals("")?"SAMPLE" : "GIFT","DR");
        insert_DCR_Item(drid,item,qty,stk_rate.equals("")?"SAMPLE" : "GIFT","DR");

    }

    public void updateDr_item( String id) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("updated", "1");
        int result = sd.update(DOCTOR_ITEM_TABLE, cv, "dr_id =" + id, null);
    }

    public void deletedata(String drid,String Rate) {

        sd = this.getWritableDatabase();

        if(Rate.equals("")) {
            //query = "DELETE FROM  " + DOCTOR_ITEM_TABLE +" WHERE dr_id='" + drid + "'" ;
            sd.delete(DOCTOR_ITEM_TABLE, "dr_id='" + drid + "' and stk_rate!='x'", null);
            delete_DCR_Item(drid,null,"SAMPLE","DR");
        }else{
            String query = "DELETE FROM  " + DOCTOR_ITEM_TABLE +" WHERE dr_id='" + drid + "' and stk_rate='x'" ;
            sd.rawQuery(query, null);
            delete_DCR_Item(drid,null,"GIFT","DR");
        }

    }

/////////////////////////// DOCOTR_pRESCRIBE_TABLE;//////////////

    public void insertdataPrescribe(String drid, String item, String item_name, String qty, String pob, String stk_rate, String visual) {
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("item_id", item);
        cv.put("item_name", item_name);
        cv.put("qty", qty);
        cv.put("pob", pob);
        cv.put("stk_rate", stk_rate);
        cv.put("visual", visual);
        sd = this.getWritableDatabase();
        sd.insert(Dr_PRESCRIBE, null, cv);
    }


    public void insertVisuals(String drid, String item, String item_name, String qty, String pob, String stk_rate, String visual) {
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("item_id", item);
        cv.put("item_name", item_name);
        cv.put("qty", qty);
        cv.put("pob", pob);
        cv.put("stk_rate", stk_rate);
        cv.put("visual", visual);
        sd = this.getWritableDatabase();
        sd.insert(DOCTOR_ITEM_TABLE, null, cv);
    }

    public void updateVisuals(String drid, String item, String item_name, String qty, String pob, String stk_rate, String visual) {
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("item_id", item);
        cv.put("item_name", item_name);
        cv.put("qty", qty);
        cv.put("pob", pob);
        cv.put("stk_rate", stk_rate);
        cv.put("visual", visual);
        sd = this.getWritableDatabase();
        sd.update(DOCTOR_ITEM_TABLE, cv, "item_id='" + item + "'", null);
    }

    public ArrayList<String> getDoctorList() {
       sd = this.getWritableDatabase();
        ArrayList<String> doclist = new ArrayList<String>();
        Cursor c = sd.query(DOCTOR_ITEM_TABLE, null, null, null, null, null, null);
        try {
            if (c.moveToFirst()) {
                do {
                    doclist.add(c.getString(c.getColumnIndex("dr_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        return doclist;
    }

    public ArrayList<String> getDoctorAllItems() {
        sd = this.getWritableDatabase();
        ArrayList<String> doclist = new ArrayList<String>();
        Cursor c = sd.query(DOCTOR_ITEM_TABLE, null, null, null, null, null, null);
        try {
            if (c.moveToFirst()) {
                do {
                    doclist.add(c.getString(c.getColumnIndex("item_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        return doclist;
    }

    public ArrayList<String> getDoctorVisualId() {
        sd = this.getWritableDatabase();
        ArrayList<String> doclist = new ArrayList<String>();
        Cursor c = sd.query(DOCTOR_ITEM_TABLE, null, null, null, null, null, null);
        try {
            if (c.moveToFirst()) {
                do {
                    doclist.add(c.getString(c.getColumnIndex("visual")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        return doclist;
    }

    public void deleteDoctorItem() {
        sd = this.getWritableDatabase();
        sd.delete(DOCTOR_ITEM_TABLE, null, null);
        Log.e("deleted", "deleted");
    }

    ///////////////Delete DrItem _prescribe////////////
    public void deleteDoctorItemPrescribe() {
        sd = this.getWritableDatabase();
        sd.delete(Dr_PRESCRIBE, null, null);
        Log.e("deleted Prescribe item", "deleted");
    }

    public ArrayList<String> getDocItem(String mdrid) {
        sd = this.getWritableDatabase();
        ArrayList<String> docItems = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb_qty = new StringBuilder();
        StringBuilder sb_pob = new StringBuilder();
        StringBuilder sb_visual = new StringBuilder();
        StringBuilder sb_noc = new StringBuilder();
        StringBuilder sb_rate = new StringBuilder();
        String quer = "select * from phdcritem where dr_id='" + mdrid + "'";
        Cursor c = sd.rawQuery(quer, null);
        try {
            if (c.moveToFirst()) {
                do {
                    sb.append(c.getString(c.getColumnIndex("item_id"))).append(",");
                    sb_qty.append(c.getString(c.getColumnIndex("qty"))).append(",");
                    sb_pob.append(c.getString(c.getColumnIndex("pob"))).append(",");
                    sb_visual.append(c.getString(c.getColumnIndex("visual"))).append(",");
                    sb_noc.append(c.getString(c.getColumnIndex("noc"))).append(",");
                    sb_rate.append(c.getString(c.getColumnIndex("stk_rate"))).append(",");
                }
                while (c.moveToNext());
            }

        } catch (Exception e) {
        } finally {
            c.close();
        }
        if (sb.toString().equals("")) {
            sb.append("0");
        }
        if (sb_qty.toString().equals("")) {
            sb_qty.append("0");
        }
        if (sb_pob.toString().equals("")) {
            sb_pob.append("0");

        }
        if (sb_visual.toString().equals("")) {
            sb_visual.append("0");
        }
        if (sb_noc.toString().equals("")) {
            sb_noc.append("0");
        }
        if (sb_rate.toString().equals("")) {
            sb_rate.append("0");
        }
        docItems.add(sb.toString());
        docItems.add(sb_qty.toString());
        docItems.add(sb_pob.toString());
        docItems.add(sb_visual.toString());
        docItems.add(sb_noc.toString());
        docItems.add(sb_rate.toString());


        return docItems;
    }

    public ArrayList<String> getPrescribeItem(String mdrid) {
        sd = this.getWritableDatabase();
        ArrayList<String> preScribeItem = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb_iDrId = new StringBuilder();
        StringBuilder sb_sItem = new StringBuilder();
        StringBuilder sb_sQty = new StringBuilder();
        String quer = "select * from drprescribe where DR_ID ='" + mdrid + "'";
        Cursor c = sd.rawQuery(quer, null);

        try {
            if (c.moveToFirst()) {
                do {
                    sb_iDrId.append(c.getString(c.getColumnIndex("item_id"))).append(",");
                    sb_sItem.append(c.getString(c.getColumnIndex("pob"))).append(",");
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        preScribeItem.add(sb_sItem.toString());
        preScribeItem.add(sb_iDrId.toString());

        return preScribeItem;
    }



    public Cursor getSampleProductsDcrDrItems(String drid) {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select item_id,item_name from phdcritem  where  item_id in(select item_id from phitem where gift_type='ORIGINAL') AND dr_id='" + drid + "'", null);
    }

    //=============================================================Doctor Products=======================================================================================
    public long insertProducts(String id, String name, double stk_rate, String gift,String SHOW_ON_TOP,String SHOW_YN) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_id", id);
        cv.put("item_name", name);
        cv.put("stk_rate", stk_rate);
        cv.put("gift_type", gift);
        cv.put("SHOW_ON_TOP", SHOW_ON_TOP);
        cv.put("SHOW_YN", SHOW_YN);
        Long l=sd.insert(DOCTOR_PRODUCTS_TABLE, null, cv);
        return l;
    }

    public Cursor getAllVisualAdd(String itemidnotin,String SHOW_ON_TOP) {
        sd = this.getWritableDatabase();

        return sd.rawQuery("select item_id, item_name,stk_rate from phitem where (gift_type='ORIGINAL' or  gift_type='OTHER') and SHOW_YN = '1' and SHOW_ON_TOP='"+SHOW_ON_TOP+"' and item_id not in(" + itemidnotin + ")", null);
    }

    public Cursor getAllProducts(String itemidnotin) {
        sd = this.getWritableDatabase();
        return sd.rawQuery(" Select  T.item_id, item_name,ifnull( PH_STK_ITEM_RATE.RATE ,T.stk_rate) as stk_rate,  sn, VSTOCK.STOCK_QTY,VSTOCK.BALANCE from (select item_id, item_name,stk_rate, '1' as sn from phitem where gift_type='ORIGINAL' and item_id not in(select item_id from phdoctoritem where dr_id="+itemidnotin+") Union all " +
               " select phdoctoritem.item_id,phdoctoritem.item_name,phitem.stk_rate,'0' as sn from phdoctoritem  inner join phitem on phdoctoritem.item_id=phitem.item_id where phdoctoritem.dr_id="+itemidnotin+" order by sn)T "+
                "left join VSTOCK on VSTOCK.ITEM_ID = T.item_id " +
                "left join PH_STK_ITEM_RATE on PH_STK_ITEM_RATE.ITEM_ID = T.item_id and PH_STK_ITEM_RATE.STK_ID ='"+itemidnotin+"'", null);
        // phitem.SHOW_YN = '1' and
        //return sd.rawQuery("select item_id, item_name,'0' as stk_rate, '0' as sn from phdoctoritem where dr_id='"+itemidnotin+"'", null);
    }


    public Cursor getDoctorSpecialityCodeByDrId(String dr_id) {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select phallmst.remark  from phdoctor inner join phallmst on phdoctor.spl_id = phallmst.allmst_id where phdoctor.dr_id=" + dr_id, null);
    }

    public Cursor getDoctorSpecialityIdByDrId(String dr_id) {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select spl_id from phdoctor where dr_id=" + dr_id, null);
    }

    public void giftDelete() {
        sd = this.getWritableDatabase();
        sd.delete("phdcritem", "dr_id=" + Custom_Variables_And_Method.DR_ID + " and item_id in(select item_id from phitem where gift_type='GIFT')", null);
        delete_DCR_Item(""+Custom_Variables_And_Method.DR_ID,null,"GIFT","DR");
    }

    public Cursor getAllGifts(String ItemIdNotIn) {
        sd = this.getWritableDatabase();
        //return sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='GIFT' ", null);
        return sd.rawQuery("select phitem.item_id, phitem.item_name,phitem.stk_rate,VSTOCK.STOCK_QTY,VSTOCK.BALANCE from phitem"
                + " left join VSTOCK on VSTOCK.ITEM_ID = phitem.item_id"
                + " where gift_type='GIFT' ", null);
    }


    public void delete_phitem() {
        sd = this.getWritableDatabase();
        sd.delete("phitem", null, null);
    }

    public void delete_phdoctoritem() {
        sd = this.getWritableDatabase();
        sd.delete("phdoctoritem", null, null);
    }

    public void delete_phdoctor() {
        sd = this.getWritableDatabase();
        sd.delete("phdoctor", null, null);
    }

    public void delete_phallmst() {
        sd = this.getWritableDatabase();
        sd.delete("phallmst", null, null);
    }

    public void delete_phparty() {
        sd = this.getWritableDatabase();
        sd.delete("phparty", null, null);
    }

    public void delete_phrelation() {
        sd = this.getWritableDatabase();
        sd.delete("phrelation", null, null);
    }


    public Cursor getSelectedFromDr(String dr_id) {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select item_name,item_id from phdoctoritem where dr_id=" + dr_id + "", null);
    }

    public long insertDoctorData(int dr_id, int item_id, String item_name) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_id", dr_id);
        cv.put("item_id", item_id);
        cv.put("item_name", item_name);
        long l= sd.insert("phdoctoritem", null, cv);
        return l;
    }

//=========================================================================phdoctor===============================================================================

    public long insert_phdoctor(int dr_id, String dr_name, String dr_code, String area, int spl_id,String LAST_VISIT_DATE
            , String CLASS, String PANE_TYPE, String POTENCY_AMT,String ITEM_NAME
            , String ITEM_POB, String ITEM_SALE, String DR_AREA, String DR_LAT_LONG, String FREQ, String NO_VISITED
            ,String DR_LAT_LONG2,String DR_LAT_LONG3,String COLORYN,String CRM_COUNT,String DRCAPM_GROUP,String SHOWYN) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_id", dr_id);
        cv.put("dr_name", dr_name);
        cv.put("dr_code", dr_code);
        cv.put("area", area);
        cv.put("spl_id", spl_id);
        cv.put("LAST_VISIT_DATE", LAST_VISIT_DATE);

         cv.put("CLASS", CLASS);
        cv.put("PANE_TYPE", PANE_TYPE);
        cv.put("POTENCY_AMT", POTENCY_AMT);
        cv.put("ITEM_NAME", ITEM_NAME);
        cv.put("ITEM_POB", ITEM_POB);
        cv.put("ITEM_SALE", ITEM_SALE);

        cv.put("DR_AREA", DR_AREA);
        cv.put("DR_LAT_LONG", DR_LAT_LONG);

        cv.put("FREQ", FREQ);
        cv.put("NO_VISITED", NO_VISITED);

        cv.put("DR_LAT_LONG2", DR_LAT_LONG2);
        cv.put("DR_LAT_LONG3", DR_LAT_LONG3);
        cv.put("COLORYN", COLORYN);

        cv.put("CRM_COUNT", CRM_COUNT);
        cv.put("DRCAPM_GROUP", DRCAPM_GROUP);
        cv.put("SHOWYN", SHOWYN);

        long l=sd.insert("phdoctor", null, cv);
        return l;
    }

    public Cursor getDoctorListLocal() {
        sd = this.getWritableDatabase();
        //Cursor c=sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_code,phdoctor.dr_name  from phdoctor left outer join phallmst on phdoctor.spl_id = phallmst.id where phdoctor.area in("+MyConnection.pub_area.toUpperCase(Locale.getDefault())+") order by phdoctor.dr_name", null);
        return sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_name,phdoctor.LAST_VISIT_DATE," +
                "phdoctor.CLASS,phdoctor.POTENCY_AMT,phdoctor.ITEM_NAME,phdoctor.ITEM_POB,phdoctor.ITEM_SALE," +
                "phdoctor.DR_AREA,phdoctor.PANE_TYPE, phdoctor.DR_LAT_LONG ,phdoctor.FREQ," +
                " phdoctor.NO_VISITED,phdoctor.DR_LAT_LONG2,phdoctor.DR_LAT_LONG3,phdoctor.COLORYN," +
                "phdoctor.CRM_COUNT,phdoctor.DRCAPM_GROUP,phdoctor.SHOWYN,cast (phdoctor.PANE_TYPE as int) as PANE_TYPE1," +
                " CASE WHEN IFNull(tempdr.dr_id,0) >0 THEN 1 ELSE 0 END AS CALLYN  from phdoctor " +
                "LEFT OUTER JOIN tempdr  ON phdoctor.DR_ID=tempdr.DR_ID where SHOWYN = '1' " +
                "order by PANE_TYPE1 DESC", null);
    }

    public Cursor getRcDoctorListLocal() {
        sd = this.getWritableDatabase();
        //Cursor c=sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_code,phdoctor.dr_name  from phdoctor left outer join phallmst on phdoctor.spl_id = phallmst.id where phdoctor.area in("+MyConnection.pub_area.toUpperCase(Locale.getDefault())+") order by phdoctor.dr_name", null);
        return sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_name,phdoctor.LAST_VISIT_DATE," +
                "phdoctor.CLASS,phdoctor.POTENCY_AMT,phdoctor.ITEM_NAME,phdoctor.ITEM_POB,phdoctor.ITEM_SALE" +
                ",phdoctor.DR_AREA,phdoctor.PANE_TYPE, phdoctor.DR_LAT_LONG ,phdoctor.FREQ, phdoctor.NO_VISITED," +
                "phdoctor.DR_LAT_LONG2,phdoctor.DR_LAT_LONG3,phdoctor.COLORYN,phdoctor.CRM_COUNT,phdoctor.DRCAPM_GROUP," +
                "phdoctor.SHOWYN,cast (phdoctor.PANE_TYPE as int) as PANE_TYPE1," +
                " CASE WHEN IFNull(phdcrdr_rc.dr_id,0) >0 THEN 1 ELSE 0 END AS CALLYN  " +
                "from phdoctor LEFT OUTER JOIN phdcrdr_rc  ON phdoctor.DR_ID=phdcrdr_rc.DR_ID " +
                "where SHOWYN = '1' order by PANE_TYPE1 DESC", null);
    }

    public Cursor getDoctorListLocal(String plan_type,String caltype) {
        sd = this.getWritableDatabase();
        String extraQuery = "";
        if (caltype != null){
            extraQuery = " and CALLYN = " +  caltype ;
        }
        //Cursor c=sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_code,phdoctor.dr_name  from phdoctor left outer join phallmst on phdoctor.spl_id = phallmst.id where phdoctor.area in("+MyConnection.pub_area.toUpperCase(Locale.getDefault())+") order by phdoctor.dr_name", null);
        return sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_name,phdoctor.LAST_VISIT_DATE," +
                "phdoctor.CLASS,phdoctor.POTENCY_AMT,phdoctor.ITEM_NAME,phdoctor.ITEM_POB,phdoctor.ITEM_SALE," +
                "phdoctor.DR_AREA,phdoctor.PANE_TYPE,phdoctor.DR_LAT_LONG,phdoctor.FREQ,phdoctor.NO_VISITED," +
                "phdoctor.DR_LAT_LONG2,phdoctor.DR_LAT_LONG3,phdoctor.COLORYN,phdoctor.CRM_COUNT,phdoctor.DRCAPM_GROUP," +
                "phdoctor.SHOWYN, CASE WHEN IFNull(tempdr.dr_id,0) >0 THEN 1 ELSE 0 END AS CALLYN  " +
                "from phdoctor LEFT OUTER JOIN tempdr  ON phdoctor.DR_ID=tempdr.DR_ID" +
                " where SHOWYN = '1' and PANE_TYPE='"+plan_type+"'" + extraQuery  , null);
    }

    public Cursor getWorkWithLocal() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select pa_name,pa_id from phparty where desig_id >=1 and desig_id <=10 order by pa_name", null);
    }


    //=========================================================================phallmst===============================================================================

    public long insert_phallmst(int allmst_id, String table_name, String field_name, String remark) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("allmst_id", allmst_id);
        cv.put("table_name", table_name);
        cv.put("field_name", field_name);
        cv.put("remark", remark);
        long l=sd.insert("phallmst", null, cv);
        return l;
    }

    //=========================================================================phparty===============================================================================

    public long insert_phparty(int pa_id, String pa_name, int desig_id, String category,
                               int hqid,String PA_LAT_LONG,String PA_LAT_LONG2,
                               String PA_LAT_LONG3,String SHOWYN) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pa_id", pa_id);
        cv.put("pa_name", pa_name);
        cv.put("desig_id", desig_id);
        cv.put("category", category);
        cv.put("hq_id", hqid);
        cv.put("PA_LAT_LONG", PA_LAT_LONG);
        cv.put("PA_LAT_LONG2", PA_LAT_LONG2);
        cv.put("PA_LAT_LONG3", PA_LAT_LONG3);
        cv.put("SHOWYN", SHOWYN);
        long l= sd.insert("phparty", null, cv);
        return l;
    }

    //=========================================================================phrelation===============================================================================

    public long insert_phrelation(int pa_id, int under_id, int rank) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pa_id", pa_id);
        cv.put("under_id", under_id);
        cv.put("rank", rank);
        long l= sd.insert("phrelation", null, cv);
        return l;
    }

    //=========================================================================ph-item_pl===============================================================================

    public long insert_phitempl(String item_id, String dr_spl_id, int srno) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("item_id", item_id);
        cv.put("dr_spl_id", dr_spl_id);
        cv.put("srno", srno);
        long l= sd.insert("phitemspl", null, cv);
        return l;
    }

    public void delete_phitemspl() {
        sd = this.getWritableDatabase();
        sd.delete("phitemspl", null, null);
    }

    public Cursor getphitemSpl() {
        sd = this.getWritableDatabase();
        //Cursor c=sd.rawQuery("select phitem.item_name,phitem.item_id from phitemspl inner join phitem phitem on phitem.item_id=phitemspl.item_id where phitemspl.dr_spl_id="+MyConnection.DOCTOR_SPL_ID+" order by phitem.item_name", null);
        return sd.rawQuery("select phitem.item_name,phitem.item_id from phitemspl inner join phitem phitem on phitem.item_id=phitemspl.item_id where  SHOW_YN = '1' and phitemspl.dr_spl_id=" + Custom_Variables_And_Method.DOCTOR_SPL_ID + " order by phitemspl.srno", null);
    }

    //=========================================================================Final_dcr_check===============================================================================

    public long insertfinalTest(String chemist, String stockist, String exp) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("chemist", chemist);
        cv.put("stockist", stockist);
        cv.put("exp", exp);
        long l= sd.insert("finaldcrcheck", null, cv);
        return l;
    }

    public void updatefinalTest(String chemist, String stockist, String exp) {
        ContentValues cv = new ContentValues();
        cv.put("chemist", chemist);
        cv.put("chemist", chemist);
        cv.put("chemist", chemist);

        sd = this.getWritableDatabase();
        sd.update("finaldcrcheck", cv, null, null);
    }

    public Cursor getFinalSubmit() {

        sd = this.getWritableDatabase();
        return sd.query("finaldcrcheck", null, null, null, null, null, null);
    }

    public void deleteFinalDcr() {
        sd = this.getWritableDatabase();
        sd.delete("finaldcrcheck", null, null);
    }

    //===========================================FTP TABLE======================================================================================================

    public long insert_FtpData(String ip, String user, String pass, String port, String path) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ftpip", ip);
        cv.put("username", user);
        cv.put("password", pass);
        cv.put("port", port);
        cv.put("path", path);
        long l= sd.insert("ftpdetail", null, cv);
        return l;
    }

    public Cursor getFTPDATA() {

        sd = this.getWritableDatabase();
        return sd.query("ftpdetail", null, null, null, null, null, null);
    }

    public void deleteFTPTABLE() {
        sd = this.getWritableDatabase();
        sd.delete("ftpdetail", null, null);
    }

    //======================================================================chemist table=============================================================================

    public long insert_Chemist(int chid, String chname, String area, String chem_code,String LAST_VISIT_DATE,
                               String DR_LAT_LONG,String DR_LAT_LONG2,String DR_LAT_LONG3,String SHOWYN){
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("chem_id", chid);
        cv.put("chem_name", chname);
        cv.put("chem_code", chem_code);
        cv.put("area", area);
        cv.put("LAST_VISIT_DATE", LAST_VISIT_DATE);
        cv.put("DR_LAT_LONG", DR_LAT_LONG);
        cv.put("DR_LAT_LONG2", DR_LAT_LONG2);
        cv.put("DR_LAT_LONG3", DR_LAT_LONG3);
        cv.put("SHOWYN", SHOWYN);

        long l= sd.insert("phchemist", null, cv);
        return l;
    }

    public void updateLatLong(String latlong, String id,String type,String index) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        if (type.equals("C")) {
            cv.put("DR_LAT_LONG"+index, latlong);
            sd.update("phchemist", cv, "chem_id =" + id, null);
        }else if (type.equals("D")) {
            cv.put("DR_LAT_LONG"+index, latlong);
            sd.update("phdoctor", cv, "dr_id =" + id, null);
        }else if (type.equals("DP")) {
            cv.put("DR_LAT_LONG"+index, latlong);
            sd.update(PH_DAIRY, cv, "DAIRY_ID =" + id, null);
        }else if (type.equals("S")) {
            cv.put("PA_LAT_LONG"+index, latlong);
            sd.update("phparty", cv, "pa_id =" + id, null);
        }

        insertLat_Long_Reg(id,latlong,type,"",index);

    }


    public void updateLatLongOnCall(String latlong, String id,String type) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();


        if (type.equals("C")) {
            cv.put("DR_LAT_LONG", latlong);
            cv.put("DR_LAT_LONG3", latlong);
            cv.put("DR_LAT_LONG2", latlong);
            cv.put("SHOWYN", "1");
            sd.update("phchemist", cv, "chem_id =" + id, null);
        }else if (type.equals("D")) {
            cv.put("DR_LAT_LONG", latlong);
            cv.put("DR_LAT_LONG3", latlong);
            cv.put("DR_LAT_LONG2", latlong);
            cv.put("SHOWYN", "1");
            sd.update("phdoctor", cv, "dr_id =" + id, null);
        }else if (type.equals("DP")) {
            cv.put("DR_LAT_LONG", latlong);
            cv.put("DR_LAT_LONG3", latlong);
            cv.put("DR_LAT_LONG2", latlong);
           // cv.put("SHOWYN", "1");
            sd.update(PH_DAIRY, cv, "DAIRY_ID =" + id, null);
        }else if (type.equals("S")) {
            cv.put("PA_LAT_LONG", latlong);
            cv.put("PA_LAT_LONG3", latlong);
            cv.put("PA_LAT_LONG2", latlong);
            cv.put("SHOWYN", "1");
            sd.update("phparty", cv, "pa_id =" + id, null);
        }

    }


    public String getLatLong(String type, String id ) {

        sd = this.getWritableDatabase();


        String latlong = "";

        if (type.equals("C")) {
            latlong = getChemistLatLong(id);
        }else if (type.equals("D")) {
            latlong = getDrCall_latLong(id);
        }else if (type.equals("S")) {
            latlong = stockistAllItemLatLong(id);
        }



        return latlong;


    }

    public Cursor getChemistListLocal() {
        sd = this.getWritableDatabase();

        //Cursor c=sd.rawQuery("select phchemist.chem_id,phchemist.chem_name from phchemist where phchemist.area in("+MyConnection.pub_area.toUpperCase(Locale.getDefault())+") order by phchemist.chem_name ", null);
        return sd.rawQuery("select phchemist.chem_id,phchemist.chem_name,phchemist.LAST_VISIT_DATE," +
                "DR_LAT_LONG,DR_LAT_LONG2,DR_LAT_LONG3,SHOWYN, CASE WHEN IFNull(phdcrchem.chem_id,0) >0 THEN 1 ELSE 0 END AS CALLYN" +
                " from phchemist LEFT OUTER JOIN phdcrchem  ON phchemist.chem_id=phdcrchem.chem_id" +
                " where SHOWYN = '1' ", null);
    }


    public void deleteChemist() {
        sd = this.getWritableDatabase();
        sd.delete("phchemist", null, null);
    }
//============================================================Add Chemist Table================================================================================

    public long addChemistInLocal(String chemid, String chemname, String visit_time, String chem_latLong, String chem_address, String updated, String chem_km,String srno,String LOC_EXTRA) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("chem_id", chemid);
        cv.put("chem_name", chemname);
        cv.put("visit_time", visit_time);
        cv.put("chem_latLong", chem_latLong);
        cv.put("chem_address", chem_address);
        cv.put("updated", updated);
        cv.put("chem_km", chem_km);
        cv.put("srno", srno);
        cv.put("LOC_EXTRA", LOC_EXTRA);


        long l= sd.insert("chemisttemp", null, cv);
        return l;
    }


    public void updateChemistKilo(String km, String id) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("updated", "1");
        cv.put("chem_km", km);

        int result = sd.update("chemisttemp", cv, "chem_id =" + id, null);
        Log.e("Inserted into Chemist", "" + km + "Result" + result);

    }


    public String getKm_Chemist(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select chem_km from chemisttemp where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("chem_km"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }

    public String getRemark_Chemist(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select remark from phdcrchem where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("remark"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }

    public String getFileChemist(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select file from phdcrchem where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("file"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }

    public String getRef_LotLong_Chemist(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select Ref_latlong from phdcrchem where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("Ref_latlong"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }
    public String getRate_Chemist(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select rate from phdcrchem where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("rate"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }
    public String getSRNO_Chemist(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select srno from chemisttemp where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("srno"));
                    //Log.d("javed SRNO table",mychem);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }

    public long getUpdateChemistAddress(String chem_id, String chem_address) {
        ContentValues cv = new ContentValues();
        sd = this.getWritableDatabase();

        cv.put("chem_address", chem_address);
        long l= sd.update("chemisttemp", cv, "chem_id =" + chem_id, null);
        return l;
    }


    public String getChemistLatLong(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from chemisttemp where chem_id='" + chem_id + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     mychem = c.getString(c.getColumnIndex("chem_latLong"));
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return mychem;
    }


    public String getChemistAddress(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from chemisttemp where chem_id='" + chem_id + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     mychem = c.getString(c.getColumnIndex("chem_address"));
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return mychem;
    }
    public String getChemistLocExtra(String chem_id) {
        String mychem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from chemisttemp where chem_id='" + chem_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mychem = c.getString(c.getColumnIndex("LOC_EXTRA"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mychem;
    }

    public ArrayList<String> chemistListForFinalSubmit() {

        return chemistListForFinalSubmit(null);
    }

    public ArrayList<String> chemistListForFinalSubmit(String updated) {
        ArrayList<String> chem_list = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c=null;
        if (updated==null) {
             c = sd.rawQuery("select * from chemisttemp", null);
        }else{
            c = sd.rawQuery("select * from chemisttemp where updated='"+updated+"'", null);
        }

        try {
            if (c.moveToFirst()) {
                do {
                    chem_list.add(c.getString(c.getColumnIndex("chem_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chem_list;
    }

    public void deleteTempChemist() {
        sd = this.getWritableDatabase();
        sd.delete("chemisttemp", null, null);
    }

    public Cursor getStockistListLocal() {
        sd = this.getWritableDatabase();
        //Cursor c=sd.rawQuery("select phdoctor.dr_id,phdoctor.dr_code,phdoctor.dr_name  from phdoctor left outer join phallmst on phdoctor.spl_id = phallmst.id where phdoctor.area in("+MyConnection.pub_area.toUpperCase(Locale.getDefault())+") order by phdoctor.dr_name", null);
        return sd.rawQuery("select phparty.pa_name,phparty.pa_id,phparty.PA_LAT_LONG,phparty.PA_LAT_LONG2," +
                "phparty.PA_LAT_LONG3, CASE WHEN IFNull(phdcrstk.stk_id,0) >0 " +
                "THEN 1 ELSE 0 END AS CALLYN  from phparty LEFT OUTER JOIN phdcrstk  ON phdcrstk.stk_id=phparty.pa_id  " +
                "where SHOWYN = '1' and category='STOCKIST'", null);
    }


    public long addStockistInLocal(String stkid, String stkname) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stk_id", stkid);
        cv.put("stk_name", stkname);

        long l= sd.insert("tempstockist", null, cv);
        return l;
    }

    public ArrayList<String> stockistListForFinalSubmit() {

        return stockistListForFinalSubmit(null);
    }
    public ArrayList<String> stockistListForFinalSubmit(String updated) {
        ArrayList<String> chem_list = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c=null;
        //Cursor c = sd.rawQuery("select * from tempstockist", null);
        if (updated==null) {
            c = sd.rawQuery("select * from phdcrstk", null);
        }else{
            c = sd.rawQuery("select * from phdcrstk where updated='"+updated+"'", null);
        }
        try {
            if (c.moveToFirst()) {
                do {
                    chem_list.add(c.getString(c.getColumnIndex("stk_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chem_list;
    }


    public void deleteTempStockist() {
        sd = this.getWritableDatabase();
        sd.delete("tempstockist", null, null);
    }
    //=================================================================login_detail  table==========================================================================

    public long insertLoginDetail(String comp_code, String db_ip, String db_name, String user, String password, String ver) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("company_code", comp_code);
        cv.put("ols_ip", db_ip);
        cv.put("ols_db_name", db_name);
        cv.put("ols_db_user", user);
        cv.put("ols_db_password", password);
        cv.put("ver", ver);

        long l= sd.insert("logindetail", null, cv);
        return l;
    }

    public Cursor getDatabaseDetail() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select * from logindetail", null);
    }


    public void deleteLoginDetail() {
        sd = this.getWritableDatabase();
        sd.delete("logindetail", null, null);
    }
    //===============================================================login user details=====================================================================================


    public long insertUserDetails(int paid, String paname, String hqtr, String desid, String pubdesid, String compname, String weburl) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pa_id", paid);
        cv.put("pa_name", paname);
        cv.put("head_qtr", hqtr);
        cv.put("desid", desid);
        cv.put("pub_desig_id", pubdesid);
        cv.put("compny_name", compname);
        cv.put("web_url", weburl);
        Long a=sd.insert("userdetail", null, cv);
        return a;
    }

    public long insertUserDetail22(String loc, String visual) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("location_required", loc);
        cv.put("visual_required", visual);
        Long a=sd.insert("userdetail2", null, cv);
        return a;
    }

    public Cursor getUserDetailLogin() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select * from userdetail", null);
    }

    public Cursor getOtherUserDetail() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select * from userdetail2", null);
    }

    public String getLocationDetail() {
        String loc = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select location_required from userdetail2", null);
        try {
            if (c.moveToFirst()) {
                do {
                    loc = c.getString(c.getColumnIndex("location_required"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return loc;
    }

    public String getVisualDetail() {
        String loc = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select visual_required from userdetail2", null);
        try {
            if (c.moveToFirst()) {
                do {
                    loc = c.getString(c.getColumnIndex("visual_required"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return loc;
    }

    public void deleteUserDetail()

    {
        sd = this.getWritableDatabase();
        sd.delete("userdetail", null, null);
    }

    public void deleteUserDetail2()

    {
        sd = this.getWritableDatabase();
        sd.delete("userdetail2", null, null);
    }
    //================================================================dcr detail============================================================================================


    public long insertDcrDetails(String dcrid, String pubarea) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        cv.put("pub_area", pubarea);
        Long a= sd.insert("dcrdetail", null, cv);
        return a;
    }

    public Cursor getDCRDetails() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select * from dcrdetail", null);
    }


    public void deleteDCRDetails() {
        sd = this.getWritableDatabase();
        sd.delete("dcrdetail", null, null);

    }

    //================================================================PHDCRCHEM TABLE====================================================================================

    public long submitChemistInLocal(String dcrid, String chemid, String pobamt, String allitemid, String allitemqty, String address,
                                     String allgiftid, String allgiftqty, String time, String battryLevel, String sample,String remark,
                                     String file,String LOC_EXTRA,String Ref_latlong,String rate) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        cv.put("chem_id", chemid);
        cv.put("pob_amt", pobamt);
        cv.put("allitemid", allitemid);
        cv.put("allitemqty", allitemqty);
        cv.put("address", address);
        cv.put("allgiftid", allgiftid);
        cv.put("allgiftqty", allgiftqty);
        cv.put("time", time);
        cv.put("battery_level", battryLevel);
        cv.put("sample", sample);
        cv.put("remark", remark);
        cv.put("file", file);
        cv.put("LOC_EXTRA", LOC_EXTRA);
        cv.put("Ref_latlong", Ref_latlong);
        cv.put("rate", rate);

        Long a=sd.insert("phdcrchem", null, cv);

        delete_DCR_Item(chemid,null,null,"CHEM");
        insert_DCR_Item(chemid,allitemid,sample,"SAMPLE","CHEM");
        insert_DCR_Item(chemid,allgiftid,allgiftqty,"GIFT","CHEM");

        return a;
    }

    public int updateChemistInLocal(String dcrid, String chemid, String pobamt, String allitemid, String allitemqty,
                                    String address, String allgiftid, String allgiftqty, String time, String sample,
                                    String remark,String file,String rate) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        cv.put("chem_id", chemid);

        if(!allitemid.equals("")) {
            cv.put("pob_amt", pobamt);
            cv.put("allitemid", allitemid);
            cv.put("allitemqty", allitemqty);
            cv.put("sample", sample);
        }
        //cv.put("address", address);
        if(!allgiftid.equals("")) {
            cv.put("allgiftid", allgiftid);
            cv.put("allgiftqty", allgiftid);
        }

        cv.put("remark", remark);
        cv.put("file", file);
        cv.put("rate", rate);
        int a=sd.update("phdcrchem", cv, "chem_id=" + chemid + "", null);

        delete_DCR_Item(chemid,null,null,"CHEM");
        insert_DCR_Item(chemid,allitemid,sample,"SAMPLE","CHEM");
        insert_DCR_Item(chemid,allgiftid,allgiftqty,"GIFT","CHEM");
        return a;

    }


    public ArrayList<String> searchChemist(String chem_id) {
        ArrayList<String> chem_list = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from phdcrchem where chem_id=" + chem_id + "", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chem_list.add(c.getString(c.getColumnIndex("chem_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }

        return chem_list;
    }

    public ArrayList<String> getChemistItem(String mdrid) {
        sd = this.getWritableDatabase();
        ArrayList<String> docItems = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb_qty = new StringBuilder();
        StringBuilder sb_pob = new StringBuilder();
        StringBuilder address = new StringBuilder();
        StringBuilder gift_id = new StringBuilder();
        StringBuilder gift_qty = new StringBuilder();
        StringBuilder time = new StringBuilder();
        String quer = "select * from phdcrchem where chem_id='" + mdrid + "'";
        Cursor c = sd.rawQuery(quer, null);
        try {
            if (c.moveToFirst()) {
                do {
                    sb.append(c.getString(c.getColumnIndex("allitemid"))).append(",");
                    sb_qty.append(c.getString(c.getColumnIndex("allitemqty"))).append(",");
                    sb_pob.append(c.getString(c.getColumnIndex("pob_amt"))).append(",");
                    address.append(c.getString(c.getColumnIndex("address"))).append(",");
                    gift_id.append(c.getString(c.getColumnIndex("allgiftid"))).append(",");
                    gift_qty.append(c.getString(c.getColumnIndex("allgiftqty"))).append(",");
                    time.append(c.getString(c.getColumnIndex("time"))).append(",");

                }
                while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        docItems.add(sb.toString());
        docItems.add(sb_qty.toString());
        docItems.add(sb_pob.toString());
        docItems.add(address.toString());
        docItems.add(gift_id.toString());
        docItems.add(gift_qty.toString());
        docItems.add(time.toString());

        return docItems;
    }

    public String chemAllItem(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allitemid from phdcrchem where chem_id='" + chid + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     chtem = c.getString(c.getColumnIndex("allitemid"));
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return chtem;

    }

    public String chemAllItemGiftQty(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allgiftqty from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("allgiftqty"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }

    public String chemAllItemGiftid(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allgiftid from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("allgiftid"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }


    public void upDateChemistAddressFinal(String chem_id, String address) {

        ContentValues cv = new ContentValues();
        sd = this.getWritableDatabase();

        cv.put("address", address);

        long result = sd.update("phdcrchem", cv, "chem_id =" + chem_id, null);


    }

    public void upDateChemistLocExtra(String chem_id, String address) {

        ContentValues cv = new ContentValues();
        sd = this.getWritableDatabase();

        cv.put("LOC_EXTRA", address);

        long result = sd.update("phdcrchem", cv, "chem_id =" + chem_id, null);


    }


    public String chemAllItemAddress(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select address from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("address"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }

    public String chemAllItemLocExtra(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select LOC_EXTRA from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("LOC_EXTRA"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }

    public String chemAllItemQty(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allitemqty from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("allitemqty"));
                    if (chtem.equals("")) {
                        return "0";
                    }
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }

    public String chemAllItemPob(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select pob_amt from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("pob_amt"));
                } while (c.moveToNext());

            }
        } finally {
            c.close();
        }
        return chtem;
    }

    public String chemAllItemSample(String chid) {
        String sample = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select sample from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    sample = c.getString(c.getColumnIndex("sample"));
                } while (c.moveToNext());

            }
        } finally {
            c.close();
        }
        return sample;
    }

    public String chemAllTime(String chid) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select time from phdcrchem where chem_id='" + chid + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     chtem = c.getString(c.getColumnIndex("time"));
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return chtem;
    }

    public String chemBatterLevel(String chid) {
        String level = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select battery_level from phdcrchem where chem_id='" + chid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    level = c.getString(c.getColumnIndex("battery_level"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return level;
    }


    public void deleteChemistRecordsTable() {
       sd = this.getWritableDatabase();
        sd.delete("phdcrchem", null, null);
    }

    public long insertChemistSample(String chem_id, String item_id, String item_name, String qty, String sample) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("chem_id", chem_id);
        cv.put("item_id", item_id);
        cv.put("item_name", item_name);
        cv.put("qty", qty);
        cv.put("sample", sample);
        long val = sd.insert("chemistsample", null, cv);
        Log.e("chemist sample inserted", "" + val);
        return val;

    }

    public ArrayList<String> getChemistIdForsample() {
        ArrayList<String> chem_id = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from chemistsample", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chem_id.add(c.getString(c.getColumnIndex("chem_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chem_id;
    }

    public void deleteChemistSample() {
        sd = this.getWritableDatabase();
        sd.delete("chemistsample", null, null);
    }


    //================================================================================================================================================================

    public long submitStockitInLocal(int dcrid, String stkid, String pobamt, String allitemid,
                                     String allitemqty, String address, String time, String battery_level,
                                     String latLong, String updated, String stk_km,String srno,String sample,
                                     String remark,String file, String LOC_EXTRA,String allgiftid,String allgiftqty,
                                     String Ref_latlong,String rate) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        cv.put("stk_id", stkid);
        cv.put("pob_amt", pobamt);
        cv.put("allitemid", allitemid);
        cv.put("allitemqty", allitemqty);
        cv.put("address", address);
        cv.put("time", time);
        cv.put("battery_level", battery_level);
        cv.put("latLong", latLong);
        cv.put("updated", updated);
        cv.put("stk_km", stk_km);
        cv.put("srno", srno);
        cv.put("sample", sample);
        cv.put("remark", remark);
        cv.put("file", file);
        cv.put("LOC_EXTRA", LOC_EXTRA);
        cv.put("allgiftid", allgiftid);
        cv.put("allgiftqty", allgiftqty);
        cv.put("Ref_latlong", Ref_latlong);
        cv.put("rate", rate);
        Long a=sd.insert("phdcrstk", null, cv);

        delete_DCR_Item(stkid,null,null,"STK");
        insert_DCR_Item(stkid,allitemid,sample,"SAMPLE","STK");
        insert_DCR_Item(stkid,allgiftid,allgiftqty,"GIFT","STK");

        return a;
    }

    public int updateStockistInLocal(int dcrid, String stkid, String pobamt, String allitemid, String allitemqty,
                                     String address, String time,String sample,String remark,String file,String allgiftid,
                                     String allgiftqty,String rate) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        cv.put("stk_id", stkid);

        if(!allitemid.equals("")) {
            cv.put("pob_amt", pobamt);
            cv.put("allitemid", allitemid);
            cv.put("allitemqty", allitemqty);
            //cv.put("address", address);
            cv.put("sample", sample);
        }
        cv.put("remark", remark);
        cv.put("file", file);
        //cv.put("time", time);

        if(!allgiftid.equals("")) {
            cv.put("allgiftid", allgiftid);
            cv.put("allgiftqty", allgiftqty);
        }
        cv.put("rate", rate);

        int a=sd.update("phdcrstk", cv, "stk_id=" + stkid + "", null);

        delete_DCR_Item(stkid,null,null,"STK");
        insert_DCR_Item(stkid,allitemid,sample,"SAMPLE","STK");
        insert_DCR_Item(stkid,allgiftid,allgiftqty,"GIFT","STK");

        return a;

    }


    public void updateStk_Km(String km, String id) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("updated", "1");
        cv.put("stk_km", km);

        int result = sd.update("phdcrstk", cv, "stk_id =" + id, null);
        Log.e("Stk Km Inserted", km + "...." + result);

    }


    public String getKm_Stockist(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select stk_km from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("stk_km"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String getRemark_Stockist(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select remark from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("remark"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String getFile_Stockist(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select file from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("file"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }
    public String getRefLatLong_Stockist(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select Ref_latlong from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("Ref_latlong"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }
    public String getRate_Stockist(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select rate from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("rate"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }
    public String getSRNO_Stockist(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select srno from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("srno"));
                    //Log.d("javed SRNO table",value);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public ArrayList<String> searchStockist(String skt_id) {
        ArrayList<String> chem_list = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from phdcrstk where stk_id=" + skt_id + "", null);

        try {
            if (c.moveToFirst()) {
                do {
                    chem_list.add(c.getString(c.getColumnIndex("stk_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chem_list;
    }


    public String stockistAllItemAddress(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select address from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("address"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String stockistAllItemLatLong(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select latLong from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("latLong"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String stockistAllItemLocExtra(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select LOC_EXTRA from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("LOC_EXTRA"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public void stockistupdateAllItemAddress(String stk_id, String address) {

        ContentValues cv = new ContentValues();
        sd = this.getWritableDatabase();

        cv.put("address", address);

        long result = sd.update("phdcrstk", cv, "stk_id =" + stk_id, null);


    }

    public void stockistupdateAllItemLocExtra(String stk_id, String address) {

        ContentValues cv = new ContentValues();
        sd = this.getWritableDatabase();

        cv.put("address", address);

        long result = sd.update("phdcrstk", cv, "stk_id =" + stk_id, null);


    }

    public String stockistAllItemPOB(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select pob_amt from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("pob_amt"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String stockistAllItemSample(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select sample from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("sample"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }


    public String stockistAllItemGiftid(String stk_id) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allgiftid from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("allgiftid"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }


    public String stockistAllItemGiftQty(String stk_id) {
        String chtem = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allgiftqty from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    chtem = c.getString(c.getColumnIndex("allgiftqty"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chtem;
    }


    public String stockistAllItemQty(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allitemqty from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("allitemqty"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }


    public String stockistAllItemId(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select allitemid from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("allitemid"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String stockistAllTime(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select time from phdcrstk where stk_id='" + stk_id + "'", null);
       try {
           if (c.moveToFirst()) {
               do {
                   value = c.getString(c.getColumnIndex("time"));
               } while (c.moveToNext());
           }
        } finally {
            c.close();
        }
        return value;
    }

    public String stockist_Battery(String stk_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select battery_level from phdcrstk where stk_id='" + stk_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("battery_level"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public void deleteStockistRecordsTable() {
        sd = this.getWritableDatabase();
        sd.delete("phdcrstk", null, null);

    }

    //================================================================Util Table======================================================================================

    public long insertUtils(String area) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pub_area", area);
        Long a=sd.insert("utils", null, cv);
        return a;
    }

    public Cursor getUtils() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select * from utils", null);
    }

    public void deleteUtils() {
        sd = this.getWritableDatabase();
        sd.delete("utils", null, null);
    }
    //===============================================================dr-Reminder Table==================================================================================
    //String RC_DOCTOR="CREATE TABLE phdcrdr_rc ( id integer primary key,dcr_id text,dr_id text,address text,time text)";

    public long insertDrRem(String dcrid, String drid, String address, String time, String latLong, String updated,
                            String rc_km,String srno,String batteryLevel,String remark,String file,String LOC_EXTRA,String Ref_latlong) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        cv.put("dr_id", drid);
        cv.put("address", address);
        cv.put("time", time);
        cv.put("latLong", latLong);
        cv.put("updated", updated);
        cv.put("rc_km", rc_km);
        cv.put("srno", srno);
        cv.put("batteryLevel", batteryLevel);
        cv.put("remark", remark);
        cv.put("file", file);
        cv.put("LOC_EXTRA", LOC_EXTRA);
        cv.put("Ref_latlong", Ref_latlong);
        Long a=sd.insert("phdcrdr_rc", null, cv);
        return a;
    }

    public void updateKm_RC(String km, String id) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (!id.equals("")) {
            cv.put("updated", "" + 1);
            cv.put("rc_km", km);

            int result = sd.update("phdcrdr_rc", cv, "dr_id =" + id, null);
            Log.e("Km inserted Reminder", km + "....." + result);
        }
    }

    public ArrayList<String> getDrRc() {

        return getDrRc(null);
    }

    public ArrayList<String> getDrRc(String updated) {
        ArrayList<String> drlist = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c=null;
        if (updated==null) {
            c = sd.rawQuery("select * from phdcrdr_rc", null);
        }else{
            c = sd.rawQuery("select * from phdcrdr_rc where updated='"+updated+"'", null);
        }
        try {
            if (c.moveToFirst()) {
                do {
                    drlist.add(c.getString(c.getColumnIndex("dr_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return drlist;
    }

    public String getKm_Rc(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select rc_km from phdcrdr_rc where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("rc_km"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }


    public String getSRNO_Rc(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select srno from phdcrdr_rc where dr_id='" + drid + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     dcrid = c.getString(c.getColumnIndex("srno"));
                     // Log.d("javed SRNO table",dcrid);
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String Rc_Battery(String dr_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select batteryLevel from phdcrdr_rc where dr_id='" + dr_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("batteryLevel"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String Rc_remark(String dr_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select remark from phdcrdr_rc where dr_id='" + dr_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("remark"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String Rc_file(String dr_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select file from phdcrdr_rc where dr_id='" + dr_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("file"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }

    public String Rc_RefLatLong(String dr_id) {
        String value = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select Ref_latlong from phdcrdr_rc where dr_id='" + dr_id + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    value = c.getString(c.getColumnIndex("Ref_latlong"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return value;
    }
    public String getTime_RC(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select time from phdcrdr_rc where dr_id='" + drid + "'", null);
       try {
           if (c.moveToFirst()) {
               do {
                   dcrid = c.getString(c.getColumnIndex("time"));
               } while (c.moveToNext());
           }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getAddress_RC(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select address from phdcrdr_rc where dr_id='" + drid + "'", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     dcrid = c.getString(c.getColumnIndex("address"));
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return dcrid;
    }
    public String getLatLong_RC(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select latLong from phdcrdr_rc where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("latLong"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }
    public String getLocExtra_RC(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select LOC_EXTRA from phdcrdr_rc where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("LOC_EXTRA"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public void Dr_RCupdateAllItemAddress(String drid, String address) {

        ContentValues cv = new ContentValues();
        sd = this.getWritableDatabase();

        cv.put("address", address);

        long result = sd.update("phdcrdr_rc", cv, "dr_id =" + drid, null);


    }
    public void deleteDoctorRc() {
        sd = this.getWritableDatabase();
        sd.delete("phdcrdr_rc", null, null);
    }

    //==============================================================================VERSION TABLE==========================================================================

    public long insertVersionInLocal(String ver) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ver", ver);
        Long a=sd.insert("version", null, cv);
        return a;
    }

    public String getNewVersion() {
        String version = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select ver from version", null);
        try {
            if (c.moveToFirst()) {
                do {
                    version = c.getString(c.getColumnIndex("ver"));
                } while (c.moveToNext());

            }
        } finally {
            c.close();
        }
        return version;
    }

    public void deleteVersion() {
        sd = this.getWritableDatabase();
        sd.delete("version", null, null);
    }

    //==============================================Doctor Workwith table===================================================================================================
    String WORK_WITH_TABLE = "CREATE TABLE dr_workwith ( id integer primary key,workwith text,wwid text)";

    public long insertDrWorkWith(String wwname, String wwid) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("workwith", wwname);
        cv.put("wwid", wwid);
        Long a=sd.insert("dr_workwith", null, cv);
        return a;
    }


    public Cursor getDR_Workwith() {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select workwith,wwid from dr_workwith ", null);
    }

    public void deleteDRWorkWith() {
        sd = this.getWritableDatabase();
        sd.delete("dr_workwith", null, null);
        Log.e("ww", "work with deleted");
    }

    //==========================================================doctor added once more==============================================================================
    String DOCTOR_IN_LOCAL = "CREATE TABLE phdcrdr_more ( id integer primary key,dr_id text,dr_name text,ww1 text,ww2 text,ww3 text,loc text,time text)";

    public long AddedDoctorMore(String drid, String drname, String time, String ww1, String ww2, String ww3, String loc) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("dr_name", drname);
        cv.put("time", time);
        cv.put("ww1", ww1);
        cv.put("ww2", ww2);
        cv.put("ww3", ww3);
        cv.put("loc", loc);
        Long a=sd.insert("phdcrdr_more", null, cv);
        return a;
    }


    public Cursor getDoctorName1() {
        sd = this.getWritableDatabase();
        return sd.query("phdcrdr_more", null, null, null, null, null, null);
    }

    public void deleteDoctormore() {
        sd = this.getWritableDatabase();
        sd.delete("phdcrdr_more", null, null);
    }


    public ArrayList<String> getdoctormoreLit(String updated) {
        ArrayList<String> mylist = new ArrayList<String>();
        sd = this.getWritableDatabase();
       // Cursor c = sd.rawQuery("select dr_id from phdcrdr_more", null);
        Cursor c = null;
        if (updated==null) {
            c = sd.rawQuery("select distinct dr_id from "+DOCTOR_ITEM_TABLE, null);
        }else{
            c = sd.rawQuery("select distinct dr_id from "+DOCTOR_ITEM_TABLE+"  where updated ='"+updated+"'", null);
        }
        try {
            if (c.moveToFirst()) {
                do {
                    mylist.add(c.getString(c.getColumnIndex("dr_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return mylist;
    }

    //===========================================================DCRID table==============================================================================================
    String DCR_ID = "CREATE TABLE mydcr ( id integer primary key,dcr_id text)";

    public long putDcrId(String dcrid) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dcr_id", dcrid);
        Long a=sd.insert("mydcr", null, cv);
        return a;
    }

    public String getDCR_ID_FromLocal() {
        String dcrid = "0";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select dcr_id from mydcr", null);
         try {
             if (c.moveToFirst()) {
                 do {
                     dcrid = c.getString(c.getColumnIndex("dcr_id"));
                 } while (c.moveToNext());
             }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public void deletedcrFromSqlite() {
        sd = this.getWritableDatabase();
        sd.delete("mydcr", null, null);
        Log.e("dcr id has been deleted", "dcr id deleted");
    }

    //=======================================================================Resigned Table=================================================================================

    String RESIGNED = "CREATE TABLE resigned ( id integer primary key,user_name text,password text,doryn text)";


    public String getResignedStatus() {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select doryn from resigned", null);
       try {
           if (c.moveToFirst()) {
               do {
                   dcrid = (c.getString(c.getColumnIndex("doryn")));
                   //dcrid.add(c.getString(c.getColumnIndex("dosyn")));
               } while (c.moveToNext());
           }
       } finally {
            c.close();
        }
        return dcrid;
    }

    public String getResignedReason() {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select * from resigned", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = (c.getString(c.getColumnIndex("dosyn")));
                    //dcrid.add(c.getString(c.getColumnIndex("dosyn")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getPassword() {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select password from resigned", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("password"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }


    public void deleteResigned() {
        sd = this.getWritableDatabase();
        sd.delete("resigned", null, null);
        Log.e("resigned  deleted", "resigned deleted");
    }

    //====================================================Doctor Table for final submit==================================================================================
    //"CREATE TABLE tempdr ( id integer primary key,dr_id text,dr_name text,batteryLevel text,dr_latLong text,dr_address text)";
    public long addTempDrInLocal(String drid, String drname, String visit_time, String batteryLevel, String dr_latLong, String dr_address,
                                 String dr_remark, String updated, String dr_Km,String srno,String work_with_name,String DR_AREA,
                                 String file,String call_type, String LOC_EXTRA,String Ref_latlong) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_id", drid);
        cv.put("dr_name", drname);
        cv.put("visit_time", visit_time);
        cv.put("batteryLevel", batteryLevel);
        cv.put("dr_latLong", dr_latLong);
        cv.put("dr_address", dr_address);
        cv.put("dr_remark", dr_remark);
        cv.put("updated", updated);
        cv.put("dr_km", dr_Km);
        cv.put("srno", srno);
        cv.put("work_with_name",work_with_name);
        cv.put("DR_AREA", DR_AREA);
        cv.put("file", file);
        cv.put("call_type", call_type);
        cv.put("LOC_EXTRA", LOC_EXTRA);
        cv.put("Ref_latlong",Ref_latlong);

        Long a=sd.insert("tempdr", null, cv);
        //Log.d("javed SRNO table",srno);
        return a;
    }


    public void updateRemark_TempDrInLocal(String drid, String dr_remark) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("dr_remark", dr_remark);
        sd.update("tempdr", cv, "dr_id =" + drid, null);
        //Log.d("javed SRNO table",srno);
    }

    public void setUpdateAllZero() {

        sd = this.getWritableDatabase();
        String drUpdate = "update tempdr set updated ='0'";
        sd.execSQL(drUpdate);
        String chemUpdate = "update chemisttemp set updated ='0'";
        sd.execSQL(chemUpdate);
        String stkUpdate = "update phdcrstk set updated ='0'";
        sd.execSQL(stkUpdate);
        String drRcUpdate = "update phdcrdr_rc set updated ='0'";
        sd.execSQL(drRcUpdate);

    }

    public void updateDrKilo(String km, String id) {

        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("updated", "1");
        cv.put("dr_km", km);

        int result = sd.update("tempdr", cv, "dr_id =" + id, null);

        Log.e("Inserted into Dr", "" + km + "Result" + result);
    }

    public ArrayList<String> tempDrListForFinalSubmit() {
        return tempDrListForFinalSubmit(null);
    }

    public ArrayList<String> tempDrListForFinalSubmit(String updated) {
        ArrayList<String> chem_list = new ArrayList<String>();
        sd = this.getWritableDatabase();
        Cursor c=null;
        if (updated==null) {
            c = sd.rawQuery("select * from tempdr", null);
        }else{
            c = sd.rawQuery("select * from tempdr where updated ='"+updated+"'", null);
        }
        try {
            if (c.moveToFirst()) {
                do {
                    chem_list.add(c.getString(c.getColumnIndex("dr_id")));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return chem_list;
    }

    public void deleteTempDr() {
        sd = this.getWritableDatabase();
        sd.delete("tempdr", null, null);
    }

    public String getDr_Remark(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select dr_remark from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("dr_remark"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getKm_Doctor(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select dr_km from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("dr_km"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getSRNO_Doctor(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select srno from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("srno"));
                    //Log.d("javed SRNO table",dcrid);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getFILE_Doctor(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select file from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("file"));
                    //Log.d("javed SRNO table",dcrid);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getCALL_TYPE_Doctor(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select call_type from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("call_type"));
                    //Log.d("javed SRNO table",dcrid);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }
    public String getRef_LatLong_Doctor(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select Ref_latlong from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("Ref_latlong"));
                    //Log.d("javed SRNO table",dcrid);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getBattryLevel_RC(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select batteryLevel from tempdr where dr_id='" + drid + "'", null);
       try {
           if (c.moveToFirst()) {
               do {
                   dcrid = c.getString(c.getColumnIndex("batteryLevel"));
               } while (c.moveToNext());
           }
        } finally {
            c.close();
        }
        return dcrid;
    }


    public String updateDrCall_Address(String dr_Address, String drId) {
        String dcrid = "";
        ContentValues contentValues = new ContentValues();
        sd = this.getWritableDatabase();
        contentValues.put("dr_address", dr_Address);
        long result = sd.update("tempdr", contentValues, "dr_id =" + drId, null);
        return dcrid;
    }


    public String getDrCall_latLong(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select dr_latLong from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("dr_latLong"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getDrCall_Location(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select dr_address from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("dr_address"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }

    public String getDrCall_LocExtra(String drid) {
        String dcrid = "";
        sd = this.getWritableDatabase();
        Cursor c = sd.rawQuery("select LOC_EXTRA from tempdr where dr_id='" + drid + "'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    dcrid = c.getString(c.getColumnIndex("LOC_EXTRA"));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return dcrid;
    }


    public ArrayList<String> getDoctorPrescribed() {
        sd = this.getWritableDatabase();
        ArrayList<String> doclist1 = new ArrayList<String>();

        //String qurey="select * from phdcrdr";
        Cursor c = sd.rawQuery("select dr_id from drprescribe", null);


        try {
            if (c.moveToFirst()) {
                do {
                    doclist1.add(c.getString(c.getColumnIndex("dr_id")));
                }
                while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return doclist1;
    }




////////////////////////////===============Farmer Registration Shivam =============//////////////////////

    //  "CREATE TABLE "+PH_Farmer+"(id integer primary key autoincrement,date text,mcc_owner_name text,Mcc_owner_no text,farmer_attendence text,group_meeting_place text,
    // product_detail text,IH_staff_attendence text,sale_to_farmer text,order_book_for_mcc text)";
    public long Save(String date, String owner_name, String owner_no, String farmer_attendence, String group_meeting_place, String product_detail, String IH_staff_attendence, String farmer_sale, String order_book, String mRemark) {
         sd = this.getWritableDatabase();
        try {

            ContentValues contentValues = new ContentValues();

            contentValues.put("date", date);
            contentValues.put("mcc_owner_name", owner_name);
            contentValues.put("Mcc_owner_no", owner_no);
            contentValues.put("farmer_attendence", farmer_attendence);
            contentValues.put("group_meeting_place", group_meeting_place);
            contentValues.put("product_detail", product_detail);
            contentValues.put("IH_staff_attendence", IH_staff_attendence);
            contentValues.put("sale_to_farmer", farmer_sale);
            contentValues.put("order_book_for_mcc", order_book);
            contentValues.put("remark", mRemark);
            sd.insert(PH_Farmer, null, contentValues);
            //return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }

        return 0;
    }


    public ArrayList collect_all_data() {


        ArrayList<String> idList = new ArrayList<String>();
        sd = this.getReadableDatabase();
        //Cursor c=sqLiteDatabase.rawQuery("Select * from PhFarmer", null);
        Cursor c = sd.rawQuery("Select * from " + PH_Farmer, null);
        try {

            if (c.moveToFirst()) {
                do {
                    idList.add(c.getString(c.getColumnIndex("id")));

                }
                while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            c.close();
        }
        return idList;
    }


    /////////======================= getting all data through id =====================/////////////////


    public String date(String id) {

        String date = "";
        sd= this.getReadableDatabase();

        Cursor cursor = sd.rawQuery("select date from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {

                date = cursor.getString(cursor.getColumnIndex("date"));
            }

        }finally {
            cursor.close();
        }

        return date;
    }

    public String remark_farmar(String id) {

        String remark = "";
        sd= this.getReadableDatabase();

        Cursor cursor = sd.rawQuery("select remark from PHFarmer where id =" + id, null);
       try {
           if (cursor.moveToFirst()) {

               remark = cursor.getString(cursor.getColumnIndex("remark"));

           }

        }finally {
            cursor.close();;
        }

        return remark;
    }

    public String owner_name_mcc(String id) {

        String owner_name = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select mcc_owner_name from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {
                owner_name = cursor.getString(cursor.getColumnIndex("mcc_owner_name"));

            }

        }finally {
            cursor.close();
        }

        return owner_name;
    }


    public String owner_no(String id) {
        String owner_no = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select Mcc_owner_no  from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {
                owner_no = cursor.getString(cursor.getColumnIndex("Mcc_owner_no"));

            }

        }finally {
            cursor.close();
        }
        return owner_no;
    }

    public String farmer_attendence( String id) {
        String farmer_atnd = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select farmer_attendence from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {
                farmer_atnd = cursor.getString(cursor.getColumnIndex("farmer_attendence"));
            }

        }finally {
            cursor.close();
        }
        return farmer_atnd;
    }

    public String meeting_place(String id) {
        String meeting_place = "";
        sd = this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select group_meeting_place from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {
                meeting_place = cursor.getString(cursor.getColumnIndex("group_meeting_place"));
            }

        }finally {
            cursor.close();
        }
        return meeting_place;
    }

    public String product_detail(String id) {
        String product = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select product_detail from PHFarmer where id =" + id, null);
       try {
           if (cursor.moveToFirst()) {
               product = cursor.getString(cursor.getColumnIndex("product_detail"));

           }

        }finally {
            cursor.close();
        }
        return product;
    }

    public String IH_staff_attnd(String id) {
        String ih_staff = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select IH_staff_attendence from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {
                ih_staff = cursor.getString(cursor.getColumnIndex("IH_staff_attendence"));
            }
        }finally {
            cursor.close();
        }
        return ih_staff;
    }

    public String sale_to_farmer(String id) {
        String sale_farmer = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select sale_to_farmer from PHFarmer where id =" + id, null);
        try {
            if (cursor.moveToFirst()) {
                sale_farmer = cursor.getString(cursor.getColumnIndex("sale_to_farmer"));

            }
        }finally {
            cursor.close();
        }
        return sale_farmer;
    }

    public String order_book_mcc(String id) {
        String order_book = "";
        sd= this.getReadableDatabase();
        Cursor cursor = sd.rawQuery("select order_book_for_mcc from PHFarmer where id =" + id, null);
       try {
           if (cursor.moveToFirst()) {
               order_book = cursor.getString(cursor.getColumnIndex("order_book_for_mcc"));

           }
        }finally {
            cursor.close();
        }
        return order_book;
    }

    public void deleteFarmar_Table() {
        sd = this.getWritableDatabase();
        sd.delete(PH_Farmer, null, null);


    }


//////////////////////////////========================////////////////////////////////////////////////////

    ///////////////////// RCPA TABLE //////////////////////////////

    //	String myRepa ="CREATE TABLE "+PH_RCPA+"(id integer primary key autoincrement,dcr_id text,paid text,drid text,chemid text,month text,itemid text,qty text)";

    public void insertDataRcpa(String dcrid, String paid, String drid, String chemid, String month, String itemid, String qty) {

        sd = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("dcr_id", dcrid);
        cv.put("paid", paid);
        cv.put("drid", drid);
        cv.put("chemid", chemid);
        cv.put("month", month);
        cv.put("itemid", itemid);
        cv.put("qty", qty);
        sd.insert(PH_RCPA, null, cv);
    }



    public void deleteRcpa_Table() {
        sd = this.getWritableDatabase();
        sd.delete(PH_RCPA, null, null);
    }

/////////////////////////////////////////////////Emp Tracking/////////////////



    public void deleteAllRecord() {


        sd = getWritableDatabase();
       /*
        String query = "Delete * from "+myTable;

        sd.execSQL(query);*/
        sd.delete(myTable, null, null);

    }
/////////////////////////////////////////////////Emp Tracking data 10 minutes/////////////////

    public Long insertData_latLon10(String lat, String lon, String time, String km,String LOC_EXTRA) {

        sd = this.getWritableDatabase();

        if(sd.rawQuery("select * from "+latLong10Minute +" where time='"+time+"'", null).getCount()==0) {

            ContentValues cv = new ContentValues();
            cv.put("lat", lat);
            cv.put("lon", lon);
            cv.put("time", time);
            cv.put("km", km);
            cv.put("updated", "0");
            cv.put("LOC_EXTRA", LOC_EXTRA);
            Long a = sd.insert(latLong10Minute, null, cv);
            return a;
        }
        return 0L;
    }

    public void latLon10_updated(String id){
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("updated", "1");
        sd.update(latLong10Minute, cv, "id =" + id, null);
    }

    public Map getDataFromlatLon10(String updated) {

        Map<String, ArrayList<String>> DataInMap = new HashMap<String, ArrayList<String>>();

        sd = this.getReadableDatabase();
        String selectQuery ="";
        if (updated==null) {
            selectQuery = "select * from " + latLong10Minute;
        }else {
            selectQuery = "select * from " + latLong10Minute +" where updated='"+updated+"'";
        }
        Cursor c = sd.rawQuery(selectQuery, null);
        ArrayList<String> idList = new ArrayList<String>();
        ArrayList<String> latList = new ArrayList<String>();
        ArrayList<String> lonList = new ArrayList<String>();
        ArrayList<String> timeList = new ArrayList<String>();
        ArrayList<String> kmList = new ArrayList<String>();
        ArrayList<String> locExtraList = new ArrayList<String>();


        try {
            if (c.moveToFirst()) {


                do {
                    String id = c.getString(c.getColumnIndex("id"));
                    String lat = c.getString(c.getColumnIndex("lat"));
                    String lon = c.getString(c.getColumnIndex("lon"));
                    String time = c.getString(c.getColumnIndex("time"));
                    String kmData = c.getString(c.getColumnIndex("km"));
                    String LocExtraData = c.getString(c.getColumnIndex("LOC_EXTRA"));

                    idList.add(id);
                    latList.add(lat);
                    lonList.add(lon);
                    timeList.add(time);
                    kmList.add(kmData);
                    locExtraList.add(kmData);

                } while (c.moveToNext());
                DataInMap.put("myId", idList);
                DataInMap.put("myLat", latList);
                DataInMap.put("myMyLon", lonList);
                DataInMap.put("myTime", timeList);
                DataInMap.put("myKm", kmList);
                DataInMap.put("myLocExtra", locExtraList);


            }


        }finally {
            c.close();
        }

        return DataInMap;
    }

    public void deleteAllRecord10() {


        sd = getWritableDatabase();
       /*
        String query = "Delete * from "+myTable;

        sd.execSQL(query);*/
        sd.delete(latLong10Minute, null, null);

    }

    public void deleteRecord10(String id) {


        sd = getWritableDatabase();
       /*
        String query = "Delete * from "+myTable;

        sd.execSQL(query);*/
        sd.delete(latLong10Minute, "id =" + id, null);

    }
////////////////////////////////////ONEmINUTElATlONG////////////////


    public Map getDataFromlatLonFromOneMinute() {

        Map<String, ArrayList<String>> DataInMap = new HashMap<String, ArrayList<String>>();

        sd = this.getReadableDatabase();
        String selectQuery = "select * from " + myTable1MinuteLatLong;
        Cursor c = sd.rawQuery(selectQuery, null);
        ArrayList<String> idList = new ArrayList<String>();
        ArrayList<String> latList = new ArrayList<String>();
        ArrayList<String> lonList = new ArrayList<String>();
        ArrayList<String> timeList = new ArrayList<String>();

       try {
           if (c.moveToFirst()) {


               do {
                   String id = c.getString(c.getColumnIndex("id"));
                   String lat = c.getString(c.getColumnIndex("lat"));
                   String lon = c.getString(c.getColumnIndex("lon"));
                   String time = c.getString(c.getColumnIndex("time"));

                   idList.add(id);
                   latList.add(lat);
                   lonList.add(lon);
                   timeList.add(time);

               } while (c.moveToNext());
               DataInMap.put("myId", idList);
               DataInMap.put("myLat", latList);
               DataInMap.put("myMyLon", lonList);
               DataInMap.put("myTime", timeList);


           }


        }finally {
            c.close();
        }


        return DataInMap;
    }

    public void deleteAllRecordFromOneMinute() {


        sd = getWritableDatabase();
       /*
        String query = "Delete * from "+myTable;

        sd.execSQL(query);*/
        sd.delete(myTable1MinuteLatLong, null, null);

    }

    //////////////////////
    public String getPaid() {
        String paid = "";
        Cursor c = getUserDetailLogin();
        try {
            if (c.moveToFirst()) {
                do {
                    int pa = c.getInt(c.getColumnIndex("pa_id"));
                    paid = String.valueOf(pa);
                } while (c.moveToNext());
            } else {
                paid = "0";
            }

        }finally {
            c.close();
        }

        return paid;
    }

    public String getPaName() {
        String paname = "";
        Cursor c = getUserDetailLogin();

        try {
            if (c.moveToFirst()) {
                do {
                    paname = c.getString(c.getColumnIndex("pa_name"));
                } while (c.moveToNext());
            }

        }finally {
            c.close();
        }
        return paname;
    }

    public String getHeadQtr() {
        String paname = "";
        Cursor c = getUserDetailLogin();
         try {
             if (c.moveToFirst()) {
                 do {
                     paname = c.getString(c.getColumnIndex("head_qtr"));
                 } while (c.moveToNext());
             }
        }finally {
            c.close();
        }
        return paname;
    }

    public String getDESIG() {
        String paname = "";
        Cursor c = getUserDetailLogin();
        try {
            if (c.moveToFirst()) {
                do {
                    paname = c.getString(c.getColumnIndex("desid"));
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return paname;
    }

    public String getDCR_ID() {
        String paname = "";
        Cursor c = getDCRDetails();
        try {
            if (c.moveToFirst()) {
                do {
                    paname = c.getString(c.getColumnIndex("dcr_id"));
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return paname;
    }

    public String getPUB_AREA() {
        String paname = "";
        Cursor c = getUtils();
         try {
             if (c.moveToFirst()) {
                 do {
                     paname = c.getString(c.getColumnIndex("pub_area"));
                 } while (c.moveToNext());
             }
        }finally {
            c.close();
        }
        return paname;
    }

    public String getPUB_DESIG() {
        String paname = "";
        Cursor c = getUserDetailLogin();
       try {
           if (c.moveToFirst()) {
               do {
                   paname = c.getString(c.getColumnIndex("pub_desig_id"));
               } while (c.moveToNext());
           }
        }finally {
            c.close();
        }
        return paname;
    }

    public String getCOMP_NAME() {
        String paname = "";
        Cursor c = getUserDetailLogin();
        try {
            if (c.moveToFirst()) {
                do {
                    paname = c.getString(c.getColumnIndex("compny_name"));
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return paname;
    }

    public String getWEB_URL() {
        String paname = "";
        Cursor c = getUserDetailLogin();
         try {
             if (c.moveToFirst()) {
                 do {
                     paname = c.getString(c.getColumnIndex("web_url"));
                 } while (c.moveToNext());
             }
        }finally {
            c.close();
        }
        return paname;
    }



    public Cursor dataFromAllTables() {

        Cursor resultSet;
        sd = this.getReadableDatabase();
        String query = "select id,'tempdr' as Doc_Type, cast(visit_time as float) as tm, dr_latLong as latLong,updated , cast (srno as int) as srno from tempdr Union all select id,'chemisttemp' as Doc_Type, cast(visit_time as float) as tm, chem_latLong as latLong,updated , cast (srno as int) as srno from chemisttemp Union all select id, 'phdcrstk' as Doc_Type,cast(time as float) as tm, latLong as latLong ,updated , cast (srno as int) as srno from phdcrstk Union all select id, 'phdcrdr_rc' as Doc_Type,cast (time as float) as tm, latLong as latLong ,updated , cast (srno as int) as srno from phdcrdr_rc order by  srno  asc";
        // String query = "select id,'tempdr' as Doc_Type,visit_time as tm, dr_latLong as latLong,updated from tempdr Union all select id,'chemisttemp' as Doc_Type,visit_time as tm, chem_latLong as latLong,updated from chemisttemp Union all select id, 'phdcrstk' as Doc_Type,time as tm, latLong as latLong ,updated from phdcrstk Union all select id, 'phdcrdr_rc' as Doc_Type,time as tm, latLong as latLong ,updated from phdcrdr_rc order by tm asc";
        resultSet = sd.rawQuery(query, null);

        if (resultSet.moveToFirst()) {
            return resultSet;
        } else {
            return null;
        }


    }
//   "CREATE TABLE " + MenuControl + "(id Integer PRIMARY KEY AUTOINCREMENT,menu text,menu_code text,menu_name text)";


    public void insertMenu(String menu, String menu_code, String menu_name, String menu_url, String main_menu_srno) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("menu", menu);
        cv.put("menu_code", menu_code);
        cv.put("menu_name", menu_name);
        cv.put("menu_url", menu_url);
        cv.put("main_menu_srno", main_menu_srno);
        long i = sd.insert(MenuControl, null, cv);


    }

    public Map<String, String> getMenu(String menu,String code) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Map<String, String> MenuDataInMap = new LinkedHashMap<String, String>();
        String query=null;
        if (code.equals("")) {
            query = "Select * from " + MenuControl + " where menu=" + "'" + menu + "'";
        }else{
            query = "Select * from " + MenuControl + " where  menu=" + "'" + menu + "' and menu_code=" + "'" + code + "'";
        }
        //  String query = "Select * from " + MenuControl;
        Cursor c = sd.rawQuery(query, null);
       try {
           if (c.moveToFirst()) {
               do {
                   String menuCode = c.getString(c.getColumnIndex("menu_code"));
                   String menuName = c.getString(c.getColumnIndex("menu_name"));
                   if (!menuName.equals("NA"))
                       MenuDataInMap.put(menuCode, menuName);
               } while (c.moveToNext());
           }
        }finally {
            c.close();
        }
        return MenuDataInMap;
    }

    public ArrayList<String> getTab() {
        sd = this.getWritableDatabase();
        ArrayList<String> Tabs = new ArrayList<>();
        String query = "Select  menu,main_menu_srno from " + MenuControl+" GROUP BY menu order by main_menu_srno";
        //  String query = "Select * from " + MenuControl;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {

                    String menuName = c.getString(c.getColumnIndex("menu"));
                    Tabs.add(menuName);
                   // Log.v("javed tab",menuName);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return Tabs;
    }


    public String getMenuUrl(String menu,String menu_code) {
        sd = this.getWritableDatabase();
        String url="";
        String query = "Select * from " + MenuControl + " where menu=" + "'" + menu + "' and menu_code='"+menu_code+"'";
        //  String query = "Select * from " + MenuControl;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    url = c.getString(c.getColumnIndex("menu_url"));
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return url;
    }


    public int getmenu_count(String table) {
        sd = this.getWritableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(sd, table);
        return Integer.parseInt(""+cnt);

    }

    public HashMap<String, ArrayList<String>> getCallDetail(String table,String look_for_id,String show_edit_delete) {
        sd = this.getWritableDatabase();
        HashMap<String, ArrayList<String>> Tabs = new HashMap<>();
        String query="";
        String name="";
        String time="";
        String id="";
        String where_cluse="";
        ArrayList<String> idList,nameList,timeList,sample_name,sample_qty,sample_pob,sample_noc,remark,gift_name,gift_qty,
                show_edit_delete_list,dr_class_list,dr_potential_list,dr_area_list,dr_work_with_list,attachment_list,
                dr_work_with_id_list,dr_camp_group_list,dr_crm_count_list,color_list;
        ArrayList<String> visible_status=new ArrayList<>();
        idList=new ArrayList<String>();
        nameList=new ArrayList<String>();
        timeList=new ArrayList<String>();
        sample_name=new ArrayList<String>();
        sample_qty=new ArrayList<String>();

        gift_name=new ArrayList<String>();
        gift_qty=new ArrayList<String>();

        sample_pob=new ArrayList<String>();
        sample_noc=new ArrayList<String>();
        remark=new ArrayList<String>();
        show_edit_delete_list=new ArrayList<String>();

        dr_class_list=new ArrayList<String>();
        dr_potential_list=new ArrayList<String>();
        dr_area_list=new ArrayList<String>();
        dr_crm_count_list=new ArrayList<String>();
        dr_camp_group_list=new ArrayList<String>();

        dr_work_with_list=new ArrayList<String>();
        dr_work_with_id_list=new ArrayList<String>();
        attachment_list=new ArrayList<String>();
        color_list=new ArrayList<String>();


            if (table.equals("chemisttemp")) {
                id = "chem_id";
                name = "chem_name";
                time = "visit_time";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select  chem_id,chem_name,visit_time from " + table + " " + where_cluse + " order by srno";
            } else if (table.equals("tempdr")) {
                id = "dr_id";
                name = "dr_name";
                time = "visit_time";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select  dr_id,dr_name,visit_time,dr_remark,work_with_name,DR_AREA,call_type from " + table + " " + where_cluse + " order by srno";
            } else if (table.equals("phdcrstk")) {
                id = "stk_id";
                name = "stk_id";
                time = "time";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select  stk_id,time,remark from " + table + " " + where_cluse + " order by srno";
            } else if (table.equals("phdcrdr_rc")) {
                id = "dr_id";
                name = "dr_id";
                time = "time";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select  dr_id,time,remark from " + table + " " + where_cluse + " order by srno";
            } else if (table.equals("Expenses")) {
                id = "exp_head_id";
                name = "exp_head";
                time = "amount";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select * from " + Expenses ;
            }else if (table.equals("nonListed_call")) {
                id = "sRemark";
                name = "sDrName";
                time = "time";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select * from " + NonListed_call ;
            }else if (table.equals("appraisal")) {
                id = "PA_ID";
                name = "PA_NAME";
                time = "sGRADE_NAME_STR";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select * from " + Appraisal +" where FLAG=1";
            }else if (table.equals("tenivia_traker")) {
                id = "DR_ID";
                name = "DR_NAME";
                time = "TIME";
                if (!look_for_id.equals("")) {
                    where_cluse = " where " + id + "='" + look_for_id + "'";
                }
                query = "Select * from " + Tenivia_traker +" " +where_cluse;
            }else if (table.equals("Dairy") || table.equals("Poultry")) {
                id = "DAIRY_ID";
                name = "DAIRY_NAME";
                time = "visit_time";

                if (table.equals("Dairy")) {
                    where_cluse = " where DOC_TYPE = 'D'" ;
                }else{
                    where_cluse = " where DOC_TYPE = 'P'" ;
                }

                if (!look_for_id.equals("")) {
                    where_cluse = where_cluse + " and " + id + "='" + look_for_id + "'";
                }
                query = "Select * from " + PH_DAIRY_DCR +" " +where_cluse;
            }

            Cursor c = sd.rawQuery(query, null);
            try {
                if (c.moveToFirst()) {
                    do {
                        String dr_sample_name = "";
                        String dr_sample_qty = "";
                        String dr_gift_name = "";
                        String dr_gift_qty = "";
                        String dr_sample_pob = "";
                        String dr_sample_noc="";
                        String pob_amt="0";
                        String dr_remark="";

                        String dr_class = "";
                        String dr_potential = "";
                        String dr_area = "";
                        String dr_crm_count="";
                        String dr_camp_group="";

                        String dr_work_with="";
                        String attachment="";
                        String color="";

                        String dr_id = c.getString(c.getColumnIndex(id));
                        String dr_name = c.getString(c.getColumnIndex(name));
                        String dr_time = c.getString(c.getColumnIndex(time));
                        if (table.equals("phdcrstk")) {
                            String dr_sample_id = "", dr_gift_id = "";
                            if (!c.getString(c.getColumnIndex("remark")).equals("")) {
                                dr_remark = c.getString(c.getColumnIndex("remark"));
                            }
                            Cursor c1 = sd.rawQuery("select pa_name, pa_id from phparty where category='STOCKIST' and pa_id='" + dr_id + "'", null);

                            try {
                                if (c1.moveToFirst()) {
                                    do {
                                        dr_name = c1.getString(c1.getColumnIndex("pa_name"));
                                    } while (c1.moveToNext());
                                }
                            } finally {
                                c1.close();
                            }


                            Cursor c2 = sd.rawQuery("select allitemid,allitemqty,sample,pob_amt,allgiftqty,allgiftid  from phdcrstk where stk_id='" + dr_id + "'", null);

                            try {
                                if (c2.moveToFirst()) {
                                    do {
                                        dr_sample_id = c2.getString(c2.getColumnIndex("allitemid"));
                                        dr_sample_qty = c2.getString(c2.getColumnIndex("sample"));
                                        dr_sample_pob = c2.getString(c2.getColumnIndex("allitemqty"));

                                        dr_gift_id = c2.getString(c2.getColumnIndex("allgiftid"));
                                        dr_gift_qty = c2.getString(c2.getColumnIndex("allgiftqty"));

                                        pob_amt=c2.getString(c2.getColumnIndex("pob_amt"));
                                    } while (c2.moveToNext());
                                }
                            } finally {
                                c2.close();
                            }

                            String dr_sample_id_list[] = dr_sample_id.split(",");
                            for (int i = 0; i < dr_sample_id_list.length; i++) {
                                Cursor c3 = sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='ORIGINAL' and item_id='" + dr_sample_id_list[i] + "'", null);

                                String saparator=",";
                                if (i==0){
                                    saparator="";
                                }

                                try {
                                    if (c3.moveToFirst()) {
                                        do {
                                            dr_sample_name =  dr_sample_name+ saparator +c3.getString(c3.getColumnIndex("item_name"));
                                        } while (c3.moveToNext());
                                    }
                                } finally {
                                    c3.close();
                                }
                            }



                            String dr_gift_id_list[] = dr_gift_id.split(",");
                            for (int i = 0; i < dr_gift_id_list.length; i++) {
                                Cursor c4 = sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='GIFT' and item_id='" + dr_gift_id_list[i] + "'", null);

                                String saparator=",";
                                if (i==0){
                                    saparator="";
                                }

                                try {
                                    if (c4.moveToFirst()) {
                                        do {
                                            dr_gift_name = dr_gift_name + saparator + c4.getString(c4.getColumnIndex("item_name"));
                                        } while (c2.moveToNext());
                                    }
                                } finally {
                                    c4.close();
                                }
                            }


                        } else if (table.equals("phdcrdr_rc")) {

                            if (!c.getString(c.getColumnIndex("remark")).equals("")) {
                                dr_remark = c.getString(c.getColumnIndex("remark"));
                            }

                            Cursor c1 = sd.rawQuery("select dr_id,dr_name,CLASS,POTENCY_AMT,DR_AREA,DRCAPM_GROUP,CRM_COUNT,PANE_TYPE  from phdoctor where dr_id='" + dr_id + "'", null);

                            try {
                                if (c1.moveToFirst()) {
                                    do {
                                        dr_name = c1.getString(c1.getColumnIndex("dr_name"));
                                        dr_area=c1.getString(c1.getColumnIndex("DR_AREA"));
                                        dr_class=c1.getString(c1.getColumnIndex("CLASS"));
                                        dr_potential=c1.getString(c1.getColumnIndex("POTENCY_AMT"));
                                        dr_crm_count=c1.getString(c1.getColumnIndex("CRM_COUNT"));
                                        dr_camp_group=c1.getString(c1.getColumnIndex("DRCAPM_GROUP"));
                                    } while (c1.moveToNext());
                                }
                            } finally {
                                c1.close();
                            }
                        } else if (table.equals("tempdr")) {
                            Float pob=0f;
                            if (!c.getString(c.getColumnIndex("dr_remark")).equals("")) {
                                dr_remark = c.getString(c.getColumnIndex("dr_remark"));
                            }
                            dr_work_with=c.getString(c.getColumnIndex("work_with_name"));
                            dr_area=c.getString(c.getColumnIndex("DR_AREA"));
                            if(c.getString(c.getColumnIndex("call_type")).equals("2")){
                                dr_name+="(U)";
                            }else if(c.getString(c.getColumnIndex("call_type")).equals("1")){
                                dr_name+="(M)";
                            }else{
                                dr_name+="(P)";
                            }

                            Cursor c1 = sd.rawQuery("select CLASS,POTENCY_AMT,CRM_COUNT,DRCAPM_GROUP,PANE_TYPE  from phdoctor where dr_id='" + dr_id + "'", null);

                            try {
                                if (c1.moveToFirst()) {
                                    do {
                                        dr_class=c1.getString(c1.getColumnIndex("CLASS"));
                                        dr_potential=c1.getString(c1.getColumnIndex("POTENCY_AMT"));
                                        dr_crm_count=c1.getString(c1.getColumnIndex("CRM_COUNT"));
                                        dr_camp_group=c1.getString(c1.getColumnIndex("DRCAPM_GROUP"));
                                    } while (c1.moveToNext());
                                }
                            } finally {
                                c1.close();
                            }

                             c1 = sd.rawQuery("select dr_id,item_name,pob,qty,stk_rate,noc from " + DOCTOR_ITEM_TABLE + " where dr_id='" + dr_id + "'", null);
                            try {
                                if (c1.moveToFirst()) {
                                    do {
                                        if (!c1.getString(c1.getColumnIndex("stk_rate")).equals("x")) {
                                            dr_sample_name = c1.getString(c1.getColumnIndex("item_name")) + "," + dr_sample_name;
                                            dr_sample_qty = c1.getString(c1.getColumnIndex("qty")) + "," + dr_sample_qty;
                                            dr_sample_pob = c1.getString(c1.getColumnIndex("pob")) + "," + dr_sample_pob;
                                            dr_sample_noc = c1.getString(c1.getColumnIndex("noc")) + "," + dr_sample_noc;
                                            pob+=Float.parseFloat(c1.getString(c1.getColumnIndex("pob")))*Float.parseFloat(c1.getString(c1.getColumnIndex("stk_rate")));
                                        }
                                        //Log.v("javed tab",c1.getString(c1.getColumnIndex("dr_id"))+"\n"+dr_sample_name+"\n"+dr_sample_qty+"\n"+dr_sample_pob);
                                    } while (c1.moveToNext());
                                }
                            } finally {
                                pob_amt=""+pob;
                                c1.close();
                            }

                            c1 = sd.rawQuery("select dr_id,item_name,pob,qty,stk_rate  from " + DOCTOR_ITEM_TABLE + " where dr_id='" + dr_id + "'", null);
                            try {
                                if (c1.moveToFirst()) {
                                    do {
                                        if (c1.getString(c1.getColumnIndex("stk_rate")).equals("x")) {
                                            dr_gift_name = c1.getString(c1.getColumnIndex("item_name")) + "," + dr_gift_name;
                                            dr_gift_qty = c1.getString(c1.getColumnIndex("qty")) + "," + dr_gift_qty;

                                        }
                                        //Log.v("javed tab",c1.getString(c1.getColumnIndex("dr_id"))+"\n"+dr_sample_name+"\n"+dr_sample_qty+"\n"+dr_sample_pob);
                                    } while (c1.moveToNext());
                                }
                            } finally {
                                c1.close();
                            }

                        }else if (table.equals("Dairy") || table.equals("Poultry")) {
                            Float pob=0f;
                            String dr_sample_id = "", dr_gift_id = "";

                            if (!c.getString(c.getColumnIndex("dr_remark")).equals("")) {
                                dr_remark = c.getString(c.getColumnIndex("dr_remark"));
                            }
                            dr_work_with=c.getString(c.getColumnIndex("person_name"));

                            dr_sample_id = c.getString(c.getColumnIndex("allitemid"));
                            dr_sample_qty = c.getString(c.getColumnIndex("sample"));
                            dr_sample_pob = c.getString(c.getColumnIndex("allitemqty"));

                            dr_gift_id = c.getString(c.getColumnIndex("allgiftid"));
                            dr_gift_qty = c.getString(c.getColumnIndex("allgiftqty"));
                            pob_amt=c.getString(c.getColumnIndex("pob_amt"));




                            String dr_sample_id_list[] = dr_sample_id.split(",");
                            for (int i = 0; i < dr_sample_id_list.length; i++) {
                                Cursor c2 = sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='ORIGINAL' and item_id='" + dr_sample_id_list[i] + "'", null);
                                String saparator=",";
                                if (i==0){
                                    saparator="";
                                }

                                try {
                                    if (c2.moveToFirst()) {
                                        do {
                                            dr_sample_name =  dr_sample_name+ saparator + c2.getString(c2.getColumnIndex("item_name"));
                                        } while (c2.moveToNext());
                                    }
                                } finally {
                                    c2.close();
                                }
                            }

                            String dr_gift_id_list[] = dr_gift_id.split(",");
                            for (int i = 0; i < dr_gift_id_list.length; i++) {
                                Cursor c2 = sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='GIFT' and item_id='" + dr_gift_id_list[i] + "'", null);

                                String saparator=",";
                                if (i==0){
                                    saparator="";
                                }

                                try {
                                    if (c2.moveToFirst()) {
                                        do {
                                            dr_gift_name = dr_gift_name + saparator + c2.getString(c2.getColumnIndex("item_name"));
                                        } while (c2.moveToNext());
                                    }
                                } finally {
                                    c2.close();
                                }
                            }


                        } else if (table.equals("chemisttemp")) {
                            String dr_sample_id = "", dr_gift_id = "";
                            //Cursor c1 =  sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='ORIGINAL' ", null);
                            Cursor c1 = sd.rawQuery("select allitemid,allitemqty,sample,allgiftid,allgiftqty,pob_amt,remark from phdcrchem where chem_id='" + dr_id + "'", null);

                            try {
                                if (c1.moveToFirst()) {
                                    do {
                                        dr_sample_id = c1.getString(c1.getColumnIndex("allitemid"));
                                        dr_sample_qty = c1.getString(c1.getColumnIndex("sample"));
                                        dr_sample_pob = c1.getString(c1.getColumnIndex("allitemqty"));

                                        dr_gift_id = c1.getString(c1.getColumnIndex("allgiftid"));
                                        dr_gift_qty = c1.getString(c1.getColumnIndex("allgiftqty"));
                                        pob_amt=c1.getString(c1.getColumnIndex("pob_amt"));
                                        if (!c1.getString(c1.getColumnIndex("remark")).equals("")) {
                                            dr_remark = c1.getString(c1.getColumnIndex("remark"));
                                        }

                                    } while (c1.moveToNext());
                                }
                            } finally {
                                c1.close();
                            }

                            String dr_sample_id_list[] = dr_sample_id.split(",");
                            for (int i = 0; i < dr_sample_id_list.length; i++) {
                                Cursor c2 = sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='ORIGINAL' and item_id='" + dr_sample_id_list[i] + "'", null);
                                String saparator=",";
                                if (i==0){
                                    saparator="";
                                }

                                try {
                                    if (c2.moveToFirst()) {
                                        do {
                                            dr_sample_name =  dr_sample_name+ saparator + c2.getString(c2.getColumnIndex("item_name"));
                                        } while (c2.moveToNext());
                                    }
                                } finally {
                                    c2.close();
                                }
                            }

                            String dr_gift_id_list[] = dr_gift_id.split(",");
                            for (int i = 0; i < dr_gift_id_list.length; i++) {
                                Cursor c2 = sd.rawQuery("select item_id, item_name,stk_rate from phitem where gift_type='GIFT' and item_id='" + dr_gift_id_list[i] + "'", null);

                                String saparator=",";
                                if (i==0){
                                    saparator="";
                                }

                                try {
                                    if (c2.moveToFirst()) {
                                        do {
                                            dr_gift_name = dr_gift_name + saparator + c2.getString(c2.getColumnIndex("item_name"));
                                        } while (c2.moveToNext());
                                    }
                                } finally {
                                    c2.close();
                                }
                            }

                        }

                        switch (table) {
                            case "Expenses":
                                remark.add(c.getString(c.getColumnIndex("remark")));
                                attachment=c.getString(c.getColumnIndex("FILE_NAME"));
                                break;
                            case "nonListed_call":
                                dr_name +=" ("+c.getString(c.getColumnIndex("sDocType"))+")";
                                remark.add(dr_id);

                                dr_sample_name=c.getString(c.getColumnIndex("iQfl"))+" ("+c.getString(c.getColumnIndex("iSpl"))+")";
                                dr_sample_qty=c.getString(c.getColumnIndex("sAdd1"));
                                dr_sample_pob=c.getString(c.getColumnIndex("sMobileNo"));
                                dr_area=c.getString(c.getColumnIndex("AREA"));
                                dr_class=c.getString(c.getColumnIndex("CLASS"));
                                dr_potential=c.getString(c.getColumnIndex("POTENCY_AMT"));
                                attachment=c.getString(c.getColumnIndex("iSplId"));

                                break;
                            case "appraisal":
                                dr_gift_name =c.getString(c.getColumnIndex("sAPPRAISAL_NAME_STR"));
                                dr_gift_qty=c.getString(c.getColumnIndex("sGRADE_STR"));
                                dr_remark="Observation :- "+c.getString(c.getColumnIndex("sOBSERVATION"))+"\nAction Taken - "+c.getString(c.getColumnIndex("sACTION_TAKEN"));
                                remark.add(dr_remark);
                                break;
                            case "tenivia_traker":
                                dr_remark=c.getString(c.getColumnIndex("REMARK"));
                                remark.add(dr_remark);
                                if (dr_remark.equals("")) {
                                    dr_gift_name = c.getString(c.getColumnIndex("QTY_CAPTION")) ;//+ "," + c.getString(c.getColumnIndex("AMOUN_CAPTION"));
                                    dr_gift_qty = c.getString(c.getColumnIndex("QTY")); //+ "," + c.getString(c.getColumnIndex("AMOUNT"));
                                }
                                break;
                            case "Poultry":
                            case "Dairy":
                            case "phdcrdr_rc":
                                remark.add(dr_remark);
                                break;
                            case "tempdr":
                            case "chemisttemp":
                            case "phdcrstk":
                                if (pob_amt.equals("0") || pob_amt.equals("0.0") || pob_amt.equals(".00") || !Custom_Variables_And_Method.SAMPLE_POB_MANDATORY.equals("N")){
                                    remark.add(dr_remark);
                                }else{
                                    remark.add("POB amount  \u20B9 "+pob_amt+"\n"+ dr_remark);
                                }
                                break;

                            default:
                                remark.add("");
                                break;
                        }
                        idList.add(dr_id);
                        nameList.add(dr_name);
                        timeList.add(dr_time);
                        sample_name.add(dr_sample_name);
                        sample_qty.add(dr_sample_qty);
                        sample_pob.add(dr_sample_pob);
                        sample_noc.add(dr_sample_noc);

                        gift_name.add(dr_gift_name);
                        gift_qty.add(dr_gift_qty);
                        show_edit_delete_list.add(show_edit_delete);
                        visible_status.add(show_edit_delete);

                        dr_area_list.add(dr_area);
                        dr_class_list.add(dr_class);
                        dr_potential_list.add(dr_potential);
                        dr_crm_count_list.add(dr_crm_count);
                        dr_camp_group_list.add(dr_camp_group);

                        dr_work_with_list.add(dr_work_with);
                        attachment_list.add(attachment);
                        color_list.add(color);
                    } while (c.moveToNext());
                }
            } finally {
                c.close();
            }

        if (nameList.size() == 0 && !table.equals("Expenses")) {
            idList.add("0");
            nameList.add("Yet to make your first Call");
            timeList.add("");
            sample_name.add("");
            sample_qty.add("");
            sample_pob.add("");
            visible_status.add("0");
            remark.add("");

            dr_area_list.add("");
            dr_class_list.add("");
            dr_potential_list.add("");
            dr_crm_count_list.add("");
            dr_camp_group_list.add("");

            dr_work_with_list.add("");
            dr_work_with_id_list.add("");
            attachment_list.add("");

        }
        Tabs.put("id", idList);
        Tabs.put("name", nameList);
        Tabs.put("time", timeList);
        Tabs.put("sample_name", sample_name);
        Tabs.put("sample_qty", sample_qty);

        Tabs.put("gift_name", gift_name);
        Tabs.put("gift_qty", gift_qty);

        Tabs.put("sample_pob", sample_pob);
        Tabs.put("sample_noc", sample_noc);
        Tabs.put("visible_status", visible_status);
        Tabs.put("remark", remark);
        Tabs.put("show_edit_delete", show_edit_delete_list);

        Tabs.put("class", dr_class_list);
        Tabs.put("potential", dr_potential_list);
        Tabs.put("area", dr_area_list);
        Tabs.put("dr_crm", dr_crm_count_list);
        Tabs.put("dr_camp_group", dr_camp_group_list);

        Tabs.put("workwith", dr_work_with_list);
        Tabs.put("workwith_id", dr_work_with_id_list);
        Tabs.put("attach", attachment_list);
        Tabs.put("color", color_list);
        return Tabs;

    }

    public int getNotification_count() {
        String countQuery = "SELECT  read_status FROM " + Notification + " Where read_status=0";
         sd = this.getReadableDatabase();
        Cursor cursor = sd.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;

    }
    ////////////////////////////////

    public void deleteMenu() {
        sd = this.getWritableDatabase();
        sd.delete(MenuControl, null, null);
    }
    //////////////////////////////

    public void insert_Notification(String Title, String msg, String logo, String content, String status, String date, String time) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Title", Title);
        cv.put("msg", msg);
        cv.put("logo_url", logo);
        cv.put("content_url", content);
        cv.put("read_status", status);
        cv.put("date", date);
        cv.put("time", time);
        sd.insert(Notification, null, cv);
        Log.v("javed","Notification inserted successfully");

    }
    public HashMap<String, ArrayList<String>> getNotificationMsg() {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String url="";
        HashMap<String, ArrayList<String>> data=new HashMap<String, ArrayList<String>>();
        ArrayList<String> Title,des,time,date,status,logo_url,info_url,id;
        Title=new ArrayList<String>();
        des=new ArrayList<String>();
        time=new ArrayList<String>();
        date=new ArrayList<String>();
        status=new ArrayList<String>();
        logo_url=new ArrayList<String>();
        info_url=new ArrayList<String>();
        id=new ArrayList<String>();

        String query = "Select * from " + Notification +" order by id desc";
        //  String query = "Select * from " + MenuControl;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Title.add(c.getString(c.getColumnIndex("Title")));
                    des.add(c.getString(c.getColumnIndex("msg")));
                    time.add(c.getString(c.getColumnIndex("time")));
                    date.add(c.getString(c.getColumnIndex("date")));
                    status.add(c.getString(c.getColumnIndex("read_status")));
                    logo_url.add(c.getString(c.getColumnIndex("logo_url")));
                    info_url.add(c.getString(c.getColumnIndex("content_url")));
                    id.add(c.getString(c.getColumnIndex("id")));

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        data.put("Title",Title);
        data.put("Des",des);
        data.put("Time",time);
        data.put("Date",date);
        data.put("Status",status);
        data.put("Logo_url",logo_url);
        data.put("Info_url",info_url);
        data.put("ID",id);

        return data;
    }

    public void notificationDeletebyID(String id) {
        sd = this.getWritableDatabase();
        if (id !=null) {
            sd.delete(Notification, "ID =" + id, null);
        }else{
            sd.delete(Notification, null, null);
        }
    }

    public void updateNotificationStatus(String id,String status) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("read_status", status);
        sd.update(Notification,cv ,"ID =" + id,null);
    }

    public void delete_weakOld_Notification(Calendar calendar1) {
        sd = this.getWritableDatabase();

        Calendar calendar2 = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        //String query = "select id,cast(date as Date) as dat  from " + Notification +" WHERE dat>='"+date+"'";
        String query = "select id,date from " + Notification ;
        //  String query = "Select * from " + MenuControl;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Date date2 = dateFormat.parse(c.getString(c.getColumnIndex("date")));
                    calendar2.setTime(date2);
                    if(calendar2.compareTo(calendar1)<1) {
                        notificationDeletebyID(c.getString(c.getColumnIndex("id")));
                        //id.add(c.getString(c.getColumnIndex("id")));
                        Log.v("javed", "Notification deleted successfully " + calendar2.compareTo(calendar1));
                    }
                } while (c.moveToNext());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            c.close();
        }
    }

    public void insert_Approval_count(String approval_menu_code, String approval_count) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("approval_menu_code", approval_menu_code);
        cv.put("approval_count", approval_count);
        sd.insert(Approval_count, null, cv);
       /* Log.v("javed","Approval_count inserted successfully");*/

    }
    public void delete_Approval_count() {
        sd = this.getWritableDatabase();
        sd.delete(Approval_count, null, null);

    }

    public String get_Approval_count(String approval_menu_code) {
        sd = this.getWritableDatabase();
        String  approval_count="0";
        String query = "Select * from " + Approval_count +" where approval_menu_code='"+approval_menu_code+"'";
        //  String query = "Select * from " + MenuControl;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    approval_count=c.getString(c.getColumnIndex("approval_count"));

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return approval_count;
    }

    public void insert_Expense(String exp_head_id, String exp_head,String amount, String remark,String FILE_NAME, String ID, String time) {
        sd = this.getWritableDatabase();
        sd.delete(Expenses, "exp_head_id='"+exp_head_id+"'", null);
        ContentValues cv = new ContentValues();
        cv.put("exp_head_id", exp_head_id);
        cv.put("exp_head", exp_head);
        cv.put("amount", amount);
        cv.put("remark", remark);
        cv.put("FILE_NAME", FILE_NAME);
        cv.put("exp_ID", ID);
        cv.put("time", time);
        sd.insert(Expenses, null, cv);

    }

    public void delete_Expense_withID(String exp_ID) {
        sd = this.getWritableDatabase();
        sd.delete(Expenses, "exp_ID='"+exp_ID+"'", null);
    }

    public void delete_Expense() {
        sd = this.getWritableDatabase();
        sd.delete(Expenses, null, null);

    }

    public  ArrayList<Map<String, String>> get_Expense() {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        sd = this.getWritableDatabase();
        String query = "Select * from " + Expenses ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put("exp_head_id",c.getString(c.getColumnIndex("exp_head_id")));
                    datanum.put("exp_head", c.getString(c.getColumnIndex("exp_head")));
                    datanum.put("amount", c.getString(c.getColumnIndex("amount")));
                    datanum.put("remark",c.getString(c.getColumnIndex("remark")));
                    datanum.put("FILE_NAME",c.getString(c.getColumnIndex("FILE_NAME")));
                    datanum.put("ID",c.getString(c.getColumnIndex("exp_ID")));
                    data.add(datanum);

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }

    public void insert_DOB_DOA(String PA_NAME, String DOB,String DOA, String MOBILE,String TYPE) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PA_NAME", PA_NAME);
        cv.put("DOB", DOB);
        cv.put("DOA", DOA);
        cv.put("MOBILE", MOBILE);
        cv.put("TYPE", TYPE);
        sd.insert(dob_doa, null, cv);

    }

    public void delete_DOB_DOA() {
        sd = this.getWritableDatabase();
        sd.delete(dob_doa, null, null);

    }

    public  ArrayList<Map<String, String>> get_DOB_DOA(String type) {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        sd = this.getWritableDatabase();
        String title="PA_NAME";
        if (type.equals("D")){
            title="DR_NAME";
        }
        String query = "Select * from " + dob_doa +" where TYPE='"+type+"'";
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put(title,c.getString(c.getColumnIndex("PA_NAME")));
                    datanum.put("DOB", c.getString(c.getColumnIndex("DOB")));
                    datanum.put("DOA", c.getString(c.getColumnIndex("DOA")));
                    datanum.put("MOBILE",c.getString(c.getColumnIndex("MOBILE")));
                    data.add(datanum);

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }


    public void insert_NonListed_Call(String sDocType, String sDrName,String sAdd1, String sMobileNo
            ,String sRemark, String iSplId,String iSpl,String iQflId, String iQfl
            ,String iSrno, String loc, String time, String CLASS, String POTENCY_AMT, String AREA) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sDocType", sDocType);
        cv.put("sDrName", sDrName);
        cv.put("sAdd1", sAdd1);
        cv.put("sMobileNo", sMobileNo);
        cv.put("sRemark", sRemark);
        cv.put("iSplId", iSplId);
        cv.put("iSpl", iSpl);
        cv.put("iQflId", iQflId);
        cv.put("iQfl", iQfl);
        cv.put("iSrno", iSrno);
        cv.put("loc", loc);
        cv.put("time", time);
        cv.put("CLASS", CLASS);
        cv.put("POTENCY_AMT", POTENCY_AMT);
        cv.put("AREA", AREA);
        sd.insert(NonListed_call, null, cv);

    }


    public void delete_Nonlisted_calls() {
        sd = this.getWritableDatabase();
        sd.delete(NonListed_call, null, null);

    }
    public void delete_Doctor_from_local_all(String dr_id) {
        sd = this.getWritableDatabase();
        sd.delete("tempdr", "dr_id='"+dr_id+"'", null);
        sd.delete("phdcrdr_more", "dr_id='"+dr_id+"'", null);
        sd.delete("phdcrdr", "dr_id='"+dr_id+"'", null);
        sd.delete(DOCTOR_ITEM_TABLE, "dr_id='"+dr_id+"'", null);
        delete_DCR_Item(dr_id,null,null,"DR");

    }

    public void delete_Stokist_from_local_all(String dr_id) {
        sd = this.getWritableDatabase();
        sd.delete("phdcrstk", "stk_id='"+dr_id+"'", null);
        delete_DCR_Item(dr_id,null,null,"STK");

    }
    public void delete_DoctorRemainder_from_local_all(String dr_id) {
        sd = this.getWritableDatabase();
        sd.delete("phdcrdr_rc", "dr_id='"+dr_id+"'", null);

    }
    public void delete_Chemist_from_local_all(String dr_id) {
        sd = this.getWritableDatabase();
        sd.delete("phdcrchem", "chem_id='"+dr_id+"'", null);
        sd.delete("chemisttemp", "chem_id='"+dr_id+"'", null);
        delete_DCR_Item(dr_id,null,null,"CHEM");

    }

    public void insert_Mail(int mail_id, String who_id,String who_name, String date
            ,String time, String is_read,String category,String type, String subject
            ,String remark, String file_name, String file_path) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mail_id", mail_id);
        cv.put("who_id", who_id);
        cv.put("who_name", who_name);
        cv.put("date", date);
        cv.put("time", time);
        cv.put("category", category);
        cv.put("type", type);
        cv.put("subject", subject);
        cv.put("remark", remark);
        cv.put("file_name", file_name);
        cv.put("file_path", file_path);

        if(get_Mail(category,""+mail_id).size()>0){
            sd.update(Mail, cv,"mail_id="+mail_id,null);
        }else {
            cv.put("is_read", is_read);
            sd.insert(Mail, null, cv);
        }

    }

    public void update_Mail(String mail_id, String is_read) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("is_read", is_read);
        sd.update(Mail, cv,"mail_id="+mail_id,null);

    }

    public  ArrayList<Map<String, String>> get_Mail(String mail_category,String mail_id) {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        sd = this.getWritableDatabase();
        String query = "Select * from " + Mail+" where category='"+mail_category+"' order by mail_id DESC" ;
        if (!mail_id.equals("")){
            query = "Select * from " + Mail+" where mail_id='"+mail_id+"' order by mail_id DESC" ;
        }
        if (mail_category.equals("X")){
            query = "Select * from " + Mail+" where id='"+mail_id+"' order by id DESC" ;
        }
        if (mail_category.equals("d")){
            query = "Select * from " + Mail+" where category='"+mail_category+"' order by id DESC" ;
        }
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Map<String,String>datanum=new HashMap<String,String>();
                    datanum.put("from",c.getString(c.getColumnIndex("who_name")));
                    datanum.put("from_id",c.getString(c.getColumnIndex("who_id")));
                    datanum.put("time",c.getString(c.getColumnIndex("time")));
                    datanum.put("sub", c.getString(c.getColumnIndex("subject")));
                    datanum.put("date",c.getString(c.getColumnIndex("date")));
                    datanum.put("mail_id", c.getString(c.getColumnIndex("mail_id")));
                    datanum.put("id", c.getString(c.getColumnIndex("id")));
                    datanum.put("category", c.getString(c.getColumnIndex("category")));
                    String[] file_name=c.getString(c.getColumnIndex("file_path")).split(",");

                    if (file_name.length>0 && file_name[0]!=null){
                        datanum.put("FILE_NAME", file_name[0]);
                    }else{
                        datanum.put("FILE_NAME","");
                    }

                    datanum.put("REMARK", c.getString(c.getColumnIndex("remark")));
                    datanum.put("IS_READ",c.getString(c.getColumnIndex("is_read")));
                    data.add(datanum);

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }


    public String getMaxMailId(String mail_category){
        int  mail_id=0;
        Cursor c = sd.rawQuery("SELECT MAX(mail_id) as mail_id FROM "+Mail+" where category='"+mail_category+"'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mail_id=c.getInt(c.getColumnIndex("mail_id"));

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return ""+mail_id;
    }

    public int getNoOfUnreadMail(String mail_category){
        int  mail_id=0;
        Cursor c = sd.rawQuery("SELECT * FROM "+Mail+" where category='"+mail_category+"' and is_read='0'", null);
        try {
            if (c.moveToFirst()) {
                do {
                    mail_id+=1;

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return mail_id;
    }

    public void delete_Mail(String mail_id) {
        sd = this.getWritableDatabase();
        if (mail_id.equals("")) {
            sd.delete(Mail, null, null);
        }else{
            sd.delete(Mail,"id='"+mail_id+"'",null);
        }

    }



    //=============================================dcr Appraisal with table================================================================

    public void setDcrAppraisal(String PA_ID, String PA_NAME,String DR_CALL, String DR_AVG,String CHEM_CALL
            , String CHEM_AVG,String FLAG, String sAPPRAISAL_ID_STR, String sAPPRAISAL_NAME_STR
            ,String sGRADE_STR,String sGRADE_NAME_STR, String sOBSERVATION, String sACTION_TAKEN) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PA_ID", PA_ID);
        cv.put("PA_NAME", PA_NAME);
        cv.put("DR_CALL", DR_CALL);
        cv.put("DR_AVG", DR_AVG);
        cv.put("CHEM_CALL", CHEM_CALL);
        cv.put("CHEM_AVG", CHEM_AVG);
        cv.put("FLAG", FLAG);
        cv.put("sAPPRAISAL_ID_STR", sAPPRAISAL_ID_STR);
        cv.put("sAPPRAISAL_NAME_STR", sAPPRAISAL_NAME_STR);
        cv.put("sGRADE_STR", sGRADE_STR);
        cv.put("sGRADE_NAME_STR", sGRADE_NAME_STR);
        cv.put("sOBSERVATION", sOBSERVATION);
        cv.put("sACTION_TAKEN", sACTION_TAKEN);
        sd.insert(Appraisal, null, cv);

    }
    public void update_Apraisal(String pa_id,String FLAG, String sAPPRAISAL_ID_STR, String sAPPRAISAL_NAME_STR
            ,String sGRADE_STR,String sGRADE_NAME_STR, String sOBSERVATION, String sACTION_TAKEN) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FLAG", FLAG);
        cv.put("sAPPRAISAL_ID_STR", sAPPRAISAL_ID_STR);
        cv.put("sAPPRAISAL_NAME_STR", sAPPRAISAL_NAME_STR);
        cv.put("sGRADE_STR", sGRADE_STR);
        cv.put("sGRADE_NAME_STR", sGRADE_NAME_STR);
        cv.put("sOBSERVATION", sOBSERVATION);
        cv.put("sACTION_TAKEN", sACTION_TAKEN);
        sd.update(Appraisal, cv,"PA_ID="+pa_id,null);

    }

    public  ArrayList<Map<String, String>> get_Appraisal(String flag,String pa_id) {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        sd = this.getWritableDatabase();
        String query = "Select * from " + Appraisal ;
        if (!pa_id.equals("")){
            query = "Select * from " + Appraisal+" where PA_ID='"+pa_id+"'" ;
        }
        if (pa_id.equals("") && !flag.equals("")){
            query = "Select * from " + Appraisal+" where FLAG='"+flag+"'" ;
        }
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Map<String,String>datanum=new HashMap<String,String>();
                    datanum.put("PA_ID",c.getString(c.getColumnIndex("PA_ID")));
                    datanum.put("PA_NAME",c.getString(c.getColumnIndex("PA_NAME")));
                    datanum.put("DR_CALL",c.getString(c.getColumnIndex("DR_CALL")));
                    datanum.put("DR_AVG", c.getString(c.getColumnIndex("DR_AVG")));
                    datanum.put("CHEM_CALL",c.getString(c.getColumnIndex("CHEM_CALL")));
                    datanum.put("CHEM_AVG", c.getString(c.getColumnIndex("CHEM_AVG")));
                    datanum.put("FLAG", c.getString(c.getColumnIndex("FLAG")));
                    datanum.put("sAPPRAISAL_ID_STR", c.getString(c.getColumnIndex("sAPPRAISAL_ID_STR")));
                    datanum.put("sAPPRAISAL_NAME_STR",c.getString(c.getColumnIndex("sAPPRAISAL_NAME_STR")));
                    datanum.put("sGRADE_STR", c.getString(c.getColumnIndex("sGRADE_STR")));
                    datanum.put("sGRADE_NAME_STR",c.getString(c.getColumnIndex("sGRADE_NAME_STR")));
                    datanum.put("sOBSERVATION", c.getString(c.getColumnIndex("sOBSERVATION")));
                    datanum.put("sACTION_TAKEN", c.getString(c.getColumnIndex("sACTION_TAKEN")));
                    data.add(datanum);

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }

    public void deleteDcrAppraisal() {
        sd = this.getWritableDatabase();
        sd.delete(Appraisal, null, null);
    }

    ///=================================================exp head==========================================

    public void Insert_EXP_Head(String FIELD_NAME, String FIELD_ID,String MANDATORY,String DA_ACTION) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FIELD_NAME", FIELD_NAME);
        cv.put("FIELD_ID", FIELD_ID);
        cv.put("MANDATORY", MANDATORY);
        cv.put("DA_ACTION", DA_ACTION);
        sd.insert(Expenses_head, null, cv);
    }
    public void delete_EXP_Head() {
        sd = this.getWritableDatabase();
        sd.delete(Expenses_head, null, null);
    }

    public ArrayList<Map<String, String>> get_mandatory_pending_exp_head() {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        sd = this.getWritableDatabase();
        String query ="Select * from " + Expenses_head + " where MANDATORY='1' and  FIELD_ID NOT IN(SELECT exp_head_id FROM "+ Expenses+")";
        //String query = "Select * from " + Expenses_head + " LEFT JOIN "+Expenses+" ON exp_head_id=FIELD_ID where MANDATORY='1' and exp_head_id != FIELD_ID" ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Map<String,String>datanum=new HashMap<String,String>();
                    datanum.put("PA_NAME",c.getString(c.getColumnIndex("FIELD_NAME")));
                    datanum.put("PA_ID",c.getString(c.getColumnIndex("FIELD_ID")));
                    data.add(datanum);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }
    public ArrayList<Map<String, String>> get_DA_ACTION_exp_head() {
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        sd = this.getWritableDatabase();
        String query ="Select * from " + Expenses_head + " where DA_ACTION='1' and  FIELD_ID  IN(SELECT exp_head_id FROM "+ Expenses+")";
        //String query = "Select * from " + Expenses_head + " LEFT JOIN "+Expenses+" ON exp_head_id=FIELD_ID where MANDATORY='1' and exp_head_id != FIELD_ID" ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Map<String,String>datanum=new HashMap<String,String>();
                    datanum.put("PA_NAME",c.getString(c.getColumnIndex("FIELD_NAME")));
                    datanum.put("PA_ID",c.getString(c.getColumnIndex("FIELD_ID")));
                    data.add(datanum);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }

    ///================================================tenivia traker=============================================

    public void Insert_tenivia_traker(String DR_ID, String DR_NAME,String QTY
            ,String AMOUNT, String QTY_CAPTION,String ITEM_ID,String AMOUN_CAPTION, String TIME,String REMARK) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DR_ID", DR_ID);
        cv.put("DR_NAME", DR_NAME);
        cv.put("QTY", QTY);
        cv.put("AMOUNT", AMOUNT);
        cv.put("QTY_CAPTION", QTY_CAPTION);
        cv.put("ITEM_ID", ITEM_ID);
        cv.put("AMOUN_CAPTION", AMOUN_CAPTION);
        cv.put("TIME", TIME);
        cv.put("REMARK", REMARK);
        sd.insert(Tenivia_traker, null, cv);
    }

    public void delete_tenivia_traker() {
        sd = this.getWritableDatabase();
        sd.delete(Tenivia_traker, null, null);
    }


    ///=================================================exp head==========================================

    public void delete_Doctor_Call_Remark() {
        sd = this.getWritableDatabase();
        sd.delete(Doctor_Call_Remark, null, null);
    }

    public ArrayList<String> get_Doctor_Call_Remark() {
        ArrayList<String> data = new ArrayList<String>();
        sd = this.getWritableDatabase();
        String query ="Select * from " + Doctor_Call_Remark ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                   /* Map<String,String>datanum=new HashMap<String,String>();
                    datanum.put("FIELD_NAME",c.getString(c.getColumnIndex("FIELD_NAME")));
                    datanum.put("FIELD_ID",c.getString(c.getColumnIndex("FIELD_ID")));
                    //data.add(datanum);*/
                    data.add(c.getString(c.getColumnIndex("FIELD_NAME")));
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        if (data.size()==0){
            data.add("Other");
        }
        return data;
    }
    public void insertDoctorCallRemark(String item_id, String item_name) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("FIELD_NAME", item_name);
        cv.put("FIELD_ID", item_id);
        sd.insert(Doctor_Call_Remark, null, cv);
    }


    public void DropDatabase(Context context) {

        context.deleteDatabase(DATABASE_NAME);
    }


    //========================================================================================================


    public void insertLat_Long_Reg(String DCS_ID, String LAT_LONG,String DCS_TYPE, String DCS_ADD,String DCS_INDES) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DCS_ID", DCS_ID);
        cv.put("LAT_LONG", LAT_LONG);
        cv.put("DCS_TYPE", DCS_TYPE);
        cv.put("DCS_ADD", DCS_ADD);
        cv.put("DCS_INDES", DCS_INDES);
        cv.put("UPDATED", "0");
        sd.insert(Lat_Long_Reg, null, cv);
    }

    public void updatedLat_Long_Reg(String DCS_ID) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("UPDATED", "1");
        int result = sd.update(Lat_Long_Reg, cv, "DCS_ID ='" + DCS_ID+"'", null);

    }

    public void delete_Lat_Long_Reg() {
        sd = this.getWritableDatabase();
        sd.delete(Lat_Long_Reg, null, null);
    }

    public ArrayList<HashMap<String,String>>  get_Lat_Long_Reg(String updated) {
        ArrayList<HashMap<String,String>>  data = new ArrayList<HashMap<String,String>> ();
        sd = this.getWritableDatabase();
        String query ="select * from "+Lat_Long_Reg +" where updated='"+updated+"'";
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String,String> datanum=new HashMap<String,String>();
                    datanum.put("DCS_ID",c.getString(c.getColumnIndex("DCS_ID")));
                    datanum.put("LAT_LONG",c.getString(c.getColumnIndex("LAT_LONG")));
                    datanum.put("DCS_TYPE",c.getString(c.getColumnIndex("DCS_TYPE")));
                    datanum.put("DCS_ADD",c.getString(c.getColumnIndex("DCS_ADD")));
                    datanum.put("DCS_INDES",c.getString(c.getColumnIndex("DCS_INDES")));
                    data.add(datanum);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }


    ///------------------------------------------------dairy tables----------------------------------------------
//    String DAIRY_TABLE = "CREATE TABLE " + PH_DAIRY+ " ( id integer primary key,DAIRY_ID integer,DAIRY_NAME text,DOC_TYPE text,LAST_VISIT_DATE text,DR_LAT_LONG text,DR_LAT_LONG2 text,DR_LAT_LONG3 text)";
//    String DAIRY_PERSON_TABLE = "CREATE TABLE " + PH_DAIRY_PERSON+ " ( id integer primary key,DAIRY_ID integer,PERSON_ID integer,PERSON_NAME text)";
//    String DAIRY_REASON_TABLE = "CREATE TABLE " + PH_DAIRY_REASON+ " ( id integer primary key,PA_ID integer,PA_NAME text)";


    public long insert_phdairy(int DAIRY_ID, String DAIRY_NAME, String DOC_TYPE, String LAST_VISIT_DATE, String DR_LAT_LONG,String DR_LAT_LONG2,String DR_LAT_LONG3) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DAIRY_ID", DAIRY_ID);
        cv.put("DAIRY_NAME", DAIRY_NAME);
        cv.put("DOC_TYPE", DOC_TYPE);
        cv.put("LAST_VISIT_DATE", LAST_VISIT_DATE);
        cv.put("DR_LAT_LONG", DR_LAT_LONG);
        cv.put("DR_LAT_LONG2", DR_LAT_LONG2);
        cv.put("DR_LAT_LONG3", DR_LAT_LONG3);
        long l= sd.insert(PH_DAIRY, null, cv);
        return l;
    }


    public Cursor getphdairy(String DOC_TYPE) {
        sd = this.getWritableDatabase();
        return sd.rawQuery("select PHDAIRY.DAIRY_ID,PHDAIRY.DAIRY_NAME,PHDAIRY.LAST_VISIT_DATE,DR_LAT_LONG,DR_LAT_LONG2,DR_LAT_LONG3, CASE WHEN IFNull(PHDAIRY_DCR.DAIRY_ID,0) >0 THEN 1 ELSE 0 END AS CALLYN from PHDAIRY LEFT OUTER JOIN PHDAIRY_DCR  ON PHDAIRY.DAIRY_ID=PHDAIRY_DCR.DAIRY_ID  where PHDAIRY.DOC_TYPE='"+DOC_TYPE+"'" , null);
    }

    public ArrayList<HashMap<String,String>>  get_phdairy(String DOC_TYPE) {
        ArrayList<HashMap<String,String>>  data = new ArrayList<HashMap<String,String>> ();
        sd = this.getWritableDatabase();
        String query = "select PHDAIRY.DAIRY_ID,PHDAIRY.DAIRY_NAME,PHDAIRY.LAST_VISIT_DATE,DR_LAT_LONG,DR_LAT_LONG2,DR_LAT_LONG3, CASE WHEN IFNull(PHDAIRY_DCR.DAIRY_ID,0) >0 THEN 1 ELSE 0 END AS CALLYN from PHDAIRY LEFT OUTER JOIN PHDAIRY_DCR  ON PHDAIRY.DAIRY_ID=PHDAIRY_DCR.DAIRY_ID  where PHDAIRY.DOC_TYPE='"+DOC_TYPE+"'" ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String,String> datanum=new HashMap<String,String>();
                    datanum.put("DAIRY_ID",c.getString(c.getColumnIndex("DAIRY_ID")));
                    datanum.put("DAIRY_NAME",c.getString(c.getColumnIndex("DAIRY_NAME")));
                    datanum.put("LAST_VISIT_DATE",c.getString(c.getColumnIndex("LAST_VISIT_DATE")));
                    datanum.put("DR_LAT_LONG",c.getString(c.getColumnIndex("DR_LAT_LONG")));
                    datanum.put("DR_LAT_LONG2",c.getString(c.getColumnIndex("DR_LAT_LONG2")));
                    datanum.put("DR_LAT_LONG3",c.getString(c.getColumnIndex("DR_LAT_LONG3")));
                    data.add(datanum);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }


    public void delete_phdairy() {
        sd = this.getWritableDatabase();
        sd.delete(PH_DAIRY, null, null);
    }


    public long insert_phdairy_person(int DAIRY_ID, int PERSON_ID, String PERSON_NAME) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DAIRY_ID", DAIRY_ID);
        cv.put("PERSON_ID", PERSON_ID);
        cv.put("PERSON_NAME", PERSON_NAME);
        long l= sd.insert(PH_DAIRY_PERSON, null, cv);
        return l;
    }

    public Cursor get_phdairy_person(String DAIRY_ID) {
        ArrayList<HashMap<String,String>>  data = new ArrayList<HashMap<String,String>> ();
        sd = this.getWritableDatabase();
        String query ="select * from "+PH_DAIRY_PERSON +" where DAIRY_ID='"+DAIRY_ID+"'";
        return sd.rawQuery(query, null);
    }

    public void delete_phdairy_person() {
        sd = this.getWritableDatabase();
        sd.delete(PH_DAIRY_PERSON, null, null);
    }

    public long insert_phdairy_reason( int PA_ID, String PA_NAME) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PA_ID", PA_ID);
        cv.put("PA_NAME", PA_NAME);
        long l= sd.insert(PH_DAIRY_REASON, null, cv);
        return l;
    }

    public ArrayList<String> get_phdairy_reason() {
        ArrayList<String> data = new ArrayList<String>();
        sd = this.getWritableDatabase();
        String query ="Select * from " + PH_DAIRY_REASON ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    data.add(c.getString(c.getColumnIndex("PA_NAME")));
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        if (data.size()==0){
            data.add("Other");
        }
        return data;
    }


    public void delete_phdairy_reason() {
        sd = this.getWritableDatabase();
        sd.delete(PH_DAIRY_REASON, null, null);
    }

//     String DAIRY_DCR_TABLE = "CREATE TABLE "+PH_DAIRY_DCR +" ( id integer primary key,DAIRY_ID text,DOC_TYPE text,DAIRY_NAME text,
// visit_time text,batteryLevel text,dr_latLong text,dr_address text,dr_remark text,updated text,dr_km text,srno text,
// person_name text,person_id text,pob_amt text,allitemid text,allitemqty text,sample text,allgiftid text,allgiftqty text,file text,LOC_EXTRA text)";

    public long insert_phdairy_dcr(String DAIRY_ID, String DAIRY_NAME, String DOC_TYPE, String visit_time, String DR_LAT_LONG,
                                   String batteryLevel,String dr_address,
                                   String dr_remark, String dr_km, String srno, String person_name,
                                   String person_id,String pob_amt,
                                   String allitemid, String allitemqty, String sample, String allgiftid, String allgiftqty,
                                   String file,String LOC_EXTRA,String IS_INTRESTED,String Ref_latlong) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DAIRY_ID", DAIRY_ID);
        cv.put("DAIRY_NAME", DAIRY_NAME);
        cv.put("DOC_TYPE", DOC_TYPE);
        cv.put("visit_time", visit_time);
        cv.put("dr_latLong", DR_LAT_LONG);
        cv.put("batteryLevel", batteryLevel);
        cv.put("dr_address", dr_address);

        cv.put("dr_remark", dr_remark);
        cv.put("updated", "0");
        cv.put("dr_km", dr_km);
        cv.put("srno", srno);
        cv.put("person_name", person_name);
        cv.put("person_id", person_id);
        cv.put("pob_amt", pob_amt);

        cv.put("allitemid", allitemid);
        cv.put("allitemqty", allitemqty);
        cv.put("sample", sample);
        cv.put("allgiftid", allgiftid);
        cv.put("allgiftqty", allgiftqty);
        cv.put("file", file);
        cv.put("LOC_EXTRA", LOC_EXTRA);
        cv.put("IS_INTRESTED", IS_INTRESTED);
        cv.put("Ref_latlong", Ref_latlong);


        delete_DCR_Item(DAIRY_ID,null,null,"DAIRY");
        insert_DCR_Item(DAIRY_ID,allitemid,sample,"SAMPLE","DAIRY");
        insert_DCR_Item(DAIRY_ID,allgiftid,allgiftqty,"GIFT","DAIRY");


        return sd.insert(PH_DAIRY_DCR, null, cv);
    }


    public long update_phdairy_dcr(String DAIRY_ID, String DAIRY_NAME, String DOC_TYPE,
                                   String dr_remark, String person_name,
                                   String person_id,String pob_amt,
                                   String allitemid, String allitemqty, String sample, String allgiftid, String allgiftqty,
                                   String file,String IS_INTRESTED) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DAIRY_ID", DAIRY_ID);
        cv.put("DAIRY_NAME", DAIRY_NAME);
        cv.put("DOC_TYPE", DOC_TYPE);
        //cv.put("visit_time", visit_time);
        //cv.put("dr_latLong", DR_LAT_LONG);
        //cv.put("batteryLevel", batteryLevel);
        //cv.put("dr_address", dr_address);

        cv.put("dr_remark", dr_remark);
        cv.put("updated", "0");
        //cv.put("dr_km", dr_km);
       // cv.put("srno", srno);
        if(!person_id.equals("")) {
            cv.put("person_name", person_name);
            cv.put("person_id", person_id);
        }

        if(!allitemid.equals("") || !IS_INTRESTED.equals("1")) {
            cv.put("pob_amt", pob_amt);

            cv.put("allitemid", allitemid);
            cv.put("allitemqty", allitemqty);
            cv.put("sample", sample);
        }
        if(!allgiftid.equals("") || !IS_INTRESTED.equals("1")) {
            cv.put("allgiftid", allgiftid);
            cv.put("allgiftqty", allgiftqty);
        }
        cv.put("file", file);
        cv.put("IS_INTRESTED", IS_INTRESTED);
        //cv.put("LOC_EXTRA", LOC_EXTRA);


        delete_DCR_Item(DAIRY_ID,null,null,"DAIRY");
        insert_DCR_Item(DAIRY_ID,allitemid,sample,"SAMPLE","DAIRY");
        insert_DCR_Item(DAIRY_ID,allgiftid,allgiftqty,"GIFT","DAIRY");

        return sd.update(PH_DAIRY_DCR, cv, "DAIRY_ID ='" + DAIRY_ID+"'", null);
    }

    public void updatedphdairy_dcr(String DAIRY_ID) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("UPDATED", "1");
        int result = sd.update(PH_DAIRY_DCR, cv, "DAIRY_ID ='" + DAIRY_ID+"'", null);

    }

    public ArrayList<HashMap<String,String>>  get_phdairy_dcr(String updated) {
        ArrayList<HashMap<String,String>>  data = new ArrayList<HashMap<String,String>> ();
        sd = this.getWritableDatabase();

        String query ="";
        if (updated==null) {
            query ="select * from "+PH_DAIRY_DCR ;
        }else{
            query ="select * from "+PH_DAIRY_DCR +" where UPDATED='"+updated+"'";
        }

        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String,String> datanum=new HashMap<String,String>();
                    datanum.put("DAIRY_ID",c.getString(c.getColumnIndex("DAIRY_ID")));
                    datanum.put("DAIRY_NAME",c.getString(c.getColumnIndex("DAIRY_NAME")));
                    datanum.put("DOC_TYPE",c.getString(c.getColumnIndex("DOC_TYPE")));
                    datanum.put("visit_time",c.getString(c.getColumnIndex("visit_time")));
                    datanum.put("dr_latLong",c.getString(c.getColumnIndex("dr_latLong")));

                    datanum.put("batteryLevel",c.getString(c.getColumnIndex("batteryLevel")));
                    datanum.put("dr_address",c.getString(c.getColumnIndex("dr_address")));
                    datanum.put("dr_remark",c.getString(c.getColumnIndex("dr_remark")));
                    datanum.put("updated",c.getString(c.getColumnIndex("updated")));
                    datanum.put("dr_km",c.getString(c.getColumnIndex("dr_km")));
                    datanum.put("srno",c.getString(c.getColumnIndex("srno")));

                    datanum.put("person_name",c.getString(c.getColumnIndex("person_name")));
                    datanum.put("person_id",c.getString(c.getColumnIndex("person_id")));
                    datanum.put("pob_amt",c.getString(c.getColumnIndex("pob_amt")));
                    datanum.put("allitemid",c.getString(c.getColumnIndex("allitemid")));
                    datanum.put("allitemqty",c.getString(c.getColumnIndex("allitemqty")));

                    datanum.put("sample",c.getString(c.getColumnIndex("sample")));
                    datanum.put("allgiftid",c.getString(c.getColumnIndex("allgiftid")));
                    datanum.put("allgiftqty",c.getString(c.getColumnIndex("allgiftqty")));
                    datanum.put("file",c.getString(c.getColumnIndex("file")));
                    datanum.put("LOC_EXTRA",c.getString(c.getColumnIndex("LOC_EXTRA")));
                    datanum.put("IS_INTRESTED",c.getString(c.getColumnIndex("IS_INTRESTED")));
                    datanum.put("Ref_latlong",c.getString(c.getColumnIndex("Ref_latlong")));
                    data.add(datanum);
                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }
        return data;
    }

    public int getCountphdairy_dcr(String DOC_TYPE) {
        sd = this.getWritableDatabase();

        int result =0;
        String query = "SELECT COUNT(*) AS c from " + PH_DAIRY_DCR +" where DOC_TYPE ='" + DOC_TYPE+ "'" ;
        Cursor c = sd.rawQuery(query, null);
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String,String> datanum=new HashMap<String,String>();
                    result = Integer.valueOf(c.getString(c.getColumnIndex("c")));

                } while (c.moveToNext());
            }
        }finally {
            c.close();
        }

        return  result;
    }

    public void delete_phdairy_dcr(String DAIRY_ID) {
        sd = this.getWritableDatabase();
        if (DAIRY_ID==null) {
            sd.delete(PH_DAIRY_DCR, null, null);
            delete_DCR_Item(null,null,null,"DAIRY");
        }else{
            sd.delete(PH_DAIRY_DCR, "DAIRY_ID ='"+DAIRY_ID+"'", null);
            delete_DCR_Item(DAIRY_ID,null,null,"DAIRY");
        }

    }


    public long insert_Item_Stock( String ITEM_ID, int STOCK_QTY) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("STOCK_QTY", STOCK_QTY);
        cv.put("ITEM_ID", ITEM_ID);
        long l= sd.insert(PH_ITEM_STOCK_DCR, null, cv);
        return l;
    }

    public void delete_Item_Stock() {
        sd = this.getWritableDatabase();
        sd.delete(PH_ITEM_STOCK_DCR, null, null);
    }

    private String GetStock(){
        String Qry = "";
        Qry = "Select "+ ColSplit("allitemid",",") +" as ITEM_ID" + ColSplit("allitemqty",",") +" as QTY from "+PH_DAIRY_DCR;
        return Qry;
    }


    public long insert_STk_Item (String STK_ID,String ITEM_ID, String RATE) {
        sd = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("STK_ID", STK_ID);
        cv.put("ITEM_ID", ITEM_ID);
        cv.put("RATE", RATE);
        long l= sd.insert(PH_STK_ITEM_RATE, null, cv);
        return l;
    }

    public void delete_STk_Item() {
        sd = this.getWritableDatabase();
        sd.delete(PH_STK_ITEM_RATE, null, null);
    }


    public void delete_DCR_Item(String ID,String item_id,String ItemType,String Category) {
        sd = this.getWritableDatabase();
        String whereClause = "";

        if(ID != null){
            whereClause =  "CategoryID = '" + ID + "'";
        }
        if(ItemType != null){
            if(!whereClause.isEmpty()){
                whereClause = whereClause + " AND ";
            }
            whereClause =  whereClause +  "Category = '" + Category + "'";
        }
        if(item_id != null){
            if(!whereClause.isEmpty()){
                whereClause = whereClause + " AND ";
            }
            whereClause =  whereClause +  "ITEM_ID = '" + item_id + "'";
        }

        if(ItemType != null){
            if(!whereClause.isEmpty()){
                whereClause = whereClause + " AND ";
            }
            whereClause =  whereClause + "ItemType = '" + ItemType + "'";
        }
        sd.delete(PH_DCR_ITEM, whereClause, null);
    }


    public void insert_DCR_Item(String ID, String ArrITEM_ID, String ArrQTY,String ItemType, String Category) {
        sd = this.getWritableDatabase();

        String item_id[] = ArrITEM_ID.split(",");
        String Qty[] = ArrQTY.split(",");

        for( int i = 0;i< item_id.length;i++) {
            ContentValues cv = new ContentValues();
            cv.put("CategoryID", ID);
            cv.put("QTY", Qty[i]);
            cv.put("ITEM_ID", item_id[i]);
            cv.put("ItemType", ItemType);
            cv.put("Category", Category);
            sd.insert(PH_DCR_ITEM, null, cv);
        }
    }

    private String ColSplit(String Str,String with){
       return  "(WITH RECURSIVE split( col, rest) AS (" +
        "SELECT '', '"+Str+"' || '"+ with +"'" +
        "UNION ALL " +
        "SELECT " +
        "substr(rest, 0, instr(rest, '"+ with +"')), "+
        "substr(rest, instr(rest, '"+ with +"')+1) " +
        "FROM split "+
        "WHERE rest <> '') " +
        "SELECT col "+
        "FROM split "+
        "WHERE col <> '';)";
    }


//    select t.col item_id,t1.col stock from (WITH RECURSIVE split( id,col, rest) AS (
//    SELECT 0,'', '123,456,678,789,121,2' || ','
//    UNION ALL
//    SELECT  id+1,
//    substr(rest, 0, instr(rest, ',')),
//    substr(rest, instr(rest, ',')+1)
//    FROM split
//    WHERE rest <> '')
//    SELECT id,col
//    FROM split
//    WHERE col <> '')T
//    left join (WITH RECURSIVE split( id,col, rest) AS (
//    SELECT 0,'', '1,55,1,1,6' || ','
//    UNION ALL
//    SELECT  id+1,
//    substr(rest, 0, instr(rest, ',')),
//    substr(rest, instr(rest, ',')+1)
//    FROM split
//    WHERE rest <> '')
//    SELECT id,col
//    FROM split
//    WHERE col <> '')T1
//    on T.id= T1.id






//    select  * ,  ifnull( t.qty ,'0') as new_qty from phitem
//    left join
//            (WITH RECURSIVE split( id,id_rest,col, rest) AS (
//    SELECT '', '123,843,678,789,121,2' || ',' ,'', '1,55,1,1,6,1' || ','
//    UNION ALL
//    SELECT '', '123,843,678,789,121,2' || ',' ,'', '1,55,1,1,6,1' || ','
//    UNION ALL
//    SELECT   substr(id_rest, 0, instr(id_rest, ',')),
//    substr(id_rest, instr(id_rest, ',')+1),
//    substr(rest, 0, instr(rest, ',')),
//    substr(rest, instr(rest, ',')+1)
//    FROM split
//    WHERE rest <> '')
//    SELECT id,sum(col) as qty
//    FROM split
//    WHERE col <> ''  group by id) T on T.id= phitem.item_id
}




