package A1;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.FrameLayout;
import c2.c;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Od;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.Yh;
import com.google.android.gms.internal.ads.bd;
import com.google.android.gms.internal.ads.cd;
import com.google.android.gms.internal.ads.dd;
import com.google.android.gms.internal.ads.ed;
import com.google.android.gms.internal.ads.fd;
import com.google.android.gms.internal.ads.gd;

/* renamed from: A1.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0116l extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ FrameLayout f143b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ FrameLayout f144c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ Context f145d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ C0120n f146e;

    public C0116l(C0120n c0120n, FrameLayout frameLayout, FrameLayout frameLayout2, Context context) {
        this.f143b = frameLayout;
        this.f144c = frameLayout2;
        this.f145d = context;
        this.f146e = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f145d, "native_ad_view_delegate");
        return new cd();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.O3(new c2.b(this.f143b), new c2.b(this.f144c));
    }

    @Override // A1.AbstractC0122o
    public final Object c() {
        gd edVar;
        dd bdVar;
        dd bdVar2;
        Context context = this.f145d;
        Gb.a(context);
        boolean booleanValue = ((Boolean) r.f168d.f171c.a(Gb.v9)).booleanValue();
        FrameLayout frameLayout = this.f144c;
        FrameLayout frameLayout2 = this.f143b;
        C0120n c0120n = this.f146e;
        if (booleanValue) {
            try {
                c2.b bVar = new c2.b(context);
                c2.b bVar2 = new c2.b(frameLayout2);
                c2.b bVar3 = new c2.b(frameLayout);
                try {
                    IBinder b4 = E1.p.a(context).b("com.google.android.gms.ads.ChimeraNativeAdViewDelegateCreatorImpl");
                    int i4 = fd.j;
                    if (b4 == null) {
                        edVar = null;
                    } else {
                        gd queryLocalInterface = b4.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegateCreator");
                        if (queryLocalInterface instanceof gd) {
                            edVar = queryLocalInterface;
                        } else {
                            edVar = new ed(b4);
                        }
                    }
                    IBinder j4 = edVar.j4(bVar, bVar2, bVar3);
                    int i5 = cd.j;
                    if (j4 == null) {
                        return null;
                    }
                    dd queryLocalInterface2 = j4.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegate");
                    if (queryLocalInterface2 instanceof dd) {
                        bdVar = queryLocalInterface2;
                    } else {
                        bdVar = new bd(j4);
                    }
                    return bdVar;
                } catch (Exception e4) {
                    throw new Exception(e4);
                }
            } catch (E1.o | RemoteException | NullPointerException e5) {
                Yh b5 = Xh.b(context);
                c0120n.getClass();
                b5.a("ClientApiBroker.createNativeAdViewDelegate", e5);
                return null;
            }
        }
        Od od = (Od) c0120n.f156m;
        od.getClass();
        try {
            IBinder j42 = ((gd) od.b(context)).j4(new c2.b(context), new c2.b(frameLayout2), new c2.b(frameLayout));
            if (j42 == null) {
                return null;
            }
            dd queryLocalInterface3 = j42.queryLocalInterface("com.google.android.gms.ads.internal.formats.client.INativeAdViewDelegate");
            if (queryLocalInterface3 instanceof dd) {
                bdVar2 = queryLocalInterface3;
            } else {
                bdVar2 = new bd(j42);
            }
            return bdVar2;
        } catch (RemoteException e6) {
            e = e6;
            E1.m.h("Could not create remote NativeAdViewDelegate.", e);
            return null;
        } catch (c.a e7) {
            e = e7;
            E1.m.h("Could not create remote NativeAdViewDelegate.", e);
            return null;
        }
    }
}
