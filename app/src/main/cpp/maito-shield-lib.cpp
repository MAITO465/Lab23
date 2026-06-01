#include <jni.h>
#include <string>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <android/log.h>
#include <sys/ptrace.h>
#include <unistd.h>

#define LOG_TAG "MAITO_SHIELD"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// --------------------------------------------------
// Contrôle 1 : tentative de détection de traçage (ptrace)
// --------------------------------------------------
static bool isBeingTraced() {
    // Si on peut pas se tracer soi-même, c'est qu'un débogueur est déjà attaché.
    long result = ptrace(PTRACE_TRACEME, 0, 0, 0);
    if (result == -1) {
        LOGE("⚠️ ETAT SUSPECT : Trace/Debug détecté (ptrace échoué)");
        return true;
    }
    LOGI("✅ Aucun Trace/Debug détecté via ptrace");
    return false;
}

// --------------------------------------------------
// Contrôle 2 : recherche de signatures dans /proc/self/maps
// --------------------------------------------------
static bool containsSuspiciousLibraryNames() {
    FILE* maps = fopen("/proc/self/maps", "r");
    if (!maps) {
        LOGW("Impossible d'ouvrir /proc/self/maps");
        return false;
    }

    char line[512];
    while (fgets(line, sizeof(line), maps)) {
        // Mots-clés liés à l'instrumentation dynamique
        if (strstr(line, "frida") ||
            strstr(line, "xposed") ||
            strstr(line, "libfrida") ||
            strstr(line, "gdbserver") ||
            strstr(line, "libgdb") ||
            strstr(line, "magisk")) {
            LOGE("🚨 SIGNATURE SUSPECTE TROUVEE DANS MAPS : %s", line);
            fclose(maps);
            return true;
        }
    }

    fclose(maps);
    LOGI("✅ Aucune signature suspecte trouvée dans /proc/self/maps");
    return false;
}

// --------------------------------------------------
// Contrôle global appelé depuis Java
// Retourne un entier (Variante B du lab)
// 0 = OK, 1 = ptrace, 2 = maps, 3 = les deux
// --------------------------------------------------
extern "C"
JNIEXPORT jint JNICALL
Java_com_maito_lab23antidebug_NativeSecurityManager_getSecurityStatusNative(JNIEnv* env, jobject /* this */) {
    bool traced = isBeingTraced();
    bool suspiciousMaps = containsSuspiciousLibraryNames();

    int status = 0;
    if (traced) status += 1;
    if (suspiciousMaps) status += 2;

    if (status > 0) {
        LOGE("❌ ETAT DE SECURITE : COMPROMIS (Code %d)", status);
    } else {
        LOGI("🛡️ ETAT DE SECURITE : OK");
    }

    return status;
}

// --------------------------------------------------
// Fonctions JNI simulées du précédent lab
// --------------------------------------------------
extern "C"
JNIEXPORT jstring JNICALL
Java_com_maito_lab23antidebug_NativeSecurityManager_helloFromJNI(JNIEnv* env, jobject /* this */) {
    return env->NewStringUTF("Hello from C++ (Sécurisé) !");
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_maito_lab23antidebug_NativeSecurityManager_factorial(JNIEnv* env, jobject /* this */, jint n) {
    if (n < 0) return -1;
    long long fact = 1;
    for (int i = 1; i <= n; i++) fact *= i;
    return static_cast<jint>(fact);
}
