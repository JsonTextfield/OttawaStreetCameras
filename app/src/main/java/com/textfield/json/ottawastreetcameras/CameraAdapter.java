package com.textfield.json.ottawastreetcameras;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jason on 24/10/2017.
 */

class CameraAdapter extends ArrayAdapter<Camera>{

    ArrayList<String> indexTitles = new ArrayList<>();
    HashMap<String, Integer> index = new HashMap<>();
    ArrayList<Camera> cameras;
    ArrayList<Camera> wholeCameras;
    private Context _context;

    public CameraAdapter(Context context, ArrayList<Camera> list) {
        super(context, 0, list);
        this._context = context;
        wholeCameras = cameras = list;
        for (Camera camera : cameras) {

            String c = (Locale.getDefault().getDisplayLanguage().contains("fr")) ?
                    Character.toString(camera.getNameFr().replaceAll("\\W", "").charAt(0)) :
                    Character.toString(camera.getName().replaceAll("\\W", "").charAt(0));

            if (!index.keySet().contains(c)) {
                indexTitles.add(c);
                index.put(c, cameras.indexOf(camera));
            }
        }
    }

    public ArrayList<String> getIndexTitles() {
        return indexTitles;
    }

    public HashMap<String, Integer> getIndex() {
        return index;
    }

    private static class ViewHolder {
        TextView title;
    }

    @Nullable
    @Override
    public Camera getItem(int position) {
        return cameras.get(position);
    }

    @Override
    public int getCount() {
        return cameras.size();
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        final Camera item = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(_context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.listtitle);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (Locale.getDefault().getDisplayLanguage().contains("fr")) {
            viewHolder.title.setText(item.getNameFr());
        } else {
            viewHolder.title.setText(item.getName());
        }


        // Return the completed view to render on screen
        return convertView;
    }

    @NonNull
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                cameras = (ArrayList<Camera>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                List<Camera> filteredResults = new ArrayList<>();
                    for (Camera camera : wholeCameras) {
                        if (camera.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                                || camera.getNameFr().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredResults.add(camera);
                        }

                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }
}
