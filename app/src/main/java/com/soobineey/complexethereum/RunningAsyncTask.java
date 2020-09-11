package com.soobineey.complexethereum;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RunningAsyncTask extends AsyncTask<String, Integer, String> {
  // UI 작업을 위해 액티비티를 초기화 할 변수
  public Activity activity;

  private ProgressBar progressBar;
  public TextView textString;
  public TextView textKinds;

  // 액티비티를 초기화 하기 위한 생성자
  public RunningAsyncTask(Activity activity) {
    this.activity = activity;
  }

  // 결과 값을 담을 변수
  private String value;
  private String tokenName;

  // asyncTask 실행 전 준비 단계
  @Override
  protected void onPreExecute() {
    super.onPreExecute();

    // 프로그래스 바 생성
    progressBar = activity.findViewById(R.id.progress_circular);

    progressBar.setVisibility(View.VISIBLE);
    progressBar.setIndeterminate(true);
    progressBar.setMax(100);
  }

  // 실제 asyncTask 동작 단계
  @Override
  protected String doInBackground(String... strings) { // 파라미터는 배열
    try {
      // 통신 파트
      URL url = new URL(strings[0]);
      URLConnection urlConnection;

      urlConnection = url.openConnection();

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

        if (ckData.equals("1")) {
          String result = String.valueOf(jsonObject.get("result"));
          Log.e("result", result);

          JSONArray jsonArray = new JSONArray(result);
          JSONObject jo = jsonArray.getJSONObject(0);

          // 조회한 결과값을 각각 value와 tokenName에 담는다.
          String valueData = String.valueOf(jo.get("value"));
          value = valueData;
          String tokenNameData = String.valueOf(jo.get("tokenName"));
          tokenName = tokenNameData;
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

  // asyncTask가 종료된 단계
  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);

    // setting할 textView 생성
    textString = activity.findViewById(R.id.text_string);
    textKinds = activity.findViewById(R.id.text_kinds);

    // 조회 결과값을 확인 후 결과값이 없으면 if문, 있으면 else문 실행
    if (value == null || tokenName == null) {
      textString.setText("검색 실패");
      textKinds.setText("검색 실패");
    } else {
      textString.setText(value);
      textKinds.setText(tokenName);
    }
    // 프로그래스바 가리기
    progressBar.setVisibility(View.INVISIBLE);
  }
}