package com.example.man_zone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.man_zone.Model.SliderModel;
import com.example.man_zone.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderModel> sliderItems;
    private ViewPager2 viewPager2;

    public SliderAdapter(List<SliderModel> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_image_container, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        SliderModel sliderItem = sliderItems.get(position);
        holder.imageView.setImageResource(sliderItem.getImageResId());
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        SliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSilde);
        }
    }
}
