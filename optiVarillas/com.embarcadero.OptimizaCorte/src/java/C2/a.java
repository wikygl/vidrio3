package C2;

import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a implements View.OnTouchListener {

    /* renamed from: j  reason: collision with root package name */
    public final Dialog f420j;

    /* renamed from: k  reason: collision with root package name */
    public final int f421k;

    /* renamed from: l  reason: collision with root package name */
    public final int f422l;

    /* renamed from: m  reason: collision with root package name */
    public final int f423m;

    public a(Dialog dialog, Rect rect) {
        this.f420j = dialog;
        this.f421k = rect.left;
        this.f422l = rect.top;
        this.f423m = ViewConfiguration.get(dialog.getContext()).getScaledWindowTouchSlop();
    }

    @Override // android.view.View.OnTouchListener
    public final boolean onTouch(View view, MotionEvent motionEvent) {
        View findViewById = view.findViewById(16908290);
        int left = findViewById.getLeft() + this.f421k;
        int width = findViewById.getWidth() + left;
        int top = findViewById.getTop() + this.f422l;
        if (new RectF(left, top, width, findViewById.getHeight() + top).contains(motionEvent.getX(), motionEvent.getY())) {
            return false;
        }
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        if (motionEvent.getAction() == 1) {
            obtain.setAction(4);
        }
        if (Build.VERSION.SDK_INT < 28) {
            obtain.setAction(0);
            int i4 = this.f423m;
            obtain.setLocation((-i4) - 1, (-i4) - 1);
        }
        view.performClick();
        return this.f420j.onTouchEvent(obtain);
    }
}
