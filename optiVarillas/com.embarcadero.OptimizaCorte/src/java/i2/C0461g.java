package i2;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Executor;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: i2.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0461g implements L {

    /* renamed from: a  reason: collision with root package name */
    public final Application f3742a;

    /* renamed from: b  reason: collision with root package name */
    public final C0463i f3743b;

    /* renamed from: c  reason: collision with root package name */
    public final Executor f3744c;

    public C0461g(Application application, C0463i c0463i, F f) {
        this.f3742a = application;
        this.f3743b = c0463i;
        this.f3744c = f;
    }

    @Override // i2.L
    public final Executor a() {
        return this.f3744c;
    }

    @Override // i2.L
    public final boolean b(String str, JSONObject jSONObject) {
        char c4;
        C0463i c0463i;
        int hashCode = str.hashCode();
        if (hashCode != 94746189) {
            if (hashCode == 113399775 && str.equals("write")) {
                c4 = 0;
            }
            c4 = 65535;
        } else {
            if (str.equals("clear")) {
                c4 = 1;
            }
            c4 = 65535;
        }
        Application application = this.f3742a;
        if (c4 != 0) {
            if (c4 != 1) {
                return false;
            }
            JSONArray optJSONArray = jSONObject.optJSONArray("keys");
            if (optJSONArray != null && optJSONArray.length() != 0) {
                HashSet hashSet = new HashSet();
                int length = optJSONArray.length();
                for (int i4 = 0; i4 < length; i4++) {
                    String optString = optJSONArray.optString(i4);
                    if (TextUtils.isEmpty(optString)) {
                        Log.d("UserMessagingPlatform", "Action[clear]: empty key at index: " + i4);
                    } else {
                        hashSet.add(optString);
                    }
                }
                D.b(application, hashSet);
            } else {
                Log.d("UserMessagingPlatform", "Action[clear]: wrong args.".concat(String.valueOf(jSONObject.toString())));
            }
            return true;
        }
        HashMap hashMap = new HashMap();
        Iterator<String> keys = jSONObject.keys();
        while (true) {
            boolean hasNext = keys.hasNext();
            c0463i = this.f3743b;
            if (!hasNext) {
                break;
            }
            String next = keys.next();
            Object opt = jSONObject.opt(next);
            String valueOf = String.valueOf(opt);
            Log.d("UserMessagingPlatform", "Writing to storage: [" + next + "] " + valueOf);
            L0.f a4 = D.a(application, next);
            if (a4 != null) {
                String str2 = (String) a4.f1433b;
                if (!hashMap.containsKey(str2)) {
                    hashMap.put(str2, application.getSharedPreferences(str2, 0).edit());
                }
                SharedPreferences.Editor editor = (SharedPreferences.Editor) hashMap.get(str2);
                boolean z4 = opt instanceof Integer;
                String str3 = (String) a4.f1434c;
                if (z4) {
                    editor.putInt(str3, ((Integer) opt).intValue());
                } else if (opt instanceof Long) {
                    editor.putLong(str3, ((Long) opt).longValue());
                } else if (opt instanceof Double) {
                    editor.putFloat(str3, ((Double) opt).floatValue());
                } else if (opt instanceof Float) {
                    editor.putFloat(str3, ((Float) opt).floatValue());
                } else if (opt instanceof Boolean) {
                    editor.putBoolean(str3, ((Boolean) opt).booleanValue());
                } else if (opt instanceof String) {
                    editor.putString(str3, (String) opt);
                }
                c0463i.f3761c.add(next);
            }
            Log.d("UserMessagingPlatform", "Failed writing key: ".concat(String.valueOf(next)));
        }
        c0463i.f3760b.edit().putStringSet("written_values", c0463i.f3761c).apply();
        for (SharedPreferences.Editor editor2 : hashMap.values()) {
            editor2.apply();
        }
        return true;
    }
}
