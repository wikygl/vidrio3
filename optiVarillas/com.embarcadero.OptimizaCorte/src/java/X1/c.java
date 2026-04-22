package X1;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import e0.C0405a;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a extends RuntimeException {
        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public a(java.lang.String r3, android.os.Parcel r4) {
            /*
                r2 = this;
                int r0 = r4.dataPosition()
                int r4 = r4.dataSize()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                r1.append(r3)
                java.lang.String r3 = " Parcel: pos="
                r1.append(r3)
                r1.append(r0)
                java.lang.String r3 = " size="
                r1.append(r3)
                r1.append(r4)
                java.lang.String r3 = r1.toString()
                r2.<init>(r3)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: X1.c.a.<init>(java.lang.String, android.os.Parcel):void");
        }
    }

    public static Bundle a(Parcel parcel, int i4) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        Bundle readBundle = parcel.readBundle();
        parcel.setDataPosition(dataPosition + m4);
        return readBundle;
    }

    public static byte[] b(Parcel parcel, int i4) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        byte[] createByteArray = parcel.createByteArray();
        parcel.setDataPosition(dataPosition + m4);
        return createByteArray;
    }

    public static <T extends Parcelable> T c(Parcel parcel, int i4, Parcelable.Creator<T> creator) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        T createFromParcel = creator.createFromParcel(parcel);
        parcel.setDataPosition(dataPosition + m4);
        return createFromParcel;
    }

    public static String d(Parcel parcel, int i4) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        String readString = parcel.readString();
        parcel.setDataPosition(dataPosition + m4);
        return readString;
    }

    public static String[] e(Parcel parcel, int i4) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        String[] createStringArray = parcel.createStringArray();
        parcel.setDataPosition(dataPosition + m4);
        return createStringArray;
    }

    public static ArrayList<String> f(Parcel parcel, int i4) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        ArrayList<String> createStringArrayList = parcel.createStringArrayList();
        parcel.setDataPosition(dataPosition + m4);
        return createStringArrayList;
    }

    public static <T> T[] g(Parcel parcel, int i4, Parcelable.Creator<T> creator) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        T[] tArr = (T[]) parcel.createTypedArray(creator);
        parcel.setDataPosition(dataPosition + m4);
        return tArr;
    }

    public static void h(Parcel parcel, int i4) {
        if (parcel.dataPosition() == i4) {
            return;
        }
        throw new a(C0405a.c("Overread allowed size end=", i4), parcel);
    }

    public static boolean i(Parcel parcel, int i4) {
        p(parcel, i4, 4);
        if (parcel.readInt() != 0) {
            return true;
        }
        return false;
    }

    public static IBinder j(Parcel parcel, int i4) {
        int m4 = m(parcel, i4);
        int dataPosition = parcel.dataPosition();
        if (m4 == 0) {
            return null;
        }
        IBinder readStrongBinder = parcel.readStrongBinder();
        parcel.setDataPosition(dataPosition + m4);
        return readStrongBinder;
    }

    public static int k(Parcel parcel, int i4) {
        p(parcel, i4, 4);
        return parcel.readInt();
    }

    public static long l(Parcel parcel, int i4) {
        p(parcel, i4, 8);
        return parcel.readLong();
    }

    public static int m(Parcel parcel, int i4) {
        if ((i4 & (-65536)) != -65536) {
            return (char) (i4 >> 16);
        }
        return parcel.readInt();
    }

    public static void n(Parcel parcel, int i4) {
        parcel.setDataPosition(parcel.dataPosition() + m(parcel, i4));
    }

    public static int o(Parcel parcel) {
        int readInt = parcel.readInt();
        int m4 = m(parcel, readInt);
        char c4 = (char) readInt;
        int dataPosition = parcel.dataPosition();
        if (c4 == 20293) {
            int i4 = m4 + dataPosition;
            if (i4 >= dataPosition && i4 <= parcel.dataSize()) {
                return i4;
            }
            throw new a(b.d(dataPosition, i4, "Size read is invalid start=", " end="), parcel);
        }
        throw new a("Expected object header. Got 0x".concat(String.valueOf(Integer.toHexString(readInt))), parcel);
    }

    public static void p(Parcel parcel, int i4, int i5) {
        int m4 = m(parcel, i4);
        if (m4 == i5) {
            return;
        }
        String hexString = Integer.toHexString(m4);
        StringBuilder sb = new StringBuilder("Expected size ");
        sb.append(i5);
        sb.append(" got ");
        sb.append(m4);
        sb.append(" (0x");
        throw new a(C.b.c(sb, hexString, ")"), parcel);
    }
}
