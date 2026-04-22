package D1;

import W1.C0323k;
import java.util.Arrays;

/* renamed from: D1.y  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0204y {

    /* renamed from: a  reason: collision with root package name */
    public final String f789a;

    /* renamed from: b  reason: collision with root package name */
    public final double f790b;

    /* renamed from: c  reason: collision with root package name */
    public final double f791c;

    /* renamed from: d  reason: collision with root package name */
    public final double f792d;

    /* renamed from: e  reason: collision with root package name */
    public final int f793e;

    public C0204y(String str, double d4, double d5, double d6, int i4) {
        this.f789a = str;
        this.f791c = d4;
        this.f790b = d5;
        this.f792d = d6;
        this.f793e = i4;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof C0204y)) {
            return false;
        }
        C0204y c0204y = (C0204y) obj;
        if (!C0323k.a(this.f789a, c0204y.f789a) || this.f790b != c0204y.f790b || this.f791c != c0204y.f791c || this.f793e != c0204y.f793e || Double.compare(this.f792d, c0204y.f792d) != 0) {
            return false;
        }
        return true;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f789a, Double.valueOf(this.f790b), Double.valueOf(this.f791c), Double.valueOf(this.f792d), Integer.valueOf(this.f793e)});
    }

    public final String toString() {
        C0323k.a aVar = new C0323k.a(this);
        aVar.a(this.f789a, "name");
        aVar.a(Double.valueOf(this.f791c), "minBound");
        aVar.a(Double.valueOf(this.f790b), "maxBound");
        aVar.a(Double.valueOf(this.f792d), "percent");
        aVar.a(Integer.valueOf(this.f793e), "count");
        return aVar.toString();
    }
}
