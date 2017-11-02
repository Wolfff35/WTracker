package com.wolff.wtracker.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wolff.wtracker.R;
import com.wolff.wtracker.model.WCoord;
import com.wolff.wtracker.model.WUser;
import com.wolff.wtracker.online.OnlineDataLab;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by wolff on 05.10.2017.
 */

public class UITools {
    public void displayFragment(AppCompatActivity activity, Fragment currentFragment) {
        FragmentTransaction fragmentTransaction;
        FragmentManager fm = activity.getSupportFragmentManager();

        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.item_container, currentFragment);
        fragmentTransaction.commit();
    }

    public Polyline drawUserWay(Context context,GoogleMap map, WUser user, Date currentDate){
        Polyline line=null;
        ArrayList<Marker> points = new ArrayList<>();
        ArrayList<WCoord> userCoords = OnlineDataLab.get(context).getOnlineUsersCoordinates(user,currentDate);
        if(userCoords.size()>0){
            double minLat=180,minLon=180,maxLat=-180,maxLon=-180;
            PolylineOptions opt = new PolylineOptions();
            for(int i=0;i<userCoords.size();i++){
                 double currLat = userCoords.get(i).get_coord_lat();
                double currLon = userCoords.get(i).get_coord_lon();
                LatLng ll = new LatLng(currLat,currLon);
                if(i%1000==0){
                    //Bitmap bitmap = GetBitmapMarker(context, R.drawable.ic_marker3, ""+i);
                    Bitmap bitmap = textAsBitmap(""+i/1000,38,Color.BLACK);

                    Debug.Log("Point","= "+i);
                    MarkerOptions mo = new MarkerOptions();
                    mo.position(ll);
                    mo.title(""+i/1000);
                    mo.snippet(new DateFormatTools().dateToString(userCoords.get(i).get_date(),DateFormatTools.TIME_FORMAT_SHORT));
                    mo.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    Marker marker = map.addMarker(mo);
                    points.add(marker);

                }
                opt.add(ll);
                if(currLat<minLat) minLat=currLat;
                if(currLat>maxLat) maxLat=currLat;
                if(currLon<minLon) minLon=currLon;
                if(currLon>maxLon) maxLon=currLon;

            }
            opt.color(Color.MAGENTA);
            line = map.addPolyline(opt);
            LatLngBounds AREA = new LatLngBounds(new LatLng(minLat,minLon),new LatLng(maxLat,maxLon));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(AREA,0));
        }
        return line;
    }
    public Map<WUser,Marker> drawLastCoords(GoogleMap map,Map<WUser,WCoord>lastUserCoordinates){
        Map<WUser,Marker> mMarkers = new HashMap<>();
        for (Map.Entry<WUser, WCoord> entry : lastUserCoordinates.entrySet()) {
            WCoord coord = entry.getValue();
            WUser user = entry.getKey();
            LatLng ll = new LatLng(coord.get_coord_lat(),coord.get_coord_lon());
            if(user==null){
                return mMarkers;
            }
            if(mMarkers.get(user)!=null){
                mMarkers.get(user).setPosition(ll);
                mMarkers.get(user).setTitle(user.get_name()+" "+user.get_id_user());
                mMarkers.get(user).setSnippet("lat: "+coord.get_coord_lat()+";  lng: "+coord.get_coord_lon());
            }else {
                MarkerOptions mo = new MarkerOptions();
                mo.position(ll);
                mo.title(user.get_name()+" "+user.get_id_user());
                mo.snippet("lat: "+coord.get_coord_lat()+";  lng: "+coord.get_coord_lon());
                //mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                Marker marker = map.addMarker(mo);
                mMarkers.put(user,marker);
            }

            CameraPosition position = new CameraPosition(ll,10,0,0);
            map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        return mMarkers;
    }

    //==============================================================================================
   /*
   MapUtils mapUtils = new MapUtils(getApplicationContext());
        Bitmap bitmap = mapUtils.GetBitmapMarker(getApplicationContext(), R.drawable.marker_blue, "1");

        Marker marker = _googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
    */

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
   public Bitmap GetBitmapMarker(Context mContext, int resourceId, String mText)
    {
        try
        {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

            // set default bitmap config if none
            if(bitmapConfig == null)
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;

            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTextSize((int) (14 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = (bitmap.getHeight() + bounds.height())/2;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;

        }
        catch (Exception e)
        {
            return null;
        }
    }
}
