package com.dorm.smartterminal.settings.localsetting.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dorm.smartterminal.R;

/***
 * local setting page
 * 
 * @author andy liu
 * 
 */
public class LocalSetting extends Activity implements OnClickListener {

    /*
     * ui
     */

    // define buttons
    Button back = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st__settings__local_setting);

        /*
         * ui
         */
        
        // get buttons
        back = (Button) findViewById(R.id.back);

        // set onclick listener
        back.setOnClickListener(this);
        
    }

    /*
     * logic
     */
    
    @Override
    public void onClick(View v) {
        
        if(v == back){
            
            this.finish();
            
        }
        
    }

}
