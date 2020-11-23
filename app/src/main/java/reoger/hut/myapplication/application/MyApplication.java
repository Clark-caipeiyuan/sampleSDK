/**
 * FileName: MyApplication
 * Author: Administrator
 * Date: 2020/11/19 0019 13:27
 * Description:
 */
package reoger.hut.myapplication.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;

/**
 * @ClassName: MyApplication
 * @Description: java类作用描述
 * @Author: Administrator
 * @Date: 2020/11/19 0019 13:27
 */
public class MyApplication extends Application {

    public void onCreate(){

        super.onCreate();
//        Bmob.initialize(this,Constant.BMOB_APP_ID)
    }


}