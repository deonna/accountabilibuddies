package com.accountabilibuddies.accountabilibuddies.network;

import android.graphics.Bitmap;
import android.util.Log;

import com.accountabilibuddies.accountabilibuddies.model.Category;
import com.accountabilibuddies.accountabilibuddies.model.Challenge;
import com.accountabilibuddies.accountabilibuddies.model.Comment;
import com.accountabilibuddies.accountabilibuddies.model.Friend;
import com.accountabilibuddies.accountabilibuddies.model.Post;
import com.accountabilibuddies.accountabilibuddies.util.CameraUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseUser.getCurrentUser;

public class APIClient {

    //Maintain an instance to reuse for all API calls
    private static APIClient client;

    private APIClient() {}

    public static APIClient getClient() {
        if (client == null) {
            client = new APIClient();
        }
        return client;
    }

    /**
     * Listener interface to send back data to fragments
     */
    public interface ChallengeListener {
        void onSuccess();
        void onFailure(String error_message);
    }

    public interface GetChallengeListListener {
        void onSuccess(List<Challenge> challengeList);
        void onFailure(String error_message);
    }

    public interface PostListener {
        void onSuccess();
        void onFailure(String error_message);
    }

    public interface GetPostListListener {
        void onSuccess(List<Post> postList);
        void onFailure(String error_message);
    }

    public interface UploadFileListener {
        void onSuccess(String fileLocation);
        void onFailure(String error_message);
    }

    public interface AddCategoryListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface GetCategoriesListener {
        void onSuccess(List<Category> categories);
        void onFailure(String errorMessage);
    }

    public interface CreateFriendListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface GetFriendsListener {
        void onSuccess(List<Friend> friends);
        void onFailure(String errorMessage);
    }

    public interface GetCommentsListListener {
        void onSuccess(List<Comment> commentsList);
        void onFailure(String error_message);
    }

    // Challenge API's
    public void createChallenge(Challenge challenge, ChallengeListener listener) {
        challenge.saveInBackground(e -> {
            if (e != null) {
                listener.onFailure(e.getMessage());
            } else {
                listener.onSuccess();
            }
        });
    }

    public void getChallengeList(ParseUser user, GetChallengeListListener listener) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereEqualTo("userList", user);
        query.findInBackground((objects, e) -> {
            if (e != null) {
                listener.onFailure(e.getMessage());
            } else {
                listener.onSuccess(objects);
            }
        });
    }

    public void getChallengesByCategory(ArrayList<Integer> categories, GetChallengeListListener listener) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);

        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.whereNotEqualTo("userList", getCurrentUser());
        query.whereContainedIn("category", categories);
        query.findInBackground((objects, e) -> {
            if (e != null) {
                listener.onFailure(e.getMessage());
            } else {
                listener.onSuccess(objects);
            }
        });
    }

    public void joinChallenge(String challengeObjectId, ChallengeListener listener) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.getInBackground(challengeObjectId, (object, e) -> {
            if (e == null) {
                object.add("userList", getCurrentUser());
                object.saveInBackground(e1 -> {
                    if (e1 != null) {
                        listener.onFailure(e1.getMessage());
                    } else {
                        listener.onSuccess();
                    }
                });
            } else {
                listener.onFailure(e.getMessage());
            }
        });
    }

    public void deleteChallenge() {

    }

    public void updateChallenge() {

    }
    
    private void filterCurrentUser(List<ParseUser> users) {

        CollectionUtils.filter(
            users,
            (ParseUser user) -> {
                String currentUserId = ParseUser.getCurrentUser().getObjectId();
                return !user.getObjectId().equals(currentUserId);
            }
        );
    }

