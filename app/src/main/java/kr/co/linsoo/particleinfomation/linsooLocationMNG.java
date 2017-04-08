package kr.co.linsoo.particleinfomation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Locale;


public class linsooLocationMNG {

    LocationManager m_localMNG=null;
    Context m_Context = null;
    Geocoder m_geocoder = null;
    resultCallback m_callback = null;

    interface resultCallback { // 인터페이스는 외부에 구현해도 상관 없습니다.
        void callbackMethod(double latitude, double longitude, String address);
    }

    public linsooLocationMNG( Context context, resultCallback callback){
        m_callback = callback;
        m_Context = context;
        m_geocoder = new Geocoder(m_Context, Locale.getDefault());
        // LocationManager 객체를 얻어온다
        m_localMNG = (LocationManager) m_Context.getSystemService(Context.LOCATION_SERVICE);

    }
    public void EndFindLocation(){
        if(m_localMNG != null) {
            m_localMNG.removeUpdates(mLocationListener);
        }
    }

    public void StartFindLocation(){
        Log.d("linsoo","StartFindLocation");
        try{
            m_localMNG.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            m_localMNG.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);

        }catch (Exception e){
            Log.e("linsoo", "StartFindLocation = "+e.getMessage() );
        }
    }

    //---------------------------------------------------------------------------------
    //http://javaexpert.tistory.com/142 경위도 tm 변환 소스
    //위치로 주소 가져오기
    public String getAddress(double lat, double lng){
        Log.d("linsoo","getAddress");

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try{
            list = m_geocoder.getFromLocation(lat, lng, 1);
        } catch(Exception e){
            Log.e("linsoo", "getAddress error="+e.getMessage());
        }

        if(list == null){
            Log.e("linsoo", "주소 데이터 얻기 실패");
            return null;
        }

        if(list.size() > 0){
            return list.get(0).getAddressLine(0).toString();
        }
        return null;
    }

    //---------------------------------------------------------------------------------
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //위치값이 갱신되면 이벤트가 발생.
            Log.d("linsoo","onLocationChanged");
            EndFindLocation();

            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            String address = getAddress(latitude, longitude);
            if(m_callback!=null)
                m_callback.callbackMethod(latitude,longitude, address );
        }
        public void onProviderDisabled(String provider) {
            Log.d("linsoo", "onProviderDisabled, provider:" + provider);
        }
        public void onProviderEnabled(String provider) {
            Log.d("linsoo", "onProviderEnabled, provider:" + provider);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("linsoo", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
}
