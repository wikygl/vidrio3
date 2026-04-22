package R0;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import e.t;
import l3.g;
import u3.q;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a extends t implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    /* renamed from: A  reason: collision with root package name */
    public ImageView f2038A;

    /* renamed from: o  reason: collision with root package name */
    public final C0021a f2039o;

    /* renamed from: p  reason: collision with root package name */
    public final SharedPreferences f2040p;

    /* renamed from: q  reason: collision with root package name */
    public final int f2041q;

    /* renamed from: r  reason: collision with root package name */
    public final int f2042r;

    /* renamed from: s  reason: collision with root package name */
    public TextView f2043s;

    /* renamed from: t  reason: collision with root package name */
    public TextView f2044t;

    /* renamed from: u  reason: collision with root package name */
    public TextView f2045u;

    /* renamed from: v  reason: collision with root package name */
    public TextView f2046v;

    /* renamed from: w  reason: collision with root package name */
    public TextView f2047w;

    /* renamed from: x  reason: collision with root package name */
    public TextView f2048x;

    /* renamed from: y  reason: collision with root package name */
    public EditText f2049y;

    /* renamed from: z  reason: collision with root package name */
    public RatingBar f2050z;

    /* renamed from: R0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class C0021a {

        /* renamed from: a  reason: collision with root package name */
        public String f2051a;

        /* renamed from: b  reason: collision with root package name */
        public String f2052b;

        /* renamed from: c  reason: collision with root package name */
        public int f2053c;

        /* renamed from: d  reason: collision with root package name */
        public q<? super a, ? super Float, ? super Boolean, g> f2054d;

        /* renamed from: e  reason: collision with root package name */
        public q<? super a, ? super Float, ? super Boolean, g> f2055e;
        public int f;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public a(Context context, C0021a c0021a) {
        super(context, 0);
        h.e(context, "context");
        this.f2039o = c0021a;
        SharedPreferences sharedPreferences = context.getSharedPreferences("RatingDialog", 0);
        h.d(sharedPreferences, "context.getSharedPrefere…fs, Context.MODE_PRIVATE)");
        this.f2040p = sharedPreferences;
        this.f2041q = 3;
        this.f2042r = c0021a.f;
    }

    public static void u(a aVar) {
        SharedPreferences.Editor edit = aVar.f2040p.edit();
        edit.putBoolean("show_never", true);
        edit.apply();
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        Editable text;
        int i4;
        boolean z4;
        h.e(view, "view");
        int id = view.getId();
        C0021a c0021a = this.f2039o;
        if (id == 2131230902) {
            u(this);
            c0021a.getClass();
            dismiss();
        } else if (id == 2131230903) {
            c0021a.getClass();
            dismiss();
        } else if (id == 2131230901) {
            u(this);
            EditText editText = this.f2049y;
            if (editText == null) {
                text = null;
            } else {
                text = editText.getText();
            }
            String valueOf = String.valueOf(text);
            int length = valueOf.length() - 1;
            int i5 = 0;
            boolean z5 = false;
            while (i5 <= length) {
                if (!z5) {
                    i4 = i5;
                } else {
                    i4 = length;
                }
                char charAt = valueOf.charAt(i4);
                if (charAt < ' ' || charAt == ' ') {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (!z5) {
                    if (!z4) {
                        z5 = true;
                    } else {
                        i5++;
                    }
                } else if (!z4) {
                    break;
                } else {
                    length--;
                }
            }
            if (TextUtils.isEmpty(valueOf.subSequence(i5, length + 1).toString())) {
                EditText editText2 = this.f2049y;
                if (editText2 != null) {
                    editText2.startAnimation(AnimationUtils.loadAnimation(editText2.getContext(), 2130772023));
                    return;
                }
                return;
            }
            c0021a.getClass();
            dismiss();
        } else if (id == 2131230900) {
            dismiss();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // e.t
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        Window window = getWindow();
        int i4 = 0;
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
        }
        setContentView(2131427386);
        this.f2043s = (TextView) findViewById(2131230908);
        this.f2045u = (TextView) findViewById(2131230902);
        this.f2044t = (TextView) findViewById(2131230903);
        this.f2046v = (TextView) findViewById(2131230905);
        this.f2047w = (TextView) findViewById(2131230901);
        this.f2048x = (TextView) findViewById(2131230900);
        this.f2050z = (RatingBar) findViewById(2131230907);
        this.f2038A = (ImageView) findViewById(2131230906);
        this.f2049y = (EditText) findViewById(2131230904);
        TextView textView = this.f2043s;
        C0021a c0021a = this.f2039o;
        if (textView != null) {
            String str = c0021a.f2051a;
            if (str == null) {
                str = getContext().getString(2131820888);
            }
            textView.setText(str);
        }
        TextView textView2 = this.f2046v;
        if (textView2 != null) {
            c0021a.getClass();
            textView2.setText(getContext().getString(2131820889));
        }
        EditText editText = this.f2049y;
        if (editText != null) {
            c0021a.getClass();
            editText.setHint(getContext().getString(2131820893));
        }
        TextView textView3 = this.f2045u;
        if (textView3 != null) {
            textView3.setOnClickListener(this);
            c0021a.getClass();
            textView3.setText(textView3.getContext().getString(2131820891));
            if (this.f2042r == 1) {
                i4 = 8;
            }
            textView3.setVisibility(i4);
        }
        TextView textView4 = this.f2044t;
        if (textView4 != null) {
            textView4.setOnClickListener(this);
            c0021a.getClass();
            textView4.setText(textView4.getContext().getString(2131820890));
        }
        TextView textView5 = this.f2047w;
        if (textView5 != null) {
            textView5.setOnClickListener(this);
            c0021a.getClass();
            textView5.setText(textView5.getContext().getString(2131820892));
        }
        TextView textView6 = this.f2048x;
        if (textView6 != null) {
            textView6.setOnClickListener(this);
            c0021a.getClass();
            textView6.setText(textView6.getContext().getString(2131820887));
        }
        RatingBar ratingBar = this.f2050z;
        if (ratingBar != null) {
            ratingBar.setOnRatingBarChangeListener(this);
            c0021a.getClass();
        }
        ImageView imageView = this.f2038A;
        if (imageView != null) {
            c0021a.getClass();
            Drawable applicationIcon = imageView.getContext().getPackageManager().getApplicationIcon(imageView.getContext().getApplicationInfo());
            h.d(applicationIcon, "context.packageManager.g…(context.applicationInfo)");
            imageView.setImageDrawable(applicationIcon);
        }
        if (c0021a.f2054d == null) {
            c0021a.f2054d = new b(this);
        }
        if (c0021a.f2055e == null) {
            c0021a.f2055e = new c(this);
        }
        int i5 = c0021a.f2053c;
        if (i5 != 0) {
            TextView textView7 = this.f2043s;
            if (textView7 != null) {
                textView7.setTextColor(i5);
            }
            TextView textView8 = this.f2046v;
            if (textView8 != null) {
                textView8.setTextColor(c0021a.f2053c);
            }
        }
    }

    @Override // android.widget.RatingBar.OnRatingBarChangeListener
    public final void onRatingChanged(RatingBar ratingBar, float f, boolean z4) {
        h.e(ratingBar, "ratingBar");
        float rating = ratingBar.getRating();
        float f4 = this.f2041q;
        C0021a c0021a = this.f2039o;
        boolean z5 = true;
        if (rating >= f4) {
            u(this);
            q<? super a, ? super Float, ? super Boolean, g> qVar = c0021a.f2054d;
            if (qVar != null) {
                Float valueOf = Float.valueOf(ratingBar.getRating());
                if (ratingBar.getRating() < f4) {
                    z5 = false;
                }
                qVar.c(this, valueOf, Boolean.valueOf(z5));
            }
        } else {
            q<? super a, ? super Float, ? super Boolean, g> qVar2 = c0021a.f2055e;
            if (qVar2 != null) {
                Float valueOf2 = Float.valueOf(ratingBar.getRating());
                if (ratingBar.getRating() < f4) {
                    z5 = false;
                }
                qVar2.c(this, valueOf2, Boolean.valueOf(z5));
            }
        }
        c0021a.getClass();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void show() {
        int i4 = this.f2042r;
        if (i4 != 1) {
            SharedPreferences sharedPreferences = this.f2040p;
            if (!sharedPreferences.getBoolean("show_never", false)) {
                int i5 = sharedPreferences.getInt("session_count", 1);
                if (i4 == i5) {
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("session_count", 1);
                    edit.apply();
                } else if (i4 > i5) {
                    this.f2039o.getClass();
                    SharedPreferences.Editor edit2 = sharedPreferences.edit();
                    edit2.putInt("session_count", i5 + 1);
                    edit2.apply();
                    return;
                } else {
                    SharedPreferences.Editor edit3 = sharedPreferences.edit();
                    edit3.putInt("session_count", i5);
                    edit3.apply();
                    return;
                }
            } else {
                return;
            }
        }
        super/*android.app.Dialog*/.show();
    }
}
