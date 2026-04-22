package l;

import F.a;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.CheckedTextView;

/* renamed from: l.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0699h {

    /* renamed from: a  reason: collision with root package name */
    public final CheckedTextView f5147a;

    /* renamed from: b  reason: collision with root package name */
    public ColorStateList f5148b = null;

    /* renamed from: c  reason: collision with root package name */
    public PorterDuff.Mode f5149c = null;

    /* renamed from: d  reason: collision with root package name */
    public boolean f5150d = false;

    /* renamed from: e  reason: collision with root package name */
    public boolean f5151e = false;
    public boolean f;

    public C0699h(CheckedTextView checkedTextView) {
        this.f5147a = checkedTextView;
    }

    public final void a() {
        CheckedTextView checkedTextView = this.f5147a;
        Drawable checkMarkDrawable = checkedTextView.getCheckMarkDrawable();
        if (checkMarkDrawable != null) {
            if (this.f5150d || this.f5151e) {
                Drawable mutate = F.a.g(checkMarkDrawable).mutate();
                if (this.f5150d) {
                    a.C0006a.h(mutate, this.f5148b);
                }
                if (this.f5151e) {
                    a.C0006a.i(mutate, this.f5149c);
                }
                if (mutate.isStateful()) {
                    mutate.setState(checkedTextView.getDrawableState());
                }
                checkedTextView.setCheckMarkDrawable(mutate);
            }
        }
    }
}
