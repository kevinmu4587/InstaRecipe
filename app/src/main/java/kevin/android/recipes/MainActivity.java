package kevin.android.recipes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RequestQueue requestQueue;
    private TextView introMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new RecipeAdapter(MainActivity.this);
        layoutManager = new LinearLayoutManager(this);
        introMessage = findViewById(R.id.intro_message);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        requestQueue = Volley.newRequestQueue(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                searchView.clearFocus();
                //String jsonString = "{\"results\":[{\"id\":654959,\"title\":\"Pasta With Tuna\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654959-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":511728,\"title\":\"Pasta Margherita\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/511728-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654812,\"title\":\"Pasta and Seafood\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654812-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654857,\"title\":\"Pasta On The Border\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654857-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654883,\"title\":\"Pasta Vegetable Soup\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654883-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654928,\"title\":\"Pasta With Italian Sausage\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654928-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654926,\"title\":\"Pasta With Gorgonzola Sauce\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654926-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654944,\"title\":\"Pasta With Salmon Cream Sauce\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654944-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654905,\"title\":\"Pasta With Chickpeas and Kale\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654905-312x231.jpg\",\"imageType\":\"jpg\"},{\"id\":654901,\"title\":\"Pasta With Chicken and Broccoli\",\"image\":\"https:\\/\\/spoonacular.com\\/recipeImages\\/654901-312x231.jpg\",\"imageType\":\"jpg\"}],\"offset\":0,\"number\":10,\"totalResults\":210}";
                String url = "https://api.spoonacular.com/recipes/complexSearch?apiKey=56b52cad1fed434eb2ce0eb1af4ce92d&query=" + newText;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    // automatically called when Json object is returned
                    public void onResponse(JSONObject response) {
                        Log.e("response", "got a response");
                        // parse the data
                        String jsonString = response.toString();
                        openFragment(jsonString);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "error");
                    }
                });
                requestQueue.add(request);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void openFragment(String jsonString) {
        Log.e("Bug", "opening fragment, passing " + jsonString);
        Fragment info = new ResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ResultsFragment.SEND_JSON_STRING, jsonString);
        info.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .replace(R.id.result_fragment_container, info)
                .addToBackStack(null)
                .commit();
        introMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("activity result", "in onactivityresutsl");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String action = data.getStringExtra("action");
            int id = data.getIntExtra("id", -1);
            String title = data.getStringExtra("title");
            String imageUrl = data.getStringExtra("imageUrl");
            Recipe add = new Recipe(id, title, imageUrl);
            if (action.equals("SAVE")) {
                adapter.recipes.add(add);
                add.setSaved(true);
                adapter.reload();
            } else if (action.equals("DELETE")) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    Recipe r = adapter.recipes.get(i);
                    if (id == r.getId()) {
                        adapter.recipes.remove(r);
                        adapter.reload();
                        break;
                    }
                }
            }
        }
    }
}