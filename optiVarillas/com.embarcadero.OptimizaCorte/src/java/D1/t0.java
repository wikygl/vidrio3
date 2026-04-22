package D1;

import A1.C0124p;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebSettings;
import com.google.android.gms.internal.ads.AX;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.M0;
import com.google.android.gms.internal.ads.Mi;
import com.google.android.gms.internal.ads.NK;
import com.google.android.gms.internal.ads.SK;
import com.google.android.gms.internal.ads.Vl;
import com.google.android.gms.internal.ads.WJ;
import com.google.android.gms.internal.ads.Yb;
import com.google.android.gms.internal.ads.eL;
import com.google.android.gms.internal.ads.fL;
import com.google.android.gms.internal.ads.mG;
import com.google.android.gms.internal.ads.pG;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.vm;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.zb;
import com.google.android.gms.internal.ads.zv;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import w1.C0846j;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class t0 {

    /* renamed from: l  reason: collision with root package name */
    public static final C0185e0 f774l = new WJ(Looper.getMainLooper());

    /* renamed from: g  reason: collision with root package name */
    public String f780g;

    /* renamed from: h  reason: collision with root package name */
    public volatile String f781h;

    /* renamed from: a  reason: collision with root package name */
    public final AtomicReference f775a = new AtomicReference(null);

    /* renamed from: b  reason: collision with root package name */
    public final AtomicReference f776b = new AtomicReference(null);

    /* renamed from: c  reason: collision with root package name */
    public final AtomicReference f777c = new AtomicReference(new Bundle());

    /* renamed from: d  reason: collision with root package name */
    public final AtomicBoolean f778d = new AtomicBoolean();

    /* renamed from: e  reason: collision with root package name */
    public boolean f779e = true;
    public final Object f = new Object();

    /* renamed from: i  reason: collision with root package name */
    public boolean f782i = false;

    /* renamed from: j  reason: collision with root package name */
    public boolean f783j = false;

    /* renamed from: k  reason: collision with root package name */
    public final ExecutorService f784k = Executors.newSingleThreadExecutor();

    public static int B(Context context, Uri uri) {
        int i4;
        String a4;
        if (context == null) {
            C0183d0.k("Trying to open chrome custom tab on a null context");
            return 3;
        }
        if (!(context instanceof Activity)) {
            C0183d0.k("Chrome Custom Tabs can only work with Activity context.");
            i4 = 2;
        } else {
            i4 = 0;
        }
        vb vbVar = Gb.Y3;
        A1.r rVar = A1.r.f168d;
        vb vbVar2 = Gb.Z3;
        Eb eb = rVar.f171c;
        if (true == ((Boolean) rVar.f171c.a(vbVar)).equals(eb.a(vbVar2))) {
            i4 = 9;
        }
        if (i4 != 0) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(uri);
            intent.addFlags(268435456);
            context.startActivity(intent);
            return i4;
        }
        if (((Boolean) eb.a(vbVar)).booleanValue()) {
            Yb yb = new Yb();
            yb.d = new o0(yb, context, uri);
            Activity activity = (Activity) context;
            if (yb.b == null && (a4 = SK.a(activity)) != null) {
                AX ax = new AX(yb);
                yb.c = ax;
                ax.f5404a = activity.getApplicationContext();
                Intent intent2 = new Intent("android.support.customtabs.action.CustomTabsService");
                if (!TextUtils.isEmpty(a4)) {
                    intent2.setPackage(a4);
                }
                activity.bindService(intent2, ax, 33);
            }
        }
        if (((Boolean) eb.a(vbVar2)).booleanValue()) {
            Intent intent3 = new Intent("android.intent.action.VIEW");
            if (!intent3.hasExtra("android.support.customtabs.extra.SESSION")) {
                Bundle bundle = new Bundle();
                bundle.putBinder("android.support.customtabs.extra.SESSION", null);
                intent3.putExtras(bundle);
            }
            intent3.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", true);
            intent3.putExtras(new Bundle());
            intent3.putExtra("androidx.browser.customtabs.extra.SHARE_STATE", 0);
            intent3.setPackage(SK.a(context));
            intent3.setData(uri);
            context.startActivity(intent3, null);
            return 5;
        }
        return 5;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0016 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0017  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final boolean C(android.view.View r2) {
        /*
            android.view.View r2 = r2.getRootView()
            r0 = 0
            if (r2 != 0) goto L9
        L7:
            r2 = r0
            goto L13
        L9:
            android.content.Context r2 = r2.getContext()
            boolean r1 = r2 instanceof android.app.Activity
            if (r1 == 0) goto L7
            android.app.Activity r2 = (android.app.Activity) r2
        L13:
            r1 = 0
            if (r2 != 0) goto L17
            return r1
        L17:
            android.view.Window r2 = r2.getWindow()
            if (r2 != 0) goto L1e
            goto L22
        L1e:
            android.view.WindowManager$LayoutParams r0 = r2.getAttributes()
        L22:
            if (r0 == 0) goto L2d
            int r2 = r0.flags
            r0 = 524288(0x80000, float:7.34684E-40)
            r2 = r2 & r0
            if (r2 == 0) goto L2d
            r2 = 1
            return r2
        L2d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: D1.t0.C(android.view.View):boolean");
    }

    public static final void D(Context context, Intent intent) {
        Bundle bundle;
        if (intent == null) {
            return;
        }
        if (intent.getExtras() != null) {
            bundle = intent.getExtras();
        } else {
            bundle = new Bundle();
        }
        bundle.putBinder("android.support.customtabs.extra.SESSION", null);
        bundle.putString("com.android.browser.application_id", context.getPackageName());
        intent.putExtras(bundle);
    }

    public static final String E(Context context) {
        if (context.getApplicationContext() != null) {
            context = context.getApplicationContext();
        }
        return s(r(context));
    }

    public static final String F() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("Mozilla/5.0 (Linux; U; Android");
        String str = Build.VERSION.RELEASE;
        if (str != null) {
            sb.append(" ");
            sb.append(str);
        }
        sb.append("; ");
        sb.append(Locale.getDefault());
        String str2 = Build.DEVICE;
        if (str2 != null) {
            sb.append("; ");
            sb.append(str2);
            String str3 = Build.DISPLAY;
            if (str3 != null) {
                sb.append(" Build/");
                sb.append(str3);
            }
        }
        sb.append(") AppleWebKit/533 Version/4.0 Safari/533");
        return sb.toString();
    }

    public static final String G() {
        String str = Build.MANUFACTURER;
        String str2 = Build.MODEL;
        if (str2.startsWith(str)) {
            return str2;
        }
        return X1.b.e(str, " ", str2);
    }

    public static final HashMap H(String str) {
        HashMap hashMap = new HashMap();
        try {
            JSONObject jSONObject = new JSONObject(str);
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                HashSet hashSet = new HashSet();
                JSONArray optJSONArray = jSONObject.optJSONArray(next);
                if (optJSONArray != null) {
                    for (int i4 = 0; i4 < optJSONArray.length(); i4++) {
                        String optString = optJSONArray.optString(i4);
                        if (optString != null) {
                            hashSet.add(optString);
                        }
                    }
                    hashMap.put(next, hashSet);
                }
            }
            return hashMap;
        } catch (JSONException e4) {
            z1.p.f6575A.f6581g.h("AdUtil.getMapOfFileNamesToKeysFromJsonString", e4);
            return hashMap;
        }
    }

    public static final long I(View view) {
        float f;
        int i4;
        float f4 = Float.MAX_VALUE;
        ViewParent viewParent = view;
        do {
            f = 0.0f;
            if (!(viewParent instanceof View)) {
                break;
            }
            View view2 = viewParent;
            f4 = Math.min(f4, view2.getAlpha());
            i4 = (f4 > 0.0f ? 1 : (f4 == 0.0f ? 0 : -1));
            viewParent = view2.getParent();
        } while (i4 > 0);
        if (f4 >= 0.0f) {
            f = f4;
        }
        return Math.round(f * 100.0f);
    }

    public static final J J(Context context) {
        try {
            Object newInstance = context.getClassLoader().loadClass("com.google.android.gms.ads.internal.util.WorkManagerUtil").getDeclaredConstructor(null).newInstance(null);
            if (!(newInstance instanceof IBinder)) {
                E1.m.d("Instantiated WorkManagerUtil not instance of IBinder.");
                return null;
            }
            IBinder iBinder = (IBinder) newInstance;
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.util.IWorkManagerUtil");
            if (queryLocalInterface instanceof J) {
                return (J) queryLocalInterface;
            }
            return new x8(iBinder, "com.google.android.gms.ads.internal.util.IWorkManagerUtil");
        } catch (Exception e4) {
            z1.p.f6575A.f6581g.h("Failed to instantiate WorkManagerUtil", e4);
            return null;
        }
    }

    public static final boolean a(Context context, String str) {
        Context a4 = Mi.a(context);
        if (b2.c.a(a4).f2924a.getPackageManager().checkPermission(str, a4.getPackageName()) == 0) {
            return true;
        }
        return false;
    }

    public static final boolean b(Context context) {
        boolean z4;
        try {
            if (a2.d.f == null) {
                if (a2.g.b() && context.getPackageManager().hasSystemFeature("com.google.android.play.feature.HPE_EXPERIENCE")) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                a2.d.f = Boolean.valueOf(z4);
            }
            return a2.d.f.booleanValue();
        } catch (NoSuchMethodError unused) {
            return false;
        }
    }

    public static final boolean c(String str) {
        if (!E1.l.c()) {
            return false;
        }
        vb vbVar = Gb.r4;
        A1.r rVar = A1.r.f168d;
        if (!((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            return false;
        }
        String str2 = (String) rVar.f171c.a(Gb.t4);
        if (!str2.isEmpty()) {
            for (String str3 : str2.split(";")) {
                if (str3.equals(str)) {
                    return false;
                }
            }
        }
        String str4 = (String) A1.r.f168d.f171c.a(Gb.s4);
        if (str4.isEmpty()) {
            return true;
        }
        for (String str5 : str4.split(";")) {
            if (str5.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean d(Context context) {
        try {
            context.getClassLoader().loadClass("com.google.android.gms.ads.internal.ClientApi");
            return false;
        } catch (ClassNotFoundException unused) {
            return true;
        } catch (Throwable th) {
            E1.m.e("Error loading class.", th);
            z1.p.f6575A.f6581g.h("AdUtil.isLiteSdk", th);
            return false;
        }
    }

    public static final boolean e(Context context) {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        PowerManager powerManager;
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
            if (activityManager == null || keyguardManager == null || (runningAppProcesses = activityManager.getRunningAppProcesses()) == null) {
                return false;
            }
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (Process.myPid() == runningAppProcessInfo.pid) {
                    if (runningAppProcessInfo.importance == 100 && !keyguardManager.inKeyguardRestrictedInputMode() && (powerManager = (PowerManager) context.getSystemService("power")) != null) {
                        if (powerManager.isScreenOn()) {
                            return false;
                        }
                        return true;
                    }
                    return true;
                }
            }
            return true;
        } catch (Throwable unused) {
        }
        return false;
    }

    public static final boolean f(Context context) {
        try {
            Bundle r4 = r(context);
            String string = r4.getString("com.google.android.gms.ads.INTEGRATION_MANAGER");
            if (TextUtils.isEmpty(s(r4))) {
                if (!TextUtils.isEmpty(string)) {
                    return true;
                }
            }
        } catch (RemoteException unused) {
        }
        return false;
    }

    public static final boolean g(Context context) {
        Window window;
        if ((context instanceof Activity) && (window = ((Activity) context).getWindow()) != null && window.getDecorView() != null) {
            Rect rect = new Rect();
            Rect rect2 = new Rect();
            window.getDecorView().getGlobalVisibleRect(rect, null);
            window.getDecorView().getWindowVisibleDisplayFrame(rect2);
            if (rect.bottom != 0 && rect2.bottom != 0 && rect.top == rect2.top) {
                return true;
            }
        }
        return false;
    }

    public static final void h(View view, int i4) {
        String str;
        int i5;
        int i6;
        int i7;
        String str2;
        mG s4;
        pG G4;
        View view2 = view;
        int[] iArr = new int[2];
        Rect rect = new Rect();
        try {
            String packageName = view.getContext().getPackageName();
            if (view2 instanceof zv) {
                view2 = ((zv) view2).getChildAt(0);
            }
            if (!(view2 instanceof C0846j) && !(view2 instanceof J1.e)) {
                str = "UNKNOWN";
                i5 = 0;
            } else {
                str = "NATIVE";
                i5 = 1;
            }
            if (view2.getLocalVisibleRect(rect)) {
                i7 = rect.width();
                i6 = rect.height();
            } else {
                i6 = 0;
                i7 = 0;
            }
            t0 t0Var = z1.p.f6575A.f6578c;
            long I2 = I(view2);
            view2.getLocationOnScreen(iArr);
            int i8 = iArr[0];
            int i9 = iArr[1];
            String str3 = "none";
            if (!(view2 instanceof vm) || (G4 = ((vm) view2).G()) == null) {
                str2 = "none";
            } else {
                str2 = G4.b;
                int hashCode = view2.hashCode();
                view2.setContentDescription(str2 + ":" + hashCode);
            }
            if ((view2 instanceof Vl) && (s4 = ((Vl) view2).s()) != null) {
                str = mG.a(s4.b);
                i5 = s4.e;
                str3 = s4.E;
            }
            Locale locale = Locale.US;
            int hashCode2 = view2.hashCode();
            String name = view2.getClass().getName();
            int width = view2.getWidth();
            int height = view2.getHeight();
            String num = Integer.toString(i4, 2);
            E1.m.f("<Ad hashCode=" + hashCode2 + ", package=" + packageName + ", adNetCls=" + str3 + ", gwsQueryId=" + str2 + ", format=" + str + ", impType=" + i5 + ", class=" + name + ", x=" + i8 + ", y=" + i9 + ", width=" + width + ", height=" + height + ", vWidth=" + i7 + ", vHeight=" + i6 + ", alpha=" + I2 + ", state=" + num + ">");
        } catch (Exception e4) {
            E1.m.e("Failure getting view location.", e4);
        }
    }

    public static final AlertDialog.Builder i(Context context) {
        u0 u0Var = z1.p.f6575A.f6580e;
        return new AlertDialog.Builder(context, 16974374);
    }

    public static final void j(Context context, String str, String str2) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str2);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            new O(context, str, (String) it.next()).b();
        }
    }

    public static final int k(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e4) {
            E1.m.g("Could not parse value:".concat(e4.toString()));
            return 0;
        }
    }

    public static final HashMap l(Uri uri) {
        if (uri == null) {
            return null;
        }
        HashMap hashMap = new HashMap();
        for (String str : uri.getQueryParameterNames()) {
            if (!TextUtils.isEmpty(str)) {
                hashMap.put(str, uri.getQueryParameter(str));
            }
        }
        return hashMap;
    }

    public static final int[] m(Activity activity) {
        View findViewById;
        Window window = activity.getWindow();
        if (window != null && (findViewById = window.findViewById(16908290)) != null) {
            return new int[]{findViewById.getWidth(), findViewById.getHeight()};
        }
        return new int[]{0, 0};
    }

    public static final int[] n(Activity activity) {
        View findViewById;
        Window window = activity.getWindow();
        int[] iArr = (window == null || (findViewById = window.findViewById(16908290)) == null) ? new int[]{0, 0} : new int[]{findViewById.getTop(), findViewById.getBottom()};
        C0124p c0124p = C0124p.f;
        return new int[]{c0124p.f161a.e(activity, iArr[0]), c0124p.f161a.e(activity, iArr[1])};
    }

    public static final boolean o(View view, PowerManager powerManager, KeyguardManager keyguardManager) {
        boolean z4;
        if (z1.p.f6575A.f6578c.f779e || keyguardManager == null || !keyguardManager.inKeyguardRestrictedInputMode() || C(view)) {
            z4 = true;
        } else {
            z4 = false;
        }
        long I2 = I(view);
        if (view.getVisibility() == 0 && view.isShown() && ((powerManager == null || powerManager.isScreenOn()) && z4)) {
            vb vbVar = Gb.b1;
            A1.r rVar = A1.r.f168d;
            if (!((Boolean) rVar.f171c.a(vbVar)).booleanValue() || view.getLocalVisibleRect(new Rect()) || view.getGlobalVisibleRect(new Rect())) {
                vb vbVar2 = Gb.q9;
                Eb eb = rVar.f171c;
                if (!((Boolean) eb.a(vbVar2)).booleanValue() || I2 >= ((Integer) eb.a(Gb.s9)).intValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final void p(Context context, Intent intent) {
        if (((Boolean) A1.r.f168d.f171c.a(Gb.K9)).booleanValue()) {
            try {
                try {
                    context.startActivity(intent);
                    return;
                } catch (Throwable unused) {
                    intent.addFlags(268435456);
                    context.startActivity(intent);
                    return;
                }
            } catch (SecurityException e4) {
                E1.m.h("", e4);
                z1.p.f6575A.f6581g.h("AdUtil.startActivityWithUnknownContext", e4);
                return;
            }
        }
        try {
            context.startActivity(intent);
        } catch (Throwable unused2) {
            intent.addFlags(268435456);
            context.startActivity(intent);
        }
    }

    public static final void q(Context context, Uri uri) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            D(context, intent);
            bundle.putString("com.android.browser.application_id", context.getPackageName());
            context.startActivity(intent);
            String uri2 = uri.toString();
            E1.m.b("Opening " + uri2 + " in a new browser.");
        } catch (ActivityNotFoundException e4) {
            E1.m.e("No browser is found.", e4);
        }
    }

    public static Bundle r(Context context) {
        try {
            return b2.c.a(context).a(context.getPackageName(), 128).metaData;
        } catch (PackageManager.NameNotFoundException | NullPointerException e4) {
            C0183d0.l("Error getting metadata", e4);
            return null;
        }
    }

    public static String s(Bundle bundle) {
        if (bundle != null) {
            String string = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            if (!TextUtils.isEmpty(string)) {
                if (string.matches("^ca-app-pub-[0-9]{16}~[0-9]{10}$") || string.matches("^/\\d+~.+$")) {
                    return string;
                }
                return "";
            }
            return "";
        }
        return "";
    }

    public static int t(int i4) {
        if (i4 >= 5000) {
            return i4;
        }
        if (i4 > 0) {
            E1.m.g("HTTP timeout too low: " + i4 + " milliseconds. Reverting to default timeout: 60000 milliseconds.");
            return 60000;
        }
        return 60000;
    }

    public static boolean u(String str, AtomicReference atomicReference, String str2) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            Pattern pattern = (Pattern) atomicReference.get();
            if (pattern == null || !str2.equals(pattern.pattern())) {
                pattern = Pattern.compile(str2);
                atomicReference.set(pattern);
            }
            return pattern.matcher(str).matches();
        } catch (PatternSyntaxException unused) {
            return false;
        }
    }

    /* JADX WARN: Type inference failed for: r1v6, types: [D1.X, java.lang.Object] */
    public static final String v(final Context context, String str) {
        final Context context2;
        if (str != null) {
            String str2 = null;
            try {
                if (X.f657b == null) {
                    X.f657b = new Object();
                }
                X x4 = X.f657b;
                if (TextUtils.isEmpty(x4.f658a)) {
                    AtomicBoolean atomicBoolean = T1.i.f2356a;
                    try {
                        context2 = context.createPackageContext("com.google.android.gms", 3);
                    } catch (PackageManager.NameNotFoundException unused) {
                        context2 = null;
                    }
                    x4.f658a = (String) V.a(context, new Callable() { // from class: D1.W
                        @Override // java.util.concurrent.Callable
                        public final Object call() {
                            SharedPreferences sharedPreferences;
                            boolean z4 = false;
                            Context context3 = context2;
                            Context context4 = context;
                            if (context3 != null) {
                                C0183d0.k("Attempting to read user agent from Google Play Services.");
                                sharedPreferences = context3.getSharedPreferences("admob_user_agent", 0);
                            } else {
                                C0183d0.k("Attempting to read user agent from local cache.");
                                sharedPreferences = context4.getSharedPreferences("admob_user_agent", 0);
                                z4 = true;
                            }
                            String string = sharedPreferences.getString("user_agent", "");
                            if (TextUtils.isEmpty(string)) {
                                C0183d0.k("Reading user agent from WebSettings");
                                string = WebSettings.getDefaultUserAgent(context4);
                                if (z4) {
                                    sharedPreferences.edit().putString("user_agent", string).apply();
                                    C0183d0.k("Persisting user agent.");
                                }
                            }
                            return string;
                        }
                    });
                }
                str2 = x4.f658a;
            } catch (Exception unused2) {
            }
            if (TextUtils.isEmpty(str2)) {
                str2 = WebSettings.getDefaultUserAgent(context);
            }
            if (TextUtils.isEmpty(str2)) {
                str2 = F();
            }
            String e4 = X1.b.e(str2, " (Mobile; ", str);
            try {
                if (b2.c.a(context).c()) {
                    e4 = e4 + ";aia";
                }
            } catch (Exception e5) {
                z1.p.f6575A.f6581g.h("AdUtil.getUserAgent", e5);
            }
            return e4.concat(")");
        }
        return F();
    }

    public static ArrayList x() {
        zb zbVar = Gb.a;
        ArrayList b4 = A1.r.f168d.f169a.b();
        ArrayList arrayList = new ArrayList();
        Iterator it = b4.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            M0 e4 = M0.e(new NK(','));
            str.getClass();
            eL e5 = ((fL) e4.k).e(e4, str);
            while (true) {
                eL eLVar = e5;
                if (eLVar.hasNext()) {
                    try {
                        arrayList.add(Long.valueOf((String) eLVar.next()));
                    } catch (NumberFormatException unused) {
                        C0183d0.k("Experiment ID is not a number");
                    }
                }
            }
        }
        return arrayList;
    }

    @SuppressLint({"UnprotectedReceiver"})
    public final void A(Context context) {
        if (this.f782i) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        Gb.a(context);
        if (((Boolean) A1.r.f168d.f171c.a(Gb.J9)).booleanValue() && Build.VERSION.SDK_INT >= 33) {
            context.getApplicationContext().registerReceiver(new q0(this), intentFilter, 4);
        } else {
            context.getApplicationContext().registerReceiver(new q0(this), intentFilter);
        }
        this.f782i = true;
    }

    public final String w(Context context, String str) {
        if (((Boolean) A1.r.f168d.f171c.a(Gb.X9)).booleanValue()) {
            if (this.f781h != null) {
                return this.f781h;
            }
            this.f781h = v(context, str);
            return this.f781h;
        }
        synchronized (this.f) {
            try {
                String str2 = this.f780g;
                if (str2 != null) {
                    return str2;
                }
                String v4 = v(context, str);
                this.f780g = v4;
                return v4;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void y(Context context, String str, HttpURLConnection httpURLConnection, int i4) {
        int t3 = t(i4);
        E1.m.f("HTTP timeout: " + t3 + " milliseconds.");
        httpURLConnection.setConnectTimeout(t3);
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.setReadTimeout(t3);
        if (TextUtils.isEmpty(httpURLConnection.getRequestProperty("User-Agent"))) {
            httpURLConnection.setRequestProperty("User-Agent", w(context, str));
        }
        httpURLConnection.setUseCaches(false);
    }

    /* JADX WARN: Type inference failed for: r1v7, types: [D1.p0, android.content.BroadcastReceiver] */
    @SuppressLint({"UnprotectedReceiver"})
    public final void z(Context context) {
        if (this.f783j) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.ads.intent.DEBUG_LOGGING_ENABLEMENT_CHANGED");
        Gb.a(context);
        if (((Boolean) A1.r.f168d.f171c.a(Gb.J9)).booleanValue() && Build.VERSION.SDK_INT >= 33) {
            context.getApplicationContext().registerReceiver(new BroadcastReceiver(), intentFilter, 4);
        } else {
            context.getApplicationContext().registerReceiver(new BroadcastReceiver(), intentFilter);
        }
        this.f783j = true;
    }
}
