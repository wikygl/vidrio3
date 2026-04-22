package i2;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class D {
    public static L0.f a(Context context, String str) {
        String str2;
        String str3;
        if (!TextUtils.isEmpty(str)) {
            String[] split = str.split("/", -1);
            int length = split.length;
            if (length == 1) {
                String valueOf = String.valueOf(context.getPackageName());
                str3 = split[0];
                str2 = valueOf.concat("_preferences");
            } else if (length == 2) {
                str2 = split[0];
                str3 = split[1];
            } else {
                return null;
            }
            if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
                return new L0.f(str2, str3);
            }
            return null;
        }
        return null;
    }

    public static void b(Context context, HashSet hashSet) {
        HashMap hashMap = new HashMap();
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            L0.f a4 = a(context, str);
            if (a4 == null) {
                Log.d("UserMessagingPlatform", "clearKeys: unable to process key: ".concat(String.valueOf(str)));
            } else {
                String str2 = (String) a4.f1433b;
                if (!hashMap.containsKey(str2)) {
                    hashMap.put(str2, context.getSharedPreferences(str2, 0).edit());
                }
                ((SharedPreferences.Editor) hashMap.get(str2)).remove((String) a4.f1434c);
            }
        }
        for (SharedPreferences.Editor editor : hashMap.values()) {
            editor.apply();
        }
    }
}
