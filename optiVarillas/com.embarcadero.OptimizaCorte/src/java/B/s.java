package B;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import androidx.core.graphics.drawable.IconCompat;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class s {

    /* renamed from: a  reason: collision with root package name */
    public final Context f255a;

    /* renamed from: e  reason: collision with root package name */
    public CharSequence f259e;
    public CharSequence f;

    /* renamed from: g  reason: collision with root package name */
    public PendingIntent f260g;

    /* renamed from: h  reason: collision with root package name */
    public IconCompat f261h;

    /* renamed from: i  reason: collision with root package name */
    public int f262i;

    /* renamed from: k  reason: collision with root package name */
    public u f264k;

    /* renamed from: m  reason: collision with root package name */
    public Bundle f266m;

    /* renamed from: n  reason: collision with root package name */
    public String f267n;

    /* renamed from: o  reason: collision with root package name */
    public final boolean f268o;

    /* renamed from: p  reason: collision with root package name */
    public final Notification f269p;
    @Deprecated

    /* renamed from: q  reason: collision with root package name */
    public final ArrayList<String> f270q;

    /* renamed from: b  reason: collision with root package name */
    public final ArrayList<p> f256b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    public final ArrayList<z> f257c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    public final ArrayList<p> f258d = new ArrayList<>();

    /* renamed from: j  reason: collision with root package name */
    public final boolean f263j = true;

    /* renamed from: l  reason: collision with root package name */
    public boolean f265l = false;

    public s(Context context, String str) {
        Notification notification = new Notification();
        this.f269p = notification;
        this.f255a = context;
        this.f267n = str;
        notification.when = System.currentTimeMillis();
        notification.audioStreamType = -1;
        this.f262i = 0;
        this.f270q = new ArrayList<>();
        this.f268o = true;
    }

    public static CharSequence b(CharSequence charSequence) {
        if (charSequence == null) {
            return charSequence;
        }
        if (charSequence.length() > 5120) {
            return charSequence.subSequence(0, 5120);
        }
        return charSequence;
    }

    public final Notification a() {
        Notification build;
        Bundle bundle;
        v vVar = new v(this);
        s sVar = vVar.f274c;
        u uVar = sVar.f264k;
        if (uVar != null) {
            uVar.b(vVar);
        }
        int i4 = Build.VERSION.SDK_INT;
        Notification.Builder builder = vVar.f273b;
        if (i4 >= 26) {
            build = builder.build();
        } else if (i4 >= 24) {
            build = builder.build();
        } else {
            builder.setExtras(vVar.f275d);
            build = builder.build();
        }
        if (uVar != null) {
            sVar.f264k.getClass();
        }
        if (uVar != null && (bundle = build.extras) != null) {
            uVar.a(bundle);
        }
        return build;
    }

    public final void c(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < 27) {
            Resources resources = this.f255a.getResources();
            int dimensionPixelSize = resources.getDimensionPixelSize(2131099745);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(2131099744);
            if (bitmap.getWidth() > dimensionPixelSize || bitmap.getHeight() > dimensionPixelSize2) {
                double min = Math.min(dimensionPixelSize / Math.max(1, bitmap.getWidth()), dimensionPixelSize2 / Math.max(1, bitmap.getHeight()));
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) Math.ceil(bitmap.getWidth() * min), (int) Math.ceil(bitmap.getHeight() * min), true);
            }
        }
        PorterDuff.Mode mode = IconCompat.k;
        bitmap.getClass();
        IconCompat iconCompat = new IconCompat(1);
        iconCompat.b = bitmap;
        this.f261h = iconCompat;
    }

    public final void d(u uVar) {
        if (this.f264k != uVar) {
            this.f264k = uVar;
            if (uVar != null) {
                uVar.d(this);
            }
        }
    }
}
