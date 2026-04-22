package W1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: W1.s  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0330s {

    /* renamed from: a  reason: collision with root package name */
    public static final r.j f2768a = new r.j();

    /* renamed from: b  reason: collision with root package name */
    public static Locale f2769b;

    public static String a(Context context) {
        String packageName = context.getPackageName();
        try {
            Context context2 = b2.c.a(context).f2924a;
            return context2.getPackageManager().getApplicationLabel(context2.getPackageManager().getApplicationInfo(packageName, 0)).toString();
        } catch (PackageManager.NameNotFoundException | NullPointerException unused) {
            String str = context.getApplicationInfo().name;
            if (TextUtils.isEmpty(str)) {
                return packageName;
            }
            return str;
        }
    }

    public static String b(Context context, int i4) {
        Resources resources = context.getResources();
        String a4 = a(context);
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 3) {
                    if (i4 != 5) {
                        if (i4 != 7) {
                            if (i4 != 9) {
                                if (i4 != 20) {
                                    switch (i4) {
                                        case 16:
                                            return d(context, "common_google_play_services_api_unavailable_text", a4);
                                        case 17:
                                            return d(context, "common_google_play_services_sign_in_failed_text", a4);
                                        case 18:
                                            return resources.getString(2131820633, a4);
                                        default:
                                            return resources.getString(2131820628, a4);
                                    }
                                }
                                return d(context, "common_google_play_services_restricted_profile_text", a4);
                            }
                            return resources.getString(2131820629, a4);
                        }
                        return d(context, "common_google_play_services_network_error_text", a4);
                    }
                    return d(context, "common_google_play_services_invalid_account_text", a4);
                }
                return resources.getString(2131820621, a4);
            } else if (a2.d.b(context)) {
                return resources.getString(2131820634);
            } else {
                return resources.getString(2131820631, a4);
            }
        }
        return resources.getString(2131820624, a4);
    }

    public static String c(Context context, int i4) {
        Resources resources = context.getResources();
        switch (i4) {
            case 1:
                return resources.getString(2131820625);
            case 2:
                return resources.getString(2131820632);
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return resources.getString(2131820622);
            case 4:
            case 6:
            case 18:
                return null;
            case 5:
                Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
                return e(context, "common_google_play_services_invalid_account_title");
            case 7:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return e(context, "common_google_play_services_network_error_title");
            case 8:
                Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
                return null;
            case 9:
                Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
                return null;
            case 10:
                Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
                return null;
            case 11:
                Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
                return null;
            case 12:
            case 13:
            case 14:
            case 15:
            case 19:
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i4);
                return null;
            case 16:
                Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
                return null;
            case 17:
                Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
                return e(context, "common_google_play_services_sign_in_failed_title");
            case 20:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return e(context, "common_google_play_services_restricted_profile_title");
        }
    }

    public static String d(Context context, String str, String str2) {
        Resources resources = context.getResources();
        String e4 = e(context, str);
        if (e4 == null) {
            e4 = resources.getString(2131820628);
        }
        return String.format(resources.getConfiguration().locale, e4, str2);
    }

    public static String e(Context context, String str) {
        I.g a4;
        Resources resources;
        r.j jVar = f2768a;
        synchronized (jVar) {
            try {
                Configuration configuration = context.getResources().getConfiguration();
                if (Build.VERSION.SDK_INT >= 24) {
                    a4 = new I.g(new I.k(I.d.a(configuration)));
                } else {
                    a4 = I.g.a(configuration.locale);
                }
                Locale locale = a4.f1135a.get(0);
                if (!locale.equals(f2769b)) {
                    jVar.clear();
                    f2769b = locale;
                }
                String str2 = (String) jVar.getOrDefault(str, null);
                if (str2 != null) {
                    return str2;
                }
                int i4 = T1.h.f2355c;
                try {
                    resources = context.getPackageManager().getResourcesForApplication("com.google.android.gms");
                } catch (PackageManager.NameNotFoundException unused) {
                    resources = null;
                }
                if (resources == null) {
                    return null;
                }
                int identifier = resources.getIdentifier(str, "string", "com.google.android.gms");
                if (identifier == 0) {
                    Log.w("GoogleApiAvailability", "Missing resource: ".concat(str));
                    return null;
                }
                String string = resources.getString(identifier);
                if (TextUtils.isEmpty(string)) {
                    Log.w("GoogleApiAvailability", "Got empty resource: ".concat(str));
                    return null;
                }
                f2768a.put(str, string);
                return string;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
