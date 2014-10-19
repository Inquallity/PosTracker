package com.example.inquallity.postracker.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.inquallity.postracker.R;
import com.example.inquallity.postracker.sqlite.TrackSql;

public class TrackCollect extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private TextView mTextLoc;
    private Button mBtnClear;
    private Button mBtnShow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_collecting_loc, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnShow = (Button) view.findViewById(R.id.btn_show);
        mBtnClear = (Button) view.findViewById(R.id.btn_clear);
        mTextLoc = (TextView) view.findViewById(R.id.loc_text);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBtnShow.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
    }


    @Override
    public void onStop() {
        mBtnShow.setOnClickListener(null);
        mBtnClear.setOnClickListener(null);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnShow) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame, new TrackMap())
                    .addToBackStack(null)
                    .commit();
        } else if (v == mBtnClear) {
            mTextLoc.setText("");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, Bundle.EMPTY, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity().getApplicationContext(), TrackSql.URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mTextLoc.setText("");
            do {
                mTextLoc.append(cursor.getString(cursor.getColumnIndex("position")) + " --- " +
                        cursor.getString(cursor.getColumnIndex("latitude")) + "," + cursor.getString(cursor.getColumnIndex("longitude")) + "\n");


            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
