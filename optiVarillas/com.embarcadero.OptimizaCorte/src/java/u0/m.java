package u0;

import android.annotation.SuppressLint;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class m {
    @SuppressLint({"UnknownNullness"})

    /* renamed from: b  reason: collision with root package name */
    public final View f6000b;

    /* renamed from: a  reason: collision with root package name */
    public final HashMap f5999a = new HashMap();

    /* renamed from: c  reason: collision with root package name */
    public final ArrayList<f> f6001c = new ArrayList<>();

    @Deprecated
    public m() {
    }

    public final boolean equals(Object obj) {
        if (obj instanceof m) {
            m mVar = (m) obj;
            if (this.f6000b == mVar.f6000b && this.f5999a.equals(mVar.f5999a)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public final int hashCode() {
        return this.f5999a.hashCode() + (this.f6000b.hashCode() * 31);
    }

    public final String toString() {
        HashMap hashMap;
        String c4 = I.h.c(("TransitionValues@" + Integer.toHexString(hashCode()) + ":\n") + "    view = " + this.f6000b + "\n", "    values:");
        for (String str : this.f5999a.keySet()) {
            c4 = c4 + "    " + str + ": " + hashMap.get(str) + "\n";
        }
        return c4;
    }

    public m(View view) {
        this.f6000b = view;
    }
}
