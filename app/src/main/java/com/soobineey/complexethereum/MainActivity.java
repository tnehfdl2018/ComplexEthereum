package com.soobineey.complexethereum;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import static com.soobineey.complexethereum.ReferData.*;

public class MainActivity extends AppCompatActivity {

    private String choiceValue; // 선택한 코인
    private String address; // API 검색할 주소

    public TextView textString; // 단가
    public TextView textKinds; // 코인 코드

    private ProgressBar progressBar; // 프로그래스 바

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 버튼 생성 및 클릭 이벤트 생성
        Button searchBtn = findViewById(R.id.select_btn);
        searchBtn.setOnClickListener(searchData);

        // 프로그래스 바 및 결과값이 전시될 텍스트뷰 생성
        progressBar = findViewById(R.id.progress_circular);
        textString = findViewById(R.id.text_string);
        textKinds = findViewById(R.id.text_kinds);

        // 스피너 생성
        Spinner selectCoinSpinner = findViewById(R.id.coin_spinner);
        ArrayAdapter selectCoinArrayAdapter = ArrayAdapter.createFromResource(this, R.array.coin_name, android.R.layout.simple_spinner_item);
        selectCoinArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCoinSpinner.setAdapter(selectCoinArrayAdapter);

        // 처음 앱 실행시 선택된 스피너의 기본값 저장 
        choiceValue = selectCoinSpinner.getSelectedItem().toString();
        Log.e("스피너 선택 값 ", choiceValue);

        // 스피너 변경 이벤트 생성
        selectCoinSpinner.setOnItemSelectedListener(changeValue);
    }

    // 조회 버튼 클릭
    View.OnClickListener searchData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 선택한 코인에 따라 미리 정의해둔 주소값을 address 변수에 담는다.
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
            }
            Log.e("선택한 주소 값 ", address);

            // 외부 asyncTask
            RunningAsyncTask runningAsyncTask = new RunningAsyncTask(MainActivity.this); // 스피너 설정을 위해 파라미터로 activity 전달
            runningAsyncTask.execute(address); // 조회할 주소를 파라미터로 같이 전달

            // 내부 asyncTask
//            AsyncTaskTest asyncTaskTest = new AsyncTaskTest();
//            asyncTaskTest.execute(address); // 조회할 주소를 파라미터로 같이 전달

            // 인터페이스
            // 초기값 (인터페이스 객체, 스피너 선택값)
//            EthereumInfo ethereumThread = new EthereumInfo(etherInterface, choiceValue);
//            progressBar = findViewById(R.id.progress_circular);
//
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setIndeterminate(true);
//            progressBar.setMax(100);
//            ethereumThread.start();
        }
    };

    // 스피너 이벤트
    private AdapterView.OnItemSelectedListener changeValue = new AdapterView.OnItemSelectedListener() {
        // 스피너 값 선택시 실행될 메소드
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String spinnerValue = parent.getSelectedItem().toString();
            choiceValue = spinnerValue;
            Log.e("스피너 선택 값 ", choiceValue);
        }
        // 스피너 값이 공백일 때 실행될 메소드
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.e("스피너 선택 값 ", choiceValue);
        }
    };

    // 인터페이스 메소드 정의
    // 스레드에서 조회가 완료되면 콜백되는 메소드
    private EtherInterface etherInterface = new EtherInterface() {
        // 조회한 결과 값을 전달 받을 HashMap
        HashMap<String, String> results;

        // 조회하여 가져온 데이터를 받는다.
        @Override
        public void resultForSetData(HashMap<String, String> data) {
            // 조회한 결과 값을 results에 담는다.
            results = data;
            runOnUiThread(new Runnable() { //  UI 변경을 위해 runOnUiThread 실행
                @Override
                public void run() {
                    // 조회 결과가 없으면 if 실행, 결과가 있으면 else문 실행
                    if (results.get("value") == null || results.get("tokenName") == null) {
                        results.put("value", "검색결과 없음");
                        results.put("tokenName", "검색결과 없음");
                    } else {
                        Log.e("최종 결과 ", results.get("value"));
                        Log.e("최종 결과 ", results.get("tokenName"));
                        textString.setText(results.get("value"));
                        textKinds.setText(results.get("tokenName"));

                        // 프로그래스바 끄기
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    /**
     * AsyncTask 시작
     */
    public class AsyncTaskTest extends AsyncTask<String, Integer, String> {
        // 조회 데이터를 담을 변수 선언
        private String value;
        private String tokenName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 프로그래스 바 셋팅
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setMax(100);
        }

        @Override
        protected String doInBackground(String... strings) { // 파라미터는 배열로 넘어온다.
            // 실제 조회 작업
            try {
                // 파라미터로 받아온 조회할 주소값을 URL로 생성
                URL url = new URL(strings[0]);

                // connector 오픈
                URLConnection urlConnection= url.openConnection();

                // 조회하여 가져온 정보를 읽는다.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                // 읽어온 데이터를 StringBuilder 형태로 만든다.
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                // StringBuilder값을 String으로 변환
                String receiveData = String.valueOf(stringBuilder);
                Log.e("json ", receiveData);

                // Json 형태의 데이터를 파싱한다.
                if (receiveData != null) {
                    JSONObject jsonObject = new JSONObject(receiveData);

                    // status를 확인하여 정상적으로 조회가 되었는지 확인
                    String ckData = String.valueOf(jsonObject.get("status"));
                    Log.e("text ", ckData);

                    // 정성적으로 조회가 되었다면 if문 실행, 데이터가 조회되지 않았다면 else문 실행
                    if (ckData.equals("1")) {
                        // Json 파싱
                        String result = String.valueOf(jsonObject.get("result"));
                        Log.e("result", result);

                        // array형태로 되어있는 result내의 결과 값을 꺼낼 수 있도록 파싱
                        JSONArray jsonArray = new JSONArray(result);
                        // 파싱한 데이터 중 0번째 index값만 가져온다.
                        JSONObject jo = jsonArray.getJSONObject(0);

                        // value값에 단가를 저장
                        String valueData = String.valueOf(jo.get("value"));
                        value = valueData;
                        Log.e("value ", valueData);
                        // tokenName에 코인 코드 저장
                        String tokenNameData = String.valueOf(jo.get("tokenName"));
                        tokenName = tokenNameData;
                        Log.e("tokenName ", tokenNameData);
                    } else {
                        return "검색된 데이터가 없음";
                    }
                } else {
                    return "검색된 데이터가 없음";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // doInBackground에서 통신이 끝나면 진입
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // 조회 완료 후 처리 작업(프로그래스 바를 위해 1초 sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 정상적으로 조회가 되었는지 확인
            if (value == null || tokenName == null) {
                textString.setText("검색 실패");
                textKinds.setText("검색 실패");
            } else {
                // 정상적으로 조회가 되어서 결과값이 있다면 textView에 setting한다.
                textString.setText(value);
                textKinds.setText(tokenName);
            }
            // 프로그래스바 끄기
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}