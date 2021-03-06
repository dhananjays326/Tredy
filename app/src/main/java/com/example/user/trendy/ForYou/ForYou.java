package com.example.user.trendy.ForYou;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.user.trendy.Account.MyAccount;
import com.example.user.trendy.BuildConfig;
import com.example.user.trendy.ForYou.AllCollection.AllCollectionAdapter;
import com.example.user.trendy.ForYou.AllCollection.AllCollectionModel;
import com.example.user.trendy.ForYou.NewArrival.NewArrivalModel;
import com.example.user.trendy.ForYou.TopCollection.TopCollectionAdapter;
import com.example.user.trendy.ForYou.TopCollection.TopCollectionModel;
import com.example.user.trendy.ForYou.TopSelling.TopSellingAdapter;
import com.example.user.trendy.ForYou.TopSelling.TopSellingModel;
import com.example.user.trendy.Groceries.Groceries;
import com.example.user.trendy.Groceries.GroceryModel;
import com.example.user.trendy.R;
import com.example.user.trendy.Util.Constants;
import com.example.user.trendy.Whislist.Whislist;
import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.HttpCachePolicy;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ForYou extends Fragment implements ResultCallBackInterface {


    private ArrayList<Object> objects = new ArrayList<>();
    ResultCallBackInterface resultCallBackInterface;


    RecyclerView topselling_recyclerview, topcollection_recyclerview, new_arrivals_recyclerview, allcollection;
    GraphClient graphClient;
    static ArrayList<TopSellingModel> topSellingModelArrayList = new ArrayList<>();
    static ArrayList<TopCollectionModel> topCollectionModelArrayList = new ArrayList<>();
    static ArrayList<NewArrivalModel> newArrivalModelArrayList = new ArrayList<>();
    static ArrayList<GroceryModel> groceryModels = new ArrayList<>();
    TopSellingAdapter topSellingAdapter;
    TopCollectionAdapter topCollectionAdapter;
    String topsellingid = "Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzM0NTA2OTg5NA==";
    String bestseller = "Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzMyMTgxNzI4Ng==";
    String newproduct = "Z2lkOi8vc2hvcGlmeS9Db2xsZWN0aW9uLzMzMjM4MTIyNjE1";
    String topsellingid1 = "345069894";
    String bestseller1 = "321817286";
    String newproduct1 = "33238122615";
    private RequestQueue mRequestQueue;
    MainAdapter adapter;
    AllCollectionAdapter allCollectionAdapter;
    ArrayList<AllCollectionModel> allCollectionModelArrayList = new ArrayList<>();
    ArrayList<String> bannerlist = new ArrayList<>();

    private ArrayList<Object> getObjects1 = new ArrayList<>();
    private ArrayList<TopCollectionModel> topcollectionlist = new ArrayList<>();
    View view;
    String id, title, price, image;
    private String collectionname;
    private JsonArrayRequest request;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000;
    SlidingImage_Adapter slidingImage_adapter;
    private String collectionid;
    CardView myaccount, whislist;
    TextView grcery;
    ArrayList<TopSellingModel> topSellingModelArray = new ArrayList<>();
    ArrayList<TopCollectionModel> topCollectionModelArray = new ArrayList<>();
    ArrayList<NewArrivalModel> newArrivalModelArray = new ArrayList<>();
    private ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.foryou, container, false);
//String collectionids="345069894,321817286,33238122615";
        topselling_recyclerview = view.findViewById(R.id.main_recyclerview);
        allcollection = view.findViewById(R.id.allcollection);
        resultCallBackInterface = (ResultCallBackInterface) this;
