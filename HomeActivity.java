package com.maurishop2911.adforest.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import com.maurishop2911.adforest.profile.Expired_SoldAds;
import com.maurishop2911.adforest.profile.MyAds_Rejected;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.squareup.picasso.Picasso;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.maurishop2911.adforest.Blog.BlogDetailFragment;
import com.maurishop2911.adforest.Blog.BlogFragment;
import com.maurishop2911.adforest.Notification.Config;
import com.maurishop2911.adforest.R;
import com.maurishop2911.adforest.Search.FragmentCatSubNSearch;
import com.maurishop2911.adforest.Search.SearchActivity;
import com.maurishop2911.adforest.Settings.Settings;
import com.maurishop2911.adforest.Shop.shopActivity;
import com.maurishop2911.adforest.SplashScreen;
import com.maurishop2911.adforest.helper.LocaleHelper;
import com.maurishop2911.adforest.home.helper.Location_popupModel;
import com.maurishop2911.adforest.messages.Message;
import com.maurishop2911.adforest.packages.PackagesFragment;
import com.maurishop2911.adforest.profile.FragmentProfile;
import com.maurishop2911.adforest.profile.MyAds;
import com.maurishop2911.adforest.profile.MyAds_Favourite;
import com.maurishop2911.adforest.profile.MyAds_Featured;
import com.maurishop2911.adforest.profile.MyAds_Inactive;
import com.maurishop2911.adforest.signinorup.MainActivity;
import com.maurishop2911.adforest.userAndSellers.SellersListFragment;
import com.maurishop2911.adforest.utills.Admob;
import com.maurishop2911.adforest.utills.CircleTransform;
import com.maurishop2911.adforest.utills.GPSTracker;
import com.maurishop2911.adforest.utills.Network.RestService;
import com.maurishop2911.adforest.utills.RuntimePermissionHelper;
import com.maurishop2911.adforest.utills.SettingsMain;
import com.maurishop2911.adforest.utills.UrlController;

import static com.maurishop2911.adforest.utills.SettingsMain.getMainColor;

//import com.com.scriptsbundle.adforest.adapters.PlaceArrayAdapter;
//import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ADDRESS;
//import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_REGIONS;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RuntimePermissionHelper.permissionInterface,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemClickListener {

    //    FirebaseDatabase database;
//    DatabaseReference myRef;

    public static Activity activity;
    public static Boolean checkLoading = false;
    boolean back_pressed = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsMain != null) {
            if (!settingsMain.getUserVerified()) {
                replaceFragment(new FragmentProfile(), "FragmentProfile");
            }
        }
    }


    ArrayList<String> strings = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    SettingsMain settingsMain;
    ImageView imageViewProfile;
    UpdateFragment updatfrag;
    TextView textViewUserName, title;
    RestService restService;
    AutoCompleteTextView currentLocationText;
    EditText MapBoxPlaces;
    FragmentHome fragmentHome;
    FragmentProfile fragmentProfile;
    GPSTracker gps;
    double latitude, longitude;
    RuntimePermissionHelper runtimePermissionHelper;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private PlacesClient placesClient;
    String location, locationIdHomePOpup, locationIdHomePOpupName;
    String imageView;
    double lat_by_mapbox, lon_by_mapbox;
    String address_by_mapbox;

    public void updateApi(UpdateFragment listener) {
        updatfrag = listener;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        System.gc();
        settingsMain = new SettingsMain(this);
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("UserLogin");
        activity = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(getMainColor())));

        fab.setOnClickListener(view -> {
            runtimePermissionHelper.requestLocationPermission(2);
        });

