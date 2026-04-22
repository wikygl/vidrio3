package K1;

import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Tv;
import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.wb;
import com.google.android.gms.internal.ads.xb;
import com.google.android.gms.internal.ads.xk;
import j$.util.DesugarCollections;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class t {

    /* renamed from: a  reason: collision with root package name */
    public final int f1400a;

    /* renamed from: b  reason: collision with root package name */
    public final long f1401b;

    /* renamed from: c  reason: collision with root package name */
    public final boolean f1402c;

    /* renamed from: d  reason: collision with root package name */
    public final boolean f1403d;

    /* renamed from: e  reason: collision with root package name */
    public final Map f1404e;
    public final ArrayDeque f = new ArrayDeque();

    /* renamed from: g  reason: collision with root package name */
    public final ArrayDeque f1405g = new ArrayDeque();

    /* renamed from: h  reason: collision with root package name */
    public final Zv f1406h;

    /* renamed from: i  reason: collision with root package name */
    public ConcurrentHashMap f1407i;

    public t(Zv zv) {
        this.f1406h = zv;
        wb wbVar = Gb.l6;
        A1.r rVar = A1.r.f168d;
        this.f1400a = ((Integer) rVar.f171c.a(wbVar)).intValue();
        xb xbVar = Gb.m6;
        Eb eb = rVar.f171c;
        this.f1401b = ((Long) eb.a(xbVar)).longValue();
        this.f1402c = ((Boolean) eb.a(Gb.q6)).booleanValue();
        this.f1403d = ((Boolean) eb.a(Gb.p6)).booleanValue();
        this.f1404e = DesugarCollections.synchronizedMap(new s(this));
    }

    public final synchronized void a(String str) {
        this.f1404e.remove(str);
    }

    public final synchronized void b(Tv tv) {
        if (!this.f1402c) {
            return;
        }
        ArrayDeque arrayDeque = this.f1405g;
        ArrayDeque clone = arrayDeque.clone();
        arrayDeque.clear();
        ArrayDeque arrayDeque2 = this.f;
        ArrayDeque clone2 = arrayDeque2.clone();
        arrayDeque2.clear();
        xk.a.execute(new F1.c(this, tv, clone, clone2, 1));
    }

    public final void c(Tv tv, ArrayDeque arrayDeque, String str) {
        Pair pair;
        while (!arrayDeque.isEmpty()) {
            Pair pair2 = (Pair) arrayDeque.poll();
            ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap(tv.a);
            this.f1407i = concurrentHashMap;
            concurrentHashMap.put("action", "ev");
            this.f1407i.put("e_r", str);
            this.f1407i.put("e_id", (String) pair2.first);
            if (this.f1403d) {
                try {
                    JSONObject jSONObject = new JSONObject((String) pair2.second);
                    pair = new Pair(w.a(jSONObject.getJSONObject("extras").getString("query_info_type")), jSONObject.getString("request_agent"));
                } catch (JSONException unused) {
                    pair = new Pair("", "");
                }
                ConcurrentHashMap concurrentHashMap2 = this.f1407i;
                String str2 = (String) pair.first;
                if (!TextUtils.isEmpty(str2)) {
                    concurrentHashMap2.put("e_type", str2);
                }
                ConcurrentHashMap concurrentHashMap3 = this.f1407i;
                String str3 = (String) pair.second;
                if (!TextUtils.isEmpty(str3)) {
                    concurrentHashMap3.put("e_agent", str3);
                }
            }
            this.f1406h.a(this.f1407i, false);
        }
    }

    public final synchronized void d() {
        z1.p.f6575A.f6584j.getClass();
        long currentTimeMillis = System.currentTimeMillis();
        try {
            Iterator it = this.f1404e.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                if (currentTimeMillis - ((Long) ((Pair) entry.getValue()).first).longValue() <= this.f1401b) {
                    break;
                }
                this.f1405g.add(new Pair((String) entry.getKey(), (String) ((Pair) entry.getValue()).second));
                it.remove();
            }
        } catch (ConcurrentModificationException e4) {
            z1.p.f6575A.f6581g.h("QueryJsonMap.removeExpiredEntries", e4);
        }
    }
}
