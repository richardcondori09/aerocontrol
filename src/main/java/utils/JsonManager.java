package utils;

import com.google.gson.Gson;

public class JsonManager {
    private static final Gson gson = new Gson();

    public static Gson getInstance() {
        return gson;
    }
}