package com.cesde.storeapp_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Guarda los datos de sesión del usuario.
     *
     * @param accessToken Token de acceso para la autenticación.
     * @param username    Nombre del usuario.
     */
    public void saveUserSession(String accessToken, String username) {
        // Limpiar datos anteriores
        logout();
        // Guardar nuevos datos
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }


    /**
     * Limpia la sesión del usuario.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Verifica si el usuario está logueado.
     *
     * @return true si el usuario está logueado, false en caso contrario.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Devuelve el nombre del usuario logueado.
     *
     * @return Nombre del usuario, o una cadena vacía si no hay sesión activa.
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    /**
     * Devuelve el token de acceso del usuario logueado.
     *
     * @return Token de acceso, o null si no hay sesión activa.
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
}
