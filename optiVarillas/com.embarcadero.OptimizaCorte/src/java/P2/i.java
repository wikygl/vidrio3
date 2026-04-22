package P2;

import A1.P0;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import q2.C0771a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class i {

    /* renamed from: a  reason: collision with root package name */
    public P0 f1860a = new h();

    /* renamed from: b  reason: collision with root package name */
    public P0 f1861b = new h();

    /* renamed from: c  reason: collision with root package name */
    public P0 f1862c = new h();

    /* renamed from: d  reason: collision with root package name */
    public P0 f1863d = new h();

    /* renamed from: e  reason: collision with root package name */
    public c f1864e = new P2.a(0.0f);
    public c f = new P2.a(0.0f);

    /* renamed from: g  reason: collision with root package name */
    public c f1865g = new P2.a(0.0f);

    /* renamed from: h  reason: collision with root package name */
    public c f1866h = new P2.a(0.0f);

    /* renamed from: i  reason: collision with root package name */
    public e f1867i = new e();

    /* renamed from: j  reason: collision with root package name */
    public e f1868j = new e();

    /* renamed from: k  reason: collision with root package name */
    public e f1869k = new e();

    /* renamed from: l  reason: collision with root package name */
    public e f1870l = new e();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public P0 f1871a = new h();

        /* renamed from: b  reason: collision with root package name */
        public P0 f1872b = new h();

        /* renamed from: c  reason: collision with root package name */
        public P0 f1873c = new h();

        /* renamed from: d  reason: collision with root package name */
        public P0 f1874d = new h();

        /* renamed from: e  reason: collision with root package name */
        public c f1875e = new P2.a(0.0f);
        public c f = new P2.a(0.0f);

        /* renamed from: g  reason: collision with root package name */
        public c f1876g = new P2.a(0.0f);

        /* renamed from: h  reason: collision with root package name */
        public c f1877h = new P2.a(0.0f);

        /* renamed from: i  reason: collision with root package name */
        public e f1878i = new e();

        /* renamed from: j  reason: collision with root package name */
        public e f1879j = new e();

        /* renamed from: k  reason: collision with root package name */
        public e f1880k = new e();

        /* renamed from: l  reason: collision with root package name */
        public e f1881l = new e();

        public static float b(P0 p02) {
            if (p02 instanceof h) {
                return ((h) p02).f1859j;
            }
            if (p02 instanceof d) {
                return ((d) p02).f1812j;
            }
            return -1.0f;
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [P2.i, java.lang.Object] */
        public final i a() {
            ?? obj = new Object();
            obj.f1860a = this.f1871a;
            obj.f1861b = this.f1872b;
            obj.f1862c = this.f1873c;
            obj.f1863d = this.f1874d;
            obj.f1864e = this.f1875e;
            obj.f = this.f;
            obj.f1865g = this.f1876g;
            obj.f1866h = this.f1877h;
            obj.f1867i = this.f1878i;
            obj.f1868j = this.f1879j;
            obj.f1869k = this.f1880k;
            obj.f1870l = this.f1881l;
            return obj;
        }
    }

    public static a a(Context context, int i4, int i5, P2.a aVar) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, i4);
        if (i5 != 0) {
            contextThemeWrapper = new ContextThemeWrapper(contextThemeWrapper, i5);
        }
        TypedArray obtainStyledAttributes = contextThemeWrapper.obtainStyledAttributes(C0771a.f5630x);
        try {
            int i6 = obtainStyledAttributes.getInt(0, 0);
            int i7 = obtainStyledAttributes.getInt(3, i6);
            int i8 = obtainStyledAttributes.getInt(4, i6);
            int i9 = obtainStyledAttributes.getInt(2, i6);
            int i10 = obtainStyledAttributes.getInt(1, i6);
            c c4 = c(obtainStyledAttributes, 5, aVar);
            c c5 = c(obtainStyledAttributes, 8, c4);
            c c6 = c(obtainStyledAttributes, 9, c4);
            c c7 = c(obtainStyledAttributes, 7, c4);
            c c8 = c(obtainStyledAttributes, 6, c4);
            a aVar2 = new a();
            P0 d4 = A3.d.d(i7);
            aVar2.f1871a = d4;
            float b4 = a.b(d4);
            if (b4 != -1.0f) {
                aVar2.f1875e = new P2.a(b4);
            }
            aVar2.f1875e = c5;
            P0 d5 = A3.d.d(i8);
            aVar2.f1872b = d5;
            float b5 = a.b(d5);
            if (b5 != -1.0f) {
                aVar2.f = new P2.a(b5);
            }
            aVar2.f = c6;
            P0 d6 = A3.d.d(i9);
            aVar2.f1873c = d6;
            float b6 = a.b(d6);
            if (b6 != -1.0f) {
                aVar2.f1876g = new P2.a(b6);
            }
            aVar2.f1876g = c7;
            P0 d7 = A3.d.d(i10);
            aVar2.f1874d = d7;
            float b7 = a.b(d7);
            if (b7 != -1.0f) {
                aVar2.f1877h = new P2.a(b7);
            }
            aVar2.f1877h = c8;
            return aVar2;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static a b(Context context, AttributeSet attributeSet, int i4, int i5) {
        P2.a aVar = new P2.a(0);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0771a.f5624r, i4, i5);
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        int resourceId2 = obtainStyledAttributes.getResourceId(1, 0);
        obtainStyledAttributes.recycle();
        return a(context, resourceId, resourceId2, aVar);
    }

    public static c c(TypedArray typedArray, int i4, c cVar) {
        TypedValue peekValue = typedArray.peekValue(i4);
        if (peekValue == null) {
            return cVar;
        }
        int i5 = peekValue.type;
        if (i5 == 5) {
            return new P2.a(TypedValue.complexToDimensionPixelSize(peekValue.data, typedArray.getResources().getDisplayMetrics()));
        }
        if (i5 == 6) {
            return new g(peekValue.getFraction(1.0f, 1.0f));
        }
        return cVar;
    }

    public final boolean d(RectF rectF) {
        boolean z4;
        boolean z5;
        boolean z6;
        if (this.f1870l.getClass().equals(e.class) && this.f1868j.getClass().equals(e.class) && this.f1867i.getClass().equals(e.class) && this.f1869k.getClass().equals(e.class)) {
            z4 = true;
        } else {
            z4 = false;
        }
        float a4 = this.f1864e.a(rectF);
        if (this.f.a(rectF) == a4 && this.f1866h.a(rectF) == a4 && this.f1865g.a(rectF) == a4) {
            z5 = true;
        } else {
            z5 = false;
        }
        if ((this.f1861b instanceof h) && (this.f1860a instanceof h) && (this.f1862c instanceof h) && (this.f1863d instanceof h)) {
            z6 = true;
        } else {
            z6 = false;
        }
        if (!z4 || !z5 || !z6) {
            return false;
        }
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [P2.i$a, java.lang.Object] */
    public final a e() {
        ?? obj = new Object();
        obj.f1871a = new h();
        obj.f1872b = new h();
        obj.f1873c = new h();
        obj.f1874d = new h();
        obj.f1875e = new P2.a(0.0f);
        obj.f = new P2.a(0.0f);
        obj.f1876g = new P2.a(0.0f);
        obj.f1877h = new P2.a(0.0f);
        obj.f1878i = new e();
        obj.f1879j = new e();
        obj.f1880k = new e();
        new e();
        obj.f1871a = this.f1860a;
        obj.f1872b = this.f1861b;
        obj.f1873c = this.f1862c;
        obj.f1874d = this.f1863d;
        obj.f1875e = this.f1864e;
        obj.f = this.f;
        obj.f1876g = this.f1865g;
        obj.f1877h = this.f1866h;
        obj.f1878i = this.f1867i;
        obj.f1879j = this.f1868j;
        obj.f1880k = this.f1869k;
        obj.f1881l = this.f1870l;
        return obj;
    }
}
