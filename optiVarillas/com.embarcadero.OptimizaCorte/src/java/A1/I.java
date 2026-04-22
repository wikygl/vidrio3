package A1;

import android.database.Cursor;
import android.os.Parcel;
import android.util.Log;
import c2.InterfaceC0374a;
import p1.q;
import u0.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class I implements q.a, f.e {
    public static InterfaceC0374a b(Parcel parcel) {
        InterfaceC0374a Z3 = InterfaceC0374a.AbstractBinderC0042a.Z(parcel.readStrongBinder());
        parcel.recycle();
        return Z3;
    }

    public static void c(com.android.billingclient.api.a aVar) {
        Log.d("BILLING_V5_COPY", "onAcknowledgePurchaseResponse: " + aVar);
    }

    @Override // u0.f.e
    public void a(f.d dVar, u0.f fVar) {
        dVar.g(fVar);
    }

    @Override // p1.q.a
    public Object apply(Object obj) {
        Cursor cursor = (Cursor) obj;
        if (!cursor.moveToNext()) {
            return null;
        }
        return Long.valueOf(cursor.getLong(0));
    }
}
