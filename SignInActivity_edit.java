package ws.coverme.im.ui.login_registe;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ws.coverme.im.R;
import ws.coverme.im.JucoreAdp.Jucore;
import ws.coverme.im.JucoreAdp.CbImplement.MyClientInstCallback;
import ws.coverme.im.JucoreAdp.ClientInst.IClientInstance;
import ws.coverme.im.JucoreAdp.Types.DataStructs.ClientConnectedIndication;
import ws.coverme.im.JucoreAdp.Types.DataStructs.PingRespond;
import ws.coverme.im.appsflyer.AppsFlyer;
import ws.coverme.im.dll.LockScreenTableOperation;
import ws.coverme.im.dll.LoginFailedLogTableOperation;
import ws.coverme.im.dll.LoginSuccessLogTableOperation;
import ws.coverme.im.dll.SettingTableOperation;
import ws.coverme.im.dll.SharedPreferencesManager;
import ws.coverme.im.dll.UserTableOperation;
import ws.coverme.im.model.ActivityStack;
import ws.coverme.im.model.KexinData;
import ws.coverme.im.model.albums.LockScreenData;
import ws.coverme.im.model.constant.AppConstants;
import ws.coverme.im.model.constant.Constants.Extra;
import ws.coverme.im.model.constant.Enums;
import ws.coverme.im.model.file_transfer.TransferManager;
import ws.coverme.im.model.hide_app.HideAppUtil;
import ws.coverme.im.model.local_crypto.LocalAESKeyManager;
import ws.coverme.im.model.local_crypto.PasswordCryptor;
import ws.coverme.im.model.local_crypto.SHAEncryptor;
import ws.coverme.im.model.login_logs.LoginFailedLog;
import ws.coverme.im.model.login_logs.LoginSuccessLog;
import ws.coverme.im.model.my_account.SuperAccountAlertUtil;
import ws.coverme.im.model.others.UseData;
import ws.coverme.im.model.others.help.HelpConstants;
import ws.coverme.im.model.push.PushType;
import ws.coverme.im.model.settings.EasyTouchListener;
import ws.coverme.im.model.settings.Security;
import ws.coverme.im.model.shake.OnShakeListener;
import ws.coverme.im.model.shake.ShakeListener;
import ws.coverme.im.model.transfer_crypto.TransferCrypto;
import ws.coverme.im.model.user.Profile;
import ws.coverme.im.model.user.User;
import ws.coverme.im.service.BCMsg;
import ws.coverme.im.service.GenericService;
import ws.coverme.im.ui.KexinApp;
import ws.coverme.im.ui.MainActivity;
import ws.coverme.im.ui.adapter.GridAdapter;
import ws.coverme.im.ui.adapter.LockoutImageAdapter;
import ws.coverme.im.ui.albums.bitmapfun.RecyclingBitmapDrawable;
import ws.coverme.im.ui.call.CallMsgManage;
import ws.coverme.im.ui.call.RemoteAnswerCallActivity;
import ws.coverme.im.ui.chat.util.LocationHelper;
import ws.coverme.im.ui.contacts.HandlerConstans;
import ws.coverme.im.ui.graphical_psw.GrapicalTouchView;
import ws.coverme.im.ui.login_registe.ResizeLayout.OnResizeListener;
import ws.coverme.im.ui.market.MarketUtil;
import ws.coverme.im.ui.my_account.ForgotMainPasswordActivity;
import ws.coverme.im.ui.my_account.ModifySuperPasswordConfirmEmailActivity;
import ws.coverme.im.ui.my_account.util.ChannelUtil;
import ws.coverme.im.ui.others.FeedbackActivity;
import ws.coverme.im.ui.others.RemindCommentUtil;
import ws.coverme.im.ui.others.advancedversion.util.PremiumUtil;
import ws.coverme.im.ui.private_document.PrivateDocHelper;
import ws.coverme.im.ui.privatenumber.PrivateReceiveCallActivity;
import ws.coverme.im.ui.superaccount.SetSuperAccountAlertActivity;
import ws.coverme.im.ui.view.BaseActivity;
import ws.coverme.im.ui.view.MyDialog;
import ws.coverme.im.ui.view.NumberGridView;
import ws.coverme.im.util.AppInstalledUtil;
import ws.coverme.im.util.AppSwitcher;
import ws.coverme.im.util.CMProgressDialog;
import ws.coverme.im.util.CMTracer;
import ws.coverme.im.util.CameraCallback;
import ws.coverme.im.util.CameraUtil;
import ws.coverme.im.util.ClickTimeSpanUtil;
import ws.coverme.im.util.DateUtil;
import ws.coverme.im.util.DialogUtil;
import ws.coverme.im.util.ImageUtil;
import ws.coverme.im.util.IntruderUtil;
import ws.coverme.im.util.OtherHelper;
import ws.coverme.im.util.Size;
import ws.coverme.im.util.SolftInputUtil;
import ws.coverme.im.util.StrUtil;
import ws.coverme.im.util.ToastUtil;
import ws.coverme.im.util.Utils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 
 * SignIn类 文件名 SignInActivity.java 创建人 Administrator 创建时间 2011-1-4 版权 南京讯天 Kexin
 * 描述 [该类的简要描述]
 * 登录界面，输入密码即可进入系统
 */
public class SignInActivity extends BaseActivity implements OnClickListener,OnShakeListener
{
    public static final String TAG = "SignInActivity";
    private static final int MSG_WHAT_SAVE_LOG = 2;
    protected static final int MSG_WHAT_SHOW_PROGRESS = 3;
    protected static final int MSG_WHAT_CLOSE_PROGRESS = 4;
    protected static final int MSG_WHAT_START_NEXTVIEW = 5;
    private static final int DIALOG_PSD_WRONG = 0;
    public static final int MSG_SHOW_SWITCH_BTN = 1;
    public static final int MSG_WHAT_CLOSE_SWITCH_BTN = 10;
 // private RelativeLayout midRelativeLayout;
    private RelativeLayout pwRelativeLayout;
 //   private Button signInBtn;
    private SurfaceView surfaceView;
    private EditText passwordEditText;
    private InputMethodManager imm;
    private boolean intruderWait = false;

    private int wrongTimes = 0;// 错误登录的次数
    private boolean isLoginSuccess;
    private boolean hasSdcard;

	//标记是否有超级密码
	private boolean hasSuperPassword;
    private String inputPassword;
    private KexinData kexinData;
    private Security security;// 私密信息

    private long waitTime;// 输入密码错误需等待的时间(s)
    private CMProgressDialog proDialog;
 // private Dialog waitDialog;

    private boolean initCam = false;
    private CameraCallback cameraCB;
    
    /**
     * 偷拍照片存放的路径
     */
    private String strImgPath;
    private boolean isMarket = false;

    private NumberGridView gridView;
    private GridAdapter numbrAdapter;
    //忘记密码
    private TextView forgetPasswordTv;

    
    private Jucore jucore;
    private IClientInstance client;
    
