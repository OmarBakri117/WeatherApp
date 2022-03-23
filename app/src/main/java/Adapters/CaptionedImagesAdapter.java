package Adapters;

import static edu.cs.birzeit.weatherapp.R.drawable.*;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.cs.birzeit.weatherapp.R;
import edu.cs.birzeit.weatherapp.WeatherDay;


public class CaptionedImagesAdapter extends RecyclerView.Adapter<CaptionedImagesAdapter.ViewHolder> {

    private ArrayList<WeatherDay> daysList;

    public CaptionedImagesAdapter(ArrayList<WeatherDay> days) {
        this.daysList = new ArrayList<>(days);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_image, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //To set Image
            CardView cardView = holder.cardView;
            ImageView imageView = (ImageView) cardView.findViewById(R.id.image);
            String state = daysList.get(position).getWeather_state_name();
            if (state.equalsIgnoreCase("snow")) {
                imageView.setImageResource(ic_snow);
            } else if (state.equalsIgnoreCase("sleet")) {
                imageView.setImageResource(ic_sleet);
            } else if (state.equalsIgnoreCase("hail")) {
                imageView.setImageResource(ic_hail);
            } else if (state.equalsIgnoreCase("thunderstorm")) {
                imageView.setImageResource(ic_thund);
            } else if (state.equalsIgnoreCase("heavy rain")) {
                imageView.setImageResource(ic_heavyrain);
            } else if (state.equalsIgnoreCase("light rain")) {
                imageView.setImageResource(ic_lightrain);
            } else if (state.equalsIgnoreCase("showers")) {
                imageView.setImageResource(ic_showers);
            } else if (state.equalsIgnoreCase("heavy cloud")) {
                imageView.setImageResource(ic_heavycloud);
            } else if (state.equalsIgnoreCase("light cloud")) {
                imageView.setImageResource(ic_lightcloud);
            } else if (state.equalsIgnoreCase("clear")) {
                imageView.setImageResource(ic_clear);
            }
            //Picasso.get().load("" + personList.get(position).getPictureURL()).into(imageView);
            //To set username
            TextView txt = (TextView) cardView.findViewById(R.id.date);
            txt.setText(daysList.get(position).getApplicable_date());
            //To set Email
            TextView txtTwo = (TextView) cardView.findViewById(R.id.minTemp);
            txtTwo.setText(""+(int)Double.parseDouble(daysList.get(position).getMin_temp()));
            //To set Email
            TextView txtThree = (TextView) cardView.findViewById(R.id.maxTemp);
            txtThree.setText(""+(int)Double.parseDouble(daysList.get(position).getMax_temp()));
            TextView txtFour = (TextView) cardView.findViewById(R.id.state);
            txtFour.setText(daysList.get(position).getWeather_state_name());
            //OnClick
            cardView.setOnClickListener(v -> {

            });
        }


    @Override
    public int getItemCount() {
        return daysList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}