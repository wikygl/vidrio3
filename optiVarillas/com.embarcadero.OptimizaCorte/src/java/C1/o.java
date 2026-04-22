package C1;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.google.android.gms.internal.ads.em;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class o {

    /* renamed from: a  reason: collision with root package name */
    public final int f380a;

    /* renamed from: b  reason: collision with root package name */
    public final ViewGroup.LayoutParams f381b;

    /* renamed from: c  reason: collision with root package name */
    public final ViewGroup f382c;

    /* renamed from: d  reason: collision with root package name */
    public final Context f383d;

    public o(em emVar) {
        this.f381b = emVar.getLayoutParams();
        ViewParent parent = emVar.getParent();
        this.f383d = emVar.O();
        if (parent != null && (parent instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) parent;
            this.f382c = viewGroup;
            this.f380a = viewGroup.indexOfChild(emVar.L());
            viewGroup.removeView(emVar.L());
            emVar.O0(true);
            return;
        }
        throw new Exception("Could not get the parent of the WebView for an overlay.");
    }
}
