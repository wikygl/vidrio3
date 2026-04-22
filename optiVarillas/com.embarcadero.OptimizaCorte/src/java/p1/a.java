package P1;

import A1.C0124p;
import E1.f;
import E1.m;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import t1.AbstractC0800b;
import t1.C0803e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a extends ViewGroup {
    public AbstractC0800b getAdListener() {
        throw null;
    }

    public C0803e getAdSize() {
        throw null;
    }

    public String getAdUnitId() {
        throw null;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onLayout(boolean z4, int i4, int i5, int i6, int i7) {
        View childAt = getChildAt(0);
        if (childAt != null && childAt.getVisibility() != 8) {
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            int i8 = ((i6 - i4) - measuredWidth) / 2;
            int i9 = ((i7 - i5) - measuredHeight) / 2;
            childAt.layout(i8, i9, measuredWidth + i8, measuredHeight + i9);
        }
    }

    @Override // android.view.View
    public final void onMeasure(int i4, int i5) {
        C0803e c0803e;
        int i6;
        int i7;
        int i8 = 0;
        View childAt = getChildAt(0);
        if (childAt != null && childAt.getVisibility() != 8) {
            measureChild(childAt, i4, i5);
            i8 = childAt.getMeasuredWidth();
            i6 = childAt.getMeasuredHeight();
        } else {
            try {
                c0803e = getAdSize();
            } catch (NullPointerException e4) {
                m.e("Unable to retrieve ad size.", e4);
                c0803e = null;
            }
            if (c0803e != null) {
                Context context = getContext();
                int i9 = c0803e.f5791a;
                if (i9 != -3) {
                    if (i9 != -1) {
                        f fVar = C0124p.f.f161a;
                        i7 = f.m(context, i9);
                    } else {
                        i7 = context.getResources().getDisplayMetrics().widthPixels;
                    }
                } else {
                    i7 = -1;
                }
                i6 = c0803e.b(context);
                i8 = i7;
            } else {
                i6 = 0;
            }
        }
        setMeasuredDimension(View.resolveSize(Math.max(i8, getSuggestedMinimumWidth()), i4), View.resolveSize(Math.max(i6, getSuggestedMinimumHeight()), i5));
    }

    public void setAdListener(AbstractC0800b abstractC0800b) {
        throw null;
    }

    public void setAdSize(C0803e c0803e) {
        throw null;
    }

    public void setAdUnitId(String str) {
        throw null;
    }
}
