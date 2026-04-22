package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.x8;

/* renamed from: A1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0090c extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f105b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ eg f106c;

    public C0090c(Context context, bg bgVar) {
        this.f105b = context;
        this.f106c = bgVar;
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object a() {
        C0120n.b(this.f105b, "out_of_context_tester");
        return null;
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        Context context = this.f105b;
        c2.b bVar = new c2.b(context);
        Gb.a(context);
        if (((Boolean) r.f168d.f171c.a(Gb.t8)).booleanValue()) {
            return AbstractC0122o.f160a.y2(bVar, this.f106c, 241199000);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // A1.AbstractC0122o
    public final Object c() {
        C0142y0 c0142y0;
        Context context = this.f105b;
        c2.b bVar = new c2.b(context);
        Gb.a(context);
        if (!((Boolean) r.f168d.f171c.a(Gb.t8)).booleanValue()) {
            return null;
        }
        try {
            try {
                IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.DynamiteOutOfContextTesterCreatorImpl");
                if (b4 == null) {
                    c0142y0 = 0;
                } else {
                    IInterface queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.client.IOutOfContextTesterCreator");
                    if (queryLocalInterface instanceof C0142y0) {
                        c0142y0 = (C0142y0) queryLocalInterface;
                    } else {
                        c0142y0 = new x8(b4, "com.google.android.gms.ads.internal.client.IOutOfContextTesterCreator");
                    }
                }
                return c0142y0.g2(bVar, this.f106c);
            } catch (Exception e4) {
                throw new Exception(e4);
            }
        } catch (E1.o e5) {
            e = e5;
            Xh.b(context).a("ClientApiBroker.getOutOfContextTester", e);
            return null;
        } catch (RemoteException e6) {
            e = e6;
            Xh.b(context).a("ClientApiBroker.getOutOfContextTester", e);
            return null;
        } catch (NullPointerException e7) {
            e = e7;
            Xh.b(context).a("ClientApiBroker.getOutOfContextTester", e);
            return null;
        }
    }
}