//        grcery = view.findViewById(R.id.grocery);
        String id = "58881703997";
        String text = "gid://shopify/Collection/" + id.trim();
        String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
        Log.e("coverted1", converted.trim());


        graphClient = GraphClient.builder(getActivity())
                .shopDomain(BuildConfig.SHOP_DOMAIN)
                .accessToken(BuildConfig.API_KEY)
                .httpCache(new File(getActivity().getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();
//        grcery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
//                transaction.replace(R.id.home_container, new Groceries(), "whislist");
////                    transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });

        mPager = (ViewPager) view.findViewById(R.id.pager);
        allCollectionAdapter = new AllCollectionAdapter(getActivity(), allCollectionModelArrayList, getFragmentManager());
        allcollection.setAdapter(allCollectionAdapter);
        allcollection.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        topselling_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        banner();
        getObject().clear();
//        productlist();
        collectionList();
        getCollection(converted.trim());
//        getBestSellingCollection();

//        getNewArrivals();
        if (getTopSellingCollection() != null || getNewArrival() != null||getGroceryModels()!=null) {
            adapter = new MainAdapter(getActivity(), getObject(), getFragmentManager());
            topselling_recyclerview.setAdapter(adapter);
        }

        myaccount = view.findViewById(R.id.myaccount);
        whislist = view.findViewById(R.id.whislist);
        whislist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.home_container, new Whislist(), "whislist");
//                    transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        myaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.home_container, new MyAccount(), "account");
//                    transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private ArrayList<Object> getObject() {

        return objects;
    }

    public static ArrayList<TopSellingModel> getTopSellingCollection() {
        return topSellingModelArrayList;
    }

    public static ArrayList<TopCollectionModel> getBestCollection() {
        return topCollectionModelArrayList;
    }

    public static ArrayList<NewArrivalModel> getNewArrival() {
        return newArrivalModelArrayList;
    }

    public static ArrayList<GroceryModel> getGroceryModels() {
        return groceryModels;
    }

    @Override
    public void bestCollection(ArrayList<TopCollectionModel> arrayList) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < arrayList.size(); i++) {
                    TopCollectionModel topCollectionModel = new TopCollectionModel(arrayList.get(i).getProduct_ID(), arrayList.get(i).getProduct_title(), arrayList.get(i).getPrice(), arrayList.get(i).getImageUrl(), arrayList.get(i).getCollectionTitle());

                    topCollectionModel.setCollectionid(arrayList.get(i).getCollectionid());
                    topCollectionModelArrayList.add(topCollectionModel);
                }

                getObject().add(topCollectionModelArrayList.get(0));
                adapter.notifyDataSetChanged();


            }
        });
    }

    @Override
    public void topSelling(ArrayList<TopSellingModel> arrayList) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                TopSellingModel topSellingModel = new TopSellingModel(id, title, price, image, collectionname);
//                Log.e("product", title);
//                topSellingModel.setCollectionid(collectionid);
                for (int i = 0; i < arrayList.size(); i++) {
                    TopSellingModel topSellingModel = new TopSellingModel(arrayList.get(i).getProduct_ID(), arrayList.get(i).getProduct_title(), arrayList.get(i).getPrice(), arrayList.get(i).getImageUrl(), arrayList.get(i).getCollectionTitle());
//                Log.e("product", title);
                    topSellingModel.setCollectionid(arrayList.get(i).getCollectionid());
                    topSellingModelArrayList.add(topSellingModel);
                }


                getObject().add(topSellingModelArrayList.get(0));
                adapter.notifyDataSetChanged();


            }
        });

    }

    @Override
    public void newArrivals(ArrayList<NewArrivalModel> arrayList) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                NewArrivalModel newArrivalModel = new NewArrivalModel(id, title, price, image, collectionname);
