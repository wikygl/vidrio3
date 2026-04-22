package u;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public boolean f5904a;

    /* renamed from: e  reason: collision with root package name */
    public float f5908e;

    /* renamed from: i  reason: collision with root package name */
    public a f5911i;

    /* renamed from: b  reason: collision with root package name */
    public int f5905b = -1;

    /* renamed from: c  reason: collision with root package name */
    public int f5906c = -1;

    /* renamed from: d  reason: collision with root package name */
    public int f5907d = 0;
    public boolean f = false;

    /* renamed from: g  reason: collision with root package name */
    public final float[] f5909g = new float[9];

    /* renamed from: h  reason: collision with root package name */
    public final float[] f5910h = new float[9];

    /* renamed from: j  reason: collision with root package name */
    public b[] f5912j = new b[16];

    /* renamed from: k  reason: collision with root package name */
    public int f5913k = 0;

    /* renamed from: l  reason: collision with root package name */
    public int f5914l = 0;

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a {

        /* renamed from: j  reason: collision with root package name */
        public static final a f5915j;

        /* renamed from: k  reason: collision with root package name */
        public static final a f5916k;

        /* renamed from: l  reason: collision with root package name */
        public static final a f5917l;

        /* renamed from: m  reason: collision with root package name */
        public static final a f5918m;

        /* renamed from: n  reason: collision with root package name */
        public static final /* synthetic */ a[] f5919n;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r5v0, types: [java.lang.Enum, u.e$a] */
        /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.Enum, u.e$a] */
        /* JADX WARN: Type inference failed for: r8v1, types: [java.lang.Enum, u.e$a] */
        /* JADX WARN: Type inference failed for: r9v1, types: [java.lang.Enum, u.e$a] */
        static {
            ?? r5 = new Enum("UNRESTRICTED", 0);
            f5915j = r5;
            Enum r6 = new Enum("CONSTANT", 1);
            ?? r7 = new Enum("SLACK", 2);
            f5916k = r7;
            ?? r8 = new Enum("ERROR", 3);
            f5917l = r8;
            ?? r9 = new Enum("UNKNOWN", 4);
            f5918m = r9;
            f5919n = new a[]{r5, r6, r7, r8, r9};
        }

        public a() {
            throw null;
        }

        public static a valueOf(String str) {
            return (a) Enum.valueOf(a.class, str);
        }

        public static a[] values() {
            return (a[]) f5919n.clone();
        }
    }

    public e(a aVar) {
        this.f5911i = aVar;
    }

    public final void a(b bVar) {
        int i4 = 0;
        while (true) {
            int i5 = this.f5913k;
            if (i4 < i5) {
                if (this.f5912j[i4] == bVar) {
                    return;
                }
                i4++;
            } else {
                b[] bVarArr = this.f5912j;
                if (i5 >= bVarArr.length) {
                    this.f5912j = (b[]) Arrays.copyOf(bVarArr, bVarArr.length * 2);
                }
                b[] bVarArr2 = this.f5912j;
                int i6 = this.f5913k;
                bVarArr2[i6] = bVar;
                this.f5913k = i6 + 1;
                return;
            }
        }
    }

    public final void b(b bVar) {
        int i4 = this.f5913k;
        int i5 = 0;
        while (i5 < i4) {
            if (this.f5912j[i5] == bVar) {
                while (i5 < i4 - 1) {
                    b[] bVarArr = this.f5912j;
                    int i6 = i5 + 1;
                    bVarArr[i5] = bVarArr[i6];
                    i5 = i6;
                }
                this.f5913k--;
                return;
            }
            i5++;
        }
    }

    public final void c() {
        this.f5911i = a.f5918m;
        this.f5907d = 0;
        this.f5905b = -1;
        this.f5906c = -1;
        this.f5908e = 0.0f;
        this.f = false;
        int i4 = this.f5913k;
        for (int i5 = 0; i5 < i4; i5++) {
            this.f5912j[i5] = null;
        }
        this.f5913k = 0;
        this.f5914l = 0;
        this.f5904a = false;
        Arrays.fill(this.f5910h, 0.0f);
    }

    public final void d(b bVar) {
        int i4 = this.f5913k;
        for (int i5 = 0; i5 < i4; i5++) {
            this.f5912j[i5].h(bVar, false);
        }
        this.f5913k = 0;
    }

    public final String toString() {
        return "" + this.f5905b;
    }
}
