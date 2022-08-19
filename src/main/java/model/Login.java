package model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Login {
 @NonNull
 private String username;
 @NonNull
 private String password;
}