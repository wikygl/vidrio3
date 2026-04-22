package P2;

import android.graphics.RectF;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class g implements c {

    /* renamed from: a  reason: collision with root package name */
    public final float f1858a;

    public g(float f) {
        this.f1858a = f;
    }

    @Override // P2.c
    public final float a(RectF rectF) {
        return Math.min(rectF.width(), rectF.height()) * this.f1858a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof g) && this.f1858a == ((g) obj).f1858a) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Float.valueOf(this.f1858a)});
    }
}
