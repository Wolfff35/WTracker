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
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

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

    //   public Polyline drawUserWay(Context context, GoogleMap map, WUser user, Date currentDate) {
    public Polyline drawUserWay(Context context, GoogleMap map, ArrayList<WCoord> userCoords) {
        Polyline line = null;
        if (userCoords.size() > 0) {
            double minLat = 180, minLon = 180, maxLat = -180, maxLon = -180;
            PolylineOptions opt = new PolylineOptions();
            for (int i = 0; i < userCoords.size(); i++) {
                double currLat = userCoords.get(i).get_coord_lat();
                double currLon = userCoords.get(i).get_coord_lon();
                LatLng ll = new LatLng(currLat, currLon);

                opt.add(ll);
                if (currLat < minLat) minLat = currLat;
                if (currLat > maxLat) maxLat = currLat;
                if (currLon < minLon) minLon = currLon;
                if (currLon > maxLon) maxLon = currLon;

            }
            opt.color(Color.MAGENTA);
            opt.width(20);
            opt.geodesic(true);
            line = map.addPolyline(opt);
            LatLngBounds AREA = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(AREA, 0));
        }
        return line;
    }

    private int getCoeff(int coord_count) {
        int count;
        if (coord_count > 10000) {
            count = 2000;
        } else if (coord_count > 1000) {
            count = 500;
        } else if (coord_count > 100) {
            count = 100;
        } else {
            count = 10;
        }
        Debug.Log("KOEFF", "coords = " + coord_count + "; koeff = " + count);
        return count;
    }

    public ArrayList<Marker> drawUserCheckPoints(GoogleMap map,WUser user, ArrayList<WCoord> userCoords) {
        ArrayList<Marker> mMarkers = new ArrayList<>();
        int koeff = getCoeff(userCoords.size());
        for (int i = 0; i < userCoords.size(); i++) {
            if ((i % koeff) == 0) {
                //Bitmap bitmap = GetBitmapMarker(context, R.drawable.ic_action_name, ""+i/1000);
                //Bitmap bitmap = drawText(context,""+i/1000,30);
                Bitmap bitmap = textAsBitmap("" + i / koeff, 30, Color.BLACK);

                Debug.Log("Point", "= " + i);
                MarkerOptions mo = new MarkerOptions();
                double currLat = userCoords.get(i).get_coord_lat();
                double currLon = userCoords.get(i).get_coord_lon();
                LatLng ll = new LatLng(currLat, currLon);
                mo.position(ll);
                mo.title("" + i / koeff);
                mo.snippet(new DateFormatTools().dateToString(userCoords.get(i).get_date(), DateFormatTools.TIME_FORMAT_SHORT));
                mo.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                Marker marker = map.addMarker(mo);
                mMarkers.add(marker);
            }
        }
    return mMarkers;
    }




    public Map<WUser, Marker> drawAllUserLastCoords(GoogleMap map, Map<WUser, WCoord> lastUserCoordinates) {
        Map<WUser, Marker> mMarkers = new HashMap<>();
        for (Map.Entry<WUser, WCoord> entry : lastUserCoordinates.entrySet()) {
            WCoord coord = entry.getValue();
            WUser user = entry.getKey();
            LatLng ll = new LatLng(coord.get_coord_lat(), coord.get_coord_lon());
            if (user == null) {
                return mMarkers;
            }
            if (mMarkers.get(user) != null) {
                mMarkers.get(user).setPosition(ll);
                mMarkers.get(user).setTitle(user.get_name() + " " + user.get_id_user());
                mMarkers.get(user).setSnippet("lat: " + coord.get_coord_lat() + ";  lng: " + coord.get_coord_lon());
            } else {
                MarkerOptions mo = new MarkerOptions();
                mo.position(ll);
                mo.title(user.get_name() + " " + user.get_id_user());
                mo.snippet("lat: " + coord.get_coord_lat() + ";  lng: " + coord.get_coord_lon());
                //mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                Marker marker = map.addMarker(mo);
                mMarkers.put(user, marker);
            }

            CameraPosition position = new CameraPosition(ll, 10, 0, 0);
            map.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        return mMarkers;
    }


    public static Bitmap drawText(Context mContext, String text, int textSize) {
        int textWidth = textSize * 2;
        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(textSize);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

// Create bitmap and canvas to draw to
        Resources resources = mContext.getResources();
        int resourceId = R.drawable.ic_action_name;
        Bitmap b = BitmapFactory.decodeResource(resources, resourceId);
        android.graphics.Bitmap.Config bitmapConfig = b.getConfig();
        b = b.copy(bitmapConfig, true);
        Canvas c = new Canvas(b);

// Draw background
        //float scale = resources.getDisplayMetrics().density;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        //c.drawPaint(paint);

// Draw text
        c.save();
        int dx = 0;
        int dy = 0;
        c.translate(dx, dy);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

public ArrayList<WCoord> renderCoords(ArrayList<WCoord> coords){
        ArrayList<WCoord> newCoords = new ArrayList<>();
        int lat_dir_prev=0, lon_dir_prev=0;
        int lat_dir=0, lon_dir=0;
        for(int i=1;i<coords.size()-1;i++){
            //current coord
            if(coords.get(i).get_coord_lat()>coords.get(i-1).get_coord_lat()){
                lat_dir=1;
            }else {
                lat_dir=-1;
            }
            if(coords.get(i).get_coord_lon()>coords.get(i-1).get_coord_lon()){
                lon_dir=1;
            }else {
                lon_dir=-1;
            }
            if((i==1)){
                lat_dir_prev=lat_dir;
                lon_dir_prev=lon_dir;
                newCoords.add(coords.get(i-1));
            }
            if(i==coords.size()-1){
                newCoords.add(coords.get(i));
            }
            //=====================================================================================
            if(lat_dir!=lat_dir_prev|lon_dir!=lon_dir_prev){
                    newCoords.add(coords.get(i));
                    lat_dir_prev=lat_dir;
                    lon_dir_prev=lon_dir;
            }

        }
        Debug.Log("render","Исходный размер = "+coords.size()+"; новый размер = "+newCoords.size());
        return newCoords;
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
  /*  public Bitmap GetBitmapMarker(Context mContext, int resourceId, String mText)
    {
        //try
        //{
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
            paint.setColor(Color.BLACK);
            paint.setTextSize((int) (8 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = (bitmap.getHeight() + bounds.height())/2;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;

        //}
        //catch (Exception e)
        //{
        //    return null;
        //}
    }
    */
/*    public static Bitmap drawText_old(String text, int textWidth, int textSize) {
// Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

// Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

// Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        c.drawPaint(paint);

// Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }
*/
}