a
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
