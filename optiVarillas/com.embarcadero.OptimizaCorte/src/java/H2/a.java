package H2;

import M.C0219a;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.material.internal.CheckableImageButton;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a extends C0219a {

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ CheckableImageButton f1020d;

    public a(CheckableImageButton checkableImageButton) {
        this.f1020d = checkableImageButton;
    }

    @Override // M.C0219a
    public final void c(View view, AccessibilityEvent accessibilityEvent) {
        super.c(view, accessibilityEvent);
        accessibilityEvent.setChecked(this.f1020d.m);
    }

    @Override // M.C0219a
    public final void d(View view, N.l lVar) {
        View.AccessibilityDelegate accessibilityDelegate = this.f1582a;
        AccessibilityNodeInfo accessibilityNodeInfo = lVar.f1743a;
        accessibilityDelegate.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        CheckableImageButton checkableImageButton = this.f1020d;
        accessibilityNodeInfo.setCheckable(checkableImageButton.n);
        accessibilityNodeInfo.setChecked(checkableImageButton.m);
    }
}
