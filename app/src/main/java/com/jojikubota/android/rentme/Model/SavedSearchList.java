package com.jojikubota.android.rentme.Model;

/**
 * Created by joji on 3/18/16.
 */

// Singleton
public class SavedSearchList {
    // Create static variable
    /*private static SavedSearchList sSavedSearchList;

    private List<SavedSearch> mSavedSearch;

    // Public constructor
    public static SavedSearchList get(Context context) {
        if (sSavedSearchList == null) {
            sSavedSearchList = new SavedSearchList(context);
        }

        return sSavedSearchList;
    }

    // Constructor
    private SavedSearchList(Context context) {
        mSavedSearch = new ArrayList<>();
    }

    // Getter
    public List<SavedSearch> getSavedSearch() {
        return mSavedSearch;
    }

    // Setter
    public void addSavedSearch(SavedSearch savedSearch) {
        mSavedSearch.add(savedSearch);
    }

    // Find Posting
    public Posting getPosting(String mKeyword) {
        for (SavedSearch posting : mSavedSearch) {
            if (posting.getPostId().equals(postId)) {
                return posting;
            }
        }
        return null;
    }

    // Delete Posting
    public void deletePosting(String postId) {
        int index = -1;
        for (SavedSearch posting : mPostings) {
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
    }*/
}
