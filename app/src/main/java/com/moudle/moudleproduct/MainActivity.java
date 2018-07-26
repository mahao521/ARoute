package com.moudle.moudleproduct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moudle.router_core.DNRouter;

@Route(path = "/main/test")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DNRouter.init(getApplication());
    }
}
