package E1;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.WJ;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import t1.C0801c;
import t1.C0804f;
import u1.AbstractC0822b;
import u1.C0821a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f {

    /* renamed from: b  reason: collision with root package name */
    public static final WJ f854b = new WJ(Looper.getMainLooper());

    /* renamed from: c  reason: collision with root package name */
    public static final String f855c = C0804f.class.getName();

    /* renamed from: d  reason: collision with root package name */
    public static final String f856d = F1.a.class.getName();

    /* renamed from: e  reason: collision with root package name */
    public static final String f857e = C0821a.class.getName();
    public static final String f = AbstractC0822b.class.getName();

    /* renamed from: g  reason: collision with root package name */
    public static final String f858g = P1.a.class.getName();

    /* renamed from: h  reason: collision with root package name */
    public static final String f859h = C0801c.class.getName();

    /* renamed from: a  reason: collision with root package name */
    public float f860a;

    public static String a(String str, String str2) {
        for (int i4 = 0; i4 < 2; i4++) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance(str2);
                messageDigest.update(str.getBytes());
                return String.format(Locale.US, "%032X", new BigInteger(1, messageDigest.digest()));
            } catch (ArithmeticException unused) {
                return null;
            } catch (NoSuchAlgorithmException unused2) {
            }
        }
        return null;
    }

    public static void i(JSONObject jSONObject, JSONObject jSONObject2) {
        Iterator<String> keys = jSONObject2.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            Object obj = jSONObject2.get(next);
            try {
                Object obj2 = jSONObject.get(next);
                if (JSONObject.class.isInstance(obj2) && JSONObject.class.isInstance(obj)) {
                    i((JSONObject) obj2, (JSONObject) obj);
                }
            } catch (JSONException unused) {
                jSONObject.put(next, obj);
            }
        }
    }

    public static final int j(DisplayMetrics displayMetrics, int i4) {
        return (int) TypedValue.applyDimension(1, i4, displayMetrics);
    }

    public static final boolean k() {
        boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.ga)).booleanValue();
        if (Build.VERSION.SDK_INT >= 31) {
            String str = Build.FINGERPRINT;
            if (str.contains("generic") || str.contains("emulator")) {
                return true;
            }
            if (booleanValue && Build.HARDWARE.contains("ranchu")) {
                return true;
            }
            return false;
        }
        return Build.DEVICE.startsWith("generic");
    }

    public static final void l(Context context, String str, Bundle bundle, e eVar) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext == null) {
            applicationContext = context;
        }
        bundle.putString("os", Build.VERSION.RELEASE);
        bundle.putString("api", String.valueOf(Build.VERSION.SDK_INT));
        bundle.putString("appid", applicationContext.getPackageName());
        if (str == null) {
            T1.f.f2354b.getClass();
            str = T1.f.a(context) + ".241199000";
        }
        bundle.putString("js", str);
        Uri.Builder appendQueryParameter = new Uri.Builder().scheme("https").path("//pagead2.googlesyndication.com/pagead/gen_204").appendQueryParameter("id", "gmob-apps");
        for (String str2 : bundle.keySet()) {
            appendQueryParameter.appendQueryParameter(str2, bundle.getString(str2));
        }
        eVar.i(appendQueryParameter.toString());
    }

    public static final int m(Context context, int i4) {
        return j(context.getResources().getDisplayMetrics(), i4);
    }

    public static final String n(Context context) {
        String string;
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            string = null;
        } else {
            string = Settings.Secure.getString(contentResolver, "android_id");
        }
        return a((string == null || k()) ? "emulator" : "emulator", "MD5");
    }

    public final JSONArray b(Collection collection) {
        JSONArray jSONArray = new JSONArray();
        for (Object obj : collection) {
            c(jSONArray, obj);
        }
        return jSONArray;
    }

    public final void c(JSONArray jSONArray, Object obj) {
        if (obj instanceof Bundle) {
            jSONArray.put(g((Bundle) obj));
        } else if (obj instanceof Map) {
            jSONArray.put(h((Map) obj));
        } else if (obj instanceof Collection) {
            jSONArray.put(b((Collection) obj));
        } else if (obj instanceof Object[]) {
            jSONArray.put(f((Object[]) obj));
        } else {
            jSONArray.put(obj);
        }
    }

    public final void d(JSONObject jSONObject, String str, Object obj) {
        Boolean[] boolArr;
        Long[] lArr;
        Double[] dArr;
        Integer[] numArr;
        if (((Boolean) A1.r.f168d.f171c.a(Gb.n)).booleanValue()) {
            str = String.valueOf(str);
        }
        if (obj instanceof Bundle) {
            jSONObject.put(str, g((Bundle) obj));
        } else if (obj instanceof Map) {
            jSONObject.put(str, h((Map) obj));
        } else if (obj instanceof Collection) {
            jSONObject.put(String.valueOf(str), b((Collection) obj));
        } else if (obj instanceof Object[]) {
            jSONObject.put(str, b(Arrays.asList((Object[]) obj)));
        } else {
            int i4 = 0;
            if (obj instanceof int[]) {
                int[] iArr = (int[]) obj;
                if (iArr == null) {
                    numArr = new Integer[0];
                } else {
                    int length = iArr.length;
                    Integer[] numArr2 = new Integer[length];
                    while (i4 < length) {
                        numArr2[i4] = Integer.valueOf(iArr[i4]);
                        i4++;
                    }
                    numArr = numArr2;
                }
                jSONObject.put(str, f(numArr));
            } else if (obj instanceof double[]) {
                double[] dArr2 = (double[]) obj;
                if (dArr2 == null) {
                    dArr = new Double[0];
                } else {
                    int length2 = dArr2.length;
                    Double[] dArr3 = new Double[length2];
                    while (i4 < length2) {
                        dArr3[i4] = Double.valueOf(dArr2[i4]);
                        i4++;
                    }
                    dArr = dArr3;
                }
                jSONObject.put(str, f(dArr));
            } else if (obj instanceof long[]) {
                long[] jArr = (long[]) obj;
                if (jArr == null) {
                    lArr = new Long[0];
                } else {
                    int length3 = jArr.length;
                    Long[] lArr2 = new Long[length3];
                    while (i4 < length3) {
                        lArr2[i4] = Long.valueOf(jArr[i4]);
                        i4++;
                    }
                    lArr = lArr2;
                }
                jSONObject.put(str, f(lArr));
            } else if (obj instanceof boolean[]) {
                boolean[] zArr = (boolean[]) obj;
                if (zArr == null) {
                    boolArr = new Boolean[0];
                } else {
                    int length4 = zArr.length;
                    Boolean[] boolArr2 = new Boolean[length4];
                    while (i4 < length4) {
                        boolArr2[i4] = Boolean.valueOf(zArr[i4]);
                        i4++;
                    }
                    boolArr = boolArr2;
                }
                jSONObject.put(str, f(boolArr));
            } else {
                jSONObject.put(str, obj);
            }
        }
    }

    public final int e(Context context, int i4) {
        if (this.f860a < 0.0f) {
            synchronized (this) {
                try {
                    if (this.f860a < 0.0f) {
                        WindowManager windowManager = (WindowManager) context.getSystemService("window");
                        if (windowManager == null) {
                            return 0;
                        }
                        Display defaultDisplay = windowManager.getDefaultDisplay();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        defaultDisplay.getMetrics(displayMetrics);
                        this.f860a = displayMetrics.density;
                    }
                } finally {
                }
            }
        }
        return Math.round(i4 / this.f860a);
    }

    public final JSONArray f(Object[] objArr) {
        JSONArray jSONArray = new JSONArray();
        for (Object obj : objArr) {
            c(jSONArray, obj);
        }
        return jSONArray;
    }

    public final JSONObject g(Bundle bundle) {
        JSONObject jSONObject = new JSONObject();
        for (String str : bundle.keySet()) {
            d(jSONObject, str, bundle.get(str));
        }
        return jSONObject;
    }

    public final JSONObject h(Map map) {
        try {
            JSONObject jSONObject = new JSONObject();
            for (String str : map.keySet()) {
                d(jSONObject, str, map.get(str));
            }
            return jSONObject;
        } catch (ClassCastException e4) {
            throw new JSONException("Could not convert map to JSON: ".concat(String.valueOf(e4.getMessage())));
        }
    }
}
