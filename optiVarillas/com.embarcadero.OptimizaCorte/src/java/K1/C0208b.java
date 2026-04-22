package K1;

import A1.C1;
import A1.y1;
import D1.C0205z;
import D1.M;
import D1.t0;
import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.AN;
import com.google.android.gms.internal.ads.Aa;
import com.google.android.gms.internal.ads.Ba;
import com.google.android.gms.internal.ads.CF;
import com.google.android.gms.internal.ads.DF;
import com.google.android.gms.internal.ads.Dn;
import com.google.android.gms.internal.ads.Dp;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Fa;
import com.google.android.gms.internal.ads.GH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Gn;
import com.google.android.gms.internal.ads.Hx;
import com.google.android.gms.internal.ads.IH;
import com.google.android.gms.internal.ads.IN;
import com.google.android.gms.internal.ads.Ia;
import com.google.android.gms.internal.ads.Iq;
import com.google.android.gms.internal.ads.JN;
import com.google.android.gms.internal.ads.Jq;
import com.google.android.gms.internal.ads.LG;
import com.google.android.gms.internal.ads.Mm;
import com.google.android.gms.internal.ads.Nx;
import com.google.android.gms.internal.ads.Ox;
import com.google.android.gms.internal.ads.Oz;
import com.google.android.gms.internal.ads.Pf;
import com.google.android.gms.internal.ads.QN;
import com.google.android.gms.internal.ads.Qh;
import com.google.android.gms.internal.ads.RK;
import com.google.android.gms.internal.ads.Th;
import com.google.android.gms.internal.ads.UN;
import com.google.android.gms.internal.ads.VK;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.WN;
import com.google.android.gms.internal.ads.Wj;
import com.google.android.gms.internal.ads.XG;
import com.google.android.gms.internal.ads.XN;
import com.google.android.gms.internal.ads.Xu;
import com.google.android.gms.internal.ads.YH;
import com.google.android.gms.internal.ads.Yj;
import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.bM;
import com.google.android.gms.internal.ads.bt;
import com.google.android.gms.internal.ads.bx;
import com.google.android.gms.internal.ads.cO;
import com.google.android.gms.internal.ads.cc;
import com.google.android.gms.internal.ads.dH;
import com.google.android.gms.internal.ads.dk;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.fn;
import com.google.android.gms.internal.ads.fq;
import com.google.android.gms.internal.ads.jN;
import com.google.android.gms.internal.ads.kc;
import com.google.android.gms.internal.ads.ki;
import com.google.android.gms.internal.ads.mG;
import com.google.android.gms.internal.ads.nU;
import com.google.android.gms.internal.ads.pO;
import com.google.android.gms.internal.ads.rx;
import com.google.android.gms.internal.ads.s7;
import com.google.android.gms.internal.ads.uG;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.wk;
import com.google.android.gms.internal.ads.xG;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.zG;
import com.google.android.gms.internal.ads.zH;
import com.google.android.gms.internal.ads.za;
import com.google.android.gms.internal.ads.zb;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import org.json.JSONObject;
import t1.C0803e;

