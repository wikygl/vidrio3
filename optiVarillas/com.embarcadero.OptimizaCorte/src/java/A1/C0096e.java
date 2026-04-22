package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.Bh;
import com.google.android.gms.internal.ads.Ch;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;

/* renamed from: A1.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0096e extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f111b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ eg f112c;

    public C0096e(Context context, bg bgVar) {
        this.f111b = context;
        this.f112c = bgVar;
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object a() {
        return null;
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.D1(new c2.b(this.f111b), this.f112c, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        Ch x8Var;
        Context context = this.f111b;
        c2.b bVar = new c2.b(context);
        try {
            try {
                IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.DynamiteOfflineUtilsCreatorImpl");
                int i4 = Bh.j;
                if (b4 == null) {
                    x8Var = null;
                } else {
                    Ch queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.offline.IOfflineUtilsCreator");
                    if (queryLocalInterface instanceof Ch) {
                        x8Var = queryLocalInterface;
                    } else {
                        x8Var = new x8(b4, "com.google.android.gms.ads.internal.offline.IOfflineUtilsCreator");
                    }
                }
                return x8Var.j0(bVar, this.f112c);
            } catch (Exception e4) {
                throw new Exception(e4);
            }
        } catch (E1.o | RemoteException | NullPointerException unused) {
            return null;
        }
    }
}
