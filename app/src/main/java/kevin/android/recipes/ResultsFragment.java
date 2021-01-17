package kevin.android.recipes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SEND_JSON_STRING = "send json string";
    private RecyclerView resultsRecyclerView;
    private RecipeAdapter resultsAdapter;
    private RecyclerView.LayoutManager resultsLayoutManager;

    public ResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        resultsRecyclerView = view.findViewById(R.id.results_recycler_view);
        resultsAdapter = new RecipeAdapter(getContext());
        resultsLayoutManager = new LinearLayoutManager(getActivity());

        resultsRecyclerView.setAdapter(resultsAdapter);
        resultsRecyclerView.setLayoutManager(resultsLayoutManager);

        if (getArguments() != null) {
            String jsonString = getArguments().getString(SEND_JSON_STRING);
            try {
                JSONObject entire = new JSONObject(jsonString);
                JSONArray results = entire.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject j = results.getJSONObject(i);
                    int id = j.getInt("id");
                    String title = j.getString("title");
                    String imageUrl = j.getString("image");
                    Recipe r = new Recipe(id, title, imageUrl);
                    resultsAdapter.recipes.add(r);
                    resultsAdapter.reload();
                }
            } catch (JSONException e) {
                Log.e("JSONException", "failed to generate jsonArray from jsonString");
            }

        }
        return view;
    }
}