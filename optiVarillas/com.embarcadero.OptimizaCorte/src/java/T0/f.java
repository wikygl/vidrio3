package T0;

import D.f;
import T0.f;
import a1.InterfaceC0342a;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import b1.C0353a;
import c1.C0373f;
import j$.util.Objects;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class f extends RecyclerView.e<RecyclerView.B> {

    /* renamed from: c  reason: collision with root package name */
    public final ArrayList<b1.e> f2323c;

    /* renamed from: d  reason: collision with root package name */
    public final Context f2324d;

    /* renamed from: e  reason: collision with root package name */
    public final C0353a f2325e;
    public final boolean f;

    /* renamed from: g  reason: collision with root package name */
    public InterfaceC0342a f2326g;

    /* renamed from: h  reason: collision with root package name */
    public int f2327h;

    /* renamed from: i  reason: collision with root package name */
    public int f2328i;

    /* renamed from: j  reason: collision with root package name */
    public int f2329j;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends RecyclerView.B {

        /* renamed from: t  reason: collision with root package name */
        public FrameLayout f2330t;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends RecyclerView.B {

        /* renamed from: t  reason: collision with root package name */
        public TextView f2331t;

        /* renamed from: u  reason: collision with root package name */
        public TextView f2332u;

        /* renamed from: v  reason: collision with root package name */
        public TextView f2333v;

        /* renamed from: w  reason: collision with root package name */
        public ProgressBar f2334w;

        /* renamed from: x  reason: collision with root package name */
        public CardView f2335x;

        /* renamed from: y  reason: collision with root package name */
        public TextView f2336y;
    }

    public f(ArrayList<b1.e> arrayList, Context context, C0353a c0353a, boolean z4) {
        this.f2323c = arrayList;
        this.f2324d = context;
        this.f2325e = c0353a;
        this.f = z4;
    }

    public final int c() {
        return this.f2323c.size();
    }

    public final void e(int i4) {
        this.f2323c.get(i4).getClass();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public final void g(RecyclerView.B b4, final int i4) {
        DecimalFormat decimalFormat;
        b bVar;
        TextView textView;
        TextView textView2;
        boolean z4;
        String c4;
        String str;
        e(i4);
        final b bVar2 = (b) b4;
        TextView textView3 = bVar2.f2331t;
        boolean z5 = this.f;
        ArrayList<b1.e> arrayList = this.f2323c;
        TextView textView4 = bVar2.f2336y;
        final TextView textView5 = bVar2.f2332u;
        if (z5) {
            bVar2.f2335x.setOnLongClickListener(new View.OnLongClickListener() { // from class: T0.c
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    f fVar = f.this;
                    ArrayList<b1.e> arrayList2 = fVar.f2323c;
                    int i5 = i4;
                    int i6 = arrayList2.get(i5).f2917g;
                    int i7 = arrayList2.get(i5).f2916e;
                    f.b bVar3 = bVar2;
                    if (i6 < i7) {
                        arrayList2.get(i5).f2917g++;
                        bVar3.f2336y.setText(arrayList2.get(i5).f2917g + "/" + arrayList2.get(i5).f2916e);
                    } else if (arrayList2.get(i5).f2917g == arrayList2.get(i5).f2916e) {
                        arrayList2.get(i5).f2917g = 0;
                        bVar3.f2336y.setText(arrayList2.get(i5).f2917g + "/" + arrayList2.get(i5).f2916e);
                    }
                    int i8 = arrayList2.get(i5).f2917g;
                    TextView textView6 = textView5;
                    if (i8 > 0 && arrayList2.get(i5).f2917g < arrayList2.get(i5).f2916e) {
                        arrayList2.get(i5).f = Color.parseColor("#d5efe0");
                        textView6.setBackgroundColor(Color.parseColor("#d5efe0"));
                    }
                    if (arrayList2.get(i5).f2917g == 0) {
                        arrayList2.get(i5).f = 0;
                        textView6.setBackgroundColor(0);
                    }
                    if (arrayList2.get(i5).f2917g == arrayList2.get(i5).f2916e) {
                        arrayList2.get(i5).f = Color.parseColor("#ffab00");
                        textView6.setBackgroundColor(Color.parseColor("#ffab00"));
                    }
                    fVar.f2326g.b(i5, arrayList2.get(i5));
                    return true;
                }
            });
            textView4.setText(arrayList.get(i4).f2917g + "/" + arrayList.get(i4).f2916e);
        } else {
            textView4.setVisibility(4);
            textView5.setOnLongClickListener(new View.OnLongClickListener() { // from class: T0.d
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    f fVar = f.this;
                    fVar.getClass();
                    C0373f.l(20L, view.getContext());
                    ArrayList<b1.e> arrayList2 = fVar.f2323c;
                    int i5 = i4;
                    b1.f fVar2 = arrayList2.get(i5).f2918h;
                    Object[] spans = fVar2.getSpans(fVar.f2327h, fVar.f2328i, Object.class);
                    int length = spans.length;
                    int i6 = 0;
                    if (length > 0) {
                        for (Object obj : spans) {
                            if (obj instanceof ImageSpan) {
                                length--;
                            }
                        }
                    }
                    TextView textView6 = textView5;
                    if (length > 0) {
                        for (Object obj2 : spans) {
                            if (!(obj2 instanceof ImageSpan)) {
                                fVar2.removeSpan(obj2);
                            }
                        }
                        textView6.setText(fVar2);
                        Object[] spans2 = fVar2.getSpans(0, fVar2.length(), Object.class);
                        ArrayList arrayList3 = new ArrayList();
                        if (spans2 != null) {
                            int length2 = spans2.length;
                            while (i6 < length2) {
                                Object obj3 = spans2[i6];
                                if (obj3 instanceof CharacterStyle) {
                                    arrayList3.add(new b1.g(fVar2.getSpanStart(obj3), fVar2.getSpanEnd(obj3)));
                                }
                                i6++;
                            }
                            if (spans2.length > 0) {
                                fVar2.f2919j = arrayList3;
                            }
                        }
                    } else {
                        fVar2.setSpan(new StrikethroughSpan(), fVar.f2327h, fVar.f2328i, 33);
                        fVar2.setSpan(new BackgroundColorSpan(Color.rgb(255, 165, 0)), fVar.f2327h, fVar.f2328i, 33);
                        textView6.setText(fVar2);
                        Object[] spans3 = fVar2.getSpans(0, fVar2.length(), Object.class);
                        ArrayList arrayList4 = new ArrayList();
                        if (spans3 != null) {
                            int length3 = spans3.length;
                            while (i6 < length3) {
                                Object obj4 = spans3[i6];
                                if (obj4 instanceof CharacterStyle) {
                                    arrayList4.add(new b1.g(fVar2.getSpanStart(obj4), fVar2.getSpanEnd(obj4)));
                                }
                                i6++;
                            }
                            if (spans3.length > 0) {
                                fVar2.f2919j = arrayList4;
                            }
                        }
                    }
                    arrayList2.get(i5).f2918h = fVar2;
                    fVar.f2326g.b(i5, arrayList2.get(i5));
                    return true;
                }
            });
            textView5.setOnTouchListener(new View.OnTouchListener() { // from class: T0.e
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    f fVar = f.this;
                    fVar.getClass();
                    float x4 = motionEvent.getX();
                    float y4 = motionEvent.getY();
                    TextView textView6 = textView5;
                    int offsetForPosition = textView6.getOffsetForPosition(x4, y4);
                    fVar.f2329j = offsetForPosition;
                    if (offsetForPosition >= 0) {
                        String charSequence = textView6.getText().toString();
                        String charSequence2 = textView6.getText().toString();
                        if (textView6.getText().toString().contains("+")) {
                            int indexOf = charSequence2.indexOf("+", fVar.f2329j) - 1;
                            fVar.f2328i = indexOf;
                            if (indexOf <= -1) {
                                fVar.f2328i = charSequence2.length();
                            }
                            String substring = charSequence.substring(0, fVar.f2329j);
                            if (substring.lastIndexOf("+") == -1) {
                                fVar.f2327h = 7;
                            } else {
                                fVar.f2327h = substring.lastIndexOf("+") + 9;
                            }
                            int i5 = fVar.f2327h;
                            if (i5 <= 1 || i5 > textView6.getText().toString().length()) {
                                fVar.f2327h = 0;
                            }
                        } else {
                            if (7 < textView6.getText().toString().length()) {
                                fVar.f2327h = 7;
                            } else {
                                fVar.f2327h = 0;
                            }
                            fVar.f2328i = textView6.getText().toString().length();
                        }
                    }
                    return false;
                }
            });
        }
        C0353a c0353a = this.f2325e;
        boolean z6 = c0353a.f2890q;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        new DecimalFormat("#.#").setDecimalFormatSymbols(decimalFormatSymbols);
        Context context = this.f2324d;
        if (z5) {
            if (z6) {
                textView2 = textView5;
                String j4 = C0373f.j(Double.parseDouble(arrayList.get(i4).f2912a) - c0353a.f2877c, c0353a.f2889p);
                StringBuilder sb = new StringBuilder();
                sb.append(arrayList.get(i4).f2916e);
                sb.append(" ");
                sb.append(context.getString(2131820868));
                sb.append(" ");
                bVar = bVar2;
                textView = textView3;
                sb.append(C0373f.j(Double.parseDouble(arrayList.get(i4).f2912a), c0353a.f2889p));
                sb.append(" ");
                sb.append(c0353a.f2888o);
                sb.append(" (");
                sb.append(context.getString(2131820870));
                sb.append(" ");
                sb.append(j4);
                sb.append(" ");
                c4 = C.b.c(sb, c0353a.f2888o, ")");
                str = context.getString(2131820869) + " " + C0373f.j(Double.parseDouble(arrayList.get(i4).f2914c), c0353a.f2889p) + " " + c0353a.f2888o + " (" + decimalFormat.format((Double.parseDouble(arrayList.get(i4).f2914c) / Double.parseDouble(arrayList.get(i4).f2912a)) * 100.0d) + "%)";
            } else {
                bVar = bVar2;
                textView = textView3;
                textView2 = textView5;
                String g4 = C0373f.g(Double.parseDouble(arrayList.get(i4).f2912a) - c0353a.f2877c);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(arrayList.get(i4).f2916e);
                sb2.append(" ");
                sb2.append(context.getString(2131820868));
                sb2.append(" ");
                sb2.append(arrayList.get(i4).f2912a);
                sb2.append(" ");
                sb2.append(c0353a.f2888o);
                sb2.append(" (");
                sb2.append(context.getString(2131820870));
                sb2.append(" ");
                sb2.append(g4);
                sb2.append(" ");
                c4 = C.b.c(sb2, c0353a.f2888o, ")");
                str = context.getString(2131820869) + " " + arrayList.get(i4).f2914c + " " + c0353a.f2888o + " (" + decimalFormat.format((Double.parseDouble(arrayList.get(i4).f2914c) / Double.parseDouble(arrayList.get(i4).f2912a)) * 100.0d) + "%)";
            }
            z4 = z5;
        } else {
            bVar = bVar2;
            textView = textView3;
            textView2 = textView5;
            if (z6) {
                String j5 = C0373f.j(Double.parseDouble(arrayList.get(i4).f2912a) - c0353a.f2877c, c0353a.f2889p);
                StringBuilder sb3 = new StringBuilder("1 ");
                sb3.append(context.getString(2131820868));
                sb3.append(" ");
                z4 = z5;
                sb3.append(C0373f.j(Double.parseDouble(arrayList.get(i4).f2912a), c0353a.f2889p));
                sb3.append(" ");
                sb3.append(c0353a.f2888o);
                sb3.append(" (");
                sb3.append(context.getString(2131820870));
                sb3.append(" ");
                sb3.append(j5);
                sb3.append(" ");
                c4 = C.b.c(sb3, c0353a.f2888o, ")");
                str = context.getString(2131820869) + " " + C0373f.j(Double.parseDouble(arrayList.get(i4).f2914c), c0353a.f2889p) + " " + c0353a.f2888o + " (" + decimalFormat.format((Double.parseDouble(arrayList.get(i4).f2914c) / Double.parseDouble(arrayList.get(i4).f2912a)) * 100.0d) + "%)";
            } else {
                z4 = z5;
                String g5 = C0373f.g(Double.parseDouble(arrayList.get(i4).f2912a) - c0353a.f2877c);
                StringBuilder sb4 = new StringBuilder("1 ");
                sb4.append(context.getString(2131820868));
                sb4.append(" ");
                sb4.append(arrayList.get(i4).f2912a);
                sb4.append(" ");
                sb4.append(c0353a.f2888o);
                sb4.append(" (");
                sb4.append(context.getString(2131820870));
                sb4.append(" ");
                sb4.append(g5);
                sb4.append(" ");
                c4 = C.b.c(sb4, c0353a.f2888o, ")");
                str = context.getString(2131820869) + " " + arrayList.get(i4).f2914c + " " + c0353a.f2888o + " (" + decimalFormat.format((Double.parseDouble(arrayList.get(i4).f2914c) / Double.parseDouble(arrayList.get(i4).f2912a)) * 100.0d) + "%)";
            }
        }
        textView.setText(c4);
        b1.f fVar = arrayList.get(i4).f2918h;
        List<b1.g> list = arrayList.get(i4).f2918h.f2919j;
        if (list != null) {
            for (int i5 = 0; i5 < list.size(); i5++) {
                int i6 = list.get(i5).f2920a;
                int i7 = list.get(i5).f2921b;
                fVar.setSpan(new StrikethroughSpan(), i6, i7, 33);
                fVar.setSpan(new BackgroundColorSpan(Color.rgb(255, 165, 0)), i6, i7, 33);
            }
        }
        arrayList.get(i4).f2918h = fVar;
        ImageSpan[] imageSpanArr = (ImageSpan[]) arrayList.get(i4).f2918h.getSpans(0, arrayList.get(i4).f2918h.length(), ImageSpan.class);
        b1.f fVar2 = new b1.f(arrayList.get(i4).f2918h);
        b1.f fVar3 = new b1.f(arrayList.get(i4).f2913b);
        m(fVar2, "typeLe", textView2.getTextSize());
        m(fVar2, "typeRi", textView2.getTextSize());
        m(fVar2, "typeBo", textView2.getTextSize());
        m(fVar2, "typeRe", textView2.getTextSize());
        m(fVar3, "typeLe", textView2.getTextSize());
        m(fVar3, "typeRi", textView2.getTextSize());
        m(fVar3, "typeBo", textView2.getTextSize());
        m(fVar3, "typeRe", textView2.getTextSize());
        arrayList.get(i4).f2918h = fVar2;
        ImageSpan[] imageSpanArr2 = (ImageSpan[]) arrayList.get(i4).f2918h.getSpans(0, arrayList.get(i4).f2918h.length(), ImageSpan.class);
        if (!z4) {
            fVar3 = arrayList.get(i4).f2918h;
        }
        TextView textView6 = textView2;
        textView6.setText(fVar3);
        b bVar3 = bVar;
        bVar3.f2333v.setText(str);
        if (z4) {
            textView6.setBackgroundColor(arrayList.get(i4).f);
        }
        ProgressBar progressBar = bVar3.f2334w;
        progressBar.setMax(100000);
        ObjectAnimator ofInt = ObjectAnimator.ofInt(progressBar, "progress", 0, ((int) (100.0d - ((Double.parseDouble(arrayList.get(i4).f2914c) * 100.0d) / Double.parseDouble(arrayList.get(i4).f2912a)))) * 1000);
        ofInt.setDuration(1000L);
        ofInt.setInterpolator(new DecelerateInterpolator());
        ofInt.start();
    }

    /* JADX WARN: Type inference failed for: r4v3, types: [T0.f$b, androidx.recyclerview.widget.RecyclerView$B] */
    /* JADX WARN: Type inference failed for: r4v6, types: [T0.f$a, androidx.recyclerview.widget.RecyclerView$B] */
    public final RecyclerView.B h(ViewGroup viewGroup, int i4) {
        if (i4 == 1) {
            View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(2131427363, viewGroup, false);
            ?? b4 = new RecyclerView.B(inflate);
            b4.f2330t = (FrameLayout) inflate.findViewById(2131230811);
            return b4;
        }
        View inflate2 = LayoutInflater.from(viewGroup.getContext()).inflate(2131427456, viewGroup, false);
        ?? b5 = new RecyclerView.B(inflate2);
        b5.f2331t = (TextView) inflate2.findViewById(2131231127);
        b5.f2332u = (TextView) inflate2.findViewById(2131231024);
        b5.f2333v = (TextView) inflate2.findViewById(2131231173);
        b5.f2334w = (ProgressBar) inflate2.findViewById(2131231165);
        b5.f2335x = inflate2.findViewById(2131230851);
        b5.f2336y = (TextView) inflate2.findViewById(2131231305);
        return b5;
    }

    public final void m(b1.f fVar, String str, float f) {
        int indexOf;
        Drawable drawable;
        Context context = this.f2324d;
        Resources resources = context.getResources();
        Resources.Theme theme = context.getTheme();
        ThreadLocal<TypedValue> threadLocal = D.f.f527a;
        Drawable a4 = f.a.a(resources, 2131165375, theme);
        Drawable a5 = f.a.a(context.getResources(), 2131165458, context.getTheme());
        Drawable a6 = f.a.a(context.getResources(), 2131165313, context.getTheme());
        Drawable a7 = f.a.a(context.getResources(), 2131165455, context.getTheme());
        int i4 = 0;
        while (i4 < fVar.length() && (indexOf = fVar.toString().indexOf(str, i4)) >= 0) {
            int length = str.length() + indexOf;
            char c4 = 65535;
            switch (str.hashCode()) {
                case -858803929:
                    if (str.equals("typeBo")) {
                        c4 = 0;
                        break;
                    }
                    break;
                case -858803629:
                    if (str.equals("typeLe")) {
                        c4 = 1;
                        break;
                    }
                    break;
                case -858803443:
                    if (str.equals("typeRe")) {
                        c4 = 2;
                        break;
                    }
                    break;
                case -858803439:
                    if (str.equals("typeRi")) {
                        c4 = 3;
                        break;
                    }
                    break;
            }
            switch (c4) {
                case 0:
                    drawable = a6;
                    break;
                case 1:
                    drawable = a4;
                    break;
                case 2:
                    drawable = a7;
                    break;
                case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                    drawable = a5;
                    break;
            }
            Objects.requireNonNull(drawable);
            drawable.setBounds(0, 0, 70, (int) (f / 1.75d));
            fVar.setSpan(new ImageSpan(drawable), indexOf, length, 33);
            i4 = length;
        }
    }
}
