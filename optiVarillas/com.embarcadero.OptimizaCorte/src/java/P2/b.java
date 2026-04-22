package P2;

import android.graphics.RectF;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b implements c {

    /* renamed from: a  reason: collision with root package name */
    public final c f1810a;

    /* renamed from: b  reason: collision with root package name */
    public final float f1811b;

    public b(float f, c cVar) {
        while (cVar instanceof b) {
            cVar = ((b) cVar).f1810a;
            f += ((b) cVar).f1811b;
        }
        this.f1810a = cVar;
        this.f1811b = f;
    }

    @Override // P2.c
    public final float a(RectF rectF) {
        return Math.max(0.0f, this.f1810a.a(rectF) + this.f1811b);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        if (this.f1810a.equals(bVar.f1810a) && this.f1811b == bVar.f1811b) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f1810a, Float.valueOf(this.f1811b)});
    }
}
