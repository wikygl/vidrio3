package N;

import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public class o {

    /* renamed from: a  reason: collision with root package name */
    public final Object f1758a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a extends AccessibilityNodeProvider {

        /* renamed from: a  reason: collision with root package name */
        public final o f1759a;

        public a(o oVar) {
            this.f1759a = oVar;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public final AccessibilityNodeInfo createAccessibilityNodeInfo(int i4) {
            l a4 = this.f1759a.a(i4);
            if (a4 == null) {
                return null;
            }
            return a4.f1743a;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public final List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String str, int i4) {
            this.f1759a.getClass();
            return null;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public final AccessibilityNodeInfo findFocus(int i4) {
            l b4 = this.f1759a.b(i4);
            if (b4 == null) {
                return null;
            }
            return b4.f1743a;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public final boolean performAction(int i4, int i5, Bundle bundle) {
            return this.f1759a.c(i4, i5, bundle);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b extends a {
        @Override // android.view.accessibility.AccessibilityNodeProvider
        public final void addExtraDataToAccessibilityNodeInfo(int i4, AccessibilityNodeInfo accessibilityNodeInfo, String str, Bundle bundle) {
            this.f1759a.getClass();
        }
    }

    public o() {
        if (Build.VERSION.SDK_INT >= 26) {
            this.f1758a = new a(this);
        } else {
            this.f1758a = new a(this);
        }
    }

    public l a(int i4) {
        return null;
    }

    public l b(int i4) {
        return null;
    }

    public boolean c(int i4, int i5, Bundle bundle) {
        return false;
    }

    public o(AccessibilityNodeProvider accessibilityNodeProvider) {
        this.f1758a = accessibilityNodeProvider;
    }
}
