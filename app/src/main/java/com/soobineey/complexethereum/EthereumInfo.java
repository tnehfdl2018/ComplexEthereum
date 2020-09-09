package com.soobineey.complexethereum;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import static com.soobineey.complexethereum.ReferData.*;

// 인터페이스 사용에서 통신하는 스레드
public class EthereumInfo extends Thread {
    private EtherInterface etherInterface;
    private String choiceValue;

    // 콜백을 위하여 콜백을 할 메소드와 조회할 코인정보 초기화
    public EthereumInfo(EtherInterface etherInterface, String choiceValue) {
        this.etherInterface = etherInterface;
        this.choiceValue = choiceValue;
    }

    // 단가와 코인 코드를 담을 변수
    public static String value;
    public static String tokenName;

    // 조회할 주소값을 담을 변수
    private String address = null;

    // 콜백 시 결과값을 담을 HashMap
    private HashMap<String, String> results = new HashMap<>();

    @Override
    public void run() {
        // 생성자에서 초기화 한 코인 명을 이용하여 주소값 결정
        Log.e("선택한 코인 명", choiceValue);
        switch (choiceValue) {
            case "BNB":
                address = bnbFullAddress;
                break;
            case "MEX":
                address = mexFullAddress;
                break;
            case "SXP":
                address = sxpFullAddress;
                break;
            case "LINK":
                address = linkFullAddress;
                break;
            case "AOA":
                address = aoaFullAddress;
                break;
            default:
                break;

        }
        try {
            Log.e("검색 주소 값 ", address);

            // 통신 파트
            URL url = new URL(address);
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            String receiveData = String.valueOf(stringBuilder);
            Log.e("json ", receiveData);

            if (receiveData != null) {
                JSONObject jsonObject = new JSONObject(receiveData);

                String ckData = String.valueOf(jsonObject.get("status"));
                Log.e("text ", ckData);

                if (ckData.equals("1")) {
                    String result = String.valueOf(jsonObject.get("result"));
                    Log.e("result", result);

                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jo = jsonArray.getJSONObject(0);

                    value = String.valueOf(jo.get("value"));
                    Log.e("value ", value);
                    tokenName = String.valueOf(jo.get("tokenName"));

                    results.put("value", value);
                    results.put("tokenName", tokenName);

                    // 조회 완료후 인터페이스 메소드 콜백(파라미터 타입 : HashMap<String, String>)
                    etherInterface.resultForSetData(results);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
