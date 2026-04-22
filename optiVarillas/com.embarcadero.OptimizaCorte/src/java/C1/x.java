package C1;

import A1.C0124p;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.xb;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class x extends FrameLayout implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final ImageButton f414j;

    /* renamed from: k  reason: collision with root package name */
    public final e f415k;

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:16:0x0058 -> B:18:0x0060). Please submit an issue!!! */
    public x(Context context, w wVar, e eVar) {
        super(context);
        Drawable drawable;
        this.f415k = eVar;
        setOnClickListener(this);
        ImageButton imageButton = new ImageButton(context);
        this.f414j = imageButton;
        String str = (String) A1.r.f168d.f171c.a(Gb.R0);
        if (!TextUtils.isEmpty(str) && !"default".equals(str)) {
            Resources a4 = z1.p.f6575A.f6581g.a();
            if (a4 != null) {
                try {
                } catch (Resources.NotFoundException unused) {
                    E1.m.b("Close button resource not found, falling back to default.");
                }
                if ("white".equals(str)) {
                    drawable = a4.getDrawable(2131165304);
                } else {
                    if ("black".equals(str)) {
                        drawable = a4.getDrawable(2131165303);
                    }
                    drawable = null;
                }
                if (drawable == null) {
                    imageButton.setImageResource(17301527);
                } else {
                    imageButton.setImageDrawable(drawable);
                    imageButton.setScaleType(ImageView.ScaleType.CENTER);
                }
            } else {
                imageButton.setImageResource(17301527);
            }
        } else {
            imageButton.setImageResource(17301527);
        }
        this.f414j.setBackgroundColor(0);
        this.f414j.setOnClickListener(this);
        ImageButton imageButton2 = this.f414j;
        E1.f fVar = C0124p.f.f161a;
        imageButton2.setPadding(E1.f.m(context, wVar.f410a), E1.f.j(context.getResources().getDisplayMetrics(), 0), E1.f.j(context.getResources().getDisplayMetrics(), wVar.f411b), E1.f.j(context.getResources().getDisplayMetrics(), wVar.f412c));
        this.f414j.setContentDescription("Interstitial close button");
        addView(this.f414j, new FrameLayout.LayoutParams(E1.f.j(context.getResources().getDisplayMetrics(), wVar.f413d + wVar.f410a + wVar.f411b), E1.f.j(context.getResources().getDisplayMetrics(), wVar.f413d + wVar.f412c), 17));
        xb xbVar = Gb.S0;
        A1.r rVar = A1.r.f168d;
        long longValue = ((Long) rVar.f171c.a(xbVar)).longValue();
        if (longValue <= 0) {
            return;
        }
        v vVar = ((Boolean) rVar.f171c.a(Gb.T0)).booleanValue() ? new v(this, 0) : null;
        this.f414j.setAlpha(0.0f);
        this.f414j.animate().alpha(1.0f).setDuration(longValue).setListener(vVar);
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        e eVar = this.f415k;
        if (eVar != null) {
            s sVar = (s) eVar;
            sVar.f391F = 2;
            sVar.f392k.finish();
        }
    }
}
