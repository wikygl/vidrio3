package V1;

import U1.d;
import W1.C0315c;
import android.content.Context;
import android.os.Handler;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class G extends o2.d implements d.a, d.b {

    /* renamed from: r  reason: collision with root package name */
    public static final n2.b f2542r = n2.e.f5379a;

    /* renamed from: k  reason: collision with root package name */
    public final Context f2543k;

    /* renamed from: l  reason: collision with root package name */
    public final Handler f2544l;

    /* renamed from: m  reason: collision with root package name */
    public final n2.b f2545m;

    /* renamed from: n  reason: collision with root package name */
    public final Set f2546n;

    /* renamed from: o  reason: collision with root package name */
    public final C0315c f2547o;

    /* renamed from: p  reason: collision with root package name */
    public n2.f f2548p;

    /* renamed from: q  reason: collision with root package name */
    public F f2549q;

    /* JADX WARN: Multi-variable type inference failed */
    public G(Context context, Handler handler, C0315c c0315c) {
        attachInterface(this, "com.google.android.gms.signin.internal.ISignInCallbacks");
        this.f2543k = context;
        this.f2544l = handler;
        this.f2547o = c0315c;
        this.f2546n = c0315c.f2707b;
        this.f2545m = f2542r;
    }

    @Override // V1.InterfaceC0297c
    public final void B(int i4) {
        x xVar = (x) this.f2549q;
        u uVar = (u) xVar.f.f2584s.get(xVar.f2622b);
        if (uVar != null) {
            if (uVar.f2612r) {
                uVar.n(new T1.b(17));
            } else {
                uVar.B(i4);
            }
        }
    }

    @Override // V1.InterfaceC0297c
    public final void Z() {
        this.f2548p.j(this);
    }

    @Override // V1.InterfaceC0303i
    public final void p0(T1.b bVar) {
        ((x) this.f2549q).b(bVar);
    }
}
