package kr.co.linsoo.particleinfomation;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by linsoo on 2017-03-24.
 */



public class OpenAPIQuery {
    final String strServiceKey = "";

    final int QueryTypeNONE                             = 0;
    final int QueryTypeGetStationNamefromTM         = 1;
    final int QueryTypeGetAirDatafromStationName        = 2;

    int m_iQueryType =QueryTypeNONE;
    resultCallback m_callback = null;
    interface resultCallback { // 인터페이스는 외부에 구현해도 상관 없습니다.
        void callbackGetAirDatafromStationName(String result);
        void callbackGetStationNamefromTM(String result);
    }

    public OpenAPIQuery( resultCallback callback){
        m_callback = callback;

    }

    public  void queryGetStationNamefromTM(double tmX, double tmY){
        try{
            m_iQueryType = QueryTypeGetStationNamefromTM;
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ strServiceKey);
            urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(Double.toString(tmX), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(Double.toString(tmY), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

            new OpenAPIThreadTask().execute(urlBuilder.toString(),null,null);
        }catch (Exception e){ Log.e("linsoo", "queryGetStationNamefromTM="+e.getMessage());}
    }

    public  void queryGetAirDatafromStationName(String stationName){
        try{
            m_iQueryType = QueryTypeGetAirDatafromStationName;
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ strServiceKey);
            urlBuilder.append("&" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataTerm","UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.1", "UTF-8"));

            new OpenAPIThreadTask().execute(urlBuilder.toString(),null,null);
        }catch (Exception e){ Log.e("linsoo", "queryGetAirDatafromStationName="+e.getMessage());}
    }

    private class OpenAPIThreadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urlString) {
            try{
                URL url = new URL(urlString[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                return sb.toString();

            }catch (Exception e){
                Log.e("linsoo", "error="+e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                if (m_callback != null){
                    switch (m_iQueryType){
                        case QueryTypeGetStationNamefromTM:  m_callback.callbackGetStationNamefromTM(result);   break;
                        case QueryTypeGetAirDatafromStationName: m_callback.callbackGetAirDatafromStationName(result);  break;
                    }
                }
            }
        }


    }
}