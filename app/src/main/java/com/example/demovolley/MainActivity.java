package com.example.demovolley;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.demovolley.beans.Weather;
import com.example.demovolley.beans.WeatherInfo;
import com.example.demovolley.image.BitmapCache;
import com.example.demovolley.request.GsonRequest;
import com.example.demovolley.request.XMLRequest;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {


    public static final String url_baidu = "https://www.baidu.com/";

    public static final String url_image = "https://www.zhuangbi.info/uploads/i/2017-03-21-1385eec7286be2f2a2dc9d9d63cc27ef.png";


    public static final String url_image_gif = "https://www.zhuangbi.info/uploads/i/2017-03-21-33f2821a27ef561d7294d8a6294fa541.gif";

    public static final String url_image_hard = "http://developer.android.com/images/home/aw_dac.png";

    public static final String url_image_error = " https://www.baidu.com/img/bd_logo2.png";


    @InjectView(R.id.tv_content)
    TextView tvContent;
    @InjectView(R.id.iv_imageView)
    ImageView ivImageView;
    @InjectView(R.id.network_image_view)
    NetworkImageView networkImageView;
    private RequestQueue mQueue;

    ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.inject(this);
        mQueue = Volley.newRequestQueue(this);

        imageLoader = new ImageLoader(mQueue, new BitmapCache());


//        mQueue.add(imageRequest);

        networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        networkImageView.setErrorImageResId(R.mipmap.ic_launcher_round);
        networkImageView.setImageUrl(url_image, imageLoader);


        mQueue.add(gsonRequest);

    }

    /**
     * 自定义请求GSON
     * url 中国北京天气接口中文乱码
     *
     * {
     "weatherinfo": {
     "city": "北京",
     "cityid": "101010100",
     "temp": "18",
     "WD": "东南风",
     "WS": "1级",
     "SD": "17%",
     "WSE": "1",
     "time": "17:05",
     "isRadar": "1",
     "Radar": "JC_RADAR_AZ9010_JB",
     "njd": "暂无实况",
     "qy": "1011",
     "rain": "0"
     }
     }
     */
    GsonRequest<Weather> gsonRequest = new GsonRequest<Weather>(
            "http://www.weather.com.cn/data/sk/101010100.html", Weather.class,
            new Response.Listener<Weather>() {
                @Override
                public void onResponse(Weather weather) {
                    WeatherInfo weatherInfo = weather.getWeatherinfo();
                    Log.d("TAG", "city is " + weatherInfo.getCity());
                    Log.d("TAG", "temp is " + weatherInfo.getTemp());
                    Log.d("TAG", "time is " + weatherInfo.getTime());
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("TAG", error.getMessage(), error);
        }
    });

    /**
     * 自定义请求xml
     * url 中国城市接口中文乱码
     */
    XMLRequest xmlRequest = new XMLRequest(
            "http://flash.weather.com.cn/wmaps/xml/china.xml",
            new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser response) {
                    try {
                        int eventType = response.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    String nodeName = response.getName();
                                    if ("city".equals(nodeName)) {
                                        String pName = response.getAttributeValue(0);
                                        Log.d("TAG", "pName is " + pName);
                                    }
                                    break;
                            }
                            eventType = response.next();
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("TAG", error.getMessage(), error);
        }
    });


    /**
     * ImageRequest
     */
    ImageRequest imageRequest = new ImageRequest(url_image, new Response.Listener<Bitmap>() {
        @Override
        public void onResponse(Bitmap response) {
            Log.e("TAG", "onResponse");
            ivImageView.setImageBitmap(response);
        }
    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("TAG", "onErrorResponse");
            ivImageView.setImageResource(R.mipmap.ic_launcher);
        }
    });


    /**
     * JSON请求
     */
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://m.weather.com.cn/data/101010100.html", null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("TAG", response.toString());
                    tvContent.setText(response.toString());
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("TAG", error.getMessage(), error);
        }
    });


    /**
     * POST
     * String 请求
     */
    StringRequest stringRequestPost = new StringRequest(Request.Method.POST, url_baidu, new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }) {

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return super.getParams();
        }
    };


    /**
     * string请求
     */
    StringRequest stringRequest = new StringRequest(url_baidu,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("TAG", response);
                    tvContent.setText(response);

                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("TAG", error.getMessage(), error);
            tvContent.setText(error.getMessage());
        }
    });


}
