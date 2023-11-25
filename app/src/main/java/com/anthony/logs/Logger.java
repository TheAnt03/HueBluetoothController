package com.anthony.logs;

import android.content.Context;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
    private static Logger instance;

    private TextView log;


    private Logger() {

    }

    private String appendLine(String logText, String line) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        return logText + "\n[" + timeStamp + "]" + line;
    }

    public void append(String line) {
        String logText = this.log.getText().toString();
        logText = this.appendLine(logText, line);

        this.log.setText(logText);
    }

    public void initializeLogView(TextView log) {
        this.log = log;
    }

    public static Logger getInstance() {
        if(instance == null) {
            instance = new Logger();
        }

        return instance;
    }
}
