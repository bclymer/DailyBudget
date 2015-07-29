package com.bclymer.dailybudget.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by bclymer on 10/19/2014.
 */
public class RaisedButton extends Button {

    public RaisedButton(Context context) {
        super(context);
    }

    public RaisedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RaisedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RaisedButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
