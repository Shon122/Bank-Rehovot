package com.gtappdevelopers.bankrehovot;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoAll {
    public Long currentTime;
    public String news;
    public String[] apiList;
    public int apiIndex;
    public String[] allNames;
    public StockModel[] stockModels;
    int stockModelIndex;
    String apiLink;
    private OkHttpClient okHttpClient;
    private FirebaseFirestore db;
    Map<String, Object> docData;
    public Context mContext;
    String result;
    int iNames;
    String timeInterval;
    String currentStockName;
    StockModel saveCurrentStockModel;
    String allStockInfoStringFirebase;
    public ArrayList<User> users;

    /////////////////////////////////////////////////
    public InfoAll(Context context) throws ParseException {
        users = new ArrayList<>();
        allStockInfoStringFirebase = "";
        timeInterval = "1min";
        result = "";
        iNames = 0;
        stockModelIndex = 0;
        okHttpClient = new OkHttpClient();
        mContext = context;
        db = FirebaseFirestore.getInstance();
        docData = new HashMap<>();
        updateApiIndexFirebase();
        updateAllStockInfoFirebase();

        allNames = new String[]{

                "ABNB", "ADBE", "ADI", "ADP", "AEP", "ALGN", "AMD", "AMGN",
//                "AMZN", "AAPL", "ATVI", "AUDNOK", "AUDPLN", "BNBUSD", "BTCUSD",
//                "CADBRL", "CADZAR", "CHFJPY", "ETHUSD", "EURBRL", "EURUSD", "GILD",
//                "GOOGL", "IBM", "INTC", "ILSUSD", "KO", "LTCUSD", "META",
//                "MMM", "MSFT", "NKE", "NFLX", "NZDCZK", "NZDTRY", "PYPL", "PLNILS",
//                "SOLUSD", "TSLA", "TRYDKK", "USDJPY", "WMT", "XRPUSD"
        };


        currentTime = System.currentTimeMillis();
        apiList = new String[]{"c42711901b00e79841bb71702345719e",
                "262483bd904a81b091b2e27cbcfc0655",
                "5e4573cba51e730e43abbfdf9ed9b975",
                "cccd134cea3374b1ec72c38c08c0b0b0",
                "5aa107ade26d9f4076b8d60f0020d49b",
                "19ba45233fe86671685bdf936a24b931",
                "f44e40de3e5c0c5b6ec60df0730c10d6",
                "21161f524ff705577169d62f61047ff7",
                "c0c89ef92565d4cf7c7ffa0a013e7313",
                "f3366d9120bda80407791f48106ec000",
                "f5eca2647dfd717ee3c6541b48950600",
                "f47d87d5284e9b73dfe85379526ba0c9",
                "9a6a270b61f40c0e58d160cbb1c57131",
                "02d49e539ff86d6fa9aa0f549efc93a3",
                "b050b1fd76d5fb561c1fa00deeeea4d5",
                "8d30c21d048073535bd26cefff977b0c",
                "e3d886b55e36cf01a11c0c15b62353eb",
                "8902fd4b5be3cdf6f457d9404d52c16a",
                "07fef516f7504f5b2781c5c58b75a63d",
                "af6063b9faed6beb8bb0fec11951feaa",
                "03e7915a807c174eabdc070225bd7997",
                "97a895c7e212f394201ce0b775894703",
                "f46c28ada7aa5eb18007371f7c19bd41",
                "1abbcbbb11ed8fbb27bc3d71e698b76d",
                "b384bd91fb7fcb3a9657beac393cc9db",
                "7ddd34443e97cc9ad5d4e6fe6d2d5502",
                "c5492f9f3334045292514ace4409bf35",
                "9826c9f4968f4726aff2e843db493424",
                "2ccc83581cab1580034bd1d43219f421",
                "36c1d526b5750ae07a2de23109bedcda",
                "dda76b57b63a5a86e18a5e94e619a370",
                "0f569e49ec24d01e6abef9bd7f3aa3d2",
                "9b5236ded34b6ec08e2baf996f2e2604"


        };
        apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/1min/BTCUSD?apikey=" + apiList[apiIndex];
        stockModels = new StockModel[allNames.length];
        for (int i = 0; i < stockModels.length; i++) {
            stockModels[i] = new StockModel(allNames[i], new ArrayList<>(), new ArrayList<>(), "none");
        }


    }

    public User updateInfoSingleUser(User u1) {
        //updates balance and trades

        return u1;
    }


    public void updateUsersFirebase() {
        //take the current users from firebase and put it the variable "users"
        db.collection("Trades").document("Users").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("allUsers"));
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("infoUsers", uploaderTaker);
                    myEdit.apply();
                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String combinedString = sharedPreferences.getString("infoUsers", "");
        //now extract string to the users variable
        String[] split = combinedString.split(",");
        int index = 0;
        ArrayList<User> users1 = new ArrayList<>();
        while (index < split.length) {
            String password = split[index++];
            String username = split[index++];
            Double balance = Double.parseDouble(split[index++]);
            ArrayList<Trade> tradesList = new ArrayList<>();
            while (index < split.length) {
                String date = split[index++];
                String stockName = split[index++];
                Boolean longShort = Boolean.parseBoolean(split[index++]);
                Double startPrice = Double.parseDouble(split[index++]);
                Double currentPrice = Double.parseDouble(split[index++]);
                Double amountInvested = Double.parseDouble(split[index++]);
                Double stopLoss = Double.parseDouble(split[index++]);
                Boolean openClose = Boolean.parseBoolean(split[index++]);
                Double limitProfit = Double.parseDouble(split[index++]);
                Double totalProfitLoss = Double.parseDouble(split[index++]);
                Double percentProfitLoss = Double.parseDouble(split[index++]);
                long updateTime = Long.parseLong(split[index++]);
                Trade trade = new Trade(date, stockName, startPrice, currentPrice, amountInvested, stopLoss, limitProfit, longShort);
                trade.totalProfitLoss = totalProfitLoss;
                trade.percentProfitLoss = percentProfitLoss;
                trade.updateTime = updateTime;
                trade.openClose = openClose;
                tradesList.add(trade);
            }
            User user = new User(password, username, tradesList, balance);
            users1.add(user);
        }
        this.users = users1;

        //here update all user data trades
        for (int i = 0; i < users.size(); i++) {
            users.set(i, updateInfoSingleUser(users.get(i)));

        }

    }

    public void uploadUsersFirebase() {
        //uploads the current arraylist of users variable to firebase
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.password).append(",");
            sb.append(user.username).append(",");
            sb.append(user.balance).append(",");
            for (Trade trade : user.trades) {
                sb.append(trade.date).append(",");
                sb.append(trade.stockName).append(",");
                sb.append(trade.longShort).append(",");
                sb.append(trade.startPrice).append(",");
                sb.append(trade.currentPrice).append(",");
                sb.append(trade.amountInvested).append(",");
                sb.append(trade.stopLoss).append(",");
                sb.append(trade.openClose).append(",");
                sb.append(trade.limitProfit).append(",");
                sb.append(trade.totalProfitLoss).append(",");
                sb.append(trade.percentProfitLoss).append(",");
                sb.append(System.currentTimeMillis()).append(",");
            }
        }
        String finalinfo = sb.toString();
        //now upload to firebase

        docData.put("allUsers", users);
        db.collection("Trades").document("Users").set(docData, SetOptions.merge());


    }


    public void updateAllStockInfoFirebase() {
        db.collection("Trades").document("stockInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("infoString"));
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("infoString41", uploaderTaker);
                    myEdit.apply();
                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        allStockInfoStringFirebase = sharedPreferences.getString("infoString41", "");
    }

    public void updateApiIndexFirebase() {
        db.collection("Trades").document("indexapi").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String uploaderTaker = String.valueOf(document.get("indexnumber"));
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putInt("dataIndexApi", Integer.parseInt((uploaderTaker)));
                    myEdit.apply();
                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        apiIndex = sharedPreferences.getInt("dataIndexApi", 0);
    }

    public void extractAllStockFirebaseString() {
        //before calling this method we need to update string in a different method
        String take = allStockInfoStringFirebase;
//        take = take.replaceAll(""+(";;"+ (char) 91), ";;");
//        take = take.replaceAll(""+((char) 93 +";;"), ";;");

        if (take.equals(""))
            return;


        //extract all and then return specific
        String[] bigStrings = take.split("><");
        for (int i = 0; i < bigStrings.length; i++) {
            String[] littleString = bigStrings[i].split(";;");


            //updateTime,name,timeInterval,priceList,dateList,analysis,gainLossPercent thats the order of strings
            long updateTime = Long.parseLong(littleString[0]);
            String name = littleString[1];
            String timeInterval2 = littleString[2];
            ArrayList<Double> priceList = new ArrayList<Double>();
            ArrayList<String> dateList = new ArrayList<>();
            if (take.contains(",")) {
                for (String str : littleString[3].split(","))
                    priceList.add(Double.parseDouble(str));
                dateList = new ArrayList<>(Arrays.asList(littleString[4].split(",")));
            } else {
                priceList.add(Double.parseDouble(littleString[3]));
                dateList.add(littleString[4]);
            }
            String analysis = littleString[5];
            Double gainLossPercent = Double.valueOf(littleString[6]);


            int tempindex = 0;
            for (int j = 0; j < stockModels.length; j++) {
                if (stockModels[j].name.equals(name))
                    tempindex = j;
            }
            stockModels[tempindex] = new StockModel(name, priceList, dateList, timeInterval2);
            // stockModelIndex++;

        }

        // stockModelIndex = 0;
    }

    public void uploadStockModelsStringFirebase() {
        String allmodels = "";
        for (int i = 0; i < stockModels.length; i++) {
            //if (!stockModels[i].name.equals("none")) {
            //updateTime,name,timeInterval,priceList,dateList,analysis,gainLossPercent thats the order of strings
            allmodels += stockModels[i].updateTime + ";;";
            allmodels += stockModels[i].name + ";;";
            allmodels += stockModels[i].timeInterval + ";;";
//
//            String takeNow = stockModels[i].priceList.toString();
//            takeNow.replaceAll(String.valueOf((char) 93), "");
//            takeNow.replaceAll(String.valueOf((char) 91), "");
//            allmodels += takeNow + ";;";
//            takeNow = stockModels[i].dateList.toString();
//            takeNow.replaceAll(String.valueOf((char) 93), "");
//            takeNow.replaceAll(String.valueOf((char) 91), "");
//            allmodels += takeNow + ";;";

            StringBuilder sb = new StringBuilder();
            String result = sb.append(stockModels[i].priceList.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","))).toString();

            allmodels += result + ";;";

            sb = new StringBuilder();
            result = sb.append(stockModels[i].dateList.stream()
                    .collect(Collectors.joining(","))).toString();
            allmodels += result + ";;";
            // allmodels += stockModels[i].priceList+ ";;";
            // allmodels += stockModels[i].dateList + ";;";
            allmodels += stockModels[i].analysis + ";;";
            allmodels += stockModels[i].gainLossPercent;
            if (i < stockModels.length - 1)
                allmodels += "><";
            //}
        }

        docData.put("infoString", allmodels);
        db.collection("Trades").document("stockInfo").set(docData, SetOptions.merge());
    }

    public void updateAllPriceModels(String timeInterval5) {

        for (int i = 0; i < allNames.length; i++) {
            GetOnePriceModel(allNames[i], timeInterval5, false);
        }

    }

    public void GetOnePriceModel(String name1, String timeinterval1, boolean single) {
        updateApiIndexFirebase();
        updateAllStockInfoFirebase();
        timeInterval = timeinterval1;
        currentStockName = name1;
        //also make sure more than 2 minutes have passed since last update if its the same timeInterval
        // stockModelIndex = 0;
        extractAllStockFirebaseString();
        int index = 0;
        for (int i = 0; i < stockModels.length; i++) {
            if (stockModels[i].name.equals(name1))
                index = i;
        }


        //proceed only if the statment true
        if (allStockInfoStringFirebase.equals("") || !stockModels[index].timeInterval.equals(timeInterval) || (System.currentTimeMillis() - stockModels[index].updateTime) > 250000) {
            //
            if (timeInterval.equals("day")) {
                if (single)
                    apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + currentStockName + "?timeseries=1&apikey=" + apiList[apiIndex];
                else
                    apiLink = "https://financialmodelingprep.com/api/v3/historical-price-full/" + currentStockName + "?apikey=" + apiList[apiIndex];

            } else {
                if (single)//make it from the current day if boolean single is true
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -4);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = dateFormat.format(calendar.getTime());

                    apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + currentStockName + "?from=" + currentDate + "&apikey=" + apiList[apiIndex];
                } else
                    apiLink = "https://financialmodelingprep.com/api/v3/historical-chart/" + timeInterval + "/" + currentStockName + "?apikey=" + apiList[apiIndex];


            }
            GetDataTask task = new GetDataTask();
            try {
                task.execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


            apiIndex++;
            if (apiIndex >= apiList.length)
                apiIndex = 0;
            //upload api index to firebase again
            docData.put("indexnumber", apiIndex);
            db.collection("Trades").document("indexapi").set(docData, SetOptions.merge());


            String taker = allStockInfoStringFirebase;
            stockModels[index] = saveCurrentStockModel;
            uploadStockModelsStringFirebase();
            return;

        }
        uploadStockModelsStringFirebase();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiLink).build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = Objects.requireNonNull(response.body()).string();
                ArrayList<Double> priceList1 = extractPrices(responseString);
                ArrayList<String> dateList1 = extractDates(responseString, timeInterval);
                saveCurrentStockModel = new StockModel(currentStockName, priceList1, dateList1, timeInterval);
            } catch (IOException | ParseException ignored) {
            }
            return "";
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
        }
    }

    private class GetDataTaskNews extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(apiLink).build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = Objects.requireNonNull(response.body()).string();
                //after i get string upload to firebase
                responseString = responseString.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                responseString = responseString.replaceAll("\\r\\n|\\r|\\n", " ");
                String temp = String.valueOf((char) 92);
                responseString = responseString.replace(temp + "n", "");
                //now i just filter so it will only show content
                ArrayList<String> allContent = new ArrayList<>();
                int contentIndex = responseString.indexOf((String.valueOf('"') + "content" + String.valueOf('"')), 25); // searching for " ("content")"
                while (contentIndex != -1) {
                    String takePart = responseString.substring(contentIndex + 14, (responseString.indexOf("tickers", contentIndex + 9)) - 10);
                    allContent.add(takePart);

                    contentIndex = responseString.indexOf(String.valueOf('"') + "content" + String.valueOf('"'), contentIndex + 15);
                }
                responseString = "";
                for (int i = 0; i < allContent.size(); i++) {
                    responseString += "" + allContent.get(i) + ". ";

                }
                news = responseString;
            } catch (IOException ignored) {
            }
            return "";
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
        }
    }

    public ArrayList<Double> extractPrices(String info) {

        //now extract prices
        ArrayList<Double> priceList = new ArrayList<>();
        String saveString = "";
        int count = 0;
        int tempIndex = info.indexOf("close");
        while (tempIndex > -1 && count < 71) {
            count++;
            String takeHere1 = (info.substring(tempIndex + 9, info.indexOf(',', tempIndex + 9)));
            //here make sure there is no infinite number like 37.00000000
            takeHere1 = removeInfiniteNumbers(takeHere1);
            //now convert to Double
            priceList.add(Double.valueOf(takeHere1));
            saveString += priceList.get(priceList.size() - 1);
            tempIndex = info.indexOf("close", tempIndex + 1);
            if (tempIndex != -1)
                saveString += ",";
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        return priceList;
    }

    public ArrayList<String> extractDates(String dataTaker, String timeInterval) throws ParseException {
        String saveString = "";
        int count = 0;
        int tempIndex = 0;

        ArrayList<String> dateList = new ArrayList<>();
        saveString = ""; //!!IMPORTANT TO RESET THE SAVESTRING!!!!!!!!!!
        tempIndex = dataTaker.indexOf("date");
        while (tempIndex != -1 && count < 71) {
            count++;
            String takeDate = "";
            if (timeInterval.equals("day")) {
                takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 10);
            } else {
                takeDate = dataTaker.substring(tempIndex + 9, tempIndex + 9 + 11 + 8);
                if (!takeDate.contains("o")) {
                    SimpleDateFormat myDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    myDate.setTimeZone(TimeZone.getTimeZone("GMT-7:00"));
                    Date newDate = null;
                    newDate = myDate.parse(takeDate);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    takeDate = df.format(newDate); //the string result is like "2022-12-10"
                }
            }
            dateList.add(takeDate);
            saveString += dateList.get(dateList.size() - 1);
            tempIndex = dataTaker.indexOf("date", tempIndex + 1);
            if (tempIndex != -1)
                saveString += ",";
        }
        saveString = saveString.replaceAll("(\\r|\\n)", "");
        return dateList;
    }

    public void updateNews() {
        long lastUpdate = 0;
        //get lastUpdate data from firebase
        db.collection("Trades").document("newsAll").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();


                    String uploaderTaker = (document.get("lastNewsUpdate").toString());
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("dataLastUpdate", uploaderTaker);
                    myEdit.apply();

                }
            }
        });
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String dataTaker = sharedPreferences.getString("dataLastUpdate", "5");
        lastUpdate = Long.parseLong((dataTaker));
        int diff = (int) (currentTime - lastUpdate); // 1 min = 60000 ms
        //make sure more than 1 day has passed since last call


        if (diff > 1000000) {
            apiLink = "https://financialmodelingprep.com/api/v3/fmp/articles?page=0&size=5&apikey=" + apiList[apiIndex];
            //load(apiLink);

            GetDataTaskNews task = new GetDataTaskNews();
            try {
                task.execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            apiIndex++;
            if (apiIndex > apiList.length)
                apiIndex = 0;
//            String data = sharedPreferences.getString("data", "none");
//            //after i get string upload to firebase
//            data = data.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
//            data = data.replaceAll("\\r\\n|\\r|\\n", " ");
//            String temp = String.valueOf((char) 92);
//            data = data.replace(temp + "n", "");
//            //now i just filter so it will only show content
//            ArrayList<String> allContent = new ArrayList<>();
//            int contentIndex = data.indexOf((String.valueOf('"') + "content" + String.valueOf('"')), 25); // searching for " ("content")"
//            while (contentIndex != -1) {
//                String takePart = data.substring(contentIndex + 14, (data.indexOf("tickers", contentIndex + 9)) - 10);
//                allContent.add(takePart);
//
//                contentIndex = data.indexOf(String.valueOf('"') + "content" + String.valueOf('"'), contentIndex + 15);
//            }
//            data = "";
//            for (int i = 0; i < allContent.size(); i++) {
//                data += "" + allContent.get(i) + ". ";
//
//            }
            docData.put("lastNewsUpdate", currentTime);
            db.collection("Trades").document("newsAll").set(docData, SetOptions.merge());

            docData.put("news", news);
            db.collection("Trades").document("newsAll").set(docData, SetOptions.merge());
        } else {
            db.collection("Trades").document("newsAll").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();


                        String uploaderTaker = (document.get("news").toString());
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("dataNews", uploaderTaker);
                        myEdit.apply();

                    }
                }
            });
            String dataNewsTaker = sharedPreferences.getString("dataNews", "5");
            news = dataNewsTaker;
        }

    }

    public String removeInfiniteNumbers(String price) {
        //make sure the price is small and compact like 302.3656 and not 302.363573895
        //0.12345678 ---> 0.12345
        //1.12345678 ---> 1.1234
        //12.12345678 --->12.123
        //123.12345678 --->123.123
        //1234.12345678 --->1234.123
        //12345.12345678 --->12345.123

        //first of all cut all the zeros at the end


        int take1 = price.length();
        while (price.charAt(take1 - 1) == '0') {
            price = price.substring(0, price.length() - 1);
            take1 = price.length();
        }
        if (price.charAt(price.length() - 1) == '.') {
            price = price.substring(0, price.length() - 1);
            return price;
        }
        //now dealing with prices who are not "37.00000"

        String beforePoint = price.substring(0, price.indexOf("."));
        String afterPoint = price.substring(price.indexOf(".") + 1);


        if (afterPoint.length() > 5)
            afterPoint = afterPoint.substring(0, 5);
        String result = beforePoint + "." + afterPoint;


        return result;

    }


}
