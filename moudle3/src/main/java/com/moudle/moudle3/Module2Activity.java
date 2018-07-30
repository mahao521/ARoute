package com.moudle.moudle3;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.moudle.base.TestService;
import com.moudle.router_annotation.Route;
import com.moudle.router_core.DNRouter;

@Route(path = "/moudle3/mahao")
public class Module2Activity extends AppCompatActivity {

    private static final String TAG = "Module2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module2);
      //  DNRouter.getInstance().inject(this);
        Log.d(TAG, "onCreate: " + Module2Activity.class.getName());
    }

    /**
     *
     * @param view
     */
    public void jump(View view){
        if(BuildConfig.isModule){
            DNRouter.getInstance().build("/moudle1/mahao").withString("msg","从Module2")
                    .navigation(this);
            TestService service = (TestService) DNRouter.getInstance().build("/moudle1/service").navigation();
            service.getTest();
        }
    }
}
