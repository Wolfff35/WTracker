package com.wolff.wtracker.tools;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wolff on 03.10.2017.
 */

public class _GeoJsonTools {
 /*   public void a(Context context, GoogleMap map){
        //GeoJsonLayer layer =new GeoJsonLayer(map,null);
        GeoJsonLayer layer = null;
        try {
            String s = "";
            JSONObject jo = new JSONObject(s);
            layer = new GeoJsonLayer(map, jo);
        } catch (JSONException e) {
            Debug.Log("GEO","ERROR 2 "+e.getLocalizedMessage());
        }
        GeoJsonPoint point = new GeoJsonPoint(new LatLng(0,0));
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Ocean", "South Atlantic");

        GeoJsonFeature pointFeature = new GeoJsonFeature(point, "Origin", properties, null);
        /*for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature.hasProperty(“Ocean”)) {
                String oceanProperty = feature.getProperty(“Ocean”);
            }
        }
        layer.addFeature(pointFeature);

        ArrayList<LatLng> lineStringArray = new ArrayList<>();
        lineStringArray.add(new LatLng(0, 0));
        lineStringArray.add(new LatLng(50, 50));
        lineStringArray.add(new LatLng(52, 53));
        GeoJsonLineString lineString = new GeoJsonLineString(lineStringArray);
        GeoJsonFeature lineStringFeature = new GeoJsonFeature(lineString, null, null, null);

// Set the color of the linestring to red
        GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
        lineStringStyle.setColor(Color.RED);

// Set the style of the feature
        lineStringFeature.setLineStringStyle(lineStringStyle);
    layer.addFeature(lineStringFeature);
    }
*/
}
