package com.uber.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.uber.sample.Adapters.FlickerAdapter;
import com.uber.sample.Http.AsyncRequest;
import com.uber.sample.Http.FlickerAPI;
import com.uber.sample.Listners.OnLoadMoreListener;
import com.uber.sample.Model.FlickerModel;
import com.uber.sample.Utils.GeneralUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncRequest.OnAsyncRequestComplete {

    private List<FlickerModel> listImages,listImagesLoadMore;
    private FlickerAdapter flickerAdapter;
    RecyclerView recyclerView;
    ImageView imageViewSearch;
    EditText searchView;
    static int count = 1;
    String searchLabel = "Kitten";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //find view by id and attaching adapter for the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
        searchView = (EditText) findViewById(R.id.searchView);
        listImagesLoadMore = new ArrayList<>();
        listImages = new ArrayList<>();
        getImagesFromFlicker();
        GeneralUtil.showSnackBar(searchView,"Loading images please wait...");

        // perform set on query text listener event
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=0;
                searchLabel = searchView.getText().toString().trim();
                if(searchLabel != null && searchLabel.length() > 0)
                   setLoadMoreFlicker(count,searchLabel);
                else
                    GeneralUtil.showSnackBar(searchView,"Please enter some label to search");

            }
        });

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

    }

    private void getImagesFromFlicker() {
        try {
            boolean checkInternetConnection = GeneralUtil.isOnline(getApplicationContext());
            if (checkInternetConnection) {
                AsyncRequest asyncRequest = new AsyncRequest(MainActivity.this, MainActivity.this, "GET",
                        FlickerAPI.FLICKER_GET_PHOTOS, "FLICKER_GET_PHOTOS");
                asyncRequest.execute();
            } else {
                GeneralUtil.showSnackBar(searchView,"No Internet connection! Please reconnect and try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void asyncResponse(String response, String flagType) {
        try{
            if(response!=null&&response.length()>0){
                switch(flagType){

                    case"FLICKER_GET_PHOTOS":
                        JSONObject jsonObject=new JSONObject(response);
                        JSONObject photosJsonObject = jsonObject.getJSONObject("photos");
                        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
                        setAdapterData(photoJsonArray,"ADD");
                        break;

                    case "FLICKER_LOAD_MORE_PHOTOS":
                        listImagesLoadMore.clear();
                        JSONObject jsonObjectLoadMore = new JSONObject(response);
                        JSONObject photosJsonObjectLoadMore = jsonObjectLoadMore.getJSONObject("photos");
                        JSONArray photoJsonArrayLoadMore = photosJsonObjectLoadMore.getJSONArray("photo");
                        setAdapterData(photoJsonArrayLoadMore,"LOAD_MORE");
                        break;

                    default:
                        break;

                }

            }else{
                GeneralUtil.showToast(getApplicationContext(),"Something is not quite right! Please try again later.");
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void setAdapterData(JSONArray photoJsonArray, String slug) {

        try {
            for (int i = 0 ; i < photoJsonArray.length() ; i++){
                FlickerModel model = new FlickerModel();
                JSONObject innerJsonObject = photoJsonArray.getJSONObject(i);
                model.setId(!innerJsonObject.isNull("id") ? innerJsonObject.getString("id"):"");
                model.setFarm(!innerJsonObject.isNull("farm") ? innerJsonObject.getString("farm"):"");
                model.setOwner(!innerJsonObject.isNull("owner") ? innerJsonObject.getString("owner"):"");
                model.setSecret(!innerJsonObject.isNull("secret") ? innerJsonObject.getString("secret"):"");
                model.setServer(!innerJsonObject.isNull("server") ? innerJsonObject.getString("server"):"");
                model.setTitle(!innerJsonObject.isNull("title") ? innerJsonObject.getString("title"):"");
                model.setImageUrl(setImageURL(model.getFarm(),model.getServer(),model.getId(),model.getSecret()));

                if(slug.equals("ADD"))
                    listImages.add(model);
                else
                    listImagesLoadMore.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (slug){

            case "ADD":
                if(listImages != null && listImages.size() > 0){
                    flickerAdapter = new FlickerAdapter(recyclerView, listImages, this);
                    recyclerView.setAdapter(flickerAdapter);

                    //set load more listener for the RecyclerView adapter
                    if(flickerAdapter != null){
                        flickerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                System.out.println("Load more===========================>");
                                setLoadMoreFlicker(count++,searchLabel);
                            }
                        });
                    }
                }
                break;

            case "LOAD_MORE":
                if(listImagesLoadMore != null){
                    listImages.addAll(listImagesLoadMore);
                    flickerAdapter.notifyDataSetChanged();
                    flickerAdapter.loadData();
                    flickerAdapter.setLoaded();
                }
                break;

        }
    }

    private void setLoadMoreFlicker(int i,String searchLabel) {
        try {
            String setLoadMoreUrl = FlickerAPI.FLICKER_LOAD_MORE_PHOTOS+i+"&text="+searchLabel;
            boolean checkInternetConnection = GeneralUtil.isOnline(getApplicationContext());
            if (checkInternetConnection) {
                AsyncRequest asyncRequest = new AsyncRequest(MainActivity.this, MainActivity.this, "GET",
                        setLoadMoreUrl, "FLICKER_LOAD_MORE_PHOTOS");
                asyncRequest.execute();
            } else {
                GeneralUtil.showSnackBar(searchView,"No Internet connection! Please try again");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String setImageURL(String farmID, String serverID, String id, String secretID) {
        String flickerImageURL = "";
        flickerImageURL = "http://farm"+farmID+".static.flickr.com/"+serverID+"/"+id+"_"+secretID+".jpg";
        return flickerImageURL;
    }
}