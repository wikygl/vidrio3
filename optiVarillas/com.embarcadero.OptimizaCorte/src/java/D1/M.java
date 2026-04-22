package D1;

import A1.C0124p;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ScrollView;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.M0;
import com.google.android.gms.internal.ads.NK;
import com.google.android.gms.internal.ads.eL;
import com.google.android.gms.internal.ads.fL;
import com.google.android.gms.internal.ads.mG;
import com.google.android.gms.internal.ads.vb;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class M {
    public static WindowManager.LayoutParams a() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 0, 0, -2);
        layoutParams.flags = ((Integer) A1.r.f168d.f171c.a(Gb.e7)).intValue();
        layoutParams.type = 2;
        layoutParams.gravity = 8388659;
        return layoutParams;
    }

    public static JSONObject b(String str, Context context, Point point, Point point2) {
        JSONObject jSONObject;
        JSONObject jSONObject2 = null;
        try {
            jSONObject = new JSONObject();
        } catch (Exception e4) {
            e = e4;
        }
        try {
            JSONObject jSONObject3 = new JSONObject();
            try {
                int i4 = point2.x;
                C0124p c0124p = C0124p.f;
                jSONObject3.put("x", c0124p.f161a.e(context, i4));
                jSONObject3.put("y", c0124p.f161a.e(context, point2.y));
                jSONObject3.put("start_x", c0124p.f161a.e(context, point.x));
                jSONObject3.put("start_y", c0124p.f161a.e(context, point.y));
                jSONObject2 = jSONObject3;
            } catch (JSONException e5) {
                E1.m.e("Error occurred while putting signals into JSON object.", e5);
            }
            jSONObject.put("click_point", jSONObject2);
            jSONObject.put("asset_id", str);
            return jSONObject;
        } catch (Exception e6) {
            e = e6;
            jSONObject2 = jSONObject;
            E1.m.e("Error occurred while grabbing click signals.", e);
            return jSONObject2;
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(26:9|10|11|12|13|14|15|17|18|19|20|(1:22)(1:81)|23|(10:25|26|27|(1:29)|30|(1:32)|33|(4:35|(2:38|36)|39|40)|41|(1:44))|45|(5:71|72|73|74|75)(1:47)|48|49|(3:62|63|(7:67|52|53|54|55|56|57))|51|52|53|54|55|56|57) */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.json.JSONObject c(android.content.Context r19, java.util.Map r20, java.util.Map r21, android.view.View r22, android.widget.ImageView.ScaleType r23) {
        /*
            Method dump skipped, instructions count: 512
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: D1.M.c(android.content.Context, java.util.Map, java.util.Map, android.view.View, android.widget.ImageView$ScaleType):org.json.JSONObject");
    }

    public static JSONObject d(Context context, View view) {
        KeyguardManager keyguardManager;
        JSONObject jSONObject = new JSONObject();
        if (view != null) {
            try {
                t0 t0Var = z1.p.f6575A.f6578c;
                jSONObject.put("can_show_on_lock_screen", t0.C(view));
                boolean z4 = false;
                if (context != null) {
                    Object systemService = context.getSystemService("keyguard");
                    if (systemService != null && (systemService instanceof KeyguardManager)) {
                        keyguardManager = (KeyguardManager) systemService;
                    } else {
                        keyguardManager = null;
                    }
                    if (keyguardManager != null && keyguardManager.isKeyguardLocked()) {
                        z4 = true;
                    }
                }
                jSONObject.put("is_keyguard_locked", z4);
            } catch (JSONException unused) {
                E1.m.g("Unable to get lock screen information");
            }
        }
        return jSONObject;
    }

    public static JSONObject e(View view) {
        int positionForView;
        JSONObject jSONObject = new JSONObject();
        if (view != null) {
            try {
                boolean z4 = true;
                if (((Boolean) A1.r.f168d.f171c.a(Gb.X6)).booleanValue()) {
                    t0 t0Var = z1.p.f6575A.f6578c;
                    ViewParent parent = view.getParent();
                    while (parent != null && !(parent instanceof ScrollView)) {
                        parent = parent.getParent();
                    }
                    if (parent == null) {
                        z4 = false;
                    }
                    jSONObject.put("contained_in_scroll_view", z4);
                } else {
                    t0 t0Var2 = z1.p.f6575A.f6578c;
                    ViewParent parent2 = view.getParent();
                    while (parent2 != null && !(parent2 instanceof AdapterView)) {
                        parent2 = parent2.getParent();
                    }
                    if (parent2 == null) {
                        positionForView = -1;
                    } else {
                        positionForView = ((AdapterView) parent2).getPositionForView(view);
                    }
                    if (positionForView == -1) {
                        z4 = false;
                    }
                    jSONObject.put("contained_in_scroll_view", z4);
                }
            } catch (Exception unused) {
            }
        }
        return jSONObject;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(10:18|(9:46|47|21|22|23|(3:25|(1:29)|39)(3:40|(1:42)|39)|30|(2:32|(1:34)(1:37))(1:38)|35)|20|21|22|23|(0)(0)|30|(0)(0)|35) */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0150, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:55:0x015c, code lost:
        E1.m.e("Could not log native template signal to JSON", r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:35:0x012a  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x013a  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0157 A[Catch: JSONException -> 0x0150, TRY_LEAVE, TryCatch #6 {JSONException -> 0x0150, blocks: (B:32:0x0121, B:49:0x014c, B:52:0x0152, B:53:0x0157), top: B:68:0x0121 }] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0173 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:55:0x015c -> B:67:0x0161). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.json.JSONObject f(android.content.Context r16, android.view.View r17) {
        /*
            Method dump skipped, instructions count: 406
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: D1.M.f(android.content.Context, android.view.View):org.json.JSONObject");
    }

    public static boolean g(Context context, mG mGVar) {
        eL eLVar;
        if (!mGVar.N) {
            return false;
        }
        vb vbVar = Gb.c7;
        A1.r rVar = A1.r.f168d;
        boolean booleanValue = ((Boolean) rVar.f171c.a(vbVar)).booleanValue();
        Eb eb = rVar.f171c;
        if (booleanValue) {
            return ((Boolean) eb.a(Gb.f7)).booleanValue();
        }
        String str = (String) eb.a(Gb.d7);
        if (!str.isEmpty() && context != null) {
            String packageName = context.getPackageName();
            M0 e4 = M0.e(new NK(';'));
            eL e5 = ((fL) e4.k).e(e4, str);
            do {
                eLVar = e5;
                if (eLVar.hasNext()) {
                }
            } while (!((String) eLVar.next()).equals(packageName));
            return true;
        }
        return false;
    }

    public static boolean h(int i4) {
        vb vbVar = Gb.Y2;
        A1.r rVar = A1.r.f168d;
        if (((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            if (!((Boolean) rVar.f171c.a(Gb.Z2)).booleanValue() && i4 > 15299999) {
                return false;
            }
            return true;
        }
        return true;
    }

    public static JSONObject i(Context context, Rect rect) {
        JSONObject jSONObject = new JSONObject();
        int i4 = rect.right - rect.left;
        C0124p c0124p = C0124p.f;
        jSONObject.put("width", c0124p.f161a.e(context, i4));
        E1.f fVar = c0124p.f161a;
        jSONObject.put("height", fVar.e(context, rect.bottom - rect.top));
        jSONObject.put("x", fVar.e(context, rect.left));
        jSONObject.put("y", fVar.e(context, rect.top));
        jSONObject.put("relative_to", "self");
        return jSONObject;
    }

    public static int j(int i4) {
        if (i4 != -2) {
            if (i4 != -1) {
                return 2;
            }
            return 3;
        }
        return 4;
    }
}
