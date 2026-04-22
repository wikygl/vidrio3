package j;

import M.V;
import M.W;
import android.view.View;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: j.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0653g {

    /* renamed from: c  reason: collision with root package name */
    public Interpolator f4708c;

    /* renamed from: d  reason: collision with root package name */
    public W f4709d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f4710e;

    /* renamed from: b  reason: collision with root package name */
    public long f4707b = -1;
    public final a f = new a();

    /* renamed from: a  reason: collision with root package name */
    public final ArrayList<V> f4706a = new ArrayList<>();

    /* renamed from: j.g$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends H.a {

        /* renamed from: j  reason: collision with root package name */
        public boolean f4711j = false;

        /* renamed from: k  reason: collision with root package name */
        public int f4712k = 0;

        public a() {
        }

        @Override // M.W
        public final void b() {
            int i4 = this.f4712k + 1;
            this.f4712k = i4;
            C0653g c0653g = C0653g.this;
            if (i4 == c0653g.f4706a.size()) {
                W w4 = c0653g.f4709d;
                if (w4 != null) {
                    w4.b();
                }
                this.f4712k = 0;
                this.f4711j = false;
                c0653g.f4710e = false;
            }
        }

        @Override // H.a, M.W
        public final void e() {
            if (this.f4711j) {
                return;
            }
            this.f4711j = true;
            W w4 = C0653g.this.f4709d;
            if (w4 != null) {
                w4.e();
            }
        }
    }

    public final void a() {
        if (!this.f4710e) {
            return;
        }
        Iterator<V> it = this.f4706a.iterator();
        while (it.hasNext()) {
            it.next().b();
        }
        this.f4710e = false;
    }

    public final void b() {
        View view;
        if (this.f4710e) {
            return;
        }
        Iterator<V> it = this.f4706a.iterator();
        while (it.hasNext()) {
            V next = it.next();
            long j4 = this.f4707b;
            if (j4 >= 0) {
                next.c(j4);
            }
            Interpolator interpolator = this.f4708c;
            if (interpolator != null && (view = next.f1551a.get()) != null) {
                view.animate().setInterpolator(interpolator);
            }
            if (this.f4709d != null) {
                next.d(this.f);
            }
            View view2 = next.f1551a.get();
            if (view2 != null) {
                view2.animate().start();
            }
        }
        this.f4710e = true;
    }
}
