package W1;

import W1.InterfaceC0320h;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import h2.C0438a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C extends X1.a {
    public static final Parcelable.Creator<C> CREATOR = new Object();

    /* renamed from: j  reason: collision with root package name */
    public final int f2636j;

    /* renamed from: k  reason: collision with root package name */
    public final IBinder f2637k;

    /* renamed from: l  reason: collision with root package name */
    public final T1.b f2638l;

    /* renamed from: m  reason: collision with root package name */
    public final boolean f2639m;

    /* renamed from: n  reason: collision with root package name */
    public final boolean f2640n;

    public C(int i4, IBinder iBinder, T1.b bVar, boolean z4, boolean z5) {
        this.f2636j = i4;
        this.f2637k = iBinder;
        this.f2638l = bVar;
        this.f2639m = z4;
        this.f2640n = z5;
    }

    public final boolean equals(Object obj) {
        Object c0438a;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C)) {
            return false;
        }
        C c4 = (C) obj;
        if (this.f2638l.equals(c4.f2638l)) {
            Object obj2 = null;
            IBinder iBinder = this.f2637k;
            if (iBinder == null) {
                c0438a = null;
            } else {
                int i4 = InterfaceC0320h.a.f2744j;
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
                if (queryLocalInterface instanceof InterfaceC0320h) {
                    c0438a = (InterfaceC0320h) queryLocalInterface;
                } else {
                    c0438a = new C0438a(iBinder, "com.google.android.gms.common.internal.IAccountAccessor");
                }
            }
            IBinder iBinder2 = c4.f2637k;
            if (iBinder2 != null) {
                int i5 = InterfaceC0320h.a.f2744j;
                IInterface queryLocalInterface2 = iBinder2.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
                if (queryLocalInterface2 instanceof InterfaceC0320h) {
                    obj2 = (InterfaceC0320h) queryLocalInterface2;
                } else {
                    obj2 = new C0438a(iBinder2, "com.google.android.gms.common.internal.IAccountAccessor");
                }
            }
            if (C0323k.a(c0438a, obj2)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i4) {
        int r4 = H.a.r(parcel, 20293);
        H.a.x(parcel, 1, 4);
        parcel.writeInt(this.f2636j);
        H.a.j(parcel, 2, this.f2637k);
        H.a.l(parcel, 3, this.f2638l, i4);
        H.a.x(parcel, 4, 4);
        parcel.writeInt(this.f2639m ? 1 : 0);
        H.a.x(parcel, 5, 4);
        parcel.writeInt(this.f2640n ? 1 : 0);
        H.a.v(parcel, r4);
    }
}
