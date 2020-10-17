package awais.instagrabber.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import awais.instagrabber.adapters.viewholder.SliderItemViewHolder;
import awais.instagrabber.adapters.viewholder.SliderPhotoViewHolder;
import awais.instagrabber.adapters.viewholder.SliderVideoViewHolder;
import awais.instagrabber.customviews.VerticalDragHelper;
import awais.instagrabber.databinding.ItemSliderPhotoBinding;
import awais.instagrabber.databinding.LayoutExoCustomControlsBinding;
import awais.instagrabber.databinding.LayoutVideoPlayerWithThumbnailBinding;
import awais.instagrabber.models.PostChild;
import awais.instagrabber.models.enums.MediaItemType;

public final class SliderItemsAdapter extends ListAdapter<PostChild, SliderItemViewHolder> {

    private final VerticalDragHelper.OnVerticalDragListener onVerticalDragListener;
    private final SliderCallback sliderCallback;
    private final awais.instagrabber.databinding.LayoutExoCustomControlsBinding controlsBinding;

    private static final DiffUtil.ItemCallback<PostChild> DIFF_CALLBACK = new DiffUtil.ItemCallback<PostChild>() {
        @Override
        public boolean areItemsTheSame(@NonNull final PostChild oldItem, @NonNull final PostChild newItem) {
            return oldItem.getPostId().equals(newItem.getPostId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull final PostChild oldItem, @NonNull final PostChild newItem) {
            return oldItem.getPostId().equals(newItem.getPostId());
        }
    };

    public SliderItemsAdapter() {
        this(null, null, null);
    }

    public SliderItemsAdapter(final VerticalDragHelper.OnVerticalDragListener onVerticalDragListener,
                              final LayoutExoCustomControlsBinding controlsBinding,
                              final SliderCallback sliderCallback) {
        super(DIFF_CALLBACK);
        this.onVerticalDragListener = onVerticalDragListener;
        this.sliderCallback = sliderCallback;
        this.controlsBinding = controlsBinding;
    }

    @NonNull
    @Override
    public SliderItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final MediaItemType mediaItemType = MediaItemType.valueOf(viewType);
        switch (mediaItemType) {
            case MEDIA_TYPE_VIDEO: {
                final LayoutVideoPlayerWithThumbnailBinding binding = LayoutVideoPlayerWithThumbnailBinding.inflate(inflater, parent, false);
                return new SliderVideoViewHolder(binding, onVerticalDragListener, controlsBinding);
            }
            case MEDIA_TYPE_IMAGE:
            default:
                final ItemSliderPhotoBinding binding = ItemSliderPhotoBinding.inflate(inflater, parent, false);
                return new SliderPhotoViewHolder(binding, onVerticalDragListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final SliderItemViewHolder holder, final int position) {
        final PostChild model = getItem(position);
        holder.bind(model, position, sliderCallback);
    }

    @Override
    public int getItemViewType(final int position) {
        final PostChild viewerPostModel = getItem(position);
        return viewerPostModel.getItemType().getId();
    }

    // @NonNull
    // @Override
    // public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
    //     final Context context = container.getContext();
    //     final ViewerPostModel sliderItem = sliderItems.get(position);
    //
    //     if (sliderItem.getItemType() == MediaItemType.MEDIA_TYPE_VIDEO) {
    //         final ViewSwitcher viewSwitcher = createViewSwitcher(context, position, sliderItem.getThumbnailUrl(), sliderItem.getDisplayUrl());
    //         container.addView(viewSwitcher);
    //         return viewSwitcher;
    //     }
    //     final GenericDraweeHierarchy hierarchy = GenericDraweeHierarchyBuilder.newInstance(container.getResources())
    //                                                                           .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
    //                                                                           .build();
    //     final SimpleDraweeView photoView = new SimpleDraweeView(context, hierarchy);
    //     photoView.setLayoutParams(layoutParams);
    //     final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(sliderItem.getDisplayUrl()))
    //                                                          .setLocalThumbnailPreviewsEnabled(true)
    //                                                          .setProgressiveRenderingEnabled(true)
    //                                                          .build();
    //     photoView.setImageRequest(imageRequest);
    //     container.addView(photoView);
    //     return photoView;
    // }

    // @NonNull
    // private ViewSwitcher createViewSwitcher(final Context context,
    //                                         final int position,
    //                                         final String thumbnailUrl,
    //                                         final String displayUrl) {
    //
    //     final ViewSwitcher viewSwitcher = new ViewSwitcher(context);
    //     viewSwitcher.setLayoutParams(layoutParams);
    //
    //     final FrameLayout frameLayout = new FrameLayout(context);
    //     frameLayout.setLayoutParams(layoutParams);
    //
    //     final GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
    //             .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
    //             .build();
    //     final SimpleDraweeView simpleDraweeView = new SimpleDraweeView(context, hierarchy);
    //     simpleDraweeView.setLayoutParams(layoutParams);
    //     simpleDraweeView.setImageURI(thumbnailUrl);
    //     frameLayout.addView(simpleDraweeView);
    //
    //     final AppCompatImageView imageView = new AppCompatImageView(context);
    //     final int px = Utils.convertDpToPx(50);
    //     final FrameLayout.LayoutParams playButtonLayoutParams = new FrameLayout.LayoutParams(px, px);
    //     playButtonLayoutParams.gravity = Gravity.CENTER;
    //     imageView.setLayoutParams(playButtonLayoutParams);
    //     imageView.setImageResource(R.drawable.exo_icon_play);
    //     frameLayout.addView(imageView);
    //
    //     viewSwitcher.addView(frameLayout);
    //
    //     final PlayerView playerView = new PlayerView(context);
    //     viewSwitcher.addView(playerView);
    //     if (shouldAutoPlay && position == 0) {
    //         loadPlayer(context, position, displayUrl, viewSwitcher, factory, playerChangeListener);
    //     } else
    //         frameLayout.setOnClickListener(v -> loadPlayer(context, position, displayUrl, viewSwitcher, factory, playerChangeListener));
    //     return viewSwitcher;
    // }

    public interface SliderCallback {
        void onThumbnailLoaded(int position);

        void onItemClicked(int position);

        void onPlayerPlay(int position);

        void onPlayerPause(int position);
    }
}
