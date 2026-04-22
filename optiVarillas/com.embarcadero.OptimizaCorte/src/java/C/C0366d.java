package c;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import androidx.activity.result.g;
import v3.h;

/* renamed from: c.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0366d extends AbstractC0363a<g, androidx.activity.result.a> {
    @Override // c.AbstractC0363a
    public final Intent a(Context context, g gVar) {
        g gVar2 = gVar;
        h.e(context, "context");
        h.e(gVar2, "input");
        Intent putExtra = new Intent("androidx.activity.result.contract.action.INTENT_SENDER_REQUEST").putExtra("androidx.activity.result.contract.extra.INTENT_SENDER_REQUEST", (Parcelable) gVar2);
        h.d(putExtra, "Intent(ACTION_INTENT_SEN…NT_SENDER_REQUEST, input)");
        return putExtra;
    }

    @Override // c.AbstractC0363a
    public final androidx.activity.result.a c(int i4, Intent intent) {
        return new androidx.activity.result.a(i4, intent);
    }
}
