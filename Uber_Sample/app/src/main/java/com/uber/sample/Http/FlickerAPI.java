package com.uber.sample.Http;

public class FlickerAPI {
    public static final String FLICKER_BASE_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=2&safe_search=1";
    public static final String FLICKER_GET_PHOTOS = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=2&safe_search=1&text=kittens&page=3";
    public static final String FLICKER_LOAD_MORE_PHOTOS = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=2&safe_search=1&page=";
}
