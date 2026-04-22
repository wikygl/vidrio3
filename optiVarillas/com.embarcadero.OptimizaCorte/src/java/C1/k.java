package C1;

import A1.N0;
import D1.C0183d0;
import D1.C0202w;
import D1.t0;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.view.View;
import com.google.android.gms.internal.ads.AG;
import com.google.android.gms.internal.ads.Em;
import com.google.android.gms.internal.ads.Fm;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Iy;
import com.google.android.gms.internal.ads.KG;
import com.google.android.gms.internal.ads.LE;
import com.google.android.gms.internal.ads.MG;
import com.google.android.gms.internal.ads.O3;
import com.google.android.gms.internal.ads.QK;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.Ts;
import com.google.android.gms.internal.ads.Ur;
import com.google.android.gms.internal.ads.VK;
import com.google.android.gms.internal.ads.W4;
import com.google.android.gms.internal.ads.Y2;
import com.google.android.gms.internal.ads.bY;
import com.google.android.gms.internal.ads.bt;
import com.google.android.gms.internal.ads.cr;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.f0;
import com.google.android.gms.internal.ads.fZ;
import com.google.android.gms.internal.ads.fr;
import com.google.android.gms.internal.ads.gZ;
import com.google.android.gms.internal.ads.hL;
import com.google.android.gms.internal.ads.i7;
import com.google.android.gms.internal.ads.pG;
import com.google.android.gms.internal.ads.px;
import com.google.android.gms.internal.ads.sf;
import com.google.android.gms.internal.ads.tf;
import com.google.android.gms.internal.ads.uG;
import com.google.android.gms.internal.ads.vi;
import com.google.android.gms.internal.ads.wz;
import com.google.android.gms.internal.ads.xI;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class k implements Em, i7, Fm, Ur, TN, Ts, z1.d, hL, LE, com.google.gson.internal.i {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f375j;

    /* renamed from: k  reason: collision with root package name */
    public Object f376k;

    public Object a() {
        new f0();
        new bt((Context) this.f376k);
        Object obj = new Object();
        HashMap hashMap = new HashMap();
        new HashSet();
        HashMap hashMap2 = new HashMap();
        hashMap.clear();
        hashMap2.clear();
        return obj;
    }

    @Override // z1.d
    public synchronized void b(View view) {
        z1.d dVar = (z1.d) this.f376k;
        if (dVar != null) {
            dVar.b(view);
        }
    }

    @Override // z1.d
    public synchronized void c() {
        z1.d dVar = (z1.d) this.f376k;
        if (dVar != null) {
            dVar.c();
        }
    }

    public void d(Object obj) {
        switch (this.f375j) {
            case 6:
                ((fr) obj).e((N0) this.f376k);
                return;
            default:
                ((gZ) obj).c((O3) this.f376k);
                return;
        }
    }

    @Override // z1.d
    public synchronized void e() {
        z1.d dVar = (z1.d) this.f376k;
        if (dVar != null) {
            dVar.e();
        }
    }

    /*  JADX ERROR: Type inference failed with exception
        jadx.core.utils.exceptions.JadxOverflowException: Type update terminated with stack overflow, arg: (r38v3 ?? I:??[int, boolean])
        	at aa.w.a(SourceFile:83)
        	at aa.w.c(SourceFile:9)
        	at z8.a0.H0(SourceFile:1)
        	at x9.i0.b(SourceFile:51)
        */
    public void f(byte[] r105, byte[] r106) {
        /*
            Method dump skipped, instructions count: 2603
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: C1.k.f(byte[], byte[]):void");
    }

    public void g(Object obj) {
        switch (this.f375j) {
            case 7:
                uG uGVar = (uG) obj;
                if (((Boolean) A1.r.f168d.f171c.a(Gb.w5)).booleanValue()) {
                    ((px) this.f376k).e.f(((pG) uGVar.b.b).e);
                    Iy iy = ((px) this.f376k).e;
                    long j4 = ((pG) uGVar.b.b).f;
                    synchronized (iy.h) {
                        iy.c = j4;
                    }
                    return;
                }
                return;
            default:
                try {
                    ((vi) this.f376k).S((ParcelFileDescriptor) obj);
                    return;
                } catch (RemoteException e4) {
                    C0183d0.l("Ad service can't call client", e4);
                    return;
                }
        }
    }

    public void h(String str, int i4, String str2, boolean z4) {
        em emVar = ((s) this.f376k).f394m;
        if (emVar != null) {
            emVar.l0();
        }
    }

    public int i() {
        int optInt = ((JSONObject) this.f376k).optInt("media_type", -1);
        if (optInt != 0) {
            if (optInt == 1) {
                return 1;
            }
            return 3;
        }
        return 2;
    }

    public void j(boolean z4, Context context, cr crVar) {
        wz wzVar = (wz) this.f376k;
        try {
            ((KG) wzVar.b).c(z4);
            KG kg = (KG) wzVar.b;
            kg.getClass();
            kg.a.h0();
        } catch (AG e4) {
            E1.m.h("Cannot show rewarded video.", e4);
            throw new Exception(e4.getCause());
        }
    }

    public Object k() {
        Type type = (Type) this.f376k;
        if (type instanceof ParameterizedType) {
            Type type2 = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type2 instanceof Class) {
                return new EnumMap((Class) type2);
            }
            throw new RuntimeException("Invalid EnumMap type: " + type.toString());
        }
        throw new RuntimeException("Invalid EnumMap type: " + type.toString());
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v6 */
    public List l(W4 w4) {
        boolean z4;
        String str;
        int i4;
        List list;
        xI xIVar = new xI((byte[]) w4.c);
        ArrayList arrayList = (List) this.f376k;
        while (xIVar.n() > 0) {
            int v4 = xIVar.v();
            int v5 = xIVar.b + xIVar.v();
            if (v4 == 134) {
                arrayList = new ArrayList();
                int v6 = xIVar.v() & 31;
                for (int i5 = 0; i5 < v6; i5++) {
                    String a4 = xIVar.a(3, QK.c);
                    int v7 = xIVar.v();
                    if ((v7 & 128) != 0) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    if (z4) {
                        i4 = v7 & 63;
                        str = "application/cea-708";
                    } else {
                        str = "application/cea-608";
                        i4 = 1;
                    }
                    byte v8 = (byte) xIVar.v();
                    xIVar.j(1);
                    if (z4) {
                        list = Collections.singletonList((v8 & 64) != 0 ? new byte[]{1} : new byte[]{0});
                    } else {
                        list = null;
                    }
                    Y2 y22 = new Y2();
                    y22.f(str);
                    y22.c = a4;
                    y22.C = i4;
                    y22.m = list;
                    arrayList.add(new O3(y22));
                }
            }
            xIVar.i(v5);
            arrayList = arrayList;
        }
        return arrayList;
    }

    public void m(Throwable th) {
        String message;
        switch (this.f375j) {
            case 7:
                if (((Boolean) A1.r.f168d.f171c.a(Gb.w5)).booleanValue()) {
                    Matcher matcher = px.h.matcher(th.getMessage());
                    if (matcher.matches()) {
                        ((px) this.f376k).e.f(Integer.parseInt(matcher.group(1)));
                        return;
                    }
                    return;
                }
                return;
            default:
                try {
                    vi viVar = (vi) this.f376k;
                    N0 a4 = MG.a(th);
                    if (VK.a(th.getMessage())) {
                        message = a4.f63k;
                    } else {
                        message = th.getMessage();
                    }
                    viVar.g0(new C0202w(message, a4.f62j));
                    return;
                } catch (RemoteException e4) {
                    C0183d0.l("Ad service can't call client", e4);
                    return;
                }
        }
    }

    public /* synthetic */ k(int i4, Object obj) {
        this.f375j = i4;
        this.f376k = obj;
    }

    public /* synthetic */ k(fZ fZVar, O3 o32, bY bYVar) {
        this.f375j = 13;
        this.f376k = o32;
    }

    /* renamed from: a  reason: collision with other method in class */
    public void m0a() {
        z1.p.f6575A.f6584j.getClass();
        long currentTimeMillis = System.currentTimeMillis();
        tf tfVar = (tf) this.f376k;
        long j4 = tfVar.c;
        Long valueOf = Long.valueOf(currentTimeMillis - j4);
        ArrayList arrayList = tfVar.b;
        arrayList.add(valueOf);
        String valueOf2 = String.valueOf(arrayList.get(0));
        C0183d0.k("LoadNewJavascriptEngine(onEngLoaded) latency is " + valueOf2 + " ms.");
        t0.f774l.postDelayed(new sf(j4, tfVar.e, tfVar.d, tfVar.a, arrayList), (long) ((Integer) A1.r.f168d.f171c.a(Gb.b)).intValue());
    }
}
