package com.accountabilibuddies.accountabilibuddies.viewmodel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.accountabilibuddies.accountabilibuddies.model.Category;
import com.accountabilibuddies.accountabilibuddies.model.Friend;
import com.accountabilibuddies.accountabilibuddies.network.APIClient;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginViewModel {

    public static final String TAG = LoginViewModel.class.getSimpleName();

    private AppCompatActivity context;

    public LoginViewModel(AppCompatActivity context) {

        this.context = context;
    }

    public void refreshTokenAndGetFriends() {

        AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
            @Override
            public void OnTokenRefreshed(AccessToken accessToken) {
                getFriendsForCurrentUser();
            }

            @Override
            public void OnTokenRefreshFailed(FacebookException exception) {

            }
        });
    }

    private void setUpNewUser(ParseUser user) {

        List<Category> categories = new ArrayList<>();
        user.put(Category.PLURAL, categories);
        user.saveInBackground();
    }

    private void saveFriend(String facebookId, String name, String photoUrl) {

        Friend friend = new Friend();

        friend.setFacebookId(facebookId);
        friend.setName(name);
        friend.setPhotoUrl(photoUrl);
        friend.setFriendOfId(ParseUser.getCurrentUser().getObjectId());

        APIClient.getClient().createFriend(friend, new APIClient.CreateFriendListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "Friend creation success!");
            }

            @Override
            public void onFailure(String errorMessage) {

                Log.d(TAG, "Friend creation failure!");
            }
        });
    }

    public void createFriendsList() {

        GraphRequest friendRequest = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),

                (objects,  response) -> {
                    Log.d(TAG + "Friends List", response.toString());
                    JSONObject resultsJson = response.getJSONObject();

                    try {
                        JSONArray resultsArray = resultsJson.getJSONArray("data");

                        for (int i = 0; i < resultsArray.length(); i++) {

                            JSONObject user = resultsArray.getJSONObject(i);
                            JSONObject picture = user.getJSONObject("picture");
                            JSONObject pictureData = picture.getJSONObject("data");

                            saveFriend(
                                user.getString("id"),
                                user.getString("name"),
                                pictureData.getString("url")
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        );

        Bundle params = new Bundle();
        params.putString("fields", "id, name, picture");
        friendRequest.setParameters(params);
        friendRequest.executeAsync();
    }

    public void logIn(View view) {

        logInWithReadPermissions();
    }

    public void logInWithReadPermissions() {

        ParseFacebookUtils.logInWithReadPermissionsInBackground(
            context,
            Arrays.asList("public_profile", "user_friends"),

            (ParseUser user, ParseException err) -> {
                if (user == null) {
                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(TAG, "User signed up and logged in through Facebook!");
                    setUpNewUser(user);
                } else {
                    Log.d(TAG, "User logged in through Facebook!");
                }
            }
        );
    }

    private void getFriendsForCurrentUser() {

        APIClient.getClient().getFriendsByUserId(
            ParseUser.getCurrentUser().getObjectId(),
            new APIClient.GetFriendsListener() {

                @Override
                public void onSuccess(List<Friend> friends) {

                    Log.d(TAG, "Here are my friends: " + friends.toString());
                }

                @Override
                public void onFailure(String errorMessage) {

                    Log.d(TAG, "Error getting friends list.");
                }
            });
    }
}
