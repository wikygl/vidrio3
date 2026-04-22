package E1;

import W1.C0323k;
import android.os.Bundle;
import android.view.View;
import androidx.activity.u;
import java.lang.reflect.Array;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class n implements com.google.gson.internal.i {
    public static final void a(View view, u uVar) {
        v3.h.e(view, "<this>");
        v3.h.e(uVar, "onBackPressedDispatcherOwner");
        view.setTag(2131231328, uVar);
    }

    public static boolean b(Bundle bundle, Bundle bundle2) {
        Object obj = bundle2;
        obj = bundle2;
        if (bundle != null && bundle2 != null) {
            if (bundle.size() != bundle2.size()) {
                return false;
            }
            for (String str : bundle.keySet()) {
                if (!bundle2.containsKey(str)) {
                    return false;
                }
                Object obj2 = bundle.get(str);
                Object obj3 = bundle2.get(str);
                if (obj2 != null && obj3 != null) {
                    if (obj2 instanceof Bundle) {
                        if (!(obj3 instanceof Bundle) || !b((Bundle) obj2, (Bundle) obj3)) {
                            return false;
                        }
                    } else if (obj2.getClass().isArray()) {
                        int length = Array.getLength(obj2);
                        if (!obj3.getClass().isArray() || length != Array.getLength(obj3)) {
                            return false;
                        }
                        for (int i4 = 0; i4 < length; i4++) {
                            if (!C0323k.a(Array.get(obj2, i4), Array.get(obj3, i4))) {
                                return false;
                            }
                        }
                        continue;
                    } else if (!obj2.equals(obj3)) {
                        return false;
                    }
                } else {
                    obj = obj3;
                    bundle = obj2;
                }
            }
            return true;
        }
        if (bundle == null && obj == null) {
            return true;
        }
        return false;
    }

    public Object k() {
        return new ArrayList();
    }
}
