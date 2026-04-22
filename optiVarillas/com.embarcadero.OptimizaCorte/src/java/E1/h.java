package E1;

import A1.N0;
import A1.P0;
import D1.C0183d0;
import a3.InterfaceFutureC0346a;
import android.app.ActivityManager;
import android.app.Application;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Base64;
import android.util.JsonWriter;
import android.view.WindowManager;
import com.android.billingclient.api.ProxyBillingActivityV2;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import com.google.android.gms.internal.ads.BY;
import com.google.android.gms.internal.ads.Bs;
import com.google.android.gms.internal.ads.Dk;
import com.google.android.gms.internal.ads.EI;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.Ek;
import com.google.android.gms.internal.ads.Fm;
import com.google.android.gms.internal.ads.Gq;
import com.google.android.gms.internal.ads.Kx;
import com.google.android.gms.internal.ads.LE;
import com.google.android.gms.internal.ads.Lb;
import com.google.android.gms.internal.ads.Lx;
import com.google.android.gms.internal.ads.M0;
import com.google.android.gms.internal.ads.MG;
import com.google.android.gms.internal.ads.Ml;
import com.google.android.gms.internal.ads.PF;
import com.google.android.gms.internal.ads.QF;
import com.google.android.gms.internal.ads.RY;
import com.google.android.gms.internal.ads.Ss;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.Ts;
import com.google.android.gms.internal.ads.Ur;
import com.google.android.gms.internal.ads.ZH;
import com.google.android.gms.internal.ads.aL;
import com.google.android.gms.internal.ads.cr;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.fG;
import com.google.android.gms.internal.ads.fL;
import com.google.android.gms.internal.ads.fr;
import com.google.android.gms.internal.ads.hL;
import com.google.android.gms.internal.ads.i7;
import com.google.android.gms.internal.ads.lI;
import com.google.android.gms.internal.ads.mI;
import com.google.android.gms.internal.ads.nI;
import com.google.android.gms.internal.ads.of;
import com.google.android.gms.internal.ads.py;
import com.google.android.gms.internal.ads.qI;
import com.google.android.gms.internal.ads.rY;
import com.google.android.gms.internal.ads.tI;
import com.google.android.gms.internal.ads.vI;
import com.google.android.gms.internal.ads.wn;
import com.google.android.gms.internal.ads.yf;
import com.google.android.gms.internal.ads.zH;
import com.google.android.gms.internal.ads.zk;
import com.google.android.gms.internal.play_billing.u;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class h implements k, androidx.activity.result.b, i7, Dk, Ur, Kx, Ts, Fm, QF, TN, fL, hL, LE {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f865j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f866k;

    public /* synthetic */ h(int i4, Object obj) {
        this.f865j = i4;
        this.f866k = obj;
    }

    public Object a() {
        Object obj = this.f866k;
        switch (this.f865j) {
            case 8:
                ZH zh = Lb.x;
                if (zh.a) {
                    return Boolean.TRUE;
                }
                Context applicationContext = ((Context) obj).getApplicationContext();
                if (applicationContext != null) {
                    if (!zh.a) {
                        zh.a = true;
                        vI a4 = vI.a();
                        a4.getClass();
                        a4.b = new lI(new Handler(), applicationContext, a4);
                        Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = nI.m;
                        boolean z4 = applicationContext instanceof Application;
                        if (z4) {
                            ((Application) applicationContext).registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
                        }
                        Ml.H = (UiModeManager) applicationContext.getSystemService("uimode");
                        WindowManager windowManager = EI.a;
                        EI.c = applicationContext.getResources().getDisplayMetrics().density;
                        EI.a = (WindowManager) applicationContext.getSystemService("window");
                        applicationContext.registerReceiver(new BroadcastReceiver(), new IntentFilter("android.media.action.HDMI_AUDIO_PLUG"));
                        tI.k.j = applicationContext.getApplicationContext();
                        mI mIVar = mI.e;
                        if (!mIVar.b) {
                            Application.ActivityLifecycleCallbacks activityLifecycleCallbacks2 = mIVar.c;
                            activityLifecycleCallbacks2.getClass();
                            if (z4) {
                                ((Application) applicationContext).registerActivityLifecycleCallbacks(activityLifecycleCallbacks2);
                            }
                            ((qI) activityLifecycleCallbacks2).l = mIVar;
                            ((qI) activityLifecycleCallbacks2).j = true;
                            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
                            ActivityManager.getMyMemoryState(runningAppProcessInfo);
                            boolean z5 = runningAppProcessInfo.importance == 100 || activityLifecycleCallbacks2.b();
                            ((qI) activityLifecycleCallbacks2).k = z5;
                            activityLifecycleCallbacks2.a(z5);
                            mIVar.d = ((qI) activityLifecycleCallbacks2).k;
                            mIVar.b = true;
                        }
                    }
                    return Boolean.valueOf(zh.a);
                }
                throw new IllegalArgumentException("Application Context cannot be null");
            default:
                return (BY) obj;
        }
    }

    @Override // E1.k
    public void b(JsonWriter jsonWriter) {
        Object obj = l.f870b;
        jsonWriter.name("params").beginObject();
        byte[] bArr = (byte[]) this.f866k;
        int length = bArr.length;
        String encodeToString = Base64.encodeToString(bArr, 0);
        if (length < 10000) {
            jsonWriter.name("body").value(encodeToString);
        } else {
            String a4 = f.a(encodeToString, "MD5");
            if (a4 != null) {
                jsonWriter.name("bodydigest").value(a4);
            }
        }
        jsonWriter.name("bodylength").value(length);
        jsonWriter.endObject();
    }

    public void c(Object obj) {
        Bundle extras;
        androidx.activity.result.a aVar = (androidx.activity.result.a) obj;
        ProxyBillingActivityV2 proxyBillingActivityV2 = (ProxyBillingActivityV2) this.f866k;
        proxyBillingActivityV2.getClass();
        Intent intent = aVar.k;
        int i4 = u.b(intent, "ProxyBillingActivityV2").a;
        ResultReceiver resultReceiver = proxyBillingActivityV2.D;
        if (resultReceiver != null) {
            if (intent == null) {
                extras = null;
            } else {
                extras = intent.getExtras();
            }
            resultReceiver.send(i4, extras);
        }
        int i5 = aVar.j;
        if (i5 != -1 || i4 != 0) {
            u.e("ProxyBillingActivityV2", "Alternative billing only dialog finished with resultCode " + i5 + " and billing's responseCode: " + i4);
        }
        proxyBillingActivityV2.finish();
    }

    public void d(Object obj) {
        Object obj2 = this.f866k;
        switch (this.f865j) {
            case 4:
                C0183d0.k("Getting a new session for JS Engine.");
                ((zk) ((Ek) ((yf) obj2)).k).b(((of) obj).j());
                return;
            case 5:
                ((fr) obj).e(MG.d(12, ((Ss) obj2).getMessage(), (N0) null));
                return;
            case 6:
                ((Bs) obj).a((K1.o) obj2);
                return;
            default:
                int i4 = rY.T;
                boolean z4 = ((RY) obj2).l;
                ((wn) obj).d();
                return;
        }
    }

    public /* synthetic */ Iterator e(M0 m02, CharSequence charSequence) {
        return new aL(this, m02, charSequence);
    }

    /*  JADX ERROR: Type inference failed with exception
        jadx.core.utils.exceptions.JadxOverflowException: Type update terminated with stack overflow, arg: (r8v28 ?? I:??[int, boolean, short, byte, char])
        	at aa.w.a(SourceFile:83)
        	at aa.w.c(SourceFile:9)
        	at z8.a0.H0(SourceFile:1)
        	at x9.i0.b(SourceFile:51)
        */
    public void f(byte[] r119, byte[] r120) {
        /*
            Method dump skipped, instructions count: 2659
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: E1.h.f(byte[], byte[]):void");
    }

    public void g(Object obj) {
        ((zH) this.f866k).f();
    }

    public Gq h(PF pf) {
        return ((fG) this.f866k).c(pf);
    }

    public void j(boolean z4, Context context, cr crVar) {
        zk zkVar = (zk) this.f866k;
        try {
            P0 p02 = z1.p.f6575A.f6577b;
            P0.d(context, (AdOverlayInfoParcel) zkVar.j.get(), true);
        } catch (Exception unused) {
        }
    }

    public InterfaceFutureC0346a k(Ei ei) {
        return ((py) ((Lx) this.f866k).c.c()).C4(ei, Binder.getCallingUid());
    }

    public /* synthetic */ h(RY ry, int i4) {
        this.f865j = 15;
        this.f866k = ry;
    }

    /* renamed from: a  reason: collision with other method in class */
    public void m1a() {
        ((em) this.f866k).l0();
    }

    public void m(Throwable th) {
    }
}
