package e0;

import M.C0229k;
import android.database.Cursor;
import android.os.Parcel;
import c2.InterfaceC0374a;
import com.google.android.gms.internal.ads.ZT;
import com.google.android.gms.internal.ads.z8;
import com.google.android.material.textfield.TextInputLayout;
import p1.q;

/* renamed from: e0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class C0405a implements C0229k.b, TextInputLayout.e, q.a {
    public static int a(int i4, int i5, int i6) {
        return ZT.t(i4) + i5 + i6;
    }

    public static InterfaceC0374a b(Parcel parcel, Parcel parcel2) {
        InterfaceC0374a Z3 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
        z8.b(parcel2);
        return Z3;
    }

    public static String c(String str, int i4) {
        return str + i4;
    }

    @Override // p1.q.a
    public Object apply(Object obj) {
        boolean z4;
        if (((Cursor) obj).getCount() > 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        return Boolean.valueOf(z4);
    }
}
