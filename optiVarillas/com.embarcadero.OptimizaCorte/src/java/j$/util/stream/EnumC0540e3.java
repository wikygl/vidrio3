package j$.util.stream;

import j$.util.Spliterator;
import java.util.EnumMap;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Init of enum DISTINCT uses external variables
	at m9.g0.q(SourceFile:103)
	at m9.g0.O(SourceFile:93)
	at m9.g0.s(SourceFile:52)
	at m9.g0.t(SourceFile:48)
	at m9.g0.p(SourceFile:136)
	at m9.g0.a(SourceFile:7)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* renamed from: j$.util.stream.e3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class EnumC0540e3 {
    public static final EnumC0540e3 DISTINCT;
    public static final EnumC0540e3 ORDERED;
    public static final EnumC0540e3 SHORT_CIRCUIT;
    public static final EnumC0540e3 SIZED;
    public static final EnumC0540e3 SORTED;
    static final int f;

    /* renamed from: g  reason: collision with root package name */
    static final int f4482g;

    /* renamed from: h  reason: collision with root package name */
    static final int f4483h;

    /* renamed from: i  reason: collision with root package name */
    private static final int f4484i;

    /* renamed from: j  reason: collision with root package name */
    private static final int f4485j;

    /* renamed from: k  reason: collision with root package name */
    private static final int f4486k;

    /* renamed from: l  reason: collision with root package name */
    static final int f4487l;

    /* renamed from: m  reason: collision with root package name */
    static final int f4488m;

    /* renamed from: n  reason: collision with root package name */
    static final int f4489n;

    /* renamed from: o  reason: collision with root package name */
    static final int f4490o;

    /* renamed from: p  reason: collision with root package name */
    static final int f4491p;

    /* renamed from: q  reason: collision with root package name */
    static final int f4492q;

    /* renamed from: r  reason: collision with root package name */
    static final int f4493r;

    /* renamed from: s  reason: collision with root package name */
    static final int f4494s;

    /* renamed from: t  reason: collision with root package name */
    static final int f4495t;

    /* renamed from: u  reason: collision with root package name */
    static final int f4496u;

    /* renamed from: v  reason: collision with root package name */
    private static final /* synthetic */ EnumC0540e3[] f4497v;

    /* renamed from: a  reason: collision with root package name */
    private final Map f4498a;

    /* renamed from: b  reason: collision with root package name */
    private final int f4499b;

    /* renamed from: c  reason: collision with root package name */
    private final int f4500c;

    /* renamed from: d  reason: collision with root package name */
    private final int f4501d;

    /* renamed from: e  reason: collision with root package name */
    private final int f4502e;

    static {
        EnumC0535d3 enumC0535d3 = EnumC0535d3.SPLITERATOR;
        C0530c3 v4 = v(enumC0535d3);
        EnumC0535d3 enumC0535d32 = EnumC0535d3.STREAM;
        v4.a(enumC0535d32);
        EnumC0535d3 enumC0535d33 = EnumC0535d3.OP;
        v4.f4464a.put(enumC0535d33, 3);
        EnumC0540e3 enumC0540e3 = new EnumC0540e3("DISTINCT", 0, 0, v4);
        DISTINCT = enumC0540e3;
        C0530c3 v5 = v(enumC0535d3);
        v5.a(enumC0535d32);
        v5.f4464a.put(enumC0535d33, 3);
        EnumC0540e3 enumC0540e32 = new EnumC0540e3("SORTED", 1, 1, v5);
        SORTED = enumC0540e32;
        C0530c3 v6 = v(enumC0535d3);
        v6.a(enumC0535d32);
        Map map = v6.f4464a;
        map.put(enumC0535d33, 3);
        EnumC0535d3 enumC0535d34 = EnumC0535d3.TERMINAL_OP;
        map.put(enumC0535d34, 2);
        EnumC0535d3 enumC0535d35 = EnumC0535d3.UPSTREAM_TERMINAL_OP;
        map.put(enumC0535d35, 2);
        EnumC0540e3 enumC0540e33 = new EnumC0540e3("ORDERED", 2, 2, v6);
        ORDERED = enumC0540e33;
        C0530c3 v7 = v(enumC0535d3);
        v7.a(enumC0535d32);
        v7.f4464a.put(enumC0535d33, 2);
        EnumC0540e3 enumC0540e34 = new EnumC0540e3("SIZED", 3, 3, v7);
        SIZED = enumC0540e34;
        C0530c3 v8 = v(enumC0535d33);
        v8.a(enumC0535d34);
        EnumC0540e3 enumC0540e35 = new EnumC0540e3("SHORT_CIRCUIT", 4, 12, v8);
        SHORT_CIRCUIT = enumC0540e35;
        f4497v = new EnumC0540e3[]{enumC0540e3, enumC0540e32, enumC0540e33, enumC0540e34, enumC0540e35};
        f = l(enumC0535d3);
        f4482g = l(enumC0535d32);
        f4483h = l(enumC0535d33);
        l(enumC0535d34);
        l(enumC0535d35);
        int i4 = 0;
        for (EnumC0540e3 enumC0540e36 : values()) {
            i4 |= enumC0540e36.f4502e;
        }
        f4484i = i4;
        int i5 = f4482g;
        f4485j = i5;
        int i6 = i5 << 1;
        f4486k = i6;
        f4487l = i5 | i6;
        EnumC0540e3 enumC0540e37 = DISTINCT;
        f4488m = enumC0540e37.f4500c;
        f4489n = enumC0540e37.f4501d;
        EnumC0540e3 enumC0540e38 = SORTED;
        f4490o = enumC0540e38.f4500c;
        f4491p = enumC0540e38.f4501d;
        EnumC0540e3 enumC0540e39 = ORDERED;
        f4492q = enumC0540e39.f4500c;
        f4493r = enumC0540e39.f4501d;
        EnumC0540e3 enumC0540e310 = SIZED;
        f4494s = enumC0540e310.f4500c;
        f4495t = enumC0540e310.f4501d;
        f4496u = SHORT_CIRCUIT.f4500c;
    }

    private EnumC0540e3(String str, int i4, int i5, C0530c3 c0530c3) {
        EnumC0535d3[] values = EnumC0535d3.values();
        int length = values.length;
        int i6 = 0;
        while (true) {
            Map map = c0530c3.f4464a;
            if (i6 >= length) {
                this.f4498a = map;
                int i7 = i5 * 2;
                this.f4499b = i7;
                this.f4500c = 1 << i7;
                this.f4501d = 2 << i7;
                this.f4502e = 3 << i7;
                return;
            }
            j$.util.D.q(map, values[i6], 0);
            i6++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int j(int i4, int i5) {
        return i4 | (i5 & (i4 == 0 ? f4484i : ~(((f4485j & i4) << 1) | i4 | ((f4486k & i4) >> 1))));
    }

    private static int l(EnumC0535d3 enumC0535d3) {
        EnumC0540e3[] values;
        int i4 = 0;
        for (EnumC0540e3 enumC0540e3 : values()) {
            i4 |= ((Integer) enumC0540e3.f4498a.get(enumC0535d3)).intValue() << enumC0540e3.f4499b;
        }
        return i4;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int m(Spliterator spliterator) {
        int characteristics = spliterator.characteristics();
        int i4 = characteristics & 4;
        int i5 = f;
        return (i4 == 0 || spliterator.getComparator() == null) ? characteristics & i5 : characteristics & i5 & (-5);
    }

    private static C0530c3 v(EnumC0535d3 enumC0535d3) {
        C0530c3 c0530c3 = new C0530c3(new EnumMap(EnumC0535d3.class));
        c0530c3.a(enumC0535d3);
        return c0530c3;
    }

    public static EnumC0540e3 valueOf(String str) {
        return (EnumC0540e3) Enum.valueOf(EnumC0540e3.class, str);
    }

    public static EnumC0540e3[] values() {
        return (EnumC0540e3[]) f4497v.clone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int z(int i4) {
        return i4 & ((~i4) >> 1) & f4485j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean r(int i4) {
        return (i4 & this.f4502e) == this.f4500c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean u(int i4) {
        int i5 = this.f4502e;
        return (i4 & i5) == i5;
    }
}
