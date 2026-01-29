package com.xoraano.deliveryboy.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Base64;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.xoraano.deliveryboy.Model.DeliveryPerson;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class MyDeliveryBoyInfoDataBase extends SQLiteAssetHelper {
    private static final String DB_NAME="MyDeliveryBoyInfo.db";
    private static final int DB_VER = 1;


    public MyDeliveryBoyInfoDataBase(Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    public DeliveryPerson getDeliveryPerson(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"name","image","mobileNo","password","pincode"};
        String sqlTable="myInfo";

        qb.setTables(sqlTable);

        Cursor c =qb.query(db,sqlSelect,null,null,null,null,null);

        DeliveryPerson deliveryPerson = new DeliveryPerson();

        if(c.moveToFirst()){

            byte[] data_cred2,data_cred1;
            String base64_cred2 = "";
            String asciiString_secondCred_11= new StringBuilder(c.getString(c.getColumnIndex("password"))).reverse().toString();
            char[] asciiString_secondCred_array_11 = asciiString_secondCred_11.toCharArray();
            char[] asciiString_secondCred_array = new char[asciiString_secondCred_array_11.length-2];
            for (int i=0;i<asciiString_secondCred_array_11.length;i++){
                if(i!=0 && i!=asciiString_secondCred_array_11.length-1){
                    asciiString_secondCred_array[i-1]=asciiString_secondCred_array_11[i];
                }
            }

            for (int i=0;i<asciiString_secondCred_array.length;i+=3){

                String x =String.valueOf(asciiString_secondCred_array[i]), y = String.valueOf(asciiString_secondCred_array[i+1]), z = String.valueOf(asciiString_secondCred_array[i+2]);
                if(!x.equals("0")){
                    base64_cred2 += (char)Integer.parseInt(x+y+z);
                }else if(!y.equals("0")){
                    base64_cred2 +=  (char)Integer.parseInt(y+z);
                }else {
                    base64_cred2 +=  (char)Integer.parseInt(z);
                }
            }

            String base64_cred1 = "";
            String asciiString_firstCred_11= new StringBuilder(c.getString(c.getColumnIndex("mobileNo"))).reverse().toString();
            char[] asciiString_firstCred_array_11 = asciiString_firstCred_11.toCharArray();
            char[] asciiString_firstCred_array = new char[asciiString_firstCred_array_11.length-2];
            for (int i=0;i<asciiString_firstCred_array_11.length;i++){
                if(i!=0 && i!=asciiString_firstCred_array_11.length-1){
                    asciiString_firstCred_array[i-1]=asciiString_firstCred_array_11[i];
                }
            }
            for (int i=0;i<asciiString_firstCred_array.length;i+=3){

                String x =String.valueOf(asciiString_firstCred_array[i]), y = String.valueOf(asciiString_firstCred_array[i+1]), z = String.valueOf(asciiString_firstCred_array[i+2]);
                if(!x.equals("0")){
                    base64_cred1 += (char)Integer.parseInt(x+y+z);
                }else if(!y.equals("0")){
                    base64_cred1 +=  (char)Integer.parseInt(y+z);
                }else {
                    base64_cred1 +=  (char)Integer.parseInt(z);
                }
            }


            data_cred2 = Base64.decode(base64_cred2, Base64.DEFAULT);
            data_cred1 = Base64.decode(base64_cred1, Base64.DEFAULT);
            String cred2 = "",cred1="";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                cred2  = new String(data_cred2, StandardCharsets.UTF_8);
                cred1  = new String(data_cred1, StandardCharsets.UTF_8);
            }else {
                cred2 = new String(data_cred2, Charset.forName("UTF-8"));
                cred1 = new String(data_cred1, Charset.forName("UTF-8"));
            }

            deliveryPerson = new DeliveryPerson(c.getString(c.getColumnIndex("name")),c.getString(c.getColumnIndex("image")),cred1,cred2,c.getString(c.getColumnIndex("pincode")));

        }

        db.close();
        return deliveryPerson;

    }

    public void saveInfo(DeliveryPerson deliveryPerson){
        SQLiteDatabase db =getWritableDatabase();

        String query;

        String firstCred,secondCred;

        byte[] data_cred2 = new byte[0];
        byte[] data_cred1 = new byte[0];

        secondCred = deliveryPerson.getPassword();
        firstCred = deliveryPerson.getMobileNo();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            data_cred2 = secondCred.getBytes(StandardCharsets.UTF_8);
            data_cred1 = firstCred.getBytes(StandardCharsets.UTF_8);

        }else {
            data_cred2 = secondCred.getBytes(Charset.forName("UTF-8"));
            data_cred1 = firstCred.getBytes(Charset.forName("UTF-8"));
        }

        String base64_secondCred = Base64.encodeToString(data_cred2, Base64.DEFAULT);

        char[] charArray_secondCred = base64_secondCred.toCharArray();
        String asciiString_secondCred="";
        for (char ch :charArray_secondCred){
            int asciiVal = (int)ch;
            if(asciiVal < 100 && asciiVal >=10){
                asciiString_secondCred += "0"+String.valueOf(asciiVal);
            }else if(asciiVal < 10){
                asciiString_secondCred += "00"+String.valueOf(asciiVal);
            }else {
                asciiString_secondCred +=String.valueOf(asciiVal);
            }
        }
        asciiString_secondCred = "1"+asciiString_secondCred+"1";
        asciiString_secondCred = new StringBuilder(asciiString_secondCred).reverse().toString();



        String base64_firstcred = Base64.encodeToString(data_cred1, Base64.DEFAULT);
        char[] charArray_firstCred = base64_firstcred.toCharArray();
        String asciiString_firstCred="";
        for (char ch :charArray_firstCred){
            int asciiVal = (int)ch;
            if(asciiVal < 100 && asciiVal >=10){
                asciiString_firstCred += "0"+String.valueOf(asciiVal);
            }else if(asciiVal < 10){
                asciiString_firstCred += "00"+String.valueOf(asciiVal);
            }else {
                asciiString_firstCred +=String.valueOf(asciiVal);
            }
        }
        asciiString_firstCred = "1"+asciiString_firstCred+"1";
        asciiString_firstCred = new StringBuilder(asciiString_firstCred).reverse().toString();

        query = String.format("INSERT INTO myInfo(name,image,mobileNo,password,pincode) VALUES('%s','%s','%s','%s','%s');",deliveryPerson.getName(),deliveryPerson.getImage(),asciiString_firstCred,asciiString_secondCred,deliveryPerson.getPincode());

        db.execSQL(query);
        db.close();
    }

    public void clearInfo(){
        SQLiteDatabase db =getWritableDatabase();
        String query = String.format("DELETE FROM myInfo");
        db.execSQL(query);
        db.close();
    }
}

