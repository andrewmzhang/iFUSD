package com.fremontunified.ifusd.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.app.Activity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsListFragment extends Fragment {
    private HandleXML mHandleXML;
    private static final String sFINALURL = "http://www.fremont.k12.ca.us/site/RSS.aspx?DomainID=1&ModuleInstanceID=4613&PageID=1";
    public static String[] sTitles;
    public static String[] sDescriptions;
    public static String[] sLinks;
    public static String[] sImages;
    private List<String> mListTitle;
    private List<String> mListDescriptions;
    private List<String> mListLinks;
    private List<String> mImages;
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;
    private RecyclerView.Adapter mAdapter;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    public NewsAdapter mNewsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), 2);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

    }

    /* @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Bundle bundle = new Bundle();
            String url = sLinks[position];
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getActivity().setTitle("News");
      //  AsyncTask taskA = new task();
      //  taskA.execute();
    }

    public class task extends AsyncTask {
        String[] test;

        @Override
        protected Object doInBackground(Object[] params) {
            fetch();
            sTitles = removeElement1(sTitles);
            sDescriptions = removeElement1(sDescriptions);
            sLinks = removeElement1(sLinks);
            sImages = removeElement1(sImages);
            return params;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            mNewsAdapter = new NewsAdapter(getActivity().getApplicationContext(),sTitles, sDescriptions, sImages);
            mRecyclerView.setAdapter(mNewsAdapter);


        }
    }


    public void fetch() {
        mHandleXML = new HandleXML(sFINALURL);
        mHandleXML.fetchXML();
        while (mHandleXML.parsingComplete) ;
        mListTitle = mHandleXML.getTitles();
        sTitles = mListTitle.toArray(new String[mListTitle.size()]);
        mListDescriptions = mHandleXML.getDescriptions();
        sDescriptions = mListDescriptions.toArray(new String[mListDescriptions.size()]);
        mListLinks = mHandleXML.getLinks();
        sLinks = mListLinks.toArray(new String[mListLinks.size()]);
        mImages = mHandleXML.getImages();
        sImages = mImages.toArray(new String[mImages.size()]);
    }

    private String[] removeElement1(String[] initial) {
        String[] finalString = new String[initial.length - 1];
        for (int n = 1; n < initial.length; n++) {
            finalString[n - 1] = initial[n];
        }
        return finalString;
    }



    private class NewsHolder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private TextView mDescription;
        private ImageView mImageView;
        private String link;

        public NewsHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.news_list_item_title);
            mDescription = (TextView) itemView.findViewById(R.id.news_list_item_des);
            mImageView = (ImageView) itemView.findViewById(R.id.news_list_item_image);
        }

        public void bindNews(String title, String des, String imgUrl, Context context) {
            mTitleView.setText(title);
            mDescription.setText(des);
          /*  if (!imgUrl.equals(""))
                Picasso.with(context)
                        .load(imgUrl)
                        .placeholder(R.drawable.photo_placeholder)
                        .into(mImageView);
            else {
                //Drawable drawable = getResources().getDrawable(R.drawable.fusd_logo);
                //imageView.setImageDrawable(drawable);
                mImageView.setVisibility(View.GONE);
            } */
        }

    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

        String[] titles;
        String[] des;
        String[] images;
        Context mContext;

        public NewsAdapter(Context context, String[] title, String[] descrip, String[] img) {
            titles = title;
            des = descrip;
            images = img;
            mContext = context;
        }

        @Override
        public NewsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item, viewGroup);
            return new NewsHolder(view);
        }

        @Override
        public void onBindViewHolder(NewsHolder newsHolder, int i) {
            newsHolder.bindNews(titles[i], des[i], images[i], mContext);
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }



    private class NewsAdapter2 extends ArrayAdapter<String> {
        private String[] mTitles;
        private String[] mDescriptions;
        private String[] mImages;

        public NewsAdapter2(String[] titles, String[] descriptions, String[] images) {
            super(getActivity(), 0, titles);
            mTitles = titles;
            mDescriptions = descriptions;
            mImages = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.news_list_item, null);
            }
            String title = mTitles[position];
            String description = mDescriptions[position];
            String image = mImages[position];
            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.news_list_item_title);
            titleTextView.setText(title);
            TextView descriptionTextView =
                    (TextView) convertView.findViewById(R.id.news_list_item_des);
            descriptionTextView.setText(description);

            ImageView imageView =
                    (ImageView) convertView.findViewById(R.id.news_list_item_image);

            if (!image.equals(""))
                Picasso.with(getContext())
                        .load(image)
                        .placeholder(R.drawable.photo_placeholder)
                        .into(imageView);
            else {
                //Drawable drawable = getResources().getDrawable(R.drawable.fusd_logo);
                //imageView.setImageDrawable(drawable);
                imageView.setVisibility(View.GONE);
            }

            System.out.println(title + description + image);

            return convertView;
        }
    }
}
