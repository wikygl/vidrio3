package x;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import v.k;

/* renamed from: x.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class AbstractC0852e extends androidx.constraintlayout.widget.c {

    /* renamed from: p  reason: collision with root package name */
    public boolean f6434p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f6435q;

    public AbstractC0852e(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void e(AttributeSet attributeSet) {
        super.e(attributeSet);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, C0851d.f6426b);
            int indexCount = obtainStyledAttributes.getIndexCount();
            for (int i4 = 0; i4 < indexCount; i4++) {
                int index = obtainStyledAttributes.getIndex(i4);
                if (index == 6) {
                    this.f6434p = true;
                } else if (index == 13) {
                    this.f6435q = true;
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onAttachedToWindow() {
        ConstraintLayout parent;
        super.onAttachedToWindow();
        if ((this.f6434p || this.f6435q) && (parent = getParent()) != null && (parent instanceof ConstraintLayout)) {
            ConstraintLayout constraintLayout = parent;
            int visibility = getVisibility();
            float elevation = getElevation();
            for (int i4 = 0; i4 < ((androidx.constraintlayout.widget.c) this).k; i4++) {
                View view = (View) constraintLayout.j.get(((androidx.constraintlayout.widget.c) this).j[i4]);
                if (view != null) {
                    if (this.f6434p) {
                        view.setVisibility(visibility);
                    }
                    if (this.f6435q && elevation > 0.0f) {
                        view.setTranslationZ(view.getTranslationZ() + elevation);
                    }
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setElevation(float f) {
        super/*android.view.View*/.setElevation(f);
        c();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setVisibility(int i4) {
        super/*android.view.View*/.setVisibility(i4);
        c();
    }

    public void h(k kVar, int i4, int i5) {
    }
}
