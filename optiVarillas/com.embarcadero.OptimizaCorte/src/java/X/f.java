package X;

import C1.C0149c;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.SparseArray;
import android.widget.TextView;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class f {

    /* renamed from: a  reason: collision with root package name */
    public final b f2794a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends b {

        /* renamed from: a  reason: collision with root package name */
        public final TextView f2795a;

        /* renamed from: b  reason: collision with root package name */
        public final d f2796b;

        /* renamed from: c  reason: collision with root package name */
        public boolean f2797c = true;

        public a(TextView textView) {
            this.f2795a = textView;
            this.f2796b = new d(textView);
        }

        @Override // X.f.b
        public final InputFilter[] a(InputFilter[] inputFilterArr) {
            if (!this.f2797c) {
                SparseArray sparseArray = new SparseArray(1);
                for (int i4 = 0; i4 < inputFilterArr.length; i4++) {
                    InputFilter inputFilter = inputFilterArr[i4];
                    if (inputFilter instanceof d) {
                        sparseArray.put(i4, inputFilter);
                    }
                }
                if (sparseArray.size() != 0) {
                    int length = inputFilterArr.length;
                    InputFilter[] inputFilterArr2 = new InputFilter[inputFilterArr.length - sparseArray.size()];
                    int i5 = 0;
                    for (int i6 = 0; i6 < length; i6++) {
                        if (sparseArray.indexOfKey(i6) < 0) {
                            inputFilterArr2[i5] = inputFilterArr[i6];
                            i5++;
                        }
                    }
                    return inputFilterArr2;
                }
                return inputFilterArr;
            }
            int length2 = inputFilterArr.length;
            int i7 = 0;
            while (true) {
                d dVar = this.f2796b;
                if (i7 < length2) {
                    if (inputFilterArr[i7] != dVar) {
                        i7++;
                    } else {
                        return inputFilterArr;
                    }
                } else {
                    InputFilter[] inputFilterArr3 = new InputFilter[inputFilterArr.length + 1];
                    System.arraycopy(inputFilterArr, 0, inputFilterArr3, 0, length2);
                    inputFilterArr3[length2] = dVar;
                    return inputFilterArr3;
                }
            }
        }

        @Override // X.f.b
        public final boolean b() {
            return this.f2797c;
        }

        @Override // X.f.b
        public final void c(boolean z4) {
            if (z4) {
                TextView textView = this.f2795a;
                textView.setTransformationMethod(e(textView.getTransformationMethod()));
            }
        }

        @Override // X.f.b
        public final void d(boolean z4) {
            this.f2797c = z4;
            TextView textView = this.f2795a;
            textView.setTransformationMethod(e(textView.getTransformationMethod()));
            textView.setFilters(a(textView.getFilters()));
        }

        @Override // X.f.b
        public final TransformationMethod e(TransformationMethod transformationMethod) {
            if (this.f2797c) {
                if (!(transformationMethod instanceof h) && !(transformationMethod instanceof PasswordTransformationMethod)) {
                    return new h(transformationMethod);
                }
                return transformationMethod;
            } else if (transformationMethod instanceof h) {
                return ((h) transformationMethod).f2804a;
            } else {
                return transformationMethod;
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {
        public InputFilter[] a(InputFilter[] inputFilterArr) {
            throw null;
        }

        public boolean b() {
            throw null;
        }

        public void c(boolean z4) {
            throw null;
        }

        public void d(boolean z4) {
            throw null;
        }

        public TransformationMethod e(TransformationMethod transformationMethod) {
            throw null;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends b {

        /* renamed from: a  reason: collision with root package name */
        public final a f2798a;

        public c(TextView textView) {
            this.f2798a = new a(textView);
        }

        @Override // X.f.b
        public final InputFilter[] a(InputFilter[] inputFilterArr) {
            boolean z4;
            if (androidx.emoji2.text.f.k != null) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (!z4) {
                return inputFilterArr;
            }
            return this.f2798a.a(inputFilterArr);
        }

        @Override // X.f.b
        public final boolean b() {
            return this.f2798a.f2797c;
        }

        @Override // X.f.b
        public final void c(boolean z4) {
            boolean z5;
            if (androidx.emoji2.text.f.k != null) {
                z5 = true;
            } else {
                z5 = false;
            }
            if (!z5) {
                return;
            }
            this.f2798a.c(z4);
        }

        @Override // X.f.b
        public final void d(boolean z4) {
            boolean z5;
            if (androidx.emoji2.text.f.k != null) {
                z5 = true;
            } else {
                z5 = false;
            }
            boolean z6 = !z5;
            a aVar = this.f2798a;
            if (z6) {
                aVar.f2797c = z4;
            } else {
                aVar.d(z4);
            }
        }

        @Override // X.f.b
        public final TransformationMethod e(TransformationMethod transformationMethod) {
            boolean z4;
            if (androidx.emoji2.text.f.k != null) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (!z4) {
                return transformationMethod;
            }
            return this.f2798a.e(transformationMethod);
        }
    }

    public f(TextView textView) {
        C0149c.e(textView, "textView cannot be null");
        this.f2794a = new c(textView);
    }
}