//        FloatingActionButton homeFloatingActionButton = (FloatingActionButton) findViewById(R.id.home);
//        homeFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(getMainColor())));
//        homeFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            SettingsMain.showDilog(activity);
            checkLoading = true;
            adforest_swipeRefresh();

        });


        toolbar.setBackgroundColor(Color.parseColor(getMainColor()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);

        if (settingsMain.getAppOpen()) {
            restService = UrlController.createService(RestService.class);
            fab.setVisibility(View.VISIBLE);
//            homeFloatingActionButton.setVisibility(View.GONE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getApplicationContext(), settingsMain.getNoLoginMessage(), Toast.LENGTH_LONG).show();

                }
            });

        } else {
            restService = UrlController.createService(RestService.class, settingsMain.getUserEmail(), settingsMain.getUserPassword(), this);

        }
        if (header != null) {
            TextView textViewUserEmail = header.findViewById(R.id.textView);
            textViewUserName = header.findViewById(R.id.username);
            imageViewProfile = header.findViewById(R.id.imageView);


            int[] colors = {Color.parseColor(getMainColor()), Color.parseColor(getMainColor())};
            //create a new gradient color
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, colors);
            gd.setCornerRadius(0f);

            header.setBackground(gd);

            if (!TextUtils.isEmpty(settingsMain.getUserEmail())) {
                textViewUserEmail.setText(settingsMain.getUserEmail());
            }
            if (!TextUtils.isEmpty(settingsMain.getUserName())) {
                textViewUserName.setText(settingsMain.getUserName());
            }
            if (settingsMain.getAppOpen()) {
                if (!TextUtils.isEmpty(settingsMain.getGuestImage())) {
                    Picasso.with(this).load(settingsMain.getGuestImage())
                            .transform(new CircleTransform())
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.placeholder)
                            .into(imageViewProfile);
                }
            } else {
                if (!TextUtils.isEmpty(settingsMain.getUserImage())) {
                    Picasso.with(this).load(settingsMain.getUserImage())
                            .transform(new CircleTransform())
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.placeholder)
                            .into(imageViewProfile);
                }
            }
        }
        locationIdHomePOpup = getIntent().getStringExtra("location_id");
        locationIdHomePOpupName = getIntent().getStringExtra("location_name");
//        locationIdHomePOpupName=getIntent().getStringExtra("location_name");
//        imageView=getIntent().getStringExtra("location_img");
//        location=getIntent().getStringExtra("location_id");
        fragmentHome = new FragmentHome();
        Bundle bundle = new Bundle();
//        bundle.putString("location_id",location);
        bundle.putString("location_id", locationIdHomePOpup);
        bundle.putString("location_name", locationIdHomePOpupName);
