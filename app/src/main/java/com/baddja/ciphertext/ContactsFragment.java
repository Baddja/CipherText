package com.baddja.ciphertext;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int CONTACTS_LOADER = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView contactsList;
    private ContactsListAdapter contactsListAdapter;

    private FrameLayout contactsFrame;

    private EditText number;
    private EditText message;

    private String selectionClause;
    private String[] selectionArgs = {null,null};

    private boolean filled = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        contactsFrame = (FrameLayout)getActivity().findViewById(R.id.contacts_frame);

        number = (EditText)getActivity().findViewById(R.id.cipher_number);
        message = (EditText)getActivity().findViewById(R.id.cipher_message);

        selectionClause = ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE + "'";// and " + ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";

        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);

        number.addTextChangedListener(this);
        number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    contactsFrame.setVisibility(View.GONE);
                    message.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsList = (ListView)view.findViewById(R.id.contacts_list);

        contactsListAdapter = new ContactsListAdapter(getActivity(),null);

        contactsList.setAdapter(contactsListAdapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView contact_number = (TextView)view.findViewById(R.id.contact_number);

                number.setText(contact_number.getText());
                filled = true;
                contactsFrame.setVisibility(View.GONE);
                message.requestFocus();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle)
    {
    /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loaderID) {
            case CONTACTS_LOADER:
                // Returns a new CursorLoader
                String partial = number.getText().toString();

                Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(partial));

                Log.e("CONTACTS","Loader Starting");
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        uri,        // Table to query
                        null,               // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.e("CONTACTS","Loader Finished");

        if(cursor != null && cursor.getCount()>0 && !filled){
            contactsFrame.setVisibility(View.VISIBLE);
            contactsFrame.bringToFront();
            Log.e("CONTACTS","Making Visible");
        }else{
            contactsFrame.setVisibility(View.GONE);
            Log.e("CONTACTS","COUNT 0");
        }

        contactsListAdapter.changeCursor(cursor);
        contactsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        //smsListAdapter.changeCursor(null);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(getLoaderManager().getLoader(CONTACTS_LOADER) != null) {
            Log.e("CONTACTS","Restarting Loader");
            getLoaderManager().restartLoader(CONTACTS_LOADER, null, this);
        }else{
            Log.e("CONTACTS","Starting Loader");
            getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
        }
        filled = false;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
