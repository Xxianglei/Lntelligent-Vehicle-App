package com.example.heath.Register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.heath.HttpUtils.OkNetRequest;
import com.example.heath.MainActivity;
import com.example.heath.MyApplication;
import com.example.heath.R;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.Response;

import static com.xiasuhuei321.loadingdialog.view.LoadingDialog.Speed.SPEED_TWO;

/**
 * Created by 丽丽超可爱 on 2018/3/11.
 */

public class Set_password extends AppCompatActivity implements View.OnClickListener {


    private String phone;
    private String password;


    private FloatingActionButton fab;
    private CardView cvAdd;
    private EditText etPassword;
    private EditText etRepeatpassword;
    private static final String PASSWORD_PATTERN = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";


    private Pattern pattern1 = Pattern.compile(PASSWORD_PATTERN);
    private Button next;
    private MyApplication myApplication;
    private NetworkChangeReceiver networkChangeReceiver;
    private LoadingDialog ld;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpassword);
        ShowEnterAnimation();
        MyApplication.getInstance().addActivity(this);
        initView();


    }

    @Override
    protected  void onPause(){
        if (ld!=null)
        ld.close();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(ld!=null)
            ld=null;
        this.unregisterReceiver(networkChangeReceiver);
    }

    private void initView() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        //动态注册
        registerReceiver(networkChangeReceiver, intentFilter);
        // 获取电话号码
        etPassword = (EditText) findViewById(R.id.et_password);
        etRepeatpassword = (EditText) findViewById(R.id.et_repeatpassword);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        next = (Button) findViewById(R.id.bt_go);
        next.setClickable(false);
        next.setOnClickListener(this);
        fab.setOnClickListener(this);
        myApplication = (MyApplication) getApplication();
        if (myApplication.getName()!=null)
        phone = myApplication.getName().toString();
    }

    private void CheckPass(String num, String pass) {

        Log.e("log_demo", "sendSms:打包电话和密码 ");
        if (!validatepassword(pass)&&((etPassword.getText().length()==etRepeatpassword.getText().length()) )&&etPassword.getText()!=null&&etRepeatpassword.getText()!=null){
            Toast.makeText(Set_password.this, "亲 请输入由英文和数字组成的密码哦！！", Toast.LENGTH_SHORT).show();
        } else {
            ld.setLoadingText("正在登录中...")
                    .setSuccessText("登录成功")//显示加载成功时的文字
                    .setFailedText("注册失败")
                    .setLoadSpeed(SPEED_TWO)
                    .show();
            GLog_in(num, pass);
        }
    }

    private void GLog_in(String name, String password) {


        HashMap<String, String> params = new HashMap<>();
        // 添加请求参数
        params.put("phone", name);
        params.put("password", password);
        Log.e("注册账号密码", params.toString());
        String url = "http://47.94.21.55/houtai/index.php";
        OkNetRequest.postFormRequest(url, params, new OkNetRequest.DataCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void requestSuccess(Response response,String result) throws Exception {
                // 请求成功的回调
                String deal_result  = result.toString();
                Log.e("注册",deal_result);
                myApplication.setPhone(phone);
                if (deal_result.contains("101")){
                    startActivity(new Intent(Set_password.this,MainActivity.class));
                    Set_password.this.finish();

                }else
                {
                    ld.loadFailed();
                    Toast.makeText(Set_password.this,"注册失败，请重新注册",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void progressSuccess(Response response) throws Exception {

            }

            @Override
            public void requestFailure(Request request, IOException e) {
                // 请求失败的回调
                ld.loadFailed();
                Toast.makeText(Set_password.this,"注册失败，请重新注册",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                Set_password.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }


    public boolean validatepassword(String pass) {
        Matcher matcher1 = pattern1.matcher(pass);
        return matcher1.matches();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_go:
                Log.e("点击按钮", "-----------");
                ld = new LoadingDialog(Set_password.this);
                CheckPass(phone, etRepeatpassword.getText().toString());

                break;
            case R.id.fab:
                animateRevealClose();
                break;
            default:
                break;

        }
    }
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //得到网络连接管理器
            ConnectivityManager connectionManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            //通过管理器得到网络实例
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            //判断是否连接
            if (networkInfo != null && networkInfo.isAvailable()) {
              next.setEnabled(true);
            } else {
                next.setEnabled(false);
                Toast.makeText(context, "当前网络已断开!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