/* renamed from: K1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0208b extends Yj {

    /* renamed from: P  reason: collision with root package name */
    public static final ArrayList f1322P = new ArrayList(Arrays.asList("/aclk", "/pcs/click", "/dbm/clk"));

    /* renamed from: Q  reason: collision with root package name */
    public static final ArrayList f1323Q = new ArrayList(Arrays.asList(".doubleclick.net", ".googleadservices.com"));

    /* renamed from: R  reason: collision with root package name */
    public static final ArrayList f1324R = new ArrayList(Arrays.asList("/pagead/adview", "/pcs/view", "/pagead/conversion", "/dbm/ad"));

    /* renamed from: S  reason: collision with root package name */
    public static final ArrayList f1325S = new ArrayList(Arrays.asList(".doubleclick.net", ".googleadservices.com", ".googlesyndication.com"));

    /* renamed from: A  reason: collision with root package name */
    public final boolean f1326A;

    /* renamed from: B  reason: collision with root package name */
    public final String f1327B;

    /* renamed from: C  reason: collision with root package name */
    public final String f1328C;

    /* renamed from: D  reason: collision with root package name */
    public final AtomicInteger f1329D;

    /* renamed from: E  reason: collision with root package name */
    public final E1.a f1330E;

    /* renamed from: F  reason: collision with root package name */
    public String f1331F;

    /* renamed from: G  reason: collision with root package name */
    public final String f1332G;

    /* renamed from: H  reason: collision with root package name */
    public final ArrayList f1333H;

    /* renamed from: I  reason: collision with root package name */
    public final ArrayList f1334I;

    /* renamed from: J  reason: collision with root package name */
    public final ArrayList f1335J;

    /* renamed from: K  reason: collision with root package name */
    public final ArrayList f1336K;

    /* renamed from: L  reason: collision with root package name */
    public final AtomicBoolean f1337L;

    /* renamed from: M  reason: collision with root package name */
    public final AtomicBoolean f1338M;

    /* renamed from: N  reason: collision with root package name */
    public final AtomicInteger f1339N;

    /* renamed from: O  reason: collision with root package name */
    public final cc f1340O;

    /* renamed from: k  reason: collision with root package name */
    public final Mm f1341k;

    /* renamed from: l  reason: collision with root package name */
    public Context f1342l;

    /* renamed from: m  reason: collision with root package name */
    public final s7 f1343m;

    /* renamed from: n  reason: collision with root package name */
    public final zG f1344n;

    /* renamed from: o  reason: collision with root package name */
    public final LG f1345o;

    /* renamed from: p  reason: collision with root package name */
    public final cO f1346p;

    /* renamed from: q  reason: collision with root package name */
    public final ScheduledExecutorService f1347q;

    /* renamed from: r  reason: collision with root package name */
    public Th f1348r;

    /* renamed from: s  reason: collision with root package name */
    public Point f1349s;

    /* renamed from: t  reason: collision with root package name */
    public Point f1350t;

    /* renamed from: u  reason: collision with root package name */
    public final Set f1351u;

    /* renamed from: v  reason: collision with root package name */
    public final Zv f1352v;

    /* renamed from: w  reason: collision with root package name */
    public final YH f1353w;

    /* renamed from: x  reason: collision with root package name */
    public final boolean f1354x;

    /* renamed from: y  reason: collision with root package name */
    public final boolean f1355y;

    /* renamed from: z  reason: collision with root package name */
    public final boolean f1356z;

    public C0208b(Mm mm, Context context, s7 s7Var, LG lg, wk wkVar, ScheduledExecutorService scheduledExecutorService, Zv zv, YH yh, E1.a aVar, cc ccVar, zG zGVar) {
        super("com.google.android.gms.ads.internal.signals.ISignalGenerator");
        ArrayList arrayList;
        this.f1349s = new Point();
        this.f1350t = new Point();
        this.f1351u = Collections.newSetFromMap(new WeakHashMap());
        this.f1329D = new AtomicInteger(0);
        this.f1337L = new AtomicBoolean(false);
        this.f1338M = new AtomicBoolean(false);
        this.f1339N = new AtomicInteger(0);
        this.f1341k = mm;
        this.f1342l = context;
        this.f1343m = s7Var;
        this.f1344n = zGVar;
        this.f1345o = lg;
        this.f1346p = wkVar;
        this.f1347q = scheduledExecutorService;
        this.f1352v = zv;
        this.f1353w = yh;
        this.f1330E = aVar;
        this.f1340O = ccVar;
        vb vbVar = Gb.w6;
        A1.r rVar = A1.r.f168d;
        this.f1354x = ((Boolean) rVar.f171c.a(vbVar)).booleanValue();
        vb vbVar2 = Gb.v6;
        Eb eb = rVar.f171c;
        this.f1355y = ((Boolean) eb.a(vbVar2)).booleanValue();
        this.f1356z = ((Boolean) eb.a(Gb.y6)).booleanValue();
        this.f1326A = ((Boolean) eb.a(Gb.A6)).booleanValue();
        this.f1327B = (String) eb.a(Gb.z6);
        this.f1328C = (String) eb.a(Gb.B6);
        this.f1332G = (String) eb.a(Gb.C6);
        if (((Boolean) eb.a(Gb.D6)).booleanValue()) {
            this.f1333H = K4((String) eb.a(Gb.E6));
            this.f1334I = K4((String) eb.a(Gb.F6));
            this.f1335J = K4((String) eb.a(Gb.G6));
            arrayList = K4((String) eb.a(Gb.H6));
        } else {
            this.f1333H = f1322P;
            this.f1334I = f1323Q;
            this.f1335J = f1324R;
            arrayList = f1325S;
        }
        this.f1336K = arrayList;
    }

    public static boolean I4(Uri uri, List list, List list2) {
        String host = uri.getHost();
        String path = uri.getPath();
        if (host != null && path != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                if (path.contains((String) it.next())) {
                    Iterator it2 = list2.iterator();
                    while (it2.hasNext()) {
                        if (host.endsWith((String) it2.next())) {
                            return true;
                        }
                    }
                    continue;
                }
            }
        }
        return false;
    }

    public static final Uri J4(Uri uri, String str, String str2) {
        String uri2 = uri.toString();
        int indexOf = uri2.indexOf("&adurl=");
        if (indexOf == -1) {
            indexOf = uri2.indexOf("?adurl=");
        }
        if (indexOf != -1) {
            int i4 = indexOf + 1;
            return Uri.parse(uri2.substring(0, i4) + str + "=" + str2 + "&" + uri2.substring(i4));
        }
        return uri.buildUpon().appendQueryParameter(str, str2).build();
    }

    public static final ArrayList K4(String str) {
        String[] split = TextUtils.split(str, ",");
        ArrayList arrayList = new ArrayList();
        for (String str2 : split) {
            if (!VK.a(str2)) {
                arrayList.add(str2);
            }
        }
        return arrayList;
    }

    public static FH L4(InterfaceFutureC0346a interfaceFutureC0346a, dk dkVar) {
        String str;
        if (!GH.a() || !((Boolean) kc.e.e()).booleanValue()) {
            return null;
        }
        try {
            FH a4 = ((x) VN.C(interfaceFutureC0346a)).a();
            a4.e(new ArrayList(Collections.singletonList(dkVar.k)));
            y1 y1Var = dkVar.m;
            if (y1Var == null) {
                str = "";
            } else {
                str = y1Var.f204y;
            }
            a4.b(str);
            return a4;
        } catch (ExecutionException e4) {
            z1.p.f6575A.f6581g.h("SignalGeneratorImpl.getConfiguredCriticalUserJourney", e4);
            return null;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.lang.Object, K1.e] */
    /* JADX WARN: Type inference failed for: r9v0, types: [com.google.android.gms.internal.ads.Iq, java.lang.Object] */
    public final Gn C4(Context context, String str, String str2, C1 c12, y1 y1Var, Bundle bundle) {
        String str3;
        y1 y1Var2;
        C1 c13;
        char c4;
        xG xGVar = new xG();
        boolean equals = "REWARDED".equals(str2);
        G3.i iVar = xGVar.o;
        if (equals) {
            iVar.f997j = 2;
        } else if ("REWARDED_INTERSTITIAL".equals(str2)) {
            iVar.f997j = 3;
        }
        C0205z l2 = this.f1341k.l();
        ?? obj = new Object();
        ((Iq) obj).a = context;
        if (str == null) {
            str3 = "adUnitId";
        } else {
            str3 = str;
        }
        xGVar.c = str3;
        if (y1Var == null) {
            y1Var2 = new y1(8, -1L, new Bundle(), -1, new ArrayList(), false, -1, false, null, null, null, null, new Bundle(), new Bundle(), new ArrayList(), null, null, false, null, -1, null, new ArrayList(), 60000, null, 0, 0L);
        } else {
            y1Var2 = y1Var;
        }
        xGVar.a = y1Var2;
        if (c12 == null) {
            switch (str2.hashCode()) {
                case -1999289321:
                    if (str2.equals("NATIVE")) {
                        c4 = 3;
                        break;
                    }
                    c4 = 65535;
                    break;
                case -428325382:
                    if (str2.equals("APP_OPEN_AD")) {
                        c4 = 4;
                        break;
                    }
                    c4 = 65535;
                    break;
                case 543046670:
                    if (str2.equals("REWARDED")) {
                        c4 = 1;
                        break;
                    }
                    c4 = 65535;
                    break;
                case 1854800829:
                    if (str2.equals("REWARDED_INTERSTITIAL")) {
                        c4 = 2;
                        break;
                    }
                    c4 = 65535;
                    break;
                case 1951953708:
                    if (str2.equals("BANNER")) {
                        c4 = 0;
                        break;
                    }
                    c4 = 65535;
                    break;
                default:
                    c4 = 65535;
                    break;
            }
            if (c4 != 0) {
                if (c4 != 1 && c4 != 2) {
                    if (c4 != 3) {
                        if (c4 != 4) {
                            c13 = new C1();
                        } else {
                            c13 = C1.h();
                        }
                    } else {
                        c13 = C1.i();
                    }
                } else {
                    c13 = new C1("reward_mb", 0, 0, true, 0, 0, null, false, false, false, false, false, false, false, false);
                }
            } else {
                c13 = new C1(context, C0803e.f5788i);
            }
        } else {
            c13 = c12;
        }
        xGVar.b = c13;
        xGVar.r = true;
        xGVar.s = bundle;
        ((Iq) obj).b = xGVar.a();
        l2.f795k = new Jq((Iq) obj);
        ?? obj2 = new Object();
        obj2.f1368j = str2;
        l2.f796l = new C0212f(obj2);
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        new HashSet();
        return l2.b();
    }

    public final jN D4(final String str) {
        final Xu[] xuArr = new Xu[1];
        InterfaceFutureC0346a a4 = this.f1345o.a();
        IN in = new IN() { // from class: K1.A
            public final InterfaceFutureC0346a d(Object obj) {
                switch (r4) {
                    case 0:
                        Xu xu = (Xu) obj;
                        C0208b c0208b = (C0208b) this;
                        c0208b.getClass();
                        ((Xu[]) xuArr)[0] = xu;
                        Context context = c0208b.f1342l;
                        Th th = c0208b.f1348r;
                        Map map = th.k;
                        JSONObject c4 = M.c(context, map, map, th.j, null);
                        JSONObject f = M.f(c0208b.f1342l, c0208b.f1348r.j);
                        JSONObject e4 = M.e(c0208b.f1348r.j);
                        JSONObject d4 = M.d(c0208b.f1342l, c0208b.f1348r.j);
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("asset_view_signal", c4);
                        jSONObject.put("ad_view_signal", f);
                        jSONObject.put("scroll_view_signal", e4);
                        jSONObject.put("lock_screen_signal", d4);
                        String str2 = (String) str;
                        if ("google.afma.nativeAds.getPublisherCustomRenderedClickSignals".equals(str2)) {
                            jSONObject.put("click_signal", M.b(null, c0208b.f1342l, c0208b.f1350t, c0208b.f1349s));
                        }
                        return xu.a(str2, jSONObject);
                    default:
                        return VN.x(Dp.a(((Oz) this).a, (View) xuArr, (mG) str));
                }
            }
        };
        cO cOVar = this.f1346p;
        AN A4 = VN.A(a4, in, cOVar);
        A4.a(new B.h(this, 2, xuArr), cOVar);
        return VN.r(VN.z(VN.B(QN.r(A4), ((Integer) A1.r.f168d.f171c.a(Gb.N6)).intValue(), TimeUnit.MILLISECONDS, this.f1347q), G.f1297a, cOVar), Exception.class, H.f1298a, cOVar);
    }

    public final void E4() {
        pO b4;
        if (((Boolean) A1.r.f168d.f171c.a(Gb.O9)).booleanValue()) {
            b4 = VN.y(new y(this), xk.a);
        } else {
            b4 = C4(this.f1342l, null, "BANNER", null, null, new Bundle()).b();
        }
        D1.E e4 = new D1.E(this);
        b4.a(new UN(b4, 0, e4), this.f1341k.a());
    }

    public final void F4() {
        vb vbVar = Gb.B8;
        A1.r rVar = A1.r.f168d;
        if (((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            vb vbVar2 = Gb.E8;
            Eb eb = rVar.f171c;
            if (!((Boolean) eb.a(vbVar2)).booleanValue()) {
                if (!((Boolean) eb.a(Gb.I8)).booleanValue() || !this.f1337L.getAndSet(true)) {
                    E4();
                }
            }
        }
    }

    public final void G4(ArrayList arrayList, InterfaceC0374a interfaceC0374a, Qh qh, boolean z4) {
        ArrayList arrayList2;
        ArrayList arrayList3;
        InterfaceFutureC0346a interfaceFutureC0346a;
        Map map;
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.M6)).booleanValue()) {
            E1.m.g("The updating URL feature is not enabled.");
            try {
                qh.A("The updating URL feature is not enabled.");
                return;
            } catch (RemoteException e4) {
                E1.m.e("", e4);
                return;
            }
        }
        Iterator it = arrayList.iterator();
        int i4 = 0;
        while (true) {
            boolean hasNext = it.hasNext();
            arrayList2 = this.f1334I;
            arrayList3 = this.f1333H;
            if (!hasNext) {
                break;
            } else if (I4((Uri) it.next(), arrayList3, arrayList2)) {
                i4++;
            }
        }
        if (i4 > 1) {
            E1.m.g("Multiple google urls found: ".concat(String.valueOf(arrayList)));
        }
        ArrayList arrayList4 = new ArrayList();
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            Uri uri = (Uri) it2.next();
            if (!I4(uri, arrayList3, arrayList2)) {
                E1.m.g("Not a Google URL: ".concat(String.valueOf(uri)));
                interfaceFutureC0346a = VN.x(uri);
            } else {
                B b4 = new B(this, uri, interfaceC0374a);
                cO cOVar = this.f1346p;
                InterfaceFutureC0346a y4 = cOVar.y(b4);
                Th th = this.f1348r;
                if (th != null && (map = th.k) != null && !map.isEmpty()) {
                    interfaceFutureC0346a = VN.A(y4, new IN() { // from class: K1.C
                        public final InterfaceFutureC0346a d(Object obj) {
                            WN v4;
                            XG xg;
                            CF cf;
                            switch (r1) {
                                case 0:
                                    final Uri uri2 = (Uri) obj;
                                    C0208b c0208b = (C0208b) this;
                                    return VN.z(c0208b.D4("google.afma.nativeAds.getPublisherCustomRenderedClickSignals"), new RK() { // from class: K1.D
                                        public final Object apply(Object obj2) {
                                            String str = (String) obj2;
                                            boolean isEmpty = TextUtils.isEmpty(str);
                                            Uri uri3 = uri2;
                                            if (!isEmpty) {
                                                return C0208b.J4(uri3, "nas", str);
                                            }
                                            return uri3;
                                        }
                                    }, c0208b.f1346p);
                                case 1:
                                    em emVar = (em) obj;
                                    if (emVar != null && emVar.q() != null) {
                                        return (InterfaceFutureC0346a) this;
                                    }
                                    throw new bx("Retrieve video view in html5 ad response failed.", 1);
                                case 2:
                                    return VN.x(new uG(new Pf(9, ((rx) this).d), Dn.a(new StringReader(((JSONObject) obj).toString()), (Ei) null)));
                                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                                    ki kiVar = (ki) obj;
                                    Ox ox = (Ox) this;
                                    ox.getClass();
                                    String str = kiVar.o;
                                    t0 t0Var = z1.p.f6575A.f6578c;
                                    boolean c4 = t0.c(str);
                                    cO cOVar2 = ox.b;
                                    if (c4) {
                                        v4 = VN.w(new bx("Ads service proxy force local", 1));
                                    } else {
                                        v4 = VN.v(VN.y(new bt(ox, kiVar), ox.a), ExecutionException.class, fq.d, cOVar2);
                                    }
                                    return VN.v(v4, Hx.class, new Nx(ox, kiVar, Binder.getCallingUid()), cOVar2);
                                default:
                                    dH dHVar = (dH) obj;
                                    DF df = (DF) this;
                                    df.getClass();
                                    if (dHVar != null && (xg = dHVar.a) != null && (cf = dHVar.b) != null) {
                                        Ba D4 = Ia.D();
                                        za F4 = Aa.F();
                                        F4.k();
                                        Aa.H(((nU) F4).k);
                                        Fa F5 = Fa.F();
                                        F4.k();
                                        Aa.D(((nU) F4).k, F5);
                                        D4.k();
                                        Ia.F(((nU) D4).k, F4.i());
                                        xg.a.c().f.Q0(D4.i());
                                        return df.b(xg, cf.b);
                                    }
                                    throw new bx("Empty prefetch", 1);
                            }
                        }
                    }, cOVar);
                } else {
                    E1.m.f("Asset view map is empty.");
                    interfaceFutureC0346a = y4;
                }
            }
            arrayList4.add(interfaceFutureC0346a);
        }
        JN jn = new JN(bM.s(arrayList4), true);
        jn.a(new UN(jn, 0, new L(this, qh, z4)), this.f1341k.a());
    }

    public final void H4(ArrayList arrayList, InterfaceC0374a interfaceC0374a, Qh qh, boolean z4) {
        Map map;
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.M6)).booleanValue()) {
            try {
                qh.A("The updating URL feature is not enabled.");
                return;
            } catch (RemoteException e4) {
                E1.m.e("", e4);
                return;
            }
        }
        I i4 = new I(this, arrayList, interfaceC0374a, 0);
        cO cOVar = this.f1346p;
        AN y4 = cOVar.y(i4);
        Th th = this.f1348r;
        if (th != null && (map = th.k) != null && !map.isEmpty()) {
            y4 = VN.A(y4, new C0218l(1, this), cOVar);
        } else {
            E1.m.f("Asset view map is empty.");
        }
        y4.a(new UN(y4, 0, new K(this, qh, z4)), this.f1341k.a());
    }

    public final void l1(InterfaceC0374a interfaceC0374a, dk dkVar, Wj wj) {
        XN x4;
        InterfaceFutureC0346a b4;
        InterfaceFutureC0346a interfaceFutureC0346a;
        XN xn;
        Bundle bundle = new Bundle();
        vb vbVar = Gb.R1;
        A1.r rVar = A1.r.f168d;
        if (((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            bundle.putLong("api-call", dkVar.m.f188I);
            z1.p.f6575A.f6584j.getClass();
            bundle.putLong("dynamite-enter", System.currentTimeMillis());
        }
        Context context = (Context) c2.b.p0(interfaceC0374a);
        this.f1342l = context;
        zH n4 = fn.n(context, IH.v);
        n4.f();
        boolean equals = "UNKNOWN".equals(dkVar.k);
        Eb eb = rVar.f171c;
        if (equals) {
            List arrayList = new ArrayList();
            zb zbVar = Gb.L6;
            if (!((String) eb.a(zbVar)).isEmpty()) {
                arrayList = Arrays.asList(((String) eb.a(zbVar)).split(","));
            }
            if (arrayList.contains(w.b(dkVar.m))) {
                XN w4 = VN.w(new IllegalArgumentException("Unknown format is no longer supported."));
                xn = w4;
                interfaceFutureC0346a = VN.w(new IllegalArgumentException("Unknown format is no longer supported."));
                J j4 = new J(this, xn, dkVar, wj, n4);
                interfaceFutureC0346a.a(new UN(interfaceFutureC0346a, 0, j4), this.f1341k.a());
            }
        }
        if (((Boolean) eb.a(Gb.O9)).booleanValue()) {
            wk wkVar = xk.a;
            x4 = wkVar.y(new E(this, dkVar, bundle, 0));
            b4 = VN.A(x4, F.f1296a, wkVar);
        } else {
            Gn C4 = C4(this.f1342l, dkVar.j, dkVar.k, dkVar.l, dkVar.m, bundle);
            x4 = VN.x(C4);
            b4 = C4.b();
        }
        interfaceFutureC0346a = b4;
        xn = x4;
        J j42 = new J(this, xn, dkVar, wj, n4);
        interfaceFutureC0346a.a(new UN(interfaceFutureC0346a, 0, j42), this.f1341k.a());
    }
}
