package commerce.ssuk;

import android.content.SharedPreferences;import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by princes on 24-May-17.
 */
public class AccountFrag  extends Fragment{
    private Button sign,reg;
    private TextView error;
    ProgressBar pb;
    private EditText pin,usrlogin,pswdlogin;

    private static final String pin_url = "http://192.168.43.227:8000/api/postcode/";

    private static String urlJsonArry = "http://192.168.43.227:8000/api/login/";
    public void AccountFrag(){}




    @Override
    public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
      View v=inflater.inflate(R.layout.account,container,false);
        usrlogin=(EditText)v.findViewById(R.id.edit_username0);
        pswdlogin=(EditText)v.findViewById(R.id.pswd0);
        pin=(EditText)v.findViewById(R.id.pin);
       pb=(ProgressBar)v.findViewById(R.id.progressBar);

        pb.setVisibility(View.GONE);
        error=(TextView)v.findViewById(R.id.error);
        error.setVisibility(View.GONE);


        sign=(Button)v.findViewById(R.id.sign);reg=(Button)v.findViewById(R.id.register);


        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);

                LogUser();
            }
        });



        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);


                RegisterUser();
            }
        });

try {
    SharedPreferences pref = getContext().getSharedPreferences("session", 0); // 0 - for private mode

    if (pref.getString("status", null).equals("logged")) {

        Log.e("dsdds",pref.getString("status",null)+"");
      Trans();
    }

}catch (Exception e)
{}



        return v;

    }
















    private void RegisterUser(){


        final String dest= pin.getText().toString();
        final String origin="58e32243eaf87011c22bc744";
        String urrr=pin_url+origin+"%20"+dest;
        Log.e("Urlslfn",urrr);

        Masker ob = new Masker();
        String res=ob.pincode_mask(dest,getContext());Log.e("Urlslsdfsdfsdffn",res);

        if(ob.pincode_mask(dest,getContext()).equals("false")) {


            JsonObjectRequest req = new JsonObjectRequest(urrr, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Reps", response.toString());

                            pb.setVisibility(View.GONE);


                            try {


                                if (Integer.parseInt(response.getString("value")) <= 5) {
                                    Fragment fragment;
                                    fragment = new register();
                                    Bundle data = new Bundle();
                                    data.putString("postcode", dest);
                                    fragment.setArguments(data);


                                    FragmentTransaction transaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.r, fragment);
                                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    Toast.makeText(getContext(),
                                            "Out of range of Shop",
                                            Toast.LENGTH_LONG).show();
                                    error.setVisibility(View.VISIBLE);
                                    error.setText("Out of range of Shop");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Resp", "Error: " + error.getMessage());
                    Toast.makeText(getContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(req);

        }
        else
        {
            pb.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            error.setText("Postcode not found");

        }




    }







    private void LogUser(){
        final String contact = usrlogin.getText().toString().trim();

        final String password = pswdlogin.getText().toString().trim();



        JsonObjectRequest req = new JsonObjectRequest(urlJsonArry+contact+"%20"+password,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Reps", response.toString());
                        pb.setVisibility(View.GONE);


                        try {


                            try {
                                  //delivery agnet will be navigated to different path
                                if(response.getString("agent").equals("ok"))
                                {
                                    Log.d("Check1", response.getString("contact"));
                                    TransAgent(response.getString("contact"));

                                }


                            } catch (Exception e){
                            // JSONObject userObject = new JSONObject(response.toString());
                            Log.d("Reps", response.toString());
                            if ((response.getString("name")).equals("Check username or password")) {
                                Toast.makeText(getContext(),
                                        response.getString("name"),
                                        Toast.LENGTH_LONG).show();
                                error.setVisibility(View.VISIBLE);
                                error.setText("Check username or password");
                                return;
                            } else {
                                Toast.makeText(getContext(),
                                        "Successfully Logged In",
                                        Toast.LENGTH_LONG).show();


                                SessionUpdate(response.getString("contact"),
                                        response.getString("name"), response.getString("address"), response.getString("password"));
                                AppController.Global_Contact = response.getString("contact");

                                Trans();
                            }
                        }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),
                                    "Check Username or Password",
                                    Toast.LENGTH_LONG).show(); error.setVisibility(View.VISIBLE);
                            error.setText("Check username or password");

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Resp", "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);


    }

public void SessionUpdate(String contact,String name,String address,String password)
{
    SharedPreferences pref = getContext().getSharedPreferences("session", 0); // 0 - for private mode
    SharedPreferences.Editor editor = pref.edit();
    editor.putString("status", "logged");
    editor.putString("contact", contact);
    editor.putString("address", address);
    editor.putString("name",name);editor.putString("password",password);
    editor.apply();
    Log.e("dsds",pref.getString("status",null)+"");


}








public void Trans()
{
    Fragment fragment;
    fragment=new SettingsFrag();
    Bundle data= new Bundle();
    fragment .setArguments(data);

    FragmentTransaction transaction = ((FragmentActivity)getContext()
    ).getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.r, fragment);

    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    transaction.addToBackStack("name");
    transaction.commit();



}


    public void TransAgent(String cont)
    {
        Fragment fragment;
        fragment=new DeliveryAgent();
        Bundle data= new Bundle();
        data.putString("contact",cont);
        fragment .setArguments(data);

        FragmentTransaction transaction = ((FragmentActivity)getContext()
        ).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.r, fragment);

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack("name");
        transaction.commit();



    }



}
