package o1;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import j$.util.Objects;
import q1.C0769a;
import q1.InterfaceC0770b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final /* synthetic */ class g implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ p f5441j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ i1.s f5442k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f5443l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ Runnable f5444m;

    public /* synthetic */ g(p pVar, i1.j jVar, int i4, Runnable runnable) {
        this.f5441j = pVar;
        this.f5442k = jVar;
        this.f5443l = i4;
        this.f5444m = runnable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        i1.s sVar = this.f5442k;
        final int i4 = this.f5443l;
        Runnable runnable = this.f5444m;
        final p pVar = this.f5441j;
        InterfaceC0770b interfaceC0770b = pVar.f;
        try {
            try {
                p1.d dVar = pVar.f5466c;
                Objects.requireNonNull(dVar);
                interfaceC0770b.a(new V0.a(dVar));
                NetworkInfo activeNetworkInfo = ((ConnectivityManager) pVar.f5464a.getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    pVar.a((i1.j) sVar, i4);
                } else {
                    final i1.j jVar = (i1.j) sVar;
                    interfaceC0770b.a(new InterfaceC0770b.a() { // from class: o1.h
                        @Override // q1.InterfaceC0770b.a
                        public final Object a() {
                            p.this.f5467d.b(jVar, i4 + 1);
                            return null;
                        }
                    });
                }
            } catch (C0769a unused) {
                pVar.f5467d.b(sVar, i4 + 1);
            }
        } finally {
            runnable.run();
        }
    }
}
