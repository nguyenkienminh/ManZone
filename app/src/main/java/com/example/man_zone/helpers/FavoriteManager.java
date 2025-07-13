package com.example.man_zone.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.man_zone.Model.ProductModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static FavoriteManager instance;
    private final SharedPreferences sharedPreferences;
    private final String PREF_NAME = "FavoritePrefs";
    private final String KEY_FAVORITES = "favorites";
    private List<ProductModel> favoriteList;
    private final Gson gson;

    private FavoriteManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadFavorites();
    }

    public static synchronized FavoriteManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadFavorites() {
        String favoritesJson = sharedPreferences.getString(KEY_FAVORITES, "");
        if (!favoritesJson.isEmpty()) {
            Type type = new TypeToken<List<ProductModel>>() {}.getType();
            favoriteList = gson.fromJson(favoritesJson, type);
        } else {
            favoriteList = new ArrayList<>();
        }
    }

    private void saveFavorites() {
        String favoritesJson = gson.toJson(favoriteList);
        sharedPreferences.edit().putString(KEY_FAVORITES, favoritesJson).apply();
    }

    public void addToFavorites(ProductModel product) {
        if (!isInFavorites(product)) {
            favoriteList.add(product);
            saveFavorites();
        }
    }

    public void removeFromFavorites(ProductModel product) {
        favoriteList.removeIf(p -> p.getName().equals(product.getName()));
        saveFavorites();
    }

    public boolean isInFavorites(ProductModel product) {
        return favoriteList.stream()
                .anyMatch(p -> p.getName().equals(product.getName()));
    }

    public List<ProductModel> getFavoriteList() {
        return new ArrayList<>(favoriteList);
    }

    public void clearFavorites() {
        favoriteList.clear();
        saveFavorites();
    }
}
