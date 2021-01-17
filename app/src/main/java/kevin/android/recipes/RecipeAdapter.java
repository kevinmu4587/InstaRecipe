package kevin.android.recipes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public ArrayList<Recipe> recipes;
    private TextView title;
    private ImageView image;
    private Context context;
    //private RequestQueue requestQueue;

    public RecipeAdapter(Context context) {
        this.context = context;
        recipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        title = view.findViewById(R.id.recipe_title);
        image = view.findViewById(R.id.recipe_image);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe current = recipes.get(position);
        title.setText(current.getTitle());
        Picasso.get().load(current.getImageUrl()).into(image);
        holder.recipeContainer.setTag(current);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void reload() {
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout recipeContainer;
        public TextView recipeTitle;

        RecipeViewHolder(View view) {
            super(view);
            recipeContainer = view.findViewById(R.id.recipe_container);
            recipeTitle = view.findViewById(R.id.recipe_title);

            //set on click listener
            recipeContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Recipe current = (Recipe) recipeContainer.getTag();
                    Intent intent = new Intent(view.getContext(), RecipeActivity.class);

                    intent.putExtra("title", current.getTitle());
                    Log.e("sending", "sending id of " + current.getId());
                    intent.putExtra("id", current.getId());
                    intent.putExtra("imageUrl", current.getImageUrl());
                    intent.putExtra("saved", current.isSaved());
                    ((AppCompatActivity) context).startActivityForResult(intent, 1);
                }
            });
        }
    }
}
