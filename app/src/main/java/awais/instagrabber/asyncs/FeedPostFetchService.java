package awais.instagrabber.asyncs;

import java.util.List;

import awais.instagrabber.customviews.helpers.PostFetcher;
import awais.instagrabber.interfaces.FetchListener;
import awais.instagrabber.models.FeedModel;
import awais.instagrabber.repositories.responses.FeedFetchResponse;
import awais.instagrabber.webservices.FeedService;
import awais.instagrabber.webservices.ServiceCallback;

public class FeedPostFetchService implements PostFetcher.PostFetchService {
    private static final String TAG = "FeedPostFetchService";
    private final FeedService feedService;
    private String nextCursor;
    private boolean hasNextPage;

    public FeedPostFetchService() {
        feedService = FeedService.getInstance();
    }

    @Override
    public void fetch(final String cursor, final FetchListener<List<FeedModel>> fetchListener) {
        feedService.fetch(25, cursor, new ServiceCallback<FeedFetchResponse>() {
            @Override
            public void onSuccess(final FeedFetchResponse result) {
                if (result == null) return;
                nextCursor = result.getNextCursor();
                hasNextPage = result.hasNextPage();
                if (fetchListener != null) {
                    fetchListener.onResult(result.getFeedModels());
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                // Log.e(TAG, "onFailure: ", t);
                if (fetchListener != null) {
                    fetchListener.onFailure(t);
                }
            }
        });
    }

    @Override
    public String getNextCursor() {
        return nextCursor;
    }

    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }
}
