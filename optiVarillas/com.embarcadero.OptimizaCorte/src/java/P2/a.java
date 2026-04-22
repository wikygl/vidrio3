package P2;

import android.graphics.RectF;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a implements c {

    /* renamed from: a  reason: collision with root package name */
    public final float f1809a;

    public a(float f) {
        this.f1809a = f;
    }

    @Override // P2.c
    public final float a(RectF rectF) {
        return this.f1809a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof a) && this.f1809a == ((a) obj).f1809a) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Float.valueOf(this.f1809a)});
    }
}
