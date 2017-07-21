package company.matek3022.personalvkchat.fragments;//package com.example.app.fragments;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.example.app.R;
//import com.example.app.transformation.CircularTransformation;
//import com.example.app.vkobjects.User;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FriendPageFragment extends Fragment {
//    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
//    static final String ARGUMENT_PAGE_ARRAY = "arg_page_array";
//    int pageNumber;
//    public ArrayList<User> users;
//    private RecyclerView recyclerView;
//
//    public static FriendPageFragment newInstance(int page, String info) {
//
//        FriendPageFragment pageFragment = new FriendPageFragment();
//        Bundle arguments = new Bundle();
//        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
//        arguments.putString(ARGUMENT_PAGE_ARRAY, info);
//        pageFragment.setArguments(arguments);
//        return pageFragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        users = new Gson().fromJson(getArguments().getString(ARGUMENT_PAGE_ARRAY), new TypeToken<List<User>>() {
//        }.getType());
//
//        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
//        if (pageNumber == 1) {
//            ArrayList<User> onlineUsers = new ArrayList<>();
//            for (int i = 0; i < users.size(); i++) {
//                if (users.get(i).getOnline() == 1) {
//                    onlineUsers.add(users.get(i));
//                }
//            }
//            users = onlineUsers;
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_list_friend, null);
//
//        recyclerView = (RecyclerView) view.findViewById(R.id.list);
//        final Adapter adapter = new Adapter();
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(llm);
//        recyclerView.setAdapter(adapter);
//        if (pageNumber == 0) {
//            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.container);
//            EditText editText = new EditText(getContext());
//            editText.setHint(getString(R.string.FILTER));
//            editText.setTag("newEditText");
//            linearLayout.addView(editText);
//            final ArrayList<User> usersFinal = new ArrayList<>();
//            for (int i=0 ; i<users.size();i++){
//                usersFinal.add(users.get(i));
//            }
//            editText.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (!s.equals("")) {
//                        users.clear();
//                        for (int i = 0; i < usersFinal.size(); i++) {
//                            String name = usersFinal.get(i).getFirst_name() + " " + usersFinal.get(i).getLast_name();
//                            if (name.contains(s)) {
//                                users.add(usersFinal.get(i));
//                            }
//                        }
//                    }else {
//                        users.clear();
//                        for (int i=0 ; i<usersFinal.size();i++){
//                            users.add(usersFinal.get(i));
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            });
//        }
//        return view;
//    }
//
//    @Override
//    public void onStop() {
//        if (pageNumber==0){
//            ((EditText) getView().findViewWithTag("newEditText")).setText("");
//        }
//        super.onStop();
//    }
//
//    private class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView photo;
//        ImageView online;
//        TextView name;
//        TextView someInformation;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            photo = (ImageView) itemView.findViewById(R.id.image);
//            online = (ImageView) itemView.findViewById(R.id.image2);
//            name = (TextView) itemView.findViewById(R.id.text);
//            someInformation = (TextView) itemView.findViewById(R.id.text2);
//
//        }
//    }
//
//    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new ViewHolder(View.inflate(getContext(), R.layout.item_friend_list, null));
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, int position) {
//            final User user = users.get(position);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    startActivity(UserActivity.getIntent(getContext(),user.getId(),new Gson().toJson(user)));
//                }
//            });
//
//            holder.name.setText(user.getFirst_name() + " " + user.getLast_name());
//            if (user.getCity() != (null)) {
//                holder.someInformation.setText(user.getCity().getTitle());
//            } else {
//                holder.someInformation.setText("");
//            }
//            if (user.getPhoto_200().equals("")) {
//                Picasso.with(getContext())
//                        .load(R.drawable.soviet200)
//                        .transform(new CircularTransformation())
//                        .into(holder.photo);
//            } else {
//                Log.i("photo", user.getPhoto_200());
//                Picasso.with(getContext())
//                        .load(user.getPhoto_200())
//                        .transform(new CircularTransformation())
//                        .into(holder.photo);
//            }
//            if (user.getOnline() == 1) {
//                holder.online.setVisibility(View.VISIBLE);
//            } else {
//                holder.online.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return users.size();
//        }
//    }
//}
