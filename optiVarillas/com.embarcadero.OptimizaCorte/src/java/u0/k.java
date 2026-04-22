package u0;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.internal.ads.R5;
import java.util.ArrayList;
import java.util.Iterator;
import u0.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class k extends f {

    /* renamed from: L  reason: collision with root package name */
    public int f5993L;

    /* renamed from: J  reason: collision with root package name */
    public ArrayList<f> f5991J = new ArrayList<>();

    /* renamed from: K  reason: collision with root package name */
    public boolean f5992K = true;

    /* renamed from: M  reason: collision with root package name */
    public boolean f5994M = false;

    /* renamed from: N  reason: collision with root package name */
    public int f5995N = 0;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends i {

        /* renamed from: a  reason: collision with root package name */
        public final /* synthetic */ f f5996a;

        public a(f fVar) {
            this.f5996a = fVar;
        }

        @Override // u0.f.d
        public final void d(f fVar) {
            this.f5996a.y();
            fVar.w(this);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class b extends i {

        /* renamed from: a  reason: collision with root package name */
        public k f5997a;

        @Override // u0.f.d
        public final void d(f fVar) {
            k kVar = this.f5997a;
            int i4 = kVar.f5993L - 1;
            kVar.f5993L = i4;
            if (i4 == 0) {
                kVar.f5994M = false;
                kVar.m();
            }
            fVar.w(this);
        }

        @Override // u0.i, u0.f.d
        public final void f(f fVar) {
            k kVar = this.f5997a;
            if (!kVar.f5994M) {
                kVar.F();
                kVar.f5994M = true;
            }
        }
    }

    @Override // u0.f
    public final void A(f.c cVar) {
        this.f5995N |= 8;
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f5991J.get(i4).A(cVar);
        }
    }

    @Override // u0.f
    public final void B(TimeInterpolator timeInterpolator) {
        this.f5995N |= 1;
        ArrayList<f> arrayList = this.f5991J;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                this.f5991J.get(i4).B(timeInterpolator);
            }
        }
        this.f5958m = timeInterpolator;
    }

    @Override // u0.f
    public final void C(f.a aVar) {
        super.C(aVar);
        this.f5995N |= 4;
        if (this.f5991J != null) {
            for (int i4 = 0; i4 < this.f5991J.size(); i4++) {
                this.f5991J.get(i4).C(aVar);
            }
        }
    }

    @Override // u0.f
    public final void D() {
        this.f5995N |= 2;
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f5991J.get(i4).D();
        }
    }

    @Override // u0.f
    public final void E(long j4) {
        this.f5956k = j4;
    }

    @Override // u0.f
    public final String G(String str) {
        String G4 = super.G(str);
        for (int i4 = 0; i4 < this.f5991J.size(); i4++) {
            StringBuilder sb = new StringBuilder();
            sb.append(G4);
            sb.append("\n");
            sb.append(this.f5991J.get(i4).G(str + "  "));
            G4 = sb.toString();
        }
        return G4;
    }

    public final void H(f fVar) {
        this.f5991J.add(fVar);
        fVar.f5963r = this;
        long j4 = this.f5957l;
        if (j4 >= 0) {
            fVar.z(j4);
        }
        if ((this.f5995N & 1) != 0) {
            fVar.B(this.f5958m);
        }
        if ((this.f5995N & 2) != 0) {
            fVar.D();
        }
        if ((this.f5995N & 4) != 0) {
            fVar.C((f.a) this.f5954E);
        }
        if ((this.f5995N & 8) != 0) {
            fVar.A(null);
        }
    }

    @Override // u0.f
    public final void c() {
        super.c();
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f5991J.get(i4).c();
        }
    }

    @Override // u0.f
    public final void d(m mVar) {
        if (t(mVar.f6000b)) {
            Iterator<f> it = this.f5991J.iterator();
            while (it.hasNext()) {
                f next = it.next();
                if (next.t(mVar.f6000b)) {
                    next.d(mVar);
                    mVar.f6001c.add(next);
                }
            }
        }
    }

    @Override // u0.f
    public final void f(m mVar) {
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f5991J.get(i4).f(mVar);
        }
    }

    @Override // u0.f
    public final void g(m mVar) {
        if (t(mVar.f6000b)) {
            Iterator<f> it = this.f5991J.iterator();
            while (it.hasNext()) {
                f next = it.next();
                if (next.t(mVar.f6000b)) {
                    next.g(mVar);
                    mVar.f6001c.add(next);
                }
            }
        }
    }

    @Override // u0.f
    /* renamed from: j */
    public final f clone() {
        k kVar = (k) super.clone();
        kVar.f5991J = new ArrayList<>();
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            f clone = this.f5991J.get(i4).clone();
            kVar.f5991J.add(clone);
            clone.f5963r = kVar;
        }
        return kVar;
    }

    @Override // u0.f
    public final void l(ViewGroup viewGroup, R5 r5, R5 r52, ArrayList<m> arrayList, ArrayList<m> arrayList2) {
        long j4 = this.f5956k;
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            f fVar = this.f5991J.get(i4);
            if (j4 > 0 && (this.f5992K || i4 == 0)) {
                long j5 = fVar.f5956k;
                if (j5 > 0) {
                    fVar.E(j5 + j4);
                } else {
                    fVar.E(j4);
                }
            }
            fVar.l(viewGroup, r5, r52, arrayList, arrayList2);
        }
    }

    @Override // u0.f
    public final void v(View view) {
        super.v(view);
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f5991J.get(i4).v(view);
        }
    }

    @Override // u0.f
    public final f w(f.d dVar) {
        super.w(dVar);
        return this;
    }

    @Override // u0.f
    public final void x(View view) {
        super.x(view);
        int size = this.f5991J.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f5991J.get(i4).x(view);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [u0.f$d, u0.k$b, java.lang.Object] */
    @Override // u0.f
    public final void y() {
        if (this.f5991J.isEmpty()) {
            F();
            m();
            return;
        }
        ?? obj = new Object();
        obj.f5997a = this;
        Iterator<f> it = this.f5991J.iterator();
        while (it.hasNext()) {
            it.next().a(obj);
        }
        this.f5993L = this.f5991J.size();
        if (!this.f5992K) {
            for (int i4 = 1; i4 < this.f5991J.size(); i4++) {
                this.f5991J.get(i4 - 1).a(new a(this.f5991J.get(i4)));
            }
            f fVar = this.f5991J.get(0);
            if (fVar != null) {
                fVar.y();
                return;
            }
            return;
        }
        Iterator<f> it2 = this.f5991J.iterator();
        while (it2.hasNext()) {
            it2.next().y();
        }
    }

    @Override // u0.f
    public final void z(long j4) {
        ArrayList<f> arrayList;
        this.f5957l = j4;
        if (j4 >= 0 && (arrayList = this.f5991J) != null) {
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                this.f5991J.get(i4).z(j4);
            }
        }
    }
}
