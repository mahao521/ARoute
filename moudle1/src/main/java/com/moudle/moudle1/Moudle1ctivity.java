package com.moudle.moudle1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moudle.base.TestService;
import com.moudle.router_annotation.Route;
import com.moudle.router_core.DNRouter;
import com.moudle.router_core.template.IService;

@Route(path = "/moudle1/mahao")
public class Moudle1ctivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moudle1ctivity);
    }

    public void jump(View view) {
        DNRouter.getInstance().build("/moudle3/mahao").withString("a",
                "从Module1").navigation(this);
        TestService service = (TestService) DNRouter.getInstance().build("/moudle3/service").navigation();
        service.getTest();
    }
}
