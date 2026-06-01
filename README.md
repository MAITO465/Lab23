# Lab23 - JNI & Native Anti-Debug Protection 🛡️

Bienvenue dans le **Lab23** ! Ce projet prolonge les concepts JNI (Java Native Interface) en y ajoutant une couche de sécurité défensive bas niveau (C++).

## 📸 Aperçu du Dashboard de Sécurité

![Screenshot Lab23 Shield](screenshot.png)

## 🎯 Objectif du Laboratoire

Les applications sensibles (banques, streaming DRM, jeux multijoueurs) intègrent souvent leurs contrôles de sécurité directement dans le code natif (C/C++). Pourquoi ?
Parce qu'un fichier binaire compilé (`.so`) est beaucoup plus difficile à décompiler, comprendre et falsifier (Reverse Engineering) qu'un simple bytecode Java/Kotlin (Dalvik/ART).

Ce laboratoire montre comment détecter qu'un "Hacker" ou qu'un script malveillant tente d'analyser le comportement de votre application.

## ⚙️ Mécanismes de Défense Implémentés

Le code C++ (`maito-shield-lib.cpp`) exécute deux contrôles critiques :

1. **Détection de Traçage (`ptrace`)**
   - L'application tente de s'attacher à elle-même via l'appel système Linux `ptrace(PTRACE_TRACEME)`. 
   - Sous Linux/Android, un processus ne peut avoir qu'un seul traceur. Si cet appel échoue, cela signifie qu'un outil de débogage (comme GDB, LLDB ou IDA Pro) est *déjà* attaché à l'application.

2. **Inspection de la Mémoire (`/proc/self/maps`)**
   - Le système de fichiers `/proc` sous Linux permet de lire l'état du processus. Le fichier `maps` liste toutes les bibliothèques chargées en mémoire.
   - L'algorithme scanne ce fichier à la recherche de "signatures textuelles" appartenant à des outils d'instrumentation dynamique bien connus des hackers : **Frida**, **Xposed**, **Magisk**, etc.

## 🏗️ Architecture "Clean Code"

Plutôt que de renvoyer un simple booléen (Vrai/Faux) en vrac, l'architecture a été pensée selon les standards professionnels :
- **Wrapper Java** : Une classe `NativeSecurityManager` s'occupe de charger la librairie `.so` et de déclarer les ponts JNI. L'Activity principale (l'UI) reste ainsi très propre.
- **Code d'État** : Le C++ renvoie un code détaillé (0, 1, 2 ou 3).
- **Politique de Réaction** : Si une compromission est détectée, l'application ne "crashe" pas brutalement. Elle affiche un avertissement visuel (💀) et verrouille logiquement l'accès aux méthodes JNI sensibles.

---
*Développé pour la démonstration des sécurités natives sous Android (NDK/JNI).*
