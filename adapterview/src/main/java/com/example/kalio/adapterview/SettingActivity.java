package com.example.kalio.adapterview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    // 1. 뷰와 그 뷰에 설정될 데이터를 연결하기 위한 배열을 정의한다.
    ListView mListView;
    String[] mDataKeyList;
    int[] mViewIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mListView = (ListView) findViewById(R.id.setting_listview);
        mListView.setOnItemClickListener(this);

        mDataKeyList = new String[]{"title", "description"};
        mViewIdList = new int[]{R.id.setting_title, R.id.setting_description};

        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> mapData = new HashMap<>();
        mapData.put("title", "신문 선택");
        mapData.put("description", "자주 구독하는 신문을 선택합니다.");
        data.add(mapData);

        mapData = new HashMap<>();
        mapData.put("title", "섹션 선택");
        mapData.put("description", "관심있는 섹션을 선택합니다.");
        data.add(mapData);

        mapData = new HashMap<>();
        mapData.put("title", "의견 보내기");
        mapData.put("description", "앱을 사용하시면서 느끼신 점을 보내주세요. 더욱 좋은 앱을 만들기 위해 노력하겠습니다.");
        data.add(mapData);

        SimpleAdapter adapter =
                new SimpleAdapter(this, data, R.layout.setting_list_item, mDataKeyList, mViewIdList);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvDescription = (TextView) view.findViewById(R.id.setting_description);
        Toast.makeText(SettingActivity.this, tvDescription.getText(), Toast.LENGTH_SHORT).show();
    }
}
