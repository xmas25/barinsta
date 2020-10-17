package awais.instagrabber.directdownload;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.Collections;

import awais.instagrabber.R;
import awais.instagrabber.asyncs.PostFetcher;
import awais.instagrabber.interfaces.FetchListener;
import awais.instagrabber.models.FeedModel;
import awais.instagrabber.models.IntentModel;
import awais.instagrabber.models.enums.DownloadMethod;
import awais.instagrabber.models.enums.IntentModelType;
import awais.instagrabber.models.enums.MediaItemType;
import awais.instagrabber.utils.Constants;
import awais.instagrabber.utils.DownloadUtils;
import awais.instagrabber.utils.IntentUtils;
import awais.instagrabber.utils.TextUtils;

public final class DirectDownload extends Activity {
    private boolean isFound = false;
    private Intent intent;
    private Context context;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
    }

    @Override
    public void onWindowAttributesChanged(final WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
        if (!isFound) {
            intent = getIntent();
            context = getApplicationContext();
            if (intent != null && context != null) {
                isFound = true;
                checkIntent();
            }
        }
    }

    @Override
    public Resources getResources() {
        if (!isFound) {
            intent = getIntent();
            context = getApplicationContext();
            if (intent != null && context != null) {
                isFound = true;
                checkIntent();
            }
        }
        return super.getResources();
    }

    private synchronized void checkIntent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            doDownload();
        else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.direct_download_perms_ask, Toast.LENGTH_LONG).show();
                    handler.removeCallbacks(this);
                }
            });
            ActivityCompat.requestPermissions(this, DownloadUtils.PERMS, 8020);
        }
        finish();
    }

    private synchronized void doDownload() {
        final String action = intent.getAction();
        if (TextUtils.isEmpty(action) || Intent.ACTION_MAIN.equals(action)) return;
        boolean error = true;

        String data = null;
        final Bundle extras = intent.getExtras();
        if (extras != null) {
            final Object extraData = extras.get(Intent.EXTRA_TEXT);
            if (extraData != null) {
                error = false;
                data = extraData.toString();
            }
        }

        if (error) {
            final Uri intentData = intent.getData();
            if (intentData != null) data = intentData.toString();
        }

        if (data != null && !TextUtils.isEmpty(data)) {
            final IntentModel model = IntentUtils.parseUrl(data);
            if (model != null && model.getType() == IntentModelType.POST) {
                final String text = model.getText();

                new PostFetcher(text, new FetchListener<FeedModel>() {
                    @Override
                    public void doBefore() {
                        final Notification fetchingPostNotif = new NotificationCompat.Builder(context, Constants.DOWNLOAD_CHANNEL_ID)
                                .setCategory(NotificationCompat.CATEGORY_STATUS).setSmallIcon(R.mipmap.ic_launcher)
                                .setAutoCancel(false).setPriority(NotificationCompat.PRIORITY_MIN)
                                .setContentText(context.getString(R.string.direct_download_loading)).build();
                        notificationManager.notify(1900000000, fetchingPostNotif);
                    }

                    @Override
                    public void onResult(final FeedModel result) {
                        if (notificationManager != null) notificationManager.cancel(1900000000);
                        if (result != null) {
                            if (result.getItemType() != MediaItemType.MEDIA_TYPE_SLIDER) {
                                DownloadUtils.batchDownload(context,
                                                            result.getProfileModel().getUsername(),
                                                            DownloadMethod.DOWNLOAD_DIRECT,
                                                            Collections.singletonList(result));
                            } else {
                                context.startActivity(new Intent(context, MultiDirectDialog.class)
                                                              .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                                              .putExtra(Constants.EXTRAS_POST, result));
                            }
                        }
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }
}