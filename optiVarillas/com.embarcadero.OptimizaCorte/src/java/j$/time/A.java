package j$.time;

import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class A implements Serializable {
    private static final long serialVersionUID = 8352817235686L;

    static {
        Map.Entry[] entryArr = {j$.com.android.tools.r8.a.j("ACT", "Australia/Darwin"), j$.com.android.tools.r8.a.j("AET", "Australia/Sydney"), j$.com.android.tools.r8.a.j("AGT", "America/Argentina/Buenos_Aires"), j$.com.android.tools.r8.a.j("ART", "Africa/Cairo"), j$.com.android.tools.r8.a.j("AST", "America/Anchorage"), j$.com.android.tools.r8.a.j("BET", "America/Sao_Paulo"), j$.com.android.tools.r8.a.j("BST", "Asia/Dhaka"), j$.com.android.tools.r8.a.j("CAT", "Africa/Harare"), j$.com.android.tools.r8.a.j("CNT", "America/St_Johns"), j$.com.android.tools.r8.a.j("CST", "America/Chicago"), j$.com.android.tools.r8.a.j("CTT", "Asia/Shanghai"), j$.com.android.tools.r8.a.j("EAT", "Africa/Addis_Ababa"), j$.com.android.tools.r8.a.j("ECT", "Europe/Paris"), j$.com.android.tools.r8.a.j("IET", "America/Indiana/Indianapolis"), j$.com.android.tools.r8.a.j("IST", "Asia/Kolkata"), j$.com.android.tools.r8.a.j("JST", "Asia/Tokyo"), j$.com.android.tools.r8.a.j("MIT", "Pacific/Apia"), j$.com.android.tools.r8.a.j("NET", "Asia/Yerevan"), j$.com.android.tools.r8.a.j("NST", "Pacific/Auckland"), j$.com.android.tools.r8.a.j("PLT", "Asia/Karachi"), j$.com.android.tools.r8.a.j("PNT", "America/Phoenix"), j$.com.android.tools.r8.a.j("PRT", "America/Puerto_Rico"), j$.com.android.tools.r8.a.j("PST", "America/Los_Angeles"), j$.com.android.tools.r8.a.j("SST", "Pacific/Guadalcanal"), j$.com.android.tools.r8.a.j("VST", "Asia/Ho_Chi_Minh"), j$.com.android.tools.r8.a.j("EST", "-05:00"), j$.com.android.tools.r8.a.j("MST", "-07:00"), j$.com.android.tools.r8.a.j("HST", "-10:00")};
        HashMap hashMap = new HashMap(28);
        for (int i4 = 0; i4 < 28; i4++) {
            Map.Entry entry = entryArr[i4];
            Object requireNonNull = Objects.requireNonNull(entry.getKey());
            if (hashMap.put(requireNonNull, Objects.requireNonNull(entry.getValue())) != null) {
                throw new IllegalArgumentException("duplicate key: " + requireNonNull);
            }
        }
        Collections.unmodifiableMap(hashMap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public A() {
        if (getClass() != B.class && getClass() != C.class) {
            throw new AssertionError("Invalid subclass");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static A E(String str) {
        int i4;
        Objects.requireNonNull(str, "zoneId");
        if (str.length() <= 1 || str.startsWith("+") || str.startsWith("-")) {
            return B.K(str);
        }
        if (str.startsWith("UTC") || str.startsWith("GMT")) {
            i4 = 3;
        } else if (!str.startsWith("UT")) {
            return C.I(str);
        } else {
            i4 = 2;
        }
        return G(str, i4);
    }

    public static A F(String str, B b4) {
        Objects.requireNonNull(str, "prefix");
        Objects.requireNonNull(b4, "offset");
        if (str.isEmpty()) {
            return b4;
        }
        if (str.equals("GMT") || str.equals("UTC") || str.equals("UT")) {
            if (b4.J() != 0) {
                str = str.concat(b4.i());
            }
            return new C(str, j$.time.zone.f.i(b4));
        }
        throw new IllegalArgumentException("prefix should be GMT, UTC or UT, is: ".concat(str));
    }

    private static A G(String str, int i4) {
        String substring = str.substring(0, i4);
        if (str.length() == i4) {
            return F(substring, B.f3838e);
        }
        if (str.charAt(i4) == '+' || str.charAt(i4) == '-') {
            try {
                B K3 = B.K(str.substring(i4));
                return K3 == B.f3838e ? F(substring, K3) : F(substring, K3);
            } catch (C0482c e4) {
                throw new RuntimeException("Invalid ID for offset-based ZoneId: ".concat(str), e4);
            }
        }
        return C.I(str);
    }

    private void readObject(ObjectInputStream objectInputStream) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new v((byte) 7, this);
    }

    public abstract j$.time.zone.f D();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void H(DataOutput dataOutput);

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof A) {
            return i().equals(((A) obj).i());
        }
        return false;
    }

    public int hashCode() {
        return i().hashCode();
    }

    public abstract String i();

    public String toString() {
        return i();
    }
}
