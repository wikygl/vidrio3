package B;

import android.app.PendingIntent;
import android.os.Bundle;
import androidx.core.graphics.drawable.IconCompat;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class p {

    /* renamed from: a  reason: collision with root package name */
    public final Bundle f241a;

    /* renamed from: b  reason: collision with root package name */
    public IconCompat f242b;

    /* renamed from: c  reason: collision with root package name */
    public final B[] f243c;

    /* renamed from: d  reason: collision with root package name */
    public final boolean f244d;

    /* renamed from: e  reason: collision with root package name */
    public final boolean f245e;
    public final int f;

    /* renamed from: g  reason: collision with root package name */
    public final boolean f246g;
    @Deprecated

    /* renamed from: h  reason: collision with root package name */
    public final int f247h;

    /* renamed from: i  reason: collision with root package name */
    public final CharSequence f248i;

    /* renamed from: j  reason: collision with root package name */
    public final PendingIntent f249j;

    /* renamed from: k  reason: collision with root package name */
    public final boolean f250k;

    public p(IconCompat iconCompat, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle, B[] bArr, B[] bArr2) {
        this.f245e = true;
        this.f242b = iconCompat;
        if (iconCompat != null && iconCompat.e() == 2) {
            this.f247h = iconCompat.d();
        }
        this.f248i = s.b(charSequence);
        this.f249j = pendingIntent;
        this.f241a = bundle == null ? new Bundle() : bundle;
        this.f243c = bArr;
        this.f244d = true;
        this.f = 0;
        this.f245e = true;
        this.f246g = false;
        this.f250k = false;
    }
}
