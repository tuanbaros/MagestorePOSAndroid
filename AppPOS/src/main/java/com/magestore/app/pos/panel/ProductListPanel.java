package com.magestore.app.pos.panel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.magestore.app.lib.controller.Controller;
import com.magestore.app.lib.controller.ControllerListener;
import com.magestore.app.lib.entity.Product;
import com.magestore.app.pos.R;
import com.magestore.app.pos.controller.LoadProductController;
import com.magestore.app.pos.controller.LoadProductImageController;
import com.magestore.app.util.ConfigUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Giao diện quản lý danh mục sản phẩm
 * Created by Mike on 12/30/2016.
 * Magestore
 * mike@trueplus.vn
 */
public class ProductListPanel extends FrameLayout {
    // View quản lý danh sách khách hàng
    RecyclerView mProductListRecyclerView;

    // Textbox search product
    AutoCompleteTextView mSearchProductTxt;

    // Task load danh sách khách hàng
    LoadProductController mLoadProductTask;
    LoadProductImageController mLoadImageTask;

    // Data Danh sách khách hàng
    List<Product> mListProduct;

    // Bắt các sự kiện
    ProductListPanelListener mProductListPanelListener;

    public ProductListPanel(Context context) {
        super(context);
        init();
    }

    public ProductListPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProductListPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initControlLayout();
        initControlValue();
        initTask();
    }

    /**
     * Đặt sự kiện xử lý khi load Product
     * @param productListPanelListener
     */
    public void setListener(ProductListPanelListener productListPanelListener) {
        mProductListPanelListener = productListPanelListener;
    }

    /**
     * Đặt số cột sản phẩm
     * @param column
     */
    public void setColumn(int column) {
        ((GridLayoutManager)(mProductListRecyclerView.getLayoutManager())).setSpanCount(column);
    }

    /**
     * Chuẩn bị layout control
     */
    protected void initControlLayout() {
        // Load layout view danh sách khách hàng
        View v = inflate(getContext(), R.layout.panel_product_list, null);
        addView(v);

        // Chuẩn bị list danh sách khách hàng
        mProductListRecyclerView = (RecyclerView) findViewById(R.id.product_list);
        mProductListRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 1));

        mSearchProductTxt = (AutoCompleteTextView) findViewById(R.id.text_search_product);
        int layoutItemId = android.R.layout.simple_dropdown_item_1line;
        String[] dogArr = getResources().getStringArray(R.array.dogs_list);
        List<String> dogList = Arrays.asList(dogArr);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), layoutItemId, dogList);
        mSearchProductTxt.setAdapter(adapter);
    }

    /**
     * Chuẩn bị layout giá trị
     */
    protected void initControlValue() {

    }

    /**
     * Chuẩn bị layout
     */
    protected void initTask() {
//        super.initTask();
        mLoadProductTask = new LoadProductController(new ProductListPanel.LoadProductListener());
    }

    /**
     * Hiển thị tiến trình
     *
     * @param show
     */
    public void showProgress(boolean show) {

    }

    /**
     * Trả lại danh sách khách hàng
     *
     * @return
     */
    public List<Product> getProductList() {
        return mListProduct;
    }

    /**
     * Đặt danh sách khách hàng
     *
     * @param listProduct
     */
    public void setProductList(List<Product> listProduct) {
        mListProduct = listProduct;
    }

    /**
     * Load danh sách sản phẩm
     */
    public void loadProductList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
        {
            mLoadProductTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else // Below Api Level 13
        {
            mLoadProductTask.execute();
        }
    }

    /**
     * Load ảnh các sản phẩm
     */
    public void loadProductImage() {
        mLoadImageTask = new LoadProductImageController(new ProductListPanel.LoadProductImageListener(), mListProduct);
        if (mLoadImageTask != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                mLoadImageTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else // Below Api Level 13
            {
                mLoadImageTask.execute();
            }
    }

    /**
     * Xử lý các sự kiện khi load danh sách product lần đầu
     */
    public class LoadProductListener implements ControllerListener<Void, Void, List<Product>> {
        @Override
        public void onPreController(Controller controller) {
            showProgress(true);
        }

        @Override
        public void onPostController(Controller controller, List<Product> productList) {
            // Tất progress đi
            mLoadProductTask = null;
            showProgress(false);

            // Hiển thị danh sách product
            setProductList(productList);
            if (mListProduct != null)
                mProductListRecyclerView.setAdapter(new ProductListPanel.ProductListRecyclerViewAdapter(mListProduct));

            // Tải ảnh và hiển thị luôn
            loadProductImage();
        }

        @Override
        public void onCancelController(Controller controller, Exception exp) {
            mLoadProductTask = null;
            showProgress(false);
        }

        @Override
        public void onProgressController(Controller controller, Void... progress) {

        }
    }

    /**
     * Bắt các sự kiện load ảnh
     */
    public class LoadProductImageListener implements ControllerListener<Void, Product, Void> {

        @Override
        public void onPreController(Controller controller) {

        }

        @Override
        public void onPostController(Controller controller, Void aVoid) {

        }

        @Override
        public void onCancelController(Controller controller, Exception exp) {

        }

        @Override
        public void onProgressController(Controller controller, Product... progress) {

        }
    }

    /**
     * Chuyển đổi dataset product sang danh mục product
     */
    public class ProductListRecyclerViewAdapter
            extends RecyclerView.Adapter<ProductListRecyclerViewAdapter.ProductListViewHolder> {
        // đánh dấu vị trí đã chọn
        private int selectedPos = 0;

        // Danh sách product
        private final List<Product> mListProduct;

        /**
         * Khởi tạo với danh sách product
         * @param productList
         */
        public ProductListRecyclerViewAdapter(List<Product> productList) {
            mListProduct = productList;
        }

        /**
         * Tạo view cho từng product trong danh sách
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_product_list_content, parent, false);
            return new ProductListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ProductListViewHolder holder, final int position) {
            Product product = mListProduct.get(position);
            holder.mItem = product;

            // Giữ tham chiếu imageview theo product
            product.setRefer(LoadProductImageController.KEY_IMAGEVIEW, holder.mImageView);

            // Đặt các trường text vào danh sách
            holder.mNameView.setText(product.getName());
            holder.mPriceView.setText(ConfigUtil.formatPrice(product.getPrice()));
            holder.mSKUView.setText(product.getSKU());
            holder.mAvaibleView.setText(R.string.avaibles);

            // highlight vị trí đã chọn
            holder.itemView.setSelected(selectedPos == position);

            // Gán ảnh cho view
            Bitmap bmp = product.getBitmap();
            if (bmp != null && !bmp.isRecycled()) holder.mImageView.setImageBitmap(bmp);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update highlight sản phẩm đã chọn
                    notifyItemChanged(selectedPos);
                    selectedPos = position;
                    notifyItemChanged(selectedPos);

                    // gập nhật số lượng trên đơn hàng
                    Product product = mListProduct.get(position);
                    if (mProductListPanelListener != null)
                        mProductListPanelListener.onSelectProduct(product);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mListProduct.size();
        }

        public class ProductListViewHolder extends RecyclerView.ViewHolder {
            public final TextView mNameView;
            public final ImageView mImageView;
            public final TextView mPriceView;
            public final TextView mAvaibleView;
            public final TextView mSKUView;
            public Product mItem;
            public View mView;

            public ProductListViewHolder(View view) {
                super(view);
                mImageView = (ImageView) view.findViewById(R.id.product_image);
                mNameView = (TextView) view.findViewById(R.id.name);
                mSKUView = (TextView) view.findViewById(R.id.sku);
                mPriceView = (TextView) view.findViewById(R.id.price);
                mAvaibleView = (TextView) view.findViewById(R.id.avaiable);
                mView = view.findViewById(R.id.sales_product_list_card_view);
            }
        }
    }
}
