package f2;

import android.os.BadParcelableException;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Status;
import j2.AbstractBinderC0676f;
import j2.BinderC0672b;
import j2.C0671a;
import p2.C0758g;

/* renamed from: f2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class BinderC0414a extends Binder implements IInterface {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f3397j;

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        int i4 = this.f3397j;
        return this;
    }

    @Override // android.os.Binder
    public final boolean onTransact(int i4, Parcel parcel, Parcel parcel2, int i5) {
        Parcelable parcelable;
        Q1.e createFromParcel;
        U1.b bVar;
        boolean z4 = false;
        Object obj = null;
        switch (this.f3397j) {
            case 0:
                if (i4 > 16777215) {
                    if (super.onTransact(i4, parcel, parcel2, i5)) {
                        return true;
                    }
                } else {
                    parcel.enforceInterface(getInterfaceDescriptor());
                }
                d dVar = (d) this;
                if (i4 == 1) {
                    Parcelable.Creator creator = Status.CREATOR;
                    int i6 = b.f3398a;
                    if (parcel.readInt() == 0) {
                        parcelable = null;
                    } else {
                        parcelable = (Parcelable) creator.createFromParcel(parcel);
                    }
                    Status status = (Status) parcelable;
                    Parcelable.Creator<Q1.e> creator2 = Q1.e.CREATOR;
                    if (parcel.readInt() == 0) {
                        createFromParcel = null;
                    } else {
                        createFromParcel = creator2.createFromParcel(parcel);
                    }
                    Q1.e eVar = createFromParcel;
                    i iVar = (i) dVar;
                    if (eVar != null) {
                        obj = new Q1.b(eVar.f2017j, eVar.f2018k);
                    }
                    int i7 = status.j;
                    C0758g c0758g = iVar.f3404k;
                    if (i7 <= 0) {
                        c0758g.f5552a.m(obj);
                    } else {
                        if (status.l != null) {
                            bVar = new U1.b(status);
                        } else {
                            bVar = new U1.b(status);
                        }
                        c0758g.f5552a.l(bVar);
                    }
                    z4 = true;
                }
                return z4;
            default:
                if (i4 > 16777215) {
                    if (super.onTransact(i4, parcel, parcel2, i5)) {
                        return true;
                    }
                } else {
                    parcel.enforceInterface(getInterfaceDescriptor());
                }
                AbstractBinderC0676f abstractBinderC0676f = (AbstractBinderC0676f) this;
                if (i4 == 1) {
                    Parcelable.Creator creator3 = Bundle.CREATOR;
                    int i8 = C0671a.f4791a;
                    if (parcel.readInt() != 0) {
                        obj = (Parcelable) creator3.createFromParcel(parcel);
                    }
                    Bundle bundle = (Bundle) obj;
                    int dataAvail = parcel.dataAvail();
                    if (dataAvail <= 0) {
                        ((BinderC0672b) abstractBinderC0676f).Z(bundle);
                        parcel2.writeNoException();
                        z4 = true;
                    } else {
                        StringBuilder sb = new StringBuilder(56);
                        sb.append("Parcel data not fully consumed, unread size: ");
                        sb.append(dataAvail);
                        throw new BadParcelableException(sb.toString());
                    }
                }
                return z4;
        }
    }
}
