package awais.instagrabber.repositories.responses.directmessages;

import awais.instagrabber.repositories.responses.Media;

public class DirectItemStoryShare {
    private final String reelId;
    private final String reelType;
    private final String text;
    private final boolean isReelPersisted;
    private final Media media;
    private final String title;
    private final String message;

    public DirectItemStoryShare(final String reelId,
                                final String reelType,
                                final String text,
                                final boolean isReelPersisted,
                                final Media media,
                                final String title,
                                final String message) {
        this.reelId = reelId;
        this.reelType = reelType;
        this.text = text;
        this.isReelPersisted = isReelPersisted;
        this.media = media;
        this.title = title;
        this.message = message;
    }

    public String getReelId() {
        return reelId;
    }

    public String getReelType() {
        return reelType;
    }

    public String getText() {
        return text;
    }

    public boolean isReelPersisted() {
        return isReelPersisted;
    }

    public Media getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
