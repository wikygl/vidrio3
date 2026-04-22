package i2;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* renamed from: i2.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0463i {

    /* renamed from: d  reason: collision with root package name */
    public static final Q f3758d = Q.p(4, "IABTCF_TCString", "IABGPP_HDR_GppString", "IABGPP_GppSID", "IABUSPrivacy_String");

    /* renamed from: a  reason: collision with root package name */
    public final Application f3759a;

    /* renamed from: b  reason: collision with root package name */
    public final SharedPreferences f3760b;

    /* renamed from: c  reason: collision with root package name */
    public final HashSet f3761c;

    public C0463i(Application application) {
        this.f3759a = application;
        SharedPreferences sharedPreferences = application.getSharedPreferences("__GOOGLE_FUNDING_CHOICE_SDK_INTERNAL__", 0);
        this.f3760b = sharedPreferences;
        this.f3761c = new HashSet(sharedPreferences.getStringSet("written_values", Collections.emptySet()));
    }

    public final HashMap a() {
        String str;
        Set<String> stringSet = this.f3760b.getStringSet("stored_info", U.f3697r);
        if (stringSet.isEmpty()) {
            stringSet = f3758d;
        }
        HashMap hashMap = new HashMap();
        for (String str2 : stringSet) {
            Application application = this.f3759a;
            L0.f a4 = D.a(application, str2);
            if (a4 == null) {
                Log.d("UserMessagingPlatform", "Fetching request info: failed for key: ".concat(String.valueOf(str2)));
            } else {
                Object obj = application.getSharedPreferences((String) a4.f1433b, 0).getAll().get((String) a4.f1434c);
                if (obj == null) {
                    Log.d("UserMessagingPlatform", "Stored info not exists: ".concat(String.valueOf(str2)));
                } else {
                    if (obj instanceof Boolean) {
                        if (true != ((Boolean) obj).booleanValue()) {
                            str = "0";
                        } else {
                            str = "1";
                        }
                    } else if (obj instanceof Number) {
                        str = obj.toString();
                    } else if (obj instanceof String) {
                        str = (String) obj;
                    } else {
                        Log.d("UserMessagingPlatform", "Failed to fetch stored info: ".concat(String.valueOf(str2)));
                    }
                    hashMap.put(str2, str);
                }
            }
        }
        return hashMap;
    }
}
