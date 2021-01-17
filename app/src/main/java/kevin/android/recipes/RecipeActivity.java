package kevin.android.recipes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {
    private TextView title;
    private TextView ingredients;
    private Button button;
    private TextView steps;
    private ImageView image;
    private RequestQueue requestQueue;

    private String recipeTitle;
    private int recipeId;
    private String recipeImageUrl;
    private String buttonStatus;

//    private SharedPreferences sharedPreferences;
//    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        title = findViewById(R.id.title);
        ingredients = findViewById(R.id.ingredients);
        button = findViewById(R.id.save);
        steps = findViewById(R.id.steps);
        image = findViewById(R.id.image);
        ingredients.setMovementMethod(new ScrollingMovementMethod());
        steps.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        recipeId = intent.getIntExtra("id", 0);
        recipeTitle = intent.getStringExtra("title");
        setTitle(recipeTitle);
        recipeImageUrl = intent.getStringExtra("imageUrl");
        Picasso.get().load(recipeImageUrl).into(image);
        boolean saved = intent.getBooleanExtra("saved", false);
        if (saved) {
            buttonStatus = "DELETE";
        } else {
            buttonStatus = "SAVE";
        }
        button.setText(buttonStatus);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

//        sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();

//        buttonStatus = (sharedPreferences.getString(recipeTitle, null));
//        if (buttonStatus == null) {
//            Log.e("saved", "no buttonstatus saved");
//        } else {
//            Log.e("saved", "loaded button status" + buttonStatus);
//            button.setText(buttonStatus);
//        }

        String url = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=56b52cad1fed434eb2ce0eb1af4ce92d&includeNutrition=false";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            // automatically called when Json object is returned
            public void onResponse(JSONObject response) {
                String ingredientList = "";
                String stepsList = "";
                // parse the data
                String jsonString = response.toString();

                try {
                    JSONObject entire = new JSONObject(jsonString);
                    JSONArray ingredients = entire.getJSONArray("extendedIngredients");
                    JSONArray stepsArray = entire.getJSONArray("analyzedInstructions");

                    for (int i = 0; i < ingredients.length(); i++) {
                        JSONObject j = ingredients.getJSONObject(i);
                        String theIngredient = j.getString("original");
                        ingredientList += theIngredient + "\n";
                    }

                    JSONObject firstObject = stepsArray.getJSONObject(0);
                    JSONArray steps = firstObject.getJSONArray("steps");
                    for (int i = 0; i < steps.length(); i++) {
                        String theStep = steps.getJSONObject(i).getString("step");
                        stepsList += (i + 1) + ": " + theStep + "\n";
                    }

                } catch (JSONException e) {
                    Log.e("JSONException", "failed to generate jsonArray from jsonString");
                }

                ingredients.setText(ingredientList);
                steps.setText(stepsList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", "error");
            }
        });
        requestQueue.add(request);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("action", buttonStatus);
                if (buttonStatus.equals("SAVE")) {
                    buttonStatus = "DELETE";
                } else if (buttonStatus.equals("DELETE")) {
                    buttonStatus = "SAVE";
                }
                button.setText(buttonStatus);
                resultIntent.putExtra("id", recipeId);
                resultIntent.putExtra("title", recipeTitle);
                resultIntent.putExtra("imageUrl", recipeImageUrl);
                setResult(RESULT_OK, resultIntent);
                //finish();
            }
        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
////        SharedPreferences sharedPreferences = getSharedPreferences("save", Context.MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
////        Log.e("Status", "saving button status " + buttonStatus);
//        editor.putString(recipeTitle, buttonStatus);
//        Log.e("saved", "saved button status " + buttonStatus);
//    }
}