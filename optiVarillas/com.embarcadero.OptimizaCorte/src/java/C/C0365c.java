package c;

import android.content.Context;
import android.content.Intent;
import v3.h;

/* renamed from: c.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0365c extends AbstractC0363a<Intent, androidx.activity.result.a> {
    @Override // c.AbstractC0363a
    public final Intent a(Context context, Intent intent) {
        Intent intent2 = intent;
        h.e(context, "context");
        h.e(intent2, "input");
        return intent2;
    }

    @Override // c.AbstractC0363a
    public final androidx.activity.result.a c(int i4, Intent intent) {
        return new androidx.activity.result.a(i4, intent);
    }
}
