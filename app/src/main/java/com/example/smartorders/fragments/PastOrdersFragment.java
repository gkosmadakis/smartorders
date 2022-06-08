package com.example.smartorders.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartorders.R;
import com.example.smartorders.adapters.PastOrderItemsAdapter;
import com.example.smartorders.interfaces.OnGetDataListenerPastOrders;
import com.example.smartorders.service.OrdersService;
import com.example.smartorders.service.OrdersServiceImpl;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Map;


public class PastOrdersFragment extends Fragment {
    private RecyclerView recyclerViewPastOrders;
    private  ProgressDialog mProgressDialog;
    private final OrdersService ordersService = new OrdersServiceImpl();

    public PastOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.past_orders_fragment, container, false);
        recyclerViewPastOrders = rootView.findViewById(R.id.recycleViewPastOrders);
        getPastOrderFromFirebase(new OnGetDataListenerPastOrders() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setMessage("Loading");
                    mProgressDialog.setIndeterminate(true);
                }
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data, List<PastOrderItemsListHelper> pastOrderItemsComponents) {
                //DO SOME THING WHEN GET DATA SUCCESS HERE
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    System.out.println("OnSuccess Called ");
                    PastOrderItemsAdapter adapterAddress = new PastOrderItemsAdapter(getContext(), pastOrderItemsComponents);
                    recyclerViewPastOrders.setAdapter(adapterAddress);
                    recyclerViewPastOrders.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
                Log.e("PastOrdersFragment ","Error while getting data from Firebase "+databaseError);
            }
        });

        /*ArrayList<PastOrderItemsListHelper> pastOrderItemsComponents = new ArrayList<>();
        ArrayList<PastOrderListOfItemsAdapter.PastOrderItemModel> pastOrderItemsList = new ArrayList<>();
        PastOrderListOfItemsAdapter.PastOrderItemModel test = new PastOrderListOfItemsAdapter.PastOrderItemModel("test");
        PastOrderListOfItemsAdapter.PastOrderItemModel test2 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test2");
        PastOrderListOfItemsAdapter.PastOrderItemModel test3 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test3");
        PastOrderListOfItemsAdapter.PastOrderItemModel test4 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test4");
        PastOrderListOfItemsAdapter.PastOrderItemModel test5 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test5");
        PastOrderListOfItemsAdapter.PastOrderItemModel test6 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test6");
        PastOrderListOfItemsAdapter.PastOrderItemModel test7 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test7");
        PastOrderListOfItemsAdapter.PastOrderItemModel test8 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test8");
        PastOrderListOfItemsAdapter.PastOrderItemModel test9 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test9");
        PastOrderListOfItemsAdapter.PastOrderItemModel test10 = new PastOrderListOfItemsAdapter.PastOrderItemModel("test10");

        pastOrderItemsList.add(test);
        pastOrderItemsList.add(test2);
        pastOrderItemsList.add(test3);
        pastOrderItemsList.add(test4);
        pastOrderItemsList.add(test5);
        pastOrderItemsList.add(test6);
        pastOrderItemsList.add(test7);
        pastOrderItemsList.add(test8);
        pastOrderItemsList.add(test9);
        pastOrderItemsList.add(test10);

        PastOrderItemsListHelper testObject = new PastOrderItemsListHelper("Your delivery by..","£3.05",
                R.drawable.greek_artisan_pastries_photo,"Order completed","12345", pastOrderItemsList);

        ArrayList<PastOrderListOfItemsAdapter.PastOrderItemModel> pastOrderItemsList2 = new ArrayList<>();
        pastOrderItemsList2.add(test2);
        pastOrderItemsList2.add(test3);
        pastOrderItemsList2.add(test4);
        pastOrderItemsList2.add(test5);
        pastOrderItemsList2.add(test6);
        pastOrderItemsList2.add(test7);

        PastOrderItemsListHelper testObject2 = new PastOrderItemsListHelper("Your delivery by..","£9.05",
                R.drawable.greek_artisan_pastries_photo,"Order completed","54321", pastOrderItemsList2);

        pastOrderItemsComponents.add(testObject);
        pastOrderItemsComponents.add(testObject2);

        PastOrderItemsAdapter adapterAddress = new PastOrderItemsAdapter(getContext(), pastOrderItemsComponents);
        recyclerViewPastOrders.setAdapter(adapterAddress);
        recyclerViewPastOrders.setLayoutManager(new LinearLayoutManager(getContext()));
*/
        // Inflate the layout for this fragment
        return rootView;
    }

    private void getPastOrderFromFirebase(final OnGetDataListenerPastOrders listener) {
        ordersService.retrievePastOrderFromFirebase(listener);
    }

    public static class PastOrderItemsListHelper {
        private  String deliveryTitle;
        private String totalPrice;
        private int imageView;
        private String orderCompletedTitle;
        private String orderId;
        private Map<String, String> pastOrderItemsNameToQuantity;

        public PastOrderItemsListHelper(String deliveryTitle, String totalPrice, int imageView, String orderCompletedTitle, String orderId, Map pastOrderItemsNameToQuantity) {
            this.deliveryTitle = deliveryTitle;
            this.totalPrice = totalPrice;
            this.imageView = imageView;
            this.orderCompletedTitle = orderCompletedTitle;
            this.orderId = orderId;
            this.pastOrderItemsNameToQuantity = pastOrderItemsNameToQuantity;
        }

        public String getDeliveryTitle() {
            return deliveryTitle;
        }

        public void setDeliveryTitle(String deliveryTitle) {
            this.deliveryTitle = deliveryTitle;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public int getImageView() {
            return imageView;
        }

        public void setImageView(int imageView) {
            this.imageView = imageView;
        }

        public String getOrderCompletedTitle() {
            return orderCompletedTitle;
        }

        public void setOrderCompletedTitle(String orderCompletedTitle) {
            this.orderCompletedTitle = orderCompletedTitle;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public Map<String, String> getPastOrderItemsNameToQuantity() {
            return pastOrderItemsNameToQuantity;
        }

        public void setPastOrderItemsNameToQuantity(Map<String, String> pastOrderItemsNameToQuantity) {
            this.pastOrderItemsNameToQuantity = pastOrderItemsNameToQuantity;
        }
    }

}