    private static final int  LOCKSCREEN_BACK = 10;
    private int imageIndex = -1;
    public String msgType;
    public String kexinId;
    public String meta;
    public LocationHelper locationHelper;
    
    private Gallery gallery;
    private boolean pwVisible = true;
    private boolean isStrong = false;
    private boolean isClickable = true;
    private LockoutImageAdapter lockoutAdapter;
    
    private ShakeListener shakeListener;
    private boolean isMonitorShake;
    private EasyTouchListener easyTouch;
    //登录密码类型
    private String loginPasswordType;
    private Button switchBtn;
    private static final int BIGGER = 11;   
    private static final int SMALLER = 12;
    public static final int MSG_WHAT_OPEN_CAMERA = 0xff20;
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case MSG_WHAT_OPEN_CAMERA:
             	if(CameraUtil.camera!=null)
     			{
             		SurfaceHolder surfaceHolder = (SurfaceHolder)msg.obj;
     				try 
     			    {
     					CameraUtil.camera.setPreviewDisplay(surfaceHolder);
     					initCam = CameraUtil.SetParameters(surfaceHolder);
     				} catch (Exception e)
     				{
     					e.printStackTrace();
     					initCam = false;
     				}
     			}
             	CMTracer.i(TAG,"open camera resulst "+initCam);
             	break;
            case BIGGER:
            	if(switchBtn!=null)
            	switchBtn.setVisibility(View.GONE);
            	break;
            case SMALLER:
            	if(switchBtn!=null&&gridView.getVisibility()==View.GONE)
            	switchBtn.setVisibility(View.VISIBLE);
            	break;
            case MSG_SHOW_SWITCH_BTN:
            	switchBtn.setVisibility(View.VISIBLE);
    	        gridView.setVisibility(View.GONE);
            	break;
            case EasyTouchListener.MSG_GESTURE_SCALE:
            	boolean scale = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.login_Pwd_pinch, SignInActivity.this);
            	if(!pwVisible && scale)
            	{
            		boolean isInResettingSuperPassword = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.AccountDataType_checkReSettingSuperPassword,SignInActivity.this);
                	if (isInResettingSuperPassword)
					{
                    	 Intent intent = new Intent(SignInActivity.this,ModifySuperPasswordConfirmEmailActivity.class);
                    	 startActivity(intent);
                    	 finish();
					}
                	else
                	{
                		showPswRelativeLayout();
					}
            	}
            	break;
            	
            case EasyTouchListener.MSG_CLICK_TIMES:
       //     	int touchTimes = msg.arg1;
            	boolean reaction = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.login_Pwd_tap, SignInActivity.this);
            	if (!pwVisible && reaction) 
				{
					// 这里把点击了几次后的动作定义,然后点击次数置为0
            		boolean isInResettingSuperPassword = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.AccountDataType_checkReSettingSuperPassword,SignInActivity.this);
                	if (isInResettingSuperPassword)
					{
                    	 Intent intent = new Intent(SignInActivity.this,ModifySuperPasswordConfirmEmailActivity.class);
                    	 startActivity(intent);
                    	 finish();
					}
                	else 
                	{
                		showPswRelativeLayout();
                		easyTouch.setClickTimes(0);
                		isClickable = false;
                		Timer timer = new Timer();
            	        timer.schedule(new TimerTask()
            	             {                  
            	                 public void run()
            	                 {
            	                	 isClickable = true;
            	                 }
            	             }, 2000);
					}
				}
            	break;
            	
            case EasyTouchListener.MSG_PRESS_LONGTIME:
            	boolean hold = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.login_Pwd_hold, SignInActivity.this);
            	if(!pwVisible && hold)
            	{
                	boolean isInResettingSuperPassword = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.AccountDataType_checkReSettingSuperPassword,SignInActivity.this);
                	if (isInResettingSuperPassword)
					{
                    	 Intent intent = new Intent(SignInActivity.this,ModifySuperPasswordConfirmEmailActivity.class);
                    	 startActivity(intent);
                    	 finish();
					}
                	else
                	{
                		showPswRelativeLayout();
                	}
            	}
            	break;
            	
                case MSG_WHAT_SAVE_LOG:  
                 strImgPath = (String) msg.obj;
                 // 保存日志
               	 saveLoginLog(isLoginSuccess, inputPassword);
                    if (isLoginSuccess)
                    {
                        initKexinData();
                    }
                    break;

                case MSG_WHAT_CLOSE_PROGRESS:
                	if(null != proDialog && proDialog.isShowing())
                    	proDialog.dismiss();
                	break;
                	
                case MSG_WHAT_SHOW_PROGRESS:
                	if(null != proDialog)
                	{
                		proDialog.show();
                		proDialog.setCancelable(false);
                	}
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            int time = 0;
                            while (time < waitTime)
                            {
                                try
                                {
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                                time++;
                            }
                            mHandler.sendEmptyMessage(MSG_WHAT_CLOSE_PROGRESS);
                        }

                    }.start();

                    break;

                case MSG_WHAT_START_NEXTVIEW:// 本地登录成功后到主界面
                	CMTracer.i("msg", "handler receive msg");
                    TransferCrypto transferCrypto = new TransferCrypto();
                    transferCrypto.initRSAKeyManager(SignInActivity.this);
                    transferCrypto.initAESKeyManager(SignInActivity.this);
                    //登录成功，去掉PUSH通知
                	if (null != GenericService.mNotificationMGR)
                	{
                		GenericService.mNotificationMGR.cancelAll();
                	}
                    if(KexinData.mIsApplicationContextCreate )
                    {
                    	RemindCommentUtil.updateLoginSuccessFrequency();
                    	KexinData.mIsApplicationContextCreate = false;
                    }
                    /*
                     * LoginThread loginThread = new
                     * LoginThread(SignInActivity.this); loginThread.start();
                     */
                    CMTracer.i("msg", "cancel dialog");
                    if (null != proDialog && proDialog.isShowing())
                    {
                        proDialog.dismiss();
                    }
                    // 在这里释放资源是为了以防万一
                    /*
                     * if (camera != null) { camera.release(); camera = null; }
                     */
