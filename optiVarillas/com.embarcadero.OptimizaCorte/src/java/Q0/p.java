package Q0;

import A1.N0;
import D1.C0183d0;
import D1.C0202w;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.MG;
import com.google.android.gms.internal.ads.P1;
import com.google.android.gms.internal.ads.Sg;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.VK;
import com.google.android.gms.internal.ads.W5;
import com.google.android.gms.internal.ads.fZ;
import com.google.android.gms.internal.ads.gA;
import com.google.android.gms.internal.ads.yi;
import com.google.android.gms.internal.ads.zH;
import java.io.File;
import java.util.ArrayList;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class p implements W5, G1.c, TN {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1991j;

    /* renamed from: k  reason: collision with root package name */
    public Object f1992k;

    /* renamed from: l  reason: collision with root package name */
    public final Object f1993l;

    public p(Context context) {
        this.f1991j = 1;
        this.f1993l = context;
        this.f1992k = null;
    }

    public File a() {
        if (((File) this.f1992k) == null) {
            this.f1992k = new File(((Context) this.f1993l).getCacheDir(), "volley");
        }
        return (File) this.f1992k;
    }

    public boolean c(int i4) {
        return ((P1) this.f1992k).a.get(i4);
    }

    @Override // G1.c
    public void e(gA gAVar) {
        try {
            ((Sg) this.f1992k).s(gAVar.a());
        } catch (RemoteException e4) {
            E1.m.e("", e4);
        }
    }

    public void g(Object obj) {
        switch (this.f1991j) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) obj;
                try {
                    boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.R1)).booleanValue();
                    yi yiVar = (yi) this.f1992k;
                    if (booleanValue) {
                        yiVar.l4(parcelFileDescriptor, (Ei) this.f1993l);
                    } else {
                        yiVar.S(parcelFileDescriptor);
                    }
                    return;
                } catch (RemoteException e4) {
                    C0183d0.l("Service can't call client", e4);
                    return;
                }
            default:
                return;
        }
    }

    public void m(Throwable th) {
        String message;
        switch (this.f1991j) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                try {
                    yi yiVar = (yi) this.f1992k;
                    N0 a4 = MG.a(th);
                    if (VK.a(th.getMessage())) {
                        message = a4.f63k;
                    } else {
                        message = th.getMessage();
                    }
                    yiVar.g0(new C0202w(message, a4.f62j));
                    return;
                } catch (RemoteException e4) {
                    C0183d0.l("Service can't call client", e4);
                    return;
                }
            default:
                zH zHVar = (zH) this.f1993l;
                zHVar.d(th);
                zHVar.e(false);
                ((FH) this.f1992k).a(zHVar);
                return;
        }
    }

    public p(com.android.billingclient.api.a aVar, ArrayList arrayList) {
        this.f1991j = 0;
        this.f1992k = arrayList;
        this.f1993l = aVar;
    }

    public /* synthetic */ p(Object obj, int i4, Object obj2) {
        this.f1991j = i4;
        this.f1992k = obj;
        this.f1993l = obj2;
    }

    public p(P1 p12, SparseArray sparseArray) {
        this.f1991j = 6;
        this.f1992k = p12;
        SparseBooleanArray sparseBooleanArray = p12.a;
        SparseArray sparseArray2 = new SparseArray(sparseBooleanArray.size());
        for (int i4 = 0; i4 < sparseBooleanArray.size(); i4++) {
            int a4 = p12.a(i4);
            fZ fZVar = (fZ) sparseArray.get(a4);
            fZVar.getClass();
            sparseArray2.append(a4, fZVar);
        }
        this.f1993l = sparseArray2;
    }

    private final void b(Object obj) {
    }
}
