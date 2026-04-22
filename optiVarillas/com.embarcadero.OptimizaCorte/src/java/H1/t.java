package h1;

import android.util.SparseArray;
import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class t {

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final SparseArray<a> f3578j;

        /* renamed from: k  reason: collision with root package name */
        public static final /* synthetic */ a[] f3579k;
        /* JADX INFO: Fake field, exist only in values array */
        a EF5;

        /* JADX WARN: Multi-variable type inference failed */
        static {
            Enum r5 = new Enum("UNKNOWN_MOBILE_SUBTYPE", 0);
            Enum r6 = new Enum("GPRS", 1);
            Enum r4 = new Enum("EDGE", 2);
            Enum r32 = new Enum("UMTS", 3);
            Enum r22 = new Enum("CDMA", 4);
            Enum r12 = new Enum("EVDO_0", 5);
            Enum r02 = new Enum("EVDO_A", 6);
            Enum r15 = new Enum("RTT", 7);
            Enum r14 = new Enum("HSDPA", 8);
            Enum r13 = new Enum("HSUPA", 9);
            Enum r122 = new Enum("HSPA", 10);
            Enum r11 = new Enum("IDEN", 11);
            Enum r10 = new Enum("EVDO_B", 12);
            Enum r9 = new Enum("LTE", 13);
            Enum r8 = new Enum("EHRPD", 14);
            Enum r7 = new Enum("HSPAP", 15);
            Enum r82 = new Enum("GSM", 16);
            Enum r72 = new Enum("TD_SCDMA", 17);
            Enum r83 = new Enum("IWLAN", 18);
            Enum r73 = new Enum("LTE_CA", 19);
            f3579k = new a[]{r5, r6, r4, r32, r22, r12, r02, r15, r14, r13, r122, r11, r10, r9, r8, r7, r82, r72, r83, r73, new Enum("COMBINED", 20)};
            SparseArray<a> sparseArray = new SparseArray<>();
            f3578j = sparseArray;
            sparseArray.put(0, r5);
            sparseArray.put(1, r6);
            sparseArray.put(2, r4);
            sparseArray.put(3, r32);
            sparseArray.put(4, r22);
            sparseArray.put(5, r12);
            sparseArray.put(6, r02);
            sparseArray.put(7, r15);
            sparseArray.put(8, r14);
            sparseArray.put(9, r13);
            sparseArray.put(10, r122);
            sparseArray.put(11, r11);
            sparseArray.put(12, r10);
            sparseArray.put(13, r9);
            sparseArray.put(14, r8);
            sparseArray.put(15, r7);
            sparseArray.put(16, r82);
            sparseArray.put(17, r72);
            sparseArray.put(18, r83);
            sparseArray.put(19, r73);
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f3579k.clone();
        }
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class b {

        /* renamed from: j  reason: collision with root package name */
        public static final SparseArray<b> f3580j;

        /* renamed from: k  reason: collision with root package name */
        public static final /* synthetic */ b[] f3581k;
        /* JADX INFO: Fake field, exist only in values array */
        b EF3;

        /* JADX WARN: Multi-variable type inference failed */
        static {
            Enum r32 = new Enum("MOBILE", 0);
            Enum r4 = new Enum("WIFI", 1);
            Enum r22 = new Enum("MOBILE_MMS", 2);
            Enum r12 = new Enum("MOBILE_SUPL", 3);
            Enum r02 = new Enum("MOBILE_DUN", 4);
            Enum r15 = new Enum("MOBILE_HIPRI", 5);
            Enum r14 = new Enum("WIMAX", 6);
            Enum r13 = new Enum("BLUETOOTH", 7);
            Enum r122 = new Enum("DUMMY", 8);
            Enum r11 = new Enum("ETHERNET", 9);
            Enum r10 = new Enum("MOBILE_FOTA", 10);
            Enum r9 = new Enum("MOBILE_IMS", 11);
            Enum r8 = new Enum("MOBILE_CBS", 12);
            Enum r7 = new Enum("WIFI_P2P", 13);
            Enum r6 = new Enum("MOBILE_IA", 14);
            Enum r5 = new Enum("MOBILE_EMERGENCY", 15);
            Enum r62 = new Enum("PROXY", 16);
            Enum r52 = new Enum("VPN", 17);
            Enum r63 = new Enum("NONE", 18);
            f3581k = new b[]{r32, r4, r22, r12, r02, r15, r14, r13, r122, r11, r10, r9, r8, r7, r6, r5, r62, r52, r63};
            SparseArray<b> sparseArray = new SparseArray<>();
            f3580j = sparseArray;
            sparseArray.put(0, r32);
            sparseArray.put(1, r4);
            sparseArray.put(2, r22);
            sparseArray.put(3, r12);
            sparseArray.put(4, r02);
            sparseArray.put(5, r15);
            sparseArray.put(6, r14);
            sparseArray.put(7, r13);
            sparseArray.put(8, r122);
            sparseArray.put(9, r11);
            sparseArray.put(10, r10);
            sparseArray.put(11, r9);
            sparseArray.put(12, r8);
            sparseArray.put(13, r7);
            sparseArray.put(14, r6);
            sparseArray.put(15, r5);
            sparseArray.put(16, r62);
            sparseArray.put(17, r52);
            sparseArray.put(-1, r63);
        }

        public static b valueOf(String str) {
            return (b) Enum.valueOf(b.class, str);
        }

        public static b[] values() {
            return (b[]) f3581k.clone();
        }
    }

    public abstract a a();

    public abstract b b();
}