//                Log.e("product", title);
//                newArrivalModel.setCollectionid(collectionid);
//                newArrivalModelArrayList.add(newArrivalModel);

                for (int i = 0; i < arrayList.size(); i++) {
                    NewArrivalModel newArrivalModel = new NewArrivalModel(arrayList.get(i).getProduct_ID(), arrayList.get(i).getProduct_title(), arrayList.get(i).getPrice(), arrayList.get(i).getImageUrl(), arrayList.get(i).getCollectionTitle());
//                Log.e("product", title);
                    newArrivalModel.setCollectionid(arrayList.get(i).getCollectionid());
                    newArrivalModelArrayList.add(newArrivalModel);
                }
                getObject().add(newArrivalModelArrayList.get(0));
                adapter.notifyDataSetChanged();


            }
        });
    }

    @Override
    public void grocery(ArrayList<GroceryModel> arrayList) {
        for (int i = 0; i <arrayList.size() ; i++) {
            GroceryModel groceryModel = new GroceryModel();
            groceryModel.setProduct(arrayList.get(i).getProduct());
            groceryModel.setQty("1");
            groceryModel.setTitle(arrayList.get(i).getTitle());
            groceryModels.add(groceryModel);
        }



        Log.d("grocery ", String.valueOf(groceryModels.size()));
        getObject().add(groceryModels.get(0));
//        adapter.notifyDataSetChanged();
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getCollection(String trim) {
        groceryModelArrayList.clear();
        Storefront.QueryRootQuery query = Storefront.query(rootQuery -> rootQuery
                .node(new ID(trim.trim()), nodeQuery -> nodeQuery
                        .onCollection(collectionQuery -> collectionQuery
                                .title()
                                .products(arg -> arg.first(10), productConnectionQuery -> productConnectionQuery
                                        .edges(productEdgeQuery -> productEdgeQuery
                                                .node(productQuery -> productQuery
                                                        .title()
                                                        .productType()
                                                        .description()
                                                        .descriptionHtml()
                                                        .images(arg -> arg.first(10), imageConnectionQuery -> imageConnectionQuery
                                                                .edges(imageEdgeQuery -> imageEdgeQuery
                                                                        .node(imageQuery -> imageQuery
                                                                                .src()
                                                                        )
                                                                )
                                                        )
                                                        .tags()
                                                        .options(option->option.name())
                                                        .variants(arg -> arg.first(10), variantConnectionQuery -> variantConnectionQuery
                                                                .edges(variantEdgeQuery -> variantEdgeQuery
                                                                        .node(productVariantQuery -> productVariantQuery
                                                                                .price()
                                                                                .title()
                                                                                .image(args -> args.src())
                                                                                .weight()
                                                                                .weightUnit()
                                                                                .available()
                                                                        )
                                                                )
                                                        )
                                                )
                                        )


                                ))));

        graphClient.queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                Storefront.Collection product = (Storefront.Collection) response.data().getNode();

                for (Storefront.ProductEdge productEdge : product.getProducts().getEdges()) {
                    GroceryModel groceryModel = new GroceryModel();
                    groceryModel.setProduct(productEdge.getNode());
                    groceryModel.setTitle(product.getTitle());
                    groceryModel.setQty("1");
                    groceryModelArrayList.add(groceryModel);
                }
                resultCallBackInterface.grocery(groceryModelArrayList);
                Log.e("groceryModelArrayList", String.valueOf(groceryModelArrayList.size()));
                Log.e("groceryModelArrayList", String.valueOf(product.getProducts().getEdges().size()));
                Log.e("productch", product.getProducts().getEdges().get(0).getNode().getTitle());
                }

            @Override
            public void onFailure(@NonNull GraphError error) {

            }
        });
    }

    private void collectionList1() {

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.collectionid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);
//                            collectionname = obj.getString("collection_name");
                            topSellingModelArrayList.clear();
                            topCollectionModelArrayList.clear();
                            newArrivalModelArrayList.clear();

                            Iterator keys = obj.keys();
                            Log.e("Keys", "" + String.valueOf(keys));

                            while (keys.hasNext()) {
                                String dynamicKey = (String) keys.next();
                                Log.d("Dynamic Key", "" + dynamicKey);

                                JSONArray array = null;
                                try {

                                    array = obj.getJSONArray(dynamicKey);


                                    for (int i = 0; i < array.length(); i++) {
                                        Log.e("inti", String.valueOf(i));
                                        JSONObject object1 = array.getJSONObject(i);
                                        collectionid = object1.getString("id");
                                        collectionname = object1.getString("title");
                                        JSONArray array1 = object1.getJSONArray("products");
                                        for (int j = 0; j < array1.length(); j++) {
                                            JSONObject objec = array1.getJSONObject(j);

                                            title = objec.getString("title");
                                            JSONArray varientsarray = objec.getJSONArray("variants");
                                            for (int k = 0; k < varientsarray.length(); k++) {
                                                JSONObject objec1 = varientsarray.getJSONObject(k);

                                                id = objec1.getString("product_id");
                                                price = objec1.getString("price");

                                            }
                                            JSONArray array2 = objec.getJSONArray("images");
                                            for (int l = 0; l < array2.length(); l++) {
                                                JSONObject objec1 = array2.getJSONObject(l);
                                                image = objec1.getString("src");
                                            }
                                            if (i == 0) {
                                                TopSellingModel topSellingModel = new TopSellingModel(id, title, price, image, collectionname);
                                                Log.e("product", title);
                                                topSellingModel.setCollectionid(collectionid);
                                                topSellingModelArray.add(topSellingModel);

                                            } else if (i == 1) {
                                                Log.e("iiii", String.valueOf(i));
                                                TopCollectionModel topCollectionModel = new TopCollectionModel(id, title, price, image, collectionname);
                                                Log.e("product", title);
                                                topCollectionModel.setCollectionid(collectionid);
                                                topCollectionModelArray.add(topCollectionModel);

//                                                resultCallBackInterface.bestCollection(collectionid, id, title, price, image, collectionname);
                                            } else if (i == 2) {
                                                Log.e("iiii", String.valueOf(i));
                                                NewArrivalModel newArrivalModel = new NewArrivalModel(id, title, price, image, collectionname);
                                                Log.e("product", title);
                                                newArrivalModel.setCollectionid(collectionid);
                                                newArrivalModelArray.add(newArrivalModel);

//                                                resultCallBackInterface.newArrivals(collectionid, id, title, price, image, collectionname);
                                            }

                                        }
                                    }

                                    resultCallBackInterface.topSelling(topSellingModelArray);
                                    resultCallBackInterface.bestCollection(topCollectionModelArray);
                                    resultCallBackInterface.newArrivals(newArrivalModelArray);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();

                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        stringRequest.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);

    }


    private void collectionList() {
        mRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.navigation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("response", " " + response);

                            JSONObject obj = new JSONObject(response);
                            Log.e("response1", response);
                            allCollectionModelArrayList.clear();
                            JSONObject menu = obj.getJSONObject("menu");
                            //  String status = obj.getString("menu");

                            String title = menu.getString("title");
                            Log.e("title", title);
                            // JSONObject allhistoryobj = obj.getJSONObject("insurance");
                            JSONArray jsonarray = menu.getJSONArray("items");
                            Log.e("jsonarray", String.valueOf(jsonarray));

                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject collectionobject = jsonarray.getJSONObject(i);


                                String id = "" + collectionobject.getString("subject_id");
                                String collectiontitle = collectionobject.getString("title");
                                String nav = collectionobject.getString("type");
//                                String image = collectionobject.getString("image");


                                Log.e("id", id);
                                Log.e("collectiontitle", collectiontitle);

                                if (id.trim().length() != 0) {
                                    String text = "gid://shopify/Collection/" + id.trim();

                                    String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
                                    Log.e("coverted", converted.trim());
                                }

                                if (nav.trim().equals("http") || nav.trim().equals("collection")) {


                                    JSONArray jsonarray1 = collectionobject.getJSONArray("items");
                                    Log.e("jsonarray1", String.valueOf(jsonarray1));
                                    if (jsonarray1.length() != 0) {
                                        for (int j = 0; j < jsonarray1.length(); j++) {
                                            JSONObject subcollectionobject = jsonarray1.getJSONObject(j);

                                            String subid = "" + subcollectionobject.getString("subject_id");
                                            String subcollectiontitle = subcollectionobject.getString("title");
                                            String type = subcollectionobject.getString("type");
                                            if (type.trim().equals("collection")) {
                                                String image1 = subcollectionobject.getString("image");
                                                if (!subid.trim().equals("null")) {


                                                    if (subid.trim().length() != 0) {
                                                        String text = "gid://shopify/Collection/" + subid.trim();

                                                        String converted = Base64.encodeToString(text.toString().getBytes(), Base64.DEFAULT);
                                                        Log.e("coverted", converted.trim());
                                                    }

                                                    AllCollectionModel allCollectionModel = new AllCollectionModel(subid, image1, subcollectiontitle);
                                                    allCollectionModelArrayList.add(allCollectionModel);
                                                }


                                            }

                                        }
                                        allCollectionAdapter.notifyDataSetChanged();
//                                    } else {
//                                        categoryList.add(categoreDetail);
                                    }

                                }


                            }
                            collectionList1();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error", error.getMessage());

                    }
                }) {

            @Override
            protected void deliverResponse(String response) {
                Log.e("ree", " " + response);
                super.deliverResponse(response);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.e("reen", " " + response.headers);
                return super.parseNetworkResponse(response);
            }
        };
        stringRequest.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);

    }

    private void banner() {
        bannerlist.clear();
        mRequestQueue = Volley.newRequestQueue(getActivity());


        request = new JsonArrayRequest(Constants.banner,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String bannerimage = jsonObject.getString("image_src");
                                bannerlist.add(bannerimage);
                                init();


                            } catch (JSONException e) {

                            }
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

        };
        request.setTag("categories_page");
        // VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        mRequestQueue.add(request);

    }

    private void init() {

//Log.e("bannerlist", ""+String.valueOf(bannerlist.size()));
        for (int i = 0; i < bannerlist.size(); i++)
            ImagesArray.add(bannerlist.get(i));


        slidingImage_adapter = new SlidingImage_Adapter(getActivity(), ImagesArray);
        mPager.setAdapter(slidingImage_adapter);


        CirclePageIndicator indicator = (CirclePageIndicator) view.
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        indicator.setRadius(3 * density);


        NUM_PAGES = ImagesArray.size();


        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 7000, 7000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }


}
