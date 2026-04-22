package H;

import M.W;
import V1.InterfaceC0304j;
import X1.b;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.internal.play_billing.j0;
import com.google.gson.internal.i;
import f2.c;
import f2.e;
import i2.C0457c;
import i2.Z;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class a implements W, i, InterfaceC0304j, Z {
    public static int f(int i4, int i5, int i6) {
        if (i4 < i5) {
            return i5;
        }
        if (i4 > i6) {
            return i6;
        }
        return i4;
    }

    public static String g(String str, Object... objArr) {
        int indexOf;
        String sb;
        int i4 = 0;
        for (int i5 = 0; i5 < objArr.length; i5++) {
            Object obj = objArr[i5];
            if (obj == null) {
                sb = "null";
            } else {
                try {
                    sb = obj.toString();
                } catch (Exception e4) {
                    String str2 = obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
                    Logger.getLogger("com.google.common.base.Strings").log(Level.WARNING, "Exception during lenientFormat for " + str2, (Throwable) e4);
                    StringBuilder f = b.f("<", str2, " threw ");
                    f.append(e4.getClass().getName());
                    f.append(">");
                    sb = f.toString();
                }
            }
            objArr[i5] = sb;
        }
        StringBuilder sb2 = new StringBuilder((objArr.length * 16) + str.length());
        int i6 = 0;
        while (i4 < objArr.length && (indexOf = str.indexOf("%s", i6)) != -1) {
            sb2.append((CharSequence) str, i6, indexOf);
            sb2.append(objArr[i4]);
            i6 = indexOf + 2;
            i4++;
        }
        sb2.append((CharSequence) str, i6, str.length());
        if (i4 < objArr.length) {
            sb2.append(" [");
            sb2.append(objArr[i4]);
            for (int i7 = i4 + 1; i7 < objArr.length; i7++) {
                sb2.append(", ");
                sb2.append(objArr[i7]);
            }
            sb2.append(']');
        }
        return sb2.toString();
    }

    public static void h(Parcel parcel, int i4, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeBundle(bundle);
        v(parcel, r4);
    }

    public static void i(Parcel parcel, int i4, byte[] bArr) {
        if (bArr == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeByteArray(bArr);
        v(parcel, r4);
    }

    public static void j(Parcel parcel, int i4, IBinder iBinder) {
        if (iBinder == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeStrongBinder(iBinder);
        v(parcel, r4);
    }

    public static void l(Parcel parcel, int i4, Parcelable parcelable, int i5) {
        if (parcelable == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcelable.writeToParcel(parcel, i5);
        v(parcel, r4);
    }

    public static void m(Parcel parcel, int i4, String str) {
        if (str == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeString(str);
        v(parcel, r4);
    }

    public static void n(Parcel parcel, int i4, String[] strArr) {
        if (strArr == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeStringArray(strArr);
        v(parcel, r4);
    }

    public static void o(Parcel parcel, int i4, List list) {
        if (list == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeStringList(list);
        v(parcel, r4);
    }

    public static void p(Parcel parcel, int i4, Parcelable[] parcelableArr, int i5) {
        if (parcelableArr == null) {
            return;
        }
        int r4 = r(parcel, i4);
        parcel.writeInt(parcelableArr.length);
        for (Parcelable parcelable : parcelableArr) {
            if (parcelable == null) {
                parcel.writeInt(0);
            } else {
                int dataPosition = parcel.dataPosition();
                parcel.writeInt(1);
                int dataPosition2 = parcel.dataPosition();
                parcelable.writeToParcel(parcel, i5);
                int dataPosition3 = parcel.dataPosition();
                parcel.setDataPosition(dataPosition);
                parcel.writeInt(dataPosition3 - dataPosition2);
                parcel.setDataPosition(dataPosition3);
            }
        }
        v(parcel, r4);
    }

    public static void q(Parcel parcel, int i4, List list) {
        if (list == null) {
            return;
        }
        int r4 = r(parcel, i4);
        int size = list.size();
        parcel.writeInt(size);
        for (int i5 = 0; i5 < size; i5++) {
            Parcelable parcelable = (Parcelable) list.get(i5);
            if (parcelable == null) {
                parcel.writeInt(0);
            } else {
                int dataPosition = parcel.dataPosition();
                parcel.writeInt(1);
                int dataPosition2 = parcel.dataPosition();
                parcelable.writeToParcel(parcel, 0);
                int dataPosition3 = parcel.dataPosition();
                parcel.setDataPosition(dataPosition);
                parcel.writeInt(dataPosition3 - dataPosition2);
                parcel.setDataPosition(dataPosition3);
            }
        }
        v(parcel, r4);
    }

    public static int r(Parcel parcel, int i4) {
        parcel.writeInt(i4 | (-65536));
        parcel.writeInt(0);
        return parcel.dataPosition();
    }

    public static /* bridge */ /* synthetic */ void s(byte b4, byte b5, byte b6, byte b7, char[] cArr, int i4) {
        if (!z(b5)) {
            if ((((b5 + 112) + (b4 << 28)) >> 30) == 0 && !z(b6) && !z(b7)) {
                int i5 = ((b4 & 7) << 18) | ((b5 & 63) << 12) | ((b6 & 63) << 6) | (b7 & 63);
                cArr[i4] = (char) ((i5 >>> 10) + 55232);
                cArr[i4 + 1] = (char) ((i5 & 1023) + 56320);
                return;
            }
        }
        throw j0.a();
    }

    public static void t(Object obj) {
        if (obj != null) {
            return;
        }
        throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }

    public static /* bridge */ /* synthetic */ void u(byte b4, byte b5, byte b6, char[] cArr, int i4) {
        if (!z(b5)) {
            if (b4 == -32) {
                if (b5 >= -96) {
                    b4 = -32;
                }
            }
            if (b4 == -19) {
                if (b5 < -96) {
                    b4 = -19;
                }
            }
            if (!z(b6)) {
                cArr[i4] = (char) (((b4 & 15) << 12) | ((b5 & 63) << 6) | (b6 & 63));
                return;
            }
        }
        throw j0.a();
    }

    public static void v(Parcel parcel, int i4) {
        int dataPosition = parcel.dataPosition();
        parcel.setDataPosition(i4 - 4);
        parcel.writeInt(dataPosition - i4);
        parcel.setDataPosition(dataPosition);
    }

    public static /* bridge */ /* synthetic */ void w(byte b4, byte b5, char[] cArr, int i4) {
        if (b4 >= -62 && !z(b5)) {
            cArr[i4] = (char) (((b4 & 31) << 6) | (b5 & 63));
            return;
        }
        throw j0.a();
    }

    public static void x(Parcel parcel, int i4, int i5) {
        parcel.writeInt(i4 | (i5 << 16));
    }

    public static /* bridge */ /* synthetic */ boolean y(byte b4) {
        if (b4 >= 0) {
            return true;
        }
        return false;
    }

    public static boolean z(byte b4) {
        if (b4 > -65) {
            return true;
        }
        return false;
    }

    @Override // i2.Z
    public /* synthetic */ Object a() {
        return new C0457c();
    }

    @Override // V1.InterfaceC0304j
    public void d(Object obj, Object obj2) {
        e eVar = (e) ((c) obj).w();
        Q1.c cVar = new Q1.c(null, null);
        f2.i iVar = new f2.i((C0758g) obj2);
        Parcel obtain = Parcel.obtain();
        obtain.writeInterfaceToken(eVar.f3400k);
        int i4 = f2.b.f3398a;
        obtain.writeInt(1);
        cVar.writeToParcel(obtain, 0);
        obtain.writeStrongBinder(iVar);
        Parcel obtain2 = Parcel.obtain();
        try {
            eVar.f3399j.transact(1, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain.recycle();
            obtain2.recycle();
        }
    }

    public Object k() {
        return new ConcurrentHashMap();
    }

    @Override // M.W
    public void c() {
    }

    @Override // M.W
    public void e() {
    }
}
