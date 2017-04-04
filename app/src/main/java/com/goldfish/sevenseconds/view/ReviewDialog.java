package com.goldfish.sevenseconds.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.goldfish.sevenseconds.R;

/**
 * Created by lenovo on 2017/4/2.
 */

public class ReviewDialog extends Dialog {

    private Context context;
    private ClickListenerInterface clickListenerInterface;
    private EditText editText;
    private String editStr;

    public ReviewDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        init();
    }

    public interface ClickListenerInterface {
        public void doConfirm();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_review, null);
        setContentView(view);

        editText = (EditText) view.findViewById(R.id.rv_dialogue_edit);
        TextView tvConfirm = (TextView) view.findViewById(R.id.rv_dialogue_confirm);

        tvConfirm.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        lp.gravity = Gravity.BOTTOM;
        lp.width = (int) (displayMetrics.widthPixels);
        lp.height = (int) (displayMetrics.heightPixels * 0.34);
        dialogWindow.setAttributes(lp);
    }

    public String getEdit() {
        if (editText.getText().toString().isEmpty()) {
            editStr = "请留下你的足迹吧";
        } else {
            editStr = editText.getText().toString();
        }
        return editStr;
    }

    public void setEditText(String text) {
        editText.setText(text);
    }

    public void setClickListenerInterface(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
            case R.id.rv_dialogue_confirm:
                clickListenerInterface.doConfirm();
                break;
            }
        }
    }

}
