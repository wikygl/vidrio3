package C3;

import java.util.concurrent.CancellationException;
import n3.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public interface Y extends f.b {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class a {
        public static /* synthetic */ L a(Y y4, boolean z4, c0 c0Var, int i4) {
            boolean z5 = false;
            if ((i4 & 1) != 0) {
                z4 = false;
            }
            if ((i4 & 2) != 0) {
                z5 = true;
            }
            return y4.r(z4, z5, c0Var);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class b implements f.c<Y> {

        /* renamed from: j  reason: collision with root package name */
        public static final /* synthetic */ b f450j = new Object();
    }

    boolean a();

    InterfaceC0158h d(d0 d0Var);

    L r(boolean z4, boolean z5, u3.l<? super Throwable, l3.g> lVar);

    boolean start();

    CancellationException t();

    void w(CancellationException cancellationException);
}