//                  CameraUtil.exit();

                    startNextActivity();                    
                    break;
                    
                case EasyTouchListener.MSG_CLEAR_CONCER_CLICK_COUNTS:
                	boolean corner = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.login_Pwd_corner, SignInActivity.this);
                	if(!pwVisible && corner)
                	{
                    	boolean isInResettingSuperPassword = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.AccountDataType_checkReSettingSuperPassword,SignInActivity.this);
                    	if (isInResettingSuperPassword)
						{
                        	 Intent intent = new Intent(SignInActivity.this,ModifySuperPasswordConfirmEmailActivity.class);
                        	 startActivity(intent);
                        	 finish();
						}
                    	else
                    	{
                    		showPswRelativeLayout();	
                    	}
                	}
                	if(easyTouch != null)
                	{
                		easyTouch.setClickTimes(0);
                		easyTouch.setDefaultMap();
                	}
    				
    				break;	
                case HandlerConstans.WHAT_OnConnectedResponse:
                    Bundle bundle = msg.getData();
                    ClientConnectedIndication ccd = (ClientConnectedIndication) bundle.getSerializable("connect");
                    if (ccd.result == 0)
                    {
                        if(intruderWait)
                        {
                        	 //发送邮件入侵警告
                        	 IntruderUtil.sendIntruderEmail(SignInActivity.this, inputPassword, strImgPath);
                        }
                    }
                    else
                    {
                    	//activity销毁过程中不显示对话框
                        if(isFinishing())
                        {
                        	return;
                        }
                        MyDialog dialog = new MyDialog(SignInActivity.this);
                        dialog.setTitle(R.string.timeout_title);
                        dialog.setMessage(R.string.timeout_content);
                        dialog.setSinglePositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                    break;
            }
        }

    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signin);
       
    	// user number > 1, means db file is ok 
        OtherHelper.backupDbPeriodically(this);
        OtherHelper.checkToDelPeriodicDbBackup();
        
        KexinData.mNeedSwitch2MessageActivity = true;
        
        // 判断是否有Sdcard
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            hasSdcard = true;
        }
        
        initView();
        initData();

        registBroadcast();
        
        showDataMigrateDialog(this);
    }

    //当可信数据被迁移到CMN后，在首页面提示可信已被注销，引导用户点击确认按钮后退出APP
    private void showDataMigrateDialog(Context context)
    {
    	String pkg = getPackageName();
    	if((AppConstants.COVERMEPACKAGENAME.equals(pkg))||
    			(AppConstants.PRIVATECALLPACKAGENAME.equals(pkg))||
    			(AppConstants.SAFEBOXPACKAGENAME.equals(pkg))){
    		String dataMigrate = AppSwitcher.judgeImMigrate(context);
        	CMTracer.i(TAG, "showDataMigrateDialog:"+dataMigrate);
    		
        	if(SharedPreferencesManager.DATA_UN_MIGRATE.equals(dataMigrate)){
        		
        	}else if(SharedPreferencesManager.DATA_CMN_USED_ALONE.equals(dataMigrate)){
        		
        	}else if(SharedPreferencesManager.DATA_HAS_MIGRATE.equals(dataMigrate)){
                MyDialog dlg = new MyDialog(this);
                dlg.setTitle(R.string.coverme_data_has_migrate_title);  
                dlg.setMessage(R.string.coverme_data_has_migrate_content);  
                //点击确认退出APP，且屏蔽返回 
                dlg.setSinglePositiveButton(R.string.main_sure, new View.OnClickListener(){
                    @Override
                    public void onClick(View dialog)
                    {
                    	TransferManager.stopAllTasks();
                        exit();
                    }
                });
                dlg.setCancelable(false);
                dlg.show();
                mHandler.sendEmptyMessage(BIGGER);
        	}
    	}
    }
    
    private void exit()
    {
    	CMTracer.i("appStatus", "logout");
    	if (null != GenericService.mNotificationMGR)
    	    GenericService.mNotificationMGR.cancelAll();
        PrivateDocHelper.cancelAllTask(kexinData.getCurrentAuthorityId()); //私密文档
        UseData useData = new UseData();
        useData.updateReceiedAndSendData();
        //调用Jucore断开连接
        jucore = Jucore.getInstance();
        IClientInstance client = jucore.getClientInstance();
        client.Disconnect();
        kexinData.connectStatus = Enums.enum_connect_status_unconnect;
        
        kexinData.isOnline = false;
        kexinData.localLogin = false;
        kexinData.mInitDbOver = false;
        kexinData.inRegisting = false;	// for fear that: if exit before login return ok, and other unexpected conditions
        
        kexinData.cleanAllData(false);
        SharedPreferencesManager.setSharedIntPreferences("currentUserId", 0, this);
        ActivityStack.getInstance().popAll();
        finish();
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();

    };

    @Override
    protected void onDestroy()
    {
    	if(mHandler != null)
    	{
    		mHandler.removeCallbacks(runnableTakeCameraFailDelay);
    	}
        kexinData.unLockInActivity = false;
        if(null != proDialog && proDialog.isShowing())
        	proDialog.dismiss();
        if (null != locationHelper)
        {
            locationHelper.removeLocationUpdate();
        }
        //提示系统及时回收内存
        System.gc();
        super.onDestroy();
        //登录成功后，需要连接到服务器
        kexinData.IsNeeedloginServer = true;
        this.unregisterReceiver(mBcReceiver);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MyClientInstCallback mcb = new MyClientInstCallback(SignInActivity.this);
        mcb.registHandler(mHandler);
        jucore.registInstCallback(mcb);
        
        if(!PremiumUtil.isPremiumFeaturesPurchased())
        {
        	boolean needOff = OtherHelper.initHideMode(this);
        	if(needOff)
            Toast.makeText(this, R.string.hiddenmode_has_off, Toast.LENGTH_LONG).show();
        }

        checkToHandleIncomingPushCall();
        
        //隐藏模式 拨号登录
        boolean is = SharedPreferencesManager.getSharedBooleanPreferencesDefaultFalse("directLogin", this);
        if(is)
        {
        	SharedPreferencesManager.setSharedBooleanPreferences("directLogin", false, this);
        	successLogin();
        }
        if (hasSdcard && CameraUtil.FindFrontCamera())
        {
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(new SurfaceCallback());
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }
    
    private void checkToHandleIncomingPushCall()
    {
        if (null != KexinData.getInstance().pushCallInfo)
        {
        	
            msgType = KexinData.getInstance().pushCallInfo.msgType;
            kexinId = KexinData.getInstance().pushCallInfo.kexinId;
            meta = KexinData.getInstance().pushCallInfo.meta;
            if (null != msgType)
            {
                if (msgType.equals(PushType.PUSH_TYPE_CALL_INVITATION))
                {
                    // 保存callsession
                    CallMsgManage callMange = CallMsgManage.getInstance();
                    callMange.remoreCallInvite(meta);
                    // 连接服务器
                    Intent intentRemote = new Intent(SignInActivity.this,
                            RemoteAnswerCallActivity.class);
                    intentRemote.putExtra("msgType", msgType);
                    intentRemote.putExtra("kexinId", kexinId);
                    intentRemote.putExtra("meta", meta);
                    
                    intentRemote.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentRemote);
                    KexinData.getInstance().pushCallInfo = null;
                    SignInActivity.this.finish();
                }
                else if (msgType.equals(PushType.PUSH_TYPE_PSTN_CALL_INVITATION))
                {
                    Intent intent = new Intent(SignInActivity.this, PrivateReceiveCallActivity.class);
                    intent.putExtra("launchState", "pushCall");
                    intent.putExtra("meta", meta);
                    startActivity(intent);
                    KexinData.getInstance().pushCallInfo = null;
					SignInActivity.this.finish();
                }
            }
        }
        kexinData.unLockInActivity = true;
    }

    @Override
    protected void onStop()
    {

        super.onStop();
        CMTracer.i(TAG, "******onStop");
        // 在这里释放资源是为了以防万一
//      CameraUtil.exit();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        jucore.unRegistInstCallback();
    }

    private OnClickListener numberClick = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String number = (String) v.getTag();

            StringBuffer sb = new StringBuffer();
            String restStr = "";
            int end = passwordEditText.getSelectionEnd();
            int length=passwordEditText.getText().length();
            String numberStr = passwordEditText.getText().toString().substring(0, end);
            if(length > end)
            {
            	restStr = passwordEditText.getText().toString().substring(end, length);
            }
            sb.append(numberStr);

            if (number.equals(GridAdapter.DOWN))// 隐藏该键盘
            {
            	gridView.setVisibility(View.GONE);
    			Timer timer = new Timer();
    	        timer.schedule(new TimerTask()
    	             {                  
    	                 public void run()
    	                 {
    	                	 SolftInputUtil.showKeyboardAtView(passwordEditText,SignInActivity.this);
    	                	 //
    	                	 mHandler.sendEmptyMessageDelayed(MSG_SHOW_SWITCH_BTN, 100);
    	                 }
    	             }, 100);
    	   	    SolftInputUtil.showSoftInputFoucus(SignInActivity.this,passwordEditText);
    	        SettingTableOperation.saveBooleanSetting(SharedPreferencesManager.Strong_Pwd,true, SignInActivity.this);
            }
            else if (number.equals(GridAdapter.DELETE))// 回退，去掉最后一个字符
            {
                if (numberStr.length() > 0)
                {
                    String sub = sb.substring(0, sb.length() - 1);
                    sb.setLength(0);
                    sb.append(sub);
                    if(end > 0)
                    {
                       end --;
                    }
                    sb.append(restStr);
                    passwordEditText.setText(sb.toString());
                }
            }
            else
            {
            	if(OtherHelper.getlenth(passwordEditText) > numberStr.length() + restStr.length())
            	{
            		sb.append(number);
            		sb.append(restStr);
	                end ++;
                    passwordEditText.setText(sb.toString());
            	}
            }
            passwordEditText.setSelection(end>sb.length()?sb.length():end);
        }
    };

    private OnLongClickListener longClick = new OnLongClickListener()
    {

        @Override
        public boolean onLongClick(View v)
        {
            String number = (String) v.getTag();
            if (number.equals(GridAdapter.DELETE))
            {
                passwordEditText.setText("");
            }
            return false;
        }
    };

    private void initView()
    {
    	switchBtn =  (Button)findViewById(R.id.sign_in_switch_btn);
        forgetPasswordTv = (TextView) findViewById(R.id.lockout_forget_password_tv);
        forgetPasswordTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        forgetPasswordTv.setOnClickListener(this);
        passwordEditText = (EditText) findViewById(R.id.signin_password_editview);
        passwordEditText.setTypeface(Typeface.SANS_SERIF);
        passwordEditText.requestFocus();
        passwordEditText.addTextChangedListener(new TextWatcher() {  
            @Override  
            public void afterTextChanged(Editable s) {  
            }  
          
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
            }  
          
            @Override  
            public void onTextChanged(CharSequence s, int start, int before,  
                    int count) {  
                String content = passwordEditText.getText().toString();  
                if(content.length() > 0)
                {
                	passwordEditText.setGravity(Gravity.CENTER);
                }
                else
                {
                	passwordEditText.setGravity(Gravity.LEFT);
                }
            }       
        });  
        
        passwordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
			}
		});
        
        pwRelativeLayout = (RelativeLayout) findViewById(R.id.signin_pw_relativelayout);
        pwRelativeLayout.setOnClickListener(this);

        cameraCB = new CameraCallback();
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
//      if (hasSdcard && CameraUtil.FindFrontCamera())
//      {
//          SurfaceHolder surfaceHolder = surfaceView.getHolder();
//          surfaceHolder.addCallback(new SurfaceCallback());
//          surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//      }

        gridView = (NumberGridView) findViewById(R.id.signin_number_gridview);
        gallery = (Gallery) findViewById(R.id.signin_gallery);
        ResizeLayout layout = (ResizeLayout) findViewById(R.id.signin_mid_relativelayout); 
        layout.setOnResizeListener(new OnResizeListener() {
			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if(h<oldh)
				{
					change = SMALLER;
				}
				mHandler.sendEmptyMessage(change);
			}
		});
    }
    
    private void showPswRelativeLayout()
    {
    	CMTracer.i(TAG, "showPswRelativeLayout----");
    	pwRelativeLayout.setVisibility(View.VISIBLE);
    	isStrong = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.Strong_Pwd, this);
		if(isStrong)
		{
			Timer timer = new Timer();
	        timer.schedule(new TimerTask()
	             {                  
	                 public void run()
	                 {
	                	 SolftInputUtil.showKeyboardAtView(passwordEditText, SignInActivity.this);
	                	 mHandler.sendEmptyMessageDelayed(MSG_SHOW_SWITCH_BTN, 100);
	                 }
	             }, 100);
		}
		else if(gridView.getVisibility()==View.GONE)
		{
			gridView.setVisibility(View.VISIBLE);
		}
