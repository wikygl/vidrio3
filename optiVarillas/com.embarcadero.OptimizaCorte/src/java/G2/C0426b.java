package g2;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable;
import e0.C0405a;

/* renamed from: g2.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0426b {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ int f3498a = 0;

    static {
        C0426b.class.getClassLoader();
    }

    public static Parcelable a(Parcel parcel, Parcelable.Creator creator) {
        if (parcel.readInt() == 0) {
            return null;
        }
        return (Parcelable) creator.createFromParcel(parcel);
    }

    public static void b(Parcel parcel) {
        int dataAvail = parcel.dataAvail();
        if (dataAvail <= 0) {
            return;
        }
        throw new BadParcelableException(C0405a.c("Parcel data not fully consumed, unread size: ", dataAvail));
    }
}
