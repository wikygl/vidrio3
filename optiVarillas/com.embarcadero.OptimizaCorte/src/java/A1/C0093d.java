package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.bk;
import com.google.android.gms.internal.ads.ck;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;

/* renamed from: A1.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0093d extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f108b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ eg f109c;

    public C0093d(Context context, bg bgVar) {
        this.f108b = context;
        this.f109c = bgVar;
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object a() {
        return null;
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.Z1(new c2.b(this.f108b), this.f109c, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        ck x8Var;
        Context context = this.f108b;
        c2.b bVar = new c2.b(context);
        try {
            try {
                IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.DynamiteSignalGeneratorCreatorImpl");
                int i4 = bk.j;
                if (b4 == null) {
                    x8Var = null;
                } else {
                    ck queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.signals.ISignalGeneratorCreator");
                    if (queryLocalInterface instanceof ck) {
                        x8Var = queryLocalInterface;
                    } else {
                        x8Var = new x8(b4, "com.google.android.gms.ads.internal.signals.ISignalGeneratorCreator");
                    }
                }
                return x8Var.j0(bVar, this.f109c);
            } catch (Exception e4) {
                throw new Exception(e4);
            }
        } catch (E1.o | RemoteException | NullPointerException unused) {
            return null;
        }
    }
}
