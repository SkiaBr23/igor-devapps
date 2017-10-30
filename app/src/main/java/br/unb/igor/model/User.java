package br.unb.igor.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class User implements Parcelable {

    public static final String PARCEL_KEY_USER = "user";
    public static final String PARCEL_KEY_EMAIL = "email";
    public static final String DEFAULT_PROFILE_PHOTO_URL = "http://www.alass.org/wp-content/uploads/default.png";

    private String userId;
    private String fullName;
    private String name;
    private String email;
    private String password;
    private String profilePictureUrl;
    private String phoneNumber;
    private String gender;
    private String birthDate;
    private List<Convite> convites = new ArrayList<>();
    private List<String> aventuras = new ArrayList<>();
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Exclude
    public boolean hasBeenFetchedFromDB = false;

    public User() {
    }

    public User(String userId, String fullName, String email, String profilePictureUrl) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public User(FirebaseUser u) {
        this.userId = u.getUid();
        this.fullName = u.getDisplayName();
        this.email = u.getEmail();
        this.profilePictureUrl = u.getPhotoUrl().toString();

        if (this.profilePictureUrl == null || this.profilePictureUrl.isEmpty()) {
            this.profilePictureUrl = DEFAULT_PROFILE_PHOTO_URL;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<Convite> getConvites() {
        return convites;
    }

    public void setConvites(List<Convite> convites) {
        this.convites.clear();
        this.convites.addAll(convites);
    }

    public List<String> getAventuras() {
        return aventuras;
    }

    public void setAventuras(List<String> aventuras) {
        this.aventuras.clear();
        this.aventuras.addAll(aventuras);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(fullName);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(profilePictureUrl);
        parcel.writeString(phoneNumber);
        parcel.writeString(gender);
        parcel.writeString(birthDate);
        parcel.writeList(convites);
        parcel.writeList(aventuras);
        parcel.writeMap(additionalProperties);
    }

    public final static Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel parcel) {
            User u = new User();
            u.userId = parcel.readString();
            u.fullName = parcel.readString();
            u.name = parcel.readString();
            u.email = parcel.readString();
            u.password = parcel.readString();
            u.profilePictureUrl = parcel.readString();
            u.phoneNumber = parcel.readString();
            u.gender = parcel.readString();
            u.birthDate = parcel.readString();
            parcel.readList(u.convites, Convite.class.getClassLoader());
            parcel.readList(u.aventuras, Aventura.class.getClassLoader());
            parcel.readMap(u.additionalProperties, Object.class.getClassLoader());
            return u;
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void addAdventure(Aventura aventura) {
        String aid = aventura.getKey();
        if (!aventuras.contains(aid)) {
            aventuras.add(aid);
        }
    }

    @Exclude
    public void setInvitation(Convite convite, boolean isInvited) {
        String aid = convite.getKeyAventura();
        if (isInvited) {
            for (Convite c : convites) {
                if (c.getKeyAventura().equals(aid)) {
                    return;
                }
            }
            convites.add(convite);
        } else {
            for (int i = convites.size() - 1; i >= 0; i--) {
                if (convites.get(i).getKeyAventura().equals(aid)) {
                    convites.remove(i);
                    return;
                }
            }
        }
    }

    public void assignInternal(User other) {
        this.userId = other.getUserId();
        this.fullName = other.fullName;
        this.name = other.name;
        this.email = other.email;
        this.password = other.password;
        this.profilePictureUrl = other.profilePictureUrl;
        this.phoneNumber = other.phoneNumber;
        this.gender = other.gender;
        this.birthDate = other.birthDate;
        this.convites.clear();
        this.convites.addAll(other.convites);
        this.aventuras.clear();
        this.aventuras.addAll(other.aventuras);
        this.additionalProperties.clear();
        this.additionalProperties.putAll(other.additionalProperties);
    }

    public void removeAdventures(List<String> adventureIDs) {
        this.aventuras.removeAll(adventureIDs);
        Iterator<Convite> it = this.convites.iterator();
        while (it.hasNext()) {
            Convite c = it.next();
            if (adventureIDs.contains(c.getKeyAventura())) {
                it.remove();
            }
        }
     }

    @Override
    public String toString() {
        return this.fullName + "[" + this.userId + "] " + this.email;
    }
}
