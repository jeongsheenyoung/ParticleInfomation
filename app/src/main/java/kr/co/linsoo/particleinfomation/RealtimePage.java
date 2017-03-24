package kr.co.linsoo.particleinfomation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;


/**
 * Created by linsoo on 2017-03-24.
 */

public class RealtimePage extends Fragment {

    TextView m_TextViewLog = null;
    TextView m_TextViewAddress = null;  //등록 위치 주소
    TextView m_TextViewStationName = null;  //관측소 이름
    TextView m_TextViewDataTime = null; //오염측정시각
    TextView m_TextViewPM25 = null; //pm2.5
    TextView m_TextViewPM25_24 = null; //pm2.5 (24시간)
    TextView m_TextViewPM10 = null; //pm10
    TextView m_TextViewPM10_24 = null; //pm10 (24시간)
    linsooLocationMNG llMng = null;
    OpenAPIQuery openApi = null;
    GeoPoint in_pt = new GeoPoint(0, 0);
    GeoPoint tm_pt = new GeoPoint(0, 0);

    XmlPullParserFactory factory= null;
    XmlPullParser xpp= null;

    //fragment가 만들어질 때
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }


        //   mPosition = getArguments() != null ? getArguments().getInt("position") : 0;	// 뷰페이저의 position값을  넘겨 받음
        llMng = new linsooLocationMNG(getActivity(), new linsooLocationMNG.resultCallback() {
            @Override
            public void callbackMethod(double latitude, double longitude, String address) {

                llMng.EndFindLocation();
                m_TextViewAddress.setText(address);

                in_pt.x = longitude;    //경도
                in_pt.y = latitude;     //위도
                tm_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, in_pt);

                openApi.queryGetStationNamefromTM(tm_pt.x, tm_pt.y);
            }
        });


        openApi = new OpenAPIQuery(new OpenAPIQuery.resultCallback() {
            @Override
            public void callbackGetAirDatafromStationName(String result) {
                m_TextViewLog.setText(result);
                xmlParseGetAirDatafromStationName(result);
            }

            @Override
            public void callbackGetStationNamefromTM(String result) {
                m_TextViewLog.setText(result);
                xmlParseGetStationNamefromTM(result);
            }
        });

    }

    final int PARSE_STATE_NOT_FOUND = 0;
    final int PARSE_STATE_FOUND = 1;
    final int PARSE_STATE_DONE = 2;

    public  void setTextViewBackgroundColor(TextView view, int value){
        if(value>151)
            view.setBackgroundColor(Color.rgb(236,61,61));
        else if(value>81)
            view.setBackgroundColor(Color.rgb(239,239,71));
        else if(value>31)
            view.setBackgroundColor(Color.rgb(71,227,134));
        else if(value >0)
            view.setBackgroundColor(Color.rgb(80,100,254));
    }

    public void xmlParseGetAirDatafromStationName(String data) {
        try {
            String tmpTag;
            int dataTime = PARSE_STATE_NOT_FOUND;
            int pm25 = PARSE_STATE_NOT_FOUND;
            int pm25_24= PARSE_STATE_NOT_FOUND;
            int pm10 = PARSE_STATE_NOT_FOUND;
            int pm10_24 = PARSE_STATE_NOT_FOUND;

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            int pmValue = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (dataTime == PARSE_STATE_NOT_FOUND && tmpTag.equals("dataTime"))
                            dataTime = PARSE_STATE_FOUND;
                        else if (pm25 == PARSE_STATE_NOT_FOUND && tmpTag.equals("pm25Value"))
                            pm25 = PARSE_STATE_FOUND;
                        else if (pm25_24 == PARSE_STATE_NOT_FOUND && tmpTag.equals("pm25Value24"))
                            pm25_24 = PARSE_STATE_FOUND;
                        else if (pm10 == PARSE_STATE_NOT_FOUND && tmpTag.equals("pm10Value"))
                            pm10 = PARSE_STATE_FOUND;
                        else if (pm10_24 == PARSE_STATE_NOT_FOUND && tmpTag.equals("pm10Value24"))
                            pm10_24 = PARSE_STATE_FOUND;
                        break;
                    case XmlPullParser.TEXT:
                        if(dataTime == PARSE_STATE_FOUND){
                            dataTime = PARSE_STATE_DONE;
                            m_TextViewDataTime.setText(xpp.getText());
                        }
                        else if(pm25 == PARSE_STATE_FOUND){
                            pm25 = PARSE_STATE_DONE;
                            pmValue = Integer.parseInt(xpp.getText());
                            m_TextViewPM25.setText(String.format("%d ㎍/㎥", pmValue) );
                            setTextViewBackgroundColor(m_TextViewPM25, pmValue);
                        }
                        else if(pm25_24 == PARSE_STATE_FOUND){
                            pm25_24 = PARSE_STATE_DONE;
                            pmValue = Integer.parseInt(xpp.getText());
                            m_TextViewPM25_24.setText(String.format("%d ㎍/㎥(24H)", pmValue));
                            setTextViewBackgroundColor(m_TextViewPM25_24, pmValue);
                        }
                        else if(pm10 == PARSE_STATE_FOUND){
                            pm10 = PARSE_STATE_DONE;
                            pmValue = Integer.parseInt(xpp.getText());
                            m_TextViewPM10.setText(String.format("%d ㎍/㎥", pmValue));
                            setTextViewBackgroundColor(m_TextViewPM10, pmValue);
                        }
                        else if(pm10_24 == PARSE_STATE_FOUND){
                            pm10_24 = PARSE_STATE_DONE;
                            pmValue = Integer.parseInt(xpp.getText());
                            m_TextViewPM10_24.setText(String.format("%d ㎍/㎥(24H)", pmValue));
                            setTextViewBackgroundColor(m_TextViewPM10_24, pmValue);
                        }
                        break;

                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void xmlParseGetStationNamefromTM(String data) {
        try {
            String tmpTag;
            int foundStationName = PARSE_STATE_NOT_FOUND;

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (foundStationName == PARSE_STATE_NOT_FOUND && tmpTag.equals("stationName"))
                            foundStationName = PARSE_STATE_FOUND;
                        break;
                    case XmlPullParser.TEXT:
                        if(foundStationName == PARSE_STATE_FOUND){
                            foundStationName = PARSE_STATE_DONE;
                            m_TextViewStationName.setText(xpp.getText());
                            openApi.queryGetAirDatafromStationName(xpp.getText());
                        }
                        break;

                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    static RealtimePage newInstance(int position) {
        RealtimePage f = new RealtimePage();	//객체 생성
        Bundle args = new Bundle();					//해당 fragment에서 사용될 정보 담을 번들 객체
        args.putInt("position", position);				//포지션 값을 저장
        f.setArguments(args);							//fragment에 정보 전달.
        return f;											//fragment 반환
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        m_TextViewLog = (TextView) rootView.findViewById(R.id.textViewLog);
        m_TextViewAddress =  (TextView) rootView.findViewById(R.id.textView_Address);
        m_TextViewStationName=  (TextView) rootView.findViewById(R.id.textView_StationName);

        m_TextViewDataTime=  (TextView) rootView.findViewById(R.id.textView_dataTime);
        m_TextViewPM25=  (TextView) rootView.findViewById(R.id.textView_pm25);
        m_TextViewPM25_24=  (TextView) rootView.findViewById(R.id.textView_pm25_24);
        m_TextViewPM10=  (TextView) rootView.findViewById(R.id.textView_pm10);
        m_TextViewPM10_24=  (TextView) rootView.findViewById(R.id.textView_pm10_24);




        return rootView;
    }

    public void refreshData(){
        Log.d("linsoo", "refreshData");
        try{
            llMng.StartFindLocation();
        }catch (Exception e){ Log.e("linsoo", "refreshData="+e.getMessage());      }
    }
}