//    public void testParseUserRemoval() {
//
//        exitChallenge("eqEWFtXMWv", new ChallengeListener() {
//            @Override
//            public void onSuccess() {
//                Log.d("API", "success");
//            }
//
//            @Override
//            public void onFailure(String error_message) {
//                Log.d("API", "failure");
//            }
//        });
//    }

    private void filterCurrentUser(List<ParseUser> users) {

        CollectionUtils.filter(
            users,
            (ParseUser user) -> {
                String currentUserId = ParseUser.getCurrentUser().getObjectId();
                return !user.getObjectId().equals(currentUserId);
            }
        );
    }

    public void exitChallenge(String challengeObjectId, ChallengeListener listener) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.getInBackground(challengeObjectId, (challenge, e) -> {
            if (e == null) {
                List<ParseUser> users = challenge.getUserList();
                filterCurrentUser(users);

                challenge.add("userList",users);
                challenge.saveInBackground(e1 -> {
                    if (e1 != null) {
                        listener.onFailure(e1.getMessage());
                    } else {
                        listener.onSuccess();
                    }
                });
            } else {
                listener.onFailure(e.getMessage());
            }
        });
    }

    public void addCategoryForUser(ParseUser user, Category category, AddCategoryListener
            listener) {

        List<Category> categories = (List<Category>) user.get(Category.PLURAL);

        if (categories == null) {
            categories = new ArrayList<>();
        }

        categories.add(category);

        user.put(Category.PLURAL, categories);

        user.saveInBackground(
            (ParseException e) -> {
                if (e != null) {
                    listener.onFailure(e.getMessage());
                } else {
                    listener.onSuccess();
                }
            }
        );
    }

    public void getHardcodedCategories(GetCategoriesListener listener) {

        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK); // or CACHE_ONLY

        query.findInBackground(
            (List<Category> categories, ParseException e) -> {
                if (e != null) {
                    listener.onFailure(e.getMessage());
                } else {
                    listener.onSuccess(categories);
                }
            }
        );
    }

    public void createFriend(Friend friend, CreateFriendListener listener) {

        friend.saveInBackground(

            (ParseException e) -> {

                if (e != null) {
                    listener.onFailure(e.getMessage());
                } else {
                    listener.onSuccess();
                }
            }
        );

    }

    public void getFriendsByUserId(String friendOfId, GetFriendsListener listener) {

        ParseQuery<Friend> query = ParseQuery.getQuery(Friend.class);
        query.whereEqualTo("friendOfId", friendOfId);

        query.findInBackground(

            (List<Friend> friends, ParseException e) -> {

                if (e != null) {
                    listener.onFailure(e.getMessage());
                } else {
                    listener.onSuccess(friends);
                }
            }
        );
    }

    //Post API's
    public void createPost(Post post, String challengeObjectId, PostListener listener) {
        post.saveInBackground(e -> {
            if (e != null) {
                listener.onFailure(e.getMessage());
            } else {
                //Add this post to the Challenge now
                ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
                query.getInBackground(challengeObjectId, (object, e1) -> {

                    if (e1 == null) {
                        object.add("postList", post);

                        //TODO: Add progress here and move callback to common place
                        object.saveInBackground(e11 -> {
                            if (e11 != null) {
                                listener.onFailure(e11.getMessage());
                            } else {
                                listener.onSuccess();
                            }
                        });
                    } else {
                        listener.onFailure(e1.getMessage());
                    }
                });
            }
        });
    }

    public void getPostList(String challengeObjectId, GetPostListListener listener) {
        ParseQuery<Challenge> query = ParseQuery.getQuery(Challenge.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.include("postList");

        query.getInBackground(challengeObjectId, (object, e) -> {
            if (e == null) {
                listener.onSuccess(object.getPostList());
            } else {
                listener.onFailure(e.getMessage());
            }
        });
    }

    public void getCommentList(String postObjectId, GetCommentsListListener listener) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.include("commentList");

        query.getInBackground(postObjectId, (object, e) -> {
            if (e == null) {
                listener.onSuccess(object.getCommentList());
            } else {
                listener.onFailure(e.getMessage());
            }
        });
    }

    public void addComment(String postId, Comment comment, PostListener listener) {
        comment.saveInBackground(e -> {
            if (e != null) {
                listener.onFailure(e.getMessage());
            } else {
                //Add this post to the Challenge now
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.getInBackground(postId, (object, e1) -> {

                    if (e1 == null) {
                        object.add("commentList", comment);

                        object.saveInBackground(e11 -> {
                            if (e11 != null) {
                                listener.onFailure(e11.getMessage());
                            } else {
                                listener.onSuccess();
                            }
                        });
                    } else {
                        listener.onFailure(e1.getMessage());
                    }
                });
            }
        });
    }

    //Like/Unlike Post
    public void likeUnlikePost(String postId, boolean like, PostListener listener) {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.getInBackground(postId, (post, error) -> {
            if (error == null) {
                List<ParseUser> users = post.getLikeList();

                if (like) {
                    users.add(getCurrentUser());
                } else {
                    filterCurrentUser(users);
                }

                post.add("userList",users);
                post.saveInBackground(e11 -> {
                    if (e11 != null) {
                        listener.onFailure(e11.getMessage());
                    } else {
                        listener.onSuccess();
                    }
                });
            } else {
                listener.onFailure(error.getMessage());
            }
        });

    }

    //Upload file
    public void uploadFile(String fileName, Bitmap bitmap, UploadFileListener listener) {
        byte[] bytes = CameraUtils.bitmapToByteArray(bitmap);
        final ParseFile photoFile = new ParseFile(fileName, bytes);
        photoFile.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    listener.onFailure(e.getMessage());
                } else {
                    Log.d("Test", photoFile.getUrl());
                    listener.onSuccess(photoFile.getUrl());
                }
            }
        });
    }
}