//        bundle.putString("location_img",imageView);

        fragmentHome.setArguments(bundle);
        if (!settingsMain.getNotificationTitle().equals("")) {
            String title, message, image;
            title = settingsMain.getNotificationTitle();
            message = settingsMain.getNotificationMessage();
            image = settingsMain.getNotificationImage();

            adforest_showNotificationDialog(title, message, image);
            updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));
        }
        try {
            if (settingsMain.getNotificationTitle().equals("")) {
                if (SplashScreen.jsonObjectAppRating.getBoolean("is_show"))
                    showRatingDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startFragment(fragmentHome, "FragmentHome");
        getIncomingIntent();
    }

    public void getIncomingIntent() {
        if (getIntent().hasExtra("title")) {
            String title = getIntent().getStringExtra("title");
            setitle(title);
        }
//        if (getIntent().hasExtra("location")) {
//            String location = getIntent().getStringExtra("location");
//            setitle(location);
//        }

    }

    private void setitle(String title) {
        TextView name = (TextView) findViewById(R.id.txt_pick_your_language);
        name.setText(title);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    private void adforest_swipeRefresh() {
        String fragment = null;
        swipeRefreshLayout.setRefreshing(true);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameContainer);
        if (currentFragment instanceof FragmentHome) {
            fragment = "FragmentHome";
        }
        if (currentFragment instanceof BlogFragment) {
            fragment = "BlogFragment";
        }
        if (currentFragment instanceof BlogDetailFragment) {
            fragment = "BlogDetailFragment";
        }
        if (currentFragment instanceof PackagesFragment) {
            fragment = "PackagesFragment";
        }
        if (currentFragment instanceof MyAds) {
            fragment = "MyAds";
        }
        if (currentFragment instanceof MyAds_Favourite) {
            fragment = "MyAds_Favourite";
        }
        if (currentFragment instanceof MyAds_Rejected) {
            fragment = "MyAds_Rejected";
        }if (currentFragment instanceof Expired_SoldAds) {
            fragment = "MyAdsExpire";
        }
        if (currentFragment instanceof MyAds_Featured) {
            fragment = "MyAds_Featured";
        }
        if (currentFragment instanceof MyAds_Inactive) {
            fragment = "MyAds_Inactive";
        }
        if (currentFragment instanceof FragmentCatSubNSearch) {
            fragment = "FragmentCatSubNSearch";
        }
        if (currentFragment instanceof FragmentAllLocations) {
            fragment = "FragmentAllLocations";
        }
        if (currentFragment instanceof FragmentAllCategories) {
            fragment = "FragmentAllCategories";
        }

        Handler handler = new Handler();
        final String finalFragment = fragment;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SettingsMain.reload(HomeActivity.this, finalFragment);
                swipeRefreshLayout.setRefreshing(false);
//                SettingsMain.hideDilog();
            }
        }, 500);
    }

    public void showRatingDialog() {
        String title = null, text = null, btn_confirm = null, btn_cancel = null, url = null;
        try {
            title = SplashScreen.jsonObjectAppRating.getString("title");
            btn_confirm = SplashScreen.jsonObjectAppRating.getString("btn_confirm");
            btn_cancel = SplashScreen.jsonObjectAppRating.getString("btn_cancel");
            url = SplashScreen.jsonObjectAppRating.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("this", "Feedback:");


        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(3)
                .threshold(3)
                .title(title)
                .positiveButtonText(btn_confirm)
                .negativeButtonText(btn_cancel)
                .ratingBarColor(R.color.yellow)
                .playstoreUrl(url)
                .onRatingBarFormSumbit(feedback -> Log.i("this", "Feedback:" + feedback))
                .build();


        ratingDialog.show();
    }

    private void adforest_showNotificationDialog(String title, String message, String image) {

        final Dialog dialog;
        dialog = new Dialog(HomeActivity.this, R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_notification_layout);
        ImageView imageView = dialog.findViewById(R.id.notificationImage);
        TextView tv_title = dialog.findViewById(R.id.notificationTitle);
        TextView tV_message = dialog.findViewById(R.id.notificationMessage);
        Button button = dialog.findViewById(R.id.cancel_button);
        button.setText(settingsMain.getGenericAlertCancelText());
        button.setBackgroundColor(Color.parseColor(getMainColor()));


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));


        if (!TextUtils.isEmpty(image)) {
            Picasso.with(this).load(image)
                    .fit()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }

        tv_title.setText(title);
        tV_message.setText(message);

        button.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void changeImage() {
        if (!TextUtils.isEmpty(settingsMain.getUserImage())) {
            Picasso.with(this).load(settingsMain.getUserImage())
                    .transform(new CircleTransform())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageViewProfile);
        }
        textViewUserName.setText(settingsMain.getUserName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        MenuItem advancedsearchViewItem = menu.findItem(R.id.action_advanced_search);
        MenuItem action_home = menu.findItem(R.id.action_home);
        MenuItem action_location = menu.findItem(R.id.action_location);
        action_home.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(HomeActivity.this.getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                return true;
            }
        });
        //&& !settingsMain.getUserLogin().equals("0")
        if (settingsMain.getShowHome() ) {
            action_home.setVisible(true);
        } else {
            action_home.setVisible(false);
        }
        if (settingsMain.getShowNearBy() && settingsMain.getAdsPositionSorter() && !settingsMain.getUserLogin().equals("0")) {
            action_location.setVisible(true);
        } else {
            action_location.setVisible(false);
        }
        action_location.setOnMenuItemClickListener(item -> {
            runtimePermissionHelper.requestLocationPermission(1);
            return true;
        });
        if (!settingsMain.getShowAdvancedSearch()) {
            advancedsearchViewItem.setVisible(false);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
            searchView.setQueryHint(settingsMain.getAlertDialogMessage("search_text"));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();

                    if (!query.equals("")) {

                        FragmentManager fm = getSupportFragmentManager();
                        Fragment fragment = fm.findFragmentByTag("FragmentCatSubNSearch");
                        Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

                        FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
                        Bundle bundle = new Bundle();
                        bundle.putString("id", "");
                        bundle.putString("title", query);

                        fragment_search.setArguments(bundle);

                        if (fragment != fragment2) {
                            replaceFragment(fragment_search, "FragmentCatSubNSearch");
                            return true;
                        } else {
                            updatfrag.updatefrag(query);
                            return true;
                        }
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        } else {
            searchViewItem.setVisible(false);
            advancedsearchViewItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent intent = new Intent(HomeActivity.this.getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                    return true;

                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();

        if (SplashScreen.gmap_has_countries) {
            request.setCountry(SplashScreen.gmap_countries);
        }
        if (settingsMain.getAlertDialogMessage("location_type").equals("regions")) {
            request.setTypeFilter(TypeFilter.ADDRESS);
        } else {
            request
                    .setTypeFilter(TypeFilter.REGIONS);
        }
        request.setSessionToken(token)
                .setQuery(query);


        placesClient.findAutocompletePredictions(request.build()).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());
            }
            String[] data = places.toArray(new String[places.size()]); // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(HomeActivity.this, android.R.layout.simple_dropdown_item_1line, data);
            currentLocationText.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String placeId = ids.get(position);
        List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
// Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            com.google.android.libraries.places.api.model.Place place = response.getPlace();
            Log.i("Places", "Place found: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
            longitude = place.getLatLng().longitude;
            latitude = place.getLatLng().latitude;

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
// Handle error with given status code.
                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });


    }

    private void adforest_loctionSearch() {

        gps = new GPSTracker(HomeActivity.this);

        List<Address> addresses1 = null;
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Location_popupModel Location_popupModel = settingsMain.getLocationPopupModel(this);

            final Dialog dialog = new Dialog(HomeActivity.this, R.style.customDialog);

            dialog.setCanceledOnTouchOutside(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_location_seekbar);

            final BubbleSeekBar bubbleSeekBar = dialog.findViewById(R.id.seakBar);
            bubbleSeekBar.getConfigBuilder()
                    .max(Location_popupModel.getSlider_number())
                    .sectionCount(Location_popupModel.getSlider_step())
                    .secondTrackColor(Color.parseColor(getMainColor()))
                    .build();

            try {
                addresses1 = new Geocoder(this, Locale.getDefault()).getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder result = new StringBuilder();
            if (addresses1.size() > 0) {
                Address address = addresses1.get(0);
                int maxIndex = address.getMaxAddressLineIndex();
                for (int x = 0; x <= maxIndex; x++) {
                    result.append(address.getAddressLine(x));
                    //result.append(",");
                }
            }
            Log.d("info location", addresses1.toString());
            Log.d("info locaLatLong", latitude + " Long " + longitude);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));
            Button Send = dialog.findViewById(R.id.send_button);
            Button Cancel = dialog.findViewById(R.id.cancel_button);
            TextView locationText = dialog.findViewById(R.id.locationText);

            currentLocationText = dialog.findViewById(R.id.et_location);
            placesClient = com.google.android.libraries.places.api.Places.createClient(this);
            currentLocationText.setOnItemClickListener(this);
