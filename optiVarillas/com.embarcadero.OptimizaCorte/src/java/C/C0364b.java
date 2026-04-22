package c;

import android.content.Context;
import android.content.Intent;
import c.AbstractC0363a;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import m3.f;
import m3.i;
import v3.h;

/* renamed from: c.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0364b extends AbstractC0363a<String[], Map<String, Boolean>> {
    @Override // c.AbstractC0363a
    public final Intent a(Context context, String[] strArr) {
        String[] strArr2 = strArr;
        h.e(context, "context");
        h.e(strArr2, "input");
        Intent putExtra = new Intent("androidx.activity.result.contract.action.REQUEST_PERMISSIONS").putExtra("androidx.activity.result.contract.extra.PERMISSIONS", strArr2);
        h.d(putExtra, "Intent(ACTION_REQUEST_PE…EXTRA_PERMISSIONS, input)");
        return putExtra;
    }

    @Override // c.AbstractC0363a
    public final AbstractC0363a.C0039a<Map<String, Boolean>> b(Context context, String[] strArr) {
        String[] strArr2 = strArr;
        h.e(context, "context");
        h.e(strArr2, "input");
        if (strArr2.length == 0) {
            return new AbstractC0363a.C0039a<>(f.f5348j);
        }
        for (String str : strArr2) {
            if (C.a.a(context, str) != 0) {
                return null;
            }
        }
        int g4 = i.g(strArr2.length);
        if (g4 < 16) {
            g4 = 16;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(g4);
        for (String str2 : strArr2) {
            linkedHashMap.put(str2, Boolean.TRUE);
        }
        return new AbstractC0363a.C0039a<>(linkedHashMap);
    }

    @Override // c.AbstractC0363a
    public final Map<String, Boolean> c(int i4, Intent intent) {
        boolean z4;
        f fVar = f.f5348j;
        if (i4 == -1 && intent != null) {
            String[] stringArrayExtra = intent.getStringArrayExtra("androidx.activity.result.contract.extra.PERMISSIONS");
            int[] intArrayExtra = intent.getIntArrayExtra("androidx.activity.result.contract.extra.PERMISSION_GRANT_RESULTS");
            if (intArrayExtra != null && stringArrayExtra != null) {
                ArrayList arrayList = new ArrayList(intArrayExtra.length);
                for (int i5 : intArrayExtra) {
                    if (i5 == 0) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                    arrayList.add(Boolean.valueOf(z4));
                }
                ArrayList arrayList2 = new ArrayList();
                for (String str : stringArrayExtra) {
                    if (str != null) {
                        arrayList2.add(str);
                    }
                }
                Iterator it = arrayList2.iterator();
                Iterator it2 = arrayList.iterator();
                ArrayList arrayList3 = new ArrayList(Math.min(m3.c.o(arrayList2), m3.c.o(arrayList)));
                while (it.hasNext() && it2.hasNext()) {
                    arrayList3.add(new l3.b(it.next(), it2.next()));
                }
                return i.h(arrayList3);
            }
            return fVar;
        }
        return fVar;
    }
}
