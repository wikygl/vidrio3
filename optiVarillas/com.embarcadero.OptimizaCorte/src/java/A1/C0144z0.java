package A1;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import com.google.android.gms.internal.ads.x8;
import com.google.android.gms.internal.ads.z8;
import java.util.ArrayList;
import java.util.List;

/* renamed from: A1.z0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0144z0 extends x8 implements A0 {
    public C0144z0(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.client.IResponseInfo");
    }

    @Override // A1.A0
    public final Bundle b() {
        Parcel Z3 = Z(B(), 5);
        Bundle bundle = (Bundle) z8.a(Z3, Bundle.CREATOR);
        Z3.recycle();
        return bundle;
    }

    @Override // A1.A0
    public final G1 d() {
        Parcel Z3 = Z(B(), 4);
        G1 g12 = (G1) z8.a(Z3, G1.CREATOR);
        Z3.recycle();
        return g12;
    }

    @Override // A1.A0
    public final String f() {
        Parcel Z3 = Z(B(), 2);
        String readString = Z3.readString();
        Z3.recycle();
        return readString;
    }

    @Override // A1.A0
    public final String g() {
        Parcel Z3 = Z(B(), 6);
        String readString = Z3.readString();
        Z3.recycle();
        return readString;
    }

    @Override // A1.A0
    public final String h() {
        Parcel Z3 = Z(B(), 1);
        String readString = Z3.readString();
        Z3.recycle();
        return readString;
    }

    @Override // A1.A0
    public final List j() {
        Parcel Z3 = Z(B(), 3);
        ArrayList createTypedArrayList = Z3.createTypedArrayList(G1.CREATOR);
        Z3.recycle();
        return createTypedArrayList;
    }
}
