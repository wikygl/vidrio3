package C1;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import i2.C0475v;
import java.io.ByteArrayOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class j implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f373j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f374k;

    public /* synthetic */ j(int i4, Object obj) {
        this.f373j = i4;
        this.f374k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        String concat;
        switch (this.f373j) {
            case 0:
                ((s) this.f374k).r();
                return;
            default:
                C0475v c0475v = (C0475v) this.f374k;
                c0475v.getClass();
                JSONObject jSONObject = new JSONObject();
                Application application = c0475v.f3804a;
                try {
                    jSONObject.put("app_name", application.getPackageManager().getApplicationLabel(application.getApplicationInfo()).toString());
                    Drawable applicationIcon = application.getPackageManager().getApplicationIcon(application.getApplicationInfo());
                    if (applicationIcon == null) {
                        concat = null;
                    } else {
                        Bitmap createBitmap = Bitmap.createBitmap(applicationIcon.getIntrinsicWidth(), applicationIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(createBitmap);
                        applicationIcon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        applicationIcon.draw(canvas);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        createBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        concat = "data:image/png;base64,".concat(String.valueOf(Base64.encodeToString(byteArrayOutputStream.toByteArray(), 2)));
                    }
                    jSONObject.put("app_icon", concat);
                    jSONObject.put("stored_infos_map", c0475v.f3810h.a());
                } catch (JSONException unused) {
                }
                c0475v.f3809g.f3777g.a("UMP_configureFormWithAppAssets", jSONObject.toString());
                return;
        }
    }
}
