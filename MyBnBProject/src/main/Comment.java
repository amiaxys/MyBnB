package main;

import java.time.LocalDateTime;

public class Comment {
    protected int cid;
    protected String sin;
    protected int rating;
    protected String text;
    protected LocalDateTime date;
    protected User commentedByUser;
    protected User commentedOnUser;
    protected Listing commentedOnListing;

    public Comment() {
        cid = -1;
        sin = null;
        rating = 0;
        text = null;
        date = null;
        commentedByUser = null;
        commentedOnUser = null;
        commentedOnListing = null;
    }
}
