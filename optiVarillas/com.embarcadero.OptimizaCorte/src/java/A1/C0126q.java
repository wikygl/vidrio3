package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;

/* renamed from: A1.q  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0126q extends y8 implements InterfaceC0133u {

    /* renamed from: j  reason: collision with root package name */
    public final InterfaceC0084a f167j;

    public C0126q(InterfaceC0084a interfaceC0084a) {
        super("com.google.android.gms.ads.internal.client.IAdClickListener");
        this.f167j = interfaceC0084a;
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 == 1) {
            u();
            parcel2.writeNoException();
            return true;
        }
        return false;
    }

    @Override // A1.InterfaceC0133u
    public final void u() {
        this.f167j.m();
    }
}
