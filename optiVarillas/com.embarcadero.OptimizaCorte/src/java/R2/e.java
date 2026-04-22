package R2;

import M.C0219a;
import N.l;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.google.android.material.snackbar.BaseTransientBottomBar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class e extends C0219a {

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ BaseTransientBottomBar f2064d;

    public e(BaseTransientBottomBar baseTransientBottomBar) {
        this.f2064d = baseTransientBottomBar;
    }

    @Override // M.C0219a
    public final void d(View view, l lVar) {
        View.AccessibilityDelegate accessibilityDelegate = this.f1582a;
        AccessibilityNodeInfo accessibilityNodeInfo = lVar.f1743a;
        accessibilityDelegate.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        lVar.a(1048576);
        accessibilityNodeInfo.setDismissable(true);
    }

    @Override // M.C0219a
    public final boolean g(View view, int i4, Bundle bundle) {
        if (i4 == 1048576) {
            this.f2064d.a();
            return true;
        }
        return super.g(view, i4, bundle);
    }
}
