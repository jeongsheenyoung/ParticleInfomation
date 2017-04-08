package kr.co.linsoo.particleinfomation;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;



public class OpenAPIQuery {
    private final String strServiceKey = "여기에 공공데이터 포털 인증키를 입력하세요";

    private final int QueryTypeNONE = 0;
    private final int QueryTypeGetStationNamefromTM = 1;
    private final int QueryTypeGetAirDatafromStationName = 2;

    private int m_iQueryType =QueryTypeNONE;
    private resultCallback m_callback = null;

    private OpenAPIThreadTask mThreadAPI = null;

    interface resultCallback { // 인터페이스는 외부에 구현해도 상관 없습니다.
        void callbackGetAirDatafromStationName(String result);
        void callbackGetStationNamefromTM(String result);
        void callbackError(String errReport);
    }

    public OpenAPIQuery( resultCallback callback){
        m_callback = callback;
    }

    public void StopQuery(){
        Log.d("linsoo", "queryThreadStop");
        if(mThreadAPI !=null){
            mThreadAPI.cancel(true);
            mThreadAPI = null;
        }

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

            //new OpenAPIThreadTask().execute(urlBuilder.toString(),null,null);

            if(mThreadAPI == null){
                mThreadAPI = new OpenAPIThreadTask();
                mThreadAPI.execute(urlBuilder.toString(),null,null);
            }

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

            //new OpenAPIThreadTask().execute(urlBuilder.toString(),null,null);
            if(mThreadAPI == null){
                mThreadAPI = new OpenAPIThreadTask();
                mThreadAPI.execute(urlBuilder.toString(),null,null);
            }

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
                if (m_callback != null){
                    m_callback.callbackError(e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mThreadAPI = null;
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
