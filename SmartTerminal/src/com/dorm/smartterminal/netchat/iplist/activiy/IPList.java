/**
 * 
 */
package com.dorm.smartterminal.netchat.iplist.activiy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.dorm.smartterminal.R;
import com.dorm.smartterminal.global.util.ActivityUtil;
import com.dorm.smartterminal.netchat.activiy.NetChart;

/**
 * view to select ip from list, or enter an new ip to call directly.
 * 
 * @author Andy
 * 
 */
public class IPList extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.st__netchart__ip_list);

        /*
         * ui
         */

        initButtons();

    }

    private void initButtons() {

        findViewById(R.id.call).setOnClickListener(this);

    }

    /*
     * logic
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.call:
            Intent intent = new Intent(this, NetChart.class);
            intent.putExtra("ip", ((EditText) findViewById(R.id.ip)).getText().toString().trim());
            ActivityUtil.intentActivity(this, intent);

        }
    }

}
