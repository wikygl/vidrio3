package N;

import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a extends ClickableSpan {

    /* renamed from: a  reason: collision with root package name */
    public final int f1738a;

    /* renamed from: b  reason: collision with root package name */
    public final l f1739b;

    /* renamed from: c  reason: collision with root package name */
    public final int f1740c;

    public a(int i4, l lVar, int i5) {
        this.f1738a = i4;
        this.f1739b = lVar;
        this.f1740c = i5;
    }

    @Override // android.text.style.ClickableSpan
    public final void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("ACCESSIBILITY_CLICKABLE_SPAN_ID", this.f1738a);
        this.f1739b.f1743a.performAction(this.f1740c, bundle);
    }
}
