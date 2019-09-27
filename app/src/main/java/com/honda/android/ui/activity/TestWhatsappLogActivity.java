package com.honda.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.honda.android.R;
import com.honda.android.utils.PermanentLoggerUtil;

import java.util.ArrayList;
import java.util.List;

public class TestWhatsappLogActivity extends AppCompatActivity {

    public static Intent generateIntent(Context context) {
        Intent intent = new Intent(context, TestWhatsappLogActivity.class);
        return intent;
    }

    RecyclerView recyclerView;
    Button vBtnRefresh;

    private List<String> logs = new ArrayList<>();
    private LogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_whatsapp_log_activity);

        recyclerView = findViewById(R.id.recyclerView);
        vBtnRefresh = findViewById(R.id.button_refresh);

        setupRecyclerView();

        vBtnRefresh.setOnClickListener(v -> {
            refreshLogs();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logs = PermanentLoggerUtil.getLogs(this);
        logAdapter = new LogAdapter();
        recyclerView.setAdapter(logAdapter);
    }

    private void refreshLogs() {
        logs = PermanentLoggerUtil.getLogs(this);
        logAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(logAdapter.getLogs());
    }

    private class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        @Override
        public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_log_item_view, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LogViewHolder holder, int position) {
            holder.bind(logs.get(position));
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        public int getLogs() {
            return logs.size();
        }
    }

    private class LogViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public LogViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.textView);
        }

        public void bind(String text) {
            mTextView.setText(text);
        }
    }
}
