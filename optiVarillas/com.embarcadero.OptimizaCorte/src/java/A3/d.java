package A3;

import A1.P0;
import C3.InterfaceC0172w;
import F3.g;
import M.O;
import M.V;
import P2.f;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewParent;
import com.google.android.gms.internal.play_billing.E;
import com.google.android.gms.internal.play_billing.f0;
import com.google.gson.internal.i;
import java.util.WeakHashMap;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class d implements H1.d, f0, i {
    public static void a(Throwable th, Throwable th2) {
        h.e(th, "<this>");
        h.e(th2, "exception");
        if (th != th2) {
            q3.b.f5634a.a(th, th2);
        }
    }

    public static boolean b(Context context) {
        boolean z4;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String string = defaultSharedPreferences.getString("IABTCF_PurposeConsents", "");
        String string2 = defaultSharedPreferences.getString("IABTCF_VendorConsents", "");
        String string3 = defaultSharedPreferences.getString("IABTCF_VendorLegitimateInterests", "");
        String string4 = defaultSharedPreferences.getString("IABTCF_PurposeLegitimateInterests", "");
        boolean f = f(string2, 755);
        boolean f4 = f(string3, 755);
        if (!f(string, new int[]{1}[0])) {
            z4 = false;
        } else {
            z4 = f;
        }
        if (z4 && g(new int[]{2, 7, 9, 10}, string, string4, f, f4)) {
            return true;
        }
        return false;
    }

    public static boolean c(Context context) {
        boolean z4;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String string = defaultSharedPreferences.getString("IABTCF_PurposeConsents", "");
        String string2 = defaultSharedPreferences.getString("IABTCF_VendorConsents", "");
        String string3 = defaultSharedPreferences.getString("IABTCF_VendorLegitimateInterests", "");
        String string4 = defaultSharedPreferences.getString("IABTCF_PurposeLegitimateInterests", "");
        boolean f = f(string2, 755);
        boolean f4 = f(string3, 755);
        int[] iArr = {1, 3, 4};
        int i4 = 0;
        while (true) {
            if (i4 < 3) {
                if (!f(string, iArr[i4])) {
                    z4 = false;
                    break;
                }
                i4++;
            } else {
                z4 = f;
                break;
            }
        }
        if (z4 && g(new int[]{2, 7, 9, 10}, string, string4, f, f4)) {
            return true;
        }
        return false;
    }

    public static P0 d(int i4) {
        if (i4 != 0) {
            if (i4 != 1) {
                return new P2.h();
            }
            return new P2.d();
        }
        return new P2.h();
    }

    public static final void e(n3.f fVar, Throwable th) {
        Throwable runtimeException;
        for (InterfaceC0172w interfaceC0172w : F3.f.f913a) {
            try {
                interfaceC0172w.l(fVar, th);
            } catch (Throwable th2) {
                if (th == th2) {
                    runtimeException = th;
                } else {
                    runtimeException = new RuntimeException("Exception while trying to handle coroutine exception", th2);
                    a(runtimeException, th);
                }
                Thread currentThread = Thread.currentThread();
                currentThread.getUncaughtExceptionHandler().uncaughtException(currentThread, runtimeException);
            }
        }
        try {
            a(th, new g(fVar));
        } catch (Throwable unused) {
        }
        Thread currentThread2 = Thread.currentThread();
        currentThread2.getUncaughtExceptionHandler().uncaughtException(currentThread2, th);
    }

    public static boolean f(String str, int i4) {
        if (str.length() >= i4 && str.charAt(i4 - 1) == '1') {
            return true;
        }
        return false;
    }

    public static boolean g(int[] iArr, String str, String str2, boolean z4, boolean z5) {
        for (int i4 : iArr) {
            if (!f(str2, i4) || !z5) {
                if (f(str, i4) && z4) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static String h(String str, String str2) {
        int length = str.length() - str2.length();
        if (length >= 0 && length <= 1) {
            StringBuilder sb = new StringBuilder(str2.length() + str.length());
            for (int i4 = 0; i4 < str.length(); i4++) {
                sb.append(str.charAt(i4));
                if (str2.length() > i4) {
                    sb.append(str2.charAt(i4));
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("Invalid input received");
    }

    public static void i(View view) {
        Drawable background = view.getBackground();
        if (background instanceof P2.f) {
            j(view, (P2.f) background);
        }
    }

    public static void j(View view, P2.f fVar) {
        E2.a aVar = fVar.f1820j.f1839b;
        if (aVar != null && aVar.f877a) {
            float f = 0.0f;
            for (ViewParent parent = view.getParent(); parent instanceof View; parent = parent.getParent()) {
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                f += O.d.i((View) parent);
            }
            f.b bVar = fVar.f1820j;
            if (bVar.f1849m != f) {
                bVar.f1849m = f;
                fVar.o();
            }
        }
    }

    public static String l(E e4) {
        StringBuilder sb = new StringBuilder(e4.l());
        for (int i4 = 0; i4 < e4.l(); i4++) {
            byte j4 = e4.j(i4);
            if (j4 != 34) {
                if (j4 != 39) {
                    if (j4 != 92) {
                        switch (j4) {
                            case 7:
                                sb.append("\\a");
                                continue;
                            case 8:
                                sb.append("\\b");
                                continue;
                            case 9:
                                sb.append("\\t");
                                continue;
                            case 10:
                                sb.append("\\n");
                                continue;
                            case 11:
                                sb.append("\\v");
                                continue;
                            case 12:
                                sb.append("\\f");
                                continue;
                            case 13:
                                sb.append("\\r");
                                continue;
                            default:
                                if (j4 >= 32 && j4 <= 126) {
                                    sb.append((char) j4);
                                    continue;
                                } else {
                                    sb.append('\\');
                                    sb.append((char) (((j4 >>> 6) & 3) + 48));
                                    sb.append((char) (((j4 >>> 3) & 7) + 48));
                                    sb.append((char) ((j4 & 7) + 48));
                                    break;
                                }
                                break;
                        }
                    } else {
                        sb.append("\\\\");
                    }
                } else {
                    sb.append("\\'");
                }
            } else {
                sb.append("\\\"");
            }
        }
        return sb.toString();
    }

    public Object k() {
        return new com.google.gson.internal.h();
    }
}
