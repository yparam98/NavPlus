package lonetech.mobile_application.navplus;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import map.finalproject.lonetech.R;

public class SearchResultsAdapter extends ArrayAdapter<SearchResult>
{
    private Context context;
    private List<SearchResult> searchResults;

    public SearchResultsAdapter(Context incomingContext, List<SearchResult> incomingItems)
    {
        super(incomingContext, -1, incomingItems);
        this.context = incomingContext;
        this.searchResults = incomingItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final SearchResultHolder holder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.search_result,
                    parent,
                    false
            );
            holder = new SearchResultHolder();
            holder.location_addr = convertView.findViewById(R.id.location_address);
            holder.distance = convertView.findViewById(R.id.distance_text_view);
            convertView.setTag(holder);
        }
        else
        {
            holder = (SearchResultHolder) convertView.getTag();
        }

        Log.e("T-MOBILE", searchResults.get(position).getAddress());
        holder.location_addr.setText(searchResults.get(position).getAddress());
        holder.distance.setText(String.format("%.2fkm", searchResults.get(position).getDistanceFromLocation()));

        return convertView;
    }

    private static class SearchResultHolder
    {
        public TextView location_addr;
        public TextView distance;
    }
}