//            MapBoxPlaces = dialog.findViewById(R.id.et_location_mapBox);
//            MapBoxPlaces.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new PlaceAutocomplete.IntentBuilder()
//                            .accessToken(getString(R.string.access_token))
//                            .placeOptions(PlaceOptions.builder().backgroundColor(Color.parseColor("#EEEEEE")).limit(10).build(PlaceOptions.MODE_CARDS))
//                            .build(HomeActivity.this);
//                    startActivityForResult(intent, 35);
//                }
//            });
            currentLocationText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    manageAutoComplete(s.toString());

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            Send.setText(Location_popupModel.getBtn_submit());
            Cancel.setText(Location_popupModel.getBtn_clear());
            locationText.setText(Location_popupModel.getText());

            if (result.toString().isEmpty()) {
                currentLocationText.setVisibility(View.GONE);
            } else
                currentLocationText.setHint(result.toString());
            Send.setBackgroundColor(Color.parseColor(getMainColor()));
            Cancel.setBackgroundColor(Color.parseColor(getMainColor()));

            Send.setOnClickListener(v -> {
                adforest_changeNearByStatus(Double.toString(latitude), Double.toString(longitude),
                        Integer.toString(bubbleSeekBar.getProgress()));
                dialog.dismiss();
            });
            Cancel.setOnClickListener(v -> {
                adforest_changeNearByStatus("", ""
                        , Integer.toString(bubbleSeekBar.getProgress()));
                dialog.dismiss();
            });

            dialog.show();
        } else
            gps.showSettingsAlert();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 35) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                CarmenFeature feature = PlaceAutocomplete.getPlace(data);
                Point point = feature.center();
                latitude = point.latitude();
                longitude = point.longitude();
                address_by_mapbox = feature.placeName();
                Log.d("karwao", address_by_mapbox.toString());
                MapBoxPlaces.setText(address_by_mapbox);

            }
        }
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            backPressed();
//            super.onBackPressed();
            overridePendingTransition(R.anim.left_enter, R.anim.right_out);
        }
    }

    private void backPressed() {
        if (!back_pressed) {
            Toast.makeText(HomeActivity.this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
            back_pressed = true;
            android.os.Handler mHandler = new android.os.Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2000L);
        } else {
            //Alert dialog for exit form login screen

            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle(settingsMain.getAlertDialogTitle("info"));
            alert.setCancelable(false);
            alert.setMessage("Are you sure you want to exit?");
            alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                HomeActivity.this.finishAffinity();
                dialog.dismiss();
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            });
            alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Admob.adforest_cancelInterstitial();
        Log.d("info onDestroy called", "onDestroy");
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_blog) {
            replaceFragment(new BlogFragment(), "BlogFragment");
        }
        if (id == R.id.nav_sellers) {
            replaceFragment(new SellersListFragment(), "SellersListFragment");
        }
        if (id == R.id.nav_shop) {
            Intent intent = new Intent(getApplicationContext(), shopActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        }
        if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        }
        if (id == R.id.nav_language) {

            Intent intent = new Intent(getApplicationContext(), ChooseLanguageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        }
        if (id == R.id.nav_location) {
            replaceFragment(new ChooseLocationFragment(),"ChooseLocationFragment");

//            Intent intent = new Intent(getApplicationContext(), ChooseLocationActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        }
        if (id == R.id.search) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            intent.putExtra("catId", "");
            startActivity(intent);
            overridePendingTransition(R.anim.right_enter, R.anim.left_out);

        } else if (id == R.id.profile) {
            replaceFragment(new FragmentProfile(), "FragmentProfile");
        } else if (id == R.id.myAds) {
            if (settingsMain.getAppOpen()) {
                settingsMain.setUserLogin("0");
                settingsMain.setFireBaseId("");
                HomeActivity.this.finish();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            } else
                replaceFragment(new MyAds(), "MyAds");
        } else if (id == R.id.favAds) {
            replaceFragment(new MyAds_Favourite(), "MyAds_Favourite");
        } else if (id == R.id.rejectedAds) {
            replaceFragment(new MyAds_Rejected(), "MyAds_Rejected");
        }else if (id == R.id.expire_sold_Ads) {
            replaceFragment(new Expired_SoldAds(), "MyAdsExpire");
        } else if (id == R.id.packages) {
            replaceFragment(new PackagesFragment(), "PackagesFragment");
        } else if (id == R.id.home) {
            FragmentManager fm = HomeActivity.this.getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
        } else if (id == R.id.inActiveAds) {
            if (settingsMain.getAppOpen()) {
                settingsMain.setUserLogin("0");
                settingsMain.setFireBaseId("");
                HomeActivity.this.finish();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("page", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            } else
                replaceFragment(new MyAds_Inactive(), "MyAds_Inactive");
        } else if (id == R.id.featureAds) {
            replaceFragment(new MyAds_Featured(), "MyAds_Featured");
        } else if (id == R.id.nav_log_out) {
            LoginManager.getInstance().logOut();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Config.TOPIC_GLOBAL);
//            if (!settingsMain.getUserLogin().equals("0")) {
//
//                ChatUserModel userModel = new ChatUserModel(true, settingsMain.getUserLogin());
//                myRef.child(settingsMain.getUserLogin()).setValue(userModel);
//            }
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle(settingsMain.getAlertDialogTitle("info"));
            alert.setCancelable(false);
            alert.setMessage(settingsMain.getAlertDialogMessage("confirmMessage"));
            alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                settingsMain.setUserLogin("0");
                settingsMain.setFireBaseId("");
                HomeActivity.this.finish();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                adforest_AddFirebaseid();
                if (settingsMain.getCheckOpen()) {
                    settingsMain.isAppOpen(true);
                }
                dialog.dismiss();
            });
            alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        } else if (id == R.id.message) {
            Intent intent = new Intent(HomeActivity.this, Message.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startFragment(Fragment someFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = someFragment;
            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment, tag).commit();
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

        if (fragment != fragment2) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
            transaction.replace(R.id.frameContainer, someFragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void adforest_AddFirebaseid() {
        if (SettingsMain.isConnectingToInternet(this)) {


            JsonObject params = new JsonObject();


            params.addProperty("firebase_id", "");

            Call<ResponseBody> myCall = restService.postFirebaseId(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info FireBase Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info Data FireBase", response.getJSONObject("data").toString());
                                settingsMain.setFireBaseId(response.getJSONObject("data").getString("firebase_reg_id"));
                                Log.d("info FireBase ID", response.getJSONObject("data").getString("firebase_reg_id"));
                                Log.d("info FireBase", "Firebase id is set with server.!");
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info FireBase ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info FireBase err", String.valueOf(t));
                        Log.d("info FireBase err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_changeNearByStatus(final String nearby_latitude,
                                             final String nearby_longitude, final String nearby_distance) {
        if (SettingsMain.isConnectingToInternet(this)) {


            JsonObject params = new JsonObject();
            params.addProperty("nearby_latitude", nearby_latitude);
            params.addProperty("nearby_longitude", nearby_longitude);
            params.addProperty("nearby_distance", nearby_distance);
            Log.d("info SendNearBy Status", params.toString());

            SettingsMain.showDilog(HomeActivity.this);
            Call<ResponseBody> myCall = restService.postChangeNearByStatus(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info NearBy Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                SettingsMain.hideDilog();
                                FragmentManager fm = getSupportFragmentManager();
                                Fragment fragment = fm.findFragmentByTag("FragmentCatSubNSearch");
                                Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

                                FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
                                Bundle bundle = new Bundle();
                                bundle.putString("nearby_latitude", nearby_latitude);
                                bundle.putString("nearby_longitude", nearby_longitude);
                                bundle.putString("nearby_distance", nearby_distance);

                                settingsMain.setLatitude(nearby_latitude);
                                settingsMain.setLongitude(nearby_longitude);
                                settingsMain.setDistance(nearby_distance);

                                fragment_search.setArguments(bundle);

                                if (fragment != fragment2) {
                                    replaceFragment(fragment_search, "FragmentCatSubNSearch");
                                } else {
                                    updatfrag.updatefrag(nearby_latitude, nearby_longitude, nearby_distance);
                                }

                            } else
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info FireBase ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info FireBase err", String.valueOf(t));
                        Log.d("info FireBase err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccessPermission(int code) {
        if (code == 1) {
            adforest_loctionSearch();
        }
        // check phone verification
        if (code == 2) {
            if (FragmentHome.Ad_post) {
                Intent intent = new Intent(HomeActivity.this, AddNewAdPost.class);
                startActivity(intent);
            } else {
                //Alert dialog for exit form Home screen
                AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert.setTitle(settingsMain.getAlertDialogTitle("info"));
                alert.setCancelable(false);
                alert.setMessage(FragmentHome.Verfiedmessage);
                alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                    FragmentProfile fragmentProfile = new FragmentProfile();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frameContainer, fragmentProfile).addToBackStack(null).commit();
                    dialog.dismiss();
                });
                alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
                alert.show();
            }
        }
    }


    //start fragment
//    public void startFragment(Fragment someFragment, String tag) {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentByTag(tag);
//        if (fragment == null) {
//            fragment = someFragment;
//            fm.beginTransaction()
//                    .add(R.id.frame, fragment, tag).commit();
//        }
//    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public interface UpdateFragment {
        void updatefrag(String s);

        void updatefrag(String latitude, String longitude, String distance);
    }


}