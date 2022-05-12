package com.rtapps.moc;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rtapps.moc.Model.LeumiGoodysScraper;
import com.rtapps.moc.Model.OrderInfo;
import com.rtapps.moc.Model.SenderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySmsReceiver extends BroadcastReceiver {
    public static final String pdu_type = "pdus";
    private static final String TAG =
            MySmsReceiver.class.getSimpleName();

    ArrayList<String> senderIdList = new ArrayList<String>();
    List<String> msgList;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("TEXTMSG", "ONMESSAGEREc");


        // Get the SMS message.
        senderIdList.add("LeumiGoodys");
        senderIdList.add("HTZONE");
        senderIdList.add("Paisplus");
        senderIdList.add("AMEX");
        senderIdList.add("+9720526006130");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (!intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            return;


        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        //Toast.makeText(context, "onReceive "+smsMessages, Toast.LENGTH_SHORT).show();

        for (SmsMessage sms : smsMessages) {
            String phoneNumber = sms.getDisplayOriginatingAddress();
            //Toast.makeText(context, "senderId= "+phoneNumber, Toast.LENGTH_SHORT).show();
            Log.v("TEXTMSG", "ONMESSAGEREC "+phoneNumber);


            DatabaseReference senderIDRef = FirebaseDatabase.getInstance().getReference("senderID");

            senderIDRef.orderByChild("id").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    Log.v("TEXTMSG", "snaps");
                    try {
                        snapshot.getValue().equals("id");
                    }catch (Exception e){

                    }

                    if (snapshot.exists()) {
                        Log.v("TEXTMSG", "Data Exist");

                        String catalogNumber = getCatalogNumber(sms.getDisplayMessageBody());


                        if (phoneNumber.equals("LeumiGoodys") || phoneNumber.equals("+9720526006130")) {

                            if (isValidateMessage(smsMessages).contains("https://Leumigoodys.co.il/OrderSummary?k=")) {


                                LeumiGoodysScraper scraper = new LeumiGoodysScraper();
                                OrderInfo info = null;
                                try {
                                    info = scraper.scrape(getOrderIdFromSms(smsMessages));
                                    System.out.println(info.title);
                                    System.out.println(info.message);
                                    System.out.println(info.date);
                                    System.out.println(info.code);
                                } catch (IOException e) {
                                    // Catch network exceptions
                                    System.err.println("Exception: " + e);
                                    e.printStackTrace();
                                }


                                NotificationUIBuilder.getInstance(context).generateAlertNotification(
                                        context.getString(R.string.notification_msg),
                                        catalogNumber,
                                        info.title,info.code,info.date,info.message);

                            }
                        } else {



                           for(DataSnapshot dataSnapshot1:snapshot.getChildren()){

                               SenderModel senderModel=dataSnapshot1.getValue(SenderModel.class);
                            if(senderModel!=null) {
                                String wordBeforeName = senderModel.getNameFrom();
                                String wordAfterName = senderModel.getNameTo();
                                String wordBeforeCode = senderModel.getCodeWordBefore();
                                String provider = senderModel.getConstantProvider();
                                String wordBeforeDate = senderModel.getDateFrom();
                                String wordAfterDate = senderModel.getDateTo();

                                wordBeforeName=replacer(wordBeforeName);
                                wordAfterName=replacer(wordAfterName);
                                wordBeforeCode=replacer(wordBeforeCode);
                                provider=replacer(provider);
                                wordBeforeDate=replacer(wordBeforeDate);
                                wordAfterDate=replacer(wordAfterDate);


                                String receivedMsg=getFullSms(smsMessages);
                                receivedMsg=replacer(receivedMsg);
                                String wordBeforeNameWithoutSpace=removeSpace(wordBeforeName);
                                String wordAfterNameWithoutSpace=removeSpace(wordAfterName);
                                receivedMsg=receivedMsg.replace(wordBeforeName,wordBeforeNameWithoutSpace);
                                receivedMsg=receivedMsg.replace(wordAfterName,wordAfterNameWithoutSpace);
                                wordBeforeName=wordBeforeNameWithoutSpace;
                                wordAfterName=wordAfterNameWithoutSpace;


                                msgList = new ArrayList<>(Arrays.asList(receivedMsg.split(" ")));
                                String name=getName(wordBeforeName,wordAfterName);



                                receivedMsg=replacer(getFullSms(smsMessages));
                                String wordAfterDateWithoutSpace=removeSpace(wordAfterDate);
                                String wordBeforeDateWithoutSpace=removeSpace( wordBeforeDate);
                                receivedMsg=receivedMsg.replace(wordBeforeDate,wordBeforeDateWithoutSpace);
                                receivedMsg=receivedMsg.replace(wordAfterDate,wordAfterDateWithoutSpace);
                                wordBeforeDate=wordBeforeDateWithoutSpace;
                                wordAfterDate=wordAfterDateWithoutSpace;


                                Log.v("TEXTMSG", "receivedMsg "+receivedMsg);
                                Log.v("TEXTMSG", "wordBeforeDate "+wordBeforeDate);



                                msgList = new ArrayList<>(Arrays.asList(receivedMsg.split(" ")));
                                String date=getDate(wordBeforeDate,wordAfterDate);


                                receivedMsg=replacer(getFullSms(smsMessages));
                                String wordBeforeCodeWithoutSpace=removeSpace(wordBeforeCode);
                                receivedMsg=receivedMsg.replace(wordBeforeCode,wordBeforeCodeWithoutSpace);
                                wordBeforeCode=wordBeforeCodeWithoutSpace;

                                msgList = new ArrayList<>(Arrays.asList(receivedMsg.split(" ")));
                                String code=getCode(wordBeforeCode);
/*

                                String[] wordBeforeNameArray=wordBeforeName.split(" ");
                                String[] wordAfterNameArray=wordAfterName.split(" ");
                                String[]  wordBeforeDateArray=wordBeforeDate.split(" ");
                                String[] wordAfterDateArray=wordAfterDate.split(" ");
                                String[] wordBeforeCodeArray=wordBeforeCode.split(" ");

                                wordBeforeName=wordBeforeNameArray[wordBeforeNameArray.length-1];
                                wordAfterName=wordAfterNameArray[0];
                                wordBeforeDate=wordBeforeDateArray[wordBeforeDateArray.length-1];
                                wordAfterDate=wordAfterDateArray[0];
                                wordBeforeCode=wordBeforeCodeArray[wordBeforeCodeArray.length-1];
*/









                                //Toast.makeText(context, "senderId= "+phoneNumber, Toast.LENGTH_SHORT).show();

                                //Toast.makeText(context, "code= "+code, Toast.LENGTH_SHORT).show();

                                //Toast.makeText(context, "msg= "+receivedMsg, Toast.LENGTH_SHORT).show();


                                //Toast.makeText(context, "code index= "+msgList.indexOf("41967921-8417"), Toast.LENGTH_SHORT).show();


                                NotificationUIBuilder.getInstance(context).generateAlertNotification(
                                        context.getString(R.string.notification_msg),
                                    catalogNumber,
                                    name,
                                    code,date,provider);
                            }

                           }




                        }


                    } else {
//                        Toast.makeText(context,"Un-Registered Number",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.v("TEXTMSG", "Error "+error.getMessage());

                }
            });


            //My changes
            /*senderIDRef.child("id").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));

                        if (Objects.equals(task.getResult().getValue(), phoneNumber)) {
                            String catalogNumber = getCatalogNumber(sms.getDisplayMessageBody());

                            //New Changes
                            LeumiGoodysScraper scraper = new LeumiGoodysScraper();
                            OrderInfo info = null;
                            try {
                                info = scraper.scrape("RMY%2FxwMn47NM5xJVUsFO8E6kFv6xDuvTmehXve9%2BAVQ%3D");
                                System.out.println(info.title);
                                System.out.println(info.message);
                                System.out.println(info.date);
                                System.out.println(info.code);
                            } catch (IOException e) {
                                // Catch network exceptions
                                System.err.println("Exception: " + e);
                                e.printStackTrace();
                            }
                            *//*Yango Deli
                            40 ₪ הנחה למשתמשים חדשים
                            delifun53924154
                            01.10 .21*//*

                            NotificationUIBuilder.getInstance(context).generateAlertNotification(
                                    "New food detected\nClick if you want to insert",
                                    info.code + "-" + info.date,
                                    info.title + info.message);



                           *//* NotificationUIBuilder.getInstance(context).generateAlertNotification(
                                    "New food detected\nClick if you want to insert",
                                    catalogNumber,
                                    sms.getDisplayOriginatingAddress());
*//*

                        }
                    } else {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                }
            });*/
            //My changes closed

            break;
        }
    }


    private String replacer(String msg){


        msg=msg.replace(":","");
        msg=msg.replace(",","");
        msg=msg.replace(";","");
        msg=msg.replace("=","");
        msg=msg.replaceAll("\\s+"," ");
        msg=msg.replace(".","");

        return msg;

    }

    private String removeSpace(String msg){

        msg=msg.replaceAll("\\s+","");

        return msg;

    }

    private String getName(String wordBeforeName,String wordAfterName) {

        String name="";

        int wordBeforeNameIndex=msgList.indexOf(wordBeforeName);
        int wordAfterNameIndex=msgList.indexOf(wordAfterName);

        Log.v("TEXTMSG", "wordBeforeNameIndex "+wordBeforeNameIndex);
        Log.v("TEXTMSG", "wordAfterNameIndex "+wordAfterNameIndex);
        Log.v("TEXTMSG", "wordBeforeName "+wordBeforeName);
        Log.v("TEXTMSG", "wordAfterName "+wordAfterName);

        try {

            if (msgList.get(wordBeforeNameIndex + 2).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1);
                return name;
            } else if (msgList.get(wordAfterNameIndex - 2).equals(wordBeforeName)) {
                name = msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordAfterNameIndex - 3).equals(wordBeforeName)) {
                name = msgList.get(wordAfterNameIndex - 2) + " " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 3).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 4).equals(wordBeforeName)) {
                name = msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 4).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+" " + msgList.get(wordBeforeNameIndex + 3);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 5).equals(wordBeforeName)) {
                name = msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 5).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 6).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 6).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 7).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 7).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 8).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 8).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 9).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 9).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 10).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 10).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 11).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 11).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10);;
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 12).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 12).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 13).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 13).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 14).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 14).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 15).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 14) + " "+msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 15).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13)+" " + msgList.get(wordBeforeNameIndex + 14);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 16).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 15) + " "+msgList.get(wordAfterNameIndex - 14) + " "+msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 16).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13)+" " + msgList.get(wordBeforeNameIndex + 14)+" " + msgList.get(wordBeforeNameIndex + 15);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 17).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 16) + " "+msgList.get(wordAfterNameIndex - 15) + " "+msgList.get(wordAfterNameIndex - 14) + " "+msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 17).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13)+" " + msgList.get(wordBeforeNameIndex + 14)+" " + msgList.get(wordBeforeNameIndex + 15)+" " + msgList.get(wordBeforeNameIndex + 16);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 18).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 17) + " "+msgList.get(wordAfterNameIndex - 16) + " "+msgList.get(wordAfterNameIndex - 15) + " "+msgList.get(wordAfterNameIndex - 14) + " "+msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 18).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13)+" " + msgList.get(wordBeforeNameIndex + 14)+" " + msgList.get(wordBeforeNameIndex + 15)+" " + msgList.get(wordBeforeNameIndex + 16)+" " + msgList.get(wordBeforeNameIndex + 17);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 19).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 18) + " "+msgList.get(wordAfterNameIndex - 17) + " "+msgList.get(wordAfterNameIndex - 16) + " "+msgList.get(wordAfterNameIndex - 15) + " "+msgList.get(wordAfterNameIndex - 14) + " "+msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 19).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13)+" " + msgList.get(wordBeforeNameIndex + 14)+" " + msgList.get(wordBeforeNameIndex + 15)+" " + msgList.get(wordBeforeNameIndex + 16)+" " + msgList.get(wordBeforeNameIndex + 17)+" " + msgList.get(wordBeforeNameIndex + 18);
                return name;
            }
            else if (msgList.get(wordAfterNameIndex - 20).equals(wordBeforeName)) {
                name =msgList.get(wordAfterNameIndex - 19) + " "+msgList.get(wordAfterNameIndex - 18) + " "+msgList.get(wordAfterNameIndex - 17) + " "+msgList.get(wordAfterNameIndex - 16) + " "+msgList.get(wordAfterNameIndex - 15) + " "+msgList.get(wordAfterNameIndex - 14) + " "+msgList.get(wordAfterNameIndex - 13) + " "+msgList.get(wordAfterNameIndex - 12) + " "+msgList.get(wordAfterNameIndex - 11) + " "+msgList.get(wordAfterNameIndex - 10) + " "+msgList.get(wordAfterNameIndex - 9) + " "+msgList.get(wordAfterNameIndex - 8) + " "+msgList.get(wordAfterNameIndex - 7) + " "+msgList.get(wordAfterNameIndex - 6) + " "+msgList.get(wordAfterNameIndex - 5) + " "+ msgList.get(wordAfterNameIndex - 4) +  msgList.get(wordAfterNameIndex - 3) + " " + msgList.get(wordAfterNameIndex - 2)+" " + msgList.get(wordAfterNameIndex - 1);
                return name;
            } else if (msgList.get(wordBeforeNameIndex + 20).equals(wordAfterName)) {
                name = msgList.get(wordBeforeNameIndex + 1) + " " + msgList.get(wordBeforeNameIndex + 2)+msgList.get(wordBeforeNameIndex + 3) + " " + msgList.get(wordBeforeNameIndex + 4)+" " + msgList.get(wordBeforeNameIndex + 5)+" " + msgList.get(wordBeforeNameIndex + 6)+" " + msgList.get(wordBeforeNameIndex + 7)+" " + msgList.get(wordBeforeNameIndex + 8)+" " + msgList.get(wordBeforeNameIndex + 9)+" " + msgList.get(wordBeforeNameIndex + 10)+" " + msgList.get(wordBeforeNameIndex + 11)+" " + msgList.get(wordBeforeNameIndex + 12)+" " + msgList.get(wordBeforeNameIndex + 13)+" " + msgList.get(wordBeforeNameIndex + 14)+" " + msgList.get(wordBeforeNameIndex + 15)+" " + msgList.get(wordBeforeNameIndex + 16)+" " + msgList.get(wordBeforeNameIndex + 17)+" " + msgList.get(wordBeforeNameIndex + 18)+" " + msgList.get(wordBeforeNameIndex + 19);
                return name;
            }
        }
        catch (Exception e){

        }


        return name;

    }

    private String getDate(String wordBeforeDate,String wordAfterDate) {

        String date="";

        int wordBeforeDateIndex=msgList.indexOf(wordBeforeDate);
        int wordAfterDateIndex=msgList.indexOf(wordAfterDate);

        if(wordAfterDateIndex==-1){

            date=msgList.get(wordBeforeDateIndex+1);
            return date;
        }
        else
        if(msgList.get(wordBeforeDateIndex+2).equals(wordAfterDate)){
            date=msgList.get(wordBeforeDateIndex+1);
            return date;
        }
        else
        if(msgList.get(wordAfterDateIndex-2).equals(wordBeforeDate)){
            date=msgList.get(wordAfterDateIndex-1);
            return date;
        }
        if(msgList.get(wordBeforeDateIndex+3).equals(wordAfterDate)){
            date=msgList.get(wordBeforeDateIndex+1)+" "+msgList.get(wordBeforeDateIndex+2);
            return date;
        }
        else
        if(msgList.get(wordAfterDateIndex-3).equals(wordBeforeDate)){
            date=msgList.get(wordAfterDateIndex-2)+" "+msgList.get(wordAfterDateIndex-1);
            return date;
        }
        if(msgList.get(wordBeforeDateIndex+4).equals(wordAfterDate)){
            date=msgList.get(wordBeforeDateIndex+1)+" "+msgList.get(wordBeforeDateIndex+2)+" "+msgList.get(wordBeforeDateIndex+3);
            return date;
        }
        else
        if(msgList.get(wordAfterDateIndex-4).equals(wordBeforeDate)){
            date=msgList.get(wordAfterDateIndex-3)+" "+msgList.get(wordAfterDateIndex-2)+" "+msgList.get(wordAfterDateIndex-1);
            return date;
        }
        if(msgList.get(wordBeforeDateIndex+5).equals(wordAfterDate)){
            date=msgList.get(wordBeforeDateIndex+1)+" "+msgList.get(wordBeforeDateIndex+2)+" "+msgList.get(wordBeforeDateIndex+3)+" "+msgList.get(wordBeforeDateIndex+4);
            return date;
        }
        else
        if(msgList.get(wordAfterDateIndex-5).equals(wordBeforeDate)){
            date=msgList.get(wordAfterDateIndex-4)+" "+msgList.get(wordAfterDateIndex-3)+" "+msgList.get(wordAfterDateIndex-2)+" "+msgList.get(wordAfterDateIndex-1);
            return date;
        }
        if(msgList.get(wordBeforeDateIndex+6).equals(wordAfterDate)){
            date=msgList.get(wordBeforeDateIndex+1)+" "+msgList.get(wordBeforeDateIndex+2)+" "+msgList.get(wordBeforeDateIndex+3)+" "+msgList.get(wordBeforeDateIndex+4)+" "+msgList.get(wordBeforeDateIndex+5);
            return date;
        }
        else
        if(msgList.get(wordAfterDateIndex-6).equals(wordBeforeDate)){
            date=msgList.get(wordAfterDateIndex-5)+" "+msgList.get(wordAfterDateIndex-4)+" "+msgList.get(wordAfterDateIndex-3)+" "+msgList.get(wordAfterDateIndex-2)+" "+msgList.get(wordAfterDateIndex-1);
            return date;
        }


        return date;
    }

    private String getCode(String wordBeforeCode) {
        int wordBeforeCodeIndex=msgList.indexOf(wordBeforeCode);
        return msgList.get(wordBeforeCodeIndex+1);
    }

    String getOrderIdFromSms(SmsMessage[] smsMessages) {
        StringBuilder fullSms = new StringBuilder();
        String orderId = "";
        for (SmsMessage sms : smsMessages) {
            fullSms.append(sms.getDisplayMessageBody());
        }
        if (!fullSms.toString().equals("")) {
            try {
                orderId = fullSms.toString().split("=")[1];
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return orderId;
    }


    String getFullSms(SmsMessage[] smsMessages) {
        StringBuilder fullSms = new StringBuilder();

        for (SmsMessage sms : smsMessages) {
            fullSms.append(sms.getDisplayMessageBody());
        }

        return fullSms.toString();
    }

    String isValidateMessage(SmsMessage[] smsMessages) {
        StringBuilder fullSms = new StringBuilder();
        String orderId = "";
        for (SmsMessage sms : smsMessages) {
            fullSms.append(sms.getDisplayMessageBody());
        }
        return fullSms.toString();
    }


    String getCatalogNumber(String s) {
        String catalogNumber = "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        while (m.find()) {
            catalogNumber = m.group();
            break;
        }
        return catalogNumber;
    }

}
