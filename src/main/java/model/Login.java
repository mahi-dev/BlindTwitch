package model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Login {
 @NonNull
 private String provider;
 @NonNull
 private String clientId;
 @NonNull
 private String clientSecret;
 @NonNull
 private String clientToken;
 @NonNull
 private String channel;
}