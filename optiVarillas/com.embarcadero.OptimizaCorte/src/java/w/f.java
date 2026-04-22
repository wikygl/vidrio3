package w;

import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class f implements d {

    /* renamed from: d  reason: collision with root package name */
    public final m f6341d;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public int f6343g;

    /* renamed from: a  reason: collision with root package name */
    public m f6338a = null;

    /* renamed from: b  reason: collision with root package name */
    public boolean f6339b = false;

    /* renamed from: c  reason: collision with root package name */
    public boolean f6340c = false;

    /* renamed from: e  reason: collision with root package name */
    public a f6342e = a.f6349j;

    /* renamed from: h  reason: collision with root package name */
    public int f6344h = 1;

    /* renamed from: i  reason: collision with root package name */
    public g f6345i = null;

    /* renamed from: j  reason: collision with root package name */
    public boolean f6346j = false;

    /* renamed from: k  reason: collision with root package name */
    public final ArrayList f6347k = new ArrayList();

    /* renamed from: l  reason: collision with root package name */
    public final ArrayList f6348l = new ArrayList();

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f6349j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f6350k;

        /* renamed from: l  reason: collision with root package name */
        public static final a f6351l;

        /* renamed from: m  reason: collision with root package name */
        public static final a f6352m;

        /* renamed from: n  reason: collision with root package name */
        public static final a f6353n;

        /* renamed from: o  reason: collision with root package name */
        public static final a f6354o;

        /* renamed from: p  reason: collision with root package name */
        public static final a f6355p;

        /* renamed from: q  reason: collision with root package name */
        public static final a f6356q;

        /* renamed from: r  reason: collision with root package name */
        public static final /* synthetic */ a[] f6357r;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r10v1, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r11v1, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r12v1, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r13v1, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r14v1, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r15v1, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r8v0, types: [java.lang.Enum, w.f$a] */
        /* JADX WARN: Type inference failed for: r9v1, types: [java.lang.Enum, w.f$a] */
        static {
            ?? r8 = new Enum("UNKNOWN", 0);
            f6349j = r8;
            ?? r9 = new Enum("HORIZONTAL_DIMENSION", 1);
            f6350k = r9;
            ?? r10 = new Enum("VERTICAL_DIMENSION", 2);
            f6351l = r10;
            ?? r11 = new Enum("LEFT", 3);
            f6352m = r11;
            ?? r12 = new Enum("RIGHT", 4);
            f6353n = r12;
            ?? r13 = new Enum("TOP", 5);
            f6354o = r13;
            ?? r14 = new Enum("BOTTOM", 6);
            f6355p = r14;
            ?? r15 = new Enum("BASELINE", 7);
            f6356q = r15;
            f6357r = new a[]{r8, r9, r10, r11, r12, r13, r14, r15};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f6357r.clone();
        }
    }

    public f(m mVar) {
        this.f6341d = mVar;
    }

    @Override // w.d
    public final void a(d dVar) {
        ArrayList arrayList = this.f6348l;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (!((f) it.next()).f6346j) {
                return;
            }
        }
        this.f6340c = true;
        m mVar = this.f6338a;
        if (mVar != null) {
            mVar.a(this);
        }
        if (this.f6339b) {
            this.f6341d.a(this);
            return;
        }
        Iterator it2 = arrayList.iterator();
        f fVar = null;
        int i4 = 0;
        while (it2.hasNext()) {
            f fVar2 = (f) it2.next();
            if (!(fVar2 instanceof g)) {
                i4++;
                fVar = fVar2;
            }
        }
        if (fVar != null && i4 == 1 && fVar.f6346j) {
            g gVar = this.f6345i;
            if (gVar != null) {
                if (gVar.f6346j) {
                    this.f = this.f6344h * gVar.f6343g;
                } else {
                    return;
                }
            }
            d(fVar.f6343g + this.f);
        }
        m mVar2 = this.f6338a;
        if (mVar2 != null) {
            mVar2.a(this);
        }
    }

    public final void b(d dVar) {
        this.f6347k.add(dVar);
        if (this.f6346j) {
            dVar.a(dVar);
        }
    }

    public final void c() {
        this.f6348l.clear();
        this.f6347k.clear();
        this.f6346j = false;
        this.f6343g = 0;
        this.f6340c = false;
        this.f6339b = false;
    }

    public void d(int i4) {
        if (this.f6346j) {
            return;
        }
        this.f6346j = true;
        this.f6343g = i4;
        Iterator it = this.f6347k.iterator();
        while (it.hasNext()) {
            d dVar = (d) it.next();
            dVar.a(dVar);
        }
    }

    public final String toString() {
        Object obj;
        StringBuilder sb = new StringBuilder();
        sb.append(this.f6341d.f6365b.f6090Y);
        sb.append(":");
        sb.append(this.f6342e);
        sb.append("(");
        if (this.f6346j) {
            obj = Integer.valueOf(this.f6343g);
        } else {
            obj = "unresolved";
        }
        sb.append(obj);
        sb.append(") <t=");
        sb.append(this.f6348l.size());
        sb.append(":d=");
        sb.append(this.f6347k.size());
        sb.append(">");
        return sb.toString();
    }
}
