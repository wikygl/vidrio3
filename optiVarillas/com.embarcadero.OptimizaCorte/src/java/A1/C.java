package A1;

import android.os.Parcel;
import com.google.android.gms.internal.ads.y8;
import com.google.android.gms.internal.ads.z8;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class C extends y8 implements D {
    public C() {
        super("com.google.android.gms.ads.internal.client.IAdLoader");
    }

    public final boolean B4(int i4, Parcel parcel, Parcel parcel2) {
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 != 4) {
                        if (i4 != 5) {
                            return false;
                        }
                        int readInt = parcel.readInt();
                        z8.b(parcel);
                        X3((y1) z8.a(parcel, y1.CREATOR), readInt);
                        parcel2.writeNoException();
                    } else {
                        String d4 = d();
                        parcel2.writeNoException();
                        parcel2.writeString(d4);
                    }
                } else {
                    boolean f = f();
                    parcel2.writeNoException();
                    ClassLoader classLoader = z8.a;
                    parcel2.writeInt(f ? 1 : 0);
                }
            } else {
                String b4 = b();
                parcel2.writeNoException();
                parcel2.writeString(b4);
            }
        } else {
            z8.b(parcel);
            h3((y1) z8.a(parcel, y1.CREATOR));
            parcel2.writeNoException();
        }
        return true;
    }
}
