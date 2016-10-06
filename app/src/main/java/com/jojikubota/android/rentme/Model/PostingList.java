package com.jojikubota.android.rentme.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joji on 3/18/16.
 */

// Singleton
public class PostingList {
    // Create static variable
    private static PostingList sPostingList;

    private List<Posting> mPostings;

    // Public constructor
    public static PostingList get(Context context) {
        if (sPostingList == null) {
            sPostingList = new PostingList(context);
        }

        return sPostingList;
    }

    // Constructor
    private PostingList(Context context) {
        mPostings = new ArrayList<>();
    }

    // Getter
    public List<Posting> getPostings() {
        return mPostings;
    }

    // Setter
    public void addPostings(Posting posting) {
        mPostings.add(posting);
    }

    // Find Posting
    public Posting getPosting(String postId) {
        for (Posting posting : mPostings) {
            if (posting.getPostId().equals(postId)) {
                return posting;
            }
        }
        return null;
    }

    // Delete Posting
    public void deletePosting(String postId) {
        int index = -1;
        for (Posting posting : mPostings) {
            if (posting.getPostId().equals(postId)) {
                index = mPostings.indexOf(posting);
            }
        }
        if (index >= 0) {
            mPostings.remove(index);
        }
    }

    // Clear Posting List
    public void clearPostings() {
        mPostings.clear();
    }
}
