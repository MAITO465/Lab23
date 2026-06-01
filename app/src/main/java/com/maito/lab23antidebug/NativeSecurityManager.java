package com.maito.lab23antidebug;

public class NativeSecurityManager {

    static {
        System.loadLibrary("maito-shield-lib");
    }

    /**
     * Retourne le statut de sécurité du processus courant calculé en C++ natif.
     * 0 = OK (Aucune menace)
     * 1 = Débogueur attaché détecté (ptrace)
     * 2 = Librairie suspecte (Frida/Xposed/Magisk) détectée via /proc/self/maps
     * 3 = Les deux (1 + 2)
     */
    public native int getSecurityStatusNative();

    // Fonctions métiers sensibles
    public native String helloFromJNI();
    public native int factorial(int n);
}
