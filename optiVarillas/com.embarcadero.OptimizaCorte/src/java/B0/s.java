package B0;

import B0.a;
import android.content.pm.PackageInfo;
import android.os.Build;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class s {

    /* renamed from: a  reason: collision with root package name */
    public static final a.d f289a;

    /* renamed from: b  reason: collision with root package name */
    public static final a.d f290b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends a.i {

        /* renamed from: d  reason: collision with root package name */
        public final Pattern f291d;

        public a() {
            super("ALGORITHMIC_DARKENING", "ALGORITHMIC_DARKENING");
            this.f291d = Pattern.compile("\\A\\d+");
        }

        @Override // B0.a
        public final boolean d() {
            int i4;
            PackageInfo packageInfo;
            boolean d4 = super.d();
            if (d4 && (i4 = Build.VERSION.SDK_INT) < 29) {
                int i5 = A0.b.f5a;
                if (i4 >= 26) {
                    packageInfo = g.a();
                } else {
                    try {
                        packageInfo = A0.b.a();
                    } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
                        packageInfo = null;
                    }
                }
                if (packageInfo == null) {
                    return false;
                }
                Matcher matcher = this.f291d.matcher(packageInfo.versionName);
                if (!matcher.find() || Integer.parseInt(packageInfo.versionName.substring(matcher.start(), matcher.end())) < 105) {
                    return false;
                }
                return true;
            }
            return d4;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class b extends a.d {
        @Override // B0.a
        public final boolean d() {
            if (!super.d() || !A0.c.e("MULTI_PROCESS")) {
                return false;
            }
            return A0.b.b();
        }
    }

    /* JADX WARN: Type inference failed for: r0v43, types: [B0.a$d, B0.a] */
    /* JADX WARN: Type inference failed for: r0v57, types: [B0.a$d, B0.a] */
    static {
        new B0.a("VISUAL_STATE_CALLBACK", "VISUAL_STATE_CALLBACK");
        new B0.a("OFF_SCREEN_PRERASTER", "OFF_SCREEN_PRERASTER");
        new B0.a("SAFE_BROWSING_ENABLE", "SAFE_BROWSING_ENABLE");
        new B0.a("DISABLED_ACTION_MODE_MENU_ITEMS", "DISABLED_ACTION_MODE_MENU_ITEMS");
        new B0.a("START_SAFE_BROWSING", "START_SAFE_BROWSING");
        new B0.a("SAFE_BROWSING_WHITELIST", "SAFE_BROWSING_WHITELIST");
        new B0.a("SAFE_BROWSING_WHITELIST", "SAFE_BROWSING_ALLOWLIST");
        new B0.a("SAFE_BROWSING_ALLOWLIST", "SAFE_BROWSING_WHITELIST");
        new B0.a("SAFE_BROWSING_ALLOWLIST", "SAFE_BROWSING_ALLOWLIST");
        new B0.a("SAFE_BROWSING_PRIVACY_POLICY_URL", "SAFE_BROWSING_PRIVACY_POLICY_URL");
        new B0.a("SERVICE_WORKER_BASIC_USAGE", "SERVICE_WORKER_BASIC_USAGE");
        new B0.a("SERVICE_WORKER_CACHE_MODE", "SERVICE_WORKER_CACHE_MODE");
        new B0.a("SERVICE_WORKER_CONTENT_ACCESS", "SERVICE_WORKER_CONTENT_ACCESS");
        new B0.a("SERVICE_WORKER_FILE_ACCESS", "SERVICE_WORKER_FILE_ACCESS");
        new B0.a("SERVICE_WORKER_BLOCK_NETWORK_LOADS", "SERVICE_WORKER_BLOCK_NETWORK_LOADS");
        new B0.a("SERVICE_WORKER_SHOULD_INTERCEPT_REQUEST", "SERVICE_WORKER_SHOULD_INTERCEPT_REQUEST");
        new B0.a("RECEIVE_WEB_RESOURCE_ERROR", "RECEIVE_WEB_RESOURCE_ERROR");
        new B0.a("RECEIVE_HTTP_ERROR", "RECEIVE_HTTP_ERROR");
        new B0.a("SHOULD_OVERRIDE_WITH_REDIRECTS", "SHOULD_OVERRIDE_WITH_REDIRECTS");
        new B0.a("SAFE_BROWSING_HIT", "SAFE_BROWSING_HIT");
        new B0.a("WEB_RESOURCE_REQUEST_IS_REDIRECT", "WEB_RESOURCE_REQUEST_IS_REDIRECT");
        new B0.a("WEB_RESOURCE_ERROR_GET_DESCRIPTION", "WEB_RESOURCE_ERROR_GET_DESCRIPTION");
        new B0.a("WEB_RESOURCE_ERROR_GET_CODE", "WEB_RESOURCE_ERROR_GET_CODE");
        new B0.a("SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY", "SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY");
        new B0.a("SAFE_BROWSING_RESPONSE_PROCEED", "SAFE_BROWSING_RESPONSE_PROCEED");
        new B0.a("SAFE_BROWSING_RESPONSE_SHOW_INTERSTITIAL", "SAFE_BROWSING_RESPONSE_SHOW_INTERSTITIAL");
        new B0.a("WEB_MESSAGE_PORT_POST_MESSAGE", "WEB_MESSAGE_PORT_POST_MESSAGE");
        new B0.a("WEB_MESSAGE_PORT_CLOSE", "WEB_MESSAGE_PORT_CLOSE");
        new B0.a("WEB_MESSAGE_ARRAY_BUFFER", "WEB_MESSAGE_ARRAY_BUFFER");
        new B0.a("WEB_MESSAGE_PORT_SET_MESSAGE_CALLBACK", "WEB_MESSAGE_PORT_SET_MESSAGE_CALLBACK");
        new B0.a("CREATE_WEB_MESSAGE_CHANNEL", "CREATE_WEB_MESSAGE_CHANNEL");
        new B0.a("POST_WEB_MESSAGE", "POST_WEB_MESSAGE");
        new B0.a("WEB_MESSAGE_CALLBACK_ON_MESSAGE", "WEB_MESSAGE_CALLBACK_ON_MESSAGE");
        new B0.a("GET_WEB_VIEW_CLIENT", "GET_WEB_VIEW_CLIENT");
        new B0.a("GET_WEB_CHROME_CLIENT", "GET_WEB_CHROME_CLIENT");
        new B0.a("GET_WEB_VIEW_RENDERER", "GET_WEB_VIEW_RENDERER");
        new B0.a("WEB_VIEW_RENDERER_TERMINATE", "WEB_VIEW_RENDERER_TERMINATE");
        new B0.a("TRACING_CONTROLLER_BASIC_USAGE", "TRACING_CONTROLLER_BASIC_USAGE");
        new r();
        new r();
        new B0.a("WEB_VIEW_RENDERER_CLIENT_BASIC_USAGE", "WEB_VIEW_RENDERER_CLIENT_BASIC_USAGE");
        new a();
        new B0.a("PROXY_OVERRIDE", "PROXY_OVERRIDE:3");
        f289a = new B0.a("MULTI_PROCESS", "MULTI_PROCESS_QUERY");
        new B0.a("FORCE_DARK", "FORCE_DARK");
        new B0.a("FORCE_DARK_STRATEGY", "FORCE_DARK_BEHAVIOR");
        new B0.a("WEB_MESSAGE_LISTENER", "WEB_MESSAGE_LISTENER");
        new B0.a("DOCUMENT_START_SCRIPT", "DOCUMENT_START_SCRIPT:1");
        new B0.a("PROXY_OVERRIDE_REVERSE_BYPASS", "PROXY_OVERRIDE_REVERSE_BYPASS");
        new B0.a("GET_VARIATIONS_HEADER", "GET_VARIATIONS_HEADER");
        new B0.a("ENTERPRISE_AUTHENTICATION_APP_LINK_POLICY", "ENTERPRISE_AUTHENTICATION_APP_LINK_POLICY");
        new B0.a("GET_COOKIE_INFO", "GET_COOKIE_INFO");
        new B0.a("REQUESTED_WITH_HEADER_ALLOW_LIST", "REQUESTED_WITH_HEADER_ALLOW_LIST");
        new B0.a("USER_AGENT_METADATA", "USER_AGENT_METADATA");
        new B0.a("MULTI_PROFILE", "MULTI_PROFILE");
        new B0.a("ATTRIBUTION_REGISTRATION_BEHAVIOR", "ATTRIBUTION_BEHAVIOR");
        new B0.a("WEBVIEW_MEDIA_INTEGRITY_API_STATUS", "WEBVIEW_INTEGRITY_API_STATUS");
        f290b = new B0.a("MUTE_AUDIO", "MUTE_AUDIO");
    }
}
