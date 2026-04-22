package v;

import java.util.HashSet;
import java.util.Iterator;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import u.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class d {

    /* renamed from: b  reason: collision with root package name */
    public final e f6052b;

    /* renamed from: c  reason: collision with root package name */
    public final a f6053c;

    /* renamed from: d  reason: collision with root package name */
    public d f6054d;

    /* renamed from: g  reason: collision with root package name */
    public u.e f6056g;

    /* renamed from: a  reason: collision with root package name */
    public HashSet<d> f6051a = null;

    /* renamed from: e  reason: collision with root package name */
    public int f6055e = 0;
    public int f = -1;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f6057j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f6058k;

        /* renamed from: l  reason: collision with root package name */
        public static final a f6059l;

        /* renamed from: m  reason: collision with root package name */
        public static final a f6060m;

        /* renamed from: n  reason: collision with root package name */
        public static final a f6061n;

        /* renamed from: o  reason: collision with root package name */
        public static final a f6062o;

        /* renamed from: p  reason: collision with root package name */
        public static final a f6063p;

        /* renamed from: q  reason: collision with root package name */
        public static final a f6064q;

        /* renamed from: r  reason: collision with root package name */
        public static final /* synthetic */ a[] f6065r;
        /* JADX INFO: Fake field, exist only in values array */
        a EF9;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r10v1, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r11v1, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r12v1, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r13v1, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r14v1, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r15v1, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Enum, v.d$a] */
        /* JADX WARN: Type inference failed for: r3v2, types: [java.lang.Enum, v.d$a] */
        static {
            Enum r9 = new Enum("NONE", 0);
            ?? r10 = new Enum("LEFT", 1);
            f6057j = r10;
            ?? r11 = new Enum("TOP", 2);
            f6058k = r11;
            ?? r12 = new Enum("RIGHT", 3);
            f6059l = r12;
            ?? r13 = new Enum("BOTTOM", 4);
            f6060m = r13;
            ?? r14 = new Enum("BASELINE", 5);
            f6061n = r14;
            ?? r15 = new Enum("CENTER", 6);
            f6062o = r15;
            ?? r32 = new Enum("CENTER_X", 7);
            f6063p = r32;
            ?? r22 = new Enum("CENTER_Y", 8);
            f6064q = r22;
            f6065r = new a[]{r9, r10, r11, r12, r13, r14, r15, r32, r22};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f6065r.clone();
        }
    }

    public d(e eVar, a aVar) {
        this.f6052b = eVar;
        this.f6053c = aVar;
    }

    public final void a(d dVar, int i4) {
        b(dVar, i4, -1, false);
    }

    public final boolean b(d dVar, int i4, int i5, boolean z4) {
        if (dVar == null) {
            h();
            return true;
        } else if (!z4 && !g(dVar)) {
            return false;
        } else {
            this.f6054d = dVar;
            if (dVar.f6051a == null) {
                dVar.f6051a = new HashSet<>();
            }
            this.f6054d.f6051a.add(this);
            if (i4 > 0) {
                this.f6055e = i4;
            } else {
                this.f6055e = 0;
            }
            this.f = i5;
            return true;
        }
    }

    public final int c() {
        d dVar;
        if (this.f6052b.f6089X == 8) {
            return 0;
        }
        int i4 = this.f;
        if (i4 > -1 && (dVar = this.f6054d) != null && dVar.f6052b.f6089X == 8) {
            return i4;
        }
        return this.f6055e;
    }

    public final d d() {
        a aVar = this.f6053c;
        int ordinal = aVar.ordinal();
        e eVar = this.f6052b;
        switch (ordinal) {
            case 0:
            case 5:
            case 6:
            case 7:
            case 8:
                return null;
            case 1:
                return eVar.f6066A;
            case 2:
                return eVar.f6067B;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return eVar.f6119y;
            case 4:
                return eVar.f6120z;
            default:
                throw new AssertionError(aVar.name());
        }
    }

    public final boolean e() {
        HashSet<d> hashSet = this.f6051a;
        if (hashSet == null) {
            return false;
        }
        Iterator<d> it = hashSet.iterator();
        while (it.hasNext()) {
            if (it.next().d().f()) {
                return true;
            }
        }
        return false;
    }

    public final boolean f() {
        if (this.f6054d != null) {
            return true;
        }
        return false;
    }

    public final boolean g(d dVar) {
        boolean z4;
        boolean z5;
        boolean z6 = false;
        if (dVar == null) {
            return false;
        }
        a aVar = a.f6061n;
        a aVar2 = this.f6053c;
        e eVar = dVar.f6052b;
        a aVar3 = dVar.f6053c;
        if (aVar3 == aVar2) {
            if (aVar2 == aVar && (!eVar.f6117w || !this.f6052b.f6117w)) {
                return false;
            }
            return true;
        }
        int ordinal = aVar2.ordinal();
        a aVar4 = a.f6063p;
        a aVar5 = a.f6064q;
        switch (ordinal) {
            case 0:
            case 5:
            case 7:
            case 8:
                return false;
            case 1:
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                if (aVar3 != a.f6057j && aVar3 != a.f6059l) {
                    z4 = false;
                } else {
                    z4 = true;
                }
                if (eVar instanceof h) {
                    return (z4 || aVar3 == aVar4) ? true : true;
                }
                return z4;
            case 2:
            case 4:
                if (aVar3 != a.f6058k && aVar3 != a.f6060m) {
                    z5 = false;
                } else {
                    z5 = true;
                }
                if (eVar instanceof h) {
                    return (z5 || aVar3 == aVar5) ? true : true;
                }
                return z5;
            case 6:
                if (aVar3 == aVar || aVar3 == aVar4 || aVar3 == aVar5) {
                    return false;
                }
                return true;
            default:
                throw new AssertionError(aVar2.name());
        }
    }

    public final void h() {
        HashSet<d> hashSet;
        d dVar = this.f6054d;
        if (dVar != null && (hashSet = dVar.f6051a) != null) {
            hashSet.remove(this);
        }
        this.f6054d = null;
        this.f6055e = 0;
        this.f = -1;
    }

    public final void i() {
        u.e eVar = this.f6056g;
        if (eVar == null) {
            this.f6056g = new u.e(e.a.f5915j);
        } else {
            eVar.c();
        }
    }

    public final String toString() {
        return this.f6052b.f6090Y + ":" + this.f6053c.toString();
    }
}
