package kr.ac.kopo.finalproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private TabHost tabHost;
    private CalendarView calendarView;
    private ListView listViewSchedules;
    private ImageView calendarIcon;
    private RelativeLayout tabCalendar;
    private RelativeLayout tabList;
    private TextView textViewGuide;

    private long selectedDate;
    private final Map<Long, ArrayList<String>> scheduleMap = new TreeMap<>();
    private final ArrayList<String> scheduleDisplayList = new ArrayList<>();
    private ArrayAdapter<String> scheduleAdapter;

    private int selectedBgResId = R.drawable.bg_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Calendar").setIndicator("달력");
        tab1.setContent(R.id.tabCalendar);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("List").setIndicator("일정 목록");
        tab2.setContent(R.id.tabList);
        tabHost.addTab(tab2);

        calendarView = findViewById(R.id.calendarView);
        listViewSchedules = findViewById(R.id.listViewSchedules);
        calendarIcon = findViewById(R.id.calendar_icon);
        tabCalendar = findViewById(R.id.tabCalendar);
        tabList = findViewById(R.id.tabList);
        textViewGuide = findViewById(R.id.textViewGuide);

        tabCalendar.setBackgroundResource(selectedBgResId);
        tabList.setBackgroundResource(selectedBgResId);

        selectedDate = calendarView.getDate();

        scheduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleDisplayList);
        listViewSchedules.setAdapter(scheduleAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = getMillisFromDate(year, month, dayOfMonth);
            showScheduleDialog(selectedDate);
        });

        listViewSchedules.setOnItemLongClickListener((parent, view, position, id) -> {
            String selectedItem = scheduleDisplayList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("일정 삭제")
                    .setMessage("정말 삭제하시겠습니까?\n" + selectedItem)
                    .setPositiveButton("삭제", (dialog, which) -> removeSchedule(selectedItem))
                    .setNegativeButton("취소", null)
                    .show();
            return true;
        });

        calendarIcon.setOnClickListener(v -> showBackgroundSelectDialog());
    }

    private long getMillisFromDate(int year, int month, int day) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private void showScheduleDialog(long dateMillis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(formatDate(dateMillis) + " 일정 입력");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String scheduleText = input.getText().toString().trim();
            if (!scheduleText.isEmpty()) {
                saveSchedule(dateMillis, scheduleText);
            } else {
                Toast.makeText(this, "일정이 비어있습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveSchedule(long dateMillis, String text) {
        if (!scheduleMap.containsKey(dateMillis)) {
            scheduleMap.put(dateMillis, new ArrayList<>());
        }
        scheduleMap.get(dateMillis).add(text);
        updateScheduleList();
    }

    private void removeSchedule(String displayText) {
        for (Long date : scheduleMap.keySet()) {
            String datePrefix = formatDate(date) + " - ";
            if (displayText.startsWith(datePrefix)) {
                String content = displayText.replace(datePrefix, "");
                scheduleMap.get(date).remove(content);
                if (scheduleMap.get(date).isEmpty()) {
                    scheduleMap.remove(date);
                }
                break;
            }
        }
        updateScheduleList();
    }

    private void updateScheduleList() {
        scheduleDisplayList.clear();
        for (Long date : scheduleMap.keySet()) {
            for (String s : scheduleMap.get(date)) {
                scheduleDisplayList.add(formatDate(date) + " - " + s);
            }
        }
        scheduleAdapter.notifyDataSetChanged();
    }

    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        return sdf.format(new Date(millis));
    }

    private void showBackgroundSelectDialog() {
        final String[] options = {"기본 배경", "노을 배경", "ETC 배경"};
        final int[] bgResIds = {R.drawable.bg_calendar, R.drawable.bg_sunset, R.drawable.bg_night};

        new AlertDialog.Builder(this)
                .setTitle("배경 이미지 선택")
                .setItems(options, (dialog, which) -> {
                    selectedBgResId = bgResIds[which];
                    tabCalendar.setBackgroundResource(selectedBgResId);
                    tabList.setBackgroundResource(selectedBgResId);
                })
                .show();
    }
}
