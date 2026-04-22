package C1;

import D1.C0195o;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class n extends RelativeLayout {

    /* renamed from: j  reason: collision with root package name */
    public final C0195o f378j;

    /* renamed from: k  reason: collision with root package name */
    public boolean f379k;

    public n(Context context, String str, String str2, String str3) {
        super(context);
        C0195o c0195o = new C0195o(context);
        c0195o.f747c = str;
        this.f378j = c0195o;
        c0195o.f749e = str2;
        c0195o.f748d = str3;
    }

    @Override // android.view.ViewGroup
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.f379k) {
            this.f378j.a(motionEvent);
            return false;
        }
        return false;
    }
}
