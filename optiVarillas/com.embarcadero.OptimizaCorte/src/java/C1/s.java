package C1;

import D1.t0;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toolbar;
import c2.InterfaceC0374a;
import com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Fh;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.WJ;
import com.google.android.gms.internal.ads.Wy;
import com.google.android.gms.internal.ads.aI;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.pz;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.wb;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class s extends Fh implements e {

    /* renamed from: G  reason: collision with root package name */
    public static final int f385G = Color.argb(0, 0, 0, 0);

    /* renamed from: A  reason: collision with root package name */
    public boolean f386A;

    /* renamed from: E  reason: collision with root package name */
    public Toolbar f390E;

    /* renamed from: k  reason: collision with root package name */
    public final Activity f392k;

    /* renamed from: l  reason: collision with root package name */
    public AdOverlayInfoParcel f393l;

    /* renamed from: m  reason: collision with root package name */
    public em f394m;

    /* renamed from: n  reason: collision with root package name */
    public o f395n;

    /* renamed from: o  reason: collision with root package name */
    public x f396o;

    /* renamed from: q  reason: collision with root package name */
    public FrameLayout f398q;

    /* renamed from: r  reason: collision with root package name */
    public WebChromeClient.CustomViewCallback f399r;

    /* renamed from: u  reason: collision with root package name */
    public n f402u;

    /* renamed from: y  reason: collision with root package name */
    public j f406y;

    /* renamed from: z  reason: collision with root package name */
    public boolean f407z;

    /* renamed from: p  reason: collision with root package name */
    public boolean f397p = false;

    /* renamed from: s  reason: collision with root package name */
    public boolean f400s = false;

    /* renamed from: t  reason: collision with root package name */
    public boolean f401t = false;

    /* renamed from: v  reason: collision with root package name */
    public boolean f403v = false;

    /* renamed from: F  reason: collision with root package name */
    public int f391F = 1;

    /* renamed from: w  reason: collision with root package name */
    public final Object f404w = new Object();

    /* renamed from: x  reason: collision with root package name */
    public final l f405x = new l(this);

    /* renamed from: B  reason: collision with root package name */
    public boolean f387B = false;

    /* renamed from: C  reason: collision with root package name */
    public boolean f388C = false;

    /* renamed from: D  reason: collision with root package name */
    public boolean f389D = true;

    public s(Activity activity) {
        this.f392k = activity;
    }

    public final void C4(int i4) {
        int i5;
        Activity activity = this.f392k;
        int i6 = activity.getApplicationInfo().targetSdkVersion;
        wb wbVar = Gb.l5;
        A1.r rVar = A1.r.f168d;
        if (i6 >= ((Integer) rVar.f171c.a(wbVar)).intValue()) {
            int i7 = activity.getApplicationInfo().targetSdkVersion;
            wb wbVar2 = Gb.m5;
            Eb eb = rVar.f171c;
            if (i7 <= ((Integer) eb.a(wbVar2)).intValue() && (i5 = Build.VERSION.SDK_INT) >= ((Integer) eb.a(Gb.n5)).intValue() && i5 <= ((Integer) eb.a(Gb.o5)).intValue()) {
                return;
            }
        }
        try {
            activity.setRequestedOrientation(i4);
        } catch (Throwable th) {
            z1.p.f6575A.f6581g.g("AdOverlay.setRequestedOrientation", th);
        }
    }

    public final void D() {
        if (((Boolean) A1.r.f168d.f171c.a(Gb.l4)).booleanValue()) {
            em emVar = this.f394m;
            if (emVar != null && !emVar.a0()) {
                this.f394m.onResume();
            } else {
                E1.m.g("The webview does not exist. Ignoring action.");
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0272  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0281  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x009d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0085  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0167  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0176  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x018e  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01af  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void D4(boolean r28) {
        /*
            Method dump skipped, instructions count: 696
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: C1.s.D4(boolean):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0031, code lost:
        if (((java.lang.Boolean) A1.r.f168d.f171c.a(com.google.android.gms.internal.ads.Gb.w0)).booleanValue() != false) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0034, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0048, code lost:
        if (((java.lang.Boolean) A1.r.f168d.f171c.a(com.google.android.gms.internal.ads.Gb.v0)).booleanValue() != false) goto L37;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void E4(android.content.res.Configuration r6) {
        /*
            r5 = this;
            com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel r0 = r5.f393l
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L10
            z1.g r0 = r0.x
            if (r0 == 0) goto L10
            boolean r0 = r0.f6549k
            if (r0 == 0) goto L10
            r0 = 1
            goto L11
        L10:
            r0 = 0
        L11:
            z1.p r3 = z1.p.f6575A
            D1.u0 r3 = r3.f6580e
            android.app.Activity r4 = r5.f392k
            boolean r6 = r3.a(r4, r6)
            boolean r3 = r5.f401t
            if (r3 == 0) goto L36
            if (r0 != 0) goto L36
            com.google.android.gms.internal.ads.vb r0 = com.google.android.gms.internal.ads.Gb.w0
            A1.r r3 = A1.r.f168d
            com.google.android.gms.internal.ads.Eb r3 = r3.f171c
            java.lang.Object r0 = r3.a(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L34
            goto L36
        L34:
            r1 = 0
            goto L57
        L36:
            if (r6 == 0) goto L4a
            com.google.android.gms.internal.ads.vb r6 = com.google.android.gms.internal.ads.Gb.v0
            A1.r r0 = A1.r.f168d
            com.google.android.gms.internal.ads.Eb r0 = r0.f171c
            java.lang.Object r6 = r0.a(r6)
            java.lang.Boolean r6 = (java.lang.Boolean) r6
            boolean r6 = r6.booleanValue()
            if (r6 == 0) goto L34
        L4a:
            com.google.android.gms.ads.internal.overlay.AdOverlayInfoParcel r6 = r5.f393l
            if (r6 == 0) goto L57
            z1.g r6 = r6.x
            if (r6 == 0) goto L57
            boolean r6 = r6.f6554p
            if (r6 == 0) goto L57
            r2 = 1
        L57:
            android.view.Window r6 = r4.getWindow()
            com.google.android.gms.internal.ads.vb r0 = com.google.android.gms.internal.ads.Gb.U0
            A1.r r3 = A1.r.f168d
            com.google.android.gms.internal.ads.Eb r3 = r3.f171c
            java.lang.Object r0 = r3.a(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L81
            android.view.View r6 = r6.getDecorView()
            if (r1 == 0) goto L7b
            if (r2 == 0) goto L78
            r0 = 5894(0x1706, float:8.259E-42)
            goto L7d
        L78:
            r0 = 5380(0x1504, float:7.539E-42)
            goto L7d
        L7b:
            r0 = 256(0x100, float:3.59E-43)
        L7d:
            r6.setSystemUiVisibility(r0)
            return
        L81:
            r0 = 2048(0x800, float:2.87E-42)
            r3 = 1024(0x400, float:1.435E-42)
            if (r1 == 0) goto L99
            r6.addFlags(r3)
            r6.clearFlags(r0)
            if (r2 == 0) goto L98
            android.view.View r6 = r6.getDecorView()
            r0 = 4098(0x1002, float:5.743E-42)
            r6.setSystemUiVisibility(r0)
        L98:
            return
        L99:
            r6.addFlags(r0)
            r6.clearFlags(r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: C1.s.E4(android.content.res.Configuration):void");
    }

    /* JADX WARN: Type inference failed for: r4v0, types: [C1.w, java.lang.Object] */
    public final void F4(boolean z4) {
        boolean z5;
        int i4;
        int i5;
        if (this.f393l.F) {
            return;
        }
        wb wbVar = Gb.o4;
        A1.r rVar = A1.r.f168d;
        int intValue = ((Integer) rVar.f171c.a(wbVar)).intValue();
        int i6 = 0;
        if (((Boolean) rVar.f171c.a(Gb.Q0)).booleanValue() || z4) {
            z5 = true;
        } else {
            z5 = false;
        }
        ?? obj = new Object();
        obj.f410a = 0;
        obj.f411b = 0;
        obj.f412c = 0;
        obj.f413d = 50;
        if (true != z5) {
            i4 = 0;
        } else {
            i4 = intValue;
        }
        obj.f410a = i4;
        if (true != z5) {
            i6 = intValue;
        }
        obj.f411b = i6;
        obj.f412c = intValue;
        this.f396o = new x(this.f392k, obj, this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        layoutParams.addRule(10);
        if (true != z5) {
            i5 = 9;
        } else {
            i5 = 11;
        }
        layoutParams.addRule(i5);
        G4(z4, this.f393l.p);
        this.f402u.addView(this.f396o, layoutParams);
    }

    public final void G3(int i4, String[] strArr, int[] iArr) {
        s sVar;
        if (i4 == 12345) {
            Activity activity = this.f392k;
            if (activity != null) {
                if (this.f393l.t == 5) {
                    sVar = this;
                } else {
                    sVar = null;
                }
                try {
                    this.f393l.E.f1(strArr, iArr, new c2.b(new Wy(activity, sVar, (String) null, (String) null)));
                    return;
                } catch (RemoteException unused) {
                    return;
                }
            }
            throw new NullPointerException("Null activity");
        }
    }

    public final void G4(boolean z4, boolean z5) {
        boolean z6;
        boolean z7;
        AdOverlayInfoParcel adOverlayInfoParcel;
        z1.g gVar;
        AdOverlayInfoParcel adOverlayInfoParcel2;
        z1.g gVar2;
        vb vbVar = Gb.O0;
        A1.r rVar = A1.r.f168d;
        boolean z8 = true;
        if (((Boolean) rVar.f171c.a(vbVar)).booleanValue() && (adOverlayInfoParcel2 = this.f393l) != null && (gVar2 = adOverlayInfoParcel2.x) != null && gVar2.f6555q) {
            z6 = true;
        } else {
            z6 = false;
        }
        vb vbVar2 = Gb.P0;
        Eb eb = rVar.f171c;
        if (((Boolean) eb.a(vbVar2)).booleanValue() && (adOverlayInfoParcel = this.f393l) != null && (gVar = adOverlayInfoParcel.x) != null && gVar.f6556r) {
            z7 = true;
        } else {
            z7 = false;
        }
        if (z4 && z5 && z6 && !z7) {
            em emVar = this.f394m;
            try {
                JSONObject put = new JSONObject().put("message", "Custom close has been disabled for interstitial ads in this ad slot.").put("action", "useCustomClose");
                if (emVar != null) {
                    emVar.V("onError", put);
                }
            } catch (JSONException e4) {
                E1.m.e("Error occurred while dispatching error event.", e4);
            }
        }
        x xVar = this.f396o;
        if (xVar != null) {
            if (!z7 && (!z5 || z6)) {
                z8 = false;
            }
            ImageButton imageButton = xVar.f414j;
            if (z8) {
                imageButton.setVisibility(8);
                if (((Long) eb.a(Gb.S0)).longValue() > 0) {
                    imageButton.animate().cancel();
                    imageButton.clearAnimation();
                    return;
                }
                return;
            }
            imageButton.setVisibility(0);
        }
    }

    public final void I() {
        this.f386A = true;
    }

    public final void J() {
        u uVar;
        AdOverlayInfoParcel adOverlayInfoParcel = this.f393l;
        if (adOverlayInfoParcel != null && (uVar = adOverlayInfoParcel.l) != null) {
            uVar.b4();
        }
    }

    public final void L() {
        AdOverlayInfoParcel adOverlayInfoParcel;
        u uVar;
        if (this.f392k.isFinishing() && !this.f387B) {
            this.f387B = true;
            em emVar = this.f394m;
            if (emVar != null) {
                emVar.J(this.f391F - 1);
                synchronized (this.f404w) {
                    try {
                        if (!this.f407z && this.f394m.P()) {
                            vb vbVar = Gb.j4;
                            A1.r rVar = A1.r.f168d;
                            if (((Boolean) rVar.f171c.a(vbVar)).booleanValue() && !this.f388C && (adOverlayInfoParcel = this.f393l) != null && (uVar = adOverlayInfoParcel.l) != null) {
                                uVar.Z();
                            }
                            j jVar = new j(0, this);
                            this.f406y = jVar;
                            t0.f774l.postDelayed(jVar, ((Long) rVar.f171c.a(Gb.N0)).longValue());
                            return;
                        }
                    } finally {
                    }
                }
            }
            r();
        }
    }

    public final void Q1() {
        synchronized (this.f404w) {
            try {
                this.f407z = true;
                Runnable runnable = this.f406y;
                if (runnable != null) {
                    WJ wj = t0.f774l;
                    wj.removeCallbacks(runnable);
                    wj.post(this.f406y);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x0094 A[Catch: m -> 0x0039, TryCatch #1 {m -> 0x0039, blocks: (B:12:0x001b, B:14:0x0029, B:16:0x002d, B:18:0x0033, B:21:0x003c, B:22:0x0047, B:24:0x0052, B:25:0x0054, B:27:0x005c, B:28:0x006a, B:30:0x0071, B:36:0x007e, B:38:0x0082, B:40:0x0087, B:43:0x0094, B:45:0x0098, B:47:0x009e, B:51:0x00a6, B:56:0x00ab, B:58:0x00b1, B:59:0x00b4, B:61:0x00ba, B:63:0x00be, B:64:0x00c1, B:66:0x00c7, B:67:0x00ca, B:74:0x00f9, B:76:0x00fd, B:77:0x0104, B:78:0x0105, B:80:0x0109, B:82:0x0116, B:33:0x0078, B:35:0x007c, B:41:0x0090, B:84:0x011a, B:85:0x0121, B:48:0x009f, B:50:0x00a3), top: B:88:0x001b }] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x0116 A[Catch: m -> 0x0039, TryCatch #1 {m -> 0x0039, blocks: (B:12:0x001b, B:14:0x0029, B:16:0x002d, B:18:0x0033, B:21:0x003c, B:22:0x0047, B:24:0x0052, B:25:0x0054, B:27:0x005c, B:28:0x006a, B:30:0x0071, B:36:0x007e, B:38:0x0082, B:40:0x0087, B:43:0x0094, B:45:0x0098, B:47:0x009e, B:51:0x00a6, B:56:0x00ab, B:58:0x00b1, B:59:0x00b4, B:61:0x00ba, B:63:0x00be, B:64:0x00c1, B:66:0x00c7, B:67:0x00ca, B:74:0x00f9, B:76:0x00fd, B:77:0x0104, B:78:0x0105, B:80:0x0109, B:82:0x0116, B:33:0x0078, B:35:0x007c, B:41:0x0090, B:84:0x011a, B:85:0x0121, B:48:0x009f, B:50:0x00a3), top: B:88:0x001b }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void V0(android.os.Bundle r9) {
        /*
            Method dump skipped, instructions count: 305
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: C1.s.V0(android.os.Bundle):void");
    }

    public final void d4(InterfaceC0374a interfaceC0374a) {
        E4((Configuration) c2.b.p0(interfaceC0374a));
    }

    public final void f() {
        this.f391F = 1;
    }

    public final void h() {
        AdOverlayInfoParcel adOverlayInfoParcel = this.f393l;
        if (adOverlayInfoParcel != null && this.f397p) {
            C4(adOverlayInfoParcel.s);
        }
        if (this.f398q != null) {
            this.f392k.setContentView(this.f402u);
            this.f386A = true;
            this.f398q.removeAllViews();
            this.f398q = null;
        }
        WebChromeClient.CustomViewCallback customViewCallback = this.f399r;
        if (customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
            this.f399r = null;
        }
        this.f397p = false;
    }

    public final boolean k0() {
        this.f391F = 1;
        if (this.f394m == null) {
            return true;
        }
        if (((Boolean) A1.r.f168d.f171c.a(Gb.U7)).booleanValue() && this.f394m.canGoBack()) {
            this.f394m.goBack();
            return false;
        }
        boolean I2 = this.f394m.I();
        if (!I2) {
            this.f394m.b("onbackblocked", Collections.emptyMap());
        }
        return I2;
    }

    public final void n() {
        u uVar;
        h();
        AdOverlayInfoParcel adOverlayInfoParcel = this.f393l;
        if (adOverlayInfoParcel != null && (uVar = adOverlayInfoParcel.l) != null) {
            uVar.q4();
        }
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.l4)).booleanValue() && this.f394m != null && (!this.f392k.isFinishing() || this.f395n == null)) {
            this.f394m.onPause();
        }
        L();
    }

    public final void o() {
        em emVar = this.f394m;
        if (emVar != null) {
            try {
                this.f402u.removeView(emVar.L());
            } catch (NullPointerException unused) {
            }
        }
        L();
    }

    public final void r() {
        em emVar;
        u uVar;
        if (!this.f388C) {
            this.f388C = true;
            em emVar2 = this.f394m;
            if (emVar2 != null) {
                this.f402u.removeView(emVar2.L());
                o oVar = this.f395n;
                if (oVar != null) {
                    this.f394m.G0(oVar.f383d);
                    this.f394m.O0(false);
                    ViewGroup viewGroup = this.f395n.f382c;
                    View L3 = this.f394m.L();
                    o oVar2 = this.f395n;
                    viewGroup.addView(L3, oVar2.f380a, oVar2.f381b);
                    this.f395n = null;
                } else {
                    Activity activity = this.f392k;
                    if (activity.getApplicationContext() != null) {
                        this.f394m.G0(activity.getApplicationContext());
                    }
                }
                this.f394m = null;
            }
            AdOverlayInfoParcel adOverlayInfoParcel = this.f393l;
            if (adOverlayInfoParcel != null && (uVar = adOverlayInfoParcel.l) != null) {
                uVar.U3(this.f391F);
            }
            AdOverlayInfoParcel adOverlayInfoParcel2 = this.f393l;
            if (adOverlayInfoParcel2 != null && (emVar = adOverlayInfoParcel2.m) != null) {
                aI t02 = emVar.t0();
                View L4 = this.f393l.m.L();
                if (t02 != null) {
                    z1.p.f6575A.f6596v.getClass();
                    pz.g(new Q0.t(t02, 3, L4));
                }
            }
        }
    }

    public final void t() {
        u uVar;
        AdOverlayInfoParcel adOverlayInfoParcel = this.f393l;
        if (adOverlayInfoParcel != null && (uVar = adOverlayInfoParcel.l) != null) {
            uVar.k3();
        }
        E4(this.f392k.getResources().getConfiguration());
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.l4)).booleanValue()) {
            em emVar = this.f394m;
            if (emVar != null && !emVar.a0()) {
                this.f394m.onResume();
            } else {
                E1.m.g("The webview does not exist. Ignoring action.");
            }
        }
    }

    public final void u() {
        this.f391F = 3;
        Activity activity = this.f392k;
        activity.finish();
        AdOverlayInfoParcel adOverlayInfoParcel = this.f393l;
        if (adOverlayInfoParcel != null && adOverlayInfoParcel.t == 5) {
            activity.overridePendingTransition(0, 0);
        }
    }

    public final void u1(Bundle bundle) {
        bundle.putBoolean("com.google.android.gms.ads.internal.overlay.hasResumed", this.f400s);
    }

    public final void x() {
        if (((Boolean) A1.r.f168d.f171c.a(Gb.l4)).booleanValue() && this.f394m != null && (!this.f392k.isFinishing() || this.f395n == null)) {
            this.f394m.onPause();
        }
        L();
    }

    public final void q() {
    }

    public final void O2(int i4, int i5, Intent intent) {
    }
}