//		gallery.setEnabled(false);
		pwVisible = true;
    }
    
    private void hidePswRelativeLayout()
    {
    	CMTracer.i(TAG, "hidePswRelativeLayout----");
//    	if(isStrong)
    	{
    		SolftInputUtil.hideSoftInputFromWindow(SignInActivity.this);
//    		hideSoftInputFromWindow(this,passwordEditText);
    	}		
    	pwRelativeLayout.setVisibility(View.INVISIBLE);
		pwVisible = false;
		switchBtn.setVisibility(View.INVISIBLE);
		
		//CMN与CoverMe摇一摇采用不同的策略
		if(!AppInstalledUtil.isCmnApp(this) && isMonitorShake && shakeListener != null && !shakeListener.isStart)
        {
        	shakeListener.start();
        	shakeListener.isStart = true;
        }
    }
    
    private OnItemClickListener onItemClick = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id)
        {
        	imageIndex = position;
        //	gallery.setVisibility(View.GONE);
        	if(!pwVisible)
        	{
        		if(!isMonitorShake)
        		{
        			showPswRelativeLayout();
        		}
        	}
        	else
        	{
       // 		hidePswRelativeLayout();
        	}
        //	setBackgroundImage(imageIndex);
        }
    };

    private void successLogin()
    {
    	proDialog.setCancelable(false);
        proDialog.show();

        String appLaunchKey = HideAppUtil.getLaunchKey(this);
        if(localLogin(appLaunchKey))
        {
        	successLoginToDo();
        }
        
        // read and start transfer tasks and these tasks will wait for login, then start transfer
        TransferManager.InitTransfer();
        TransferManager.resumeAllTasks();
    }

    private void initData()
    {
        // 前置摄像头
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 后置摄像头
        // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        int wrongTimes = SettingTableOperation.getIntSetting(SharedPreferencesManager.WRONG_LOGIN_TIME, this);
        //根据有无超级密码来决定要不要显示忘记登录密码
        hasSuperPassword = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.AccountDataType_HasSuperPassword, this);
        if (hasSuperPassword&&wrongTimes>=4&&!UserTableOperation.isMainPasswordProtectedBySuperpassword(this))
		{
			forgetPasswordTv.setVisibility(View.VISIBLE);
		}
        else
        {
        	forgetPasswordTv.setVisibility(View.GONE);
		}
        jucore = Jucore.getInstance();

        kexinData = KexinData.getInstance(this);
        kexinData.localLogin = false;
        
        security = kexinData.getSeurity();
        proDialog = new CMProgressDialog(this);
        client = jucore.getClientInstance();
        numbrAdapter = new GridAdapter(this, numberClick, longClick);
        gridView.setAdapter(numbrAdapter);
        
        setGalleryAdapter();
        
        isMonitorShake = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.show_login_Pwd, this);
        isStrong = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.Strong_Pwd, this);//SharedPreferencesManager.getSharedBooleanPreferencesDefaultFalse(SharedPreferencesManager.Strong_Pwd, this);
        if(!isStrong)
        {
        	SolftInputUtil.hideSoftInputFromWindow(this,passwordEditText );
        	switchBtn.setVisibility(View.GONE);
        }
        else
        {
        	showSystemKeybord();
        }
        shakeListener = new ShakeListener(this, this);
        
        if(isMonitorShake)
        {   
        	DisplayMetrics outMetrics = new DisplayMetrics();
    		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            int screenWidth = outMetrics.widthPixels;
            int screenHeight = outMetrics.heightPixels;
            
        	//创建手势类
    		easyTouch = new EasyTouchListener(mHandler, screenHeight, screenWidth, this);
        	easyTouch.setLongTime(5000L);
            gallery.setOnTouchListener(easyTouch);

            hidePswRelativeLayout();
        }
        else
        {
        	gallery.setOnItemClickListener(onItemClick);
        	if(isStrong)
    		{
        		mHandler.postDelayed(new Runnable(){

					@Override
					public void run() {
						SolftInputUtil.showKeyboardAtView(passwordEditText, SignInActivity.this);
					}
        		}, 500);
    		}
        }
    }

	private void showSystemKeybord() {
		gridView.setVisibility(View.GONE);
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		     {                  
		         public void run()
		         {
		        	 SolftInputUtil.showKeyboardAtView(passwordEditText,SignInActivity.this);
		        	 mHandler.sendEmptyMessageDelayed(MSG_SHOW_SWITCH_BTN, 100);
		         }
		     }, 100);
		SolftInputUtil.showSoftInputFoucus(SignInActivity.this,passwordEditText);
	}
    
    /**
     * 隐藏Activity里面获得焦点的View的软键盘
     * @param context
     */
    public  void hideSoftInputFromWindow(Activity activity, View view)
    {
        InputMethodManager parentImm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(parentImm.isActive())
        {
            if(null != view)
            {
                parentImm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_HIDDEN);
            }
        }
    }

    protected void showMyDialog(int id, Bundle args)
    {
        MyDialog dialog = null;
        switch (id)
        {
            case DIALOG_PSD_WRONG:

                long time = args.getLong("waitTime");
                CMTracer.i(TAG, "time----" + time);
                StringBuffer sb = new StringBuffer();
                sb.append(getResources().getString(
                        R.string.signin_activity_warning_text_please, time));
                dialog = new MyDialog(this);
                dialog.setTitle(R.string.signin_activity_warning);
                dialog.setMessage(sb.toString());
                dialog.setSinglePositiveButton(R.string.close,
                        null);
                dialog.show();
                dialog.setCancelable(false);
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
    	Intent intent = null;
        switch (v.getId())
        {
            case R.id.signin_rl:
            case R.id.signin_ok_btn:
                if (ClickTimeSpanUtil.isFastDoubleClick(1500))
                {
                    break;
                }
                inputPassword = passwordEditText.getText().toString().trim();
                strImgPath = null;
                if (!StrUtil.isNull(inputPassword))
                {
                	int wrongTimes = SettingTableOperation.getIntSetting(SharedPreferencesManager.WRONG_LOGIN_TIME, this);
                	if (wrongTimes>4)
					{
                		long nowTime = System.currentTimeMillis();
                		//锁定发生时的时间
                		long lockTime = SettingTableOperation.getLongSetting(SharedPreferencesManager.AccountDataType_LoginFail_LockTime, this);
                		double result = (nowTime-lockTime) * 1.0 / (1000 * 60 *60);
                		//超过60分钟可以解除锁定
						if (result>1)
						{
							wrongTimes = 0;
							SettingTableOperation.saveIntSetting(SharedPreferencesManager.WRONG_LOGIN_TIME, wrongTimes, this);
							tryToLogin();
						}
						else
						{
							DialogUtil.showLoginFiveTimesAlert(this, hasSuperPassword, TAG);
						}
					}
                	else
                	{
						tryToLogin();
					}
                }
                else
                {
                    return;
                }
                break;
            	
            case R.id.signin_pw_relativelayout:
            	if(isClickable)
            	{
            		hidePswRelativeLayout();
            		passwordEditText.setText("");
            	}
            	break;
            //忘记登录密码
            case R.id.lockout_forget_password_tv:
                if (ClickTimeSpanUtil.isFastDoubleClick(3000))
                {
                    break;
                }
            	intent = new Intent(this,ForgotMainPasswordActivity.class);
            	intent.putExtra("from", TAG);
            	startActivityForResult(intent, 0);
            	break;
            case R.id.sign_in_switch_btn:
            	switchBtn.setVisibility(View.GONE);
                SolftInputUtil.hideSoftInputFromWindow(SignInActivity.this,passwordEditText);
            	InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive())
				{
					imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				}
				
    			Timer timer = new Timer();
    	        timer.schedule(new TimerTask()
    	             {                  
    	                 public void run()
    	                 {
    	                	 runOnUiThread(new Runnable() {
								@Override
								public void run() {
									 SettingTableOperation.saveBooleanSetting(SharedPreferencesManager.Strong_Pwd,false, SignInActivity.this);
		    	            		 gridView.setVisibility(View.VISIBLE);
								}
							});
    	                 }
    	             }, 200);
            	break;
            case R.id.rl_signin_help: 
            	intent = new Intent(this,SignInFeedBackActivity.class);
            	startActivityForResult(intent, 0);
            break;
            	
            default:
                break;
        }
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if(resultCode==RESULT_OK)
    	{
    		int type = data.getIntExtra("type", -1);
    	if(type==SignInFeedBackActivity.RESULT_CODE_FORGOT_MAINPASSWORD)
    	{
    		Intent intent= new Intent(SignInActivity.this,ForgotMainPasswordActivity.class);
        	intent.putExtra("from", TAG);
       		startActivity(intent);
    	}
    	else if(type==SignInFeedBackActivity.RESULT_CODE_FEEDBACK)
    	{
    		Intent intent= new Intent(SignInActivity.this,FeedbackActivity.class);
			intent.putExtra(Extra.EXTRA_ISSUE_ID, HelpConstants.PRODUCT_LOGIN_ISSURE);
       		startActivity(intent);
    	}
    	}

    }

	public void tryToLogin()
	{
		//开启线程尝试登陆
		if (localLogin(inputPassword))// 登录
		{
		    proDialog.setCancelable(false);
		    proDialog.show();
		    AppsFlyer.addEvent(AppsFlyer.AppsFlyer_EVENT_signup);
		    successLoginToDo();
		    TransferManager.InitTransfer();
		    TransferManager.resumeAllTasks();
		}
		else
		{
		    failedLoginToDo();
		}
	}

    /**
     * 注册广播
     */
    private void registBroadcast()
    {
        IntentFilter pushCallFilter = new IntentFilter(BCMsg.ACTION_END_SIGNIN_ON_CALL);
        pushCallFilter.addAction(BCMsg.ACTION_SIGNIN_INCOMING_PUSH_CALL);
        
        this.registerReceiver(mBcReceiver, pushCallFilter);
    }

    private BroadcastReceiver mBcReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (BCMsg.ACTION_END_SIGNIN_ON_CALL.equals(intent.getAction()))
            {
                finish();
            }
            else if (BCMsg.ACTION_SIGNIN_INCOMING_PUSH_CALL.equals(intent.getAction()))
            {
                checkToHandleIncomingPushCall();
            }
        }
    };

    /**
     * 登录成功操作
     */
    private void successLoginToDo()
    {
    	//登录成功后不再弹出系统键盘
//    	SolftInputUtil.hideSoftInputFromWindow(SignInActivity.this,passwordEditText);
    	kexinData.isInForgotpassword = false;
    	GrapicalTouchView.mTotalTimes = 5;
        Profile myProfile = kexinData.getMyProfile();
        boolean isphoto = false;

        Jucore.myUserId = myProfile.userId;
        isLoginSuccess = true;
        KexinData.setCounter2ReconnectImmediately();
        //清除错误次数
        SettingTableOperation.saveIntSetting(SharedPreferencesManager.WRONG_LOGIN_TIME, 0, SignInActivity.this);
        if (security.photoYou && hasSdcard && CameraUtil.FindFrontCamera() && initCam)
        {
            isphoto = CameraUtil.TakePicture(cameraCB.picCB, cameraCB.shCB);
        }
           
        if (!isphoto) // 不需要拍照或拍照失败时登录
        {
        	saveLoginLog(true, inputPassword);
            initKexinData();
        }
        //拍照2s超时回调失败，继续登录流程
        else
       {
        	mHandler.postDelayed(runnableTakeCameraFailDelay,2000);
       }
    }
    
    private Runnable runnableTakeCameraFailDelay = new Runnable()
    {  
        public void run()
        {  
        	mHandler.removeMessages(MSG_WHAT_SAVE_LOG);
			saveLoginLog(isLoginSuccess, inputPassword);
			initKexinData();  
        }  
    };  

    // 登录失败操作
    private void failedLoginToDo()
    {
        isLoginSuccess = false;
        passwordEditText.setText("");
        // 登录失败
        wrongTimes = SettingTableOperation.getIntSetting(SharedPreferencesManager.WRONG_LOGIN_TIME, this);
        wrongTimes++;
        SettingTableOperation.saveIntSetting(SharedPreferencesManager.WRONG_LOGIN_TIME, wrongTimes, SignInActivity.this);
        boolean isphoto = false;
        if (security.photoIntruder && hasSdcard && CameraUtil.FindFrontCamera() && initCam)
        {
        	isphoto = CameraUtil.TakePicture(cameraCB.picCB, cameraCB.shCB);
        }
        if(!isphoto)
        {
            saveLoginLog(false, inputPassword);
        }
        //登错1，2次给出错误提示
        if (wrongTimes<=4)
		{
          	MyDialog dialog = new MyDialog(this);
            dialog.setTitle(R.string.Key_5207_login_page_warning_1_title);
            dialog.setMessage(getString(R.string.Key_5208_login_page_warning_1_content, 5-wrongTimes));
            dialog.setSinglePositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					   showPswRelativeLayout();
				}
			});
            dialog.show();
		}
        else if (wrongTimes>4)
		{
        	//记录当前登录时间
        	long currentTime = System.currentTimeMillis();
        	SettingTableOperation.saveLongSetting(SharedPreferencesManager.AccountDataType_LoginFail_LockTime, currentTime, this);
        	DialogUtil.showLoginFiveTimesAlert(this, hasSuperPassword, TAG);
		}
   	    if (hasSuperPassword&&wrongTimes>=4&&!UserTableOperation.isMainPasswordProtectedBySuperpassword(this))
		{
			forgetPasswordTv.setVisibility(View.VISIBLE);
		}
        else
        {
        	forgetPasswordTv.setVisibility(View.GONE);
		}
    }
    
    private void initKexinData()
    {
        new Thread()
        {
            @Override
            public void run()
            {
            	CMTracer.i(TAG, "enter init all kexinData thread");
                kexinData.initAllData();
                CMTracer.i(TAG, "init all kexinData over");
                boolean run = mHandler.sendEmptyMessage(MSG_WHAT_START_NEXTVIEW);
            	CMTracer.i("run", " send msg result:"+run);
            }
        }.start();
    }

    private void startNextActivity()
    {
        Intent intent;
        intent = new Intent();
        /* //先取出
        boolean covermeSystemKilled = SharedPreferencesManager.getSharedBooleanPreferencesDefaultFalse(SharedPreferencesManager.COVERME_SYSTEM_KILLED, this);
        //再设置
        SharedPreferencesManager.setSharedBooleanPreferences(SharedPreferencesManager.COVERME_SYSTEM_KILLED, true, this);*/
        intent.putExtra("covermeSystemKilled", getIntent().getBooleanExtra("popInvalidateKillCovermeDialog", false));
        if(MarketUtil.isInDuration() && MarketUtil.needPrize(this) && isMarket)
        {
        	/*MyDialog myDialog = new MyDialog(this);  
        	myDialog.setTitle(R.string.tip);
            myDialog.setMessage(R.string.market_content_opportunity);
            myDialog.setNegativeButton(R.string.market_skip, new OnClickListener()
            {
            	public void onClick(View v)
                {
            		CMTracer.i(TAG, "start MainActivity");
                	CMTracer.i("appStatus", "login from signin success");
                	Intent intent = new Intent();
                    intent.setClass(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            myDialog.setPositiveButton(R.string.market_test, new OnClickListener()
            {
            	public void onClick(View v)
                {
            		if(MarketUtil.isInDuration() && MarketUtil.needPrize(SignInActivity.this))
           	        {
           	        	CMTracer.i(TAG, "start MarketQuestion Activity");
           	        	Intent intent;
           	            intent = new Intent();
           	        	intent.setClass(SignInActivity.this, MarketQuestionActivity.class);
           	        	MarketUtil.reducePrizeTimes(SignInActivity.this);
           	        	startActivity(intent);
           	        	finish();
           	        }
                }
            });
            myDialog.show();*/
        }
        else
        {
        	CMTracer.i(TAG, "start MainActivity");
        	CMTracer.i("appStatus", "login from signin success");
        	
            boolean isNeedAlert = SuperAccountAlertUtil.checkIsNeedSuperAccountAlert(this); 
            if (isNeedAlert)
			{
               intent.setClass(SignInActivity.this, SetSuperAccountAlertActivity.class);
			}
            else
            {
               intent.setClass(SignInActivity.this, MainActivity.class);
			}
            startActivity(intent);
            finish();
        }        
    }
    
    /**
     * 本地登录(点击signIn登录)
     * 
     * @param password
     */
    private boolean localLogin(String password)
    {
    	CMTracer.i(TAG, "localLogin----->");

        User user = new PasswordCryptor().getUserByPsw(password);

        if (null != user)
        {
            CMTracer.i(TAG, "local login, find user from db");
            LocalAESKeyManager localAesKeyManager = new LocalAESKeyManager();
            if (localAesKeyManager.fetchLocalAES128Key(password, user.aesKey))
            {
            	if (localAesKeyManager.getLocalAesKeyCount(this) == 0)
				{
            		ToastUtil.showToast(this, "重新初始化KeyChain");
					localAesKeyManager.addFirstUserKey(user.id);
				}
                localAesKeyManager.initLocalKeys(user.id);
                SharedPreferencesManager.setSharedIntPreferences("currentUserId", user.id, this);
                kexinData.localLogin = true;
                //是数字更新到数据库
                if (Utils.isNumber(password))
				{
                	loginPasswordType = SharedPreferencesManager.DIGIT_PWD;
				}
                else
                {
                	loginPasswordType = SharedPreferencesManager.COMPLEX_PWD;
				}
                //保存当前登录密码类型(数字，复杂密码)
                SharedPreferencesManager.setSharedPreferences(SharedPreferencesManager.IsDigit_login_password_type, loginPasswordType, this);
                return true;
            }
        }
        return false;
    }

    /**
     * 保存登录结果
     * 
     * @param isLoginSuccess
     *            true insert success history, password set null false insert
     *            failed history
     */
    private void saveLoginLog(boolean isLoginSuccess, String password)
    {
        //清除错误登录次数
        if (null != locationHelper)
        {
            //多次登录（失败)后，清除上次的update
            locationHelper.removeLocationUpdate();
        }
        long backId = 0;
        // 登录成功时， password set null
        if (isLoginSuccess)
        {
            LoginSuccessLog loginSuccessLog = new LoginSuccessLog();
            loginSuccessLog.loginTime = DateUtil.getStringDate();
            loginSuccessLog.messageSendNum = 0;
            loginSuccessLog.photoSendNum = 0;
            loginSuccessLog.phoneCallTime = 0;
            loginSuccessLog.userFaceImage = strImgPath;
            if(null != kexinData.getUserInfo())
            {
            	loginSuccessLog.isMainPassword = kexinData.getUserInfo().isMainPassword;
            }
            else
            {
            	loginSuccessLog.isMainPassword = 1;
            	CMTracer.e(TAG, "Can't get userInfo");
            }
            loginSuccessLog.authorityId = kexinData.getCurrentAuthorityId();
            //loginSuccessLog.location = getCurrentLocation();

            backId = LoginSuccessLogTableOperation.saveLog(loginSuccessLog, this);
            if(!ChannelUtil.isFromCNCountry(this))
            {
                locationHelper = new LocationHelper(backId,true);
                locationHelper.getCurrentLocation();
            }
            else
            {
            	KexinApp kexinApp = (KexinApp) this.getApplication();
            	if (kexinApp!=null&&kexinApp.BDMap!=null)
				{
            		kexinApp.BDMap.getCurrentLocation(backId, true);
				}
            }
            // 保存到kexinData里面 ，以便用随时更新数据至数据库
            loginSuccessLog.id = backId;
            kexinData.setLoginSuccessLog(loginSuccessLog);
            
            CMTracer.i(TAG, "CurrentAuthorityId = " + loginSuccessLog.authorityId);
        }
        else
        {
        	//如果打开邮箱入侵警告，调用接口，发送入侵邮件,需修改此接口
            if (security.emailIntruder)
    		{
                //向邮箱发送入侵警告。
				if(checkConnectServer())//需要连接server
			    {											
					CMTracer.i(TAG, "connectToServer");
			        //等与服务器连接上后再调sendPhone
					intruderWait = true;
                    connectToServer();
			    }
				else
				{
	                IntruderUtil.sendIntruderEmail(this, inputPassword, strImgPath);
				}
    		}
        	
            LoginFailedLog loginFailedLog = new LoginFailedLog();
            loginFailedLog.loginTime = DateUtil.getStringDate();
            loginFailedLog.failedPassword = password;
            loginFailedLog.userFaceImage = strImgPath;
            //loginFailedLog.location = getCurrentLocation();

            backId = LoginFailedLogTableOperation.saveLog(loginFailedLog, this);
            if (!ChannelUtil.isFromCNCountry(this))
            {
                locationHelper = new LocationHelper(backId, false);
                locationHelper.getCurrentLocation();
            }
            else
            {
            	KexinApp kexinApp = (KexinApp) this.getApplication();
            	if (kexinApp!=null&&kexinApp.BDMap!=null)
            	{
            		kexinApp.BDMap.getCurrentLocation(backId, false);
            	}
            }
        }
    }


    /********************************************* 新增照相回调 *************************************************/

    public class SurfaceCallback implements SurfaceHolder.Callback
    {

        public void surfaceCreated(SurfaceHolder holder)
        {
            CMTracer.i(TAG, "surfaceCallback====");
            initCam = false;
            CameraUtil.asynOpenCamera(holder, mHandler, hasSdcard);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            CMTracer.i("Camera", "camera----------------surfaceChanged");
//          initCam = CameraUtil.SetParameters(holder);
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
        	if (!initCam)  
                return;
            CMTracer.i("Camera", "camera----------------surfaceDestroyed");
            holder.removeCallback(this);
            CameraUtil.exit();
            initCam = false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
            // showDialog();

            Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(mHomeIntent);
        }
        return super.dispatchKeyEvent(event);
    }
    
    private RecyclingBitmapDrawable getBgDrawable(String path)
    {
    	Bitmap bm = null;
		Display display = this.getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        
        Size srcSize = new Size(options.outWidth, options.outHeight);
        Size dstSize = new Size(screenWidth, screenHeight);
        Size newSize = ImageUtil.getFitinSize(srcSize, dstSize);
        int sample = 1;
		if(newSize.mHeight ==  options.outHeight)
			sample = 1;				
		else//get small ratio
		{
			float ratio = newSize.mHeight * 1.0f / options.outHeight;
			while(((sample<<=1) * ratio) < 1)
				;
		}
    //    s = BitmapUtil.getBigPicScale(options.outWidth, options.outHeight, screenWidth, screenHeight);

        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;
        options.inSampleSize = sample;
        bm = BitmapFactory.decodeFile(path, options);
        if(bm == null)
        {
        	bm = BitmapFactory.decodeResource(getResources(),
                    R.drawable.nophoto);
        }
        
        if (null != bm)
        {

        	RecyclingBitmapDrawable visibledrawable = new RecyclingBitmapDrawable(
                    null, bm);
        	return visibledrawable;
        }
        else
        {
        	return null;
        }
    }
    
    private String[] getScreenPicList(String ducketId)
    {
        String orderBy = MediaStore.Images.Media._ID + " desc";
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.BUCKET_ID + " = " + " '" + ducketId
                        + "'", null, orderBy);
        int i = 0;
        String[] pathList;
   //     visibleAlbumDataList.clear();
        // 用这种方式(Set)来保证唯一性
        if(cursor != null)
        {
        	pathList = new String[32];
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
            	pathList[i] = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            	i++;
            	if (i>=32)
            	{
            		break;
            	}
            }
            cursor.close();
            return pathList;
        }
        else
        {
        	return null;
        }
    }
    
    private void setGalleryAdapter()
    {
    	int[] imagesResid;
        String[] imagesPath = null;
        Display display = this.getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        
//    	if(PremiumUtil.isPremiumFeaturesPurchased()) //增加是否购买的判断
        {
    		ArrayList<LockScreenData> imageDataList = LockScreenTableOperation.getAllLockScreenData(this);
    		imagesResid = new int[imageDataList.size()];
            if (imageDataList != null && imageDataList.size()>0 && imageDataList.get(0).picType == 1)
            {
            	if(imageDataList.get(0).url == null)
            	{
            		LockScreenTableOperation.deleteAllImage(this);
            		LockScreenTableOperation.restoreDefault(this);
            		imageDataList = LockScreenTableOperation.getAllLockScreenData(this);
            	}
            	
            	
            	imagesPath = new String[imageDataList.size()];
            	for(int i = 0; i < imageDataList.size(); i++)
                {
                	if(imageDataList.get(i).picType == 1)
                	{
                	//	imagesResid[i] = imageDataList.get(i).resId;
                		
                		imagesPath[i] = imageDataList.get(i).url;
                		if(imagesPath[i].equalsIgnoreCase("R.drawable.lockout_1"))
                		{
                			imagesResid[i] = R.drawable.lockout_1;
                		}
                		else if(imagesPath[i].equalsIgnoreCase("R.drawable.lockout_2"))
                		{
                			imagesResid[i] = R.drawable.lockout_2;
                		}
                		else if(imagesPath[i].equalsIgnoreCase("R.drawable.lockout_3"))
                		{
                			imagesResid[i] = R.drawable.lockout_3;
                		}
                		else if(imagesPath[i].equalsIgnoreCase("R.drawable.lockout_4"))
                		{
                			imagesResid[i] = R.drawable.lockout_4;
                		}
                		else if(imagesPath[i].equalsIgnoreCase("R.drawable.lockout_5"))
                		{
                			imagesResid[i] = R.drawable.lockout_5;
                		}
                		else if(imagesPath[i].equalsIgnoreCase("R.drawable.lockout_6"))
                		{
                			imagesResid[i] = R.drawable.lockout_6;
                		}
                	}
                }
            }
            else if(imageDataList != null && imageDataList.size()>0 && (imageDataList.get(0).picType == 2 ||imageDataList.get(0).picType == 4))
            {
            	imagesPath = new String[imageDataList.size()];
            	for(int i = 0; i < imageDataList.size(); i++)
                {
            		if(imageDataList.get(i).picType == 2 || imageDataList.get(i).picType == 4)
            		{
            			imagesPath[i] = imageDataList.get(i).url;
            		}
                }
            }
            else
            {
            	if (imageDataList != null && imageDataList.size()>0)
            		imagesPath = getScreenPicList(imageDataList.get(0).title);
            }
            
            if(imageDataList!=null && imageDataList.size()>0)
            {
                lockoutAdapter = new LockoutImageAdapter(imagesResid, imagesPath, 
                		this, imageDataList.get(0).picType, screenWidth, screenHeight);
            }
            else
            {
            	imagesResid = new int[3];
            	imagesResid[0] = R.drawable.lockout_2;
            	imagesResid[1] = R.drawable.lockout_3;
            	imagesResid[2] = R.drawable.lockout_6;
            	lockoutAdapter = new LockoutImageAdapter(imagesResid, null, this, 1, screenWidth, screenHeight);
				
			}
        }
    	
        gallery.setAdapter(lockoutAdapter);
        
        if(lockoutAdapter.getCount() > 1)
        {
        	if(imageIndex >= 0)
        	{
        		gallery.setSelection(imageIndex);
        	}
        	else
        	{
        		gallery.setSelection(1); // 从第二张图片开始加载
        	}
        }
    }
    private void connectToServer()
    {
    	KexinData.getInstance().connectStatus =Enums.enum_connect_status_ping;
        PingRespond res = client.Ping(Enums.enum_suggest_ping_timeout_for_firsttime);
        if (res.errorCode == 0)
        {
        	KexinData.getInstance().connectStatus = Enums.enum_connect_status_connectting;
        	client.Connect(res.bestServerPing, res.nPort);
        }
        else if (res.errorCode == -1 || res.errorCode == -2)
        {
//        	showNetWorkErrDlg();
        }
    }
    
	private boolean checkConnectServer()
	{
		return !kexinData.isOnline && kexinData.connectStatus == Enums.enum_connect_status_unconnect;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}
	@Override
	public void onShake() {
		if(!AppInstalledUtil.isCmnApp(this))
    	{
    		boolean isInResettingSuperPassword = SettingTableOperation.getBooleanSetting(SharedPreferencesManager.AccountDataType_checkReSettingSuperPassword,SignInActivity.this);
        	if (isInResettingSuperPassword)
			{
            	 Intent intent = new Intent(SignInActivity.this,ModifySuperPasswordConfirmEmailActivity.class);
            	 startActivity(intent);
            	 finish();
			}
        	 else 
        	{
             	showPswRelativeLayout();
                if(shakeListener.isStart && !AppInstalledUtil.isCmnApp(this))//停止检测
                {
                    shakeListener.stop();
 					CMTracer.i("ShakeListener", "SignInActivity shakeListener.stop()");
                }
			}
    	}
	}
}
