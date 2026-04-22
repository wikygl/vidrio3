package l;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class g0 {

    /* renamed from: a  reason: collision with root package name */
    public final Context f5141a;

    /* renamed from: b  reason: collision with root package name */
    public final View f5142b;

    /* renamed from: c  reason: collision with root package name */
    public final TextView f5143c;

    /* renamed from: d  reason: collision with root package name */
    public final WindowManager.LayoutParams f5144d;

    /* renamed from: e  reason: collision with root package name */
    public final Rect f5145e;
    public final int[] f;

    /* renamed from: g  reason: collision with root package name */
    public final int[] f5146g;

    public g0(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.f5144d = layoutParams;
        this.f5145e = new Rect();
        this.f = new int[2];
        this.f5146g = new int[2];
        this.f5141a = context;
        View inflate = LayoutInflater.from(context).inflate(2131427355, (ViewGroup) null);
        this.f5142b = inflate;
        this.f5143c = (TextView) inflate.findViewById(2131231078);
        layoutParams.setTitle(g0.class.getSimpleName());
        layoutParams.packageName = context.getPackageName();
        layoutParams.type = 1002;
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.format = -3;
        layoutParams.windowAnimations = 2131886084;
        layoutParams.flags = 24;
    }
}
