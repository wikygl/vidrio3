package T1;

import W1.F;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import h2.BinderC0439b;
import h2.C0440c;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class s extends BinderC0439b implements F {

    /* renamed from: j  reason: collision with root package name */
    public final int f2364j;

    public s(byte[] bArr) {
        super("com.google.android.gms.common.internal.ICertData");
        if (bArr.length == 25) {
            this.f2364j = Arrays.hashCode(bArr);
            return;
        }
        throw new IllegalArgumentException();
    }

    public static byte[] Z(String str) {
        try {
            return str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e4) {
            throw new AssertionError(e4);
        }
    }

    @Override // h2.BinderC0439b
    public final boolean B(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                return false;
            }
            parcel2.writeNoException();
            parcel2.writeInt(this.f2364j);
        } else {
            c2.b i5 = i();
            parcel2.writeNoException();
            C0440c.c(parcel2, i5);
        }
        return true;
    }

    @Override // W1.F
    public final int e() {
        return this.f2364j;
    }

    public final boolean equals(Object obj) {
        if (obj != null && (obj instanceof F)) {
            try {
                F f = (F) obj;
                if (f.e() != this.f2364j) {
                    return false;
                }
                return Arrays.equals(p0(), (byte[]) c2.b.p0(f.i()));
            } catch (RemoteException e4) {
                Log.e("GoogleCertificates", "Failed to get Google certificates from remote", e4);
            }
        }
        return false;
    }

    public final int hashCode() {
        return this.f2364j;
    }

    @Override // W1.F
    public final c2.b i() {
        return new c2.b(p0());
    }

    public abstract byte[] p0();
}
