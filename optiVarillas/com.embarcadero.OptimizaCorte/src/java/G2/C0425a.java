package g2;

import android.os.IBinder;
import android.os.IInterface;

/* renamed from: g2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class C0425a implements IInterface {

    /* renamed from: j  reason: collision with root package name */
    public final IBinder f3496j;

    /* renamed from: k  reason: collision with root package name */
    public final String f3497k;

    public C0425a(IBinder iBinder, String str) {
        this.f3496j = iBinder;
        this.f3497k = str;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.f3496j;
    }
}
