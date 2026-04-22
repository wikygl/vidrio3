package V2;

import L2.b;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import l.C0689B;
import q2.C0771a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a extends C0689B {
    public static int r(Context context, TypedArray typedArray, int... iArr) {
        int i4 = -1;
        for (int i5 = 0; i5 < iArr.length && i4 < 0; i5++) {
            int i6 = iArr[i5];
            TypedValue typedValue = new TypedValue();
            if (typedArray.getValue(i6, typedValue) && typedValue.type == 2) {
                TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{typedValue.data});
                int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, -1);
                obtainStyledAttributes.recycle();
                i4 = dimensionPixelSize;
            } else {
                i4 = typedArray.getDimensionPixelSize(i6, -1);
            }
        }
        return i4;
    }

    @Override // l.C0689B, android.widget.TextView
    public final void setTextAppearance(Context context, int i4) {
        super.setTextAppearance(context, i4);
        if (b.b(context, 2130904075, true)) {
            TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(i4, C0771a.f5625s);
            int r4 = r(getContext(), obtainStyledAttributes, 1, 2);
            obtainStyledAttributes.recycle();
            if (r4 >= 0) {
                setLineHeight(r4);
            }
        }
    }
}
