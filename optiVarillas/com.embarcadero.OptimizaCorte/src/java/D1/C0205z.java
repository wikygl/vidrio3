package D1;

import K1.C0212f;
import android.content.Context;
import com.google.android.gms.internal.ads.Em;
import com.google.android.gms.internal.ads.Fu;
import com.google.android.gms.internal.ads.Gn;
import com.google.android.gms.internal.ads.Jq;
import com.google.android.gms.internal.ads.bx;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.mQ;
import com.google.android.gms.internal.ads.pm;
import com.google.android.gms.internal.ads.pn;
import com.google.android.gms.internal.ads.yG;
import com.google.android.gms.internal.ads.yk;
import java.util.ArrayList;
import p1.f;
import p1.g;

/* renamed from: D1.z  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0205z implements Em, k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f794j;

    /* renamed from: k  reason: collision with root package name */
    public Object f795k;

    /* renamed from: l  reason: collision with root package name */
    public Object f796l;

    public void a(String str, double d4, double d5) {
        ArrayList arrayList;
        ArrayList arrayList2;
        ArrayList arrayList3;
        int i4 = 0;
        while (true) {
            arrayList = (ArrayList) this.f794j;
            int size = arrayList.size();
            arrayList2 = (ArrayList) this.f795k;
            arrayList3 = (ArrayList) this.f796l;
            if (i4 >= size) {
                break;
            }
            double doubleValue = ((Double) arrayList3.get(i4)).doubleValue();
            double doubleValue2 = ((Double) arrayList2.get(i4)).doubleValue();
            if (d4 < doubleValue || (doubleValue == d4 && d5 < doubleValue2)) {
                break;
            }
            i4++;
        }
        arrayList.add(i4, str);
        arrayList3.add(i4, Double.valueOf(d4));
        arrayList2.add(i4, Double.valueOf(d5));
    }

    public Gn b() {
        mQ.f((Jq) this.f795k, Jq.class);
        mQ.f((C0212f) this.f796l, C0212f.class);
        return new Gn((pn) this.f794j, (C0212f) this.f796l, (Jq) this.f795k);
    }

    @Override // k3.a
    public Object get() {
        return new p1.x(((Integer) ((k3.a) this.f796l).get()).intValue(), (Context) ((k3.a) this.f794j).get(), (String) ((k3.a) this.f795k).get());
    }

    public void h(String str, int i4, String str2, boolean z4) {
        Fu fu = (Fu) this.f794j;
        yk ykVar = (yk) this.f796l;
        if (z4) {
            yG yGVar = fu.a;
            if (yGVar.a != null) {
                em emVar = (em) this.f795k;
                if (emVar.q() != null) {
                    emVar.q().E4(yGVar.a);
                }
            }
            ykVar.d();
            return;
        }
        fu.getClass();
        ykVar.c(new bx("Html video Web View failed to load. Error code: " + i4 + ", Description: " + str + ", Failing URL: " + str2, 1));
    }

    public /* synthetic */ C0205z(Fu fu, pm pmVar, yk ykVar) {
        this.f794j = fu;
        this.f795k = pmVar;
        this.f796l = ykVar;
    }

    public C0205z() {
        this.f794j = new ArrayList();
        this.f795k = new ArrayList();
        this.f796l = new ArrayList();
    }

    public C0205z(i2.Y y4) {
        p1.f fVar = f.a.f5515a;
        p1.g gVar = g.a.f5516a;
        this.f794j = y4;
        this.f795k = fVar;
        this.f796l = gVar;
    }
}
