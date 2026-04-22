package e1;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import b1.C0353a;
import com.google.gson.Gson;

/* renamed from: e1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0407a {

    /* renamed from: a  reason: collision with root package name */
    public final SharedPreferences f3355a;

    public C0407a(Context context) {
        this.f3355a = context.getSharedPreferences("cutterglobals", 0);
    }

    public final C0353a a() {
        Gson gson = new Gson();
        C0353a c0353a = new C0353a();
        String string = this.f3355a.getString("globals", "");
        if (!TextUtils.isEmpty(string)) {
            try {
                return (C0353a) gson.b(string);
            } catch (Exception e4) {
                e4.printStackTrace();
                return c0353a;
            }
        }
        return c0353a;
    }

    public final synchronized void b(C0353a c0353a) {
        try {
            SharedPreferences.Editor edit = this.f3355a.edit();
            edit.putString("globals", new Gson().f(new C0353a(c0353a)));
            edit.apply();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }
}
